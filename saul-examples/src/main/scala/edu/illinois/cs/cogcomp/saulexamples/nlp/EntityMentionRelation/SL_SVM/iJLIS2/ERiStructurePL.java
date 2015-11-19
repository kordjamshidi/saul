package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;

/**
 * Created by parisakordjamshidi on 19/09/14.
 */

import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;

public class ERiStructurePL implements IStructure {

    public Labels Rlables;
   // public ERqInstancePL qi;
   public FeatureVectorBuffer fvPeop =new FeatureVectorBuffer(new int[0] , new double[0]);
   public FeatureVectorBuffer fvOrg=new FeatureVectorBuffer(new int[0] , new double[0]);
//    public FeatureVectorBuffer fvLoc=new FeatureVectorBuffer(new int[0] , new double[0]);
//    public FeatureVectorBuffer fvOther=new FeatureVectorBuffer(new int[0] , new double[0]);
//    public FeatureVectorBuffer fvO=new FeatureVectorBuffer(new int[0] , new double[0]);
      public FeatureVectorBuffer fvWorkFor=new FeatureVectorBuffer(new int[0] , new double[0]);
//    public FeatureVectorBuffer fvLivesIn=new FeatureVectorBuffer(new int[0] , new double[0]);
//
    public ERiStructurePL(Labels Rlables) {
        this.Rlables = Rlables;
        // this.Rlables.set(Rlables.E1Label(),Rlables.E2Label(),Rlables.RelLabel());
        //this.qi=qi;
    }
    @Override
    public String toString() {
        return "[ERiStructure: " + this.Rlables + "]";
    }
}


