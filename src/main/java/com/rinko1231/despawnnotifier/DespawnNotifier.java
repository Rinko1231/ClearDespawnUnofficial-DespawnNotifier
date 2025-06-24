package com.rinko1231.despawnnotifier;


import com.rinko1231.despawnnotifier.config.DespawnNotifierConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(DespawnNotifier.MODID)
public class DespawnNotifier {
    public static final String MODID = "despawnnotifier";
    public static final String MOD_NAME = "DespawnNotifier";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public DespawnNotifier(Dist dist, ModContainer modContainer) {
        if (dist.isClient()) {
            modContainer.registerConfig(ModConfig.Type.CLIENT, DespawnNotifierConfig.SPEC, "DespawnNotifierConfig.toml");
			modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
	}

    public static boolean isDev(){
        return !FMLEnvironment.production;
    }
}
