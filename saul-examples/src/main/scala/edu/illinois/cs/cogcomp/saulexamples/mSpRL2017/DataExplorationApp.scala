package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.util.Logging
import DataProportion._
import MultiModalPopulateData._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.sentenceToTokenGenerating


/**
  * Created by Taher on 2017-02-12.
  */
object DataExplorationApp extends App with Logging {

  val documentList = getDocumentList(Train).take(10)
  val sentenceList = getSentenceList(Train).filter(s => documentList.exists(_.getId == s.getDocument.getId))
  val imageList = getImageList(Train)
    .filter(i => documentList.exists(_.getPropertyFirstValue("IMAGE").endsWith("/" + i.getLabel)))
  val segmentList = getSegmentList(Train).filter(s => imageList.exists(_.getId == s.getAssociatedImageID))
  val imageRelationList = getImageRelationList(Train).filter(r => imageList.exists(_.getId == r.getImageId))

  val tokens = sentenceList.flatMap(sentenceToTokenGenerating)
  setTokenRoles(tokens, Train)
  val trCandidates = getTrajectorCandidates(tokens, true)
  val lmCandidates = getLandmarkCandidates(tokens, true)
  val spCandidates = getIndicatorCandidates(tokens, true)

}
