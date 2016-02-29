//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base for 1.8 - Generic Client Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.architecture;

import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.Thread;

import static org.lwjgl.opengl.GL11.*;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import net.minecraftforge.common.*;
import net.minecraftforge.client.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.*;

import net.minecraftforge.fml.client.*;
import net.minecraftforge.fml.client.registry.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.registry.*;

//import gcewing.architecture.BaseMod.IBlock;
import gcewing.architecture.BaseMod.*;
import static gcewing.architecture.BaseUtils.setField;

public class BaseModClient<MOD extends BaseMod<? extends BaseModClient>> implements IGuiHandler {

//	static class IDBinding<T> {
//		public int id;
//		public T object;
//	}
//	
//	static class BRBinding extends IDBinding<ISimpleBlockRenderingHandler> {}
//	
//	static Map<String, BRBinding>
//		blockRenderers = new HashMap<String, BRBinding>();

	public boolean debugModelRegistration = false;

	MOD base;
	boolean customRenderingRequired;
	boolean debugSound = false;

	Map<Integer, Class<? extends GuiScreen>> screenClasses =
		new HashMap<Integer, Class<? extends GuiScreen>>();

	public BaseModClient(MOD mod) {
		base = mod;
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	public void preInit(FMLPreInitializationEvent e) {
	}
	
	public void init(FMLInitializationEvent e) {
		//System.out.printf("BaseModClient.init\n");
	}
	
	public void postInit(FMLPostInitializationEvent e) {
		//registerModelLocations();
		//registerScreens();
		//registerRenderers();
		//registerOther();
		for (BaseSubsystem sub : base.subsystems) {
			sub.registerScreens();
			sub.registerRenderers();
			sub.registerOtherClient();
		}
		registerDefaultModelLocations();
		for (BaseSubsystem sub : base.subsystems)
			sub.registerModelLocations();
		registerSavedVillagerSkins();
		if (customRenderingRequired)
			enableCustomRendering();
	}
	
	void registerSavedVillagerSkins() {
		VillagerRegistry reg = VillagerRegistry.instance();
		for (VSBinding b : base.registeredVillagers)
			reg.registerVillagerSkin(b.id, b.object);
	}
		
//	String qualifyName(String name) {
//		return getClass().getPackage().getName() + "." + name;
//	}
	
	void registerOther() {}
	
	//-------------- Screen registration --------------------------------------------------------
	
	void registerScreens() {
		//
		//  Make calls to addScreen() here.
		//
		//  Screen classes registered using these methods must implement one of:
		//
		//  (1) A static method create(EntityPlayer, World, int x, int y, int z)
		//  (2) A constructor MyScreen(EntityPlayer, World, int x, int y, int z)
		//  (3) A constructor MyScreen(MyContainer) where MyContainer is the
		//      corresponding registered container class
		//
		//System.out.printf("%s: BaseModClient.registerScreens\n", this);
	}
	
	public void addScreen(Enum id, Class<? extends GuiScreen> cls) {
		addScreen(id.ordinal(), cls);
	}

	public void addScreen(int id, Class<? extends GuiScreen> cls) {
		screenClasses.put(id, cls);
	}
	
	//-------------- Renderer registration --------------------------------------------------------
	
	void registerRenderers() {
		// Make calls to addBlockRenderer(), addItemRenderer() and addTileEntityRenderer() here
	}
	
	void addTileEntityRenderer(Class <? extends TileEntity> teClass, TileEntitySpecialRenderer renderer) {
		ClientRegistry.bindTileEntitySpecialRenderer(teClass, renderer);
	}
	
	void addEntityRenderer(Class<? extends Entity> entityClass, Render renderer) {
		RenderingRegistry.registerEntityRenderingHandler(entityClass, renderer);
	}
	
	//-------------- Client-side guis ------------------------------------------------
	
	public static void openClientGui(GuiScreen gui) {
		FMLClientHandler.instance().getClient().displayGuiScreen(gui);
	}
	
	//-------------- Rendering --------------------------------------------------------
	
	public ResourceLocation textureLocation(String path) {
		return base.resourceLocation("textures/" + path);
	}
	
	public void bindTexture(String path) {
		bindTexture(textureLocation(path));
	}
	
	public static void bindTexture(ResourceLocation rsrc) {
		TextureManager tm = Minecraft.getMinecraft().getTextureManager();
		tm.bindTexture(rsrc);
	}
	
	//--------------- Model Locations ----------------------------------------------------
	
	public ModelResourceLocation modelResourceLocation(String path, String variant) {
		return new ModelResourceLocation(base.resourceLocation(path), variant);
	}
	
	public void registerModelLocations() {
	}

	protected void registerDefaultModelLocations() {
		BlockModelShapes blockReg = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
		ItemModelMesher itemReg = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		//CustomBlockRenderDispatch blockDisp = getCustomBlockRenderDispatch();
		CustomItemRenderDispatch itemDisp = getCustomItemRenderDispatch();
		for (Block block : base.registeredBlocks) {
			Item item = Item.getItemFromBlock(block);
			if (blockNeedsCustomRendering(block)) {
				//registerRenderDispatcherForBlock(blockReg, block, blockDisp);
				if (item != null)
					registerRenderDispatcherForItem(itemReg, item, itemDisp);
			}
			else
				registerModelLocation(itemReg, item, 0, block.getUnlocalizedName());
		}
		for (Item item : base.registeredItems) {
			if (itemNeedsCustomRendering(item))
				registerRenderDispatcherForItem(itemReg, item, itemDisp);
			else
				registerModelLocation(itemReg, item, 0, item.getUnlocalizedName());
		}
	}
	
//	private void registerRenderDispatcherForBlock(BlockModelShapes reg, Block block, CustomBlockRenderDispatch disp) {
//		if (debugModelRegistration)
//			System.out.printf("BaseMod: Registering model location %s for %s\n", disp.modelLocation, block);
//		reg.registerBlockWithStateMapper(block, customBlockStateMapper);
//	}

	private void registerRenderDispatcherForItem(ItemModelMesher reg, Item item, CustomItemRenderDispatch disp) {
		if (debugModelRegistration)
			System.out.printf("BaseMod: Registering model location %s for %s\n", disp.modelLocation, item);
		reg.register(item, 0, disp.modelLocation);
	}
	
	protected boolean blockNeedsCustomRendering(Block block) {
		return blockRenderers.containsKey(block) || specifiesTextures(block);
	}
	
	protected boolean itemNeedsCustomRendering(Item item) {
		return itemRenderers.containsKey(item) || specifiesTextures(item);
	}
	
	protected boolean specifiesTextures(Object obj) {
		return obj instanceof ITextureConsumer && ((ITextureConsumer)obj).getTextureNames() != null;
	}
	
	protected void registerModelLocation(ItemModelMesher reg, Item item, int meta, String extdName) {
		String name = extdName.substring(5); // strip "item." or "tile."
		if (debugModelRegistration)
			System.out.printf("BaseMod: Registering model location %s#inventory\n", name);
		reg.register(item, 0, new ModelResourceLocation(name, "inventory"));
	}
		
//	private IStateMapper customBlockStateMapper = new DefaultStateMapper() {
//		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
//			return getCustomBlockRenderDispatch().modelLocation;
//		}
//	};

	//-------------- GUI - Internal --------------------------------------------------------
	
	/**
	 * Returns a Container to be displayed to the user. 
	 * On the client side, this needs to return a instance of GuiScreen
	 * On the server side, this needs to return a instance of Container
	 *
	 * @param ID The Gui ID Number
	 * @param player The player viewing the Gui
	 * @param world The current world
	 * @param pos Position in world
	 * @return A GuiScreen/Container to be displayed to the user, null if none.
	 */
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return base.getServerGuiElement(id, player, world, x, y, z);
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return getClientGuiElement(id, player, world, new BlockPos(x, y, z));
	}

