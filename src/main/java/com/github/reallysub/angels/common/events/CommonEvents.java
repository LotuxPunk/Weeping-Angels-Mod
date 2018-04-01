package com.github.reallysub.angels.common.events;

import com.github.reallysub.angels.common.WAObjects;
import com.github.reallysub.angels.common.WorldGenArms;
import com.github.reallysub.angels.main.Utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonEvents {
	
	@SubscribeEvent
	public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {
			Utils.getAllAngels(event.getEntityLiving(), 40, 40);
		}
	}
	
	@SubscribeEvent
	public static void decorBiomeEvent(DecorateBiomeEvent e) {
		if (e.getWorld().getBiome(e.getPos()).isSnowyBiome()) {
			WorldGenArms arms = new WorldGenArms(WAObjects.angelArm);
			arms.generate(e.getWorld(), e.getRand(), e.getPos());
		}
	}
	
}