package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.File

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Relation, Token}
import edu.illinois.cs.cogcomp.saulexamples.vision.{Image, Segment, SegmentRelation}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer

import scala.collection.immutable.HashMap

object MultiModalSpRLSensors {

  private lazy val word2Vec = WordVectorSerializer.loadGoogleModel(new File("data/GoogleNews-vectors-negative300.bin.gz"), true)

  def getWord2VectorSimilarity(w1: String, w2:String) = {
    word2Vec.similarity(w1, w2)
  }

  def imageToSegmentMatching(i: Image, s: Segment): Boolean = {
    i.getId == s.getAssociatedImageID
  }

  def segmentRelationToSegmentMatching(r: SegmentRelation, s: Segment): Boolean = {
    (r.getFirstSegmentId == s.getSegmentId || r.getSecondSegmentId == s.getSegmentId) && (r.getImageId == s.getAssociatedImageID)
  }

  def relationToTokenMatching(r: Relation, t: Token): Boolean = {
    (r.containsProperty("TR_RELATION") && t.getPropertyValues("TRAJECTOR_id").contains(r.getArgumentId(0))) ||
      (r.containsProperty("LM_RELATION") && t.getPropertyValues("LANDMARK_id").contains(r.getArgumentId(0))) ||
      t.getPropertyValues("SPATIALINDICATOR_id").contains(r.getArgumentId(1))
  }

  def documentToImageMatching(d: Document, i: Image): Boolean = {
    d.getPropertyFirstValue("IMAGE").endsWith("/" + i.getLabel)
  }

  val phraseConceptToWord = HashMap(
    "child-boy" -> "child",
    "child-girl" -> "child",
    "construction-other" -> "construction",
    "couple-of-persons" -> "persons",
    "face-of-person" -> "person",
    "floor-other" -> "floor",
    "floor-wood" -> "floor",
    "group-of-persons" -> "persons",
    "hand-of-person" -> "hand",
    "kitchen-pot" -> "pot",
    "non-wooden-furniture" -> "furniture",
    "plant-pot" -> "pot",
    "public-sign" -> "sign",
    "ruin-archeological" -> "ruin",
    "sand-beach" -> "beach",
    "sand-dessert" -> "dessert",
    "sky-blue" -> "sky",
    "sky-light" -> "sky",
    "sky-night" -> "sky",
    "sky-red-sunset-dusk" -> "sky",
    "water-reflection" -> "water",
    "wooden-furniture" -> "furniture"
  )

}