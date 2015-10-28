package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS

import java.util
import java.util.{ArrayList, List}

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.Conll04_ReaderNew
import edu.illinois.cs.cogcomp.sl.applications.cs_multiclass._
import edu.illinois.cs.cogcomp.sl.core._
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory
import edu.illinois.cs.cogcomp.sl.util.{Lexiconer, WeightVector}


object Runner {
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
  def readXY(cr:Conll04_ReaderNew, outlist: => List[IStructure], sclist: => List[IInstance], l:Int,u:Int){
    var i: Int = l
    while (i < u){//cr.instances.size){
      val newqi: ERqInstance = new ERqInstance(cr.instances.elementAt(i))
      sclist.add(newqi)
      val a: Nodevalues=new Nodevalues()
      a.a=(cr.instances.elementAt(i).entType.toLowerCase.contains("peop"))
      val cs: ERiStructure = new ERiStructure(newqi, a.a)
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

  def trainSSVM(modelname: String): String = {



    var cr = new Conll04_ReaderNew("/Users/parisakordjamshidi/wolfe-0.1.0/LBJ/data/conll04.corp","Pair")
    var sclist: List[IInstance]= new ArrayList[IInstance]//=readX(cr)
    var outlist: List[IStructure] = new ArrayList[IStructure]//readY(cr)
    readXY(cr,outlist,sclist,1,100)
    cr=null
    lexm.setAllowNewFeatures(false)
    val sp: SLProblem = new SLProblem
    sp.instanceList = sclist
    sp.goldStructureList = outlist
    val para: SLParameters = new SLParameters
    para.TOTAL_NUMBER_FEATURE = lexm.getNumOfFeature
    para.C_FOR_STRUCTURE = 1
    para.CHECK_INFERENCE_OPT = false
    val si: iERinference = new iERinference
    val model: SLModel = new SLModel
    model.infSolver= si
    model.featureGenerator= ERFeatureGenerator
    model.lm = lexm
    model.saveModel(modelname);
    model.config = new util.HashMap();
    model.para=para

   // LabeledMultiClassData sp = MultiClassIOManager.readTrainingData(trainingDataPath);
    model.infSolver = new iERinference;
    model.featureGenerator = new MultiClassFeatureGenerator();
    val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para);
    model.wv = learner.train(sp);
    return modelname
  }

  def testSequenceSSVM(model_name: String) {
    val model: SLModel = SLModel.loadModel(model_name)
    Runner.lexm = model.lm
    val learned_wv: WeightVector = model.wv
    val inference_proc: iERinference = model.infSolver.asInstanceOf[iERinference]
    var cr = new Conll04_ReaderNew("/Users/parisakordjamshidi/wolfe-0.1.0/LBJ/data/conll04.corp","Pair")
    var sclist: List[IInstance]= new ArrayList[IInstance]//=readX(cr)
    var outlist: List[IStructure] = new ArrayList[IStructure]//readY(cr)
    readXY(cr,outlist,sclist,101,200)
    cr=null

   // var sclist: List[IInstance]=readX(cr)
    //var outlist: List[IStructure] = readY(cr)
   // val sclist: List[IInstance] = new ArrayList[IInstance]
    //val outlist: List[IStructure] = new ArrayList[IStructure]
   // val fname: String = "/Users/parisakordjamshidi/wolfe-0.1.0/wolfe-examples/src/main/scala/ml/wolfe/examples/parisa/iJLIS/namedata-test"
    //readData(sclist, outlist, fname)
    val sp: SLProblem = new SLProblem
    sp.instanceList = sclist
    sp.goldStructureList = outlist
    printTestACC(sp, inference_proc, learned_wv)
  }
  private def printTestACC(sp: SLProblem, s_finder: AbstractInferenceSolver, ssvm_wv: WeightVector) {
    var tp: Double = 0
    var fp: Double = 0
    var tn: Double = 0
    var fn: Double = 0
    var total: Double = 0.0

      var i: Int = 0
      while (i < sp.instanceList.size) {
          val gold: ERiStructure = sp.goldStructureList.get(i).asInstanceOf[ERiStructure]
          val prediction:ERiStructure = s_finder.getBestStructure(ssvm_wv, sp.instanceList.get(i)).asInstanceOf[ERiStructure]
          System.out.println(prediction)
          //{
            //var j: Int = 0
           // while (j < gold.nodeValues.size) {
             // {
                val pred: Boolean = prediction.nodeValues
                val goldval: Boolean = gold.nodeValues
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
    val modelname: String = "mytest.ssvm.model"
    trainSSVM(modelname)
    System.out.println("\n=== NOW TESTING ===")
    testSequenceSSVM(modelname)
  }

}
