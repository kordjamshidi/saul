package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers

import java.io.{File, IOException, PrintWriter}

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel.{dummyToken, pos, tokens}
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Relation, Token}
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.getCandidateRelations
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Dictionaries

import scala.io.Source

/**
  * Created by taher on 2017-02-28.
  */
object CandidateGenerator {

  def generatePairCandidates(isTrain: Boolean, populateNullPairs: Boolean): List[Relation] = {

    val tokenInstances = (if (isTrain) tokens.getTrainingInstances.toList else tokens.getTestingInstances.toList)
      .filter(_.getId != dummyToken.getId)
    if (populateNullPairs) {
      tokens.populate(List(dummyToken), isTrain)
    }

    val trCandidates = getTrajectorCandidates(tokenInstances, isTrain)
    trCandidates.foreach(_.addPropertyValue("TR-Candidate", "true"))

    val lmCandidates = getLandmarkCandidates(tokenInstances, isTrain)
    lmCandidates.foreach(_.addPropertyValue("LM-Candidate", "true"))

    val spCandidates = getIndicatorCandidates(tokenInstances, isTrain)
    spCandidates.foreach(_.addPropertyValue("SP-Candidate", "true"))

    val firstArgCandidates = (if (populateNullPairs) List(null) else List()) ++
      tokenInstances.filter(x => x.containsProperty("TR-Candidate") || x.containsProperty("LM-Candidate"))

    val candidateRelations = getCandidateRelations(firstArgCandidates, spCandidates)

    if (populateNullPairs) {
      // replace null arguments with dummy token
      candidateRelations.filter(_.getArgumentId(0) == null).foreach(x => {
        x.setArgumentId(0, dummyToken.getId)
        x.setArgument(0, dummyToken)
      })
    }
    candidateRelations
  }

  def getIndicatorCandidates(tokenInstances: List[Token], isTrain: Boolean): List[Token] = {

    val spLex = getSpatialIndicatorLexicon(tokenInstances, 0, false)
    val spPosTagLex = List("IN", "TO")
    val spCandidates = tokenInstances
      .filter(x => spLex.contains(x.getText.toLowerCase) ||
        spPosTagLex.contains(pos(x)) ||
        Dictionaries.isPreposition(x.getText))
    ReportHelper.reportRoleStats(tokenInstances, spCandidates, "SPATIALINDICATOR")
    spCandidates
  }

  def getLandmarkCandidates(tokenInstances: List[Token], isTrain: Boolean): List[Token] = {

    val lmPosTagLex = List("PRP", "NN", "PRP$", "JJ", "NNS", "CD")
    //getRolePosTagLexicon(tokenInstances, lmTag, 5, isTrain)
    val lmCandidates = tokenInstances.filter(x => lmPosTagLex.contains(pos(x)))
    ReportHelper.reportRoleStats(tokenInstances, lmCandidates, "LANDMARK")
    lmCandidates
  }

  def getTrajectorCandidates(tokenInstances: List[Token], isTrain: Boolean): List[Token] = {

    val trPosTagLex = List("NN", "JJR", "PRP$", "VBG", "JJ", "NNP", "NNS", "CD", "VBN", "VBD")
    //getRolePosTagLexicon(tokenInstances, trTag, 5, isTrain)
    val trCandidates = tokenInstances.filter(x => trPosTagLex.contains(pos(x)))
    ReportHelper.reportRoleStats(tokenInstances, trCandidates, "TRAJECTOR")
    trCandidates
  }

  private def getRolePosTagLexicon(tokenInstances: List[Token], tagName: String, minFreq: Int, generate: Boolean): List[String] = {

    val lexFile = new File(s"data/mSprl/${tagName.toLowerCase}PosTag.lex")
    if (generate) {
      val posTagLex = tokenInstances.filter(x => x.containsProperty(s"${tagName.toUpperCase}_id"))
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

  private def getSpatialIndicatorLexicon(tokenInstances: List[Token], minFreq: Int, generate: Boolean): List[String] = {

    val lexFile = new File("data/mSprl/spatialIndicator.lex")
    if (generate) {
      val sps = tokenInstances.filter(_.containsProperty("SPATIALINDICATOR_id"))
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
