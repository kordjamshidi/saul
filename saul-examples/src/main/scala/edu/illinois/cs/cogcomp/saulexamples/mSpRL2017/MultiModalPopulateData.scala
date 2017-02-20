package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.{File, IOException, PrintWriter}

import edu.illinois.cs.cogcomp.saulexamples.data.CLEFImageReader
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.getCandidateRelations
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Dictionaries
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.XmlMatchings
import edu.illinois.cs.cogcomp.saulexamples.vision.{Image, Segment, SegmentRelation}
import scala.collection.JavaConversions._

import scala.io.Source

/**
  * Created by Taher on 2017-02-12.
  */
object DataProportion extends Enumeration {
  type DataProportion = Value
  val Train, ValidationTrain, ValidationTest, Test, All = Value
}

object MultiModalPopulateData {

  import DataProportion._

  val trTag = "TRAJECTOR"
  val lmTag = "LANDMARK"
  val spTag = "SPATIALINDICATOR"
  val relationTag = "RELATION"
  private val dataDir = "data/mSprl/saiapr_tc-12/"
  private lazy val CLEFDataSet = new CLEFImageReader(dataDir, false)
  private lazy val trainReader = createXmlReader(Train)
  private lazy val testReader = createXmlReader(Test)
  private lazy val validationTestReader = createXmlReader(ValidationTest)
  private lazy val validationTrainReader = createXmlReader(ValidationTrain)

  def populateData(isTrain: Boolean, proportion: DataProportion, populateNullPairs: Boolean = true) = {

    documents.populate(getDocumentList(proportion), isTrain)
    sentences.populate(getSentenceList(proportion), isTrain)
    val tokenInstances = (if (isTrain) tokens.getTrainingInstances.toList else tokens.getTestingInstances.toList)
      .filter(_.getId != dummyToken.getId)
    if (populateNullPairs) {
      tokens.populate(List(dummyToken), isTrain)
    }
    images.populate(getImageList(proportion), isTrain)
    segments.populate(getSegmentList(proportion), isTrain)
    segmentRelations.populate(getImageRelationList(proportion), isTrain)

    setTokenRoles(tokenInstances, proportion)

    val trCandidates = getTrajectorCandidates(tokenInstances, isTrain)
    trCandidates.foreach(_.addPropertyValue("TR-Candidate", "true"))

    val lmCandidates = getLandmarkCandidates(tokenInstances, isTrain)
    lmCandidates.foreach(_.addPropertyValue("LM-Candidate", "true"))

    val spCandidates = getIndicatorCandidates(tokenInstances, isTrain)
    spCandidates.foreach(_.addPropertyValue("SP-Candidate", "true"))

    val firstArgCandidates = (if (populateNullPairs) List(null) else List()) ++
      tokenInstances.filter(x => x.containsProperty("TR-Candidate") || x.containsProperty("LM-Candidate"))

    val candidateRelations = getCandidateRelations(firstArgCandidates, spCandidates)

    if (populateNullPairs) {
      // replace null arguments with dummy token
      candidateRelations.filter(_.getArgumentId(0) == null).foreach(x => {
        x.setArgumentId(0, dummyToken.getId)
        x.setArgument(0, dummyToken)
      })
    }
    setRelationTypes(candidateRelations, proportion, populateNullPairs)
    pairs.populate(candidateRelations, isTrain)
    saveCandidateList(proportion, candidateRelations)
  }

  private def saveCandidateList(proportion: DataProportion, candidateRelations: List[Relation]) = {

    def getArg(i: Int, r: Relation) = r.getArgument(i).getText.toLowerCase

    def print(r: Relation) = {
      MultiModalSpRLClassifiers.relationFeatures(FeatureSets.BaseLine)
        .map(prop => printVal(prop(r))).mkString(" | ")
    }

    def printVal(v: Any) = {
      v match {
        case x: List[_] => x.mkString(", ")
        case _ => v.toString
      }
    }

    val writer = new PrintWriter(s"data/mSprl/results/RoleCandidates-${proportion}.txt")
    candidateRelations.foreach(x =>
      writer.println(s"(${getArg(0, x)}, ${getArg(1, x)})[${print(x)}] -> ${x.getProperty("RelationType")}")
    )
    writer.close()
  }

