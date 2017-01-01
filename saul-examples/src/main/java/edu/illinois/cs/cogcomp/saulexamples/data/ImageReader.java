/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.data;

import com.jmatio.io.MatFileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Reads documents, given a directory
 * 
 * @author Umar Manzoor
 * 
 */
public class ImageReader {

    public List<Image> images;
    private List<Segment> segments;
    private Hashtable<Integer, String> MapCode2Concept = new Hashtable<Integer, String>();
    private String path;
    public ImageReader(String directory) throws IOException {
        File d = new File(directory);
        
        if (!d.exists()) {
            throw new IOException(directory + " does not exist!");
        }
        
        if (!d.isDirectory()) {
            throw new IOException(directory + " is not a directory!");
        }
        path = directory;
        images = new ArrayList<>();
        segments = new ArrayList<>();
        // Load Concepts
        getConcepts(directory);
    }

    /*****************************************/
    // Takes object code as input and returns
    // object concept
    /*****************************************/
    private String MappingCode2Concept(int code)
    {
        return MapCode2Concept.get(code);
    }

    /*******************************************************/
    // Loading Image Codes and its Corresponding Concept
    // Storing information in HashTable for quick retrieval
    /*******************************************************/
    private void getConcepts(String directory) throws IOException
    {
        String file = directory + "/wlist.txt";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
            while ((line = reader.readLine()) != null) {
            String[] CodesInfo = line.split("\\t");
            MapCode2Concept.put(Integer.parseInt(CodesInfo[0]),CodesInfo[1]);
        }
    }

    /*******************************************************/
    // Loading Image
    /*******************************************************/
    public List<Image> getImages() throws IOException
    {
        String folder = path + "/images/00";
        File d = new File(folder);

        if (!d.exists()) {
            throw new IOException(path + " does not exist!");
        }

        if (!d.isDirectory()) {
            throw new IOException(path + " is not a directory!");
        }

        for (File f: d.listFiles()){

            String label = f.getName();
            String[] split = label.split("\\.");
            images.add(new Image(label, split[0]));}
        return images;
    }
    /*******************************************************/
    // Loading Image
    /*******************************************************/
    public List<Segment> getSegments() throws IOException
    {
        String file = path + "/features.txt";
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while ((line = reader.readLine()) != null) {
            String[] segmentInfo = line.split("\\t");
            String imageID = segmentInfo[0];
            int segmentCode = Integer.parseInt(segmentInfo[3]);
            String segmentConcept = MappingCode2Concept(segmentCode);
            String segmentFeatures = segmentInfo[2];
            segments.add(new Segment(imageID, segmentCode, segmentFeatures, segmentConcept));
        }
        return segments;
    }
}
