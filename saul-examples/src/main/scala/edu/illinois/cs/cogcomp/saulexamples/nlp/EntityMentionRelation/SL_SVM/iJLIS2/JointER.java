// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D6DC13E0280401641EBACF5EEE91C2981588581C31C2034898BCC4239120A48BBBA426747F2F53F85A72329690592B8F9B6C049A341A2937D49393BA0C1BE18819F46FDC316388AD8923FB9AB6E501A91F0DED7C4B4880CCCE3CE65EDEE5EF771E70799C4B1EBAAE334C88DE309EC8F214C8000000
package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;

//import LBJ.infer.GurobiHook;
//import LBJ.infer.ILPInference;


import edu.illinois.cs.cogcomp.lbjava.infer.GurobiHook;
import edu.illinois.cs.cogcomp.lbjava.infer.ILPInference;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;
import edu.illinois.cs.cogcomp.lbjava.learn.Normalizer;
import edu.illinois.cs.cogcomp.lbjava.learn.Softmax;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation;

public class JointER extends ILPInference
{
  public static ConllRelation findHead(ConllRelation t)
  {
    return t;
  }


  public JointER() { }
  public JointER(ConllRelation head)
  {
    super(head, new GurobiHook());
    constraint = new JointER$subjectto().makeConstraint(head);
  }

  public String getHeadType() { return "ml.wolfe.examples.parisa.ConllRelation"; }
  public String[] getHeadFinderTypes()
  {
    return new String[]{ "ml.wolfe.examples.parisa.ConllRelation" };
  }

  public Normalizer getNormalizer(Learner c)
  {
    return new Softmax();
  }
}

