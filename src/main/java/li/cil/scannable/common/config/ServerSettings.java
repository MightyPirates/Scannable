package li.cil.scannable.common.config;

// Mirror of Settings, but with authoritative server settings.
public final class ServerSettings {
    public final boolean useEnergy;
    public final String[] oresBlacklist;
    public final String[] oresCommon;
    public final String[] oresRare;

    public ServerSettings(final boolean useEnergy, final String[] oresBlacklist, final String[] oresCommon, final String[] oresRare) {
        this.useEnergy = useEnergy;
        this.oresBlacklist = oresBlacklist;
        this.oresCommon = oresCommon;
        this.oresRare = oresRare;
    }

    public ServerSettings() {
        useEnergy = Settings.useEnergy;
        oresBlacklist = Settings.oreBlacklist;
        oresCommon = Settings.oresCommon;
        oresRare = Settings.oresRare;
    }
}
