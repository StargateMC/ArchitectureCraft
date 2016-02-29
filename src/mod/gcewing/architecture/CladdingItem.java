//------------------------------------------------------------------------------
//
//   ArchitectureCraft - Cladding Item
//
//------------------------------------------------------------------------------

package gcewing.architecture;

import java.util.List;

import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;

public class CladdingItem extends Item {

	public ItemStack newStack(IBlockState state, int stackSize) {
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		return newStack(block, meta, stackSize);
	}

	public ItemStack newStack(Block block, int meta, int stackSize) {
		ItemStack result = new ItemStack(this, stackSize, meta);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("block", BaseBlockUtils.getNameForBlock(block));
		result.setTagCompound(nbt);
		return result;
	}
	
	public IBlockState blockStateFromStack(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			Block block = Block.getBlockFromName(nbt.getString("block"));
			if (block != null)
				return block.getStateFromMeta(stack.getItemDamage());
		}
		return null;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean par4) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			Block block = Block.getBlockFromName(tag.getString("block"));
			int meta = stack.getItemDamage();
			if (block != null)
				lines.add(Utils.displayNameOfBlock(block, meta));
		}
	}

}
