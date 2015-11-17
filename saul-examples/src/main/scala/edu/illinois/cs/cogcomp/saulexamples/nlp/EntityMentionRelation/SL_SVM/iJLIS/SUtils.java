package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS;
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.RunnerPL;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;


public class SUtils {

	// Totally made up labels
	public enum NodeLabel {
		VALIANT, COWARDLY
	};
	
	public static List<NodeLabel> binaryToNodeLabel(String binary){
		ArrayList<NodeLabel> ret = new ArrayList<NodeLabel>();
		
		for(char c : binary.toCharArray()){
			int val = Integer.parseInt(c+"");
			if (val == 0){
				ret.add(NodeLabel.VALIANT);
			}else{
				ret.add(NodeLabel.COWARDLY);
			}
		}
		
		return ret;
		
	}
	
	
	/**
	 * A static method for smartly adding features to the LexManager, and creating a FeatureVector
	 * at the same time.
	 * 
	 * @param feats
	 * @return
	 */
	public static IFeatureVector makeFeatures(List<String> feats) {
		ArrayList<Object> idxList = new ArrayList();
		ArrayList<Object> valList = new ArrayList();

		for (String feat : feats) {

			if (!RunnerPL.lexm().containFeature(feat)) {
                System.out.println("Flag of preview"+ RunnerPL.lexm().getNumOfFeature());
                System.out.print("before Flag of feature set"+ RunnerPL.lexm().isAllowNewFeatures()+"\n");
                if(RunnerPL.lexm().isAllowNewFeatures())
				 //Todo see the error of the following line
				    //   RunnerPL.lexm().previewFeature(feat);
                System.out.print("After preview flag"+RunnerPL.lexm().getNumOfFeature());
                System.out.print("Flag of  after feature set"+ RunnerPL.lexm().isAllowNewFeatures()+"\n");

            }
            if (RunnerPL.lexm().containFeature(feat)) {

                int fid = RunnerPL.lexm().getFeatureId(feat);
                idxList.add(fid);
                valList.add(1.);
            }
		}

		int[] idx = ArrayUtils.toPrimitive(idxList.toArray(new Integer[0]));
		double[] val = ArrayUtils.toPrimitive(valList.toArray(new Double[0]));
        //Todo check waht is going on in the next two lines
         return makeFeatures(feats);
		//return new IFeatureVector(idx, val);
	}

	
	public static int numVowels(String s){
		int ret = 0;
		
		for(char c : s.toCharArray()){
			if(isVowel(c)){
				ret +=1;
			}
		}
		return ret;
	}
	
	public static boolean isVowel(char c){
		return isVowel(c+"");
	}
	
	public static boolean isVowel(String s){
		List<String> vowels = new ArrayList<String>();
		vowels.add("a");
		vowels.add("e");
		vowels.add("i");
		vowels.add("o");
		vowels.add("u");
		
		return vowels.contains(s);
	}
}
