/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.data.CLEFImageReader
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.XmlMatchings
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Dictionaries

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

object textApp extends App with Logging {

  import MultiModalSpRLDataModel._

  val isTrain = true
  val path = if (isTrain) "data/SpRL/2017/clef/train/sprl2017_train.xml" else "data/SpRL/2017/clef/gold/sprl2017_gold.xml"
  val reader = new NlpXmlReader(path, "SCENE", "SENTENCE", null, null)
  reader.setIdUsingAnotherProperty("SCENE", "DOCNO")
  val documentList = reader.getDocuments()
  val sentenceList = reader.getSentences()

  documents.populate(documentList, isTrain)
  sentences.populate(sentenceList, isTrain)

  reader.addPropertiesFromTag("TRAJECTOR", tokens().toList, XmlMatchings.xmlHeadwordMatching)
  reader.addPropertiesFromTag("LANDMARK", tokens().toList, XmlMatchings.xmlHeadwordMatching)
  reader.addPropertiesFromTag("SPATIALINDICATOR", tokens().toList, XmlMatchings.xmlHeadwordMatching)

  SpatialRoleClassifier.modelDir = "models/mSpRL/spatialRole/"
  if (isTrain) {
    logger.info("training started ...")
    SpatialRoleClassifier.learn(100)
    SpatialRoleClassifier.save()
  }
  else {
    logger.info("testing started ...")
    SpatialRoleClassifier.load()
    SpatialRoleClassifier.test()
  }
}

object combinedApp extends App with Logging {

  val classifier = TrajectorRoleClassifier
  runClassifier(true)
  runClassifier(false)

  def runClassifier(isTrain: Boolean) = {
    combinedPairApp.populateData(isTrain)

    classifier.modelDir = "models/mSpRL/spatialRole/"
    if (isTrain) {
      println("training started ...")
      classifier.learn(50)
      classifier.save()
    }
    else {
      println("testing started ...")
      classifier.load()
      classifier.test()
    }
  }
}

object combinedPairApp extends App with Logging {

  import MultiModalSpRLDataModel._

  val isTrajector = false
  val classifier = TrajectorPairClassifier
  runClassifier(true, isTrajector)
  runClassifier(false, isTrajector)

  def runClassifier(isTrain: Boolean, isTrajector: Boolean) = {
    val missingRelations = populateData(isTrain, true, isTrajector)
    println("Missing relations count: " + missingRelations)

    classifier.modelDir = "models/mSpRL/spatialRole/"
    if (isTrain) {
      println("training started ...")
      classifier.learn(50)
      classifier.save()
    }
    else {
      println("testing started ...")
      classifier.load()
      classifier.test()
    }
  }

  lazy val CLEFDataSet = new CLEFImageReader("data/mSprl/saiapr_tc-12", false)

  def populateData(isTrain: Boolean, populateRelations: Boolean = false, isTrajectorPair: Boolean = true) = {
    val path = if (isTrain) "data/SpRL/2017/clef/train/sprl2017_train.xml" else "data/SpRL/2017/clef/gold/sprl2017_gold.xml"

    val reader = new NlpXmlReader(path, "SCENE", "SENTENCE", null, null)
    reader.setIdUsingAnotherProperty("SCENE", "DOCNO")

    val documentList = reader.getDocuments()
    val sentenceList = reader.getSentences()
    val imageList = if (isTrain) CLEFDataSet.trainingImages else CLEFDataSet.testImages
    val segmentList = if (isTrain) CLEFDataSet.trainingSegments else CLEFDataSet.testSegments
    val relationList = if (isTrain) CLEFDataSet.trainingRelations else CLEFDataSet.testRelations

    documents.populate(documentList, isTrain)
    sentences.populate(sentenceList, isTrain)
    images.populate(imageList, isTrain)
    segments.populate(segmentList, isTrain)
    segmentRelations.populate(relationList, isTrain)

    reader.addPropertiesFromTag("TRAJECTOR", tokens().toList, XmlMatchings.xmlHeadwordMatching)
    reader.addPropertiesFromTag("LANDMARK", tokens().toList, XmlMatchings.xmlHeadwordMatching)
    reader.addPropertiesFromTag("SPATIALINDICATOR", tokens().toList, XmlMatchings.xmlHeadwordMatching)

    if (populateRelations) {

      // read TRAJECTOR/LANDMARK elements as document and find empty ones: elements with `start` == -1
      reader.setDocumentTagName(getFirstArgumentTagName(isTrajectorPair))
      val nullArgumentIds = reader.getDocuments().filter(_.getStart == -1).map(_.getId)

      val goldRelations = reader.getRelations("RELATION", getFirstArgumentIdName(isTrajectorPair), "spatial_indicator_id")
        .filter(x => !nullArgumentIds.contains(x.getArgumentId(0)))
        .toList

      val firstArgCandidates = tokens().filter(x => getPos(x).head.contains("NN") || getPos(x).head.contains("PRP")).toList
      val spCandidates = tokens().filter(x => getPos(x).head.contains("IN") || Dictionaries.isPreposition(x.getText)).toList

      val candidateRelations = getCandidateRelations(firstArgCandidates, spCandidates)

      if (isTrajectorPair) {
        candidateRelations
          .foreach(r => r.setProperty("RelationType", isGold(goldRelations, r, "TRAJECTOR_id") match {
            case true => "TR_SP"
            case false => "None"
          }))
      }
      else {
        candidateRelations
          .foreach(r => r.setProperty("RelationType", isGold(goldRelations, r, "LANDMARK_id") match {
            case true => "LM_SP"
            case false => "None"
          }))
      }
      textRelations.populate(candidateRelations, isTrain)
      goldRelations.size - candidateRelations.count(_.getProperty("RelationType") != "None")
    }
    else {
      0
    }
  }

  private def getFirstArgumentIdName(isTrajectorPair: Boolean) = {
    if (isTrajectorPair) "trajector_id" else "landmark_id"
  }

  private def getFirstArgumentTagName(isTrajectorPair: Boolean) = {
    if (isTrajectorPair) "TRAJECOTR" else "LANDMARK"
  }

  def isGold(goldRelations: List[Relation], r: Relation, firstArgName: String): Boolean = {
    goldRelations.exists(x =>
      r.getArgument(0).getPropertyValues(firstArgName).contains(x.getArgumentId(0)) &&
        r.getArgument(1).getPropertyValues("SPATIALINDICATOR_id").contains(x.getArgumentId(1))
    )
  }

}


