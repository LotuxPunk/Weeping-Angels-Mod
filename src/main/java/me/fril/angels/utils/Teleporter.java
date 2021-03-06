package me.fril.angels.utils;

import me.fril.angels.common.entities.EntityAnomaly;
import me.fril.angels.common.entities.EntityWeepingAngel;
import me.fril.angels.common.events.mods.EventAngelTeleport;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;

public final class Teleporter {

    @Nullable
	public static Entity move(Entity entity, int dimension, BlockPos pos) {
		return move(entity, dimension, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
	}

	public static BlockPos getSafePos(World world, double x, double y, double z) {
		BlockPos p = new BlockPos(x, y, z);

		if (world.isAirBlock(p)) {
			if (world.getBlockState(p.add(0, -1, 0)).getMaterial().isSolid()) {
				return p;
			} else {
				for (int i = 1; i < 255; i++) {
					if (world.getBlockState(p.add(0, -p.getY() + i - 1, 0)).getMaterial().isSolid()) {
						return new BlockPos(p.getX(), i, p.getZ());
					}
				}
			}
		} else {
			for (int i = 1; i < 255; i++) {
				if (world.isAirBlock(p.add(0, -p.getY() + i, 0)) && world.getBlockState(p.add(0, -p.getY() + i - 1, 0)).getMaterial().isSolid()) {
					return new BlockPos(p.getX(), i, p.getZ());
				}
			}
		}
		return p;
	}

	@Nullable
	public static Entity move(Entity entity, int dimension, double x, double y, double z) {
        if (entity.world.isRemote || !entity.isNonBoss()) {
			return null;
		}
		if (entity.dimension == dimension) {
			if (entity instanceof EntityPlayerMP) {
				((EntityPlayerMP) entity).connection.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
			} else {
				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			}
			return entity;
		}
		return entity.changeDimension(dimension, new WATeleport(x, y, z));
	}
	
	public static Entity move(EntityPlayer player, BlockPos pos, int dim, EntityWeepingAngel entityWeepingAngel) {

		BlockPos oldPos = player.getPosition();

    	MinecraftForge.EVENT_BUS.post(new EventAngelTeleport(player, entityWeepingAngel, pos, dim));
		BlockPos newPos = getSafePos(player.world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);

		for(int i=1; i<11; i++){
			if (isTeleportFriendlyBlock(DimensionManager.getWorld(dim), player, newPos.getX(), newPos.getZ(), newPos.getY(), i, i)) {
				EntityAnomaly anomaly = new EntityAnomaly(entityWeepingAngel.world);
				anomaly.setPositionAndUpdate(player.posX, player.posY, player.posZ);
				entityWeepingAngel.world.spawnEntity(anomaly);
				return move(player, dim, newPos.getX(), newPos.getY(), newPos.getZ());
			}
		}

		return move(player, player.dimension, oldPos.getX(), oldPos.getY(), oldPos.getZ());
	}

	public static boolean isTeleportFriendlyBlock(World targetWorld, Entity target, int x, int z, int y, int xOffset, int zOffset)
	{
		BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
		IBlockState iblockstate = targetWorld.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(target.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(target) && targetWorld.isAirBlock(blockpos.up()) && targetWorld.isAirBlock(blockpos.up(2));
	}

	private static final class WATeleport implements ITeleporter {
		private final double x, y, z;
		
		private WATeleport(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public void placeEntity(World world, Entity entity, float yaw) {
			entity.setLocationAndAngles(x, y, z, yaw, entity.rotationPitch);
		}
	}
}
