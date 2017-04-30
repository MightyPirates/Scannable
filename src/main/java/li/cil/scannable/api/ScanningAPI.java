package li.cil.scannable.api;

import li.cil.scannable.api.scanning.ScanResultProvider;

public final class ScanningAPI {
    /**
     * Register a new scan result provider.
     * <p>
     * {@link ScanResultProvider}s are queried whenever a scan is performed to
     * collect the list of all scan results in the scanned area.
     *
     * @param provider the provider to add.
     */
    public static void addScanResultProvider(final ScanResultProvider provider) {
        if (API.scanningAPI != null) {
            API.scanningAPI.addScanResultProvider(provider);
        }
    }

    // --------------------------------------------------------------------- //

    private ScanningAPI() {
    }
}
