//------------------------------------------------------------------------------
//
//   ArchitectureCraft - Hammer
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
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class HammerItem extends Item {

	public HammerItem() {
		setMaxStackSize(1);
	}
	
	@Override
	public CreativeTabs getCreativeTab() {
		return CreativeTabs.TOOLS;
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player,
		World world, BlockPos pos, EnumHand hand, EnumFacing side,
		float hitX, float hitY, float hitZ)
	{
	    System.out.printf("HammerItem.onItemUse\n");
		ShapeTE te = ShapeTE.get(world, pos);
		if (te != null) {
		    System.out.printf("HammerItem.onItemUse: te = %s\n", te);
			te.onHammerUse(player, side, hitX, hitY, hitZ);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

}
