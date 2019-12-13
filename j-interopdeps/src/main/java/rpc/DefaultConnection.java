/* Donated by Jarapac (http://jarapac.sourceforge.net/)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */
package rpc;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.util.Hexdump;
import ndr.NdrBuffer;
import ndr.NetworkDataRepresentation;
import rpc.core.AuthenticationVerifier;
import rpc.pdu.AlterContextPdu;
import rpc.pdu.AlterContextResponsePdu;
import rpc.pdu.Auth3Pdu;
import rpc.pdu.BindAcknowledgePdu;
import rpc.pdu.BindNoAcknowledgePdu;
import rpc.pdu.BindPdu;
import rpc.pdu.CancelCoPdu;
import rpc.pdu.FaultCoPdu;
import rpc.pdu.OrphanedPdu;
import rpc.pdu.RequestCoPdu;
import rpc.pdu.ResponseCoPdu;
import rpc.pdu.ShutdownPdu;

public class DefaultConnection implements Connection {

    protected NetworkDataRepresentation ndr;

    protected NdrBuffer transmitBuffer;

    protected NdrBuffer receiveBuffer;

    protected Security security;

    protected int contextId;

    private static final Logger logger = Logger.getLogger("org.jinterop");

    public DefaultConnection() {
        this(ConnectionOrientedPdu.MUST_RECEIVE_FRAGMENT_SIZE,
                ConnectionOrientedPdu.MUST_RECEIVE_FRAGMENT_SIZE);
    }

    public DefaultConnection(int transmitLength, int receiveLength) {
        ndr = new NetworkDataRepresentation();
        transmitBuffer = new NdrBuffer(new byte[transmitLength], 0);
        receiveBuffer = new NdrBuffer(new byte[receiveLength], 0);
    }

    @Override
    public void transmit(ConnectionOrientedPdu pdu, Transport transport)
            throws IOException {
        if (!(pdu instanceof Fragmentable)) {
            transmitFragment(pdu, transport);
            return;
        }
        Iterator fragments
                = ((Fragmentable) pdu).fragment(transmitBuffer.getCapacity());
        while (fragments.hasNext()) {
            transmitFragment((ConnectionOrientedPdu) fragments.next(),
                    transport);
        }
    }

    @Override
    public ConnectionOrientedPdu receive(final Transport transport)
            throws IOException {
        final ConnectionOrientedPdu fragment = receiveFragment(transport);
        if (!(fragment instanceof Fragmentable)
                || fragment.getFlag(ConnectionOrientedPdu.PFC_LAST_FRAG)) {
            return fragment;
        }
        return (ConnectionOrientedPdu) ((Fragmentable) fragment).assemble(new Iterator() {
            ConnectionOrientedPdu currentFragment = fragment;

            @Override
            public boolean hasNext() {
                return (currentFragment != null);
            }
            private int i = 0;

            @Override
            public Object next() {
                if (currentFragment == null) {
                    throw new NoSuchElementException();
                }
                try {
                    return currentFragment;
                } finally {
                    if (currentFragment.getFlag(
                            ConnectionOrientedPdu.PFC_LAST_FRAG)) {
                        currentFragment = null;
                    } else {
                        try {
                            //fragLengthOfReceiveBuffer = -1;//clear the buffer here.
                            //System.out.println("VIKRAM VIKRAM ");
                            if (logger.isLoggable(Level.FINEST)) {
                                logger.log(Level.FINEST, "[Fragmented Packet] [{0}] recieved , fragment decomposition is below:- ", i++);
                            }
                            currentFragment = receiveFragment(transport);
                        } catch (Exception ex) {
                            throw new IllegalStateException();
                        }
                    }
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        });
    }

    protected void transmitFragment(ConnectionOrientedPdu fragment,
            Transport transport) throws IOException {
        transmitBuffer.reset();

        fragment.encode(ndr, transmitBuffer);

        processOutgoing();

        //jcifs.util.Hexdump.hexdump(System.err, transmitBuffer.getBuffer(), 0, transmitBuffer.length);
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "[TRANSMIT BUFFER]:-\n{0}", Hexdump.toHexString(transmitBuffer.getBuffer()));
        }
        transport.send(transmitBuffer);
    }

    private boolean bytesRemainingInRecieveBuffer = false;

