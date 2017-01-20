/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.data.CLEFImageReader
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.mSpRL2017DataModel._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.ImageClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.XmlMatchings
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader

import scala.collection.JavaConversions._

object mSpRL2017App extends App {

  val CLEFDataset = new CLEFImageReader("data/mSprl/saiapr_tc-12")

  val imageListTrain = CLEFDataset.trainingImages
  val segementListTrain = CLEFDataset.trainingSegments
  val relationListTrain = CLEFDataset.trainingRelations

  images.populate(imageListTrain)
  segments.populate(segementListTrain)
  relation.populate(relationListTrain)


  val imageListTest = CLEFDataset.testImages
  val segementListTest = CLEFDataset.testSegments
  val relationListTest = CLEFDataset.testRelations

  images.populate(imageListTest, false)
  segments.populate(segementListTest, false)
  relation.populate(relationListTest, false)

  ImageSVMClassifier.learn(5)
  ImageSVMClassifier.test(segementListTest)

  ImageClassifierWeka.learn(5)
  ImageClassifierWeka.test(segementListTest)

}

object SpatialOntologyApp extends App {

  import SpatialOntologyDataModel._

  val reader = new NlpXmlReader("./saul-examples/src/test/resources/SpRL/2017/test.xml", "SCENE", "SENTENCE", null, null)
  val documentList = reader.getDocuments()
  val sentenceList = reader.getSentences()

  documents.populate(documentList)
  sentences.populate(sentenceList)

  reader.addPropertiesFromTag("TRAJECTOR", tokens().toList, XmlMatchings.xmlHeadwordMatching)
  reader.addPropertiesFromTag("LANDMARK", tokens().toList, XmlMatchings.xmlHeadwordMatching)
  reader.addPropertiesFromTag("SPATIALINDICATOR", tokens().toList, XmlMatchings.xmlHeadwordMatching)

  val trRelationList = reader.getRelations("RELATION", "trajector_id", "spatial_indicator_id")
  trRelationList.foreach(_.setProperty("TR_RELATION", "true"))
  val lmRelationList = reader.getRelations("RELATION", "landmark_id", "spatial_indicator_id")
  lmRelationList.foreach(_.setProperty("LM_RELATION", "true"))
  relations.populate(trRelationList ++ lmRelationList)

  val trCandidates = tokens().filter(x => getPos(x).contains("NN") && !x.containsProperty("TRAJECTOR_id")).toList
  val spCandidates = tokens().filter(x => getPos(x).contains("IN") && !x.containsProperty("SPATIALINDICATOR_id")).toList
  val lmCandidates = tokens().filter(x => getPos(x).contains("NN") && !x.containsProperty("LANDMARK_id")).toList
  val trCandidateRelations = getCandidateRelations[Token](trCandidates, spCandidates)
  val lmCandidateRelations = getCandidateRelations[Token](lmCandidates, spCandidates)
  relations.populate(trCandidateRelations ++ lmCandidateRelations)




}
