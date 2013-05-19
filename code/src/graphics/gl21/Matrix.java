package graphics.gl21;

import org.lwjgl.opengl.GL11;

public class Matrix extends graphics.gl00.Matrix {
  public void setProjection(int w, int h) { setProjection(w, h, false); }
  public void setProjection(int w, int h, boolean flip) {
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    
    if(!flip) GL11.glOrtho(0, w, h, 0, 1, -1);
    else      GL11.glOrtho(0, w, 0, h, 1, -1);
    
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
  }
  
  public void push() {
    GL11.glPushMatrix();
  }
  
  public void pop() {
    GL11.glPopMatrix();
  }
  
  public void translate(float x, float y) {
    GL11.glTranslatef(x, y, 0);
  }
  
  public void rotate(float angle, float x, float y) {
    GL11.glRotatef(angle, x, y, 0);
  }
  
  public void scale(float x, float y) {
    GL11.glScalef(x, y, 1);
  }
  
  public void reset() {
    GL11.glLoadIdentity();
  }
}