    protected ConnectionOrientedPdu receiveFragment(Transport transport)
            throws IOException {

        int fragmentLength = -1;
        int type = -1;
        boolean read = true;

        if (bytesRemainingInRecieveBuffer) {
            if (receiveBuffer.length > ConnectionOrientedPdu.TYPE_OFFSET) {
                receiveBuffer.setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
                type = receiveBuffer.dec_ndr_small();
                if (isValidType(type)) {
                    //this is required so that the correct length for the next fragment can be obtained. If is < 8 bytes than the fraglength would be an arbitary length.
                    while (receiveBuffer.length <= ConnectionOrientedPdu.FRAG_LENGTH_OFFSET) {
                        //perform a read again in a new buffer and assign that to the reciever buffer
                        //this needs to be a small buffer 10 bytes
                        NdrBuffer tmpBuffer = new NdrBuffer(new byte[10], 0);
                        transport.receive(tmpBuffer);
                        System.arraycopy(tmpBuffer.buf, 0, receiveBuffer.buf, receiveBuffer.length, tmpBuffer.length);
                        receiveBuffer.length = receiveBuffer.length + tmpBuffer.length;
                    }
                    read = false;
                } else {
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.log(Level.FINEST, "bytesRemainingInRecieveBuffer is TRUE, RecieveBuffer size =  {0}", receiveBuffer.buf.length);
                    }
                }

            }

            bytesRemainingInRecieveBuffer = false;
        }

