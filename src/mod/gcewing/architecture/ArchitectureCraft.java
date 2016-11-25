//------------------------------------------------------
//
//   ArchitectureCraft - Main
//
//------------------------------------------------------

package gcewing.architecture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Hashtable;

import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.BlockPos;
import net.minecraft.world.*;

import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.registry.*;

import gcewing.architecture.BaseDataChannel.*;

@Mod(
	modid = Info.modID,
	name = Info.modName,
	version = Info.versionNumber,
	acceptableRemoteVersions = Info.versionBounds,
	acceptedMinecraftVersions = Info.acceptedMinecraftVersions)

public class ArchitectureCraft extends BaseMod<ArchitectureCraftClient> {

	public static ArchitectureCraft mod;
	public static BaseDataChannel channel;
	
	//
	//   Blocks and Items
	//
	
	public static SawbenchBlock blockSawbench;
	public static Block blockShape;

	public static Item itemSawblade;
	public static Item itemLargePulley;
	public static Item itemChisel;
	public static Item itemHammer;
	public static CladdingItem itemCladding;
	
	public ArchitectureCraft() {
		super();
		mod = this;
		channel = new BaseDataChannel(modID);
		//debugCreativeTabs = true;
	}
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		super.init(e);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

	@Override
	ArchitectureCraftClient initClient() {
		return new ArchitectureCraftClient(this);
	}

	protected void registerBlocks() {
		blockSawbench = newBlock("sawbench", SawbenchBlock.class);
		blockSawbench.setHardness(2.0F);
		blockShape = newBlock("shape", ShapeBlock.class, ShapeItem.class);
	}
	
	protected void registerTileEntities() {
		GameRegistry.registerTileEntity(SawbenchTE.class, "gcewing.sawbench");
		GameRegistry.registerTileEntity(ShapeTE.class, "gcewing.shape");
	}
	
	protected void registerItems() {
		itemSawblade = newItem("sawblade");
		itemLargePulley = newItem("largePulley");
		itemChisel = newItem("chisel", ChiselItem.class);
		itemHammer = newItem("hammer", HammerItem.class);
		itemCladding = newItem("cladding", CladdingItem.class);
	}

	protected void registerRecipes() {
	    ItemStack orangeDye = new ItemStack(Items.dye, 1, EnumDyeColor.ORANGE.getDyeDamage());
		newRecipe(blockSawbench, 1,
			"I*I",
			"/0/",
			"/_/",
			'I', Items.iron_ingot, '*', itemSawblade, '/', Items.stick, 
			'_', Blocks.wooden_pressure_plate, '0', itemLargePulley);
		newRecipe(itemSawblade, 1,
			" I ",
			"I/I",
			" I ",
			'I', Items.iron_ingot, '/', Items.stick);
		newRecipe(itemLargePulley, 1,
			" W ",
			"W/W",
			" W ",
			'W', Blocks.planks, '/', Items.stick);
		newRecipe(itemChisel, 1,
			"I ",
			"ds",
			'I', Items.iron_ingot, 's', Items.stick, 'd', orangeDye);
		newRecipe(itemHammer, 1,
			"II ",
			"dsI",
			"ds ",
			'I', Items.iron_ingot, 's', Items.stick, 'd', orangeDye);
}
	
	//--------------- GUIs ----------------------------------------------------------

	public final static int guiSawbench = 1;
	
	@Override
	protected void registerContainers() {
		addContainer(guiSawbench, SawbenchContainer.class);
	}
	
	public void openGuiSawbench(World world, BlockPos pos, EntityPlayer player) {
		openGui(player, guiSawbench, world, pos);
	}

}
