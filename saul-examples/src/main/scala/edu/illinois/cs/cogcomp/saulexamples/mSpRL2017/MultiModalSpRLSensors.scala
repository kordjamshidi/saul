package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.File

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Relation, Token}
import edu.illinois.cs.cogcomp.saulexamples.vision.{Image, Segment, SegmentRelation}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec

import scala.collection.immutable.HashMap
import scala.collection.JavaConversions._

object MultiModalSpRLSensors {

  private lazy val googleWord2Vec = WordVectorSerializer.loadGoogleModel(new File("data/GoogleNews-vectors-negative300.bin"), true)
  private lazy val clefWord2Vec = WordVectorSerializer.readWord2VecModel("data/clef.bin")

  def getWord2VectorSimilarity(w2v: Word2Vec, w1: String, w2: String) = {
    if (w2v.getVocab.containsWord(w1) && w2v.getVocab.containsWord(w2))
      w2v.similarity(w1, w2)
    else
      0.0
  }

  def getWordVector(w2v: Word2Vec, w: String): List[Double] = {
    if (w == null)
      return List.fill(300)(0.0)
    val v = w2v.getWordVector(w)
    if (v == null) {
      List.fill(300)(0.0)
    } else {
      v.toList
    }
  }

  def getAverageSimilarity(w1: String, w2: String) = (googleWord2Vec.similarity(w1, w2) + getClefSimilarity(w1, w2)) / 2

  def getGoogleSimilarity(w1: String, w2: String) = googleWord2Vec.similarity(w1, w2)

  def getGoogleWordVector(w: String): List[Double] = getWordVector(googleWord2Vec, w)

  def getClefSimilarity(w1: String, w2: String) = clefWord2Vec.similarity(w1, w2)

  def getClefWordVector(w: String): List[Double] = getWordVector(clefWord2Vec, w)

  def getAverage(a: List[Double]*): List[Double] = a.head.zipWithIndex.map { case (_, i) => a.map(_ (i)).sum / a.size }

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