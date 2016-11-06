//------------------------------------------------------------------------------
//
//   ArchitectureCraft - Sawbench Block
//
//------------------------------------------------------------------------------

package gcewing.architecture;

import java.util.ArrayList;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import gcewing.architecture.BaseMod.*;

public class SawbenchBlock extends BaseBlock<SawbenchTE> {

	static String model = "block/sawbench.smeg";
	static String[] textures = {"sawbench-wood", "sawbench-metal"};
	static ModelSpec modelSpec = new ModelSpec(model, textures);

	public SawbenchBlock() {
		super(Material.WOOD, SawbenchTE.class);
		//renderID = -1;
	}
	
	@Override
	public IOrientationHandler getOrientationHandler() {
		return BaseOrientation.orient4WaysByState;
	}
	
	@Override
	public String[] getTextureNames() {
		return textures;
	}
	
	@Override
	public ModelSpec getModelSpec(IBlockState state) {
		return modelSpec;
	}
	
//	@Override
//	public String getModelNameForState(IBlockState state) {
//		return model;
//	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
		EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		//System.out.printf("SawbenchBlock.onBlockActivated\n");
		if (!player.isSneaking()) {
			if (!world.isRemote) {
				//System.out.printf("SawbenchBlock.onBlockActivated: opening gui\n");
				ArchitectureCraft.mod.openGuiSawbench(world, pos, player);
			}
			return true;
		}
		else
			return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new SawbenchTE();
	}

//	@Override
//	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int data) {
//		Utils.dumpInventoryIntoWorld(world, x, y, z);
//		super.onBlockDestroyedByPlayer(world, x, y, z, data);
//	}
	
}
