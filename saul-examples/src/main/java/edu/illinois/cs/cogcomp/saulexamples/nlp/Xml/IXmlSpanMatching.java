package edu.illinois.cs.cogcomp.saulexamples.nlp.Xml;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.ISpanElementMatching;

/**
 * Created by Taher on 2016-12-28.
 */
public interface IXmlSpanMatching extends ISpanElementMatching {
    String getXpathQuery(String startPropertyName, String endPropertyName, int start, int end);
}
