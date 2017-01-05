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
import java.util.List;
import com.jmatio.io.*;
import com.jmatio.types.*;
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Image;
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Segment;
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.SegmentRelation;


/**
 * Reads documents, given a directory
 * 
 * @author Umar Manzoor
 * 
 */
public class ImageReader {

    private List<Image> images;
    private List<Segment> segments;
    private List<SegmentRelation> relations;
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
        relations = new ArrayList<>();
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
        String folder = path + "/00/images/";
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
    // Loading Segments
    /*******************************************************/
    public List<Segment> getSegments() throws IOException
    {
        String file = path + "/features.txt";
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while ((line = reader.readLine()) != null) {
            String[] segmentInfo = line.split("\\t");
            String imageID = segmentInfo[0];
            int segmentID = Integer.parseInt(segmentInfo[1]);
            int segmentCode = Integer.parseInt(segmentInfo[3]);
            String segmentConcept = MappingCode2Concept(segmentCode);
            String segmentFeatures = segmentInfo[2];
            segments.add(new Segment(imageID, segmentID, segmentCode, segmentFeatures, segmentConcept));
        }
        return segments;
    }

    /*******************************************************/
    // Loading Segment Relations
    /*******************************************************/
    public List<SegmentRelation> getSegmentsRelations() throws IOException
    {
        String spatial_rels = path + "/00/spatial_rels";

        File d = new File(spatial_rels);

        if (!d.exists()) {
            throw new IOException(spatial_rels + " does not exist!");
        }

        for (File f : d.listFiles()) {
            String spatial_file = spatial_rels + "/" +f.getName();
            MatFileReader matfilereader = new MatFileReader(spatial_file);
            int val;
            int sg_id1;
            int sg_id2;
            String[] s = f.getName().split("\\.");
            String img_id=s[0];
            String rel;
            double[][] topo = ((MLDouble) matfilereader.getMLArray("topo")).getArray();
            double[][] x_rels = ((MLDouble) matfilereader.getMLArray("x_rels")).getArray();
            double[][] y_rels = ((MLDouble) matfilereader.getMLArray("y_rels")).getArray();

            /**************************************************/
            // Exemptional case
            // Sometimes mat file is returning only one value
            /**************************************************/
            if (topo.length>1) {
                // Finding Relationships
                for (int x = 0; x < topo[0].length; x++)
                    for (int y = 0; y < topo[1].length; y++) {
                        //Ignoring same indexes
                        if (x != y) {
                            sg_id1 = x + 1;
                            sg_id2 = y + 1;
                            val = (int) topo[x][y];
                            if (val == 1)
                                rel = "adjacent";
                            else if (val == 2)
                                rel = "disjoint";
                            else
                                rel = null;

                            //Creating new Relation between segments
                            relations.add(new SegmentRelation(img_id, sg_id1, sg_id2, rel));

                            val = (int) x_rels[x][y];
                            if (val == 3)
                                rel = "beside";
                            else if (val == 4)
                                rel = "x-aligned";
                            else
                                rel = null;
                            relations.add(new SegmentRelation(img_id, sg_id1, sg_id2, rel));


                            val = (int) y_rels[x][y];
                            if (val == 5)
                                rel = "above";
                            else if (val == 6)
                                rel = "below";
                            else if (val == 7)
                                rel = "y-aligned";
                            else
                                rel = null;
                            relations.add(new SegmentRelation(img_id, sg_id1, sg_id2, rel));

                        }
                    }
            }
        }
        return relations;
    }

}