	public Object getClientGuiElement(int id, EntityPlayer player, World world, BlockPos pos) {
		int param = id >> 16;
		id = id & 0xffff;
		Object result = null;
		if (base.debugGui)
			System.out.printf("BaseModClient.getClientGuiElement: for id %s\n", id);
		Class scrnCls = screenClasses.get(id);
		if (scrnCls != null) {
			if (base.debugGui)
				System.out.printf("BaseModClient.getClientGuiElement: Instantiating %s\n", scrnCls);
			// If there is a container class registered for this gui and the screen class has
			// a constructor taking it, instantiate the screen automatically.
			Class contCls = base.containerClasses.get(id);
			if (contCls != null) {
				try {
					if (base.debugGui)
						System.out.printf("BaseModClient.getClientGuiElement: Looking for constructor taking %s\n", contCls);
					Constructor ctor = scrnCls.getConstructor(contCls);
					if (base.debugGui)
						System.out.printf("BaseModClient.getClientGuiElement: Instantiating container\n");
					Object cont = base.createGuiElement(contCls, player, world, pos, param);
					if (cont != null) {
						if (base.debugGui)
							System.out.printf("BaseModClient.getClientGuiElement: Instantiating screen with container\n");
						try {
							result = ctor.newInstance(cont);
						}
						catch (Exception e) {
							//throw new RuntimeException(e);
							base.reportExceptionCause(e);
							return null;
						}
					}
				}
				catch (NoSuchMethodException e) {
				}
			}
			// Otherwise, contruct screen from player, world, pos.
			if (result == null)
				result = base.createGuiElement(scrnCls, player, world, pos, param);
		}
		else {
			result = getGuiScreen(id, player, world, pos, param);
		}
		base.setModOf(result);
		if (base.debugGui)
			System.out.printf("BaseModClient.getClientGuiElement: returning %s\n", result);
		return result;
	}
	
