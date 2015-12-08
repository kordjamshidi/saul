package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.Conll04_ReaderNew
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationBasicDataModel._

import scala.collection.JavaConversions._

object IndependentTraining extends App {

  def populate_ER_graph = {
    val reader = new Conll04_ReaderNew("./data/EntityMentionRelation/conll04.corp", "Token")
    val trainSentences = reader.sentences.toList
    val trainTokens = trainSentences.flatMap(_.sentTokens).slice(1, 10)
    val trainRelations = reader.relations.toList

    sentences populate trainSentences
    tokens populate trainTokens
    pairs populate trainRelations

  }

  populate_ER_graph
  val it = 2
  println("Indepent Training with iteration " + it)
  PersonClassifier.learn(it)
  PersonClassifier.test(tokens.getAllInstances)
  OrgClassifier.learn(it)
  OrgClassifier.test(tokens.getAllInstances)

  LocationClassifier.learn(it)
  LocationClassifier.test(tokens.getAllInstances)
  WorkForClassifier.learn(it)
  WorkForClassifier.test(pairs.getAllInstances)
  LivesInClassifier.learn(it)
  LivesInClassifier.test(pairs.getAllInstances)

  LocationClassifier.learn(it)
  LocationClassifier.test(tokens.getAllInstances)

}
