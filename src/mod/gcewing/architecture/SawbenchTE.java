//------------------------------------------------------------------------------
//
//   ArchitectureCraft - Sawbench Tile Entity
//
//------------------------------------------------------------------------------

package gcewing.architecture;

import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

import net.minecraftforge.common.util.*;
import static gcewing.architecture.Shape.*;

public class SawbenchTE extends BaseTileInventory {

	final public static int materialSlot = 0;
	final public static int resultSlot = 1;
	
	final public static int[] materialSideSlots = {materialSlot};
	final public static int[] resultSideSlots = {resultSlot};
	
//	static int[] materialUsedForShape = new int[] {
//		1, 1, 2,  1, 1, 1,  1, 1, 1
//	};
//	
//	static int[] productMadeForShape = new int[] {
//		2, 3, 3,  4, 3, 1,  1, 0, 0
//	};

	public static ShapePage[] pages = {
		new ShapePage("Roofing",
			RoofTile, RoofOuterCorner, RoofInnerCorner,
			RoofRidge, RoofSmartRidge, RoofValley,
			RoofSmartValley, RoofOverhang, RoofOverhangOuterCorner,
			RoofOverhangInnerCorner, RoofOverhangGableLH, RoofOverhangGableRH,
			RoofOverhangGableEndLH, RoofOverhangGableEndRH, RoofOverhangRidge,
			RoofOverhangValley, BevelledOuterCorner, BevelledInnerCorner),
		new ShapePage("Rounded",
			Cylinder, CylinderHalf, CylinderQuarter, CylinderLargeQuarter, AnticylinderLargeQuarter,
			Pillar, Post, Pole, SphereFull, SphereHalf,
			SphereQuarter, SphereEighth, SphereEighthLarge, SphereEighthLargeRev),
		new ShapePage("Classical",
			PillarBase, Pillar, DoricCapital, DoricTriglyph, DoricTriglyphCorner, DoricMetope,
			IonicCapital, CorinthianCapital, Architrave, ArchitraveCorner, CorniceLH, CorniceRH,
			CorniceEndLH, CorniceEndRH, CorniceRidge, CorniceValley, CorniceBottom),
		new ShapePage("Window",
			WindowFrame, WindowCorner, WindowMullion),
		new ShapePage("Arches",
			ArchD1, ArchD2, ArchD3A, ArchD3B, ArchD3C, ArchD4A, ArchD4B, ArchD4C),
		new ShapePage("Railings",
			BalustradePlain, BalustradePlainOuterCorner, BalustradePlainInnerCorner,
			BalustradePlainWithNewel, BalustradePlainEnd,
			BanisterPlainTop, BanisterPlain, BanisterPlainBottom, BanisterPlainEnd, BanisterPlainInnerCorner,
			BalustradeFancy, BalustradeFancyCorner, BalustradeFancyWithNewel, BalustradeFancyNewel,
			BanisterFancyTop, BanisterFancy, BanisterFancyBottom, BanisterFancyEnd, BanisterFancyNewelTall),
		new ShapePage("Other",
			CladdingSheet, Slab, Stairs, StairsOuterCorner, StairsInnerCorner),
	};
	
	public IInventory inventory = new InventoryBasic("Items", false, 2);
	public int selectedPage = 0;
	public int[] selectedSlots = new int[pages.length];
	public boolean pendingMaterialUsage = false; // Material for the stack in the result slot
	                                      // has not yet been removed from the material slot
	                                      
	public Shape getSelectedShape() {
		if (selectedPage >= 0 && selectedPage < pages.length) {
			int slot = selectedSlots[selectedPage];
			if (slot >= 0 && slot < pages[selectedPage].size())
				return pages[selectedPage].get(slot);
		}
		return null;
	}
	
