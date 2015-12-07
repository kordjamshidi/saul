package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.sl.core.SLProblem
/**
 * Created by Parisa on 12/4/15.
 */
object SL_IOManager {
   def makeSLProblem [HEAD<: AnyRef](dm : DataModel, list: List[ConstrainedClassifier[_, HEAD]]): SLProblem ={
     var sp : SLProblem = new SLProblem()
     val allHeads= dm.getNodeWithType[HEAD].getAllInstances
     allHeads.foreach( x => new Saul_SL_Instance(list.asInstanceOf,x))
    // val input: Saul_SL_Instance = new Saul_SL_Instance(list,)
   sp
   }
}
