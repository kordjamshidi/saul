public class ConllRawToken {
	public int sentId,wordId;
	public String entType, POS, phrase;
	public String[] splitWords, splitPOS;
	
	public void setPhrase(String phrase){
		this.phrase=phrase;
		splitWords=phrase.split("/");
		
	}
	
	public void setPOS(String POS){
		this.POS=POS;
		splitPOS=POS.split("/");		
	}
	
	public int getLength(){
		return splitWords.length;
	}
	
	public String getPhrase(boolean isLowerCase){
		if(isLowerCase){
			return (new String(phrase)).toLowerCase();
		}
		return phrase;
	}
	
	public String[] getWords(boolean isLowerCase){		
		if(isLowerCase){
			String[] returnString=new String[splitWords.length];
			for(int i=0;i<splitWords.length;i++){
				returnString[i]=splitWords[i].toLowerCase();
			}
			return returnString;
		}
		return splitWords;
	}
	
	
	public void printInstance(){
		System.out.println("sent: "+sentId+" wordId: "+wordId+" phrase: "+phrase+" POS: "+POS+" entity type: "+entType);
	}
}
