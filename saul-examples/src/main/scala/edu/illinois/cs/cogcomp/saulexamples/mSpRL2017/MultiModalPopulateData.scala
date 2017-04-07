package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.{ documentToSentenceGenerating }

import scala.collection.JavaConversions._

/** Created by Taher on 2017-02-12.
  */

object MultiModalPopulateData {

  def populateDataFromAnnotatedCorpus(
    xmlReader: SpRLXmlReader,
    imageReader: ImageReaderHelper,
    isTrain: Boolean,
    populateImages: Boolean = false,
    populateNullPairs: Boolean = true
  ): Unit = {

    if(isTrain){
      CandidateGenerator.createSpatialIndicatorLexicon(xmlReader)
    }
    documents.populate(xmlReader.getDocuments, isTrain)
    sentences.populate(xmlReader.getSentences, isTrain)
    if (populateNullPairs) {
      phrases.populate(List(dummyPhrase), isTrain)
    }

    val phraseInstances = (if (isTrain) phrases.getTrainingInstances.toList else phrases.getTestingInstances.toList)
      .filter(_.getId != dummyPhrase.getId)

    xmlReader.setRoles(phraseInstances)

    val candidateRelations = CandidateGenerator.generatePairCandidates(phraseInstances, populateNullPairs)
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
    if (populateNullPairs) {
      phrases.populate(List(dummyPhrase), isTrain)
    }
    val candidateRelations = CandidateGenerator.generatePairCandidates(phrases.getTrainingInstances.toList, populateNullPairs)
    pairs.populate(candidateRelations, isTrain)
  }
}

