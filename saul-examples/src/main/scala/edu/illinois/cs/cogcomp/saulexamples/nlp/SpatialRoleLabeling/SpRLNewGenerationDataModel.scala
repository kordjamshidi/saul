package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Sentence, Token}
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

  val docTosen = edge(documents,sentences)
  val sentenceToToken = edge(sentences,tokens)

  // when we have the annotation in the xml then we need to just use a matching sensor
 // docTosen.addSensor(a_matchingSensor)

  //when we want to use our NLP tools then we use generating sensors
 // sentenceToToken.addSensor(a_generatingSensor)

  val spPairs = join(trajectors,landmarks)
 // val spTriplets = join(spPairs,spatialIndicators)
  val isTrajector= property(tokens){
    x: Token => x.hasXmlAttribute("trajector") // or  x.hastag("trajector")

      // each base type has two methods of hasXMLTag or hasXMLAttribute that recieves a string as input.
  }
  val lemma = property (tokens) {
    x:Token => TextAnnotationSensor(x)
  }


}

object SpRLApp extends App{

 val reader = new NlpXmlReader("SpRL/2017/test.xml", "SCENE");
 reader.loadDocuments();
 reader.getDocuments()
 SpRLNewGenerationDataModel.documents.populate(reader.getDocuments)
 SpRLNewGenerationDataModel.sentences.populate(reader.getAllSentences)



  //since we have a matching sensor the documents and the sentences ids are martched and connected
  //since we have a generating sensor for tokens the sentences are
  // tokenized and connected to the contained sentences
// we need to have a way to treat each tag as a property of the base types
  // or as a node with a subset of nodes of the base type

  SpRLNewGenerationDataModel.spPairs.populate(reader.getAllRelations("tagname"))
  //this should get two tokens since spPairs has been defined as a pair of tokens and an edge
  //will be established between the tokens in the relation wit the tokens in the token node
  //we should be able to populate the same node by filtering and joining tokens
  SpRLNewGenerationDataModel.spPairs.populate(tokens.filter(...), tokens.filter(...))


}
