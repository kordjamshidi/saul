package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-28.
 */
public interface ISpanElement {
    int getStart();
    void setStart(int start);
    void setEnd(int end);
    int getEnd();
    boolean matches(ISpanElement e);
    boolean contains(ISpanElement e);
    boolean isPartOf(ISpanElement e);
    boolean overlaps(ISpanElement e);
}
