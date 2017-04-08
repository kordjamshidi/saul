package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.File

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers.IndicatorRoleClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Dictionaries
import edu.illinois.cs.cogcomp.saulexamples.vision.{Image, Segment, SegmentRelation}

/** Created by Taher on 2017-01-11.
  */
object MultiModalSpRLDataModel extends DataModel {

  val dummyPhrase = new Phrase()
  dummyPhrase.setText("[[None]]")
  dummyPhrase.setId("[[dummy]]")
  dummyPhrase.addPropertyValue("TRAJECTOR_id", dummyPhrase.getId)
  dummyPhrase.addPropertyValue("LANDMARK_id", dummyPhrase.getId)

  var useVectorAverages = true

  /*
  Nodes
   */
  val documents = node[Document]
  val sentences = node[Sentence]
  val phrases = node[Phrase]
  val tokens = node[Token]
  val pairs = node[Relation]

  val images = node[Image]
  val segments = node[Segment]
  val segmentRelations = node[SegmentRelation]

  /*
  Edges
   */
  val documentToSentence = edge(documents, sentences)
  documentToSentence.addSensor(documentToSentenceMatching _)

  val sentenceToRelations = edge(sentences, pairs)
  //sentenceToRelations.addSensor(sentenceToRelationMatching _)

  //val sentenceToToken = edge(sentences, tokens)
  //sentenceToToken.addSensor(sentenceToTokenGenerating _)

  val sentenceToPhrase = edge(sentences, phrases)
  sentenceToPhrase.addSensor(refinedSentenceToPhraseGenerating _)

  val phraseToToken = edge(phrases, tokens)
  phraseToToken.addSensor(phraseToTokenGenerating _)

  val relationToFirstArgument = edge(pairs, phrases)
  relationToFirstArgument.addSensor(relationToFirstArgumentMatching _)

  val relationToSecondArgument = edge(pairs, phrases)
  relationToSecondArgument.addSensor(relationToSecondArgumentMatching _)

  val documentToImage = edge(documents, images)
  documentToImage.addSensor(documentToImageMatching _)

  val imageToSegment = edge(images, segments)
  imageToSegment.addSensor(imageToSegmentMatching _)

  val segmentRelationsToSegments = edge(segmentRelations, segments)
  segmentRelationsToSegments.addSensor(segmentRelationToSegmentMatching _)

  /*
  Properties
   */
  val trajectorRole = property(phrases) {
    x: Phrase =>
      if (x.containsProperty("TRAJECTOR_id") && x != dummyPhrase)
        "Trajector"
      else
        "None"
  }

  val landmarkRole = property(phrases) {
    x: Phrase =>
      if (x.containsProperty("LANDMARK_id") && x != dummyPhrase)
        "Landmark"
      else
        "None"
  }

  val indicatorRole = property(phrases) {
    x: Phrase =>
      if (x.containsProperty("SPATIALINDICATOR_id"))
        "Indicator"
      else
        "None"
  }

  val spatialRole = property(phrases) {
    x: Phrase =>
      if (x.containsProperty("TRAJECTOR_id") && x != dummyPhrase)
        "Trajector"
      else if (x.containsProperty("LANDMARK_id") && x != dummyPhrase)
        "Landmark"
      else if (x.containsProperty("SPATIALINDICATOR_id"))
        "Indicator"
      else
        "None"
  }

  val wordForm = property(phrases, cache = true) {
    x: Phrase =>
      if (x != dummyPhrase) (phrases(x) ~> phraseToToken).toList.sortBy(_.getStart)
        .map(t => t.getText.toLowerCase).mkString("|") else "None"
  }

  val lemma = property(phrases, cache = true) {
    x: Phrase =>
      if (x != dummyPhrase) (phrases(x) ~> phraseToToken).toList.sortBy(_.getStart)
        .map(t => getLemma(t).mkString).mkString("|") else "None"
  }

  val pos = property(phrases, cache = true) {
    x: Phrase =>
      if (x != dummyPhrase) (phrases(x) ~> phraseToToken).toList.sortBy(_.getStart)
        .map(t => getPos(t).mkString).mkString("|") else "None"
  }

  val headWordFrom = property(phrases, cache = true) {
    x: Phrase => if (x != dummyPhrase) getHeadword(x).getText.toLowerCase else "None"
  }

  val headWordPos = property(phrases, cache = true) {
    x: Phrase => if (x != dummyPhrase) getPos(getHeadword(x)).mkString else "None"
  }

  val headWordLemma = property(phrases, cache = true) {
    x: Phrase => if (x != dummyPhrase) getLemma(getHeadword(x)).mkString else "None"
  }

  val phrasePos = property(phrases, cache = true) {
    x: Phrase => if (x != dummyPhrase) getPhrasePos(x) else "None"
  }

