//------------------------------------------------------------------------------
//
//   ArchitectureCraft - Chisel
//
//------------------------------------------------------------------------------

package gcewing.architecture;

import java.util.List;

import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class ChiselItem extends Item {

	public ChiselItem() {
		setMaxStackSize(1);
	}
	
	@Override
	public CreativeTabs getCreativeTab() {
		return CreativeTabs.tabTools;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player,
		World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof ShapeTE) {
			if (!world.isRemote) {
				ShapeTE ste = (ShapeTE)te;
				ste.onChiselUse(player, side, hitX, hitY, hitZ);
			}
			return true;
		}
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.glass || block == Blocks.glass_pane
			|| block == Blocks.glowstone || block == Blocks.ice)
		{
			world.setBlockState(pos, Blocks.air.getDefaultState(), 0x3);
			if (!world.isRemote) {
				dropBlockAsItem(world, pos, state);
				world.playAuxSFX(2001, pos, Block.getStateId(Blocks.stone.getDefaultState())); // block breaking sound and particles
			}
			return true;
		}
		return false;
	}

	void dropBlockAsItem(World world, BlockPos pos, IBlockState state) {
		ItemStack stack = BaseUtils.blockStackWithState(state, 1);
		Block.spawnAsEntity(world, pos, stack);
	}

}
