//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base for 1.8 - Texture
//
//------------------------------------------------------------------------------------------------

package gcewing.architecture;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.*;
import gcewing.architecture.BaseModClient.ITexture;

public abstract class BaseTexture implements ITexture {

	public int tintIndex;
	public double red = 1, green = 1, blue = 1;
	public boolean isEmissive;
	public boolean isProjected;

	public int tintIndex() {return tintIndex;}
	public double red() {return red;}
	public double green() {return green;}
	public double blue() {return blue;}
	public boolean isEmissive() {return isEmissive;}
	public boolean isProjected() {return isProjected;}
	public boolean isSolid() {return false;}
	
	public static ITexture fromSprite(TextureAtlasSprite icon) {
		return new Sprite(icon);
	}
	
	public static ITexture fromImage(ResourceLocation location) {
		return new Image(location);
	}
	
	public ResourceLocation location() {
		return null;
	}
	
	public ITexture tinted(int index) {
		BaseTexture result = new Proxy(this);
		result.tintIndex = index;
		return result;
	}
	
	public ITexture colored(double red, double green, double blue) {
		BaseTexture result = new Proxy(this);
		result.red = red;
		result.green = green;
		result.blue = blue;
		return result;
	}
	
	public ITexture projected() {
		BaseTexture result = new Proxy(this);
		result.isProjected = true;
		return result;
	}

	//-------------------------------------------------------------------------------------------

	public static class Proxy extends BaseTexture {
	
		public ITexture base;
		
		public Proxy(ITexture base) {
			this.base = base;
			this.tintIndex = base.tintIndex();
			this.red = base.red();
			this.green = base.green();
			this.blue = base.blue();
			this.isEmissive = base.isEmissive();
			this.isProjected = base.isProjected();
		}
	
		@Override
		public ResourceLocation location() {
			return base.location();
		}
		
		@Override
		public boolean isSolid() {
			return base.isSolid();
		}
	
		public double interpolateU(double u) {
			return base.interpolateU(u);
		}
		
		public double interpolateV(double v) {
			return base.interpolateV(v);
		}
	
	}
	
	//-------------------------------------------------------------------------------------------

	public static class Sprite extends BaseTexture {

		public TextureAtlasSprite icon;
		
		public Sprite(TextureAtlasSprite icon) {
			this.icon = icon;
			red = green = blue = 1.0;
		}
		
//		public TextureAtlasSprite getIcon() {
//			return icon;
//		}
		
		public double interpolateU(double u) {
			return icon.getInterpolatedU(u * 16);
		}
		
		public double interpolateV(double v) {
			return icon.getInterpolatedV(v * 16);
		}
	
	}
	
	//-------------------------------------------------------------------------------------------

	public static class Image extends BaseTexture {

		public ResourceLocation location;
		
		public Image(ResourceLocation location) {
			this.location = location;
		}

		public ResourceLocation location() {
			return location;
		}
	
		public double interpolateU(double u) {
			return u;
		}
		
		public double interpolateV(double v) {
			return v;
		}
	
	}

	//-------------------------------------------------------------------------------------------

	public static class Solid extends BaseTexture {
	
		public Solid(double red, double green, double blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}
	
		@Override
		public boolean isSolid() {
			return true;
		}

		public double interpolateU(double u) {return 0;}
		public double interpolateV(double v) {return 0;}
	
	}
	
	//-------------------------------------------------------------------------------------------

	public static class Debug extends Sprite {
	
		public Debug(TextureAtlasSprite icon) {
			super(icon);
		}
	
		@Override
		public double interpolateU(double u) {
			double iu = super.interpolateU(u);
			System.out.printf("BaseTexture: %s u (%s - %s)\n", icon.getIconName(), icon.getMinU(), icon.getMaxU());
			System.out.printf("BaseTexture: u %s --> %s\n", u, iu);
			return iu;
		}
		
		public double interpolateV(double v) {
			double iv = super.interpolateV(v);
			System.out.printf("BaseTexture: %s v (%s - %s)\n", icon.getIconName(), icon.getMinV(), icon.getMaxV());
			System.out.printf("BaseTexture: v %s --> %s\n", v, iv);
			return iv;
		}
	
	}

	//-------------------------------------------------------------------------------------------

}
