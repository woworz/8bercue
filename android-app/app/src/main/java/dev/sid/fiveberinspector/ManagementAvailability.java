package dev.sid.fiveberinspector;
/** Minimum implementation requirements, never a claim of device qualification or write authority. */
final class ManagementAvailability {
    static String reason(int api, String[] abis) {
        if(api<28)return "This build needs Android 9 or later.";
        if(abis!=null)for(String abi:abis)if("arm64-v8a".equals(abi))return "";
        return "This build needs an ARM64 phone for card management.";
    }
    private ManagementAvailability(){}
}