  val semanticRole = property(phrases) {
    x: Phrase => "" //getSemanticRole(x)
  }

  val dependencyRelation = property(phrases, cache = true) {
    x: Phrase =>
      if (x != dummyPhrase) (phrases(x) ~> phraseToToken).toList.sortBy(_.getStart)
        .map(t => getDependencyRelation(t)).mkString("|") else "None"
  }

  val headDependencyRelation = property(phrases, cache = true) {
    x: Phrase => if (x != dummyPhrase) getDependencyRelation(getHeadword(x)) else "None"
  }

  val subCategorization = property(phrases, cache = true) {
    x: Phrase =>
      if (x != dummyPhrase) (phrases(x) ~> phraseToToken).toList.sortBy(_.getStart)
        .map(t => getSubCategorization(t)).mkString("|") else "None"
  }

  val headSubCategorization = property(phrases, cache = true) {
    x: Phrase => if (x != dummyPhrase) getSubCategorization(getHeadword(x)) else "None"
  }

  val headSpatialContext = property(phrases, cache = true) {
    x: Phrase =>
      val head = if (x == dummyPhrase) null else getHeadword(x)
      if (x == dummyPhrase)
        "None"
      else if (!Dictionaries.isSpatial(head.getText))
        "0"
      else if (getWindow(head, 0, 5).count(w => Dictionaries.isSpatial(w)) > 1)
        "1"
      else
        "2"
  }

  val spatialContext = property(phrases, cache = true) {
    x: Phrase =>
      val tokens = if (x == dummyPhrase) null else phrases(x) ~> phraseToToken
      if (x == dummyPhrase)
        "None"
      else if (tokens.forall(t => !Dictionaries.isSpatial(t.getText)))
        "0"
      else if (tokens.exists(t => getWindow(t, 0, 5).count(w => Dictionaries.isSpatial(w)) > 1))
        "1"
      else
        "2"
  }

  val tokenVector = property(phrases, cache = true) {
    x: Phrase => if (x != dummyPhrase) getVector(getHeadword(x).getText.toLowerCase) else getVector(null)
  }

  val isTokenAnImageConcept = property(phrases, cache = true) {
    p: Phrase =>
      if (p != dummyPhrase) {
        val head = getHeadword(p)
        getSegmentConcepts(head)
          .exists(x => getSimilarity(head.getText.toLowerCase, x) > 0.6).toString
      } else {
        ""
      }
  }

  val nearestSegmentConceptVector = property(phrases, cache = true) {
    p: Phrase =>
      if (p != dummyPhrase) {
        val head = getHeadword(p)
        val concepts = getSegmentConcepts(head).map(x => (x, getSimilarity(head.getText.toLowerCase, x)))
        val (nearest, _) = if (concepts.isEmpty) ("", 0) else concepts.maxBy(x => x._2)
        getVector(nearest)
      } else {
        getVector(null)
      }
  }

  val isTrajectorRelation = property(pairs, cache = true) {
    x: Relation =>
      x.getProperty("RelationType") match {
        case "TR-SP" => "TR-SP"
        case _ => "None"
      }
  }

  val isLandmarkRelation = property(pairs, cache = true) {
    x: Relation =>
      x.getProperty("RelationType") match {
        case "LM-SP" => "LM-SP"
        case _ => "None"
      }
  }

  val isTrajectorCandidate = property(pairs) {
    r: Relation => getArguments(r)._1.containsProperty("TR-Candidate")
  }

  val isLandmarkCandidate = property(pairs) {
    r: Relation => getArguments(r)._1.containsProperty("LM-Candidate")
  }

  val isIndicatorCandidate = property(pairs) {
    r: Relation => getArguments(r)._1.containsProperty("SP-Candidate")
  }

