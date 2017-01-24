package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Sentence, Token}
import org.scalatest.{FlatSpec, Matchers}
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.{getCandidateRelations, getPos}
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.XmlMatchings
import scala.collection.JavaConversions._

/**
  * Created by Taher on 2017-01-24.
  */
class MultiModalSpRLDataModelTests extends FlatSpec with Matchers {
  val reader = new NlpXmlReader("./saul-examples/src/test/resources/SpRL/2017/test.xml", "SCENE", "SENTENCE", null, null)
  val documentList = reader.getDocuments()
  val sentenceList = reader.getSentences()

  documents.populate(documentList)
  sentences.populate(sentenceList)

  reader.addPropertiesFromTag("TRAJECTOR", tokens().toList, XmlMatchings.xmlHeadwordMatching)
  reader.addPropertiesFromTag("LANDMARK", tokens().toList, XmlMatchings.xmlHeadwordMatching)
  reader.addPropertiesFromTag("SPATIALINDICATOR", tokens().toList, XmlMatchings.xmlHeadwordMatching)

  s"text features for `${sentences().head.getText}`" should "be correct." in {
    val firstSentenceTokens = tokens().filter(_.getSentence.getId == sentences().head.getId).toList
    spatialContext(firstSentenceTokens(0)) should be(1)
    spatialContext(firstSentenceTokens(1)) should be(0)
    spatialContext(firstSentenceTokens(2)) should be(0)
    spatialContext(firstSentenceTokens(3)) should be(0)
    spatialContext(firstSentenceTokens(4)) should be(2)
    spatialContext(firstSentenceTokens(5)) should be(0)
    spatialContext(firstSentenceTokens(6)) should be(0)
    spatialContext(firstSentenceTokens(7)) should be(0)
    spatialContext(firstSentenceTokens(8)) should be(0)
    spatialContext(firstSentenceTokens(10)) should be(0)
    spatialContext(firstSentenceTokens(11)) should be(0)
    spatialContext(firstSentenceTokens(12)) should be(0)
    spatialContext(firstSentenceTokens(13)) should be(1)
    spatialContext(firstSentenceTokens(14)) should be(0)
    spatialContext(firstSentenceTokens(15)) should be(0)
    spatialContext(firstSentenceTokens(16)) should be(0)
    spatialContext(firstSentenceTokens(17)) should be(2)
    spatialContext(firstSentenceTokens(18)) should be(0)
    spatialContext(firstSentenceTokens(19)) should be(0)

    spatialRole(firstSentenceTokens(0)) should be("Indicator")
    spatialRole(firstSentenceTokens(1)) should be("Landmark")
    spatialRole(firstSentenceTokens(2)) should be("None")
    spatialRole(firstSentenceTokens(3)) should be("Trajector")
    spatialRole(firstSentenceTokens(4)) should be("None")
    spatialRole(firstSentenceTokens(5)) should be("None")
    spatialRole(firstSentenceTokens(6)) should be("None")
    spatialRole(firstSentenceTokens(7)) should be("None")
    spatialRole(firstSentenceTokens(8)) should be("None")
    spatialRole(firstSentenceTokens(10)) should be("None")
    spatialRole(firstSentenceTokens(11)) should be("None")
    spatialRole(firstSentenceTokens(12)) should be("Trajector")
    spatialRole(firstSentenceTokens(13)) should be("None")
    spatialRole(firstSentenceTokens(14)) should be("None")
    spatialRole(firstSentenceTokens(15)) should be("Trajector")
    spatialRole(firstSentenceTokens(16)) should be("None")
    spatialRole(firstSentenceTokens(17)) should be("Indicator")
    spatialRole(firstSentenceTokens(18)) should be("Landmark")
    spatialRole(firstSentenceTokens(19)) should be("None")
  }
}
