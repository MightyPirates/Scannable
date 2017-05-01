package li.cil.scannable.common.config;

public final class Constants {
    // --------------------------------------------------------------------- //
    // Mod data

    public static final String MOD_NAME = "Scannable";
    public static final String PROXY_CLIENT = "li.cil.scannable.client.ProxyClient";
    public static final String PROXY_SERVER = "li.cil.scannable.server.ProxyServer";

    // --------------------------------------------------------------------- //
    // Block, item, entity and container names

    public static final String NAME_SCANNER = "scanner";

    // --------------------------------------------------------------------- //
    // Scanner settings

    // The radius in which to collect scan results around the player.
    public static final float SCAN_RADIUS = 64f;
    // The number of ticks over which to compute scan results. Which is at the
    // same time the use time of the scanner item.
    public static final int SCAN_COMPUTE_DURATION = 40;
    // How long the ping takes to reach the end of the visible area at the
    // default visibility range of 12 chunks.
    public static final long SCAN_GROWTH_DURATION = 2000;
    // How long the results from a scan should remain visible.
    public static final long SCAN_STAY_DURATION = 10000;
}
