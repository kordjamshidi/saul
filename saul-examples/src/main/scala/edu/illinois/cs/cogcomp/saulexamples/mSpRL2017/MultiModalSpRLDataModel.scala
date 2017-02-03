package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.File

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Dictionaries
import edu.illinois.cs.cogcomp.saulexamples.vision.{Image, Segment, SegmentRelation}

/**
  * Created by Taher on 2017-01-11.
  */
object MultiModalSpRLDataModel extends DataModel {

  /*
  Nodes
   */
  val documents = node[Document]
  val sentences = node[Sentence]
  val tokens = node[Token]
  val textRelations = node[Relation]

  val images = node[Image]
  val segments = node[Segment]
  val segmentRelations = node[SegmentRelation]

  /*
  Edges
   */
  val documentToSentence = edge(documents, sentences)
  documentToSentence.addSensor(documentToSentenceMatching _)

  val sentenceToToken = edge(sentences, tokens)
  sentenceToToken.addSensor(sentenceToTokenGenerating _)

  val relationToToken = edge(textRelations, tokens)
  relationToToken.addSensor(relationToTokenMatching _)

  val documentToImage = edge(documents, images)
  documentToImage.addSensor(documentToImageMatching _)

  val imageToSegment = edge(images, segments)
  imageToSegment.addSensor(imageToSegmentMatching _)

  val segmentRelationsToSegments = edge(segmentRelations, segments)
  segmentRelationsToSegments.addSensor(segmentRelationToSegmentMatching _)

  /*
  Properties
   */
  val trajectorRole = property(tokens) {
    x: Token =>
      if (x.containsProperty("TRAJECTOR_id"))
        "Trajector"
      else
        "None"
  }

  val landmarkRole = property(tokens) {
    x: Token =>
      if (x.containsProperty("LANDMARK_id"))
        "Landmark"
      else
        "None"
  }

  val indicatorRole = property(tokens) {
    x: Token =>
      if (x.containsProperty("SPATIALINDICATOR_id"))
        "Indicator"
      else
        "None"
  }

  val spatialRole = property(tokens) {
    x: Token =>
      if (x.containsProperty("TRAJECTOR_id"))
        "Trajector"
      else if (x.containsProperty("LANDMARK_id"))
        "Landmark"
      else if (x.containsProperty("SPATIALINDICATOR_id"))
        "Indicator"
      else
        "None"
  }

  val wordForm = property(tokens) {
    x: Token => x.getText
  }

  val pos = property(tokens) {
    x: Token => getPos(x).mkString
  }

  val semanticRole = property(tokens) {
    x: Token => "" //getSemanticRole(x)
  }

  val dependencyRelation = property(tokens) {
    x: Token => getDependencyRelation(x)
  }

  val subCategorization = property(tokens) {
    x: Token => getSubCategorization(x)
  }

  val spatialContext = property(tokens) {
    x: Token =>
      if (!Dictionaries.isSpatial(x.getText))
        0
      else if (getWindow(x, 0, 5).count(w => Dictionaries.isSpatial(w)) > 1)
        1
      else
        2
  }

  val tokenVector = property(tokens) {
    x: Token => getWordVector(x.getText.toLowerCase)
  }

  val isTokenAnImageConcept = property(tokens) {
    t: Token =>
      getSegmentConcepts(t)
        .exists(x => getWord2VectorSimilarity(t.getText.toLowerCase, x) > 0.6)
  }

  val nearestSegmentConceptVector = property(tokens) {
    t: Token =>
      val concepts = getSegmentConcepts(t).map(x => (x, getWord2VectorSimilarity(t.getText.toLowerCase, x)))
      val (nearest, _) = if (concepts.isEmpty) ("", 0) else concepts.maxBy(x => x._2)
      getWordVector(nearest)
  }

  val relationType = property(textRelations) {
    x: Relation => x.getProperty("RelationType")
  }

  val relationWordForm = property(textRelations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      wordForm(first) + wordForm(second)
  }

  val relationPos = property(textRelations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      pos(first) + pos(second)
  }

  val relationSemanticRole = property(textRelations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      semanticRole(first) + semanticRole(second)
  }

  val relationDependencyRelation = property(textRelations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      dependencyRelation(first) + dependencyRelation(second)
  }

  val relationSubCategorization = property(textRelations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      subCategorization(first) + subCategorization(second)
  }

  val relationSpatialContext = property(textRelations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      spatialContext(first) + spatialContext(second)
  }

  val relationTokensVector = property(textRelations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      tokenVector(first) ++ tokenVector(second)
  }

  val relationNearestSegmentConceptVector = property(textRelations) {
    r: Relation =>
      val (first, _) = getArguments(r)
      nearestSegmentConceptVector(first)
  }

  val relationIsTokenAnImageConcept = property(textRelations) {
    r: Relation =>
      val (first, _) = getArguments(r)
      isTokenAnImageConcept(first)
  }

  val before = property(textRelations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      isBefore(first, second)
  }

  val distance = property(textRelations) {
    r: Relation =>
      val (first, second) = getArguments(r)
      getTokenDistance(first, second)
  }

  val imageLabel = property(images) {
    x: Image => x.getLabel
  }

  val imageId = property(images) {
    x: Image => x.getId
  }

  val segmentLabel = property(segments) {
    x: Segment => x.getSegmentConcept
  }

  val segmentId = property(segments) {
    x: Segment => x.getSegmentCode
  }

  val segmentFeatures = property(segments) {
    x: Segment => x.getSegmentFeatures.split(" ").toList.map(_.toDouble)
  }

  ////////////////////////////////////////////////////////////////////
  /// Helper methods
  ////////////////////////////////////////////////////////////////////
  def getArguments(r: Relation): (Token, Token) = {
    val arguments = (textRelations(r) ~> relationToToken).toList
    if (arguments(0).getPropertyValues("SPATIALINDICATOR_id").contains(r.getArgumentId(1)))
      (arguments(1), arguments(0))
    else
      (arguments(0), arguments(1))
  }

  private def getSegmentConcepts(t: Token) = {
    ((tokens(t) <~ sentenceToToken <~ documentToSentence) ~> documentToImage ~> imageToSegment)
      .map(x =>
        if (!phraseConceptToWord.contains(x.getSegmentConcept))
          x.getSegmentConcept
        else
          phraseConceptToWord(x.getSegmentConcept)
      )
  }
}