/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Reads documents, given a directory
 * 
 * @author Umar Manzoor
 * 
 */
public class ImageReader {

    public ArrayList<Image> images= new ArrayList<>();
    private ArrayList<Segment> objectCodesFeatures = new ArrayList<>();
    private Hashtable<Integer, String> MapCode2Concept = new Hashtable<Integer, String>();

    public ImageReader(String directory) throws IOException {
        File d = new File(directory);
        
        if (!d.exists()) {
            throw new IOException(directory + " does not exist!");
        }
        
        if (!d.isDirectory()) {
            throw new IOException(directory + " is not a directory!");
        }
        // Load Concepts
        LoadConcepts();
        // Load Image Objects / Features
        LoadImageInfo(d);
    }

    /*****************************************/
    // Takes object code as input and returns
    // object concept
    /*****************************************/
    public String MappingCode2Concept(int code)
    {
        return MapCode2Concept.get(code);
    }

    /*******************************************************/
    // Loading Image Codes and its Corresponding Concept
    // Storing information in HashTable for quick retrieval
    /*******************************************************/
    private void LoadConcepts() throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader("mSprl/wlist.txt"));
        String line;
            while ((line = reader.readLine()) != null) {
            String[] CodesInfo = line.split("\\t");
            MapCode2Concept.put(Integer.parseInt(CodesInfo[0]),CodesInfo[1]);
        }
    }

    /*******************************************************/
    // Loading Image and its Objects information
    // (Including features / codes / Concepts)
    /*******************************************************/
    private void LoadImageInfo(File d) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader("data/features.txt"));
        String line = null;
        boolean repeat = false;
        int PreImageName = -1;
        int ImageName = -1;
        int objectCode;
        String features;
        Image i_Obj;
        while ((line = reader.readLine()) != null) {
            String[] ImageInfo = line.split("\\t");
            // We are interested in ImageName, Object Code and Features
            // Ignoring Object Count ImageInfo[1];
            ImageName = Integer.parseInt(ImageInfo[0]);
            features = ImageInfo[2];
            objectCode = Integer.parseInt(ImageInfo[3]);


            if (!repeat) {
                PreImageName = ImageName;
                repeat = true;
            }
            if(PreImageName!=ImageName)
            {
                i_Obj = new Image(Integer.toString(PreImageName));
                i_Obj.associatedObjects = objectCodesFeatures;
                images.add(i_Obj);

                objectCodesFeatures = new ArrayList<>();
                PreImageName = ImageName;
                Segment s_obj = new Segment(objectCode,features, MappingCode2Concept(objectCode));
                objectCodesFeatures.add(s_obj);

            }
            else
            {
                PreImageName = ImageName;
                Segment s_obj = new Segment(objectCode,features, MappingCode2Concept(objectCode));
                objectCodesFeatures.add(s_obj);
            }
        }
        i_Obj = new Image(Integer.toString(PreImageName));
        i_Obj.associatedObjects = objectCodesFeatures;
        images.add(i_Obj);
    }
}