	GuiScreen getGuiScreen(int id, EntityPlayer player, World world, BlockPos pos, int param) {
		//  Called when screen id not found in registry
		System.out.printf("%s: BaseModClient.getGuiScreen: No GuiScreen class found for gui id %d\n", 
			this, id);
		return null;
	}

	//======================================= Custom Rendering =======================================
	
	public interface ICustomRenderer {
		void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, IRenderTarget target,
			EnumWorldBlockLayer layer, Trans3 t);
		void renderItemStack(ItemStack stack, IRenderTarget target);
	}
	
	public interface ITexture {
		//TextureAtlasSprite getIcon();
		ResourceLocation location();
		int tintIndex();
		double red();
		double green();
		double blue();
		double interpolateU(double u);
		double interpolateV(double v);
		boolean isEmissive();
		boolean isProjected();
		boolean isSolid();
		ITexture tinted(int index);
		ITexture colored(double red, double green, double blue);
		ITexture projected();
	}

	public interface IRenderTarget {
		void setTexture(ITexture texture);
		void setColor(double r, double g, double b, double a);
		void setNormal(Vector3 n);
		void beginTriangle();
		void beginQuad();
		void addVertex(Vector3 p, double u, double v);
		void addProjectedVertex(Vector3 p, EnumFacing face);
		void endFace();
	}
	
	public interface IModel {
		AxisAlignedBB getBounds();
		void addBoxesToList(Trans3 t, List list);
		void render(Trans3 t, IRenderTarget renderer, ITexture... textures);
	}
	
	protected Map<Block, ICustomRenderer> blockRenderers = new HashMap<Block, ICustomRenderer>();
	protected Map<Item, ICustomRenderer> itemRenderers = new HashMap<Item, ICustomRenderer>();
	protected Map<IBlockState, ICustomRenderer> stateRendererCache = new HashMap<IBlockState, ICustomRenderer>();
	protected Map<ResourceLocation, ITexture> textureCache = new HashMap<ResourceLocation, ITexture>();
	
	//-------------- Renderer registration -------------------------------

	public void addBlockRenderer(Block block, ICustomRenderer renderer) {
		blockRenderers.put(block, renderer);
		customRenderingRequired = true;
		Item item = Item.getItemFromBlock(block);
		if (item != null)
			addItemRenderer(item, renderer);
	}
	
	public void addItemRenderer(Item item, ICustomRenderer renderer) {
		itemRenderers.put(item, renderer);
	}

	//------------------------------------------------------------------------------------------------

	protected ICustomRenderer getCustomRenderer(IBlockAccess world, BlockPos pos, IBlockState state) {
		//System.out.printf("BaseModClient.getCustomRenderer: %s\n", state);
		Block block = state.getBlock();
		ICustomRenderer rend = blockRenderers.get(block);
		if (rend == null && block instanceof IBlock /*&& block.getRenderType() == -1*/) {
			IBlockState astate = block.getActualState(state, world, pos);
			rend = getCustomRendererForState(astate);
		}
		return rend;
	}
	
	protected ICustomRenderer getCustomRendererForSpec(ModelSpec spec) {
		//System.out.printf("BaseModClient.getCustomRendererForSpec: %s", spec.modelName);
		//for (int i = 0; i < spec.textureNames.length; i++)
		//	System.out.printf(" %s", spec.textureNames[i]);
		//System.out.printf("\n");
		IModel model = getModel(spec.modelName);
		ITexture[] textures = new ITexture[spec.textureNames.length];
		for (int i = 0; i < textures.length; i++)
			textures[i] = getTexture(spec.textureNames[i]);
		//for (int i = 0; i < spec.textureNames.length; i++)
		//	System.out.printf("BaseModClient.getCustomRendererForSpec: texture[%s] = %s\n",
		//		i, textures[i]);
		return new BaseModelRenderer(model, textures);
	}
	
	protected ICustomRenderer getCustomRendererForState(IBlockState astate) {
		//System.out.printf("BaseModClient.getCustomRendererForState: %s\n", astate);
		ICustomRenderer rend = stateRendererCache.get(astate);
		if (rend == null) {
			Block block = astate.getBlock();
			if (block instanceof IBlock) {
				ModelSpec spec = ((IBlock)block).getModelSpec(astate);
				if (spec != null) {
					rend = getCustomRendererForSpec(spec);
					stateRendererCache.put(astate, rend);
				}
			}
		}
		return rend;
	}

	public IModel getModel(String name) {
		return base.getModel(name);
	}

	public ITexture getTexture(String name) {
		ResourceLocation loc = base.textureLocation(name);
		return textureCache.get(loc);
	}

	@SubscribeEvent
	public void onTextureStitchEventPre(TextureStitchEvent.Pre e) {
		//System.out.printf("BaseModClient.onTextureStitchEventPre: %s\n", e.map);
		textureCache.clear();
		for (Block block : base.registeredBlocks) {
			//System.out.printf("BaseModClient.onTextureStitchEvent: Block %s\n", block.getUnlocalizedName());
			registerSprites(e.map, block);
		}
		for (Item item : base.registeredItems)
			registerSprites(e.map, item);
	}
	
	protected void registerSprites(TextureMap reg, Object obj) {
		System.out.printf("BaseModClient.registerSprites: for %s\n", obj);
		if (obj instanceof ITextureConsumer) {
			String names[] = ((ITextureConsumer)obj).getTextureNames();
			System.out.printf("BaseModClient.registerSprites: texture names = %s\n", (Object)names);
			if (names != null) {
				customRenderingRequired = true;
				for (String name : names) {
					ResourceLocation loc = base.textureLocation(name);
					if (textureCache.get(loc) == null) {
						TextureAtlasSprite icon = reg.registerSprite(loc);
						ITexture texture = BaseTexture.fromSprite(icon);
						textureCache.put(loc, texture);
					}
				}
			}
		}
	}
	
	//------------------------------------------------------------------------------------------------

	protected class CustomBlockRendererDispatcher extends BlockRendererDispatcher {
	
		protected BlockRendererDispatcher base;
	
		public CustomBlockRendererDispatcher(BlockRendererDispatcher base) {
			super(null, null);
			this.base = base;
		}
		
		@Override public BlockModelShapes getBlockModelShapes()
			{return base.getBlockModelShapes();}
		@Override public BlockModelRenderer getBlockModelRenderer()
			{return base.getBlockModelRenderer();}
		@Override public IBakedModel getModelFromBlockState(IBlockState state, IBlockAccess world, BlockPos pos)
			{return base.getModelFromBlockState(state, world, pos);}
		@Override public void renderBlockBrightness(IBlockState state, float brightness)
			{base.renderBlockBrightness(state, brightness);}
		@Override public boolean isRenderTypeChest(Block block, int i)
			{return base.isRenderTypeChest(block, i);}

		@Override
		public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite icon, IBlockAccess world) {
			ICustomRenderer rend = getCustomRenderer(world, pos, state);
			if (rend != null) {
				BaseBakedRenderTarget target = new BaseBakedRenderTarget(pos, icon);
				Trans3 t = new Trans3(-pos.getX(), -pos.getY(), -pos.getZ());
				Block block = state.getBlock();
				for (EnumWorldBlockLayer layer : EnumWorldBlockLayer.values())
					if (block.canRenderInLayer(layer))
						rend.renderBlock(world, pos, state, target, layer, t);
				IBakedModel model = target.getBakedModel();
				WorldRenderer tess = Tessellator.getInstance().getWorldRenderer();
				getBlockModelRenderer().renderModel(world, model, state, pos, tess);
			}
			else
				base.renderBlockDamage(state, pos, icon, world);
		}

