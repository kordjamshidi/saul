//import ConllReader.*;
//import ConllReader.ConllRawSentence;

public class ConllRelation {
	public int wordId1, wordId2;
	public int sentId;
	public ConllRawSentence s=new ConllRawSentence(sentId);
	public String relType;
	public ConllRawToken e1;
	public ConllRawToken e2;

	public void printRelation(){
		System.out.println(" word1: "+wordId1+" word2: "+wordId2+" reltype: "+relType);
	}
}
