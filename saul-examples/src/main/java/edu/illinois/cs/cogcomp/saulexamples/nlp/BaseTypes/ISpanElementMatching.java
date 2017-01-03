package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.ISpanElement;

/**
 * Created by Taher on 2016-12-28.
 */
public interface ISpanElementMatching {
    boolean matches(ISpanElement xmlElement, ISpanElement element);
}
