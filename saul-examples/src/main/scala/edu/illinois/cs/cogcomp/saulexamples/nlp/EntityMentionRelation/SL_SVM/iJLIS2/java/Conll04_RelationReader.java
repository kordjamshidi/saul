import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


//import LBJ2.parse.Parser;
//import jointentityrelationextraction.
//import edu.illinois.cs.cogcomp.datastruct;
import edu.illinois.cs.cogcomp.core.io.LineIO;
//import ConllRawSentence;
//import der.ConllRawToken;
//import ConllReader.ConllRelation;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
//import ConllReader.Conll04Reader;
//import edu.illinois.cs.cogcomp.tutorial.Document;

public class Conll04_RelationReader implements Parser {
	public Vector<ConllRawToken> instances;
	public Vector<ConllRawSentence> sentences;
	public Vector<ConllRelation> relations;
	
	public String[] entityLabels,relLabels;
	private int currentInstanceId;
	
	
	public Conll04_RelationReader(String filename){
		instances=new Vector<ConllRawToken>();
		relations=new Vector<ConllRelation>();
		sentences=new Vector<ConllRawSentence>();
		entityLabels=new String[0];
		relLabels=new String[0];
//	}
	
	
	//public void readData(String filename) throws Exception {
		//BufferedReader br=new BufferedReader(new FileReader(filename));
		List<String> lines = null;
		try {
			lines = LineIO.read(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line;
		String[] tokens;
	
		
		ConllRawToken c=new ConllRawToken();
		
		ConllRelation r;
		int currSentId=0;
		boolean sentEnd=false;
		ConllRawSentence sent=new ConllRawSentence(currSentId);
		
		ArrayList<String> entityal = new ArrayList<String>();	
		ArrayList<String> relal = new ArrayList<String>();
		
		boolean relationSeen=false;
		int sentindex = 0;
		while(sentindex < lines.size()){
			line = lines.get(sentindex);
			sentindex ++;
			
			//System.out.println(sentindex + " " + line);
			if(line.isEmpty()){
				sentEnd=true;
				
/*				if(!sentEnd){					
					currSentId++;
					sentEnd=true;
					
					sentences.add(sent);
					
					sent=new ConllRawSentence(currSentId);
				}*/
				continue;
			}
			
			tokens=line.split("\t|\n");
			int s=tokens.length;
			if(s==3){				
				relationSeen=true;
				r=new ConllRelation();
				r.sentId=currSentId;
				//r.s=new ConllRawSentence(currSentId);
				//r.sentId1=currSentId-1;
				//r.sentId2=currSentId-1;
				r.wordId1=Integer.parseInt(tokens[0]);
				r.wordId2=Integer.parseInt(tokens[1]);
				r.relType=tokens[2];
				relations.add(r);
			//	System.out.println("WORD1:"+r.s.sentTokens.elementAt(r.wordId1).phrase);
				sent.addRelations(r);
//				sentences.elementAt(sentences.size()-1).addRelations(r);
				if(!relal.contains(tokens[2])){
					relal.add(tokens[2]);
				}
			}			
			else{
				//System.out.println("tokens[1]="+tokens[1]+"done");
				if(sentEnd){
					//if(!relationSeen)
					{
						sentences.add(sent);
/*						if(currSentId < 700)
							System.out.println("sid:" + currSentId);
						else System.out.println("sid:" + (currSentId + 51));
						for(int ind = 0;ind < sent.sentTokens.size();ind ++)
							System.out.print(sent.sentTokens.get(ind).phrase + " ");
						System.out.println();
	*/					
						currSentId++;						
					}					
					sent=new ConllRawSentence(currSentId);
				}
				
				c=new ConllRawToken();
				
/*				if(currSentId < 700)
					assert (currSentId == Integer.parseInt(tokens[0]));
				else 
				{
					assert(currSentId == Integer.parseInt(tokens[0]) - 51);
					if(currSentId != Integer.parseInt(tokens[0]) - 51)
						System.out.println("fuck you here");
				}*/
				
				c.entType=tokens[1];
				c.sentId=currSentId;
				c.wordId=Integer.parseInt(tokens[2]);
				c.setPOS(tokens[4]);				
				c.setPhrase(tokens[5]);
				
				sent.addTokens(c);
				if(!tokens[1].trim().equals("O")){
					instances.add(c);
					sent.setCurrentTokenAsEntity();
					if(!entityal.contains(tokens[1])){
						entityal.add(tokens[1]);
					}
				}
				
				sentEnd=false;
				relationSeen=false;
			}			
		}
		
		entityLabels=entityal.toArray(entityLabels);
		relLabels=relal.toArray(relLabels);
	 
		for (int counter=0; counter<relations.size();counter++){
			int sindex=relations.elementAt(counter).sentId;
			relations.elementAt(counter).s.sentTokens.addAll(0,sentences.elementAt(sindex).sentTokens);
		    relations.elementAt(counter).e1=sentences.elementAt(sindex).sentTokens.elementAt(relations.elementAt(counter).wordId1);
		    relations.elementAt(counter).e2=sentences.elementAt(sindex).sentTokens.elementAt(relations.elementAt(counter).wordId2);
		    		
		}
	}
	
	
	public void printData(){
		System.out.println("printing total "+sentences.size()+" sentences");
		for(int i=0;i<sentences.size();i++){
//			sentences.elementAt(i).printSentence();		
			sentences.elementAt(i).printEntities();
			sentences.elementAt(i).printRelations();
		}
		System.out.println("printing total "+instances.size()+" instances");
		for(int i=0;i<instances.size();i++){
			instances.elementAt(i).printInstance();			
		}
		System.out.println("printing total "+relations.size()+ " relations");
		for(int i=0;i<relations.size();i++){
			relations.elementAt(i).printRelation();
			System.out.println("WORD1:"+relations.elementAt(i).s.sentTokens.elementAt(relations.elementAt(i).wordId1).phrase);
			System.out.println("WORD2:"+relations.elementAt(i).s.sentTokens.elementAt(relations.elementAt(i).wordId2).phrase);
			
			//sense ("WORD2:"+t.s.sentTokens.elementAt(t.wordId2).phrase);
		}		
	}
//	public static void main(String[] args) throws Exception{
//		System.out.println("here");
//		Conll04_InstanceReader cr=new Conll04_InstanceReader("./data/conll04.corp");
//		//cr.readData("./data/conll04.corp");
//		cr.printData();
//	}
	public void close() {
	}
	public Object next() { 

		if (currentInstanceId < relations.size()) {
		
			ConllRelation file = relations.get(currentInstanceId++);
		

//			String[] split = file.getPath().split(File.separator);

	//		String label = split[split.length - 2];

			return file;//Document(file, label);
		} else
			return null;
	}

	public void reset() {
		currentInstanceId = 0;
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("here");
		Conll04_RelationReader cr=new Conll04_RelationReader("./data/conll04.corp");
		//cr.readData("/home/roth/rsamdan2/Project/EMStructuredPrediction/UnsupRelationExtraction/data/conll04.corp");
		cr.printData();
	}

}
