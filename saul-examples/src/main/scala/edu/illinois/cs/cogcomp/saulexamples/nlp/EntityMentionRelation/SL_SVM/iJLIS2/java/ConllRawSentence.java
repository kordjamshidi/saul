//import ConllReader.ConllRawToken;
//import ConllReader.ConllRelation;

import java.util.Vector;

public class ConllRawSentence {
	public Vector<ConllRawToken> sentTokens;
	public Vector<ConllRelation> relations;
	public int sentId;
	public Vector<Integer> entityIndices;
	
	public ConllRawSentence(int sentId){
		sentTokens	= new Vector<ConllRawToken>();
		relations=	new Vector<ConllRelation>();
		entityIndices=new Vector<Integer>();
		this.sentId=sentId;
	}
	
	public void addTokens(ConllRawToken c){
		sentTokens.add(c);
	}
	
	public void addRelations(ConllRelation r){
		relations.add(r);
	}
	
	public void setCurrentTokenAsEntity(){
		entityIndices.add(sentTokens.size()-1);
	}
	
	public Vector<ConllRawToken> getEntitiesInSentence(){
		Vector<ConllRawToken> entities=new Vector<ConllRawToken>();
		for(int i=0;i<entityIndices.size();i++){
			entities.add(sentTokens.elementAt(entityIndices.elementAt(i)));
		}
		return entities;
	}
	
	public String returnRelationType(int word1, int word2){
		for(int i=0;i<relations.size();i++){
	//		System.out.println("rel type = "+relations.elementAt(i).relType);
//			System.out.println("wordId1=" + relations.elementAt(i).wordId1+" word1= "+word1+" word id2 = " + relations.elementAt(i).wordId2+" word2 = "+word2);
			if(relations.elementAt(i).wordId1==word1 && relations.elementAt(i).wordId2==word2){
//				System.out.println("rel type = "+relations.elementAt(i).relType);
				return relations.elementAt(i).relType;
				
			}
		}
		return "None";
	}
	
	public ConllRawToken[] returnWindow(int loc, int offset1, int offset2){
//		String[] window=new String[(Math.min(sentTokens.size()-1,loc+offset2) - Math.max(0, loc-offset1))];
		int index=0;
		ConllRawToken[] window=new ConllRawToken[(Math.min(sentTokens.size()-1,loc+offset2) - Math.max(0, loc-offset1))];
		for(int i=-offset1;i<=offset2;i++){
			if((i==0) || (i+loc<0)) continue;
			if(i+loc>sentTokens.size()-1) break;
			window[index]=sentTokens.elementAt(i+loc);
//			String a=sentTokens.elementAt(i+loc).getPhrase(isLowerCase);
//			window[index]="Window"+i+":"+a;
			index++;
	//		window[index]="Window:"+a;
	//		index++;
		}
		return window;
	}
	
	public void printEntities(){
		for(int i=0;i<entityIndices.size();i++){
			sentTokens.elementAt(entityIndices.elementAt(i)).printInstance();
		}
	}
	
	public void printRelations(){
		for(int i=0;i<relations.size();i++){
			relations.elementAt(i).printRelation();
		}
	}
	public void printSentence(){
		for(int i=0;i<sentTokens.size();i++){
			System.out.println("sent id= "+sentId);
			sentTokens.elementAt(i).printInstance();
		}
	}

}