  private def setRelationTypes(candidateRelations: List[Relation], proportion: DataProportion, populateNullPairs: Boolean): Unit = {

    val goldTrajectorRelations = getGoldTrajectorPairs(proportion, populateNullPairs)
    val goldLandmarkRelations = getGoldLandmarkPairs(proportion, populateNullPairs)

    candidateRelations.foreach(_.setProperty("RelationType", "None"))

    setLmSpRelationTypes(goldLandmarkRelations, candidateRelations)
    setTrSpRelationTypes(goldTrajectorRelations, candidateRelations)

    reportRelationStats(candidateRelations, goldTrajectorRelations, goldLandmarkRelations)
  }

  private def setTrSpRelationTypes(goldTrajectorRelations: List[Relation], candidateRelations: List[Relation]): Unit = {

    goldTrajectorRelations.foreach(r => {
      val c = candidateRelations
        .find(x =>
          x.getArgument(0).getPropertyValues(s"${trTag}_id").contains(r.getArgumentId(0)) &&
            x.getArgument(1).getPropertyValues(s"${spTag}_id").contains(r.getArgumentId(1))
        )

      if (c.nonEmpty) {
        if (c.get.getProperty("RelationType") == "TR-SP") {
          println(s"warning: candidate already marked as TR-SP via ${c.get.getId}. duplicate relation: ${r.getId}")
        } else {
          if (c.get.getProperty("RelationType") == "LM-SP") {
            println(s"warning: overriding LM-SP relation ${c.get.getId} by TR-SP relation: ${r.getId}")
          }
          c.get.setProperty("RelationType", "TR-SP")
          c.get.setId(r.getId)
        }
      } else {
        println(s"cannot find TR-SP candidate relation for ${r.getId}")
      }
    })
  }

  private def setLmSpRelationTypes(goldLandmarkRelations: List[Relation], candidateRelations: List[Relation]): Unit = {

    goldLandmarkRelations.foreach(r => {
      val c = candidateRelations
        .find(x =>
          x.getArgument(0).getPropertyValues(s"${lmTag}_id").contains(r.getArgumentId(0)) &&
            x.getArgument(1).getPropertyValues(s"${spTag}_id").contains(r.getArgumentId(1))
        )

      if (c.nonEmpty) {
        if (c.get.getProperty("RelationType") == "LM-SP") {
          println(s"warning: candidate already marked as LM-SP via ${c.get.getId}. duplicate relation: ${r.getId}")
        } else {
          if (c.get.getProperty("RelationType") == "TR-SP") {
            println(s"warning: overriding TR-SP relation ${c.get.getId} by LM-SP relation: ${r.getId}")
          }
          c.get.setProperty("RelationType", "LM-SP")
          c.get.setId(r.getId)
        }
      } else {
        println(s"cannot find LM-SP candidate relation for ${r.getId}")
      }
    })
  }

  def getIndicatorCandidates(tokenInstances: List[Token], isTrain: Boolean): List[Token] = {

    val spLex = getSpatialIndicatorLexicon(tokenInstances, 0, false)
    val spPosTagLex = List("IN", "TO")
    val spCandidates = tokenInstances
      .filter(x => spLex.contains(x.getText.toLowerCase) ||
        spPosTagLex.contains(pos(x)) ||
        Dictionaries.isPreposition(x.getText))
    reportRoleStats(tokenInstances, spCandidates, spTag)
    spCandidates
  }

  def getLandmarkCandidates(tokenInstances: List[Token], isTrain: Boolean): List[Token] = {

    val lmPosTagLex = List("PRP", "NN", "PRP$", "JJ", "NNS", "CD")
    //getRolePosTagLexicon(tokenInstances, lmTag, 5, isTrain)
    val lmCandidates = tokenInstances.filter(x => lmPosTagLex.contains(pos(x)))
    reportRoleStats(tokenInstances, lmCandidates, lmTag)
    lmCandidates
  }

  def getTrajectorCandidates(tokenInstances: List[Token], isTrain: Boolean): List[Token] = {

    val trPosTagLex = List("NN", "JJR", "PRP$", "VBG", "JJ", "NNP", "NNS", "CD", "VBN", "VBD")
    //getRolePosTagLexicon(tokenInstances, trTag, 5, isTrain)
    val trCandidates = tokenInstances.filter(x => trPosTagLex.contains(pos(x)))
    reportRoleStats(tokenInstances, trCandidates, trTag)
    trCandidates
  }

