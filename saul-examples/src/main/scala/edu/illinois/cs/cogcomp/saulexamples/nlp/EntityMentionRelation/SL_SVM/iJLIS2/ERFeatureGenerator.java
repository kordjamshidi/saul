package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;

import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS.ERiStructure;
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS.ERqInstance;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;

/**
 * Created by Parisa on 11/16/15.
 */
public class ERFeatureGenerator extends AbstractFeatureGenerator {
     private static final long serialVersionUID = 1L;
        public ERFeatureGenerator(){

        }
        public IFeatureVector getFeatureVector (IInstance x, IStructure y){
            ERqInstancePL mx= (ERqInstancePL) x;
            ERiStructurePL my=(ERiStructurePL) y;
            if (my.Rlables.E1Label().toLowerCase().contains("peop"))
                my.fvPeop=mx.E1fv;
            if (my.Rlables.E2Label().toLowerCase().contains("peop"))
            {
                 (my.fvPeop).addFeature(mx.E2fv);}

            if (my.Rlables.E1Label().toLowerCase().contains("org"))
                my.fvOrg=mx.E1fv;
            if (my.Rlables.E2Label().toLowerCase().contains("org"))
                my.fvOrg.addFeature(mx.E2fv);


            my.fvOrg.shift(RunnerPL.lexm().getNumOfFeature());
            my.fv.addFeature(my.fvPeop);
            my.fv.addFeature(my.fvOrg);

            if(my.Rlables.RelLabel().toLowerCase().contains("work"))
                my.fvWorkFor=mx.Rfv;
            my.fvWorkFor.shift((RunnerPL.lexm().getNumOfFeature()*2));

            my.fv.addFeature(my.fvWorkFor);

           // return my.fv;

          //  FeatureVectorBuffer fvb= new FeatureVectorBuffer(my.fv);
            //TODO tune fvb indexes
            return my.fv.toFeatureVector();
        }
    }


//   List<String> feats = new ArrayList<String>();


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


