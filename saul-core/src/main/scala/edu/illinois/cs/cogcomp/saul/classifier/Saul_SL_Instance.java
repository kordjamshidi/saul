package edu.illinois.cs.cogcomp.saul.classifier;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Parisa on 12/4/15.
 */
public class Saul_SL_Instance<U,T> implements IInstance {


    public Saul_SL_Instance(ArrayList<ConstrainedClassifier<U, T>> l, T x ){
        {
            for (ConstrainedClassifier c :  l)
            {
                Classifier oracle = c.onClassifier().getLabeler();
                ArrayList<T> cands= (ArrayList<T>) c.getCandidates(x);
                for (T ci: cands){
                    c.classifier().discreteValue(ci) ; //prediction result
                    oracle.discreteValue(ci) ; // true lable
                    c.onClassifier().getExampleArray(ci) ;  // return a Feature values and indexs
//                    val a0 = a(0).asInstanceOf[Array[Int]]
//                    val a1 = a(1).asInstanceOf[Array[Double]]
                }
            }

        }
    }
}
