/*
package ml.wolfe.examples.parisa.iJLIS;

import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.indsup.inference.AbstractLossSensitiveStructureFinder;
import edu.illinois.cs.cogcomp.indsup.inference.IInstance;
import edu.illinois.cs.cogcomp.indsup.inference.IStructure;
import edu.illinois.cs.cogcomp.indsup.learning.*;
import edu.illinois.cs.cogcomp.indsup.learning.L2Loss.L2LossJLISLearner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import edu.illinois.cs.cogcomp.jlistutorial.SUtils.NodeLabel;

public class Runner {

	*/
/*
	 * Notice that lexm is static.
	 *//*

	static LexManager lexm = new LexManager();

	*/
/**
	 * This modifies the two lists given two it. They are initialized.
	 * 
	 * @param sclist
	 * @param outlist
	 * @param fname
	 * @throws java.io.FileNotFoundException
	 *//*

	public static void readData(List<IInstance> sclist, List<IStructure> outlist, String fname) throws FileNotFoundException {

		ArrayList<String> lines = LineIO.read(fname);

		for(String line : lines){
			List<String> s = Arrays.asList(line.split("\\s+"));
			
			QueryInstance newqi = new QueryInstance(s.subList(0, 3));
			sclist.add(newqi);
			GraphStructure cs = new GraphStructure(newqi, SUtils.binaryToNodeLabel(s.get(3)));
			outlist.add(cs);
		}
	}

	public static String trainSSVM(String modelname) throws Exception {

		// First we read the data.
		// X instance list
		List<IInstance> sclist = new ArrayList();
		// Y instance (structure)
		List<IStructure> outlist = new ArrayList();

		String fname = "/Users/parisakordjamshidi/wolfe-0.1.0/wolfe-examples/src/main/scala/ml/wolfe/examples/parisa/iJLIS/namedata-train";
		readData(sclist, outlist, fname);
		lexm.setAllowNewFeatures(false);

		// Define the problem, and give it data
		StructuredProblem sp = new StructuredProblem();
		sp.input_list = sclist;
		sp.output_list = outlist;

		// Create parameters
		JLISParameters para = new JLISParameters();
		para.total_number_features = lexm.totalNumofFeature();
		para.c_struct = 1;
		para.check_inference_opt = false;

		SimpleInference si = new SimpleInference();
		
		// Actually do the learning
		L2LossJLISLearner learner = new L2LossJLISLearner();
		WeightVector learned_wv = learner.trainStructuredSVM(si, sp, para);
		
		// Package all the parameters together
		SimpleModel model = new SimpleModel();
		model.wv = learned_wv;	
		model.s_finder = new SimpleInference();
		model.lm = lexm;
		
		// This is just a way to serialize the model
		JLISModelIOManager iom = new JLISModelIOManager();
		iom.saveModel(model, modelname);

		return modelname;

	}

	public static void testSequenceSSVM(String model_name) throws Exception {
		// Load the model
		JLISModelIOManager iom = new JLISModelIOManager();
		SimpleModel model = (SimpleModel) iom.loadModel(model_name);
		Runner.lexm = model.lm;
		WeightVector learned_wv = model.wv;
		SimpleInference inference_proc = (SimpleInference) model.s_finder;
		
		// X instance list
		List<IInstance> sclist = new ArrayList();
		// Y instance (structure)
		List<IStructure> outlist = new ArrayList();

		// Load the data
		String fname = "/Users/parisakordjamshidi/wolfe-0.1.0/wolfe-examples/src/main/scala/ml/wolfe/examples/parisa/iJLIS/namedata-trainnamedata-test";
		readData(sclist, outlist, fname);

		// Create a problem with testing data
		StructuredProblem sp = new StructuredProblem();
		sp.input_list = sclist;
		sp.output_list = outlist;

		printTestACC(sp, inference_proc, learned_wv);
	}

	private static void printTestACC(StructuredProblem sp, AbstractLossSensitiveStructureFinder s_finder, WeightVector ssvm_wv) throws IOException,
			Exception {

		double tp = 0;
		double fp = 0;
		double tn = 0;
		double fn = 0;
		double total = 0.0;

		for (int i = 0; i < sp.input_list.size(); i++) {

			GraphStructure gold = (GraphStructure) sp.output_list.get(i);
			GraphStructure prediction = (GraphStructure) s_finder.getBestStructure(ssvm_wv, sp.input_list.get(i));
			System.out.println(prediction);
			
			for (int j = 0; j < gold.nodeValues.size(); j++) {
				SUtils.NodeLabel pred = prediction.nodeValues.get(j);
				SUtils.NodeLabel goldval = gold.nodeValues.get(j);

				// got it right.
				if (goldval == pred) {
					if (pred == SUtils.NodeLabel.VALIANT) {
						tp += 1.0;
					} else {
						tn += 1.0;
					}
				} else { // so I got the answer wrong
					if (pred == SUtils.NodeLabel.VALIANT) {
						fp += 1.0;
					} else {
						fn += 1.0;
					}
				}

				total += 1.0;
			}
		}

		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);
		double F1 = 2 * precision * recall / (precision + recall);
		System.out.println("=========================");
		System.out.println(String.format("      %-5s %-5s %-5s", "P", "R", "F1"));
//		System.out.println(String.format("COWARDLY: %4.3f %4.3f %4.3f", precision, recall, F1));
		System.out.println(String.format("VALIANT : %4.3f %4.3f %4.3f", precision, recall, F1));
		System.out.println("=========================");
		System.out.println("Acc = " + (tp + tn) / total);
	}

	public static void main(String[] args) throws Exception {
		String modelname = "mytest.ssvm.model";
		trainSSVM(modelname);

		System.out.println("\n=== NOW TESTING ===");
		testSequenceSSVM(modelname);

	}

}
*/
