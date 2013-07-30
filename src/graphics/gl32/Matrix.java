package graphics.gl32;

import java.util.Stack;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Matrix extends graphics.gl00.Matrix {
  private static Stack<Matrix4f> _matrix = new Stack<Matrix4f>();
  private Matrix4f _proj;
  private Matrix4f _top;
  
  protected Matrix() {
    _top = getIdentity();
  }
  
  public void setProjection(int w, int h) { setProjection(w, h, false); }
  public void setProjection(int w, int h, boolean flip) {
    _proj = new Matrix4f();
    _proj.m00 =  2f / w;
    _proj.m11 =  (flip ? 2f : -2f) / h;
    _proj.m22 =  2f; // / (far - near);
    _proj.m30 = -1f;
    _proj.m31 =  1f;
    _proj.m32 = -1f;
    _proj.m33 =  1f; //((far + near) / (far - near));
  }
  
  public Matrix4f getProjection() {
    return _proj;
  }
  
  public Matrix4f getWorld() {
    return _matrix.peek();
  }
  
  public Matrix4f getIdentity() {
    Matrix4f ident = new Matrix4f();
    ident.m00 = 1f;
    ident.m11 = 1f;
    ident.m22 = 1f;
    ident.m30 = 1f;
    ident.m31 = 1f;
    ident.m33 = 1f;
    return ident;
  }
  
  public Matrix4f getTranslation(float x, float y) {
    Matrix4f trans = new Matrix4f();
    trans.m00 = 1f;
    trans.m11 = 1f;
    trans.m22 = 1f;
    /// TODO: Figure out why I need to subtract 1
    trans.m30 = x - 1;
    trans.m31 = y - 1;
    trans.m33 = 1f;
    return trans;
  }
  
  public void push() {
    _matrix.push(_top);
    _top = new Matrix4f(_matrix.peek());
  }
  
  public void pop() {
    _top = _matrix.pop();
  }
  
  public void translate(float x, float y) {
    _matrix.peek().translate(new Vector2f(x, y));
  }
  
  public void rotate(float angle, float x, float y) {
    _matrix.peek().rotate(angle, new Vector3f(x, y, 0));
  }
  
  public void scale(float x, float y) {
    _matrix.peek().scale(new Vector3f(x, y, 0f));
  }
  
  public void reset() {
    _matrix.peek().load(getIdentity());
  }
}