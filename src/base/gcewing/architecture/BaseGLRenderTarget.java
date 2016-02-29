//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base for 1.8 - OpenGL rendering target
//
//------------------------------------------------------------------------------------------------

package gcewing.architecture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;

import gcewing.architecture.BaseModClient.*;

public class BaseGLRenderTarget extends BaseRenderTarget {

	public boolean inventoryMode;

	protected int glMode;
	protected boolean emissiveMode;
	protected boolean texturedMode = true;
	
	public BaseGLRenderTarget() {
		super(0, 0, 0);
	}
	
	public void start() {
		//System.out.printf("BaseGLRenderTarget.start\n");
	}
	
	@Override
	public void setTexture(ITexture tex) {
		if (texture != tex) {
			super.setTexture(tex);
			ResourceLocation loc = tex.location();
			if (loc != null)
				BaseModClient.bindTexture(loc);
			setTexturedMode(!tex.isSolid());
			setEmissiveMode(tex.isEmissive());
		}
	}	
	
	protected void setEmissiveMode(boolean state) {
//		if (emissiveMode != state) {
//			glSetEnabled(GL_LIGHTING, !state);
//			if (!inventoryMode) {
//				OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
//				glSetEnabled(GL_TEXTURE_2D, !state);
//				OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
//			}
//			emissiveMode = state;
//		}
	}
	
	protected void setTexturedMode(boolean state) {
		if (texturedMode != state) {
			setGLMode(0);
			texturedMode = state;
			glSetEnabled(GL_TEXTURE_2D, state);
		}
	}

	protected void glSetEnabled(int mode, boolean state) {
		if (state) {
			//System.out.printf("BaseGLRenderTarget: glEnable(%s)\n", mode);
			glEnable(mode);
		}
		else {
			//System.out.printf("BaseGLRenderTarget: glDisable(%s)\n", mode);
			glDisable(mode);
		}
	}

	@Override
	protected void rawAddVertex(Vector3 p, double u, double v) {
		setGLMode(verticesPerFace);
		//System.out.printf("BaseGLRenderTarget: glColor4f(%.2f, %.2f, %.2f, %.2f)\n",
		//	r(), g(), b(), a());
		glColor4f(r(), g(), b(), a());
		glNormal3d(normal.x, normal.y, normal.z);
		glTexCoord2d(u, v);
		//System.out.printf("BaseGLRenderTarget: glVertex3d%s\n", p);
		glVertex3d(p.x, p.y, p.z);
	}
	
	protected void setGLMode(int mode) {
		if (glMode != mode) {
			if (glMode != 0) {
				//System.out.printf("BaseGLRenderTarget: glEnd()\n");
				glEnd();
			}
			glMode = mode;
			switch (glMode) {
				case 0:
					break;
				case 3:
					//System.out.printf("BaseGLRenderTarget: glBegin(GL_TRIANGLES)\n");
					glBegin(GL_TRIANGLES);
					break;
				case 4:
					//System.out.printf("BaseGLRenderTarget: glBegin(GL_QUADS)\n");
					glBegin(GL_QUADS);
					break;
				default:
					throw new IllegalStateException(String.format("Invalid glMode %s", glMode));
			}
		}
	}
	
	@Override
	public void finish() {
		//System.out.printf("BaseGLRenderTarget.finish\n");
		setGLMode(0);
		setEmissiveMode(false);
		setTexturedMode(true);
		super.finish();
	}

}
