/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io._

import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.data.CLEFImageReader
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalConstrainedClassifiers.{ LMPairConstraintClassifier, TRPairConstraintClassifier }
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.XmlMatchings
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Dictionaries
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval.{ RelationEval, RelationsEvalDocument, SpRLEvaluator }
import edu.illinois.cs.cogcomp.saulexamples.vision.{ Image, Segment, SegmentRelation }

import scala.collection.JavaConversions._
import scala.io.Source

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

object combinedPairApp extends App with Logging {

  object DataProportion extends Enumeration {
    type DataProportion = Value
    val Train, Test, Both = Value
  }

  val trTag = "TRAJECTOR"
  val lmTag = "LANDMARK"
  val spTag = "SPATIALINDICATOR"
  val relationTag = "RELATION"
  val classifiers = List(
    TrajectorRoleClassifier,
    LandmarkRoleClassifier,
    IndicatorRoleClassifier,
    TrajectorPairClassifier,
    LandmarkPairClassifier
  )

  import MultiModalSpRLDataModel._
  import DataProportion._

  runClassifiers(true, Train)
  runClassifiers(false, Test)

  private def runClassifiers(isTrain: Boolean, proportion: DataProportion) = {
    populateData(isTrain, proportion)

    classifiers.foreach(_.modelDir = "models/mSpRL/spatialRole/")

    if (isTrain) {
      println("training started ...")

      classifiers.foreach(classifier => {
        classifier.learn(50)
        classifier.save()
      })
    } else {

      println("testing started ...")

      classifiers.foreach(classifier => {
        classifier.load()
        classifier.test()
      })
      testTriplet(isTrain, proportion)
      TRPairConstraintClassifier.test()
      LMPairConstraintClassifier.test()

    }
  }

  def populateData(isTrain: Boolean, proportion: DataProportion, populateImageData: Boolean = true) = {

    documents.populate(getDocumentList(proportion), isTrain)
    sentences.populate(getSentenceList(proportion), isTrain)
    val tokenInstances = if (isTrain) tokens.getTrainingInstances.toList else tokens.getTestingInstances.toList

    if (populateImageData) {
      images.populate(getImageList(proportion), isTrain)
      segments.populate(getSegmentList(proportion), isTrain)
      segmentRelations.populate(getImageRelationList(proportion), isTrain)
    }
    setTokenRoles(tokenInstances, proportion)

    val trCandidates = getTrajectorCandidates(isTrain, tokenInstances)
    trCandidates.foreach(_.addPropertyValue("TR-Candidate", "true"))

    val lmCandidates = getLandmarkCandidates(isTrain, tokenInstances)
    lmCandidates.foreach(_.addPropertyValue("LM-Candidate", "true"))

    val firstArgCandidates = trCandidates.toSet.union(lmCandidates.toSet).toList

    val spCandidates = getIndicatorCandidates(isTrain, tokenInstances)
    spCandidates.foreach(_.addPropertyValue("SP-Candidate", "true"))

    val candidateRelations = getCandidateRelations(firstArgCandidates, spCandidates)

    setRelationTypes(candidateRelations, proportion)

    pairs.populate(candidateRelations, isTrain)
  }

  private def testTriplet(isTrain: Boolean, proportion: DataProportion): Unit = {

    val tokenInstances = if (isTrain) tokens.getTrainingInstances else tokens.getTestingInstances
    val indicators = tokenInstances.filter(t => IndicatorRoleClassifier(t) == "Indicator").toList
      .sortBy(x => x.getSentence.getStart + x.getStart)

    val relations = indicators.flatMap(sp => {
      val pairs = tokens(sp) <~ relationToSecondArgument
      val trajectorPairs = pairs.filter(r => TrajectorPairClassifier(r) == "TR-SP") ~> relationToFirstArgument
      if (trajectorPairs.nonEmpty) {
        val landmarkPairs = pairs.filter(r => LandmarkPairClassifier(r) == "LM-SP") ~> relationToFirstArgument
        if (landmarkPairs.nonEmpty) {
          trajectorPairs.flatMap(tr => landmarkPairs.map(lm => getRelationEval(Some(tr), Some(sp), Some(lm)))).toList
        } else {
          List() //trajectorPairs.map(tr => getRelationEval(Some(tr), Some(sp), None)).toList
        }
      } else {
        List()
      }
    })
    val predictedRelations = new RelationsEvalDocument(relations)
    val actualRelations = new RelationsEvalDocument(getActualRelationEvalsTokenBased(isTrain))
    val evaluator = new SpRLEvaluator()
    val results = evaluator.evaluateRelations(actualRelations, predictedRelations)
    evaluator.printEvaluation(results)
  }

