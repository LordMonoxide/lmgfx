package graphics.gl32;

public class Vertex extends graphics.gl00.Vertex {
  public static final int floatSize = 4;
  
  public static final int locCount = 2;
  public static final int colCount = 4;
  public static final int texCount = 2;
  public static final int allCount = locCount + colCount + texCount;
  
  public static final int locSize = locCount * floatSize;
  public static final int colSize = colCount * floatSize;
  public static final int texSize = texCount * floatSize;
  
  public static final int locOffset = 0;
  public static final int colOffset = locOffset + locSize;
  public static final int texOffset = colOffset + colSize;
  
  public static final int stride = locSize + colSize + texSize;
}