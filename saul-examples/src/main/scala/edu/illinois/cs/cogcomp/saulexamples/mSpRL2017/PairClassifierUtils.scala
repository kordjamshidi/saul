package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.{ReportHelper, SpRLXmlReader}
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{NlpBaseElement, Phrase, Relation}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval._

import scala.collection.JavaConversions._

/** Created by taher on 2017-02-26.
  */
object PairClassifierUtils {

  import MultiModalSpRLDataModel._

  def evaluate(
                predicted: List[Relation],
                dataPath: String,
                resultsDir: String,
                resultsFilePrefix: String,
                isTrain: Boolean,
                isTrajector: Boolean
              ): Seq[SpRLEvaluation] = {

    val actual = getActualRelationEvalsPhraseBased(dataPath, isTrajector)

    val name = if (isTrajector) "TrSp" else "LmSp"
    ReportHelper.reportRelationResults(resultsDir, resultsFilePrefix + s"_$name", actual, predicted, new OverlapComparer, 2)
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////
  private def getActualRelationEvalsPhraseBased(dataPath: String, isTrajector: Boolean): List[Relation] = {

    val roleName = if (isTrajector) "TRAJECTOR" else "LANDMARK"
    val reader = new SpRLXmlReader(dataPath).reader
    val relations = reader.getRelations("RELATION", s"${roleName.toLowerCase}_id", "spatial_indicator_id")

    reader.setPhraseTagName(roleName)
    val roles = reader.getPhrases().map(x => x.getId -> x).toMap

    reader.setPhraseTagName("SPATIALINDICATOR")
    val indicators = reader.getPhrases().map(x => x.getId -> x).toMap

    relations.map(r => {
      val role = roles(r.getArgumentId(0))
      val sp = indicators(r.getArgumentId(1))
      r.setArgument(0, role)
      r.setArgument(1, sp)
      r
    }).groupBy(x => x.getArgumentIds.mkString(",")).map(_._2.head).toList // remove duplicates and nulls
  }

}

