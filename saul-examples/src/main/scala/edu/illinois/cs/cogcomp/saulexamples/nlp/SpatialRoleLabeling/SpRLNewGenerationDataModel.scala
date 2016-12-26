package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader
import SpRLNewSensors._
import edu.illinois.cs.cogcomp.saulexamples.data.Image

import scala.collection.JavaConversions._

/**
  * Created by parisakordjamshidi on 12/22/16.
  */
object SpRLNewGenerationDataModel extends DataModel {


  /*
  Nodes
   */
  val documents = node[Document]
  val images = node[Image]
  val sentences = node[Sentence]
  val tokens = node[Token]
  val phrases = node[Phrase]
  val relations = node[Relation]

  /*
  Edges
   */

  val docTosen = edge(documents, sentences)
  docTosen.addSensor(DocToSentence _)
//  val sentenceToPhrase = edge(sentences, phrases)
//
  val relToTr = edge(relations, phrases)
  relToTr.addSensor(RelToTr _)

  // maybe we do not need the join nodes at all then?!

  val relToLm = edge(relations, phrases)

  relToLm.addSensor(RelToLm _)

  val relToSp = edge(relations, phrases)
  relToSp.addSensor(RelToSp _)
  /*
     Properties
  */

  val testPhraseProperty = property(phrases) {
    x: Phrase => x.getProperty("TESTPROP_first_value")
  }
  val testDocumentProperty = property(documents) {
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

object SpRLApp2 extends App {

  import SpRLNewGenerationDataModel._

  val reader = new NlpXmlReader("/Users/parisakordjamshidi/IdeaProjects/saul/saul-examples/src/test/resources/SpRL/2017/test.xml", "SCENE", "SENTENCE", "TRAJECTOR", null)
  val documentList = reader.getDocuments()
  val sentencesList = reader.getSentences()
  val TrajectorList = reader.getPhrases("TRAJECTOR", "TESTPROP")
  val LandmarkList = reader.getPhrases("LANDMARK")

  val relationList = reader.getRelations("RELATION")

  documents.populate(documentList)
  sentences.populate(sentencesList)
  phrases.populate(TrajectorList)
  phrases.populate(LandmarkList)

  relations.populate(relationList)

  println ("number of trajectors connected to the relations:",(relations()~> relToTr size) , "relations:" , relations().size)
  println ("number of trajectors connected to the relations:",(relations()~> relToTr prop testPhraseProperty) , "relations:" , relations().size)
  println ("number of landmarks connected to the relations:",(relations()~> relToLm size) , "relations:" , LandmarkList.size)
  println ("number of indicators connected to the relations:",(relations()~> relToSp size) , "relations:" , relations().size)

  println(documents() prop testDocumentProperty)
  println(phrases() prop testPhraseProperty)
  val d = documents()~> docTosen
  println("sentence num:", d.size , "should be equal to", sentencesList.size(), "should be equal to", sentences().size )

}
