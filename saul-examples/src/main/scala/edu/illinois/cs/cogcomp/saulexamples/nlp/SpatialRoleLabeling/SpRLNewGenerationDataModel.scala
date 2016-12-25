package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader
import SpRLNewSensors._

import scala.collection.JavaConversions._

/**
  * Created by parisakordjamshidi on 12/22/16.
  */
object SpRLNewGenerationDataModel extends DataModel {


  /*
  Nodes
   */
  val documents = node[Document]
  val sentences = node[Sentence]
  val tokens = node[Token]
  val phrases = node[Phrase]
  val relations= node[Relation]

  /*
  Edges
   */
  val docTosen = edge(documents,sentences)
  // here DocToSentence needs to check the id match, therefore we need to have the id of documents referenced in the senetences as parent id
  docTosen.addSensor(DocToSentence _)
  val sentenceToToken = edge(sentences,tokens)

  val relToTr = edge (relations,phrases)
  relToTr.addSensor( /* here we need to compare the tr id that appears in the relatiuon with the id that appeas in the phrase, or use the spans*/)
  // maybe we do not need the join nodes at all then?!

  val relToLm = edge (relations,phrases)
  val relToSp = edge (relations, phrases)

  /*
     Properties
  */

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
// val spTriplets = join(spPairs,spatialIndicators)
//  val isTrajector= property(tokens){
//    x: Token => x.hasXmlAttribute("trajector") // or  x.hastag("trajector")
// each base type has two methods of hasXMLTag or hasXMLAttribute that recieves a string as input.
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

   val relationList = reader.getRelations("RELATION", "Trajector_Indicator", "trajector_id", "spatial_indicator_id")
  //  reader.addPropertiesFromTag("TESTPROP", phrasesList)

  documents.populate(documentList)
  sentences.populate(sentencesList)
  phrases.populate(phrasesList)

 println( documents() prop testDocumentProperty)
 println (phrases() prop testPhraseProperty)

}
