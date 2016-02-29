//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base for 1.8 - Generic Tile Entity
//
//------------------------------------------------------------------------------------------------

package gcewing.architecture;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.nbt.*;
import net.minecraft.network.play.server.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;

import net.minecraftforge.common.*;

public class BaseTileEntity extends TileEntity
	implements BaseMod.ITileEntity, IInventory, ISidedInventory
{

	public byte side, turn;
	protected int[] allSlots;
	
	public void setSide(int side) {
		this.side = (byte)side;
	}
	
	public void setTurn(int turn) {
		this.turn = (byte)turn;
	}
	
	public Trans3 localToGlobalTransformation() {
		return localToGlobalTransformation(pos.getX(), pos.getY(), pos.getZ());
	}

	public Trans3 localToGlobalTransformation(double x, double y, double z) {
		return Trans3.sideTurn(x + 0.5, y + 0.5, z + 0.5, side, turn);
	}
	
	public Trans3 localToGlobalRotation() {
		return Trans3.sideTurn(side, turn);
	}

	@Override
	public Packet getDescriptionPacket() {
		//System.out.printf("BaseTileEntity.getDescriptionPacket for %s\n", this);
		if (syncWithClient()) {
			NBTTagCompound nbt = new NBTTagCompound();
			writeToNBT(nbt);
			return new S35PacketUpdateTileEntity(pos, 0, nbt);
		}
		else
			return null;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		//System.out.printf("BaseTileEntity.onDataPacket for %s\n", this);
		readFromNBT(pkt.getNbtCompound());
		worldObj.markBlockForUpdate(pos);
	}
	
	boolean syncWithClient() {
		return true;
	}
	
	public void markBlockForUpdate() {
		worldObj.markBlockForUpdate(pos);
	}
	
	public void playSoundEffect(String name, float volume, float pitch) {
		worldObj.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, name, volume, pitch);
	}
	
	@Override
	public void onAddedToWorld() {
	}
	
	protected IInventory getInventory() {
		return null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		side = nbt.getByte("side");
		turn = nbt.getByte("turn");
		readInventoryFromNBT(nbt);
	}

	public void readInventoryFromNBT(NBTTagCompound nbt) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			NBTTagList list = nbt.getTagList("inventory", 10);
			int n = list.tagCount();
			for (int i = 0; i < n; i++) {
				NBTTagCompound item = (NBTTagCompound)list.getCompoundTagAt(i);
				int slot = item.getInteger("slot");
				ItemStack stack = ItemStack.loadItemStackFromNBT(item);
				inventory.setInventorySlotContents(slot, stack);
			}
		}
	}
	
	public void readFromItemStackNBT(NBTTagCompound nbt) {
	}
	
	public void readFromItemStack(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null)
			readFromItemStackNBT(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (side != 0)
			nbt.setByte("side", side);
		if (turn != 0)
			nbt.setByte("turn", turn);
		writeInventoryToNBT(nbt);
	}

	public void writeInventoryToNBT(NBTTagCompound nbt) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			NBTTagList list = new NBTTagList();
			int n = inventory.getSizeInventory();
			for (int i = 0; i < n; i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack != null) {
					NBTTagCompound item = new NBTTagCompound();
					item.setInteger("slot", i);
					stack.writeToNBT(item);
					list.appendTag(item);
				}
			}
			nbt.setTag("inventory", list);
		}
	}
	
	public void writeToItemStackNBT(NBTTagCompound nbt) {
	}
	
	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}
	
	public void markChanged() {
		markDirty();
		markBlockForUpdate();
	}

//------------------------------------- IInventory -----------------------------------------

	protected void onInventoryChanged(int slot) {
		markDirty();
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getSizeInventory() : 0;
	}	

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int slot) {
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getStackInSlot(slot) : null;
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			ItemStack result = inventory.decrStackSize(slot, amount);
			onInventoryChanged(slot);
			return result;
		}
		else
			return null;
	}

//
//  1.8
//
//	/**
//	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
//	 * like when you close a workbench GUI.
//	 */
//	@Override
//	public ItemStack getStackInSlotOnClosing(int slot) {
//		IInventory inventory = getInventory();
//		if (inventory != null) {
//			ItemStack result = inventory.getStackInSlotOnClosing(slot);
//			onInventoryChanged(slot);
//			return result;
//		}
//		else
//			return null;
//	}

//
//  1.8.9
//
	@Override
	public ItemStack removeStackFromSlot(int slot) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			ItemStack result = inventory.removeStackFromSlot(slot);
			onInventoryChanged(slot);
			return result;
		}
		else
			return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			inventory.setInventorySlotContents(slot, stack);
			onInventoryChanged(slot);
		}
	}

//	/**
//	 * Returns the name of the inventory.
//	 */
//	public String getInventoryName() {
//		IInventory inventory = getInventory();
//		return (inventory != null) ? inventory.getInventoryName() : "";
//	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
	 * this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit() {
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getInventoryStackLimit() : 0;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.isUseableByPlayer(player) : true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.closeInventory(player);
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		IInventory inventory = getInventory();
		if (inventory != null)
			return inventory.isItemValidForSlot(slot, stack);
		else
			return false;
	}
	
//	public boolean hasCustomInventoryName() {
//		IInventory inventory = getInventory();
//		if (inventory != null)
//			return inventory.hasCustomInventoryName();
//		else
//			return false;
//	}

	@Override
	public int getField(int id) {
		IInventory inventory = getInventory();
		if (inventory != null)
			return inventory.getField(id);
		else
			return 0;
	}

	@Override
	public void setField(int id, int value) {
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		IInventory inventory = getInventory();
		if (inventory != null)
			return inventory.getFieldCount();
		else
			return 0;
	}

	@Override
	public void clear() {
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.clear();
	}

//------------------------------------- ISidedInventory -----------------------------------------

	/**
	 * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
	 * block.
	 */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory)inventory).getSlotsForFace(side);
		else {
			if (allSlots == null) {
				int n = getSizeInventory();
				allSlots = new int[n];
				for (int i = 0; i < n; i++)
					allSlots[i] = i;
			}
			return allSlots;
		}
	}

	/**
	 * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
	 * side
	 */
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory)inventory).canInsertItem(slot, stack, side);
		else
			return true;
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
	 * side
	 */
	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory)inventory).canExtractItem(slot, stack, side);
		else
			return true;
	}

}
