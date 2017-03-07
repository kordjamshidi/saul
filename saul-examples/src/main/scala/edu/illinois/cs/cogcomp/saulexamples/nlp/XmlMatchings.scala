/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ ISpanElement, ISpanElementMatching, Phrase }
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.getHeadword
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml._

/** Created by Taher on 2016-12-28.
  */
object XmlMatchings {

  val xmlContainsElementHeadwordMatching = new ISpanElementMatching {

    override def matches(xmlElement: ISpanElement, element: ISpanElement) = {
      if (xmlElement.overlaps(element)) {
        val head = element match {
          case p: Phrase => getHeadword(p)
          case _ => element
        }
        xmlElement.contains(head)
      } else {
        false
      }
    }
  }

  val elementContainsXmlHeadwordMatching = new ISpanElementMatching {

    override def matches(xmlElement: ISpanElement, element: ISpanElement) = {
      if (xmlElement.overlaps(element)) {
        val (_, start, end) = getHeadword(xmlElement.getText)
        element.getStart <= start + xmlElement.getStart && element.getEnd >= end + xmlElement.getStart
      } else {
        false
      }
    }
  }
}
