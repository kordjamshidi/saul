/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.data;

import edu.illinois.cs.cogcomp.lbjava.nlp.StringArraysToWords;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Hashtable;

/**
 * Reads documents, given a directory
 * 
 * @author Umar Manzoor
 * 
 */
public class ImageReader {

    private ArrayList<Image> images= new ArrayList<>();
    private ArrayList<Integer> objectCodes = new ArrayList<>();
    private ArrayList<String> objectConcepts = new ArrayList<>();
    private Hashtable<Integer, ArrayList<Integer>> ImageTable = new Hashtable<Integer, ArrayList<Integer>>();
    private Hashtable<Integer, String> MapCode2Concept = new Hashtable<Integer, String>();

    public ImageReader(String directory) throws IOException {
        File d = new File(directory);
        
        if (!d.exists()) {
            throw new IOException(directory + " does not exist!");
        }
        
        if (!d.isDirectory()) {
            throw new IOException(directory + " is not a directory!");
        }
        /*******************************************************/
        // Loading Image Codes and its Corresponding Concept
        // Storing information in HashTable for quick retrieval
        /*******************************************************/
        BufferedReader reader = new BufferedReader(new FileReader("mSprl/wlist.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] CodesInfo = line.split("\\t");
            MapCode2Concept.put(Integer.parseInt(CodesInfo[0]),CodesInfo[1]);
        }
        /*******************************************************/
        // Loading Image and its Objects information
        // Storing information in HashTable for quick retrieval
        /*******************************************************/
        reader = new BufferedReader(new FileReader("mSprl/labels.txt"));
        line = null;
        boolean repeat = false;
        int PreImageName = -1;
        int ImageName = -1;
        int objectCode;
        while ((line = reader.readLine()) != null) {
            String[] ImageInfo = line.split("\\t");
            // We are interested in ImageName and Object Code
            // Ignoring Object Count ImageInfo[1];
            ImageName = Integer.parseInt(ImageInfo[0]);
            objectCode = Integer.parseInt(ImageInfo[2]);

            if (!repeat) {
                PreImageName = ImageName;
                repeat = true;
            }
            if(PreImageName!=ImageName)
            {
                ImageTable.put(PreImageName, objectCodes);
                objectCodes = new ArrayList<Integer>();
                PreImageName = ImageName;
                objectCodes.add(objectCode);

            }
            else
            {
                PreImageName = ImageName;
                objectCodes.add(objectCode);
            }
        }
        ImageTable.put(ImageName, objectCodes);
        objectConcepts.clear();
        for (File f : d.listFiles()) {
            String[] s = f.getName().split("\\.");
            String label = s[0];
            Image iObj = new Image(label);

            // Object Code to Object Concept Conversion
            objectCodes = ImageTable.get(Integer.parseInt(label));
            for (Integer i : objectCodes )
            {
                objectConcepts.add(MappingCode2Concept(i));
            }
            iObj.setObjects(objectCodes, objectConcepts);
            images.add(iObj);
            objectConcepts.clear();
        }
    }
    /*****************************************/
    // Takes object code as input and returns
    // object concept
    /*****************************************/
    public String MappingCode2Concept(int code)
    {
        return MapCode2Concept.get(code);
    }
    public void close() {
    }

}