  private def getGoldLandmarkPairs(proportion: DataProportion, populateNullPairs: Boolean): List[Relation] = {

    // create pairs which first argument is landmark and second is indicator, and remove duplicates
    val nullLandmarkIds = getTags(lmTag, proportion).filter(_.getStart == -1).map(_.getId)
    val relations = getRelations("landmark_id", "spatial_indicator_id", proportion)
      .groupBy(x => x.getArgumentId(0) + "_" + x.getArgumentId(1))
      .map { case (_, list) => list.head }
      .toList
    if (populateNullPairs) {
      relations.foreach(r => if (nullLandmarkIds.contains(r.getArgumentId(0))) r.setArgumentId(0, dummyToken.getId))
      relations
    } else {
      relations.filterNot(r => nullLandmarkIds.contains(r.getArgumentId(0)))
    }
  }

  private def getGoldTrajectorPairs(proportion: DataProportion, populateNullPairs: Boolean): List[Relation] = {

    // create pairs which first argument is trajector and second is indicator, and remove duplicates
    val nullTrajectorIds = getTags(trTag, proportion).filter(_.getStart == -1).map(_.getId)
    val relations = getRelations("trajector_id", "spatial_indicator_id", proportion)
      .groupBy(x => x.getArgumentId(0) + "_" + x.getArgumentId(1))
      .map { case (_, list) => list.head }
      .toList
    if (populateNullPairs) {
      relations.foreach(r => if (nullTrajectorIds.contains(r.getArgumentId(0))) r.setArgumentId(0, dummyToken.getId))
      relations
    } else {
      relations.filterNot(r => nullTrajectorIds.contains(r.getArgumentId(0)))
    }
  }

  private def getRelations(firstArgId: String, secondArgId: String, proportion: DataProportion): List[Relation] = {
    proportion match {
      case All => getXmlReader(Train).getRelations(relationTag, firstArgId, secondArgId).toList ++
        getXmlReader(Test).getRelations(relationTag, firstArgId, secondArgId)

      case x => getXmlReader(x).getRelations(relationTag, firstArgId, secondArgId).toList
    }
  }

  private def getTags(tag: String, proportion: DataProportion): List[NlpBaseElement] = {

    proportion match {
      case All => getXmlReader(Train).getTagAsNlpBaseElement(tag).toList ++
        getXmlReader(Test).getTagAsNlpBaseElement(tag)

      case x => getXmlReader(x).getTagAsNlpBaseElement(tag).toList
    }
  }

  def setTokenRoles(tokenInstances: List[Token], proportion: DataProportion): Unit = {

    def setTokenRole(tokenInstances: List[Token], proportion: DataProportion, tag: String) = {
      proportion match {
        case All =>
          getXmlReader(Train).addPropertiesFromTag(tag, tokenInstances, XmlMatchings.xmlHeadwordMatching)
          getXmlReader(Test).addPropertiesFromTag(tag, tokenInstances, XmlMatchings.xmlHeadwordMatching)

        case x =>
          getXmlReader(x).addPropertiesFromTag(tag, tokenInstances, XmlMatchings.xmlHeadwordMatching)
      }
    }

    setTokenRole(tokenInstances, proportion, trTag)
    setTokenRole(tokenInstances, proportion, lmTag)
    setTokenRole(tokenInstances, proportion, spTag)
  }

  def getSentenceList(proportion: DataProportion): List[Sentence] = {

    proportion match {
      case All => getXmlReader(Train).getSentences().toList ++ getXmlReader(Test).getSentences()
      case x => getXmlReader(x).getSentences().toList
    }
  }

  def getDocumentList(proportion: DataProportion): List[Document] = {

    proportion match {
      case All => getXmlReader(Train).getDocuments().toList ++ getXmlReader(Test).getDocuments()
      case x => getXmlReader(x).getDocuments().toList
    }
  }

  def getImageRelationList(proportion: DataProportion): List[SegmentRelation] = {

    proportion match {
      case Train | ValidationTest | ValidationTrain => CLEFDataSet.trainingRelations.toList
      case Test => CLEFDataSet.testRelations.toList
      case All => CLEFDataSet.trainingRelations.toList ++ CLEFDataSet.testRelations
    }
  }

  def getSegmentList(proportion: DataProportion): List[Segment] = {

    proportion match {
      case Train | ValidationTest | ValidationTrain => CLEFDataSet.trainingSegments.toList
      case Test => CLEFDataSet.testSegments.toList
      case All => CLEFDataSet.trainingSegments.toList ++ CLEFDataSet.testSegments
    }
  }

  def getImageList(proportion: DataProportion): List[Image] = {

    proportion match {
      case Train | ValidationTest | ValidationTrain => CLEFDataSet.trainingImages.toList
      case Test => CLEFDataSet.testImages.toList
      case All => CLEFDataSet.trainingImages.toList ++ CLEFDataSet.testImages
    }
  }

