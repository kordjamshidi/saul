    package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;

    import edu.illinois.cs.cogcomp.saul.classifier.Learnable;
    import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
    import edu.illinois.cs.cogcomp.sl.core.IInstance;
    import edu.illinois.cs.cogcomp.sl.core.IStructure;
    import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
    import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
    import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

    import java.util.ArrayList;
    import java.util.List;

    public class ERFeatureGenerator extends AbstractFeatureGenerator {
         private static final long serialVersionUID = 1L;
         private Lexiconer lm;
      //   public List<Learnable> c;


         public ERFeatureGenerator(Lexiconer lm) {
            this.lm = lm;
        }
         public Lexiconer getlexicon(){
             return this.lm;
         }
         public IFeatureVector getFeatureVector (IInstance x, IStructure y){

                FeatureVectorBuffer fv = new FeatureVectorBuffer();
                ERqInstancePL mx = (ERqInstancePL) x;
                ERiStructurePL my = (ERiStructurePL) y;

                // do something with node names to make features
                mx.E1fv =  makeFeatures(mx.E1feats);
                mx.E2fv =  makeFeatures(mx.E2feats);
                mx.Rfv=    makeFeatures(mx.Rfeats);

                if (my.Rlables.E1Label().toLowerCase().contains("peop"))
                    my.fvPeop=mx.E1fv;

                if (my.Rlables.E2Label().toLowerCase().contains("org"))
                    my.fvOrg=mx.E2fv;
                fv.addFeature(my.fvPeop);
                fv.addFeature(my.fvOrg);

                if(my.Rlables.RelLabel().toLowerCase().contains("work"))
                    my.fvWorkFor=mx.Rfv;
                fv.addFeature(my.fvWorkFor);

                 //TODO tune fvb indexes
                  return fv.toFeatureVector();
            }

        public  FeatureVectorBuffer makeFeatures(List<String> feats) {
            ArrayList<Object> idxList = new ArrayList();
            ArrayList<Object> valList = new ArrayList();
            FeatureVectorBuffer temp= new FeatureVectorBuffer();
            //c.get(0).featureExtractor();
            for (String feat : feats) {

                if (!this.lm.containFeature(feat)) {
                    System.out.println("Flag of preview: "+ this.lm.getNumOfFeature());
                    System.out.println("before Flag of feature set: "+ this.lm.isAllowNewFeatures()+"\n");
                    if(this.lm.isAllowNewFeatures())
                        System.out.println("After preview flag: "+this.lm.getNumOfFeature());
                    System.out.println("Flag of  after feature set: "+ this.lm.isAllowNewFeatures()+"\n");
                    this.lm.addFeature(feat);
                }
                if (this.lm.containFeature(feat)) {

                    int fid = this.lm.getFeatureId(feat);
                    idxList.add(fid);
                    valList.add(1.);
                    temp.addFeature(fid,1.);
                }
            }

         //   int[] idx = ArrayUtils.toPrimitive(idxList.toArray(new Integer[0]));
         //   double[] val = ArrayUtils.toPrimitive(valList.toArray(new Double[0]));
            //Todo check waht is going on in the next two lines
           // return new FeatureVectorBuffer(idx, val);
            return temp;
        }

    }

