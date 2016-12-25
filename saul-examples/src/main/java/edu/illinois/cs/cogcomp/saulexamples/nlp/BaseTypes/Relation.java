package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Taher on 2016-12-25.
 */
public class Relation {
    private Map<String, String> properties = new HashMap<>();
    private String firstId;
    private String secondId;
    private String name;

    public Relation(String name) {
        this.setName(name);
    }

    public Relation(String name, String firstId, String secondId) {
        this(name);
        this.setFirstId(firstId);
        this.setSecondId(secondId);
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

    public String getFirstId() {
        return firstId;
    }

    public void setFirstId(String firstId) {
        this.firstId = firstId;
    }

    public String getSecondId() {
        return secondId;
    }

    public void setSecondId(String secondId) {
        this.secondId = secondId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