  private def reportRoleStats(tokenInstances: List[Token], candidates: List[Token], tagName: String): Unit = {

    val instances = tokenInstances.filter(_.containsProperty(s"${tagName}_id"))
    val actual = instances.map(_.getPropertyValues(s"${tagName}_id").size()).sum
    val missingTokens = instances.toSet.diff(candidates.toSet).toList.map(_.getText.toLowerCase())
    val missing = actual - candidates.map(_.getPropertyValues(s"${tagName}_id").size()).sum

    println(s"Candidate ${tagName}: ${candidates.size}")
    println(s"Actual ${tagName}: $actual")
    println(s"Missing ${tagName} in the candidates: $missing (${missingTokens.mkString(", ")})")
  }

  private def reportRelationStats(candidateRelations: List[Relation], goldTrajectorRelations: List[Relation],
                                  goldLandmarkRelations: List[Relation]): Unit = {

    val missedTrSp = goldTrajectorRelations.size - candidateRelations.count(_.getProperty("RelationType") == "TR-SP")
    println(s"actual TR-SP: ${goldTrajectorRelations.size}")
    println(s"Missing TR-SP in the candidates: $missedTrSp")
    val missingTrRelations = goldTrajectorRelations
      .filterNot(r => candidateRelations.exists(x => x.getProperty("RelationType") == "TR-SP" && x.getId == r.getId))
      .map(_.getId)
    println(s"missing relations from TR-SP: (${missingTrRelations.mkString(", ")})")

    val missedLmSp = goldLandmarkRelations.size - candidateRelations.count(_.getProperty("RelationType") == "LM-SP")
    println(s"actual LM-SP: ${goldLandmarkRelations.size}")
    println(s"Missing LM-SP in the candidates: $missedLmSp")
    val missingLmRelations = goldLandmarkRelations
      .filterNot(r => candidateRelations.exists(x => x.getProperty("RelationType") == "LM-SP" && x.getId == r.getId))
      .map(_.getId)
    println(s"missing relations from LM-SP: (${missingLmRelations.mkString(", ")})")
  }

  private def getRolePosTagLexicon(tokenInstances: List[Token], tagName: String, minFreq: Int, generate: Boolean): List[String] = {

    val lexFile = new File(s"data/mSprl/${tagName.toLowerCase}PosTag.lex")
    if (generate) {
      val posTagLex = tokenInstances.filter(x => x.containsProperty(s"${tagName.toUpperCase}_id"))
        .map(x => pos(x)).groupBy(x => x).map { case (key, list) => (key, list.size) }.filter(_._2 >= minFreq)
        .keys.toList
      val writer = new PrintWriter(lexFile)
      posTagLex.foreach(p => writer.println(p))
      writer.close()
      posTagLex
    } else {
      if (!lexFile.exists())
        throw new IOException(s"cannot find ${lexFile.getAbsolutePath} file")
      Source.fromFile(lexFile).getLines().toList
    }
  }

  private def getSpatialIndicatorLexicon(tokenInstances: List[Token], minFreq: Int, generate: Boolean): List[String] = {

    val lexFile = new File("data/mSprl/spatialIndicator.lex")
    if (generate) {
      val sps = tokenInstances.filter(_.containsProperty("SPATIALINDICATOR_id"))
        .groupBy(_.getText.toLowerCase).map { case (key, list) => (key, list.size, list) }.filter(_._2 >= minFreq)
      val prepositionLex = sps.map(_._1).toList
      val writer = new PrintWriter(lexFile)
      prepositionLex.foreach(p => writer.println(p))
      writer.close()
      prepositionLex
    } else {
      if (!lexFile.exists())
        throw new IOException(s"cannot find ${lexFile.getAbsolutePath} file")
      Source.fromFile(lexFile).getLines().toList
    }
  }

  def getXmlReader(proportion: DataProportion): NlpXmlReader = {
    proportion match {
      case Train => trainReader
      case Test => testReader
      case ValidationTrain => validationTrainReader
      case ValidationTest => validationTestReader
    }
  }

  private def createXmlReader(proportion: DataProportion): NlpXmlReader = {
    val path = dataDir + "sprl2017_" + (proportion match {
      case Train => "train.xml"
      case ValidationTrain => "validation_train.xml"
      case ValidationTest => "validation_test.xml"
      case Test => "gold.xml"
    })
    val reader = new NlpXmlReader(path, "SCENE", "SENTENCE", null, null)
    reader.setIdUsingAnotherProperty("SCENE", "DOCNO")
    reader
  }

}

