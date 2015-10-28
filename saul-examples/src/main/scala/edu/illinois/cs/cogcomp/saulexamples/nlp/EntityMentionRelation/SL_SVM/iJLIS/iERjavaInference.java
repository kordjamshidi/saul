/*
package ml.wolfe.examples.parisa.iJLIS;

*/
/**
 * Created by parisakordjamshidi on 22/09/14.
 *//*


import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.indsup.inference.AbstractLossSensitiveStructureFinder;
import edu.illinois.cs.cogcomp.indsup.inference.IInstance;
import edu.illinois.cs.cogcomp.indsup.inference.IStructure;
import edu.illinois.cs.cogcomp.indsup.learning.WeightVector;
import gurobi.*;

import java.util.ArrayList;
import java.util.List;
//import edu.illinois.cs.cogcomp.jlistutorial.SUtils.NodeLabel;

public class iERjavaInference extends AbstractLossSensitiveStructureFinder {

    private static final long serialVersionUID = 1L;

    */
/**
     * This is for training
     *//*

    @Override

    public Pair<IStructure, Double> getLossSensitiveBestStructure(WeightVector wv, IInstance input, IStructure gold) throws Exception {
////
        ERiStructure goldStruct = (ERiStructure) gold;
        ERqInstance qi = (ERqInstance) input;
        Integer goldL = (((ERiStructure) gold).nodeValues) ? 1 : 0;
        Double coefLos= Double.valueOf((1-2*(goldL)));
//        double loss;
        ERiStructure maxC = new ERiStructure((ERqInstance)input,true);
        Double  coef=wv.dotProduct(((ERqInstance) input).fv)+coefLos;
        try{
            GRBEnv    env   = new GRBEnv("mip1.log");
            GRBModel  model = new GRBModel(env);
            //create variables
            GRBVar per=model.addVar(0,1,0.0,GRB.BINARY,"per");
            // integrate the new variables
            model.update();
            //Set objective maximize fn(x).per
            GRBLinExpr expr= new GRBLinExpr();
            expr.addTerm(coef,per);
            model.setObjective(expr,GRB.MAXIMIZE);
            //add constraints
            model.optimize();
            double x=per.get(GRB.DoubleAttr.X);
            maxC.nodeValues=(x>0.5);
            model.dispose();
            env.dispose();
        }
        catch (GRBException e)
        {
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
        }
       // Return structure with highest score+loss, and loss of this structure
        return new Pair<IStructure, Double>(maxC, getHammingLoss(maxC, goldStruct));
    }

    public Pair<IStructure, Double> getLossSensitiveBestStructureback(WeightVector wv, IInstance input, IStructure gold) throws Exception {

        ERiStructure goldStruct = (ERiStructure) gold;
        ERqInstance qi = (ERqInstance) input;

        List<ERiStructure> options = getOptions(qi);

        double max = Double.NEGATIVE_INFINITY;
        double score;
        ERiStructure maxC = null;
        double loss;

        System.out.println();
        System.out.println(gold);

        // Now get the structure with the max: score+loss
        for (ERiStructure c : options) {
            score = wv.dotProduct(c.getFeatureVector());
            loss = getHammingLoss(c, goldStruct);
            score += loss;

            System.out.println(c);
            System.out.println(c.getFeatureVector());
            System.out.println(score);

            if (score > max) {
                max = score;
                maxC = c;
            }
        }

        // Return structure with highest score+loss, and loss of this structure
        return new Pair<IStructure, Double>(maxC, getHammingLoss(maxC, goldStruct));
    }

    public double getHammingLoss(ERiStructure hyp, ERiStructure gold){
        double loss = 0;
        if (hyp.nodeValues==gold.nodeValues)
            loss=0;
        else
            loss=1;
        return loss;
    }

    */
/**
     * This is for prediction.
     *//*

    //@Override
    public IStructure getBestStructureback(WeightVector wv, IInstance input) throws Exception {
        ERqInstance qi = (ERqInstance) input;

        List<ERiStructure> options = getOptions(qi);

        double max = Double.NEGATIVE_INFINITY;
        double score;
        ERiStructure maxC = null;

        // Now get the structure with the max: score+loss
        for (ERiStructure c : options) {
            score = wv.dotProduct(c.getFeatureVector());

            if (score > max) {
                max = score;
                maxC = c;
            }
        }

        // Return structure with highest score+loss, and loss of this structure
        return maxC;
    }

    @Override
    public IStructure getBestStructure(WeightVector wv, IInstance input) throws Exception {

        ERqInstance qi = (ERqInstance) input;
        List<ERiStructure> options = getOptions(qi);

        double max = Double.NEGATIVE_INFINITY;
        //double score;
        ERiStructure maxC = new ERiStructure((ERqInstance)input,true);

        // Now get the structure with the max: score+loss
       // for (ERiStructure c : options) {
         //   score = wv.dotProduct(c.getFeatureVector());
         Double  coef=wv.dotProduct(((ERqInstance) input).fv);
           // if (score > max) {
             //   max = score;
               // maxC = c;
            //}
        //}
       /*/
/*//*
 // Return structure with highest score+loss, and loss of this structure
       // return maxC;
        try{
           GRBEnv    env   = new GRBEnv("mip1.log");
           GRBModel  model = new GRBModel(env);
           //create variables
           GRBVar per=model.addVar(0,1,0.0,GRB.BINARY,"per");
           // integrate the new variables
           model.update();
           //Set objective maximize fn(x).per
           GRBLinExpr expr= new GRBLinExpr();
           expr.addTerm(coef,per);
           model.setObjective(expr,GRB.MAXIMIZE);

           //add constraints
           model.optimize();
           double x=per.get(GRB.DoubleAttr.X);
           maxC.nodeValues=(x>0.5);
           model.dispose();
           env.dispose();
         }
       catch (GRBException e)
          {
           System.out.println("Error code: " + e.getErrorCode() + ". " +
                   e.getMessage());
           }
      return maxC;
    }
    */
/**
     * Given this QueryInstance, what are the options for responses?
     * This simple method just enumerates all possibilities. (in this
     * contrived example there are 8 total)
     *
     * @param qi
     * @return
     *//*

    public List<ERiStructure> getOptions(ERqInstance qi) {

        List<ERiStructure> options = new ArrayList();


       // val options: List[ERiStructure] = new ArrayList[ERiStructure]
        //for (i <- NodeLabel.values) {
        // for (j <- NodeLabel.values) {
        // for (k <- NodeLabel.values) {
        // val labelOptions: List[Nodevalues]= new ArrayList[Nodevalues]()
        // val a:Nodevalues=new Nodevalues()
        // a.a=true
        //labelOptions.add(a)
        //a.a=false
        //labelOptions.add(a)
        //labelOptions.add(k)
        options.add(new ERiStructure(qi, true));
        options.add(new ERiStructure(qi, false));

        return options;
    }

}
*/
