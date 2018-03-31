package com.github.reallysub.angels.common.entities;

import java.util.ArrayList;
import java.util.List;

import com.github.reallysub.angels.common.InitEvents;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EntityAngel extends EntityMob {
	
	private static DataParameter<Boolean> isAngry = EntityDataManager.<Boolean>createKey(EntityAngel.class, DataSerializers.BOOLEAN);
	private static DataParameter<Boolean> isSeen = EntityDataManager.<Boolean>createKey(EntityAngel.class, DataSerializers.BOOLEAN);
	private static DataParameter<Integer> timeViewed = EntityDataManager.<Integer>createKey(EntityAngel.class, DataSerializers.VARINT);
	
	public EntityAngel(World world) {
		super(world);
		
		tasks.addTask(2, new EntityAIAttackMelee(this, 4.0F, false));
		tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		
		tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] { EntityPigZombie.class }));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
		
		experienceValue = 25;
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		getDataManager().register(isSeen, false);
		getDataManager().register(isAngry, false);
		getDataManager().register(timeViewed, 0);
	}
	
	public void travel(float strafe, float vertical, float forward) {
		if (!isSeen() || !onGround) {
			super.travel(strafe, vertical, forward);
		} else {
			// Do nothing
		}
	}
	
	/**
	 * Causes this entity to do an upwards motion (jumping).
	 */
	@Override
	protected void jump() {
		if (!isSeen()) {
			super.jump();
		} else {
			// Do nothing
		}
	}
	
	/**
	 * @return Returns whether the angel is being isSeen or not
	 */
	public boolean isSeen() {
		return getDataManager().get(isSeen);
	}
	
	/**
	 * @return Returns whether the angel is angry or not
	 */
	public boolean isAngry() {
		return getDataManager().get(isAngry);
	}
	
	/**
	 * Set's whether the angel is being isSeen or not
	 */
	public void setAngry(boolean angry) {
		getDataManager().set(isAngry, angry);
	}
	
	/**
	 * Set's whether the angel is being isSeen or not
	 */
	public void setSeen(boolean beingViewed) {
		getDataManager().set(isSeen, beingViewed);
	}
	
	/**
	 * @return Returns the time the angel has been isSeen for
	 */
	public int getSeenTime() {
		return getDataManager().get(timeViewed);
	}
	
	/**
	 * Set's the time the angel is being isSeen for
	 */
	public void setSeenTime(int time) {
		getDataManager().set(timeViewed, time);
	}
	
	/**
	 * Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("isSeen", isSeen());
		compound.setInteger("timeSeen", getSeenTime());
		compound.setBoolean("isAngry", isAngry());
	}
	
	/**
	 * Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		
		if (compound.hasKey("isSeen")) setSeen(compound.getBoolean("isSeen"));
		
		if (compound.hasKey("timeSeen")) setSeenTime(compound.getInteger("timeSeen"));
		
		if (compound.hasKey("isAngry")) setAngry(compound.getBoolean("isAngry"));
	}
	
	@Override
	protected void collideWithEntity(Entity entity) {
		entity.applyEntityCollision(this);
		if (rand.nextInt(100) == 50) {
			WorldBorder border = entity.getEntityWorld().getWorldBorder();
			int x = rand.nextInt(border.getSize());
			int z = rand.nextInt(border.getSize());
			int y = world.getSpawnPoint().getY();
			teleportEntity(entity, x, y, z);
		}
	}
	
	/**
	 * Dead and sleeping entities cannot move
	 */
	@Override
	protected boolean isMovementBlocked() {
		return getHealth() <= 0.0F || isSeen();
	}
	
	   /**
     * Sets the rotation of the entity.
     */
	@Override
    protected void setRotation(float yaw, float pitch)
    {
       if(!isSeen()) 
       {
    	   super.setRotation(yaw, pitch);
       }
    }
	
	@Override
	public void onUpdate() {

		super.onUpdate();
		
		if (rand.nextInt(500) == 250) {
			setAngry(rand.nextBoolean());
		}
		
		if (getSeenTime() == 1 && rand.nextInt(5) == 2) {
			playSound(InitEvents.angelSeen, 1, 1);
		}
		
		if (!world.isRemote) if (isSeen()) {
			setSeenTime(getSeenTime() + 1);
			if (getSeenTime() > 15) setSeen(false);
		} else {
			setSeenTime(0);
		}
		
		if (isSeen()) {
			if (!world.isRemote) {
				if (world.getGameRules().getBoolean("mobGriefing")) {
					replaceBlocks(getEntityBoundingBox().expand(9, 9, 9));
				}
			}
		}
	}
	
	private boolean teleportEntity(Entity e, double X, double Y, double Z) {
		BlockPos p = new BlockPos(X, Y, Z);
		
		if (world.isAirBlock(p)) {
			if (world.getBlockState(p.add(0, -1, 0)).getMaterial().isSolid()) {
				e.setPositionAndUpdate(p.getX(), p.getY(), p.getZ());
			} else {
				for (int i = 1; i < 255; i++) {
					if (world.getBlockState(p.add(0, -p.getY() + i - 1, 0)).getMaterial().isSolid()) {
						e.setPositionAndUpdate(p.getX(), i, p.getZ());
					}
				}
			}
		} else {
			for (int i = 1; i < 255; i++) {
				if (world.isAirBlock(p.add(0, -p.getY() + i, 0)) && world.getBlockState(p.add(0, -p.getY() + i - 1, 0)).getMaterial().isSolid()) {
					e.setPositionAndUpdate(p.getX(), i, p.getZ());
				}
			}
		}
		return true;
	}
	
	@SubscribeEvent
	public static void cancelDamage(LivingHurtEvent e) {
		if (e.getSource().getTrueSource() instanceof Entity) {
			EntityLivingBase attacker = (EntityLivingBase) e.getSource().getTrueSource();
			EntityLivingBase victim = e.getEntityLiving();
			
			if (victim instanceof EntityAngel) {
				Item item = attacker.getHeldItem(EnumHand.MAIN_HAND).getItem();
				if (item instanceof ItemPickaxe) {
					e.setCanceled(true);
					e.setAmount(0);
					victim.attackEntityFrom(DamageSource.MAGIC, 2.0F);
				}
			}
		}
	}
	
	public void replaceBlocks(AxisAlignedBB box) {
		for (int x = (int) box.minX; x <= box.maxX; x++) {
			for (int y = (int) box.minY; y <= box.maxY; y++) {
				for (int z = (int) box.minZ; z <= box.maxZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					Block block = getEntityWorld().getBlockState(pos).getBlock();
					
					if (block == Blocks.TORCH || block == Blocks.REDSTONE_TORCH) {
						if (shouldBreak()) {
							getEntityWorld().setBlockToAir(pos);
						}
					}
					
					if (block == Blocks.LIT_PUMPKIN) {
						if (shouldBreak()) {
							getEntityWorld().setBlockToAir(pos);
						}
						getEntityWorld().setBlockState(pos, Blocks.PUMPKIN.getDefaultState());
					}
					
					if (block == Blocks.LIT_REDSTONE_LAMP) {
						if (shouldBreak()) {
							getEntityWorld().setBlockToAir(pos);
						}
						getEntityWorld().setBlockState(pos, Blocks.REDSTONE_LAMP.getDefaultState());
					}
				}
			}
		}
	}
	
	private boolean shouldBreak() {
		return rand.nextInt(1000) == 500;
	}
	
}