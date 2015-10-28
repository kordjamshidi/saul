package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2


import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;
import edu.illinois.cs.cogcomp.sl.core.IStructure;

/**
 * Created by parisakordjamshidi on 19/09/14.
 */
public class ERiStructurePL implements IStructure {

        public Labels Rlables;
        public ERqInstancePL qi;
        private FeatureVector fvPeop=new FeatureVector(new int[0] , new double[0]);
        private FeatureVector fvOrg=new FeatureVector(new int[0] , new double[0]);
        private FeatureVector fvLoc=new FeatureVector(new int[0] , new double[0]);
        private FeatureVector fvOther=new FeatureVector(new int[0] , new double[0]);
        private FeatureVector fvO=new FeatureVector(new int[0] , new double[0]);
        private FeatureVector fvWorkFor=new FeatureVector(new int[0] , new double[0]);
        private FeatureVector fvLivesIn=new FeatureVector(new int[0] , new double[0]);
        private FeatureVector fv;

        public ERiStructurePL(ERqInstancePL qi, Labels Rlables) {
       // List<String> feats = new ArrayList<String>();
        this.Rlables = Rlables;
        this.qi=qi;
            //this.fv = FeatureVector.plus(this.qi.fv, this.fv);
        }

        @Override
        public FeatureVector getFeatureVector() {
         //   List<String> feats = new ArrayList<String>();
            if (Rlables.E1Label().toLowerCase().contains("peop"))
                    fvPeop=qi.E1fv;
            if (Rlables.E2Label().toLowerCase().contains("peop"))
                    fvPeop= FeatureVector.plus(fvPeop,qi.E2fv);

            if (Rlables.E1Label().toLowerCase().contains("org"))
                fvOrg=qi.E1fv;
            if (Rlables.E2Label().toLowerCase().contains("org"))
                fvOrg=FeatureVector.plus(fvOrg,qi.E2fv);


            fvOrg=fvOrg.copyWithShift(RunnerPL.lexm().totalNumofFeature());
            fv=FeatureVector.plus(fvPeop,fvOrg);

            if(Rlables.RelLabel().toLowerCase().contains("work"))
                fvWorkFor=qi.Rfv;
            fvWorkFor=fvWorkFor.copyWithShift(RunnerPL.lexm().totalNumofFeature()*2);

            fv=FeatureVector.plus(fv,fvWorkFor);

            return this.fv;


          /*  if (Rlables.E1Label().contains("loc"))
                fvLoc=qi.E1fv;
            if (Rlables.E2Label().contains("loc"))
                fvLoc=FeatureVector.plus(fvLoc,qi.E2fv);

            if (Rlables.E1Label().contains("other"))
                  fvOther=qi.E2fv;
            if (Rlables.E2Label().contains("other"))
                fvOther=FeatureVector.plus(fvOther,qi.E2fv);
            if (Rlables.E1Label().equalsIgnoreCase("O"))
                fvO=qi.E1fv;
            if (Rlables.E2Label().equalsIgnoreCase("O"))
                fvO=FeatureVector.plus(fvO,qi.E2fv);


            if (Rlables.RelLabel().contains("Live-in"))
                fvLivesIn=qi.Rfv;
            if (Rlables.RelLabel().contains("work-for"))
                fvWorkFor=qi.Rfv;*/
           //return




        }

        @Override
        public String toString() {
            return "[ERiStructure: " + this.Rlables + "]";
        }

    }


