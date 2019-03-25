package com.coffee.common.utils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



public class JsonMng{
	private JSONObject json;
	private JSONArray array;
	
	public JsonMng() {
		json= new JSONObject();
		
	}
	
	@SuppressWarnings("unchecked")
	public  void put(String key, String value) {
		json.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public  void putAll(Map map) {
		json.putAll(map);
	}
	
	public void newArray() {
		array= new JSONArray();
	}
	
	@SuppressWarnings("unchecked")
	public void arrayAdd(String value) {
		array.add(value);
	}
	
	@SuppressWarnings("unchecked")
	public void arrayAddAll(Collection c) {
		array.addAll(c);
	}
	
	@SuppressWarnings("unchecked")
	public void putArray(String key) {
		json.put(key, array);
	}
	
	public static JsonObject getfromFile(String path) throws FileNotFoundException {
		// open a json reader to load the file 
		JsonReader reader = Json.createReader(new FileInputStream(path));
		//creating the json object from the string
        JsonObject jsonObject = reader.readObject();
        //closing tjhe reader
        reader.close();
        return jsonObject;
        
		
	}
	
	/**
	 * Returns a JsonObject from a string
	 * @param chain
	 * @return
	 */
	public static JsonObject getfromString(String chain) {
		// open a json reader to load the info from outside
		JsonReader reader = Json.createReader(new StringReader(chain));
		//creating the json object from the string
        JsonObject jsonObj = reader.readObject();
        //closing tjhe reader
        reader.close();
        return jsonObj;
	}

}
