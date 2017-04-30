package li.cil.scannable.api.detail;

import li.cil.scannable.api.scanning.ScanResultProvider;

public interface ScanningAPI {
    /**
     * Register a new scan result provider.
     * <p>
     * {@link ScanResultProvider}s are queried whenever a scan is performed to
     * collect the list of all scan results in the scanned area.
     *
     * @param provider the provider to add.
     */
    void addScanResultProvider(final ScanResultProvider provider);
}
