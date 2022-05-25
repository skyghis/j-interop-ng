/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jinterop.dcom.transport.utils;

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

/**
 * Confines selector operations to a single thread.
 * Calls back to registered {@link ChannelListener}s on this thread when read operations are ready.
 */
public final class SelectorManager {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private final List<Runnable> taskList = new ArrayList<>();
    private final Selector selector;
    private final Thread selectThread;

    public SelectorManager() throws IOException {
        this.selector = Selector.open();
        this.selectThread = new Thread(() -> {
            try {
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        LOGGER.log(Level.INFO, "Selector manager interrupted");
                        return;
                    }
                    doInvocations();
                    doSelect();
                }
            } catch (Exception ex) {
                cleanup();
                LOGGER.log(Level.SEVERE, "Selector manager is unexpectedly exiting", ex);
            }
        }, "jI_SelectorManager");
        this.selectThread.setDaemon(true);
        this.selectThread.start();
    }

    /**
     * Shuts down the selector manager
     */
    public void destroy() {
        if (selectThread.isAlive()) {
            selectThread.interrupt();
        }
    }

    void registerChannel(SelectableChannel selectableChannel, Runnable listener) throws IOException {
        invokeSync(() -> {
            selectableChannel.configureBlocking(false);
            selectableChannel.register(selector, 0, listener);
            return null;
        });
    }

    void setReadInterest(SelectableChannel selectableChannel) throws IOException {

        invokeSync(() -> {
            setInterestOps(selectableChannel, SelectionKey.OP_READ);
            return null;
        });
    }

    void removeReadInterest(SelectableChannel selectableChannel) throws IOException {
        invokeSync(() -> {
            setInterestOps(selectableChannel, 0);
            return null;
        });
    }

    private void setInterestOps(SelectableChannel selectableChannel, int interestOps) throws IOException {
        try {
            if (selectableChannel.isRegistered()) {
                final SelectionKey selectionKey = selectableChannel.keyFor(selector);
                selectionKey.interestOps(interestOps);
            }
        } catch (final CancelledKeyException ex) {
            throw new IOException("Unable to set interest ops", ex);
        }
    }

    private void invokeAsync(Runnable task) {
        synchronized (taskList) {
            taskList.add(task);
        }
        // To break out of the select and execute the tasks...
        selector.wakeup();
    }

    private void invokeSync(Callable<Void> task) throws IOException {
        final ExceptionHolder exceptionHolder = new ExceptionHolder();
        if (Thread.currentThread() == selectThread) {
            try {
                task.call();
            } catch (final Exception ex) {
                // Store the exception so we can check it's one of the ones declared as thrown
                exceptionHolder.setException(ex);
            }
        } else {
            // Used to deliver the notification that the task is executed
            final CountDownLatch latch = new CountDownLatch(1);
            invokeAsync(() -> {
                try {
                    task.call();
                } catch (Exception ex) {
                    exceptionHolder.setException(ex);
                } finally {
                    latch.countDown();
                }
            });

            try {
                latch.await(); // Wait for completion
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt(); // Set the interrupted flag
            }
        }

        // Throw any exception thrown by the task
        if (exceptionHolder.getException() != null) {
            final Exception thrownException = exceptionHolder.getException();
            throw launderIOException(thrownException);
        }
    }

    private IOException launderIOException(Exception thrownException) {
        if (thrownException instanceof RuntimeException) {
            throw (RuntimeException) thrownException;
        }
        if (thrownException instanceof IOException) {
            return (IOException) thrownException;
        }
        throw new UndeclaredThrowableException(thrownException);
    }

    private void doInvocations() {
        boolean processedTask = false;

        synchronized (taskList) {
            for (Runnable task : taskList) {
                task.run();
                processedTask = true;
            }
            taskList.clear();
        }

        // Just in case we are called with nothing to do so that we dont get busy cpu.
        if (!processedTask) {
            try {
                Thread.sleep(0, 1);
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt(); // Set the interrupted flag
            }
        }
    }

    private void doSelect() {
        try {
            if (selector.select() != 0) {
                final Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    try {
                        final SelectionKey selectionKey = it.next();
                        it.remove();
                        // Client must re-obtain read interest once it has handled the read and is ready for the next read
                        selectionKey.interestOps(0);
                        // Call back to the listener for it to do the read
                        final Runnable listener = (Runnable) selectionKey.attachment();
                        listener.run();
                    } catch (CancelledKeyException ex) {
                        LOGGER.log(Level.FINE, "Ignoring cancelled key exception", ex);
                    }
                }
            }
        } catch (IOException | RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Exception during SelectionManager select", ex);
        }
    }

    private void cleanup() {
        for (SelectionKey key : selector.keys()) {
            try {
                key.channel().close();
            } catch (IOException | RuntimeException ex) {
                LOGGER.log(Level.FINE, "Ignoring channel close exception", ex);
            }
        }

        try {
            selector.close();
        } catch (IOException | RuntimeException ex) {
            LOGGER.log(Level.FINE, "Ignoring selector close exception", ex);
        }
    }

    private static final class ExceptionHolder {

        private Exception exception;

        Exception getException() {
            return exception;
        }

        void setException(Exception ex) {
            this.exception = ex;
        }
    }
}
