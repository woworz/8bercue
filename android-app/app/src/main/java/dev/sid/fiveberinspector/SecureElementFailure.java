package dev.sid.fiveberinspector;

/** Fixed diagnostic codes; never forward vendor exception messages or card identifiers. */
final class SecureElementFailure extends SecurityException {
    enum Reason {
        NO_SERVICE, SYSTEM_INTERFACE_UNAVAILABLE, ACCESS_DENIED, NO_READABLE_EUICC,
        AMBIGUOUS_EUICC, SELECTED_CARD_CHANGED, CHANNEL_CLEANUP_FAILED, READ_ONLY_TRANSPORT,
        NO_CARD, APPLET_UNAVAILABLE, INVALID_EID_RESPONSE, SERVICE_UNREACHABLE, INVALID_READER_INVENTORY
    }
    final Reason reason;
    SecureElementFailure(Reason reason) { super(reason.name()); this.reason = reason; }
    static SecureElementFailure find(Throwable error) {
        for (int depth = 0; error != null && depth < 12; depth++, error = error.getCause())
            if (error instanceof SecureElementFailure) return (SecureElementFailure) error;
        return null;
    }
    static String message(String code) {
        if (Reason.ACCESS_DENIED.name().equals(code))
            return "The system denies access to the eUICC applet. Starting the computer connection alone cannot grant missing card access on this ROM.";
        if (Reason.NO_SERVICE.name().equals(code))
            return "No accessible card service was found. Check the compatibility results for HAL, Telephony and OMAPI.";
        if (Reason.SYSTEM_INTERFACE_UNAVAILABLE.name().equals(code))
            return "Required system interface classes or methods are unavailable. Check the result for each card channel.";
        if (Reason.NO_READABLE_EUICC.name().equals(code))
            return "No readable eUICC was found. Check the card and this ROM's Secure Element access policy.";
        if (Reason.AMBIGUOUS_EUICC.name().equals(code))
            return "More than one readable eUICC was found. Choose the intended card before continuing.";
        if (Reason.SELECTED_CARD_CHANGED.name().equals(code))
            return "The explicitly selected card is missing or changed. Choose and verify a card again; no alternative was selected automatically.";
        if (Reason.CHANNEL_CLEANUP_FAILED.name().equals(code))
            return "A card channel could not be closed. Stop and verify the card connection before continuing.";
        if (Reason.INVALID_READER_INVENTORY.name().equals(code))
            return "The system reader inventory is invalid or exceeds this build's bounds. Automatic selection stopped.";
        if (Reason.NO_CARD.name().equals(code))
            return "The checked readers reported no card. Check the inserted card and the reader routing.";
        if (Reason.APPLET_UNAVAILABLE.name().equals(code))
            return "The ISD-R management applet could not be opened on the checked readers.";
        if (Reason.INVALID_EID_RESPONSE.name().equals(code))
            return "The card did not return a verifiable complete eUICC identity.";
        if (Reason.SERVICE_UNREACHABLE.name().equals(code))
            return "A card service could not be reached; it may be absent or blocked by system policy.";
        if (Reason.READ_ONLY_TRANSPORT.name().equals(code))
            return "This operation is unavailable on the selected card connection.";
        return "";
    }
}
