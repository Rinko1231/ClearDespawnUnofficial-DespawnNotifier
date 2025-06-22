package com.rinko1231.despawnnotifier;


import com.rinko1231.despawnnotifier.config.DespawnNotifierConfig;
import com.rinko1231.despawnnotifier.render.RenderItemEntityExtended;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(DespawnNotifier.MODID)
public class DespawnNotifier {
	
	public static final String MODID = "despawnnotifier";
	
	public DespawnNotifier(ModContainer modContainer) {
			modContainer.registerConfig(ModConfig.Type.COMMON, DespawnNotifierConfig.SPEC,"DespawnNotifierConfig.toml");
		}

	@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class Events {

		@SubscribeEvent
		public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(EntityType.ITEM, new RenderItemEntityExtended.Factory());
		}

	}
}
