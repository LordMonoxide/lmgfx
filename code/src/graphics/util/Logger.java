package graphics.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Logger {
  public static final String LOG_TEXTURE = "texture";
  public static final String LOG_SHADER  = "shader";
  
  private static HashMap<String, ArrayList<String>> _refs = new HashMap<String, ArrayList<String>>();
  
  public static void addRef(String type, String name) {
    ArrayList<String> names;
    
    if(!_refs.containsKey(type)) {
      names = new ArrayList<String>();
      _refs.put(type, names);
    } else {
      names = _refs.get(type);
    }
    
    names.add(name);
  }
  
  public static void removeRef(String type, String name) {
    ArrayList<String> names = _refs.get(type);
    
    if(names == null) {
      System.err.println("Tried to remove ref " + name + " from " + type + " but " + type + " doesn't exist.");
      return;
    }
    
    if(!names.remove(name)) {
      System.err.println("Tried to remove ref " + name + " from " + type + " but it doesn't exist.");
      return;
    } else {
      if(names.isEmpty()) {
        _refs.values().remove(names);
      }
    }
  }
  
  public static void printRefs() {
    if(!_refs.isEmpty()) {
      System.err.println("UNRELEASED REFERENCES:");
      
      for(Map.Entry<String, ArrayList<String>> entry : _refs.entrySet()) {
        System.err.println(entry.getKey() + ":");
        
        for(String ref : entry.getValue()) {
          System.err.println(ref);
        }
      }
    }
  }
}