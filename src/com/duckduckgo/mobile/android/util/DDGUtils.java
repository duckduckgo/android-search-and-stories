package com.duckduckgo.mobile.android.util;

import java.util.HashSet;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class DDGUtils {
	public static boolean saveArray(SharedPreferences prefs, String[] array, String arrayName) {   
	    SharedPreferences.Editor editor = prefs.edit();  
	    editor.putInt(arrayName +"_size", array.length);  
	    for(int i=0;i<array.length;i++)  
	        editor.putString(arrayName + "_" + i, array[i]);  
	    return editor.commit();  
	} 
	
	public static boolean saveSet(SharedPreferences prefs, Set<String> set, String setName) {   		
	    SharedPreferences.Editor editor = prefs.edit();  
	    final int setSize = set.size();
	    editor.putInt(setName +"_size", setSize);  
	    int i=0;
	    for(String s : set)  {
	        editor.putString(setName + "_" + i, s);
	        ++i;
	    }
	    return editor.commit();  
	} 
	
	public static String[] loadArray(SharedPreferences prefs, String arrayName) {  
	    int size = prefs.getInt(arrayName + "_size", 0);  
	    String array[] = new String[size];  
	    for(int i=0;i<size;i++)  
	        array[i] = prefs.getString(arrayName + "_" + i, null);  
	    return array;  
	}  
	
	public static Set<String> loadSet(SharedPreferences prefs, String setName) {  
	    final int size = prefs.getInt(setName + "_size", 0);  
	    Set<String> set = new HashSet<String>(size);
	    for(int i=0;i<size;i++)  
	        set.add(prefs.getString(setName + "_" + i, null));  
	    return set;  
	}  
	
	public static void deleteSet(SharedPreferences prefs, String setName) {  
	    final int size = prefs.getInt(setName + "_size", 0);  
	    Editor editor = prefs.edit();
	    for(int i=0;i<size;i++)  
	    	editor.remove(setName + "_" + i);  
	    editor.remove(setName + "_size");
	    editor.commit();  
	} 
}
