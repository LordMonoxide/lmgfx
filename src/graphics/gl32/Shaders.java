package graphics.gl32;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shaders {
  private static Map<String, Shader> _programs = new HashMap<String, Shader>();
  private static Map<String, Integer> _shaders = new HashMap<String, Integer>();
  
  private static Shader _default = loadProgram("default.vsh", "default.fsh");
  private static Shader _box     = loadProgram("box.vsh",     "box.fsh");
  
  public static Shader getDefault() {
    return _default;
  }
  
  public static Shader getBox() {
    return _box;
  }
  
  private static int loadShader(String shader, int type) {
    int shaderID = GL20.glCreateShader(type);
    GL20.glShaderSource(shaderID, shader);
    GL20.glCompileShader(shaderID);
    
    if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
      int size = GL20.glGetShaderi(shaderID, GL20.GL_INFO_LOG_LENGTH);
      String error = GL20.glGetShaderInfoLog(shaderID, size);
      System.err.println("Error compiling shader:\n" + shader + "\n" + error);
      return 0;
    }
    
    return shaderID;
  }
  
  private static int loadShaderFile(String file, int type) {
    if(_shaders.containsKey(file)) {
      System.out.println("Shader \"" + file + "\" already loaded.");
      return _shaders.get(file);
    }
    
    System.out.println("Shader \"" + file + "\" loading...");
    
    StringBuilder shaderSource = new StringBuilder();
    
    try {
      BufferedReader reader = new BufferedReader(new FileReader(new File("../gfx/shaders/" + file)));
      String line;
      while((line = reader.readLine()) != null) {
        shaderSource.append(line).append("\n");
      }
      reader.close();
    } catch(IOException e) {
      System.err.println("Could not read file.");
      e.printStackTrace();
      return 0;
    }
    
    return loadShader(shaderSource.toString(), type);
  }
  
  public static Shader loadProgram(String vertexShader, String fragmentShader) {
    String file = vertexShader + "|" + fragmentShader;
    
    if(_programs.containsKey(file)) {
      System.out.println("Program \"" + file + "\" already loaded.");
      return _programs.get(file);
    }
    
    System.out.println("Program \"" + file + "\" loading...");
    
    int vsh  = loadShaderFile(vertexShader,   GL20.GL_VERTEX_SHADER);
    int fsh  = loadShaderFile(fragmentShader, GL20.GL_FRAGMENT_SHADER);
    int prog = GL20.glCreateProgram();
    
    if(vsh == 0 || fsh == 0 || prog == 0) {
      return null;
    }
    
    GL20.glAttachShader(prog, vsh);
    GL20.glAttachShader(prog, fsh);
    GL20.glLinkProgram(prog);
    
    if(GL20.glGetProgrami(prog, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
      int size = GL20.glGetProgrami(prog, GL20.GL_INFO_LOG_LENGTH);
      String error = GL20.glGetProgramInfoLog(prog, size);
      System.err.println("Error linking shader " + file + ":\n" + error);
      return null;
    }
    
    // Bind to shader attributes
    GL20.glBindAttribLocation(prog, 0, "vecPos");
    GL20.glBindAttribLocation(prog, 1, "vecCol");
    GL20.glBindAttribLocation(prog, 2, "vecTex");
    
    // Locate uniforms
    int proj  = GL20.glGetUniformLocation(prog, "matProj");
    int world = GL20.glGetUniformLocation(prog, "matWorld");
    int trans = GL20.glGetUniformLocation(prog, "matTrans");
    
    GL20.glValidateProgram(prog);
    
    Shader shader = new Shader(file, prog, vsh, fsh, proj, world, trans);
    
    _shaders.put(vertexShader,   vsh);
    _shaders.put(fragmentShader, fsh);
    _programs.put(file, shader);
    
    return shader;
  }
  
  public static void destroy() {
    for(Shader shader : _programs.values()) {
      shader.destroy();
    }
    
    _programs.clear();
    _shaders.clear();
  }
}