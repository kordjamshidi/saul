package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-24.
 */
public class Phrase extends NlpBaseElement{

    public Phrase(){

    }

    public Phrase(String id, Integer start, Integer end, String text) {
        super(id, start, end, text);
    }
}