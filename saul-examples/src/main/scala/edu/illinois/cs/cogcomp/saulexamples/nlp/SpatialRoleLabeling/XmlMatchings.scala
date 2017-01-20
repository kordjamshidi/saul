/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ISpanElement, Phrase}
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.getHeadword
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.{IXmlSpanMatching, XmlOverlapMatching}

/** Created by Taher on 2016-12-28.
  */
object XmlMatchings {
  val phraseHeadwordMatching = new IXmlSpanMatching {

    override def getXpathQuery(startPropertyName: String, endPropertyName: String, start: Int, end: Int) =
      new XmlOverlapMatching().getXpathQuery(startPropertyName, endPropertyName, start, end)

    override def matches(xmlElement: ISpanElement, element: ISpanElement) = {
      val p = element.asInstanceOf[Phrase]
      val head = getHeadword(p)
      xmlElement.contains(head)
    }
  }
  val xmlHeadwordMatching = new IXmlSpanMatching {

    override def getXpathQuery(startPropertyName: String, endPropertyName: String, start: Int, end: Int) =
      new XmlOverlapMatching().getXpathQuery(startPropertyName, endPropertyName, start, end)

    override def matches(xmlElement: ISpanElement, element: ISpanElement) = {
      val (_, start, end) = getHeadword(xmlElement.getText)
      element.getStart <= start + xmlElement.getStart && element.getEnd >= end + xmlElement.getStart
    }
  }
}
