package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers

import java.io.{File, IOException, PrintWriter}

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.mSpRLConfigurator
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Phrase, Relation}
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.{getCandidateRelations, getPos}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Dictionaries

import scala.collection.JavaConversions._
import scala.io.Source

/** Created by taher on 2017-02-28.
  */
object CandidateGenerator {

  def generatePairCandidates(phraseInstances: List[Phrase], populateNullPairs: Boolean, indicatorClassifier: Phrase => Boolean): List[Relation] = {

    val trCandidates = getTrajectorCandidates(phraseInstances)
    trCandidates.foreach(_.addPropertyValue("TR-Candidate", "true"))

    val lmCandidates = getLandmarkCandidates(phraseInstances)
    lmCandidates.foreach(_.addPropertyValue("LM-Candidate", "true"))

    val spCandidates = phraseInstances.filter(indicatorClassifier) // getIndicatorCandidates(phraseInstances)
    spCandidates.foreach(_.addPropertyValue("SP-Candidate", "true"))

    val firstArgCandidates = (if (populateNullPairs) List(null) else List()) ++
      phraseInstances.filter(x => x.containsProperty("TR-Candidate") || x.containsProperty("LM-Candidate"))

    val candidateRelations = getCandidateRelations(firstArgCandidates, spCandidates)

    if (populateNullPairs) {
      // replace null arguments with dummy token
      candidateRelations.filter(_.getArgumentId(0) == null).foreach(x => {
        x.setArgumentId(0, dummyPhrase.getId)
        x.setArgument(0, dummyPhrase)
      })
    }
    candidateRelations
  }

  def getIndicatorCandidates(phrases: List[Phrase]): List[Phrase] = {

    val spLex = LexiconHelper.spatialIndicatorLexicon
    val spPosTagLex = List("IN", "TO")
    val spCandidates = phrases
      .filter(x =>
        spLex.exists(s => x.getText.toLowerCase.matches("(^|.*[^\\w])" + s + "([^\\w].*|$)")) ||
          spPosTagLex.exists(p => getPos(x).contains(p)) ||
          Dictionaries.isPreposition(x.getText))
    ReportHelper.reportRoleStats(phrases, spCandidates, "SPATIALINDICATOR")
    spCandidates
  }

  def getLandmarkCandidates(phrases: List[Phrase]): List[Phrase] = {

    val lmPosTagLex = List("PRP", "NN", "PRP$", "JJ", "NNS", "CD")
    val lmCandidates = phrases.filter(x => lmPosTagLex.exists(p => getPos(x).contains(p)))
    ReportHelper.reportRoleStats(phrases, lmCandidates, "LANDMARK")
    lmCandidates
  }

  def getTrajectorCandidates(phrases: List[Phrase]): List[Phrase] = {

    val trPosTagLex = List("NN", "JJR", "PRP$", "VBG", "JJ", "NNP", "NNS", "CD", "VBN", "VBD")
    val trCandidates = phrases.filter(x => trPosTagLex.exists(p => getPos(x).contains(p)))
    ReportHelper.reportRoleStats(phrases, trCandidates, "TRAJECTOR")
    trCandidates
  }

  private def getRolePosTagLexicon(phrases: List[Phrase], tagName: String, minFreq: Int, generate: Boolean): List[String] = {

    val lexFile = new File(s"data/mSprl/${tagName.toLowerCase}PosTag.lex")
    if (generate) {
      val posTagLex = phrases.filter(x => x.containsProperty(s"${tagName.toUpperCase}_id"))
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

}