        //will be true for all cases and false if anything valid is already in the buffer
        if (read) {
            //read the transport now...
            receiveBuffer.reset();
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "Reading bytes from RecieveBuffer Socket...Current Capacity:- {0}", receiveBuffer.getCapacity());
            }

            transport.receive(receiveBuffer);

            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("[RECIEVER BUFFER] Full packet is dumped below...");
                logger.log(Level.FINEST, "{0}", Hexdump.toHexString(receiveBuffer.getBuffer()));
                logger.log(Level.FINEST, "Bytes read from RecieveBuffer Socket:- {0}", receiveBuffer.length);
            }

        }

        byte[] newbuffer = null;
        int counter = 0;
        int trimSize = -1;
        int lengthOfArrayTobeRead = receiveBuffer.length;
        //frag length logic
        if (receiveBuffer.length > 0) {
            receiveBuffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
            fragmentLength = receiveBuffer.dec_ndr_short();
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "length of the fragment {0}\n size in bytes of the buffer [] {1}", new Object[]{fragmentLength, receiveBuffer.buf.length});
            }

            //the new buffer should be equal to fragment size
            newbuffer = new byte[fragmentLength];

            if (fragmentLength > receiveBuffer.length)//this means the socket buffer is not fully read, this packet is bigger than the reciever buffer size
            {
                int remainingBytes = fragmentLength - receiveBuffer.length;
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Some bytes from RecieveBuffer Socket have not been read: Remaining  {0}", remainingBytes);
                }

                //now reset and read again.
                while (fragmentLength > counter) {
                    System.arraycopy(receiveBuffer.buf, 0, newbuffer, counter, lengthOfArrayTobeRead);
                    counter = counter + lengthOfArrayTobeRead;
                    if (fragmentLength == counter) {
                        break;
                    }
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.log(Level.FINEST, "About to read more bytes from socket , current counter is: {0}", counter);
                    }

                    receiveBuffer.reset();
                    transport.receive(receiveBuffer); //now read again so as to take it from network buffer to your buffer
                    //this may actually read 2 or more packets , one is this partial one (now complete) and one may be some other one , like a request packet.
                    //or it may not ...and reads only the partial packet.
                    if (fragmentLength - counter >= receiveBuffer.length) {
                        lengthOfArrayTobeRead = receiveBuffer.length;
                    } else {
                        //this would be the last one. Now we need to trim the buffer to it's read length as well.
                        lengthOfArrayTobeRead = fragmentLength - counter;
                        trimSize = receiveBuffer.length - lengthOfArrayTobeRead;
                    }

                    if (logger.isLoggable(Level.FINEST)) {
                        logger.log(Level.FINEST, "lengthOfArrayTobeRead = {0}\ntrimSize = {1}\nRecieveBuffer current read size: {2}", new Object[]{lengthOfArrayTobeRead, trimSize, receiveBuffer.length});
                        logger.finest("[RECIEVER BUFFER] and the read packet is dumped below...");
                        logger.log(Level.FINEST, Hexdump.toHexString(receiveBuffer.getBuffer()));

                    }

                }

            } else {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("fragmentLength is less than  receiveBuffer.length");
                }

                //Since fragment length is smaller, There might be 2 or more packets in here
                //just read what is your packet.
                System.arraycopy(receiveBuffer.buf, 0, newbuffer, 0, fragmentLength);
                //there might be more. Now we need to trim the buffer to it's read length as well.
                trimSize = receiveBuffer.length - fragmentLength;
            }

            if (trimSize > 0) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "trimSize = {0}", trimSize);
                }
                System.arraycopy(receiveBuffer.buf, receiveBuffer.length - trimSize, receiveBuffer.buf, 0, trimSize);
                receiveBuffer.length = trimSize;
                receiveBuffer.index = 0;
                receiveBuffer.start = 0;
                bytesRemainingInRecieveBuffer = true; //reciever buffer read more than it should , after we trim only the additionally read bytes will be left.
                //these have to be read in the next call to recieveFragment.
            }

            NdrBuffer bufferToBeUsed = new NdrBuffer(newbuffer, 0);
            bufferToBeUsed.length = newbuffer.length;//this will be fully utilized  and not left empty.

            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "bufferToBeUsed Size = {0}", bufferToBeUsed.length);
                logger.finest("[bufferToBeUsed] packet is dumped below...");
                logger.log(Level.FINEST, "{0}", Hexdump.toHexString(bufferToBeUsed.getBuffer()));
                logger.finest("*********************************************************************************");
            }

            //caution , frag length is changed here...it is void of security info.
            processIncoming(bufferToBeUsed);
            bufferToBeUsed.setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
            type = bufferToBeUsed.dec_ndr_small();

            ConnectionOrientedPdu pdu = null;
            switch (type) {
                case AlterContextPdu.ALTER_CONTEXT_TYPE:
                    pdu = new AlterContextPdu();
                    break;
                case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
                    pdu = new AlterContextResponsePdu();
                    break;
                case Auth3Pdu.AUTH3_TYPE:
                    pdu = new Auth3Pdu();
                    break;
                case BindPdu.BIND_TYPE:
                    pdu = new BindPdu();
                    break;
                case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
                    pdu = new BindAcknowledgePdu();
                    break;
                case BindNoAcknowledgePdu.BIND_NO_ACKNOWLEDGE_TYPE:
                    pdu = new BindNoAcknowledgePdu();
                    break;
                case CancelCoPdu.CANCEL_TYPE:
                    pdu = new CancelCoPdu();
                    break;
                case FaultCoPdu.FAULT_TYPE:
                    pdu = new FaultCoPdu();
                    break;
                case OrphanedPdu.ORPHANED_TYPE:
                    pdu = new OrphanedPdu();
                    break;
                case RequestCoPdu.REQUEST_TYPE:
                    pdu = new RequestCoPdu();
                    break;
                case ResponseCoPdu.RESPONSE_TYPE:
                    pdu = new ResponseCoPdu();
                    break;
                case ShutdownPdu.SHUTDOWN_TYPE:
                    pdu = new ShutdownPdu();
                    break;
                default:
                    throw new IOException("Unknown PDU type: 0x"
                            + Integer.toHexString(type));
            }

            bufferToBeUsed.setIndex(0);
            pdu.decode(ndr, bufferToBeUsed);
            return pdu;

        } else {
            //socket has been closed.
            throw new IOException("Socket Closed"); //Vikram
        }

    }

    private boolean isValidType(int type) {
        switch (type) {
            case AlterContextPdu.ALTER_CONTEXT_TYPE:
            case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
            case Auth3Pdu.AUTH3_TYPE:
            case BindPdu.BIND_TYPE:
            case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
            case BindNoAcknowledgePdu.BIND_NO_ACKNOWLEDGE_TYPE:
            case CancelCoPdu.CANCEL_TYPE:
            case FaultCoPdu.FAULT_TYPE:
            case OrphanedPdu.ORPHANED_TYPE:
            case RequestCoPdu.REQUEST_TYPE:
            case ResponseCoPdu.RESPONSE_TYPE:
            case ShutdownPdu.SHUTDOWN_TYPE:
                return true;
            default:
                return false;
        }

    }

    protected void processIncoming(NdrBuffer buffer) throws IOException {
        buffer.setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
        boolean logMsg = true;
        switch (buffer.dec_ndr_small()) {
            case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
                if (logMsg) {
                    logger.info("Recieved BIND_ACK");
                    logMsg = false;
                }

            case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
                if (logMsg) {
                    logger.info("Recieved ALTER_CTX_RESP");
                    logMsg = false;
                }

            case BindPdu.BIND_TYPE:
                if (logMsg) {
                    logger.info("Recieved BIND");
                    logMsg = false;
                }

            case AlterContextPdu.ALTER_CONTEXT_TYPE:
                if (logMsg) {
                    logger.info("Recieved ALTER_CTX");
                    logMsg = false;
                }

                AuthenticationVerifier verifier = detachAuthentication(buffer);
                if (verifier != null) {
                    incomingRebind(verifier);
                }
                break;

            case FaultCoPdu.FAULT_TYPE:
                if (logMsg) {
                    logger.info("Recieved FAULT");
                    logMsg = false;
                }

            case CancelCoPdu.CANCEL_TYPE:
                if (logMsg) {
                    logger.info("Recieved CANCEL");
                    logMsg = false;
                }

            case OrphanedPdu.ORPHANED_TYPE:
                if (logMsg) {
                    logger.info("Recieved ORPHANED");
                    logMsg = false;
                }

            case ResponseCoPdu.RESPONSE_TYPE:
                if (logMsg) {
                    logger.info("Recieved RESPONSE");
                    logMsg = false;
                }

            case RequestCoPdu.REQUEST_TYPE:
                if (logMsg) {
                    logger.info("Recieved REQUEST");
                    logMsg = false;
                }

                if (security != null) {
                    NetworkDataRepresentation ndr2 = new NetworkDataRepresentation();
                    ndr2.setBuffer(buffer);
                    verifyAndUnseal(ndr2);
                } else {
                    detachAuthentication(buffer);//just strip the information , do not use it.
                }
                break;
            case Auth3Pdu.AUTH3_TYPE:
                if (logMsg) {
                    logger.info("Recieved AUTH3");
                    logMsg = false;
                }

                incomingRebind(detachAuthentication2(buffer));
                break;

            case BindNoAcknowledgePdu.BIND_NO_ACKNOWLEDGE_TYPE:
            case ShutdownPdu.SHUTDOWN_TYPE:
                return;
            default:
                throw new RpcException("Invalid incoming PDU type.");
        }
    }

    protected void processOutgoing() throws IOException {
        ndr.getBuffer().setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
        boolean logMsg = true;
        switch (ndr.readUnsignedSmall()) {

            case BindPdu.BIND_TYPE:
                if (logMsg) {
                    logger.info("Sending BIND");
                    logMsg = false;
                }
            case Auth3Pdu.AUTH3_TYPE:
                if (logMsg) {
                    logger.info("Sending AUTH3");
                    logMsg = false;
                }

            case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
                if (logMsg) {
                    logger.info("Sending BIND_ACK");
                    logMsg = false;
                }

            case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
                if (logMsg) {
                    logger.info("Sending ALTER_CTX_RESP");
                    logMsg = false;
                }

                AuthenticationVerifier verifier = outgoingRebind();
                if (verifier != null) {
                    attachAuthentication(verifier);
                }

                break;
            case AlterContextPdu.ALTER_CONTEXT_TYPE:
                if (logMsg) {
                    logger.info("Sending ALTER_CTX");
                    logMsg = false;
                }
                break;
            case RequestCoPdu.REQUEST_TYPE:
                if (logMsg) {
                    logger.info("Sending REQUEST");
                    logMsg = false;
                }
//        	verifier = outgoingRebind();
//            if (verifier != null) attachAuthentication(verifier);
            case CancelCoPdu.CANCEL_TYPE:
                if (logMsg) {
                    logger.info("Sending CANCEL");
                    logMsg = false;
                }

            case OrphanedPdu.ORPHANED_TYPE:
                if (logMsg) {
                    logger.info("Sending ORPHANED");
                    logMsg = false;
                }

            case FaultCoPdu.FAULT_TYPE:
                if (logMsg) {
                    logger.info("Sending FAULT");
                    logMsg = false;
                }

            case ResponseCoPdu.RESPONSE_TYPE:
                if (logMsg) {
                    logger.info("Sending RESPONSE");
                    logMsg = false;
                }

                if (security != null) {
                    signAndSeal(ndr);
                }
                break;
            case BindNoAcknowledgePdu.BIND_NO_ACKNOWLEDGE_TYPE:
            case ShutdownPdu.SHUTDOWN_TYPE:
                return;
            default:
                throw new RpcException("Invalid outgoing PDU type.");
        }
    }

    protected void setSecurity(Security security) {
        this.security = security;
    }

    private void attachAuthentication(AuthenticationVerifier verifier)
            throws IOException {
        try {
            NdrBuffer buffer = ndr.getBuffer();
            int length = buffer.getLength();
            buffer.setIndex(length);
            verifier.encode(ndr, buffer);
            length = buffer.getLength();
            buffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
            ndr.writeUnsignedShort(length);
            ndr.writeUnsignedShort(verifier.body.length);
            // buffer.setIndex(ConnectionOrientedPdu.FLAGS_OFFSET);
            // ndr.writeUnsignedSmall(0);
        } catch (Exception ex) {
            throw new IOException("Error attaching authentication to PDU: "
                    + ex.getMessage());
        }
    }

    private AuthenticationVerifier detachAuthentication2(NdrBuffer buffer) throws IOException {
        try {
            //NdrBuffer buffer = ndr.getBuffer();
            buffer.setIndex(ConnectionOrientedPdu.AUTH_LENGTH_OFFSET);
            int length = buffer.dec_ndr_short();//ndr.readUnsignedShort(); // auth body size
            int index = 20;
            buffer.setIndex(index); //exactly at the auth type.
            AuthenticationVerifier verifier
                    = new AuthenticationVerifier(length);
            verifier.decode(ndr, buffer);
            buffer.setIndex(index + 2); // auth padding
            length = index - buffer.dec_ndr_small();//ndr.readUnsignedSmall();
            buffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
            buffer.enc_ndr_short(length);
            buffer.enc_ndr_short(0);
            //ndr.writeUnsignedShort(length);
            //ndr.writeUnsignedShort(0);
            buffer.setIndex(length);
            return verifier;
        } catch (Exception ex) {
            throw new IOException("Error stripping authentication from PDU: "
                    + ex);
        }
    }

    private AuthenticationVerifier detachAuthentication(NdrBuffer buffer) throws IOException {
        try {
            //NdrBuffer buffer = ndr.getBuffer();
            buffer.setIndex(ConnectionOrientedPdu.AUTH_LENGTH_OFFSET);
            int length = buffer.dec_ndr_short();//ndr.readUnsignedShort(); // auth body size

            if (length == 0) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("In [detachAuthentication] No authn info present...");
                }
                return null;
            }

            int index = buffer.getLength() - length - 8; // 8 = auth header size
            buffer.setIndex(index);
            AuthenticationVerifier verifier
                    = new AuthenticationVerifier(length);
            verifier.decode(ndr, buffer);
            buffer.setIndex(index + 2); // auth padding
            length = index - buffer.dec_ndr_small();//ndr.readUnsignedSmall();
            buffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
            buffer.enc_ndr_short(length);
            buffer.enc_ndr_short(0);
            buffer.setIndex(length);
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "In [detachAuthentication] (after stripping authn info) setting new FRAG_LENGTH_OFFSET for the packet as = {0}", length);
            }

            return verifier;
        } catch (Exception ex) {
            throw new IOException("Error stripping authentication from PDU: "
                    + ex);
        }
    }

    private void signAndSeal(NetworkDataRepresentation ndr) throws IOException {
        int protectionLevel = security.getProtectionLevel();
        if (protectionLevel < Security.PROTECTION_LEVEL_INTEGRITY) {
            return;
        }
        int verifierLength = security.getVerifierLength();
        AuthenticationVerifier verifier = new AuthenticationVerifier(
                security.getAuthenticationService(), protectionLevel, contextId,
                verifierLength);
        NdrBuffer buffer = ndr.getBuffer();
        int length = buffer.getLength();
        buffer.setIndex(length);
        verifier.encode(ndr, buffer);
        length = buffer.getLength();
        buffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
        ndr.writeUnsignedShort(length);
        ndr.writeUnsignedShort(verifierLength);
        int verifierIndex = length - verifierLength;
        length -= verifierLength + 8; // less verifier + header
        int index = ConnectionOrientedPdu.HEADER_LENGTH;
        buffer.setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
        switch (ndr.readUnsignedSmall()) {
            case RequestCoPdu.REQUEST_TYPE:
                index += 8;
                buffer.setIndex(ConnectionOrientedPdu.FLAGS_OFFSET);
                if ((ndr.readUnsignedSmall()
                        & ConnectionOrientedPdu.PFC_OBJECT_UUID) != 0) {
                    index += 16;
                }
                break;
            case FaultCoPdu.FAULT_TYPE:
                index += 16;
                break;
            case ResponseCoPdu.RESPONSE_TYPE:
                index += 8;
                break;
            case CancelCoPdu.CANCEL_TYPE:
            case OrphanedPdu.ORPHANED_TYPE:
                index = length;
                break;
            default:
                throw new IntegrityException("Not an authenticated PDU type.");
        }
        boolean isFragmented = true;
        buffer.setIndex(ConnectionOrientedPdu.FLAGS_OFFSET);
        int flags = ndr.readUnsignedSmall();
        if ((flags & ConnectionOrientedPdu.PFC_FIRST_FRAG) == ConnectionOrientedPdu.PFC_FIRST_FRAG
                && (flags & ConnectionOrientedPdu.PFC_LAST_FRAG) == ConnectionOrientedPdu.PFC_LAST_FRAG) {
            isFragmented = false;
        }
        length -= index;
        security.processOutgoing(ndr, index, length, verifierIndex, isFragmented);
    }

    private void verifyAndUnseal(NetworkDataRepresentation ndr) throws IOException {
        NdrBuffer buffer = ndr.getBuffer();
        buffer.setIndex(ConnectionOrientedPdu.AUTH_LENGTH_OFFSET);
        int verifierLength = ndr.readUnsignedShort();
        if (verifierLength <= 0) {
            return;
        }
        int verifierIndex = buffer.getLength() - verifierLength;
        int length = verifierIndex - 8;
        int index = ConnectionOrientedPdu.HEADER_LENGTH;
        buffer.setIndex(ConnectionOrientedPdu.TYPE_OFFSET);
        switch (ndr.readUnsignedSmall()) {
            case RequestCoPdu.REQUEST_TYPE:
                index += 8;
                buffer.setIndex(ConnectionOrientedPdu.FLAGS_OFFSET);
                if ((ndr.readUnsignedSmall()
                        & ConnectionOrientedPdu.PFC_OBJECT_UUID) != 0) {
                    index += 16;
                }
                break;
            case FaultCoPdu.FAULT_TYPE:
                index += 16;
                break;
            case ResponseCoPdu.RESPONSE_TYPE:
                index += 8;
                break;
            case CancelCoPdu.CANCEL_TYPE:
            case OrphanedPdu.ORPHANED_TYPE:
                index = length;
                break;
            default:
                throw new IntegrityException("Not an authenticated PDU type.");
        }

        length -= index;

        boolean isFragmented = true;
        buffer.setIndex(ConnectionOrientedPdu.FLAGS_OFFSET);
        int flags = ndr.readUnsignedSmall();
        if ((flags & ConnectionOrientedPdu.PFC_FIRST_FRAG) == ConnectionOrientedPdu.PFC_FIRST_FRAG
                && (flags & ConnectionOrientedPdu.PFC_LAST_FRAG) == ConnectionOrientedPdu.PFC_LAST_FRAG) {
            isFragmented = false;
        }

        security.processIncoming(ndr, index, length, verifierIndex, isFragmented);
        buffer.setIndex(verifierIndex - 6); // auth padding field
        length = verifierIndex - ndr.readUnsignedSmall() - 8;
        buffer.setIndex(ConnectionOrientedPdu.FRAG_LENGTH_OFFSET);
        // "doctor" the PDU by removing the auth and padding
        ndr.writeUnsignedShort(length);
        ndr.writeUnsignedShort(0);
        buffer.length = length;
    }

    protected void incomingRebind(AuthenticationVerifier verifier)
            throws IOException {
    }

    protected AuthenticationVerifier outgoingRebind() throws IOException {
        return null;
    }
}
