package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import java.util.Properties

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{Constituent, TextAnnotation, TokenLabelView, TreeView}
import edu.illinois.cs.cogcomp.core.datastructures._
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory
import edu.illinois.cs.cogcomp.edison.features.helpers.PathFeatureHelper
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator._
import edu.illinois.cs.cogcomp.nlp.utilities.CollinsHeadFinder
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.TextAnnotationFactory

import scala.collection.JavaConverters._
import scala.collection.mutable

/** Created by parisakordjamshidi on 12/25/16.
  */
object NlpBaseTypesSensors {
  private val dependencyView = ViewNames.DEPENDENCY_STANFORD
  private val sentenceMap = mutable.HashMap[String, TextAnnotation]()
  private val settings = new Properties()
  TextAnnotationFactory.disableSettings(settings, USE_SRL_NOM, USE_NER_ONTONOTES, USE_SRL_VERB, USE_NER_CONLL)
  private val as = TextAnnotationFactory.createPipelineAnnotatorService(settings)

  def documentToSentenceMatching(d: Document, s: Sentence): Boolean = {
    d.getId == s.getDocument.getId
  }

  def sentenceToPhraseGenerating(s: Sentence): Seq[Phrase] = {
    getPhrases(s)
  }

  def sentenceToPhraseMatching(s: Sentence, p: Phrase): Boolean = {
    s.getId == p.getSentence.getId
  }

  def phraseToTokenGenerating(p: Phrase): Seq[Token] = {
    getTokens(p)
  }

  def phraseToTokenMatching(p: Phrase, t: Token): Boolean = {
    p.getId == t.getPhrase.getId
  }

  def sentenceToTokenGenerating(s: Sentence): Seq[Token] = {
    getTokens(s)
  }

  def sentenceToTokenMatching(s: Sentence, t: Token): Boolean = {
    s.getId == t.getSentence.getId
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
    val ta = getTextAnnotation(p.getSentence)
    val (startId: Int, endId: Int) = getTextAnnotationSpan(p, ta)
    val phrase = ta.getView(ViewNames.SHALLOW_PARSE).getConstituentsCoveringSpan(startId, endId + 1).get(0)

    val tree: TreeView = ta.getView(dependencyView).asInstanceOf[TreeView]
    val parsePhrase = tree.getParsePhrase(phrase)
    val headId = CollinsHeadFinder.getInstance.getHeadWordPosition(parsePhrase)
    val head = ta.getView(ViewNames.TOKENS).asInstanceOf[TokenLabelView].getConstituentAtToken(headId)
    new Token(p, p.getId + head.getSpan, head.getStartCharOffset, head.getEndCharOffset, head.toString)
  }

  def getDependencyRelation(t: Token): String = {
    getDependencyRelations(getTextAnnotation(t.getSentence)).find(r => r.getTarget.getStartCharOffset == t.getStart) match {
      case Some(r) => r.getRelationName
      case _ => ""
    }
  }

  private def getDependencyRelations(ta: TextAnnotation): Seq[textannotation.Relation] = {
    ta.getView(dependencyView).asInstanceOf[TreeView].getRelations.asScala
  }


  private def getSentence(e: NlpBaseElement) = e match {
    case s: Sentence => s
    case p: Phrase => p.getSentence
    case t: Token => t.getSentence
    case _ => null
  }

  private def getPhrases(sentence: Sentence): Seq[Phrase] = {
    val ta = getTextAnnotation(sentence)
    val v = ta.getView(ViewNames.SHALLOW_PARSE)
    v.getConstituents.asScala.map(x =>
      new Phrase(sentence, generateId(sentence, x), x.getStartCharOffset, x.getEndCharOffset, x.toString))
  }

  private def getTokens(e: NlpBaseElement): Seq[Token] = {
    val sentence = getSentence(e)
    val ta = getTextAnnotation(sentence)
    val v = ta.getView(ViewNames.TOKENS)
    val (startId: Int, endId: Int) = getTextAnnotationSpan(e, ta)
    v.getConstituentsCoveringSpan(startId, endId).asScala.map(x =>
      e match {
        case p: Phrase => new Token(p, generateId(e, x), x.getStartCharOffset, x.getEndCharOffset, x.toString)
        case s: Sentence => new Token(s, generateId(e, x), x.getStartCharOffset, x.getEndCharOffset, x.toString)
        case _ => null
      }
    )
  }

  private def generateId(e: NlpBaseElement, x: Constituent): String = {
    e.getId + x.getSpan
  }

  private def getElementConstituents(e: NlpBaseElement): Seq[Constituent] = {
    val s = getSentence(e)
    val ta = getTextAnnotation(s)
    val v = ta.getView(ViewNames.TOKENS)
    val startId = ta.getTokenIdFromCharacterOffset(e.getStart)
    val endId = ta.getTokenIdFromCharacterOffset(e.getEnd - 1)
    v.getConstituentsCoveringSpan(startId, endId + 1).asScala
  }

  private def getTextAnnotation(sentence: Sentence): TextAnnotation = {
    if (!sentenceMap.contains(sentence.getId)) {
      val ta = as.createAnnotatedTextAnnotation(sentence.getDocument.getId, sentence.getId, sentence.getText)
      sentenceMap.put(sentence.getId, ta)
    }
    sentenceMap(sentence.getId)
  }

  private def getTextAnnotationSpan(e: NlpBaseElement, ta: TextAnnotation) = {
    val startId = ta.getTokenIdFromCharacterOffset(e.getStart)
    val endId = ta.getTokenIdFromCharacterOffset(e.getEnd - 1)
    (startId, endId)
  }
}

