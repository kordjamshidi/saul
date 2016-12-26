package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Taher on 2016-12-18.
 */
public abstract class NlpBaseElement {
    private String id;
    private int start;
    private int end;
    private String text;
    private Map<String, String> properties = new HashMap<>();

    public NlpBaseElement() {
        start = -1;
        end = -1;
    }

    public NlpBaseElement(String id, Integer start, Integer end, String text) {
        this.setId(id);
        this.setStart(start);
        this.setEnd(end);
        this.setText(text);
    }

    public abstract NlpBaseElementTypes getType();

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

    public int getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static NlpBaseElement create(NlpBaseElementTypes type){

        switch (type){
            case Document:
                return new Document();
            case Sentence:
                return new Sentence();
            case Phrase:
                return new Phrase();
            case Token:
                return new Token();
        }
        return null;
    }
}
