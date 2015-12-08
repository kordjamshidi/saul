package edu.illinois.cs.cogcomp.saul.classifier;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Parisa on 12/4/15.
 */
public class Saul_SL_Instance<_> implements IInstance {

    List<Object[]> inputFeatures;
    public Saul_SL_Instance(ArrayList<ConstrainedClassifier<_, _>> l, _ x ){
        {
            for (ConstrainedClassifier c :  l)
            {
                Classifier oracle = c.onClassifier().getLabeler();
                ArrayList<_> cands= (ArrayList<_>) c.getCandidates(x);
                for (_ ci: cands){
                    c.classifier().discreteValue(ci) ; //prediction result
                    oracle.discreteValue(ci) ; // true lable
                     ;  // return a Feature values and indexs
                    inputFeatures.add(c.onClassifier().getExampleArray(ci));

//                    val a0 = a(0).asInstanceOf[Array[Int]]
//                    val a1 = a(1).asInstanceOf[Array[Double]]
                }
            }

        }
    }
}
