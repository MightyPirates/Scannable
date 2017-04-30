package li.cil.scannable.api.scanning;

public enum ScanResultRenderType {
    /**
     * Scan result information is rendered in-world, typically at the location
     * of the scan result. When used, this is called during the {@link net.minecraftforge.client.event.RenderWorldLastEvent}
     * event from the scan result manager until the scan expired or a new scan
     * was triggered.
     */
    DIEGETIC,

    /**
     * Scan result information is rendered in the UI, typically in a location
     * such that it overlaps the projected position of the scan result, or at
     * the edge of the screen if the player is looking in a direction where the
     * location of the scan result is not visible. When used, this is called
     * during the {@link net.minecraftforge.client.event.RenderGameOverlayEvent.Post}
     * event from the scan result manager until the scan  expired or a new scan
     * was triggered.
     */
    NON_DIEGETIC
}
