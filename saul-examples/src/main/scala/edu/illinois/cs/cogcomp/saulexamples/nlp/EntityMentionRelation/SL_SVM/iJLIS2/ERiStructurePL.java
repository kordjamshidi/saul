package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;

import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.core.IStructure;

/**
 * Created by parisakordjamshidi on 19/09/14.
 */
public class ERiStructurePL implements IStructure {

        public Labels Rlables;
        public ERqInstancePL qi;
        public FeatureVectorBuffer fvPeop=new FeatureVectorBuffer(new int[0] , new double[0]);
        public FeatureVectorBuffer fvOrg=new FeatureVectorBuffer(new int[0] , new double[0]);
        public FeatureVectorBuffer fvLoc=new FeatureVectorBuffer(new int[0] , new double[0]);
        public FeatureVectorBuffer fvOther=new FeatureVectorBuffer(new int[0] , new double[0]);
        public FeatureVectorBuffer fvO=new FeatureVectorBuffer(new int[0] , new double[0]);
        public FeatureVectorBuffer fvWorkFor=new FeatureVectorBuffer(new int[0] , new double[0]);
        public FeatureVectorBuffer fvLivesIn=new FeatureVectorBuffer(new int[0] , new double[0]);
        public FeatureVectorBuffer fv;

        public ERiStructurePL(ERqInstancePL qi, Labels Rlables) {
       // List<String> feats = new ArrayList<String>();
        this.Rlables = Rlables;
        this.qi=qi;
            //this.fv = FeatureVector.plus(this.qi.fv, this.fv);
        }
        @Override
        public String toString() {
            return "[ERiStructure: " + this.Rlables + "]";
        }

    }


