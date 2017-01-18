package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import SpRLNewSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
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
  val phrases = node[Phrase]
  val relations = node[Relation]

  /*
  Edges
   */

  val documentToSentence = edge(documents, sentences)
  documentToSentence.addSensor(documentToSentenceMatching _)

  val sentenceToPhrase = edge(sentences, phrases)
  sentenceToPhrase.addSensor(sentenceToPhraseGenerating _)

  val phraseToToken = edge(phrases, tokens)
  phraseToToken.addSensor(phraseToTokenGenerating _)

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

  val dependencyRelation = property(tokens){
    x: Token => getDependencyRelation(x)
  }

  val subCategorization = property(tokens){
    x: Token => getSubCategorization(x)
  }

  val spatialContext = property(tokens){
    x: Token => ""
  }

  val before = property(relations){
    x: Relation => ""
  }

  val distance = property(relations){
    x: Relation => ""
  }




}
