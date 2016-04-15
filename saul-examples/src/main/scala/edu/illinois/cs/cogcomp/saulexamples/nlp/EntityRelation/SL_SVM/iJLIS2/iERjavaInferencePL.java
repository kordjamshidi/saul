//package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;
//
//import edu.illinois.cs.cogcomp.lbjava.infer.GurobiHook;
//import edu.illinois.cs.cogcomp.lbjava.infer.InferenceManager;
//import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation;
////import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.mYjava.work_forClassifier;
//import edu.illinois.cs.cogcomp.sl.core.AbstractInferenceSolver;
//import edu.illinois.cs.cogcomp.sl.core.IInstance;
//import edu.illinois.cs.cogcomp.sl.core.IStructure;
//import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
//import edu.illinois.cs.cogcomp.sl.util.WeightVector;
//import gurobi.*;
//import javafx.util.Pair;
//
//import java.util.Arrays;
//
//public class iERjavaInferencePL extends AbstractInferenceSolver {
//
//    private static final long serialVersionUID = 1L;
//
//
//    public Pair<IStructure, Double> getLossSensitiveBestStructure(WeightVector wv, IInstance input, IStructure gold) throws Exception {
//
//        int blockSize= RunnerPL.lexm().getNumOfFeature();
//        float[] globalWeight=wv.getWeightArray();
//        float[]  WeightPer= Arrays.copyOfRange(globalWeight, 0,blockSize-1);
//        float[]  WeightOrg=Arrays.copyOfRange(globalWeight,blockSize,2*blockSize-1);
//        float[]  WeightWF=Arrays.copyOfRange(globalWeight,2*blockSize,3*blockSize-1);
//
//        WeightVector wvPer=new WeightVector(new WeightVector(WeightPer),1);
//        WeightVector wvOrg=new WeightVector(new WeightVector(WeightOrg),1);
//        WeightVector wvWF=new WeightVector(new WeightVector(WeightWF),1);
//        ERiStructurePL goldStruct = (ERiStructurePL) gold;
//
//        Integer goldL = (((ERiStructurePL) gold).Rlables.E1Label().toLowerCase().contains("peop")) ? 1 : 0;
//        Double coefLosper1= Double.valueOf((1-2*(goldL)));
//        goldL = (((ERiStructurePL) gold).Rlables.E2Label().toLowerCase().contains("peop")) ? 1 : 0;
//        Double coefLosper2= Double.valueOf((1-2*(goldL)));
//        goldL = (((ERiStructurePL) gold).Rlables.E1Label().toLowerCase().contains("org")) ? 1 : 0;
//        Double coefLosorg1= Double.valueOf((1-2*(goldL)));
//        goldL = (((ERiStructurePL) gold).Rlables.E2Label().toLowerCase().contains("org")) ? 1 : 0;
//        Double coefLosorg2= Double.valueOf((1-2*(goldL)));
//        goldL= (((ERiStructurePL) gold).Rlables.RelLabel().toLowerCase().contains("work")) ? 1 : 0;
//        Double coefLosWF= Double.valueOf((1-2*(goldL)));
//        Labels temp=new Labels();
//
//        ERiStructurePL maxC = new ERiStructurePL(temp);
//        Double  coefper1=wvPer.dotProduct((IFeatureVector) ((ERqInstancePL) input).E1fv)+coefLosper1;
//        Double  coefper2=wvPer.dotProduct((IFeatureVector) ((ERqInstancePL) input).E2fv)+coefLosper2;
//        Double  coeforg1=wvOrg.dotProduct((IFeatureVector) ((ERqInstancePL) input).E1fv)+coefLosorg1;
//        Double  coeforg2=wvOrg.dotProduct((IFeatureVector) ((ERqInstancePL) input).E2fv)+coefLosorg2;
//        Double  coefWF=wvWF.dotProduct((IFeatureVector) ((ERqInstancePL) input).Rfv)+coefLosWF;
//
//     try{
//         GRBEnv    env   = new GRBEnv("mip1.log");
//         GRBModel  model = new GRBModel(env);
//         //create variables
//         GRBVar per1=model.addVar(0,1,0.0,GRB.BINARY,"per1");
//         GRBVar per2=model.addVar(0,1,0.0,GRB.BINARY,"per2");
//         GRBVar org1=model.addVar(0,1,0.0,GRB.BINARY,"org1");
//         GRBVar org2=model.addVar(0,1,0.0,GRB.BINARY,"org2");
//         GRBVar WF=model.addVar(0,1,0.0,GRB.BINARY,"WF");
//         // integrate the new variables
//         model.update();
//         //Set objective maximize fn(x).per
//         GRBLinExpr expr= new GRBLinExpr();
//         expr.addTerm(coefper1,per1);
//         expr.addTerm(coefper2,per2);
//         expr.addTerm(coeforg1,org1);
//         expr.addTerm(coeforg2,org2);
//         expr.addTerm(coefWF,WF);
//         model.setObjective(expr,GRB.MAXIMIZE);
//         //add constraints
//         expr=new GRBLinExpr();
//         expr.addTerm(1,per1);
//         expr.addTerm(1,org1);
//         model.addConstr(expr,GRB.LESS_EQUAL,1.0,"c1");
//
//         expr=new GRBLinExpr();
//         expr.addTerm(1,per2);
//         expr.addTerm(1,org2);
//         model.addConstr(expr,GRB.LESS_EQUAL,1.0,"c2");
//
//
//         expr=new GRBLinExpr();
//         expr.addTerm(-1,per1);
//         expr.addTerm(1,WF);
//         model.addConstr(expr,GRB.LESS_EQUAL,0.0,"c3");
//
//         expr=new GRBLinExpr();
//         expr.addTerm(-1,org2);
//         expr.addTerm(1,WF);
//         model.addConstr(expr,GRB.LESS_EQUAL,0.0,"c4");
//
//         model.optimize();
//         double x=per1.get(GRB.DoubleAttr.X);
//         //addBooleanVariabl(2.0);
//
//         java.lang.String x1=(x>0.5)? "peop":"npeop";
//         x=per2.get(GRB.DoubleAttr.X);
//         java.lang.String x2=(x>0.5)? "peop":"npeop";
//
//         if (x1.equals("npeop")){
//         x=org1.get(GRB.DoubleAttr.X);
//         x1=(x>0.5)? "org":"norg";}
//         if (x2.equals("npeop")){
//         x=org2.get(GRB.DoubleAttr.X);
//         x2=(x>0.5)? "org":"norg";}
//
//         x=WF.get(GRB.DoubleAttr.X);
//         java.lang.String x3=(x>0.5)? "work_for":"nwf";
//
//         // x=org2.get(GRB.DoubleAttr.X);
//         maxC.Rlables.set(x1,x2,x3);//.E1Label=(x>0.5)? "peop":"npeop";
//         model.dispose();
//         env.dispose();
//
//     }
//        catch (GRBException e)
//        {
//            System.out.println("Error code: " + e.getErrorCode() + ". " +
//                    e.getMessage());
//        }
//       // Return structure with highest score+loss, and loss of this structure
//        return new Pair<IStructure, Double>(maxC, ((double) getLoss( input, maxC, goldStruct)));
//    }
//
//    @Override
//    public float getLoss(IInstance iInstance, IStructure iStructure, IStructure iStructure1) {
//
//        float E1loss = 0;
//        float E2loss=0;
//        float Rloss=0;
//        ERiStructurePL hyp= ((ERiStructurePL) iStructure);
//        ERiStructurePL gold=((ERiStructurePL) iStructure1);
//        if (hyp.Rlables.E1Label().equals(gold.Rlables.E1Label()))
//            E1loss=0;
//        else
//            E1loss=1;
//
//        if (hyp.Rlables.E2Label().equals(gold.Rlables.E2Label()))
//            E2loss=0;
//        else
//            E2loss=1;
//
//
//        if (hyp.Rlables.RelLabel().equals(gold.Rlables.RelLabel()))
//            Rloss=0;
//        else
//            Rloss=1;
//        return ((E1loss+E2loss+Rloss)/3);
//  }
//  /**
//     * This is for prediction.
//     */
//
//
//  @Override
//  public IStructure getLossAugmentedBestStructure(WeightVector wv, IInstance input, IStructure iStructure) throws Exception {
//
//       int blockSize=RunnerPL.lexm().getNumOfFeature();
//        WeightVector globalWeight=new WeightVector(wv.getWeightArray());
//        WeightVector  WeightPer= new WeightVector(Arrays.copyOfRange(globalWeight.getWeightArray(), 0,blockSize-1));
//        WeightVector WeightOrg=new WeightVector(Arrays.copyOfRange(globalWeight.getWeightArray(),blockSize,2*blockSize-1));
//        WeightVector WeightWF=new WeightVector(Arrays.copyOfRange(globalWeight.getWeightArray(),2*blockSize,3*blockSize-1));
//        WeightVector wvPer=new WeightVector(WeightPer,1);
//        WeightVector wvOrg=new WeightVector(WeightOrg,1);
//        WeightVector wvWF=new WeightVector(WeightWF,1);
//        // ERqInstancePL qi = (ERqInstancePL) input;
//        // List<ERiStructurePL> options = getOptions(qi);
//
//        //  double max = Double.NEGATIVE_INFINITY;
//        //double score;
//        Labels temp=new Labels();
//        ERiStructurePL maxC = new ERiStructurePL(temp);
//
//
//        double  coefper1=wvPer.dotProduct(((ERqInstancePL) input).E1fv.toFeatureVector());
//        double  coefper2=wvPer.dotProduct(((ERqInstancePL) input).E2fv.toFeatureVector());
//        double  coeforg1=wvOrg.dotProduct(((ERqInstancePL) input).E1fv.toFeatureVector());
//        double  coeforg2=wvOrg.dotProduct(((ERqInstancePL) input).E2fv.toFeatureVector());
//        double  coefWF=wvWF.dotProduct(((ERqInstancePL) input).Rfv.toFeatureVector());
//        //try
//        {
//            GurobiHook myGurobi=new GurobiHook();
//            double coeff[]={coefper1,coefper2,coeforg1,coeforg2,coefWF};
//            int[] vars=new int[coeff.length];
//            for (int i = 0; i < coeff.length; i++) {
//                //System.out.println("adding: i : +" + coeff[i]);
//                vars[i]= myGurobi.addBooleanVariable(coeff[i]);
//
//            }
//
//
//           // GRBEnv    env   = new GRBEnv("mip1.log");
//           // GRBModel  model = new GRBModel(env); model.
//            //create variables
//           // GRBVar per1=model.addVar(0,1,0.0,GRB.BINARY,"per1");
//           // GRBVar per2=model.addVar(0,1,0.0,GRB.BINARY,"per2");
//           // GRBVar org1=model.addVar(0,1,0.0,GRB.BINARY,"org1");
//           // GRBVar org2=model.addVar(0,1,0.0,GRB.BINARY,"org2");
//           // GRBVar WF=model.addVar(0,1,0.0,GRB.BINARY,"WF");
//
//            // integrate the new variables
//           // model.update();
//            //Set objective maximize fn(x).per
//           // GRBLinExpr expr= new GRBLinExpr();
//
//          //  expr.addTerm(coefper1,per1);
//           // expr.addTerm(coefper2,per2);
//           // expr.addTerm(coeforg1,org1);
//            //expr.addTerm(coeforg2,org1);
//            //expr.addTerm(coefWF,WF);
//           // model.setObjective(expr,GRB.MAXIMIZE);
//            // per(i)=> ~org(i)
//            // work-for(i,j)=>per(i)and org(j)
//
//            //add constraints
//            double coeffc1[]={1,0,1,0,0};
//            myGurobi.addLessThanConstraint(vars,coeffc1,1);
//            double coeffc2[]={0,1,0,1,0};
//            myGurobi.addLessThanConstraint(vars,coeffc2,1);
//            double coeffc3[]={-1,0,0,0,1};
//            myGurobi.addLessThanConstraint(vars,coeffc3,0);
//            double coeffc4[]={0,0,0,-1,1};
//            myGurobi.addLessThanConstraint(vars,coeffc4,0);
//
//            // expr=new GRBLinExpr();
//           // expr.addTerm(1,per1);
//           // expr.addTerm(1,org1);
//           // model.addConstr(expr,GRB.LESS_EQUAL,1.0,"c1");
//
//            //expr=new GRBLinExpr();
//            //expr.addTerm(1,per2);
//            //expr.addTerm(1,org2);
//            //model.addConstr(expr,GRB.LESS_EQUAL,1.0,"c2");
//
//          //  expr=new GRBLinExpr();
//           // expr.addTerm(-1,per1);
//           // expr.addTerm(1,WF);
//           // model.addConstr(expr,GRB.LESS_EQUAL,0.0,"c3");
//
//          //  expr=new GRBLinExpr();
//          //  expr.addTerm(-1,org2);
//          //  expr.addTerm(1,WF);
//           // model.addConstr(expr,GRB.LESS_EQUAL,0.0,"c4");
//
//
//           // model.optimize();
//
//            myGurobi.setMaximize(true);
//            boolean solved = false;
//            try {
//                solved = myGurobi.solve();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (!solved) {
//                System.out.println("ILP solver failed");
//            //    System.exit(0);
//            }
//
//           // read the solution
//
//         //   x=myGurobi.getBooleanValue(0)per1.get(GRB.DoubleAttr.X);
//            java.lang.String x1=myGurobi.getBooleanValue(0)? "peop":"npeop";
//           // x=per2.get(GRB.DoubleAttr.X);
//            java.lang.String x2=myGurobi.getBooleanValue(1)? "peop":"npeop";
//
//            if (x1.equals("npeop")){
//               // x=org1.get(GRB.DoubleAttr.X);
//                x1=myGurobi.getBooleanValue(2)? "org":"norg";}
//            if (x2.equals("npeop")){
//               // x=org2.get(GRB.DoubleAttr.X);
//                x2=myGurobi.getBooleanValue(3)? "org":"norg";}
//           // x=WF.get(GRB.DoubleAttr.X);
//            java.lang.String x3=myGurobi.getBooleanValue(4)? "work_for":"nwf";
//
//            // x=org2.get(GRB.DoubleAttr.X);
//            maxC.Rlables.set(x1,x2,x3);//.E1Label=(x>0.5)? "peop":"npeop";
//          //  model.dispose();
//           // env.dispose();
//        }
//       // catch (GRBException e)
//       // {
//          //  System.out.println("Error code: " + e.getErrorCode() + ". " +
//        //            e.getMessage());
//      //  }
//        return maxC;
//    }
//
//
//
//
//
//
//    public IStructure getBestStructureDirectGurobi(WeightVector wv, IInstance input) throws Exception {
//        int blockSize=RunnerPL.lexm().getNumOfFeature();
//        WeightVector globalWeight=new WeightVector(wv.getWeightArray());
//        WeightVector  WeightPer= new WeightVector(Arrays.copyOfRange(globalWeight.getWeightArray(), 0,blockSize-1));
//        WeightVector  WeightOrg=new WeightVector(Arrays.copyOfRange(globalWeight.getWeightArray(),blockSize,2*blockSize-1));
//        WeightVector WeightWF=new WeightVector(Arrays.copyOfRange(globalWeight.getWeightArray(), 2 * blockSize, 3 * blockSize - 1));
//        WeightVector wvPer=new WeightVector(WeightPer,1);
//        WeightVector wvOrg=new WeightVector(WeightOrg,1);
//        WeightVector wvWF=new WeightVector(WeightWF,1);
//       // ERqInstancePL qi = (ERqInstancePL) input;
//       // List<ERiStructurePL> options = getOptions(qi);
//
//      //  double max = Double.NEGATIVE_INFINITY;
//        //double score;
//        Labels temp=new Labels();
//        ERiStructurePL maxC = new ERiStructurePL(temp);
//
//
//        float  coefper1=wvPer.dotProduct((IFeatureVector) ((ERqInstancePL) input).E1fv);
//        float  coefper2=wvPer.dotProduct((IFeatureVector) ((ERqInstancePL) input).E2fv);
//        float  coeforg1=wvOrg.dotProduct((IFeatureVector) ((ERqInstancePL) input).E1fv);
//        float  coeforg2=wvOrg.dotProduct((IFeatureVector) ((ERqInstancePL) input).E2fv);
//        float  coefWF=wvWF.dotProduct((IFeatureVector) ((ERqInstancePL) input).Rfv);
//         try{
//           GRBEnv    env   = new GRBEnv("mip1.log");
//           GRBModel  model = new GRBModel(env);
//           //create variables
//           GRBVar per1=model.addVar(0,1,0.0,GRB.BINARY,"per1");
//           GRBVar per2=model.addVar(0,1,0.0,GRB.BINARY,"per2");
//           GRBVar org1=model.addVar(0,1,0.0,GRB.BINARY,"org1");
//           GRBVar org2=model.addVar(0,1,0.0,GRB.BINARY,"org2");
//           GRBVar WF=model.addVar(0,1,0.0,GRB.BINARY,"WF");
//
//             // integrate the new variables
//           model.update();
//           //Set objective maximize fn(x).per
//           GRBLinExpr expr= new GRBLinExpr();
//           expr.addTerm(coefper1,per1);
//           expr.addTerm(coefper2,per2);
//           expr.addTerm(coeforg1,org1);
//           expr.addTerm(coeforg2,org1);
//           expr.addTerm(coefWF,WF);
//           model.setObjective(expr,GRB.MAXIMIZE);
//            // per(i)=> ~org(i)
//            // work-for(i,j)=>per(i)and org(j)
//           //add constraints
//           expr=new GRBLinExpr();
//           expr.addTerm(1,per1);
//           expr.addTerm(1,org1);
//           model.addConstr(expr,GRB.LESS_EQUAL,1.0,"c1");
//
//             expr=new GRBLinExpr();
//             expr.addTerm(1,per2);
//             expr.addTerm(1,org2);
//             model.addConstr(expr,GRB.LESS_EQUAL,1.0,"c2");
//
//             expr=new GRBLinExpr();
//             expr.addTerm(-1,per1);
//             expr.addTerm(1,WF);
//             model.addConstr(expr,GRB.LESS_EQUAL,0.0,"c3");
//
//             expr=new GRBLinExpr();
//             expr.addTerm(-1,org2);
//             expr.addTerm(1,WF);
//             model.addConstr(expr,GRB.LESS_EQUAL,0.0,"c4");
//
//
//             model.optimize();
//           // read the solution
//           double x=per1.get(GRB.DoubleAttr.X);
//           java.lang.String x1=(x>0.5)? "peop":"npeop";
//           x=per2.get(GRB.DoubleAttr.X);
//           java.lang.String x2=(x>0.5)? "peop":"npeop";
//
//           if (x1.equals("npeop")){
//                 x=org1.get(GRB.DoubleAttr.X);
//                 x1=(x>0.5)? "org":"norg";}
//           if (x2.equals("npeop")){
//                 x=org2.get(GRB.DoubleAttr.X);
//                 x2=(x>0.5)? "org":"norg";}
//             x=WF.get(GRB.DoubleAttr.X);
//             java.lang.String x3=(x>0.5)? "work_for":"nwf";
//
//             // x=org2.get(GRB.DoubleAttr.X);
//           maxC.Rlables.set(x1,x2,x3);//.E1Label=(x>0.5)? "peop":"npeop";
//           model.dispose();
//           env.dispose();
//         }
//       catch (GRBException e)
//          {
//           System.out.println("Error code: " + e.getErrorCode() + ". " +
//                   e.getMessage());
//           }
//      return maxC;
//    }
//    @Override
//    public IStructure getBestStructure(WeightVector wv, IInstance input) throws Exception { //using GurobiHook
//        int blockSize=RunnerPL.lexm().getNumOfFeature();
//        WeightVector globalWeight= new WeightVector(wv.getWeightArray());
//        WeightVector WeightPer= new WeightVector(Arrays.copyOfRange(globalWeight.getWeightArray(), 0,blockSize-1));
//        WeightVector WeightOrg= new WeightVector(Arrays.copyOfRange(globalWeight.getWeightArray(),blockSize,2*blockSize-1));
//        WeightVector WeightWF= new WeightVector(Arrays.copyOfRange(globalWeight.getWeightArray(),2*blockSize,3*blockSize-1));
//        WeightVector wvPer= new WeightVector(WeightPer,1);
//        WeightVector wvOrg= new WeightVector(WeightOrg,1);
//        WeightVector wvWF= new WeightVector(WeightWF,1);
//        JointER JE= new JointER(((ERqInstancePL) input).pair);
//        ConllRelation head = JointER.findHead((ConllRelation) ((ERqInstancePL) input).pair);
//        JointER inference = (JointER) InferenceManager.get("ml.wolfe.examples.parisa.iJLIS2.JointER", head);
//
//        if (inference == null)
//        {
//            inference = new JointER(head);
//            InferenceManager.put(inference);
//        }
//
////        String result = null;
////        work_forClassifier __work_forClassifier = new work_forClassifier();
////        try {
////            result = inference.valueOf((__work_forClassifier), ((ERqInstancePL) input).pair);
////        }
////        catch (Exception e)
////        {
////            System.err.println("LBJ ERROR: Fatal error while evaluating classifier RelArgsClassifier: " + e);
////            e.printStackTrace();
////            System.exit(1);
////        }
//
//        //return result;
//        //JointER$subjectto JEc=new JointER$subjectto();
//        //JEc.makeConstraint(((ERqInstancePL) input).pair);
//        //((ERqInstancePL) input).pair
//        // ERqInstancePL qi = (ERqInstancePL) input;
//        // List<ERiStructurePL> options = getOptions(qi);
//
//        //  double max = Double.NEGATIVE_INFINITY;
//        //double score;
//        Labels temp=new Labels();
//        ERiStructurePL maxC = new ERiStructurePL(temp);
/////////
//
//
//        double  coefper1=wvPer.dotProduct((IFeatureVector) ((ERqInstancePL) input).E1fv);
//        double  coefper2=wvPer.dotProduct((IFeatureVector) ((ERqInstancePL) input).E2fv);
//        double  coeforg1=wvOrg.dotProduct((IFeatureVector) ((ERqInstancePL) input).E1fv);
//        double  coeforg2=wvOrg.dotProduct((IFeatureVector) ((ERqInstancePL) input).E2fv);
//        double  coefWF=wvWF.dotProduct((IFeatureVector) ((ERqInstancePL) input).Rfv);
//        //try
//        {
//            GurobiHook myGurobi=new GurobiHook();
//            double coeff[]={coefper1,coefper2,coeforg1,coeforg2,coefWF};
//            int[] vars=new int[coeff.length];
//            for (int i = 0; i < coeff.length; i++) {
//                //System.out.println("adding: i : +" + coeff[i]);
//                vars[i]= myGurobi.addBooleanVariable(coeff[i]);
//
//            }
//
//
//            // GRBEnv    env   = new GRBEnv("mip1.log");
//            // GRBModel  model = new GRBModel(env); model.
//            //create variables
//            // GRBVar per1=model.addVar(0,1,0.0,GRB.BINARY,"per1");
//            // GRBVar per2=model.addVar(0,1,0.0,GRB.BINARY,"per2");
//            // GRBVar org1=model.addVar(0,1,0.0,GRB.BINARY,"org1");
//            // GRBVar org2=model.addVar(0,1,0.0,GRB.BINARY,"org2");
//            // GRBVar WF=model.addVar(0,1,0.0,GRB.BINARY,"WF");
//
//            // integrate the new variables
//            // model.update();
//            //Set objective maximize fn(x).per
//            // GRBLinExpr expr= new GRBLinExpr();
//
//            //  expr.addTerm(coefper1,per1);
//            // expr.addTerm(coefper2,per2);
//            // expr.addTerm(coeforg1,org1);
//            //expr.addTerm(coeforg2,org1);
//            //expr.addTerm(coefWF,WF);
//            // model.setObjective(expr,GRB.MAXIMIZE);
//            // per(i)=> ~org(i)
//            // work-for(i,j)=>per(i)and org(j)
//
//            //add constraints
//            double coeffc1[]={1,0,1,0,0};
//            myGurobi.addLessThanConstraint(vars,coeffc1,1);
//            double coeffc2[]={0,1,0,1,0};
//            myGurobi.addLessThanConstraint(vars,coeffc2,1);
//            double coeffc3[]={-1,0,0,0,1};
//            myGurobi.addLessThanConstraint(vars,coeffc3,0);
//            double coeffc4[]={0,0,0,-1,1};
//            myGurobi.addLessThanConstraint(vars,coeffc4,0);
//
//            // expr=new GRBLinExpr();
//            // expr.addTerm(1,per1);
//            // expr.addTerm(1,org1);
//            // model.addConstr(expr,GRB.LESS_EQUAL,1.0,"c1");
//
//            //expr=new GRBLinExpr();
//            //expr.addTerm(1,per2);
//            //expr.addTerm(1,org2);
//            //model.addConstr(expr,GRB.LESS_EQUAL,1.0,"c2");
//
//            //  expr=new GRBLinExpr();
//            // expr.addTerm(-1,per1);
//            // expr.addTerm(1,WF);
//            // model.addConstr(expr,GRB.LESS_EQUAL,0.0,"c3");
//
//            //  expr=new GRBLinExpr();
//            //  expr.addTerm(-1,org2);
//            //  expr.addTerm(1,WF);
//            // model.addConstr(expr,GRB.LESS_EQUAL,0.0,"c4");
//
//
//            // model.optimize();
//
//            myGurobi.setMaximize(true);
//            boolean solved = false;
//            try {
//                solved = myGurobi.solve();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (!solved) {
//                System.out.println("ILP solver failed");
//                //    System.exit(0);
//            }
//
//            // read the solution
//
//            //   x=myGurobi.getBooleanValue(0)per1.get(GRB.DoubleAttr.X);
//            java.lang.String x1=myGurobi.getBooleanValue(0)? "peop":"npeop";
//            // x=per2.get(GRB.DoubleAttr.X);
//            java.lang.String x2=myGurobi.getBooleanValue(1)? "peop":"npeop";
//
//            if (x1.equals("npeop")){
//                // x=org1.get(GRB.DoubleAttr.X);
//                x1=myGurobi.getBooleanValue(2)? "org":"norg";}
//            if (x2.equals("npeop")){
//                // x=org2.get(GRB.DoubleAttr.X);
//                x2=myGurobi.getBooleanValue(3)? "org":"norg";}
//            // x=WF.get(GRB.DoubleAttr.X);
//            java.lang.String x3=myGurobi.getBooleanValue(4)? "work_for":"nwf";
//
//            // x=org2.get(GRB.DoubleAttr.X);
//            maxC.Rlables.set(x1,x2,x3);//.E1Label=(x>0.5)? "peop":"npeop";
//            //  model.dispose();
//            // env.dispose();
//        }
//        // catch (GRBException e)
//        // {
//        //  System.out.println("Error code: " + e.getErrorCode() + ". " +
//        //            e.getMessage());
//        //  }
//        return maxC;
//    }
//
//
//
//    /*public FirstOrderConstraint makeConstraint(Object __example)
//    {
//        if (!(__example instanceof ConllRelation))
//        {
//            String type = __example == null ? "null" : __example.getClass().getName();
//            System.err.println("Constraint 'PersonWorkFor(ConllRelation)' defined on line 113 of LALModel.lbj received '" + type + "' as input.");
//            new Exception().printStackTrace();
//            System.exit(1);
//        }
//
//        ConllRelation t = (ConllRelation) __example;
//        infer.FirstOrderConstraint __result = new infer.FirstOrderConstant(true);
//
//        {
//            FirstOrderConstraint LBJ2$constraint$result$0 = null;
//            {
//                FirstOrderConstraint LBJ2$constraint$result$1 = null;
//                LBJ2$constraint$result$1 = new FirstOrderEqualityWithValue(true, new FirstOrderVariable(__work_forClassifier, t), "" + (true));
//                FirstOrderConstraint LBJ2$constraint$result$2 = null;
//                LBJ2$constraint$result$2 = new FirstOrderEqualityWithValue(true, new FirstOrderVariable(PersonClassifier, t.e1), "" + (true));
//                LBJ2$constraint$result$0 = new FirstOrderImplication(LBJ2$constraint$result$1, LBJ2$constraint$result$2);
//            }
//            __result = new FirstOrderConjunction(__result, LBJ2$constraint$result$0);
//        }
//
//        return __result;
//    }
//*/
//    public iERjavaInferencePL clone()  {
//        return new iERjavaInferencePL();
//    }
//}
