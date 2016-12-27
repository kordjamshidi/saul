package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Constituent, TextAnnotation}
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator.{USE_NER_CONLL, USE_NER_ONTONOTES, USE_SRL_NOM, USE_SRL_VERB, USE_STANFORD_PARSE, USE_STANFORD_DEP}
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

import scala.collection.JavaConverters._
import scala.collection.mutable


/**
  * Created by parisakordjamshidi on 12/25/16.
  */
object SpRLNewSensors {
  private val sentenceMap = mutable.HashMap[String, TextAnnotation]()
  private val settings = new Properties()
  TextAnnotationFactory.disableSettings(settings, USE_SRL_NOM, USE_NER_ONTONOTES, USE_SRL_VERB, USE_NER_CONLL, USE_STANFORD_DEP, USE_STANFORD_PARSE)
  private val as = TextAnnotationFactory.createPipelineAnnotatorService(settings)

  def DocToSentence(d: Document, s: Sentence): Boolean = {
    d.getId == s.getDocumentId
  }

  def RelToTr(r: Relation, p: Phrase): Boolean = {
    r.getProperty("trajector_id") == p.getId
  }

  def RelToLm(r: Relation, p: Phrase): Boolean = {
    r.getProperty("landmark_id") == p.getId
  }

  def RelToSp(r: Relation, p: Phrase): Boolean = {
    r.getProperty("spatial_indicator_id") == p.getId
  }

  def getPos(p: NlpBaseElement, s: Sentence): Seq[String] = {
    val constituents = getPhraseConstituents(p, s)
    constituents.map(x => WordFeatureExtractorFactory.pos.getFeatures(x).asScala.mkString)
  }

  def getLemma(p: NlpBaseElement, s: Sentence): Seq[String] = {
    val constituents = getPhraseConstituents(p, s)
    constituents.map(x => WordFeatureExtractorFactory.lemma.getFeatures(x).asScala.mkString)
  }

  private def getPhraseConstituents(p: NlpBaseElement, s: Sentence): Seq[Constituent] = {
    val ta = getTextAnnotaion(s)
    val v = ta.getView(ViewNames.TOKENS)
    val startId = ta.getTokenIdFromCharacterOffset(p.getStart - s.getStart)
    val endId = ta.getTokenIdFromCharacterOffset(p.getEnd - 1 - s.getStart)
    v.getConstituentsCoveringSpan(startId, endId + 1).asScala
  }

  private def getTextAnnotaion(sentence: Sentence): TextAnnotation = {
    if (!sentenceMap.contains(sentence.getId)) {
      val ta = as.createAnnotatedTextAnnotation(sentence.getDocumentId, sentence.getId, sentence.getText)
      sentenceMap.put(sentence.getId, ta)
    }
    sentenceMap(sentence.getId)
  }
}




