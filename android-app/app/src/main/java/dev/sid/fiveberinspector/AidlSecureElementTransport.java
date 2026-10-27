package dev.sid.fiveberinspector;

/** Adapter for the installed system AIDL interface, without a HAL init/reset call. */
final class AidlSecureElementTransport implements SecureElementTransport {
    private final Object hal;
    private final Class<?> api;
    AidlSecureElementTransport(Object hal, Class<?> api) {
        if (hal == null) throw new SecureElementFailure(SecureElementFailure.Reason.NO_SERVICE);
        this.hal = hal; this.api = api;
    }
    public boolean isCardPresent() throws Exception {
        return (Boolean) api.getMethod("isCardPresent").invoke(hal);
    }
    public int openLogicalChannel(byte[] aid, byte p2) throws Exception {
        Object response = api.getMethod("openLogicalChannel", byte[].class, byte.class)
            .invoke(hal, aid, p2);
        return response.getClass().getField("channelNumber").getInt(response);
    }
    public byte[] transmit(byte[] command) throws Exception {
        return (byte[]) api.getMethod("transmit", byte[].class).invoke(hal, (Object) command);
    }
    public void closeChannel(int channel) throws Exception {
        api.getMethod("closeChannel", byte.class).invoke(hal, (byte) channel);
    }
}
