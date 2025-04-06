package li.cil.scannable.common.config;

import li.cil.scannable.api.API;
import li.cil.scannable.client.scanning.ScanResultProviderBlock;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@Config(modid = API.MOD_ID, category = "client", name = "Client")
public final class ClientSettings {
    @Config.LangKey(Constants.CONFIG_SCAN_STAY_DURATION)
    @Config.Comment("How long the results from a scan should remain visible (in milliseconds).")
    @Config.RangeInt(min = -1)
    public static int scanStayDuration = 10000;

    /*
     * public static int getScanStayDuration() {
     * return serverSettings != null ? serverSettings.scanStayDuration :
     * scanStayDuration;
     * }
     */

    private ClientSettings() {
    }
}