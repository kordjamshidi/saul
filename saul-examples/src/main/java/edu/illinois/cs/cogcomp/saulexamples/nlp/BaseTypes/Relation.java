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
    private NlpBaseElementTypes firstType;
    private NlpBaseElementTypes secondType;

    public Relation(){

    }

    public  Relation(NlpBaseElementTypes firstType, String firstId, NlpBaseElementTypes secondType, String secondId){
        this.setFirstId(firstId);
        this.setSecondId(secondId);
        this.setFirstType(firstType);
        this.setSecondType(secondType);
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

    public NlpBaseElementTypes getFirstType() {
        return firstType;
    }

    public void setFirstType(NlpBaseElementTypes firstType) {
        this.firstType = firstType;
    }

    public NlpBaseElementTypes getSecondType() {
        return secondType;
    }

    public void setSecondType(NlpBaseElementTypes secondType) {
        this.secondType = secondType;
    }
}
