package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS;


import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;

import java.util.ArrayList;
import java.util.List;

//import edu.illinois.cs.cogcomp.jlistutorial.SUtils.NodeLabel;

/**
 * This is the Y instance for the prediction problem
 * 
 * @author mayhew2
 * 
 */
public class GraphStructure implements IStructure {

	public List<SUtils.NodeLabel> nodeValues;
	public QueryInstance qi;
	private IFeatureVector fv;

	public GraphStructure(QueryInstance qi, List<SUtils.NodeLabel> nodeValues) {
		this.nodeValues = nodeValues;
		this.qi = qi;
		List<String> feats = new ArrayList<String>();

		int i = 0;
		for(String n : qi.nodeNames){
			feats.add("num" + i + "vow:" + SUtils.numVowels(n) + ":" + nodeValues.get(i));
			int j = 0;
			for(char c : n.toCharArray()){
				feats.add("isvowpos:" + i + j + ":" + SUtils.isVowel(c) + ":" + nodeValues.get(i));
				j++;
			}
			i++;
		}

		this.fv = SUtils.makeFeatures(feats);
		//this.fv = IFeatureVector.plus(this.qi.fv, this.fv);
	}


	public IFeatureVector getFeatureVector() {
		return this.fv;
	}

	@Override
	public String toString() {
		return "[GraphStructure: " + this.nodeValues + "]";
	}

}
