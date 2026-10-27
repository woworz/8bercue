package dev.sid.fiveberinspector;

/** Channel I/O only. Caller authentication, identity binding and APDU policies stay with callers. */
interface SecureElementTransport {
    boolean isCardPresent() throws Exception;
    int openLogicalChannel(byte[] aid, byte p2) throws Exception;
    byte[] transmit(byte[] command) throws Exception;
    void closeChannel(int channel) throws Exception;
}
