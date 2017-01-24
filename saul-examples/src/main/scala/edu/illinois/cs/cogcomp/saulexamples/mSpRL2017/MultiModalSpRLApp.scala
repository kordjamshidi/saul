/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.data.CLEFImageReader
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.XmlMatchings
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._

import scala.collection.JavaConversions._

object imageApp extends App {

  val readFullData = false
  val CLEFDataset = new CLEFImageReader("data/mSprl/saiapr_tc-12", readFullData)

  val imageListTrain = CLEFDataset.trainingImages
  val segmentListTrain = CLEFDataset.trainingSegments
  val relationListTrain = CLEFDataset.trainingRelations

  images.populate(imageListTrain)
  segments.populate(segmentListTrain)
  segmentRelations.populate(relationListTrain)


  val imageListTest = CLEFDataset.testImages
  val segementListTest = CLEFDataset.testSegments
  val relationListTest = CLEFDataset.testRelations

  images.populate(imageListTest, false)
  segments.populate(segementListTest, false)
  segmentRelations.populate(relationListTest, false)

  ImageSVMClassifier.learn(5)
  ImageSVMClassifier.test(segementListTest)

  ImageClassifierWeka.learn(5)
  ImageClassifierWeka.test(segementListTest)
}

object textApp extends App {

  import MultiModalSpRLDataModel._

  val reader = new NlpXmlReader("./saul-examples/src/test/resources/SpRL/2017/test.xml", "SCENE", "SENTENCE", null, null)
  val documentList = reader.getDocuments()
  val sentenceList = reader.getSentences()

  documentList.foreach(x => println(x.getPropertyFirstValue("IMAGE")))

  documents.populate(documentList)
  sentences.populate(sentenceList)

  reader.addPropertiesFromTag("TRAJECTOR", tokens().toList, XmlMatchings.xmlHeadwordMatching)
  reader.addPropertiesFromTag("LANDMARK", tokens().toList, XmlMatchings.xmlHeadwordMatching)
  reader.addPropertiesFromTag("SPATIALINDICATOR", tokens().toList, XmlMatchings.xmlHeadwordMatching)

  val trRelationList = reader.getRelations("RELATION", "trajector_id", "spatial_indicator_id")
  trRelationList.foreach(_.setProperty("TR_RELATION", "true"))
  val lmRelationList = reader.getRelations("RELATION", "landmark_id", "spatial_indicator_id")
  lmRelationList.foreach(_.setProperty("LM_RELATION", "true"))
  textRelations.populate(trRelationList ++ lmRelationList)

  val trCandidates = tokens().filter(x => getPos(x).contains("NN") && !x.containsProperty("TRAJECTOR_id")).toList
  val spCandidates = tokens().filter(x => getPos(x).contains("IN") && !x.containsProperty("SPATIALINDICATOR_id")).toList
  val lmCandidates = tokens().filter(x => getPos(x).contains("NN") && !x.containsProperty("LANDMARK_id")).toList
  val trCandidateRelations = getCandidateRelations[Token](trCandidates, spCandidates)
  val lmCandidateRelations = getCandidateRelations[Token](lmCandidates, spCandidates)
  textRelations.populate(trCandidateRelations ++ lmCandidateRelations)
}