  private def getActualRelationEvalsPhraseBased(isTrain: Boolean): List[RelationEval] = {
    val reader = getXmlReader(isTrain)
    val relations = reader.getRelations("RELATION", "trajector_id", "spatial_indicator_id", "landmark_id")

    reader.setPhraseTagName("TRAJECTOR")
    val trajectors = reader.getPhrases().map(x => x.getId -> x).toMap

    reader.setPhraseTagName("LANDMARK")
    val landmarks = reader.getPhrases().map(x => x.getId -> x).toMap

    reader.setPhraseTagName("SPATIALINDICATOR")
    val indicators = reader.getPhrases().map(x => x.getId -> x).toMap

    relations.map(r => {
      val tr = trajectors(r.getArgumentId(0))
      val sp = indicators(r.getArgumentId(1))
      val lm = landmarks(r.getArgumentId(2))
      val (trStart: Int, trEnd: Int) = getSpan(tr)
      val (spStart: Int, spEnd: Int) = getSpan(sp)
      val (lmStart: Int, lmEnd: Int) = getSpan(lm)
      new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd)
    }).toList
  }

  private def getActualRelationEvalsTokenBased(isTrain: Boolean): List[RelationEval] = {
    val reader = getXmlReader(isTrain)
    val relations = reader.getRelations("RELATION", "trajector_id", "spatial_indicator_id", "landmark_id")

    reader.setPhraseTagName("TRAJECTOR")
    val trajectors = reader.getPhrases().map(x => x.getId -> x).toMap

    reader.setPhraseTagName("LANDMARK")
    val landmarks = reader.getPhrases().map(x => x.getId -> x).toMap

    reader.setPhraseTagName("SPATIALINDICATOR")
    val indicators = reader.getPhrases().map(x => x.getId -> x).toMap

    relations.map(r => {
      val tr = trajectors(r.getArgumentId(0))
      val sp = indicators(r.getArgumentId(1))
      val lm = landmarks(r.getArgumentId(2))
      val (trStart: Int, trEnd: Int) = getHeadSpan(tr)
      val (spStart: Int, spEnd: Int) = getHeadSpan(sp)
      val (lmStart: Int, lmEnd: Int) = getHeadSpan(lm)
      new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd)
    }).toList
  }

  private def getRelationEval(tr: Option[Token], sp: Option[Token], lm: Option[Token]): RelationEval = {
    val offset = sp.get.getSentence.getStart
    val lmStart = if (lm.nonEmpty) offset + lm.get.getStart else -1
    val lmEnd = if (lm.nonEmpty) offset + lm.get.getEnd else -1
    val trStart = if (tr.nonEmpty) offset + tr.get.getStart else -1
    val trEnd = if (tr.nonEmpty) offset + tr.get.getEnd else -1
    val spStart = offset + sp.get.getStart
    val spEnd = offset + sp.get.getEnd
    new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd)
  }

  private def setRelationTypes(candidateRelations: List[Relation], proportion: DataProportion): Unit = {

    val goldTrajectorRelations = getGoldTrajectorPairs(proportion)
    val goldLandmarkRelations = getGoldLandmarkPairs(proportion)

    candidateRelations.foreach(_.setProperty("RelationType", "None"))

    setLmSpRelationTypes(goldLandmarkRelations, candidateRelations)
    setTrSpRelationTypes(goldTrajectorRelations, candidateRelations)

    reportRelationStats(candidateRelations, goldTrajectorRelations, goldLandmarkRelations)
  }

  private def setTrSpRelationTypes(goldTrajectorRelations: List[Relation], candidateRelations: List[Relation]): Unit = {

    goldTrajectorRelations.foreach(r => {
      val c = candidateRelations
        .find(x => x.getArgument(0).getPropertyValues(s"${trTag}_id").contains(r.getArgumentId(0)) &&
          x.getArgument(1).getPropertyValues(s"${spTag}_id").contains(r.getArgumentId(1)))

      if (c.nonEmpty) {
        if (c.get.getProperty("RelationType") == "TR-SP") {
          println(s"warning: candidate already marked as TR-SP via ${c.get.getId}. duplicate relation: ${r.getId}")
        } else {
          if (c.get.getProperty("RelationType") == "TR-SP") {
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
        .find(x => x.getArgument(0).getPropertyValues(s"${lmTag}_id").contains(r.getArgumentId(0)) &&
          x.getArgument(1).getPropertyValues(s"${spTag}_id").contains(r.getArgumentId(1)))

      if (c.nonEmpty) {
        if (c.get.getProperty("RelationType") == "LM-SP") {
          println(s"warning: candidate already marked as LM-SP via ${c.get.getId}. duplicate relation: ${r.getId}")
        } else {
          c.get.setProperty("RelationType", "LM-SP")
          c.get.setId(r.getId)
        }
      } else {
        println(s"cannot find LM-SP candidate relation for ${r.getId}")
      }
    })
  }

  private def getIndicatorCandidates(isTrain: Boolean, tokenInstances: List[Token]): List[Token] = {

    val spLex = List("behind", "standing", "underneath", "in", "below", "outside", "before", "lying", "walking",
      "above", "to", "around", "at", "through", "distant", "over", "on", "leaning", "with", "from", "next", "leading",
      "under", "between", "sitting", "along", "inside", "of", "right", "attached", "left", "lined", "close",
      "supported", "side", "goes", "surrounded")
    //getSpatialIndicatorLexicon(tokenInstances, 2, isTrain)
    val spPosTagLex = List("IN", "VBG", "JJ", "TO")
    // getRolePosTagLexicon(tokenInstances, spTag, 10, isTrain)
    val spCandidates = tokenInstances
      .filter(x => spLex.contains(x.getText.toLowerCase) ||
        spPosTagLex.contains(pos(x)) ||
        Dictionaries.isPreposition(x.getText))
    reportRoleStats(tokenInstances, spCandidates, spTag)
    spCandidates
  }

  private def getLandmarkCandidates(isTrain: Boolean, tokenInstances: List[Token]): List[Token] = {

    val lmPosTagLex = List("PRP", "NN", "PRP$", "JJ", "NNS", "CD")
    //getRolePosTagLexicon(tokenInstances, lmTag, 5, isTrain)
    val lmCandidates = tokenInstances.filter(x => lmPosTagLex.contains(pos(x)))
    reportRoleStats(tokenInstances, lmCandidates, lmTag)
    lmCandidates
  }

  private def getTrajectorCandidates(isTrain: Boolean, tokenInstances: List[Token]): List[Token] = {

    val trPosTagLex = List("NN", "JJR", "PRP$", "VBG", "JJ", "NNP", "NNS", "CD", "VBN", "VBD")
    //getRolePosTagLexicon(tokenInstances, trTag, 5, isTrain)
    val trCandidates = tokenInstances.filter(x => trPosTagLex.exists(y => pos(x).contains(y)))
    reportRoleStats(tokenInstances, trCandidates, trTag)
    trCandidates
  }

  private def getGoldLandmarkPairs(proportion: DataProportion): List[Relation] = {

    // create pairs which first argument is landmark and second is indicator, and remove duplicates
    val nullLandmarkIds = getTags(lmTag, proportion).filter(_.getStart == -1).map(_.getId)
    getRelations("landmark_id", "spatial_indicator_id", proportion)
      .filter(x => !nullLandmarkIds.contains(x.getArgumentId(0)))
      .groupBy(x => x.getArgumentId(0) + "_" + x.getArgumentId(1))
      .map { case (_, list) => list.head }
      .toList
  }

  private def getGoldTrajectorPairs(proportion: DataProportion): List[Relation] = {

    // create pairs which first argument is trajector and second is indicator, and remove duplicates
    val nullTrajectorIds = getTags(trTag, proportion).filter(_.getStart == -1).map(_.getId)
    getRelations("trajector_id", "spatial_indicator_id", proportion)
      .filter(x => !nullTrajectorIds.contains(x.getArgumentId(0)))
      .groupBy(x => x.getArgumentId(0) + "_" + x.getArgumentId(1))
      .map { case (_, list) => list.head }
      .toList
  }

  private def getRelations(firstArgId: String, secondArgId: String, proportion: DataProportion): List[Relation] = {
    proportion match {
      case Train => getXmlReader(true).getRelations(relationTag, firstArgId, secondArgId).toList
      case Test => getXmlReader(false).getRelations(relationTag, firstArgId, secondArgId).toList
      case Both => getXmlReader(true).getRelations(relationTag, firstArgId, secondArgId).toList ++
        getXmlReader(false).getRelations(relationTag, firstArgId, secondArgId)
    }
  }

  private def getTags(tag: String, proportion: DataProportion): List[NlpBaseElement] = {

    proportion match {
      case Train => getXmlReader(true).getTagAsNlpBaseElement(tag).toList
      case Test => getXmlReader(false).getTagAsNlpBaseElement(tag).toList
      case Both => getXmlReader(true).getTagAsNlpBaseElement(tag).toList ++
        getXmlReader(false).getTagAsNlpBaseElement(tag)
    }
  }

  private def setTokenRoles(tokenInstances: List[Token], proportion: DataProportion): Unit = {

    if (proportion != Test) {
      getXmlReader(true).addPropertiesFromTag(trTag, tokenInstances, XmlMatchings.xmlHeadwordMatching)
      getXmlReader(true).addPropertiesFromTag(lmTag, tokenInstances, XmlMatchings.xmlHeadwordMatching)
      getXmlReader(true).addPropertiesFromTag(spTag, tokenInstances, XmlMatchings.xmlHeadwordMatching)
    }

    if (proportion != Train) {
      getXmlReader(false).addPropertiesFromTag(trTag, tokenInstances, XmlMatchings.xmlHeadwordMatching)
      getXmlReader(false).addPropertiesFromTag(lmTag, tokenInstances, XmlMatchings.xmlHeadwordMatching)
      getXmlReader(false).addPropertiesFromTag(spTag, tokenInstances, XmlMatchings.xmlHeadwordMatching)
    }
  }

  private def getSentenceList(proportion: DataProportion): List[Sentence] = {

    proportion match {
      case Train => getXmlReader(true).getSentences().toList
      case Test => getXmlReader(false).getSentences().toList
      case Both => getXmlReader(true).getSentences().toList ++ getXmlReader(false).getSentences()
    }
  }

  private def getDocumentList(proportion: DataProportion): List[Document] = {

    proportion match {
      case Train => getXmlReader(true).getDocuments().toList
      case Test => getXmlReader(false).getDocuments().toList
      case Both => getXmlReader(true).getDocuments().toList ++ getXmlReader(false).getDocuments()
    }
  }

  private def getImageRelationList(proportion: DataProportion): List[SegmentRelation] = {

    proportion match {
      case Train => CLEFDataSet.trainingRelations.toList
      case Test => CLEFDataSet.testRelations.toList
      case Both => CLEFDataSet.trainingRelations.toList ++ CLEFDataSet.testRelations
    }
  }

  private def getSegmentList(proportion: DataProportion): List[Segment] = {

    proportion match {
      case Train => CLEFDataSet.trainingSegments.toList
      case Test => CLEFDataSet.testSegments.toList
      case Both => CLEFDataSet.trainingSegments.toList ++ CLEFDataSet.testSegments
    }
  }

  private def getImageList(proportion: DataProportion): List[Image] = {

    proportion match {
      case Train => CLEFDataSet.trainingImages.toList
      case Test => CLEFDataSet.testImages.toList
      case Both => CLEFDataSet.trainingImages.toList ++ CLEFDataSet.testImages
    }
  }

  private def reportRoleStats(tokenInstances: List[Token], candidates: List[Token], tagName: String): Unit = {

    val instances = tokenInstances.filter(_.containsProperty(s"${tagName}_id"))
    val actual = instances.map(_.getPropertyValues(s"${tagName}_id").size()).sum
    val missingTokens = instances.toSet.diff(candidates.toSet).toList.map(_.getText.toLowerCase())
    val missing = actual - candidates.map(_.getPropertyValues(s"${tagName}_id").size()).sum

    println(s"Actual ${tagName}: $actual")
    println(s"Missing ${tagName} in the candidates: $missing ($missingTokens)")
  }

  private def reportRelationStats(candidateRelations: List[Relation], goldTrajectorRelations: List[Relation],
    goldLandmarkRelations: List[Relation]): Unit = {

    val missedTrSp = goldTrajectorRelations.size - candidateRelations.count(_.getProperty("RelationType") == "TR-SP")
    println("actual TR-SP:" + goldTrajectorRelations.size)
    println("Missing TR-SP in the candidates: " + missedTrSp)
    val missingTrRelations = goldTrajectorRelations
      .filterNot(r => candidateRelations.exists(x => x.getProperty("RelationType") == "TR-SP" && x.getId == r.getId))
      .map(_.getId)
    println("missing relations from TR-SP: " + missingTrRelations)

    val missedLmSp = goldLandmarkRelations.size - candidateRelations.count(_.getProperty("RelationType") == "LM-SP")
    println("actual LM-SP:" + goldLandmarkRelations.size)
    println("Missing LM-SP in the candidates: " + missedLmSp)
    val missingLmRelations = goldLandmarkRelations
      .filterNot(r => candidateRelations.exists(x => x.getProperty("RelationType") == "LM-SP" && x.getId == r.getId))
      .map(_.getId)
    println("missing relations from LM-SP: " + missingLmRelations)
  }

  private def getRolePosTagLexicon(tokenInstances: List[Token], tagName: String, minFreq: Int, isTrain: Boolean): List[String] = {

    val lexFile = new File(s"data/mSprl/${tagName.toLowerCase}PosTag.lex")
    if (isTrain) {
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

  private def getSpatialIndicatorLexicon(tokenInstances: List[Token], minFreq: Int, isTrain: Boolean): List[String] = {

    val lexFile = new File("data/mSprl/spatialIndicator.lex")
    if (isTrain) {
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

  private lazy val CLEFDataSet = new CLEFImageReader("data/mSprl/saiapr_tc-12", false)
  private lazy val trainReader = createXmlReader(true)
  private lazy val testReader = createXmlReader(false)

  private def getXmlReader(isTrain: Boolean): NlpXmlReader = {
    if (isTrain) trainReader else testReader
  }

  private def createXmlReader(isTrain: Boolean): NlpXmlReader = {
    val path = if (isTrain) "data/SpRL/2017/clef/train/sprl2017_train.xml" else "data/SpRL/2017/clef/gold/sprl2017_gold.xml"
    val reader = new NlpXmlReader(path, "SCENE", "SENTENCE", null, null)
    reader.setIdUsingAnotherProperty("SCENE", "DOCNO")
    reader
  }

  private def getHeadSpan(p: Phrase): (Int, Int) = {
    if (p.getStart == -1)
      return (0, 0)

    val offset = p.getSentence.getStart + p.getStart
    val (_, trHeadStart, trHeadEnd) = getHeadword(p.getText)

    (offset + trHeadStart, offset + trHeadEnd)
  }

  private def getSpan(p: Phrase): (Int, Int) = {
    if (p.getStart == -1)
      return (0, 0)

    val offset = p.getSentence.getStart

    (offset + p.getStart, offset + p.getEnd)
  }
}

