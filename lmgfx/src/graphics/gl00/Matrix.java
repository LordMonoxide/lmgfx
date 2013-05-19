package graphics.gl00;

public abstract class Matrix {
  protected Matrix() { }
  public abstract void setProjection(int w, int h);
  public abstract void setProjection(int w, int h, boolean flip);
  public abstract void push();
  public abstract void pop();
  public abstract void translate(float x, float y);
  public abstract void rotate(float angle, float x, float y);
  public abstract void scale(float x, float y);
  public abstract void reset();
}