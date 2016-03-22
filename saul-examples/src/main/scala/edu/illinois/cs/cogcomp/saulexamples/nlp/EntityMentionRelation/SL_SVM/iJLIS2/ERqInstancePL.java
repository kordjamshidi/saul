package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by parisakordjamshidi on 19/09/14.
 */
public class ERqInstancePL implements IInstance {
    //ConllRawToken q;
    ConllRelation pair;
   // List<String> nodeNames; // order matters!

    FeatureVectorBuffer E1fv;
    FeatureVectorBuffer E2fv;
    FeatureVectorBuffer Rfv;
    List<String> E1feats;
    List<String> E2feats;
    List<String> Rfeats;
    public ERqInstancePL(ConllRelation pair) {
        this.pair=pair;
        E1feats = new ArrayList<String>();
        E2feats=new ArrayList<String>();
        Rfeats=new ArrayList<String>();
        E1feats.add("Ph:"+pair.s.sentTokens.elementAt(pair.wordId1).phrase);
        E1feats.add("POS:"+pair.s.sentTokens.elementAt(pair.wordId1).POS);
        E2feats.add("Ph:"+pair.s.sentTokens.elementAt(pair.wordId2).phrase);
        E2feats.add("POS:"+pair.s.sentTokens.elementAt(pair.wordId2).POS);
        if (pair.wordId1<pair.wordId2)
        Rfeats.add("before");

        }
}
