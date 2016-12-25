package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Phrase, Sentence, Token}
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader

import scala.collection.JavaConversions._

/**
  * Created by parisakordjamshidi on 12/22/16.
  */
object SpRLNewGenerationDataModel extends DataModel {


  // data model
  val documents = node[Document]
  val sentences = node[Sentence]
  val tokens = node[Token]
  val phrases = node[Phrase]

  val docTosen = edge(documents,sentences)
  val sentenceToToken = edge(sentences,tokens)

  val testPhraseProperty = property(phrases) {
    x: Phrase => x.getProperty("TESTPROP_first_value")
  }

  val testDocumentProperty = property (documents) {
    x: Document => x.getProperty("test")
  }

  // when we have the annotation in the xml then we need to just use a matching sensor
 // docTosen.addSensor(a_matchingSensor)

  //when we want to use our NLP tools then we use generating sensors
 // sentenceToToken.addSensor(a_generatingSensor)

//  val spPairs = join(tokens,tokens)
// // val spTriplets = join(spPairs,spatialIndicators)
//  val isTrajector= property(tokens){
//    x: Token => x.hasXmlAttribute("trajector") // or  x.hastag("trajector")
//
//      // each base type has two methods of hasXMLTag or hasXMLAttribute that recieves a string as input.
//  }
//  val lemma = property (tokens) {
//    x:Token => TextAnnotationSensor(x)
//  }


}

object SpRLApp2 extends App{
  import SpRLNewGenerationDataModel._
   val  reader = new NlpXmlReader("/Users/parisakordjamshidi/IdeaProjects/saul/saul-examples/src/test/resources/SpRL/2017/test.xml")
   val documentList = reader.getDocuments("SCENE")
   val sentencesList = reader.getSentences("SENTENCE")
   val phrasesList = reader.getPhrases("TRAJECTOR")
  //  reader.addPropertiesFromTag("TESTPROP", phrasesList)

  documents.populate(documentList)
  sentences.populate(sentencesList)
  phrases.populate(phrasesList)

 println( documents() prop testDocumentProperty)
 println (phrases() prop testPhraseProperty)


}
