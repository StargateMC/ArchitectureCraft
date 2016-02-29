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
import net.minecraft.world.*;

public class HammerItem extends Item {

	public HammerItem() {
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
		ShapeTE te = ShapeTE.get(world, pos);
		if (te != null) {
			te.onHammerUse(player, side, hitX, hitY, hitZ);
			return true;
		}
		return false;
	}

}
