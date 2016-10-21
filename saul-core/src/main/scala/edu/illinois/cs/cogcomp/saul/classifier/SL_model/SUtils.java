/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.SL_model;

import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import org.apache.commons.lang.ArrayUtils;


import java.util.ArrayList;
import java.util.List;


public class SUtils {
    /**
	 * A static method for smartly adding features to the LexManager, and creating a FeatureVector
	 * at the same time.
	 * 
	 * @param feats
	 * @return
	 */
	public static FeatureVectorBuffer makeFeatures(List<String> feats) {
		ArrayList<Object> idxList = new ArrayList();
		ArrayList<Object> valList = new ArrayList();

		for (String feat : feats) {

			if (!SL_IOManager.lexm().containFeature(feat)) {
                System.out.println("Flag of preview: "+ SL_IOManager.lexm().getNumOfFeature());
                System.out.println("before Flag of feature set: "+ SL_IOManager.lexm().isAllowNewFeatures()+"\n");
                if(SL_IOManager.lexm().isAllowNewFeatures())
				 //Todo see the error of the following line
				    //   RunnerPLPL.lexm().previewFeature(feat);
                System.out.println("After preview flag: "+SL_IOManager.lexm().getNumOfFeature());
                System.out.println("Flag of  after feature set: "+ SL_IOManager.lexm().isAllowNewFeatures()+"\n");
                SL_IOManager.lexm().addFeature(feat);
            }
            if (SL_IOManager.lexm().containFeature(feat)) {

                int fid = SL_IOManager.lexm().getFeatureId(feat);
                idxList.add(fid);
                valList.add(1.);
            }
		}

		int[] idx = ArrayUtils.toPrimitive(idxList.toArray(new Integer[0]));
		double[] val = ArrayUtils.toPrimitive(valList.toArray(new Double[0]));
        //Todo check waht is going on in the next two lines
         return new FeatureVectorBuffer(idx, val);
	}


}
