//------------------------------------------------------------------------------
//
//   ArchitectureCraft - SawbenchContainer
//
//------------------------------------------------------------------------------

package gcewing.architecture;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.play.server.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;

import gcewing.architecture.BaseDataChannel.*;

public class SawbenchContainer extends BaseContainer {

	public static int guWidth = 242;
	public static int guiHeight = 224;
	public static int inputSlotLeft = 12;
	public static int inputSlotTop = 19;
	public static int outputSlotLeft = 12;
	public static int outputSlotTop = 57;

	SawbenchTE te;
	
	public static Container create(EntityPlayer player, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof SawbenchTE)
			return new SawbenchContainer(player, (SawbenchTE)te);
		else
			return null;
	}
	
	public SawbenchContainer(EntityPlayer player, SawbenchTE te) {
		super(guWidth, guiHeight);
		this.te = te;
		addSlotToContainer(new Slot(te, 0, inputSlotLeft, inputSlotTop));
		addSlotToContainer(new SlotSawbenchResult(te, 1, outputSlotLeft, outputSlotTop));
		addPlayerSlots(player, 8, guiHeight - 81);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.te.isUseableByPlayer(player);
	}

//	/**
//	 * Called to transfer a stack from one inventory to the other eg. when shift clicking.
//	 */
//	 
//	@Override
//	public ItemStack transferStackInSlot(EntityPlayer player, int par1) {
//		// TODO
//		return null;
//	}
	
	//
	//   Server
	//
	
	@Override
	public void detectAndSendChanges() {
		for (int i = 0; i < this.inventorySlots.size(); ++i) {
			ItemStack newstack = ((Slot)this.inventorySlots.get(i)).getStack();
			ItemStack oldstack = (ItemStack)this.inventoryItemStacks.get(i);
			if (!ItemStack.areItemStacksEqual(oldstack, newstack))
			{
				oldstack = newstack == null ? null : newstack.copy();
				this.inventoryItemStacks.set(i, oldstack);
				for (Object listener : listeners) {
					if (listener instanceof EntityPlayerMP) {
						//System.out.printf("SawbenchContainer.updateCraftingResults: sending %s in slot %d to player\n", newstack, i);
						((EntityPlayerMP)listener).connection.sendPacket(
							new SPacketSetSlot(windowId, i, newstack));
					}
					else
						((IContainerListener)listener).sendSlotContents(this, i, newstack);
				}
			}
		}
//		for (Object crafter : crafters)
//			sendStateTo((ICrafting)crafter);
	}

//	void sendStateTo(ICrafting crafter) {
//		//System.out.printf("SawbenchContainer.sendStateTo: %s; selectedShape = %d\n", crafter, te.selectedShape);
//		crafter.sendProgressBarUpdate(this, 0, te.selectedPage);
//		crafter.sendProgressBarUpdate(this, 1, te.selectedSlots[selectedPage]);
//		crafter.sendProgressBarUpdate(this, 2, te.pendingMaterialUsage ? 1 : 0);
//	}
	
	@ServerMessageHandler("SelectShape")
	public void onSelectShape(EntityPlayer player, ChannelInput data) {
		int page = data.readInt();
		int slot = data.readInt();
		te.setSelectedShape(page, slot);
	}

	//
	//   Client
	//

	@Override 
	public void putStackInSlot(int i, ItemStack stack) {
		// Slot update packet has arrived from server. Do not trigger crafting behaviour.
		Slot slot = getSlot(i);
		if (slot instanceof SlotSawbench) {
			//System.out.printf("SawbenchContainer.putStackInSlot: %d %s on %s\n", i, stack, te.worldObj);
			((SlotSawbench)slot).updateFromServer(stack);
		}
		else
			super.putStackInSlot(i, stack);
	}

//	@Override
//	public void updateProgressBar(int i, int value) {
//		//System.out.printf("SawbenchContainer.updateProgressBar: %d %d\n", i, value);
//		switch (i) {
//			case 0: te.selectedPage = value;
//			case 1: te.selectedSlots[selectedPage] = value;
//			case 2: te.pendingMaterialUsage = value != 0;
//		}
//	}

}

//------------------------------------------------------------------------------

class SlotSawbench extends Slot {

	SawbenchTE te;
	int index;

	public SlotSawbench(SawbenchTE te, int index, int x, int y) {
		super(te, index, x, y);
		this.te = te;
		this.index = index;
	}
	
	void updateFromServer(ItemStack stack) {
		te.inventory.setInventorySlotContents(index, stack);
	}

}

//------------------------------------------------------------------------------

class SlotSawbenchResult extends SlotSawbench {

	public SlotSawbenchResult(SawbenchTE te, int index, int x, int y) {
		super(te, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack newstack) {
		return false;
	}

}
	
	