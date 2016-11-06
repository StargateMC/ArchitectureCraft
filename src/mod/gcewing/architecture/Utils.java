//------------------------------------------------------
//
//   ArchitectureCraft - Utilities
//
//------------------------------------------------------

package gcewing.architecture;

import java.io.*;
import java.util.*;
import static java.lang.Math.*;

import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.block.material.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class Utils {

	public static Random random = new Random();

//	public static void dumpInventoryIntoWorld(World world, int x, int y, int z) {
//		// Based on BlockChest.breakBlock()
//		IInventory te = (IInventory)world.getTileEntity(x, y, z);
//		if (te != null) {
//			for (int i = 0; i < te.getSizeInventory(); ++i) {
//				ItemStack stack = te.getStackInSlot(i);
//				if (stack != null) {
//					float dx = random.nextFloat() * 0.8F + 0.1F;
//					float dy = random.nextFloat() * 0.8F + 0.1F;
//					float dz = random.nextFloat() * 0.8F + 0.1F;
//					while (stack.stackSize > 0) {
//						int n = random.nextInt(21) + 10;
//						if (n > stack.stackSize) {
//							n = stack.stackSize;
//						}
//						stack.stackSize -= n;
//						EntityItem entity = new EntityItem(world, x + dx, y + dy, z + dz,
//							new ItemStack(stack.getItem(), n, stack.getItemDamage()));
//						if (stack.hasTagCompound()) {
//							entity.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
//						}
//						float f = 0.05F;
//						entity.motionX = random.nextGaussian() * f;
//						entity.motionY = random.nextGaussian() * f + 0.2F;
//						entity.motionZ = random.nextGaussian() * f;
//						world.spawnEntityInWorld(entity);
//					}
//				}
//			}
//		}
//	}
	
	public static int playerTurn(EntityLivingBase player) {
		return MathHelper.floor_double((player.rotationYaw * 4.0 / 360.0) + 0.5) & 3;
	}
	
	public static int lookTurn(Vector3 look) {
		double a = atan2(look.x, look.z);
		return (int)round(a * 2 / PI) & 3;
	}
	
//	public static ItemStack blockStackWithTileEntity(Block block, int size, TileEntity te) {
//		ItemStack stack = new ItemStack(block, size);
//		NBTTagCompound tag = new NBTTagCompound();
//		te.writeToNBT(tag);
//		tag.setInteger("x", 0);
//		tag.setInteger("y", 0);
//		tag.setInteger("z", 0);
//		stack.setTagCompound(tag);
//		return stack;
//	}
	
	public static boolean playerIsInCreativeMode(EntityPlayer player) {
		return (player instanceof EntityPlayerMP)
			&& ((EntityPlayerMP)player).interactionManager.isCreative();
	}
	
	public static TextureAtlasSprite getSpriteForBlockState(IBlockState state) {
	    if (state != null)
		    return Minecraft.getMinecraft().getBlockRendererDispatcher()
			    .getBlockModelShapes().getTexture(state);
		else
		    return null;
	}
	
	public static String displayNameOfBlock(Block block, int meta) {
		String name = null;
		Item item = Item.getItemFromBlock(block);
		if (item != null) {
			ItemStack stack = new ItemStack(item, 1, meta);
			name = stack.getDisplayName();
		}
		if (name == null)
			name = block.getLocalizedName();
		return "Cut from " + name;
	}
	
	public static AxisAlignedBB unionOfBoxes(List<AxisAlignedBB> list) {
		AxisAlignedBB box = list.get(0);
		int n = list.size();
		for (int i = 1; i < n; i++)
			box = box.union(list.get(i));
		return box;
	}
}
