package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Taher on 2016-12-25.
 */
public class Relation {
    private String id;
    private Map<String, String> properties = new HashMap<>();
    private Map<Integer, String> argumentIds = new HashMap<>();

    public Relation() {
        id = "";
    }

    public Relation(String id) {
        this.setId(id);
    }

    public boolean containsProperty(String name) {
        return properties.containsKey(name);
    }

    public String getProperty(String name) {
        if (properties.containsKey(name))
            return properties.get(name);
        return null;
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getArgumentsCount(){
        return argumentIds.size();
    }

    public void setArgumentId(int index, String argumentId) {
        argumentIds.put(index, argumentId);
    }
    public String getArgumentId(int index){
        if(!argumentIds.containsKey(index))
            return null;
        return argumentIds.get(index);
    }
}
