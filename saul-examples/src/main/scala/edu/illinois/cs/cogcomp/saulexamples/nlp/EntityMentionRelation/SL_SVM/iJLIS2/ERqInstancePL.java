package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;

import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation;
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS.SUtils;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;

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

    public ERqInstancePL(ConllRelation pair) {
        this.pair=pair;
        List<String> E1feats = new ArrayList<String>();
        List<String> E2feats=new ArrayList<String>();
        List<String> Rfeats=new ArrayList<String>();
        E1feats.add("Ph:"+pair.s.sentTokens.elementAt(pair.wordId1).phrase);
        E1feats.add("POS:"+pair.s.sentTokens.elementAt(pair.wordId1).POS);
        E2feats.add("Ph:"+pair.s.sentTokens.elementAt(pair.wordId2).phrase);
        E2feats.add("POS:"+pair.s.sentTokens.elementAt(pair.wordId2).POS);
        if (pair.wordId1<pair.wordId2)
        Rfeats.add("before");

       //System.out.println(RunnerPL.lexm().totalNumofFeature());

      // do something with node names to make features
        this.E1fv = (FeatureVectorBuffer) SUtils.makeFeatures(E1feats);

        this.E2fv = (FeatureVectorBuffer) SUtils.makeFeatures(E2feats);
        this.Rfv= (FeatureVectorBuffer) SUtils.makeFeatures(Rfeats);
      }




}
