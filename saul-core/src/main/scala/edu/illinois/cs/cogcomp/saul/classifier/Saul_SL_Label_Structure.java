package edu.illinois.cs.cogcomp.saul.classifier;

import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Parisa on 12/8/15.
 */
public class Saul_SL_Label_Structure <_> implements IStructure {

    ArrayList<String> labels;

    public Saul_SL_Label_Structure(ArrayList<ConstrainedClassifier<_, _>> l, _ x ){

        for (ConstrainedClassifier c : l){
            labels.add(c.onClassifier().discreteValue(x));
        }
    }
}
