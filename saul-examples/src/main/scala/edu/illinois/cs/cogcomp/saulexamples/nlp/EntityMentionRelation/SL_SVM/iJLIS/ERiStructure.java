package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS;

import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by parisakordjamshidi on 19/09/14.
 */
public class ERiStructure implements IStructure {

        public Boolean nodeValues;
        public ERqInstance qi;
        private IFeatureVector fv;

        public ERiStructure(ERqInstance qi,Boolean nodeValues) {
            List<String> feats = new ArrayList<String>();
            this.nodeValues = nodeValues;
           // List<String> feats2=new ArrayList<String>();
            //feats.add(tok.entType);
            // do something with node names to make features
           // if (nodeValues==true)
            {
                this.qi=qi;
                this.fv =this.qi.fv; //only if the lable is one the features are considered otherwise all of them turned to zero
              //  nodeValues=(true);
            }
           // else {
           //       this.fv=SUtils.makeFeatures(feats);

              //    nodeValues=(false);
           // }

            //this.fv = FeatureVector.plus(this.qi.fv, this.fv);
        }

//        @Override
//        public IFeatureVector getFeatureVector() {
//            List<String> feats = new ArrayList<String>();
//            if (nodeValues==true)
//                        return this.fv;
//            else
//               return SUtils.makeFeatures(feats);
//        }

    public String toString() { return "" + this.nodeValues;  }

    }



