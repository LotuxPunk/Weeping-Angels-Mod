package me.sub.angels.common.blocks;

import me.sub.angels.common.WAObjects;
import me.sub.angels.common.tiles.TileCG;
import me.sub.angels.main.WeepingAngels;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCG extends BlockContainer implements ITileEntityProvider {

	protected static final AxisAlignedBB CG_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.09375D, 1.0D);
	
	public BlockCG() {
		super(Material.GRASS, MapColor.ADOBE);
		setUnlocalizedName("cg");
		setRegistryName(WeepingAngels.MODID, "cg");
		translucent = true;
		this.setCreativeTab(WAObjects.angelTab);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return CG_AABB;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
		return true;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	/**
	 * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to skip all rendering
	 */
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}


    /**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 *
	 * @param worldIn
	 * @param meta
	 */
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCG();
	}
}