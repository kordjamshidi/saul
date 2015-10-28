package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS;

import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the X instance for the prediction problem
 * 
 * @author mayhew2
 * 
 */
public class QueryInstance implements IInstance {

	List<String> nodeNames; // order matters!

	IFeatureVector fv;

	public QueryInstance(List<String> nodeNames) {
		this.nodeNames = nodeNames;

		List<String> feats = new ArrayList<String>();

		// do something with node names to make features

		int i = 0;
		for(String n : nodeNames){
			feats.add("num" + i + "vow:" + SUtils.numVowels(n));
			int j = 0;
			for(char c : n.toCharArray()){
				feats.add("isvowpos:" + i + j + ":" + SUtils.isVowel(c));
				j++;
			}
			i++;
		}
		
		this.fv = SUtils.makeFeatures(feats);
	}

}
