package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ISpanElement, Phrase}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLNewSensors.getHeadword
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.{IXmlSpanMatching, XmlOverlapMatching}

/**
  * Created by Taher on 2016-12-28.
  */
object XmlMatchings {
  val headwordMatching = new IXmlSpanMatching {

    override def getXpathQuery(startPropertyName: String, endPropertyName: String, start: Int, end: Int) =
      new XmlOverlapMatching().getXpathQuery(startPropertyName, endPropertyName, start, end)

    override def matches(xmlElement: ISpanElement, element: ISpanElement) = {
      val p = element.asInstanceOf[Phrase]
      val head = getHeadword(p)
      xmlElement.contains(head)
    }
  }
}
