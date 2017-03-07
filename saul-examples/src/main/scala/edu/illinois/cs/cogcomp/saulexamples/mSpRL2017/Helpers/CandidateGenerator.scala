package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers

import java.io.{File, IOException, PrintWriter}

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{NlpBaseElement, Phrase, Relation, Token}
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.{getCandidateRelations, getPos}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Dictionaries

import scala.io.Source

/**
  * Created by taher on 2017-02-28.
  */
object CandidateGenerator {

  def generatePairCandidates(isTrain: Boolean, populateNullPairs: Boolean): List[Relation] = {

    val phraseInstances = (if (isTrain) phrases.getTrainingInstances.toList else phrases.getTestingInstances.toList)
      .filter(_.getId != dummyPhrase.getId)
    if (populateNullPairs) {
      phrases.populate(List(dummyPhrase), isTrain)
    }

    val trCandidates = getTrajectorCandidates(phraseInstances, isTrain)
    trCandidates.foreach(_.addPropertyValue("TR-Candidate", "true"))

    val lmCandidates = getLandmarkCandidates(phraseInstances, isTrain)
    lmCandidates.foreach(_.addPropertyValue("LM-Candidate", "true"))

    val spCandidates = getIndicatorCandidates(phraseInstances, isTrain)
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

  def getIndicatorCandidates(phrases: List[Phrase], isTrain: Boolean): List[Phrase] = {

    val spLex = getSpatialIndicatorLexicon(phrases, 0, isTrain)
    val spPosTagLex = List("IN", "TO")
    val spCandidates = phrases
      .filter(x => spLex.contains(x.getText.toLowerCase) ||
        spPosTagLex.exists(p => getPos(x).contains(p)) ||
        Dictionaries.isPreposition(x.getText))
    ReportHelper.reportRoleStats(phrases, spCandidates, "SPATIALINDICATOR")
    spCandidates
  }

  def getLandmarkCandidates(phrases: List[Phrase], isTrain: Boolean): List[Phrase] = {

    val lmPosTagLex = List("PRP", "NN", "PRP$", "JJ", "NNS", "CD")
    //getRolePosTagLexicon(tokenInstances, lmTag, 5, isTrain)
    val lmCandidates = phrases.filter(x => lmPosTagLex.exists(p => getPos(x).contains(p)))
    ReportHelper.reportRoleStats(phrases, lmCandidates, "LANDMARK")
    lmCandidates
  }

  def getTrajectorCandidates(phrases: List[Phrase], isTrain: Boolean): List[Phrase] = {

    val trPosTagLex = List("NN", "JJR", "PRP$", "VBG", "JJ", "NNP", "NNS", "CD", "VBN", "VBD")
    //getRolePosTagLexicon(tokenInstances, trTag, 5, isTrain)
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

  private def getSpatialIndicatorLexicon[T <: NlpBaseElement](instances: List[T], minFreq: Int, generate: Boolean): List[String] = {

    val lexFile = new File("data/mSprl/spatialIndicator.lex")
    if (generate) {
      val sps = instances.filter(_.containsProperty("SPATIALINDICATOR_id"))
        .groupBy(_.getText.toLowerCase).map { case (key, list) => (key, list.size, list) }.filter(_._2 >= minFreq)
      val prepositionLex = sps.map(_._1).toList
      val writer = new PrintWriter(lexFile)
      prepositionLex.foreach(p => writer.println(p))
      writer.close()
      prepositionLex
    } else {
      if (!lexFile.exists())
        throw new IOException(s"cannot find ${lexFile.getAbsolutePath} file")
      Source.fromFile(lexFile).getLines().toList
    }
  }

}
