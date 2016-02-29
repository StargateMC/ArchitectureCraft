//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base for 1.8 - Render block using model + textures
//
//------------------------------------------------------------------------------------------------

package gcewing.architecture;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.*;

import gcewing.architecture.BaseMod.*;
import gcewing.architecture.BaseModClient.*;

public class BaseModelRenderer implements ICustomRenderer {

	protected IModel model;
	protected ITexture[] textures;

	public BaseModelRenderer(IModel model, ITexture... textures) {
		this.model = model;
		this.textures = textures;
	}

	public void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, IRenderTarget target,
		EnumWorldBlockLayer layer, Trans3 t)
	{
		IBlock block = (IBlock)state.getBlock();
		Trans3 t2 = t.t(block.localToGlobalTransformation(world, pos, state));
		model.render(t2, target, textures);
	}
	
	public void renderItemStack(ItemStack stack, IRenderTarget target) {
		model.render(Trans3.blockCenter, target, textures);
	}

}

