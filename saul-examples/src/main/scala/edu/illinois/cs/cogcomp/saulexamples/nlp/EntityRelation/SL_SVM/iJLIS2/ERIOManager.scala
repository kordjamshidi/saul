package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.SL_SVM.iJLIS2

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.{ Conll04_Reader }
import edu.illinois.cs.cogcomp.sl.core.SLProblem

object ERIOManager {

  def readXY(cr: Conll04_Reader, l: Int, u: Int): SLProblem = {
    var sp: SLProblem = new SLProblem()
    var i = l
    while (i < u) {
      val newqi: ERqInstancePL = new ERqInstancePL(cr.relations.elementAt(i))
      var a: Labels = new Labels()
      val pair = cr.relations.elementAt(i)
      a.E1Label = pair.s.sentTokens.elementAt(pair.wordId1).entType.toLowerCase
      a.E2Label = pair.s.sentTokens.elementAt(pair.wordId2).entType.toLowerCase
      a.RelLabel = pair.relType.toLowerCase
      val cs: ERiStructurePL = new ERiStructurePL(a)
      i = i + 1
      sp.addExample(newqi, cs)
    }
    sp
  }
}