	@Override
	protected IInventory getInventory() {
		return inventory;
	}
	
//	@Override
//	public ItemStack getStackInSlotOnClosing(int i) {
//		return null; // Leave items in inventory on closing
//	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		//System.out.printf("SawbenchTE.setInventorySlotContents: %d to %s on %s\n", i, stack, worldObj);
		beforeChangeSlot(i);
		super.setInventorySlotContents(i, stack);
		updateResultSlot();
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		//System.out.printf("SawbenchTE.decrStackSize: %d by %d on %s\n", slot, amount, worldObj);
		beforeChangeSlot(slot);
		ItemStack result = super.decrStackSize(slot, amount);
		updateResultSlot();
		return result;
	}
	
	void beforeChangeSlot(int slot) {
		//System.out.printf("SawbenchTE.beforeChangeSlot: %d pmu = %s\n", slot, pendingMaterialUsage);
		if (slot == resultSlot && pendingMaterialUsage) {
			//System.out.printf("SawbenchTE.beforeChangeSlot: setting pmu to false\n");
			pendingMaterialUsage = false;
			ItemStack result = getStackInSlot(resultSlot);
			if (result != null) {
				//System.out.printf("SawbenchTE.beforeChangeSlot: using material\n");
				inventory.decrStackSize(materialSlot, materialMultiple());
			}
		}
	}

	/**
	 * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
	 * block.
	 */
	public int[] getAccessibleSlotsFromSide(int side) {
		if (side == 1) // UP
			return materialSideSlots;
		else
			return resultSideSlots;
	}

	@Override
	public void readFromNBT(NBTTagCompound tc) {
		super.readFromNBT(tc);
		selectedPage = tc.getInteger("Page");
		int[] ss = tc.getIntArray("Slots");
		if (ss != null)
			for (int page = 0; page < pages.length; page++) {
				int slot = page < ss.length ? ss[page] : 0;
				selectedSlots[page] = slot >= 0 && slot < pages[page].size() ? slot : 0;
		}
		pendingMaterialUsage = tc.getBoolean("PMU");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tc) {
		super.writeToNBT(tc);
		tc.setInteger("Page", selectedPage);
		tc.setIntArray("Slots", selectedSlots);
		tc.setBoolean("PMU", pendingMaterialUsage);
		return tc;
	}
	
//	public void setSelectedShape(int page, int slot) {
//		if (page >= 0 && page < pages.length) {
//			selectedPage = page;
//			if (slot >= 0 && slot < pages[selectedPage].size())
//				selectedSlots[selectedPage] = slot;
//			else
//				selectedSlots[selectedPage] = 0;
//			markDirty();
//			updateResultSlot();
//		}
//	}
	
	public void setSelectedShape(int page, int slot) {
		if (page >= 0 && page < pages.length) {
			selectedPage = page;
			if (slot >= 0 && slot < pages[selectedPage].size()) {
				selectedSlots[selectedPage] = slot;
				markDirty();
				updateResultSlot();
				BaseMod.sendTileEntityUpdate(this);
			}
		}
	}

	void updateResultSlot() {
		//System.out.printf("SawbenchTE.updateResultSlot: pmu = %s on %s\n",
		//	pendingMaterialUsage, worldObj);
		ItemStack oldResult = getStackInSlot(resultSlot);
		if (oldResult == null || pendingMaterialUsage) {
			ItemStack materialStack = getStackInSlot(materialSlot);
			ItemStack resultStack = null;
			Shape resultShape = getSelectedShape();
			if (resultShape != null) {
				//System.out.printf("SawbenchTE.updateResultSlot: material = %s\n", materialStack);
				//System.out.printf("SawbenchTE.updateResultSlot: old result = %s\n", resultStack);
				if (materialStack != null && materialStack.stackSize >= resultShape.materialUsed) {
					Item materialItem = materialStack.getItem();
					if (materialItem instanceof ItemBlock) {
						Block materialBlock = Block.getBlockFromItem(materialItem);
						//System.out.printf("SawbenchTE.updateResultSlot: material block id = %d\n",
						//	materialBlock.blockID);
						//if (materialBlock.isOpaqueCube()) {
						if (!materialBlock.hasTileEntity()) {
							resultStack = resultShape.kind.newStack(resultShape, materialBlock,
								materialStack.getItemDamage(), resultShape.itemsProduced);
						}
					}
				}
			}
			if (!ItemStack.areItemStacksEqual(resultStack, oldResult)) {
				//System.out.printf("SawbenchTE.updateResultSlot: setting result to %s on %s\n",
				//	resultStack, worldObj);
				inventory.setInventorySlotContents(resultSlot, resultStack);
			}
			pendingMaterialUsage = resultStack != null;
			//System.out.printf("SawbenchTE.updateResultSlot: now pmu = %s on %s\n",
			//	pendingMaterialUsage, worldObj);
		}
	}
	
	int materialMultiple() {
		//return materialUsedForShape[selectedShape];
		Shape shape = getSelectedShape();
		if (shape != null)
			return shape.materialUsed;
		return 0;
	}
	
	int resultMultiple() {
		//return productMadeForShape[selectedShape];
		Shape shape = getSelectedShape();
		if (shape != null)
			return shape.itemsProduced;
		return 0;
	}
	
}