  val relationWordForm = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      wordForm(first) + "::" + wordForm(second)
  }

  val relationFirstWord = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      wordForm(first)
  }

  val relationSecondWord = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      wordForm(second)
  }

  val relationHeadWordForm = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      headWordFrom(first) + "::" + headWordFrom(second)
  }

  val relationFirstHeadWord = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      headWordFrom(first)
  }

  val relationSecondHeadWord = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      headWordFrom(second)
  }

  val relationLemma = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      lemma(first) + "::" + lemma(second)
  }

  val relationFirstLemma = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      lemma(first)
  }

  val relationSecondLemma = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      lemma(second)
  }

  val relationHeadWordLemma = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      headWordLemma(first) + "::" + headWordLemma(second)
  }

  val relationFirstHeadLemma = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      headWordLemma(first)
  }

  val relationSecondHeadLemma = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      headWordLemma(second)
  }

  val relationPos = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      pos(first) + "::" + pos(second)
  }

  val relationFirstPos = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      pos(first)
  }

  val relationSecondPos = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      pos(second)
  }

  val relationHeadWordPos = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      headWordPos(first) + "::" + headWordPos(second)
  }

  val relationFirstHeadPos = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      headWordPos(first)
  }

  val relationSecondHeadPos = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      headWordPos(second)
  }

  val relationPhrasePos = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      phrasePos(first) + "::" + phrasePos(second)
  }

  val relationFirstPhrasePos = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      phrasePos(first)
  }

  val relationSecondPhrasePos = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      phrasePos(second)
  }

  val relationSemanticRole = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      semanticRole(first) + "::" + semanticRole(second)
  }

  val relationDependencyRelation = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      dependencyRelation(first) + "::" + dependencyRelation(second)
  }

  val relationFirstDependencyRelation = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      dependencyRelation(first)
  }

  val relationSecondDependencyRelation = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      dependencyRelation(second)
  }

  val relationHeadDependencyRelation = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      headDependencyRelation(first) + "::" + headDependencyRelation(second)
  }

  val relationFirstHeadDependency = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      headDependencyRelation(first)
  }

  val relationSecondHeadDependency = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      headDependencyRelation(second)
  }

  val relationSubCategorization = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      subCategorization(first) + "::" + subCategorization(second)
  }

  val relationFirstSubCategorization = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      subCategorization(first)
  }

  val relationSecondSubCategorization = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      subCategorization(second)
  }

  val relationHeadSubCategorization = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      headSubCategorization(first) + "::" + headSubCategorization(second)
  }

  val relationFirstHeadSubCategorization = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      headSubCategorization(first)
  }

  val relationSecondHeadSubCategorization = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      headSubCategorization(second)
  }

  val relationSpatialContext = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      spatialContext(first) + "::" + spatialContext(second)
  }

  val relationFirstSpatialContext = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      spatialContext(first)
  }

  val relationSecondSpatialContext = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      spatialContext(second)
  }

  val relationHeadSpatialContext = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      headSpatialContext(first) + "::" + headSpatialContext(second)
  }

  val relationFirstHeadSpatialContext = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      headSpatialContext(first)
  }

  val relationSecondHeadSpatialContext = property(pairs, cache = true) {
    r: Relation =>
      val (_, second) = getArguments(r)
      headSpatialContext(second)
  }

  val relationTokensVector = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      tokenVector(first) ++ tokenVector(second)
  }

  val relationNearestSegmentConceptVector = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      nearestSegmentConceptVector(first)
  }

  val relationIsTokenAnImageConcept = property(pairs, cache = true) {
    r: Relation =>
      val (first, _) = getArguments(r)
      isTokenAnImageConcept(first)
  }

  val before = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      if (first == dummyPhrase)
        ""
      else
        isBefore(first, second).toString
  }

  val distance = property(pairs, cache = true) {
    r: Relation =>
      val (first, second) = getArguments(r)
      if (first == dummyPhrase)
        -1
      else
        getTokenDistance(first, second)
  }

  val imageLabel = property(images, cache = true) {
    x: Image => x.getLabel
  }

  val imageId = property(images, cache = true) {
    x: Image => x.getId
  }

  val segmentLabel = property(segments, cache = true) {
    x: Segment => x.getSegmentConcept
  }

  val segmentId = property(segments, cache = true) {
    x: Segment => x.getSegmentCode
  }

  val segmentFeatures = property(segments, cache = true) {
    x: Segment => x.getSegmentFeatures.split(" ").toList.map(_.toDouble)
  }

  ////////////////////////////////////////////////////////////////////
  /// Helper methods
  ////////////////////////////////////////////////////////////////////
  private def getVector(w: String): List[Double] = {
    if (useVectorAverages) {
      getAverage(getGoogleWordVector(w), getClefWordVector(w))
    } else {
      getGoogleWordVector(w)
    }
  }

  private def getSimilarity(w1: String, w2: String): Double = {
    if (useVectorAverages) {
      (getGoogleSimilarity(w1, w2) + getClefSimilarity(w1, w2)) / 2.0
    } else {
      getGoogleSimilarity(w1, w2)
    }
  }

  private def getArguments(r: Relation): (Phrase, Phrase) = {
    ((pairs(r) ~> relationToFirstArgument).head, (pairs(r) ~> relationToSecondArgument).head)
  }

  private def getSegmentConcepts(t: Token) = {
    ((tokens(t) <~ phraseToToken <~ sentenceToPhrase <~ documentToSentence) ~> documentToImage ~> imageToSegment)
      .map(x =>
        if (!phraseConceptToWord.contains(x.getSegmentConcept))
          x.getSegmentConcept
        else
          phraseConceptToWord(x.getSegmentConcept))
  }
}
