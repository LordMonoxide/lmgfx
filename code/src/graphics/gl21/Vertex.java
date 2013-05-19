package graphics.gl21;

import org.lwjgl.opengl.GL11;

public class Vertex extends graphics.gl00.Vertex {
  public void use() {
    GL11.glColor4f(_col[0], _col[1], _col[2], _col[3]);
    GL11.glTexCoord2f(_tex[0], _tex[1]);
    GL11.glVertex2f(_loc[0], _loc[1]);
  }
}