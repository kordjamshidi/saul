package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Sensors._

/**
  * Created by Taher on 2017-01-11.
  */
object SpatialOntologyDataModel extends DataModel {
  /*
  Nodes
   */
  val documents = node[Document]
  val sentences = node[Sentence]
  val tokens = node[Token]
  val relations = node[Relation]

  /*
  Edges
   */

  val documentToSentence = edge(documents, sentences)
  documentToSentence.addSensor(documentToSentenceMatching _)

  val sentenceToToken = edge(sentences, tokens)
  sentenceToToken.addSensor(sentenceToTokenGenerating _)

  val relationToToken = edge(relations, tokens)
  relationToToken.addSensor(relationToTokenMatching _)

  /*
  Properties
   */
  val wordForm = property(tokens) {
    x: Token => x.getText
  }

  val pos = property(tokens) {
    x: Token => getPos(x).mkString
  }

  val semanticRole = property(tokens) {
    x: Token => getSemanticRole(x)
  }

  val dependencyRelation = property(tokens) {
    x: Token => getDependencyRelation(x)
  }

  val subCategorization = property(tokens) {
    x: Token => getSubCategorization(x)
  }

  val spatialContext = property(tokens) {
    x: Token => ""
  }

  val spatialRole = property(tokens)
  {
    x: Token => x.getPropertyValues("Trajector").get(0)
  }
  //Question: how I can get the information that I have in the XML file tags here as the properties of the base classes.
  //This should be added to the documentation. 

  val before = property(relations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      isBefore(first, second)
  }

  val distance = property(relations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      getTokenDistance(first, second)
  }

  def getArguments(r: Relation): (Token, Token) = {
    val arguments = (relations(r) ~> relationToToken).toList
    if (arguments(0).getPropertyValues("SPATIALINDICATOR_id").contains(r.getArgumentId(1)))
      (arguments(1), arguments(0))
    else
      (arguments(0), arguments(1))
  }


}
