package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.sl.core.AbstractInferenceSolver;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;

import java.util.ArrayList;
import java.util.List;

//import edu.illinois.cs.cogcomp.jlistutorial.SUtils.NodeLabel;

public class SimpleInference extends AbstractInferenceSolver {

	private static final long serialVersionUID = 1L;

	/**
	 * This is for training
	 */
	//@Override
	public Pair<IStructure, Double> getLossSensitiveBestStructure(WeightVector wv, IInstance input, IStructure gold) throws Exception {

		GraphStructure goldStruct = (GraphStructure) gold;
		QueryInstance qi = (QueryInstance) input;
		
		List<GraphStructure> options = getOptions(qi);

		double max = Double.NEGATIVE_INFINITY;
		double score;
		GraphStructure maxC = null;
		double loss;
		
		System.out.println();
		System.out.println(gold);
		
		// Now get the structure with the max: score+loss
		for (GraphStructure c : options) {
			score = wv.dotProduct(c.getFeatureVector());
			loss = getLoss(null, c, goldStruct);
			score += loss;
		
			System.out.println(c);
			System.out.println(c.getFeatureVector());
			System.out.println(score);
			
			if (score > max) {
				max = score;
				maxC = c;
			}
		}
		
		// Return structure with highest score+loss, and loss of this structure
		return new Pair<IStructure, Double>(maxC, ((double) getLoss(null, maxC, goldStruct)));
	}
	
//	public double getHammingLoss(GraphStructure hyp, GraphStructure gold){
//		double loss = 0;
//		for (int i = 0; i < hyp.nodeValues.size(); i++) {
//			if (hyp.nodeValues.get(i) != gold.nodeValues.get(i)) {
//				loss += 1.0;
//			}
//		}
//		return loss;
//	}

	/**
	 * This is for prediction.
	 */
	@Override
	public IStructure getBestStructure(WeightVector wv, IInstance input) throws Exception {
		QueryInstance qi = (QueryInstance) input;
		
		List<GraphStructure> options = getOptions(qi);

		double max = Double.NEGATIVE_INFINITY;
		double score;
		GraphStructure maxC = null;
		
		// Now get the structure with the max: score+loss
		for (GraphStructure c : options) {
			score = wv.dotProduct(c.getFeatureVector());
			
			if (score > max) {
				max = score;
				maxC = c;
			}
		}
		
		// Return structure with highest score+loss, and loss of this structure
		return maxC;
	}

    @Override
    public IStructure getLossAugmentedBestStructure(WeightVector weightVector, IInstance iInstance, IStructure iStructure) throws Exception {
        return null;
    }

    @Override
    public float getLoss(IInstance iInstance, IStructure gold, IStructure pred) {
        float loss = 0;
        ERiStructure igold= (ERiStructure) gold;
        ERiStructure ipred=(ERiStructure)pred;
        //for (int i = 0; i < ipred.nodeValues.size(); i++) {
         //   if (ipred.nodeValues.get(i) != igold.nodeValues.get(i)) {
        if (ipred.nodeValues != igold.nodeValues) {

            loss += 1.0;
            }
       // }
        return loss;
    }

    /**
	 * Given this QueryInstance, what are the options for responses?
	 * This simple method just enumerates all possibilities. (in this
	 * contrived example there are 8 total)
	 * 
	 * @param qi
	 * @return
	 */
	public List<GraphStructure> getOptions(QueryInstance qi) {

		List<GraphStructure> options = new ArrayList();


		// Get all combinations
		for (SUtils.NodeLabel i : SUtils.NodeLabel.values()) {
			for (SUtils.NodeLabel j : SUtils.NodeLabel.values()) {
				for (SUtils.NodeLabel k : SUtils.NodeLabel.values()) {
					List<SUtils.NodeLabel> labelOptions = new ArrayList<SUtils.NodeLabel>();
					labelOptions.add(i);
					labelOptions.add(j);
					labelOptions.add(k);
					options.add(new GraphStructure(qi, labelOptions));
				}
			}
		}

		return options;
	}

}
