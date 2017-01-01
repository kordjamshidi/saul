package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Constituent, TextAnnotation, TokenLabelView, TreeView}
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator.{USE_NER_CONLL, USE_NER_ONTONOTES, USE_SRL_NOM, USE_SRL_VERB, USE_STANFORD_DEP, USE_STANFORD_PARSE}
import edu.illinois.cs.cogcomp.nlp.utilities.CollinsHeadFinder
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
  TextAnnotationFactory.disableSettings(settings, USE_SRL_NOM, USE_NER_ONTONOTES, USE_SRL_VERB, USE_NER_CONLL, USE_STANFORD_DEP)
  private val as = TextAnnotationFactory.createPipelineAnnotatorService(settings)

  def DocToSentenceMatching(d: Document, s: Sentence): Boolean = {
    d.getId == s.getDocument.getId
  }

  def SentencePhraseGenerating(s: Sentence): Seq[Phrase] = {
    getPhrases(s)
  }

  def RelToTrMatching(r: Relation, p: Phrase): Boolean = {
    // when not using exact matching it can be more than one phrases for a trajector
    p.getPropertyValues("TRAJECTOR_id").contains(r.getArgumentId(0)) ||
      r.getArgumentId(0) == p.getId
  }

  def RelToLmMatching(r: Relation, p: Phrase): Boolean = {
    p.getPropertyValues("LANDMARK_id").contains(r.getArgumentId(2)) ||
      r.getArgumentId(2) == p.getId
  }

  def RelToSpMatching(r: Relation, p: Phrase): Boolean = {
    p.getPropertyValues("SPATIALINDICATOR_id").contains(r.getArgumentId(1)) ||
      r.getArgumentId(1) == p.getId
  }

  def getPos(e: NlpBaseElement): Seq[String] = {
    val constituents = getElementConstituents(e)
    constituents.map(x => WordFeatureExtractorFactory.pos.getFeatures(x).asScala.mkString)
  }

  def getLemma(e: NlpBaseElement): Seq[String] = {
    val constituents = getElementConstituents(e)
    constituents.map(x => WordFeatureExtractorFactory.lemma.getFeatures(x).asScala.mkString)
  }

  def getHeadword(p: Phrase): Token = {
    val ta = getTextAnnotaion(p.getSentence)
    val startId = ta.getTokenIdFromCharacterOffset(p.getStart)
    val endId = ta.getTokenIdFromCharacterOffset(p.getEnd - 1)
    val phrase = ta.getView(ViewNames.SHALLOW_PARSE).getConstituentsCoveringSpan(startId, endId + 1).get(0)

    val tree: TreeView = ta.getView(ViewNames.PARSE_STANFORD).asInstanceOf[TreeView]
    val parsePhrase = tree.getParsePhrase(phrase)
    val headId = CollinsHeadFinder.getInstance.getHeadWordPosition(parsePhrase)
    val head = ta.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituentAtToken(headId)
    new Token(p, p.getId + head.getSpan, head.getStartCharOffset, head.getEndCharOffset, head.toString)
  }

  def getSentence(e: NlpBaseElement) = e match {
    case s: Sentence => s
    case p: Phrase => p.getSentence
    case t: Token => t.getSentence
    case _ => null
  }

  def getPhrases(sentence: Sentence): Seq[Phrase] = {
    val ta = getTextAnnotaion(sentence)
    val v = ta.getView(ViewNames.SHALLOW_PARSE)
    v.getConstituents.asScala.map(x =>
      new Phrase(sentence, getPhraseId(sentence, x), x.getStartCharOffset, x.getEndCharOffset, x.toString))
  }

  private def getPhraseId(sentence: Sentence, x: Constituent): String = {
    return sentence.getId + x.getSpan
  }

  private def getElementConstituents(e: NlpBaseElement): Seq[Constituent] = {
    val s = getSentence(e)
    val ta = getTextAnnotaion(s)
    val v = ta.getView(ViewNames.TOKENS)
    val startId = ta.getTokenIdFromCharacterOffset(e.getStart)
    val endId = ta.getTokenIdFromCharacterOffset(e.getEnd - 1)
    v.getConstituentsCoveringSpan(startId, endId + 1).asScala
  }

  private def getTextAnnotaion(sentence: Sentence): TextAnnotation = {
    if (!sentenceMap.contains(sentence.getId)) {
      val ta = as.createAnnotatedTextAnnotation(sentence.getDocument.getId, sentence.getId, sentence.getText)
      sentenceMap.put(sentence.getId, ta)
    }
    sentenceMap(sentence.getId)
  }
}




