//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base for 1.8 - Block Utilities
//
//------------------------------------------------------------------------------------------------

package gcewing.architecture;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.*;

import static gcewing.architecture.BaseUtils.*;

public class BaseBlockUtils {

	public static String getNameForBlock(Block block) {
		return Block.blockRegistry.getNameForObject(block).toString();
	}
	
	/*
	 *   Test whether a block is receiving a redstone signal from a source
	 *   other than itself. For blocks that can both send and receive in
	 *   any direction.
	 */
	public static boolean blockIsGettingExternallyPowered(World world, BlockPos pos) {
		for (EnumFacing side : facings) {
			if (isPoweringSide(world, pos.offset(side), side))
					return true;
		}
		return false;
	}
	
	static boolean isPoweringSide(World world, BlockPos pos, EnumFacing side) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block.getWeakPower(world, pos, state, side) > 0)
			return true;
		if (block.shouldCheckWeakPower(world, pos, side)) {
			for (EnumFacing side2 : facings)
				if (side2 != side.getOpposite())
					if (world.getStrongPower(pos.offset(side2), side2) > 0)
						return true;
		}
		return false;
	}

}
