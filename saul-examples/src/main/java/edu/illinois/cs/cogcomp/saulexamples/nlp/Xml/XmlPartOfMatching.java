package edu.illinois.cs.cogcomp.saulexamples.nlp.Xml;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.PartOfMatching;

/**
 * Created by Taher on 2016-12-28.
 */
public class XmlPartOfMatching extends PartOfMatching implements IXmlSpanMatching {
    @Override
    public String getXpathQuery(String startPropertyName, String endPropertyName, int start, int end) {
        return String.format("@%s >= %d and @%s <= %d", startPropertyName, start, endPropertyName, end);
    }
}