//		@Override
//		public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite damageIcon, IBlockAccess world) {
//			ICustomRenderer rend = getCustomRenderer(world, pos, state);
//			if (rend != null) {
//				BaseBakedRenderTarget target = new BaseBakedRenderTarget(pos);
//				Block block = state.getBlock();
//				Trans3 t = new Trans3(-pos.getX(), -pos.getY(), -pos.getZ());
//				for (EnumWorldBlockLayer layer : EnumWorldBlockLayer.values())
//					if (block.canRenderInLayer(layer))
//						rend.renderBlock(world, pos, state, target, layer, t);
//				TextureAtlasSprite particle = getBlockModelShapes().getTexture(getBlockParticleState(state, world, pos));
//				IBakedModel model = target.getBakedModel(particle);
//				IBakedModel damageModel = (new SimpleBakedModel.Builder(model, damageIcon)).makeBakedModel();
//				WorldRenderer tess = Tessellator.getInstance().getWorldRenderer();
//				getBlockModelRenderer().renderModel(world, damageModel, state, pos, tess);
//			}
//			else
//				base.renderBlockDamage(state, pos, damageIcon, world);
//		}

//		@Override
//		public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite damageIcon, IBlockAccess world) {
//			base.renderBlockDamage(state, pos, damageIcon, world);
//		}

		@Override
		public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess world, WorldRenderer tess) {
			ICustomRenderer rend = getCustomRenderer(world, pos, state);
			if (rend != null)
				return customRenderBlockToWorld(world, pos, state, tess, null, rend);
			else
				return base.renderBlock(state, pos, world, tess);
		}
		
	}
	
	protected boolean customRenderBlockToWorld(IBlockAccess world, BlockPos pos, IBlockState state, WorldRenderer tess,
		TextureAtlasSprite icon, ICustomRenderer rend)
	{
		//System.out.printf("BaseModClient.customRenderBlock: %s\n", state);
		BaseWorldRenderTarget target = new BaseWorldRenderTarget(world, pos, tess, icon);
		EnumWorldBlockLayer layer = MinecraftForgeClient.getRenderLayer();
		rend.renderBlock(world, pos, state, target, layer, Trans3.ident);
		return target.end();
	}
	
	protected IBakedModel customRenderBlockToBakedModel(IBlockAccess world, BlockPos pos, IBlockState state,
		ICustomRenderer rend)
	{
		BaseBakedRenderTarget target = new BaseBakedRenderTarget(pos);
		Trans3 t = new Trans3(-pos.getX(), -pos.getY(), -pos.getZ());
		EnumWorldBlockLayer layer = MinecraftForgeClient.getRenderLayer();
		BlockModelShapes shapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
		TextureAtlasSprite particle = shapes.getTexture(getBlockParticleState(state, world, pos));
		rend.renderBlock(world, pos, state, target, layer, t);
		return target.getBakedModel(particle);
	}

	public IBlockState getBlockParticleState(IBlockState state, IBlockAccess world, BlockPos pos) {
		Block block = state.getBlock();
		if (block instanceof IBlock)
			return ((IBlock)block).getParticleState(world, pos);
		else
			return block.getActualState(state, world, pos);
	}

	//------------------------------------------------------------------------------------------------

	protected static class CustomRenderDispatch implements IBakedModel {
	
		public ModelResourceLocation modelLocation;
		
		public void install(ModelBakeEvent event) {
			System.out.printf("BaseModClient: Installing %s at %s\n", this, modelLocation);
			event.modelRegistry.putObject(modelLocation, this);
		}
	
		// ----- IBakedModel -----
		
    public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {return null;}
    public List<BakedQuad> getGeneralQuads() {return null;}
    public boolean isAmbientOcclusion() {return false;}
    public boolean isGui3d() {return false;}
    public boolean isBuiltInRenderer() {return false;}
    public TextureAtlasSprite getParticleTexture() {return null;}
    public ItemCameraTransforms getItemCameraTransforms() {return null;}
		
	}
	
	//------------------------------------------------------------------------------------------------
	
