package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS;

import edu.illinois.cs.cogcomp.core.io.LineIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataGenerator {
	
	public DataGenerator() throws IOException{

		// Process: generate three names
		// three labels: each 0
		// if name1 contains more than one vowel:
		//    label it as 1
		// if name2 starts with a vowel, and name1 is labeled as 1:
		//    name2 gets a 1
		// if name2 is 1 and name3 has a vowel in the last place and if name1 is 0:
		//    name3 gets a 0
		
		ArrayList<String> names = LineIO.read("names");
		List<String> triples = new ArrayList();
		List<String> outlines = new ArrayList();
		for(String name : names){
			triples.add(name.toLowerCase().trim());
			
			if(triples.size() == 3){
				System.out.println(triples);
				
				String n1 = triples.get(0);
				String n2 = triples.get(1);
				String n3 = triples.get(2);
				
				int lab1, lab2, lab3;
				
				if(SUtils.numVowels(n1) > 2){
					lab1 = 1;
				}else{
					lab1 = 0;
				}
				
				if(SUtils.isVowel(n2.charAt(0)) && lab1 == 1){
					lab2 = 1;
				}else{
					lab2 = 0;
				}
				
				if(SUtils.isVowel(n2.charAt(n2.length()-1)) && lab1 == 0){
					lab3 = 1;
				}else{
					lab3 = 0;
				}
				
				System.out.println("" + lab1 + lab2 + lab3 + "");
				
				String out = String.format("%s %s %s %d%d%d", n1,n2,n3,lab1,lab2,lab3);
				outlines.add(out);
				
				triples.clear();
			}
		}
		
		
		double part = 0.75;	
		int partInd = (int) (outlines.size() * part);
		
		LineIO.write("namedata-train", outlines.subList(0, partInd));
		LineIO.write("namedata-test", outlines.subList(partInd, outlines.size()));

		
		
	}
	

	
	public static void main(String[] args) throws IOException{
		DataGenerator dg = new DataGenerator();
		
	}

}
