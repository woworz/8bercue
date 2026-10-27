package dev.sid.fiveberinspector;

/** Uses the system OMAPI access rules. Never replaces the system service's HAL callback. */
final class OmapiSecureElementTransport implements SecureElementTransport {
    private final Object reader, listener;
    private final Class<?> readerApi, sessionApi, channelApi, listenerApi;
    private Object session, channel;
    OmapiSecureElementTransport(Object reader, Object listener, Class<?> readerApi,
            Class<?> sessionApi, Class<?> channelApi, Class<?> listenerApi) {
        this.reader = reader; this.listener = listener; this.readerApi = readerApi;
        this.sessionApi = sessionApi; this.channelApi = channelApi; this.listenerApi = listenerApi;
    }
    public boolean isCardPresent() throws Exception {
        return (Boolean) readerApi.getMethod("isSecureElementPresent").invoke(reader);
    }
    public int openLogicalChannel(byte[] aid, byte p2) throws Exception {
        if (session != null || channel != null) throw new SecurityException("Duplicate channel open");
        session = readerApi.getMethod("openSession").invoke(reader);
        try {
            channel = sessionApi.getMethod("openLogicalChannel", byte[].class, byte.class, listenerApi)
                .invoke(session, aid, p2, listener);
            if (channel == null) throw new IllegalStateException("Channel unavailable");
            // OMAPI owns and encodes the physical channel. Policies use one virtual channel.
            return 1;
        } catch (Exception error) {
            try { closeChannel(1); }
            catch (Exception cleanup) {
                throw new SecureElementFailure(SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED);
            }
            Throwable cause = error;
            while (cause instanceof java.lang.reflect.InvocationTargetException)
                cause = ((java.lang.reflect.InvocationTargetException) cause).getTargetException();
            if (cause instanceof SecurityException)
                throw new SecureElementFailure(SecureElementFailure.Reason.ACCESS_DENIED);
            throw error;
        }
    }
    public byte[] transmit(byte[] command) throws Exception {
        if (channel == null || command == null || command.length < 5
                || (command[0] & 0xff) != 0x81)
            throw new SecurityException("Command does not address the owned logical channel");
        byte[] normalized = command.clone();
        normalized[0] = (byte) 0x80; // OMAPI inserts its own channel number.
        return (byte[]) channelApi.getMethod("transmit", byte[].class).invoke(channel, (Object) normalized);
    }
    public void closeChannel(int number) throws Exception {
        if (number != 1) throw new SecurityException("Foreign channel");
        Object oldChannel = channel, oldSession = session;
        channel = null; session = null;
        // Close only our objects, never reader.closeSessions() or a global HAL reset.
        try { if (oldChannel != null) channelApi.getMethod("close").invoke(oldChannel); }
        finally { if (oldSession != null) sessionApi.getMethod("close").invoke(oldSession); }
    }
}
