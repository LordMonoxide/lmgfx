package graphics.gl32;

import java.util.Stack;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Matrix extends graphics.gl00.Matrix {
  public static Matrix4f Identity() {
    Matrix4f mat = new Matrix4f();
    mat.m00 = 1f;
    mat.m11 = 1f;
    mat.m22 = 1f;
    mat.m30 = 1f;
    mat.m31 = 1f;
    mat.m33 = 1f;
    return mat;
  }
  
  public static Matrix4f Ortho(int w, int h) {
    Matrix4f mat = new Matrix4f();
    mat.m00 =  2f / w;
    mat.m11 = -2f / h;
    mat.m22 =  2f;
    mat.m30 = -1f;
    mat.m31 =  1f;
    mat.m33 =  1f;
    return mat;
  }
  
  public static Matrix4f Translation(float x, float y) {
    Matrix4f mat = new Matrix4f();
    mat.m00 = 1f;
    mat.m11 = 1f;
    mat.m22 = 1f;
    mat.m30 = x - 1;
    mat.m31 = y - 1;
    mat.m33 = 1f;
    return mat;
  }
  
  private static Stack<Matrix4f> _matrix = new Stack<Matrix4f>();
  private Matrix4f _proj;
  private Matrix4f _top;
  
  protected Matrix() {
    _top = Identity();
  }
  
  public void setProjection(int w, int h) { setProjection(w, h, false); }
  public void setProjection(int w, int h, boolean flip) {
    _proj = Ortho(w, h);
  }
  
  public Matrix4f getProjection() {
    return _proj;
  }
  
  public Matrix4f getWorld() {
    return _top;
  }
  
  public void push() {
    _matrix.push(_top);
    _top = new Matrix4f(_top);
  }
  
  public void pop() {
    _top = _matrix.pop();
  }
  
  public void translate(float x, float y) {
    _top.translate(new Vector2f(x, y));
  }
  
  public void rotate(float angle, float x, float y) {
    _top.rotate(angle, new Vector3f(x, y, 0));
  }
  
  public void scale(float x, float y) {
    _top.scale(new Vector3f(x, y, 0));
  }
  
  public void reset() {
    _top.load(Identity());
  }
}