//	protected class CustomBlockRenderDispatch extends CustomRenderDispatch implements ISmartBlockModel {
//	
//		private List<BakedQuad> emptyQuads = new ArrayList<BakedQuad>();
//		private List<List<BakedQuad>> emptyFaceQuads = new ArrayList<List<BakedQuad>>();
//		private IBakedModel emptyBakedModel = new SimpleBakedModel(emptyQuads, emptyFaceQuads, false, false, null, null);
//	
//		public CustomBlockRenderDispatch() {
//			modelLocation = modelResourceLocation("__custblock__", "");
//		}
//		
//		// ----- ISmartBlockModel -----
//	
//		public IBakedModel handleBlockState(IBlockState state) {
//			//System.out.printf("CustomBlockRenderDispatch.handleBlockState: %s\n", state);
//			if (state instanceof BaseBlockState) {
//				BaseBlockState bstate = (BaseBlockState)state;
//				ICustomRenderer rend = getCustomRenderer(bstate.world, bstate.pos, state);
//				if (rend == null)
//					throw new RuntimeException(String.format("Could not find custom renderer for %s", state));
//				return customRenderBlockToBakedModel(bstate.world, bstate.pos, state, rend);
//			}
//			else
//				throw new RuntimeException(String.format(
//					"BaseModClient: Block with custom renderer did not return a BaseBlockState from getExtendedState(): %s",
//					state));
//		}
//		
//	}
	
	//------------------------------------------------------------------------------------------------
	
	protected class CustomItemRenderDispatch extends CustomRenderDispatch implements ISmartItemModel {
	
		public CustomItemRenderDispatch() {
			modelLocation = modelResourceLocation("__custitem__", "");
		}
		
		// ----- ISmartItemModel -----
		
		public IBakedModel handleItemState(ItemStack stack) {
			//System.out.printf("BaseModClient.CustomItemRenderDispatch.handleItemState: %s\n", stack);
			Item item = stack.getItem();
			ICustomRenderer rend = itemRenderers.get(item);
			if (rend == null && item instanceof IItem) {
				ModelSpec spec = ((IItem)item).getModelSpec(stack);
				if (spec != null)
					rend = getCustomRendererForSpec(spec);
			}
			if (rend == null) {
				Block block = Block.getBlockFromItem(item);
				if (block != null)
					rend = getCustomRendererForState(block.getDefaultState());
			}
			if (rend != null) {
				GlStateManager.shadeModel(GL_SMOOTH);
				BaseBakedRenderTarget target = new BaseBakedRenderTarget();
				rend.renderItemStack(stack, target);
				return target.getBakedModel();
			}
			else
				return null;
		}
		
	}

	//------------------------------------------------------------------------------------------------

	protected CustomBlockRendererDispatcher customBlockRendererDispatcher;
	//protected CustomBlockRenderDispatch customBlockRenderDispatch;
	protected CustomItemRenderDispatch customItemRenderDispatch;
	
	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
		//getCustomBlockRenderDispatch().install(event);
		getCustomItemRenderDispatch().install(event);
	}
	
	protected CustomBlockRendererDispatcher getCustomBlockRendererDispatcher() {
		if (customBlockRendererDispatcher == null) {
			Minecraft mc = Minecraft.getMinecraft();
			customBlockRendererDispatcher = new CustomBlockRendererDispatcher(mc.getBlockRendererDispatcher());
			setField(mc, "blockRenderDispatcher", "field_175618_aM", customBlockRendererDispatcher);
		}
		return customBlockRendererDispatcher;
	}
	
//	protected CustomBlockRenderDispatch getCustomBlockRenderDispatch() {
//		if (customBlockRenderDispatch == null)
//			customBlockRenderDispatch = new CustomBlockRenderDispatch();
//		return customBlockRenderDispatch;
//	}

	protected CustomItemRenderDispatch getCustomItemRenderDispatch() {
		if (customItemRenderDispatch == null)
			customItemRenderDispatch = new CustomItemRenderDispatch();
		return customItemRenderDispatch;
	}

	public void enableCustomRendering() {
		getCustomBlockRendererDispatcher();
	}
	
}
