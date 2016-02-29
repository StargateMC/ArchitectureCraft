//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base for 1.8 - Block orientation handlers
//
//------------------------------------------------------------------------------------------------

package gcewing.architecture;

import net.minecraft.block.Block;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import gcewing.architecture.BaseBlock.IOrientationHandler;

public class BaseOrientation {

	public static IOrientationHandler orient4WaysByState = new Orient4WaysByState();
	public static IOrientationHandler orient24WaysByTE = new Orient24WaysByTE();
	
	//------------------------------------------------------------------------------------------------

	public static class Orient4WaysByState implements IOrientationHandler {
	
		public IProperty FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
		public void defineProperties(BaseBlock block) {
			block.addProperty(FACING);
		}
		
		public IBlockState onBlockPlaced(Block block, World world, BlockPos pos, EnumFacing side, 
			float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
		{
			EnumFacing dir = placer.getHorizontalFacing().rotateY();
			return block.getDefaultState().withProperty(FACING, dir);
		}
		
		public Trans3 localToGlobalTransformation(IBlockAccess world, BlockPos pos, IBlockState state) {
			EnumFacing f = (EnumFacing)state.getValue(FACING);
			int i;
			switch (f) {
				case EAST: i = 0; break;
				case NORTH: i = 1; break;
				case WEST: i = 2; break;
				case SOUTH: i = 3; break;
				default: i = 0;
			}
			return new Trans3(pos).turn(i);
		}

	}

//------------------------------------------------------------------------------------------------

	public static class Orient24WaysByTE extends BaseBlock.Orient1Way {
	
		public Trans3 localToGlobalTransformation(IBlockAccess world, BlockPos pos, IBlockState state) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof BaseTileEntity) {
				BaseTileEntity bte = (BaseTileEntity)te;
				return bte.localToGlobalTransformation(pos.getX(), pos.getY(), pos.getZ());
			}
			return super.localToGlobalTransformation(world, pos, state);
		}
		
	}

}
