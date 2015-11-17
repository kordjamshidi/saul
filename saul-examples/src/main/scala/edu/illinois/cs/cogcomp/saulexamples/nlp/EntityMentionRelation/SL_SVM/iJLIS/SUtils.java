package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS;

import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import org.apache.commons.lang3.ArrayUtils;

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

			if (!Runner.lexm().containFeature(feat)) {
                System.out.println("Flag of preview: "+ Runner.lexm().getNumOfFeature());
                System.out.print("before Flag of feature set: "+ Runner.lexm().isAllowNewFeatures()+"\n");
                if(Runner.lexm().isAllowNewFeatures())
				 //Todo see the error of the following line
				    //   RunnerPL.lexm().previewFeature(feat);
                System.out.print("After preview flag: "+Runner.lexm().getNumOfFeature());
                System.out.print("Flag of  after feature set: "+ Runner.lexm().isAllowNewFeatures()+"\n");
                Runner.lexm().addFeature(feat);
            }
            if (Runner.lexm().containFeature(feat)) {

                int fid = Runner.lexm().getFeatureId(feat);
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
