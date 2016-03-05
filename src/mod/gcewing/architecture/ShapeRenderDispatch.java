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

import gcewing.architecture.BaseModClient.*;

public class ShapeRenderDispatch implements ICustomRenderer {

	// Cannot have any per-render state, because it may be
	// called from more than one thread.

	@Override
	public void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, IRenderTarget target,
		EnumWorldBlockLayer layer, Trans3 t)
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
				TextureAtlasSprite icon = Utils.getSpriteForBlockState(base);
				if (icon != null) {
					ITexture[] textures = new ITexture[3];
					textures[0] = BaseTexture.fromSprite(icon);
					textures[1] = textures[0].projected();
					if (renderSecondary) {
						TextureAtlasSprite icon2 = Utils.getSpriteForBlockState(te.secondaryBlockState);
						if (icon2 != null)
							textures[2] = BaseTexture.fromSprite(icon2);
						else
							renderSecondary = false;
					}
					if (renderBase && textures[2] == null && te.shape.kind.secondaryDefaultsToBase()) {
						textures[2] = textures[0];
						renderSecondary = renderBase;
					}
					te.shape.kind.renderShape(te, textures, target, t, renderBase, renderSecondary);
				}
			}
		}
	}
	
}
