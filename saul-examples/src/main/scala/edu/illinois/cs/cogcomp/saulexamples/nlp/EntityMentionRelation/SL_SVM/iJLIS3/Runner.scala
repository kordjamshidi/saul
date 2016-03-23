package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS3

import java.util.{ArrayList, List}

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.Conll04_ReaderNew
import edu.illinois.cs.cogcomp.sl.core.{IInstance, IStructure}

/**
 * Created by Parisa on 3/22/16.
 */
object Runner {


  def readXY(cr: Conll04_ReaderNew, outlist: List[IStructure], sclist: List[IInstance], l: Int, u: Int) {
//    var i: Int = l
//    while (i < u) { //cr.instances.size){
//    val newqi: Saul_SL_java_Instance = new Saul_SL_java_Instance(List(PerConstraintClassifier),cr.instances.elementAt(i))
//      sclist.add(newqi)
//      val a: Nodevalues = new Nodevalues()
//      a.a = (cr.instances.elementAt(i).entType.toLowerCase.contains("peop"))
//      val cs: ERiStructure = new ERiStructure(newqi, a.a)
//      outlist.add(cs)
//      i = i + 1
//    }
  }

  def trainSSVM(modelname: String): Unit = {
    var cr = new Conll04_ReaderNew("./data/EntityMentionRelation/conll04.corp", "Pair")
    var sclist: List[IInstance] = new ArrayList[IInstance] //=readX(cr)
    var outlist: List[IStructure] = new ArrayList[IStructure] //readY(cr)
    readXY(cr, outlist, sclist, 1, 100)

    cr = null
  }

}
