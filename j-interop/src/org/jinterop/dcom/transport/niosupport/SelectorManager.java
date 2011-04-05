/*
 * Copyright 2004 WIT-Software, Lda. 
 * - web: http://www.wit-software.com 
 * - email: info@wit-software.com
 *
 * All rights reserved. Relased under terms of the 
 * Creative Commons' Attribution-NonCommercial-ShareAlike license.
 */
package org.jinterop.dcom.transport.niosupport;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jinterop.dcom.common.JISystem;

/**
 * Confines selector operations to a single thread. Calls back to registered
 * {@link ChannelListener}s on this thread when read operations are ready.
 */
public final class SelectorManager implements Runnable
{
    private final Selector selector;

    private final Thread selectThread;

    private final List<Runnable> taskList = new ArrayList<Runnable>();

    /**
     * Constructor for SelectorManager.
     * 
     * @throws IOException
     */
    public SelectorManager() throws IOException
    {
        selectThread = new Thread(this, "jI_SelectorManager");
        selectThread.setDaemon(true);

        selector = Selector.open();
        selectThread.start();
    }

    /**
     * Shuts down the selector manager
     */
    public void destroy()
    {
        if (selectThread.isAlive())
        {
            selectThread.interrupt();
        }
    }

    void registerChannel(final SelectableChannel selectableChannel,
            final ChannelListener listener) throws IOException
    {
        final Callable<Void> task = new Callable<Void>()
        {
            public Void call() throws IOException
            {
                selectableChannel.configureBlocking(false);
                selectableChannel.register(selector, 0, listener);

                return null;
            }
        };

        invokeSync(task);
    }

    void setReadInterest(final SelectableChannel selectableChannel)
            throws IOException
    {
        final Callable<Void> task = new Callable<Void>()
        {
            public Void call() throws IOException
            {
                setInterestOps(selectableChannel, SelectionKey.OP_READ);

                return null;
            }
        };

        invokeSync(task);
    }

    void removeReadInterest(final SelectableChannel selectableChannel)
            throws IOException
    {
        final Callable<Void> task = new Callable<Void>()
        {
            public Void call() throws Exception
            {
                setInterestOps(selectableChannel, 0);

                return null;
            }
        };

        invokeSync(task);
    }

    private void setInterestOps(SelectableChannel selectableChannel,
            int interestOps) throws IOException
    {
        try
        {
            if (selectableChannel.isRegistered())
            {
                final SelectionKey selectionKey = selectableChannel
                        .keyFor(selector);

                selectionKey.interestOps(interestOps);
            }
        }
        catch (final CancelledKeyException e)
        {
            throw new IOException("Unable to set interest ops", e);
        }
    }

    private void invokeAsync(final Runnable task)
    {
        synchronized (taskList)
        {
            taskList.add(task);
        }

        // To break out of the select and execute the tasks...
        selector.wakeup();
    }

    private void invokeSync(final Callable<Void> task) throws IOException
    {
        final ExceptionHolder exceptionHolder = new ExceptionHolder();

        if (Thread.currentThread() == selectThread)
        {
            try
            {
                task.call();
            }
            catch (final Exception e)
            {
                // Store the exception so we can check it's one of the ones
                // declared as thrown
                exceptionHolder.setException(e);
            }
        }
        else
        {
            // Used to deliver the notification that the task is executed
            final CountDownLatch latch = new CountDownLatch(1);

            invokeAsync(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        task.call();
                    }
                    catch (final Exception e)
                    {
                        exceptionHolder.setException(e);
                    }
                    finally
                    {
                        latch.countDown();
                    }
                }
            });

            try
            {
                // Wait for completion
                latch.await();
            }
            catch (final InterruptedException e)
            {
                // Set the interrupted flag
                Thread.currentThread().interrupt();
            }
        }

        // Throw any exception thrown by the task
        if (exceptionHolder.getException() != null)
        {
            final Exception thrownException = exceptionHolder.getException();

            throw launderIOException(thrownException);
        }
    }

    private IOException launderIOException(Exception thrownException)
    {
        if (thrownException instanceof RuntimeException)
        {
            throw (RuntimeException) thrownException;
        }

        if (thrownException instanceof IOException)
        {
            return (IOException) thrownException;
        }

        throw new UndeclaredThrowableException(thrownException);
    }

    private void doInvocations()
    {
        boolean processedTask = false;

        synchronized (taskList)
        {
            for (Runnable task : taskList)
            {
                task.run();
                processedTask = true;
            }
            taskList.clear();
        }

        // Just in case we are called with nothing to do so that we dont
        // get busy cpu.
        if (!processedTask)
        {
            try
            {
                Thread.sleep(0, 1);
            }
            catch (final InterruptedException e)
            {
                // Set the interrupted flag
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        try
        {
            while (true)
            {
                if (Thread.currentThread().isInterrupted())
                {
                    JISystem.getLogger().log(Level.INFO,
                            "Selector manager interrupted");
                    return;
                }

                doInvocations();

                doSelect();
            }
        }
        catch (Exception t)
        {
            cleanup();
            getLogger().log(Level.SEVERE,
                    "Selector manager is unexpectedly exiting", t);
        }
    }

    private void doSelect()
    {
        try
        {
            if (selector.select() != 0)
            {
                final Iterator<SelectionKey> it = selector.selectedKeys()
                        .iterator();

                while (it.hasNext())
                {
                    try
                    {
                        final SelectionKey selectionKey = it.next();
                        it.remove();

                        // Client must re-obtain read interest once it has
                        // handled the read and is ready for the next read
                        selectionKey.interestOps(0);

                        // Call back to the listener for it to do the read
                        final ChannelListener listener = (ChannelListener) selectionKey
                                .attachment();

                        listener.readReady();
                    }
                    catch (CancelledKeyException e)
                    {
                        if (getLogger().isLoggable(Level.FINE))
                        {
                            getLogger().log(Level.FINE,
                                    "Ignoring cancelled key exception", e);
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            getLogger().log(Level.WARNING,
                    "Exception during SelectionManager select", e);
        }
    }

    private void cleanup()
    {
        for (SelectionKey key : selector.keys())
        {
            try
            {
                key.channel().close();
            }
            catch (IOException e)
            {
                if (getLogger().isLoggable(Level.FINE))
                {
                    getLogger().log(Level.FINE,
                            "Ignoring channel close exception", e);
                }
            }
        }

        try
        {
            selector.close();
        }
        catch (IOException e)
        {
            if (getLogger().isLoggable(Level.FINE))
            {
                getLogger().log(Level.FINE,
                        "Ignoring selector close exception", e);
            }
        }
    }

    private Logger getLogger()
    {
        return JISystem.getLogger();
    }

    private static class ExceptionHolder
    {
        private Exception exception;

        Exception getException()
        {
            return exception;
        }

        void setException(Exception e)
        {
            this.exception = e;
        }
    }
}
