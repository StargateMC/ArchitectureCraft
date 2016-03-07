//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base for 1.8 - Generic Item
//
//------------------------------------------------------------------------------------------------

package gcewing.architecture;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import gcewing.architecture.BaseMod.*;
import static gcewing.architecture.BaseUtils.*;

public class BaseItem extends Item implements IItem {

    public String[] getTextureNames() {
        return null;
    }

    public ModelSpec getModelSpec(ItemStack stack) {
        return null;
    }
    
    public int getNumSubtypes() {
        return 1;
    }
    
    @Override
    public boolean getHasSubtypes() {
        return getNumSubtypes() > 1;
    }
    
}
