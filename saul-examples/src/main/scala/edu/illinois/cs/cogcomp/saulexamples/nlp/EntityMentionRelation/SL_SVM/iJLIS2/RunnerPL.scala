package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2

import _root_.java.util

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.Conll04_ReaderNew
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS.SimpleModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.ERqInstancePL
import edu.illinois.cs.cogcomp.sl.core.{IInstance, IStructure}
import edu.illinois.cs.cogcomp.sl.util.{Lexiconer, WeightVector}


object RunnerPL {
  /**
   * This modifies the two lists given two it. They are initialized.
   *
  // * @param sclist
  // * @param outlist
  // * @param fname
   * @throws java.io.FileNotFoundException
   */
var lexm: Lexiconer = new Lexiconer()
  /*def readX(cr:Conll04_InstanceReader):List[IInstance]={
    var i: Int = 0
    val sclist: List[IInstance]=new ArrayList[IInstance]
    while (i < 100){//cr.instances.size){
      val newqi: ERqInstance = new ERqInstance(cr.instances.elementAt(i))
      sclist.add(newqi)
      i=i+1
      }
     sclist
    }*/
  def readXY(cr:Conll04_ReaderNew, outlist: List[IStructure], sclist: List[IInstance], l:Int,u:Int){

    var i= l
    while (i < u){//cr.instances.size){
      val newqi: ERqInstancePL = new ERqInstancePL(cr.relations.elementAt(i))
      sclist.add(newqi)
      var a: Labels=new Labels()
      val pair=cr.relations.elementAt(i);
      a.E1Label=pair.s.sentTokens.elementAt(pair.wordId1).entType.toLowerCase();
      a.E2Label=pair.s.sentTokens.elementAt(pair.wordId2).entType.toLowerCase();
      a.RelLabel=pair.relType.toLowerCase();
      val cs: ERiStructurePL = new ERiStructurePL(newqi, a)
      outlist.add(cs)
      i=i+1
    }
  }
  /*def readY(cr:Conll04_InstanceReader):List[IStructure]={
    var i: Int = 0
    val outlist: List[IStructure]=new util.ArrayList[IStructure]
    while (i < 100){//cr.instances.size){
    val a: Nodevalues=new Nodevalues()
     a.a=(cr.instances.elementAt(i).entType.toLowerCase.contains("peop"))
    val cs: ERiStructure = new ERiStructure(cr.instances.elementAt(i), a.a)
    outlist.add(cs)
    i=i+1
    }
    outlist
  }
*/

  def trainSSVM(modelname: String, cr:Conll04_ReaderNew): String = {

    var sclist: List[IInstance]= new util.ArrayList[IInstance]//=readX(cr)
    var outlist: List[IStructure] = new util.ArrayList[IStructure]//readY(cr)
    readXY(cr,outlist,sclist,0,10)
    //cr=null
  //  val fname: String = "/Users/parisakordjamshidi/wolfe-0.1.0/wolfe-examples/src/main/scala/ml/wolfe/examples/parisa/iJLIS/namedata-train"
    lexm.setAllowNewFeatures(false)
    val sp: StructuredProblem = new StructuredProblem
    sp.input_list = sclist
    sp.output_list = outlist
    val para: JLISParameters = new JLISParameters
    para.total_number_features = 3*lexm.totalNumofFeature
    para.c_struct = 1
    para.check_inference_opt = false
    val si: iERjavaInferencePL = new iERjavaInferencePL
    val learner: L2LossJLISLearner = new L2LossJLISLearner
    val learned_wv: WeightVector = learner.trainStructuredSVM(si, sp, para)
    val model: SimpleModel = new SimpleModel
    model.wv = learned_wv
    model.s_finder = new iERjavaInferencePL
    model.lm = lexm
    val iom: JLISModelIOManager = new JLISModelIOManager
    iom.saveModel(model, modelname)
    return modelname
  }

