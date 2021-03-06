package me.fril.angels.common.tileentities;

import me.fril.angels.common.entities.EntityAnomaly;
import me.fril.angels.common.entities.EntityWeepingAngel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityChronodyneGenerator extends TileEntity implements ITickable {
	
	private AxisAlignedBB AABB = new AxisAlignedBB(0.2, 0, 0, 0.8, 2, 0.1);
	
	@Override
	public void update() {
		
		if (!world.getEntitiesWithinAABB(EntityWeepingAngel.class, AABB.offset(getPos())).isEmpty() && !world.isRemote) {
			
			for (EntityWeepingAngel angel : world.getEntitiesWithinAABB(EntityWeepingAngel.class, AABB.offset(getPos()))) {
				if (world.isRemote) {
                    world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, getPos().getX(), getPos().getY(), getPos().getZ(), 1.0D, 0.0D, 0.0D);
				} else {
					EntityAnomaly a = new EntityAnomaly(world);
					a.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
					world.spawnEntity(a);
				}
				angel.setDead();
			}
			
			world.setBlockToAir(getPos());
		}
	}
	
}
