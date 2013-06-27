package com.alipay.android.push.connection;

import java.io.PrintStream;
import java.io.PrintWriter;

import com.alipay.android.push.util.StreamError;
import com.alipay.android.push.util.XMPPError;

/**
 * A generic exception that is thrown when an error occurs performing an
 * XMPP operation. XMPP servers can respond to error conditions with an error code
 * and textual description of the problem, which are encapsulated in the XMPPError
 * class. When appropriate, an XMPPError instance is attached instances of this exception.<p>
 *
 * When a stream error occured, the server will send a stream error to the client before
 * closing the connection. Stream errors are unrecoverable errors. When a stream error
 * is sent to the client an XMPPException will be thrown containing the StreamError sent
 * by the server.
 *
 * @see XMPPError
 * @author Matt Tucker
 */
public class XMPPException extends Exception {

    private StreamError streamError = null;
    private Throwable wrappedThrowable = null;

    /**
     * Creates a new XMPPException.
     */
    public XMPPException() {
        super();
    }

    /**
     * Creates a new XMPPException with a description of the exception.
     *
     * @param message description of the exception.
     */
    public XMPPException(String message) {
        super(message);
    }

    /**
     * Creates a new XMPPException with the Throwable that was the root cause of the
     * exception.
     *
     * @param wrappedThrowable the root cause of the exception.
     */
    public XMPPException(Throwable wrappedThrowable) {
        super();
        this.wrappedThrowable = wrappedThrowable;
    }

    /**
     * Cretaes a new XMPPException with the stream error that was the root case of the
     * exception. When a stream error is received from the server then the underlying
     * TCP connection will be closed by the server.
     *
     * @param streamError the root cause of the exception.
     */
    public XMPPException(StreamError streamError) {
        super();
        this.streamError = streamError;
    }

    /**
     * Creates a new XMPPException with a description of the exception and the
     * Throwable that was the root cause of the exception.
     *
     * @param message a description of the exception.
     * @param wrappedThrowable the root cause of the exception.
     */
    public XMPPException(String message, Throwable wrappedThrowable) {
        super(message);
        this.wrappedThrowable = wrappedThrowable;
    }

    /**
     * Returns the StreamError asscociated with this exception, or <tt>null</tt> if there
     * isn't one. The underlying TCP connection is closed by the server after sending the
     * stream error to the client.
     *
     * @return the StreamError asscociated with this exception.
     */
    public StreamError getStreamError() {
        return streamError;
    }

    /**
     * Returns the Throwable asscociated with this exception, or <tt>null</tt> if there
     * isn't one.
     *
     * @return the Throwable asscociated with this exception.
     */
    public Throwable getWrappedThrowable() {
        return wrappedThrowable;
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream out) {
        super.printStackTrace(out);
        if (wrappedThrowable != null) {
            out.println("Nested Exception: ");
            wrappedThrowable.printStackTrace(out);
        }
    }

    public void printStackTrace(PrintWriter out) {
        super.printStackTrace(out);
        if (wrappedThrowable != null) {
            out.println("Nested Exception: ");
            wrappedThrowable.printStackTrace(out);
        }
    }

    public String getMessage() {
        String msg = super.getMessage();
        // If the message was not set, but there is an XMPPError, return the
        // XMPPError as the message.
        if (msg == null && streamError != null) {
            return streamError.toString();
        }
        return msg;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        String message = super.getMessage();
        if (message != null) {
            buf.append(message).append(": ");
        }
        if (streamError != null) {
            buf.append(streamError);
        }
        if (wrappedThrowable != null) {
            buf.append("\n  -- caused by: ").append(wrappedThrowable);
        }

        return buf.toString();
    }
}