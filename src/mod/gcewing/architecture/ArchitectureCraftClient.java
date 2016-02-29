//------------------------------------------------------
//
//   ArchitectureCraft - Client Proxy
//
//------------------------------------------------------

package gcewing.architecture;

import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
//import cpw.mods.fml.client.registry.RenderingRegistry;

public class ArchitectureCraftClient extends BaseModClient<ArchitectureCraft> {

	public static ShapeRenderDispatch shapeRenderDispatch = new ShapeRenderDispatch();

	public ArchitectureCraftClient(ArchitectureCraft mod) {
		super(mod);
		//debugModelRegistration = true;
		RenderWindow.init(this);
	}
	
	@Override
	void registerScreens() {
		addScreen(ArchitectureCraft.guiSawbench, SawbenchGui.class);
	}

	@Override
	void registerRenderers() {
		addBlockRenderer(base.blockShape, shapeRenderDispatch);
		addItemRenderer(base.itemCladding, new CladdingRenderer());
	}
	
}
