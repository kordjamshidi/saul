package edu.illinois.cs.cogcomp.saul.classifier.SL_model;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Parisa on 12/4/15.
 */
public class Saul_SL_java_Instance<_, HEAD> implements IInstance {

    List<Object[]> inputFeatures;
    List<ConstrainedClassifier> factorClassifiers;

    public Saul_SL_java_Instance(List<ConstrainedClassifier<_, HEAD>> l, HEAD x){
        {
            for (ConstrainedClassifier c :  l)
            {
                Classifier oracle = c.onClassifier().getLabeler();
                List<_> cands= (List<_>) c.getCandidates(x);
                for (_ ci: cands){
                    c.classifier().discreteValue(ci) ; //prediction result
                    oracle.discreteValue(ci) ; // true lable
                     ;  // return a Feature values and indexs
                    inputFeatures.add(c.onClassifier().getExampleArray(ci));
                    factorClassifiers.add(c);
                    }
                    ;

//                    val a0 = a(0).asInstanceOf[Array[Int]]
//                    val a1 = a(1).asInstanceOf[Array[Double]]
                }
            }

        }
    }

