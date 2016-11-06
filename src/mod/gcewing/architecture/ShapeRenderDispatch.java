//------------------------------------------------------
//
//   ArchitectureCraft - Shape rendering dispatcher
//
//------------------------------------------------------

package gcewing.architecture;

import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import net.minecraftforge.client.MinecraftForgeClient;

import gcewing.architecture.BaseModClient.*;

public class ShapeRenderDispatch implements ICustomRenderer {

	// Cannot have any per-render state, because it may be
	// called from more than one thread.

	@Override
	public void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, IRenderTarget target,
		BlockRenderLayer layer, Trans3 t)
	{
		ShapeTE te = ShapeTE.get(world, pos);
		if (te != null) {
			Trans3 t2 = t.t(te.localToGlobalRotation());
			boolean renderBase = te.baseBlockState != null
				&& te.baseBlockState.getBlock().getBlockLayer() == layer;
			boolean renderSecondary = te.secondaryBlockState != null
				&& te.secondaryBlockState.getBlock().getBlockLayer() == layer;
			renderShapeTE(te, target, t2, renderBase, renderSecondary);
		}
	}

	@Override	
	public void renderItemStack(ItemStack stack, IRenderTarget target, Trans3 t) {
		ShapeTE te = new ShapeTE();
		te.readFromItemStack(stack);
		renderShapeTE(te, target, t,
			te.baseBlockState != null, te.secondaryBlockState != null);
	}

	protected void renderShapeTE(ShapeTE te, IRenderTarget target, Trans3 t,
		boolean renderBase, boolean renderSecondary)
	{
		if (te.shape != null && (renderBase || renderSecondary)) {
			IBlockState base = te.baseBlockState;
			if (base != null) {
			    //System.out.printf("ShapeRenderDispatch.renderShapeTE: in layer %s renderBase = %s renderSecondary = %s\n",
			    //    MinecraftForgeClient.getRenderLayer(), renderBase, renderSecondary);
				TextureAtlasSprite icon = Utils.getSpriteForBlockState(base);
				TextureAtlasSprite icon2 = Utils.getSpriteForBlockState(te.secondaryBlockState);
				if (icon != null) {
					ITexture[] textures = new ITexture[4];
					if (renderBase) {
					    textures[0] = BaseTexture.fromSprite(icon);
					    textures[1] = textures[0].projected();
					}
					if (renderSecondary) {
						if (icon2 != null) {
							textures[2] = BaseTexture.fromSprite(icon2);
							textures[3] = textures[2].projected();
						}
						else
							renderSecondary = false;
					}
					if (renderBase && te.shape.kind.secondaryDefaultsToBase()) {
					    if (icon2 == null || (te.secondaryBlockState != null &&
					        te.secondaryBlockState.getBlock().getBlockLayer() != BlockRenderLayer.SOLID)) {
                                textures[2] = textures[0];
                                textures[3] = textures[1];
                                renderSecondary = renderBase;
                        }
					}
					te.shape.kind.renderShape(te, textures, target, t, renderBase, renderSecondary);
				}
			}
		}
	}
	
}
