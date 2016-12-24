package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-18.
 */
public class Token extends NlpBaseElement {
    public Token() {

    }

    public Token(String id, Integer start, Integer end, String text) {
        super(id, start, end, text);
    }
}
