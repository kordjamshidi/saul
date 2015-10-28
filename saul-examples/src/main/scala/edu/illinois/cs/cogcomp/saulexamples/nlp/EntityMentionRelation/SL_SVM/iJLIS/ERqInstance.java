package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS;

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRawToken;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parisakordjamshidi on 19/09/14.
 */
public class ERqInstance implements IInstance {
    ConllRawToken q;
    List<String> nodeNames; // order matters!
    IFeatureVector fv;

    public ERqInstance(ConllRawToken tok) {
        q=tok;
        List<String> feats = new ArrayList<String>();
        feats.add("Ph:"+tok.phrase);
        feats.add("POS:"+tok.POS);
        this.nodeNames = feats;
        // do something with node names to make features
        this.fv = SUtils.makeFeatures(feats);
    }

}
