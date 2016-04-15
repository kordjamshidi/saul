package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.SL_SVM.iJLIS;

import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;

/**
 * Created by Parisa on 10/27/15.
 */
public class ERFeatureGenerator extends AbstractFeatureGenerator {
    private static final long serialVersionUID = 1L;
    public ERFeatureGenerator(){

    }
    public IFeatureVector getFeatureVector (IInstance x, IStructure y){
        ERqInstance mx= (ERqInstance) x;
        ERiStructure my=(ERiStructure) y;
        FeatureVectorBuffer fvb= new FeatureVectorBuffer(mx.fv);
        //TODO tune fvb indexes
        return fvb.toFeatureVector();
    }
}
