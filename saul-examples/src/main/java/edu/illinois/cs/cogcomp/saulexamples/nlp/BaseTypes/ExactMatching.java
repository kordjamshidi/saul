package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-28.
 */
public class ExactMatching implements ISpanElementMatching {

    @Override
    public boolean matches(ISpanElement xmlElement, ISpanElement element) {
        return xmlElement.matches(element);
    }
}