  def testSequenceSSVM(model_name: String,cr:Conll04_ReaderNew) {
    val iom: JLISModelIOManager = new JLISModelIOManager
    val model: SimpleModel = iom.loadModel(model_name).asInstanceOf[SimpleModel]
    RunnerPL.lexm = model.lm
    val learned_wv: WeightVector = model.wv
    val inference_proc: iERjavaInferencePL = model.s_finder.asInstanceOf[iERjavaInferencePL]
   // var cr = new Conll04_RelationReader("/Users/parisakordjamshidi/wolfe-0.1.0/LBJ/data/conll04.corp")
    var sclist: List[IInstance]= new util.ArrayList[IInstance]//=readX(cr)
    var outlist: List[IStructure] = new util.ArrayList[IStructure]//readY(cr)
    readXY(cr,outlist,sclist,11,20)
  //  cr=null

   // var sclist: List[IInstance]=readX(cr)
    //var outlist: List[IStructure] = readY(cr)
   // val sclist: List[IInstance] = new ArrayList[IInstance]
    //val outlist: List[IStructure] = new ArrayList[IStructure]
   // val fname: String = "/Users/parisakordjamshidi/wolfe-0.1.0/wolfe-examples/src/main/scala/ml/wolfe/examples/parisa/iJLIS/namedata-test"
    //readData(sclist, outlist, fname)
    val sp: StructuredProblem = new StructuredProblem
    sp.input_list = sclist
    sp.output_list = outlist
    printTestACC(sp, inference_proc, learned_wv)
  }
  private def printTestACC(sp: StructuredProblem, s_finder: AbstractLossSensitiveStructureFinder, ssvm_wv: WeightVector) {
    var tp: Double = 0
    var fp: Double = 0
    var tn: Double = 0
    var fn: Double = 0
    var total: Double = 0.0

      var i: Int = 0
      while (i < sp.input_list.size) {
          val gold: ERiStructurePL = sp.output_list.get(i).asInstanceOf[ERiStructurePL]
          val prediction:ERiStructurePL = s_finder.getBestStructure(ssvm_wv, sp.input_list.get(i)).asInstanceOf[ERiStructurePL]
          System.out.println(prediction)
          //{
            var j: Int = 0

           // while (j < gold.nodeValues.size) {
             // {
            val tempGold= gold.Rlables.LinirizLabels()
            val tempPred=prediction.Rlables.LinirizLabels()
            while (j<tempGold.size) {
                var k: Int = 0
                while (k<tempGold(j).size){
                val pred: Boolean = tempPred(j)(k)//prediction.nodeValues
                val goldval: Boolean = tempGold(j)(k)//d.nodeValues
                if (goldval == pred) {
                  if (pred == true) {
                    tp += 1.0
                  }
                  else {
                    tn += 1.0
                  }
                }
                else {
                  if (pred ==false) {
                    fp += 1.0
                  }
                  else {
                    fn += 1.0
                  }
                }
                total += 1.0
                  k=k+1
                }
             j=j+1
      }
            i=i+1

    }
    val precision: Double = tp / (tp + fp)
    val recall: Double = tp / (tp + fn)
    val F1: Double = 2 * precision * recall / (precision + recall)
    println("=========================")
    println("      P        R     F   ")
   // println(String.format("      %-5s %-5s %-5s", "P", "R", "F1"))
    println(precision, recall, F1)//String.format("VALIANT : %4.3f %4.3f %4.3f", precision, recall, F1))
    println("=========================")
    println("Acc = " + (tp + tn) / total)
  }

  def main(args: Array[String]) {
    var cr = new Conll04_ReaderNew("/Users/parisakordjamshidi/wolfe-0.1.0/wolfe-examples/src/main/scala/ml/wolfe/examples/parisa/conll04.corp","Pair")
    val modelname: String = "mytest1.ssvm.model"
    trainSSVM(modelname,cr)
    System.out.println("\n=== NOW TESTING ===")
    testSequenceSSVM(modelname,cr)
  }

}
