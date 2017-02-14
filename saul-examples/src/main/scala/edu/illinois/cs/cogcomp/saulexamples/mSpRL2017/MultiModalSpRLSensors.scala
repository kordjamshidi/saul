package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.File

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ Document, Relation, Token }
import edu.illinois.cs.cogcomp.saulexamples.vision.{ Image, Segment, SegmentRelation }
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer

import scala.collection.immutable.HashMap
import scala.collection.JavaConversions._

object MultiModalSpRLSensors {

  private lazy val word2Vec = WordVectorSerializer.loadGoogleModel(new File("data/GoogleNews-vectors-negative300.bin"), true)

  def getWord2VectorSimilarity(w1: String, w2: String) = {
    word2Vec.similarity(w1, w2)
  }

  def getWordVector(w: String): List[Double] = {
    if(w == null)
      return List.fill(300)(0.0)
    val v = word2Vec.getWordVector(w)
    if (v == null) {
      List.fill(300)(0.0)
    } else {
      v.toList
    }
  }

  def imageToSegmentMatching(i: Image, s: Segment): Boolean = {
    i.getId == s.getAssociatedImageID
  }

  def segmentRelationToSegmentMatching(r: SegmentRelation, s: Segment): Boolean = {
    (r.getFirstSegmentId == s.getSegmentId || r.getSecondSegmentId == s.getSegmentId) && (r.getImageId == s.getAssociatedImageID)
  }

  def relationToFirstArgumentMatching(r: Relation, t: Token): Boolean = {
    r.getArgumentId(0) == t.getId
  }

  def relationToSecondArgumentMatching(r: Relation, t: Token): Boolean = {
    r.getArgumentId(1) == t.getId
  }

  def documentToImageMatching(d: Document, i: Image): Boolean = {
    d.getPropertyFirstValue("IMAGE").endsWith("/" + i.getLabel)
  }

  val phraseConceptToWord = HashMap(
    "child-boy" -> "kid",
    "child-girl" -> "kid",
    "construction-other" -> "construction",
    "couple-of-persons" -> "people",
    "face-of-person" -> "person",
    "group-of-persons" -> "people",
    "hand-of-person" -> "hand",
    "head-of-person" -> "head",
    "person-related-objects" -> "person",
    "floor-other" -> "floor",
    "floor-wood" -> "floor",
    "floor-carpet" -> "carpet",
    "floor-tennis-court" -> "tennis",
    "branch" -> "tree",
    "kitchen-pot" -> "pot",
    "plant-pot" -> "pot",
    "non-wooden-furniture" -> "furniture",
    "wooden-furniture" -> "furniture",
    "furniture-other" -> "furniture",
    "public-sign" -> "sign",
    "ruin-archeological" -> "ruin",
    "sand-beach" -> "beach",
    "sand-dessert" -> "dessert",
    "sky-blue" -> "sky",
    "sky-light" -> "sky",
    "sky-night" -> "sky",
    "sky-red-sunset-dusk" -> "sky",
    "water-reflection" -> "water",
    "aerostatic-balloon" -> "balloon",
    "air-vehicles" -> "vehicles",
    "ground-vehicles" -> "vehicles",
    "vehicles-with-tires" -> "vehicles",
    "space-shuttle" -> "shuttle",
    "ancent-building" -> "building",
    "swimming-pool" -> "swimming",
    "boat-rafting" -> "boat",
    "bobcat-wildcat" -> "cat",
    "polar-bear" -> "bear",
    "fowl-hen" -> "hen",
    "flock-of-birds" -> "birds",
    "school-of-fishes" -> "fishes",
    "man-made" -> "thing",
    "man-made-other" -> "thing",
    "ocean-animal" -> "ocean",
    "herd-of-mammals" -> "mammal",
    "mammal-other" -> "mammal",
    "musical-instrument" -> "instrument"
  )
}