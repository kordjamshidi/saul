package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.File

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Dictionaries
import edu.illinois.cs.cogcomp.saulexamples.vision.{Image, Segment, SegmentRelation}

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer

/**
  * Created by Taher on 2017-01-11.
  */
object MultiModalSpRLDataModel extends DataModel {

  val gModel = new File("data/GoogleNews-vectors-negative300.bin.gz");
  val word2Vec = WordVectorSerializer.loadGoogleModel(gModel, true);

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

  val isTokenAnImageConcept = property(tokens) {
    t: Token =>
      ((tokens(t) <~ sentenceToToken <~ documentToSentence) ~> documentToImage ~> imageToSegment)
        .map(x => getHeadword(x.getSegmentConcept.replaceAll("_", " ").toLowerCase)._1)
        .exists(x => word2Vec.similarity(t.getText.toLowerCase, x) > 0.60)
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


}
