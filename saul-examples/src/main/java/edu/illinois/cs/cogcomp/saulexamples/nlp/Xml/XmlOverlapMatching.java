package edu.illinois.cs.cogcomp.saulexamples.nlp.Xml;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.OverlapMatching;

/**
 * Created by Taher on 2016-12-28.
 */
public class XmlOverlapMatching extends OverlapMatching implements IXmlSpanMatching {
    @Override
    public String getXpathQuery(String startPropertyName, String endPropertyName, int start, int end) {
        return String.format("(@%s<=%d and @%s>%d) or (@%s>=%d and @%s<%d)"
                , startPropertyName, start, endPropertyName, start, startPropertyName, start, startPropertyName , end);
    }
}
