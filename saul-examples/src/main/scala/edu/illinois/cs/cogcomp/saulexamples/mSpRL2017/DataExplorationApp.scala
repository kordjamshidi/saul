package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.util

import edu.illinois.cs.cogcomp.saul.util.Logging
import DataProportion._
import MultiModalPopulateData._
import MultiModalSpRLSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.sentenceToTokenGenerating

/**
  * Created by Taher on 2017-02-12.
  */
object DataExplorationApp extends App with Logging {

  val documentList = getDocumentList(Test).take(10)
  val sentenceList = getSentenceList(Test).filter(s => documentList.exists(_.getId == s.getDocument.getId))
  val imageList = getImageList(Test)
    .filter(i => documentList.exists(_.getPropertyFirstValue("IMAGE").endsWith("/" + i.getLabel)))
  val segmentList = getSegmentList(Test).filter(s => imageList.exists(_.getId == s.getAssociatedImageID))
  val imageRelationList = getImageRelationList(Test).filter(r => imageList.exists(_.getId == r.getImageId))

  val tokens = sentenceList.flatMap(sentenceToTokenGenerating)
  setTokenRoles(tokens, Test)
  val trCandidates = getTrajectorCandidates(tokens, true)
  val lmCandidates = getLandmarkCandidates(tokens, true)
  val spCandidates = getIndicatorCandidates(tokens, true)

//  println(trCandidates)
  imageList.foreach(i => println(i.getId))
/*
  val trlmCandidatesImage = segmentList.map(_.getSegmentConcept)

  var concept = ""
  var trcount = 0
  var lmcount = 0
  trlmCandidatesImage.distinct.foreach(segment =>
  {
    if (!phraseConceptToWord.contains(segment))
      concept = segment
    else
      concept = phraseConceptToWord(segment)
    trCandidates.foreach(tr => {
      val similarity = MultiModalSpRLSensors.getWord2VectorSimilarity(tr.toString, concept)
      if(similarity > 0.40) {
        trcount = trcount + 1
        println("trajector" + tr + " - " + concept + " -> " + similarity)
      }
    })
/*    lmCandidates.foreach(lm => {
      val similarity = MultiModalSpRLSensors.getWord2VectorSimilarity(lm.toString, concept)
      if(similarity > 0.40) {
        lmcount = lmcount + 1
        println("landmark" + lm + " - " + concept + " -> " + similarity)
      }
      println("All landmark" + lm + " - " + concept + " -> " + similarity)
    })*/
  })
  println("Total trajectors text ->" + trCandidates.length)
  println("Total trajectors Image ->" + trcount)
//  println("Total landmarks text ->" + lmCandidates.length)
//  println("Total landmarks Image ->" + lmcount)*/

}
