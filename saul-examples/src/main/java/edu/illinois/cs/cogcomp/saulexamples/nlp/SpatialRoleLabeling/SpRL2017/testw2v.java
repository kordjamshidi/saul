/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2017;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.weights.WeightInit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

/**
 * Created by parisakordjamshidi on 1/28/17.
 */
public class testw2v {

    public testw2v() throws IOException {
    }



    public static void main(String [ ] args) throws IOException {


        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder()
                .iterations(1)
                .weightInit(WeightInit.XAVIER)
                .activation("relu")
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.05)
                // ... other hyperparameters
               // .backprop()
                .build();

        File gModel = new File("/Users/parisakordjamshidi/IdeaProjects/saul/data/GoogleNews-vectors-negative300.bin");

        Word2Vec vec = null;
        try {
            vec = WordVectorSerializer.loadGoogleModel(gModel, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStreamReader r = new InputStreamReader(System.in);

        BufferedReader br = new BufferedReader(r);


       // System.out.print("Word: ");
       String word = "kids";// br.readLine();

        // if ("EXIT".equals(word))
        System.out.println(vec.similarity("children", "kids"));
        Collection<String> lst = vec.wordsNearest(word, 20);

        System.out.println(word + " -> " + lst);
    }
  }
