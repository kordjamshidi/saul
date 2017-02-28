package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.{documentToSentenceGenerating}

import scala.collection.JavaConversions._

/**
  * Created by Taher on 2017-02-12.
  */

object MultiModalPopulateData {

  def populateDataFromAnnotatedCorpus(
                                       xmlReader: XmlReaderHelper,
                                       imageReader: ImageReaderHelper,
                                       isTrain: Boolean,
                                       populateImages: Boolean = false,
                                       populateNullPairs: Boolean = true
                                     ): Unit = {

    documents.populate(xmlReader.getDocuments, isTrain)
    sentences.populate(xmlReader.getSentences, isTrain)

    val tokenInstances = (if (isTrain) tokens.getTrainingInstances.toList else tokens.getTestingInstances.toList)
      .filter(_.getId != dummyToken.getId)

    xmlReader.setTokenRoles(tokenInstances)

    val candidateRelations = CandidateGenerator.generatePairCandidates(isTrain, populateNullPairs)
    pairs.populate(candidateRelations, isTrain)

    if (populateImages) {
      images.populate(imageReader.getImageList, isTrain)
      segments.populate(imageReader.getSegmentList, isTrain)
      segmentRelations.populate(imageReader.getImageRelationList, isTrain)
    }

    val relations = if (isTrain) pairs.getTrainingInstances.toList else pairs.getTestingInstances.toList
    xmlReader.setRelationTypes(relations, populateNullPairs)
  }


  def populateDataFromPlainTextDocuments(documentList: List[Document], populateNullPairs: Boolean = true): Unit = {

    val isTrain = false
    documents.populate(documentList, isTrain)
    sentences.populate(documentList.flatMap(d => documentToSentenceGenerating(d)), isTrain)
    val candidateRelations = CandidateGenerator.generatePairCandidates(isTrain, populateNullPairs)
    pairs.populate(candidateRelations, isTrain)
  }
}

