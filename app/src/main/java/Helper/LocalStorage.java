package Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

import Constants.Constants;

public class LocalStorage {

    private static LocalStorage localStorage = null;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;
    private LocalStorage(Context context){
        preferences = context.getSharedPreferences(Constants.PREF_NAME, 0);
        editor = preferences.edit();
    }

    public static LocalStorage getInstance(Context context){
        if(localStorage == null)
            localStorage = new LocalStorage(context);
        return localStorage;
    }

    public void writeString(String key, String val){
        editor.putString(key, val);
        editor.commit();
    }

    public Boolean getBoolean(String key){
        return preferences.getBoolean(key, false);
    }

    public void setBoolean(String key, Boolean val){
        editor.putBoolean(key, val);
        editor.commit();
    }

    public void writeList(String key, ArrayList arrayList){
        Gson gson = new Gson();
        writeString(key, gson.toJson(arrayList));
    }

    public ArrayList getList(String key, Type type){
        Gson gson = new Gson();
        return gson.fromJson(getString(key), type);
    }

    public String getString(String key){
        return preferences.getString(key, "");
    }

    public void writeInt(String key, int val){
        editor.putInt(key, val);
        editor.commit();
    }

    public int getInt(String key){
        return preferences.getInt(key, -1);
    }

    public boolean exists(String key){
        return preferences.contains(key);
    }
}
