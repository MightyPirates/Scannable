package li.cil.scannable.util;

import li.cil.scannable.api.API;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public enum SoundManager {
    INSTANCE;

    private static final SoundEvent SCANNER_CHARGE = new SoundEvent(new ResourceLocation(API.MOD_ID, "scanner_charge"));
    private static final SoundEvent SCANNER_ACTIVATE = new SoundEvent(new ResourceLocation(API.MOD_ID, "scanner_activate"));

    @Nullable
    private PositionedSoundRecord currentChargingSound;

    public void playChargingSound() {
        currentChargingSound = PositionedSoundRecord.getMasterRecord(SCANNER_CHARGE, 1);
        Minecraft.getMinecraft().getSoundHandler().playSound(currentChargingSound);
    }

    public void stopChargingSound() {
        if (currentChargingSound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(currentChargingSound);
            currentChargingSound = null;
        }
    }

    public void playActivateSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SCANNER_ACTIVATE, 1));
    }
}
