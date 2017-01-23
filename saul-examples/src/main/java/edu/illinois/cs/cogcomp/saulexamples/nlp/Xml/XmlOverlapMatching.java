/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
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
