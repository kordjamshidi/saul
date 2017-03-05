package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.DataProportion._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel.dummyPhrase
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.XmlMatchings

import scala.collection.JavaConversions._

/**
  * Created by taher on 2017-02-28.
  */
class XmlReaderHelper(dataDir: String, proportion: DataProportion) {

  val trTag = "TRAJECTOR"
  val lmTag = "LANDMARK"
  val spTag = "SPATIALINDICATOR"
  val relationTag = "RELATION"

  lazy val reader = createXmlReader()

  def setRelationTypes(candidateRelations: List[Relation], populateNullPairs: Boolean): Unit = {

    val goldTrajectorRelations = getGoldTrajectorPairs(populateNullPairs)
    val goldLandmarkRelations = getGoldLandmarkPairs(populateNullPairs)

    candidateRelations.foreach(_.setProperty("RelationType", "None"))

    setLmSpRelationTypes(goldLandmarkRelations, candidateRelations)
    setTrSpRelationTypes(goldTrajectorRelations, candidateRelations)

    ReportHelper.reportRelationStats(candidateRelations, goldTrajectorRelations, goldLandmarkRelations)
  }

  def setTrSpRelationTypes(goldTrajectorRelations: List[Relation], candidateRelations: List[Relation]): Unit = {

    goldTrajectorRelations.foreach(r => {
      val c = candidateRelations
        .find(x =>
          x.getArgument(0).getPropertyValues(s"${trTag}_id").contains(r.getArgumentId(0)) &&
            x.getArgument(1).getPropertyValues(s"${spTag}_id").contains(r.getArgumentId(1))
        )

      if (c.nonEmpty) {
        if (c.get.getProperty("RelationType") == "TR-SP") {
          println(s"warning: candidate already marked as TR-SP via ${c.get.getId}. duplicate relation: ${r.getId}")
        } else {
          if (c.get.getProperty("RelationType") == "LM-SP") {
            println(s"warning: overriding LM-SP relation ${c.get.getId} by TR-SP relation: ${r.getId}")
          }
          c.get.setProperty("RelationType", "TR-SP")
          c.get.setId(r.getId)
        }
      } else {
        println(s"cannot find TR-SP candidate relation for ${r.getId}")
      }
    })
  }

  def setLmSpRelationTypes(goldLandmarkRelations: List[Relation], candidateRelations: List[Relation]): Unit = {

    goldLandmarkRelations.foreach(r => {
      val c = candidateRelations
        .find(x =>
          x.getArgument(0).getPropertyValues(s"${lmTag}_id").contains(r.getArgumentId(0)) &&
            x.getArgument(1).getPropertyValues(s"${spTag}_id").contains(r.getArgumentId(1))
        )

      if (c.nonEmpty) {
        if (c.get.getProperty("RelationType") == "LM-SP") {
          println(s"warning: candidate already marked as LM-SP via ${c.get.getId}. duplicate relation: ${r.getId}")
        } else {
          if (c.get.getProperty("RelationType") == "TR-SP") {
            println(s"warning: overriding TR-SP relation ${c.get.getId} by LM-SP relation: ${r.getId}")
          }
          c.get.setProperty("RelationType", "LM-SP")
          c.get.setId(r.getId)
        }
      } else {
        println(s"cannot find LM-SP candidate relation for ${r.getId}")
      }
    })
  }

  private def getGoldLandmarkPairs(populateNullPairs: Boolean): List[Relation] = {

    // create pairs which first argument is landmark and second is indicator, and remove duplicates
    val nullLandmarkIds = getTags(lmTag).filter(_.getStart == -1).map(_.getId)
    val relations = getRelations("landmark_id", "spatial_indicator_id")
      .groupBy(x => x.getArgumentId(0) + "_" + x.getArgumentId(1))
      .map { case (_, list) => list.head }
      .toList
    if (populateNullPairs) {
      relations.foreach(r => if (nullLandmarkIds.contains(r.getArgumentId(0))) r.setArgumentId(0, dummyPhrase.getId))
      relations
    } else {
      relations.filterNot(r => nullLandmarkIds.contains(r.getArgumentId(0)))
    }
  }

  private def getGoldTrajectorPairs(populateNullPairs: Boolean): List[Relation] = {

    // create pairs which first argument is trajector and second is indicator, and remove duplicates
    val nullTrajectorIds = getTags(trTag).filter(_.getStart == -1).map(_.getId)
    val relations = getRelations("trajector_id", "spatial_indicator_id")
      .groupBy(x => x.getArgumentId(0) + "_" + x.getArgumentId(1))
      .map { case (_, list) => list.head }
      .toList
    if (populateNullPairs) {
      relations.foreach(r => if (nullTrajectorIds.contains(r.getArgumentId(0))) r.setArgumentId(0, dummyPhrase.getId))
      relations
    } else {
      relations.filterNot(r => nullTrajectorIds.contains(r.getArgumentId(0)))
    }
  }

  private def getRelations(firstArgId: String, secondArgId: String): List[Relation] = {
    reader.getRelations(relationTag, firstArgId, secondArgId).toList
  }

  private def getTags(tag: String): List[NlpBaseElement] = {
    reader.getTagAsNlpBaseElement(tag).toList
  }

  def setRoles(tokenInstances: List[NlpBaseElement]): Unit = {
    val matching = tokenInstances match{
      case _: List[Token] => XmlMatchings.phraseHeadwordMatching
      case _: List[Phrase] => XmlMatchings.xmlHeadwordMatching
    }
    reader.addPropertiesFromTag(trTag, tokenInstances, matching)
    reader.addPropertiesFromTag(lmTag, tokenInstances, matching)
    reader.addPropertiesFromTag(spTag, tokenInstances, matching)
  }

  def getSentences: List[Sentence] = {
    reader.getSentences().toList
  }

  def getDocuments: List[Document] = {
    reader.getDocuments().toList
  }

  private def createXmlReader(): NlpXmlReader = {
    val path = dataDir + "sprl2017_" + (proportion match {
      case Train => "train.xml"
      case ValidationTrain => "validation_train.xml"
      case ValidationTest => "validation_test.xml"
      case Test => "gold.xml"
      case All => "all.xml"
    })
    val reader = new NlpXmlReader(path, "SCENE", "SENTENCE", null, null)
    reader.setIdUsingAnotherProperty("SCENE", "DOCNO")
    reader
  }

}
