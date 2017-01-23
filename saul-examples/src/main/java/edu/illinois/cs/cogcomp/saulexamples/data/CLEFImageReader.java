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
import java.util.*;

import com.jmatio.io.*;
import com.jmatio.types.*;
import edu.illinois.cs.cogcomp.saulexamples.vision.Image;
import edu.illinois.cs.cogcomp.saulexamples.vision.Segment;
import edu.illinois.cs.cogcomp.saulexamples.vision.SegmentRelation;
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Document;

/**
 * Reads CLEF Image dataset, given a directory
 * @author Umar Manzoor
 *
 */
public class CLEFImageReader
{

    private String path;
    private Boolean readFullData;
    private List<String> trainingData;
    private List<String> testData;

    public List<Image> trainingImages;
    public List<Segment> trainingSegments;
    public List<SegmentRelation> trainingRelations;

    public List<Image> testImages;
    public List<Segment> testSegments;
    public List<SegmentRelation> testRelations;

    private Hashtable<Integer, String> MapCode2Concept = new Hashtable<Integer, String>();

    public CLEFImageReader(String directory, Boolean readFullData) throws IOException {
        File d = new File(directory);

        if (!d.exists()) {
            throw new IOException(directory + " does not exist!");
        }

        if (!d.isDirectory()) {
            throw new IOException(directory + " is not a directory!");
        }
        trainingData = new ArrayList<>();
        testData = new ArrayList<>();

        this.readFullData = readFullData;

        // Training Data
        trainingImages = new ArrayList<>();
        trainingSegments = new ArrayList<>();
        trainingRelations = new ArrayList<>();

        // Test Data
        testImages = new ArrayList<>();
        testSegments = new ArrayList<>();
        testRelations = new ArrayList<>();

        path = directory;
        // Load Concepts
        getConcepts(directory);
        // Load Training
        getTrainingImages();
        // Load Testing
        getTestImages();
        // Load all Images
        getallImages(directory);

        System.out.println("Total Train Data " + trainingData.size());

        System.out.println("Total Test Data " + testData.size());

        System.out.println("Total Train Images " + trainingImages.size());

        System.out.println("Total Test Images " + testImages.size());

        System.out.println("Total Train Segments " + trainingSegments.size());

        System.out.println("Total Test Segments " + testSegments.size());

        System.out.println("Total Train Relations " + trainingRelations.size());

        System.out.println("Total Test Relations " + testRelations.size());

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
        String file = directory + "/wlist100.txt";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] CodesInfo = line.split("\\t");
            if(CodesInfo.length>1) {
                MapCode2Concept.put(Integer.parseInt(CodesInfo[0]), CodesInfo[1]);
            }
            else {
                MapCode2Concept.put(Integer.parseInt(CodesInfo[0]), " ");
            }
        }
    }

    /*******************************************************/
    // Load all Images in the CLEF Dataset
    /*******************************************************/
    private void getallImages(String directory) throws IOException
    {
        File d = new File(directory);

        if (!d.exists()) {
            throw new IOException(directory + " does not exist!");
        }

        if (!d.isDirectory()) {
            throw new IOException(directory + " is not a directory!");
        }
        int length=0;

        for (File f : d.listFiles()) {
            if (f.isDirectory()) {
                ++length;
                String mainFolder = directory + "/" +f.getName();
                System.out.println(mainFolder);
                //Load all images
                String imageFolder = mainFolder + "/images";
                getImages(imageFolder);

                //Load all segments
                String file = mainFolder + "/features.txt";
                getSegments(file);

                //Load all relations
                String spatialRelations = mainFolder + "/spatial_rels";
                getSegmentsRelations(spatialRelations);

            }
            if (!readFullData&&length==2)
                break;
        }
    }

    /*******************************************************/
    // Loading Images
    /*******************************************************/
    private void getImages(String folder)
    {
        File d = new File(folder);

        if (d.exists()) {

            for (File f: d.listFiles()){
                String label = f.getName();
                String[] split = label.split("\\.");
                if(trainingData.contains(split[0]))
                    trainingImages.add(new Image(label, split[0]));
                else if (testData.contains(split[0]))
                    testImages.add(new Image(label, split[0]));
            }
        }
    }
    /*******************************************************/
    // Loading Segments
    /*******************************************************/
    private void getSegments(String file) throws IOException
    {
        File d = new File(file);

        if (d.exists()) {

            String line;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String[] segmentInfo = line.split("\\t");
                if(segmentInfo.length==4) {
                    String imageId = segmentInfo[0];
                    int segmentId = Integer.parseInt(segmentInfo[1]);
                    int segmentCode = Integer.parseInt(segmentInfo[3]);
                    String segmentConcept = MappingCode2Concept(segmentCode);
                    if (segmentConcept != null) {
                        String segmentFeatures = segmentInfo[2];
                        segmentFeatures = segmentFeatures.trim().replaceAll(" +", " ");
                        if (trainingData.contains(imageId))
                            trainingSegments.add(new Segment(imageId, segmentId, segmentCode, segmentFeatures, segmentConcept));
                        else if (testData.contains(imageId))
                            testSegments.add(new Segment(imageId, segmentId, segmentCode, segmentFeatures, segmentConcept));
                    }
                }
            }
        }
    }

    /*******************************************************/
    // Loading Segment Relations
    /*******************************************************/
    private void getSegmentsRelations(String spatial_rels) throws IOException
    {
        File d = new File(spatial_rels);

        if (d.exists()) {
            for (File f : d.listFiles()) {
                String spatial_file = spatial_rels + "/" + f.getName();
                MatFileReader matFileReader = new MatFileReader(spatial_file);
                int val;
                int firstSegmentId;
                int secondSegmentId;
                String[] s = f.getName().split("\\.");
                String imgId = s[0];
                String rel;
                double[][] topo = ((MLDouble) matFileReader.getMLArray("topo")).getArray();
                double[][] xRels = ((MLDouble) matFileReader.getMLArray("x_rels")).getArray();
                double[][] yRels = ((MLDouble) matFileReader.getMLArray("y_rels")).getArray();

                /**************************************************/
                // Exemptional case
                // Sometimes mat file is returning only one value
                /**************************************************/
                if (topo.length > 1) {
                    // Finding Relationships
                    for (int x = 0; x < topo[0].length; x++)
                        for (int y = 0; y < topo[1].length; y++) {
                            //Ignoring same indexes
                            if (x != y) {
                                firstSegmentId = x + 1;
                                secondSegmentId = y + 1;
                                val = (int) topo[x][y];
                                if (val == 1)
                                    rel = "adjacent";
                                else if (val == 2)
                                    rel = "disjoint";
                                else
                                    rel = null;

                                if(trainingData.contains(imgId)) {
                                    //Creating new Relation between segments
                                    trainingRelations.add(new SegmentRelation(imgId, firstSegmentId, secondSegmentId, rel));
                                }
                                else if (testData.contains(imgId)) {
                                    testRelations.add(new SegmentRelation(imgId, firstSegmentId, secondSegmentId, rel));
                                }
                                val = (int) xRels[x][y];
                                if (val == 3)
                                    rel = "beside";
                                else if (val == 4)
                                    rel = "x-aligned";
                                else
                                    rel = null;

                                if(trainingData.contains(imgId)) {
                                    //Creating new Relation between segments
                                    trainingRelations.add(new SegmentRelation(imgId, firstSegmentId, secondSegmentId, rel));
                                }
                                else if (testData.contains(imgId)) {
                                    testRelations.add(new SegmentRelation(imgId, firstSegmentId, secondSegmentId, rel));
                                }


                                val = (int) yRels[x][y];
                                if (val == 5)
                                    rel = "above";
                                else if (val == 6)
                                    rel = "below";
                                else if (val == 7)
                                    rel = "y-aligned";
                                else
                                    rel = null;

                                if(trainingData.contains(imgId)) {
                                    //Creating new Relation between segments
                                    trainingRelations.add(new SegmentRelation(imgId, firstSegmentId, secondSegmentId, rel));
                                }
                                else if (testData.contains(imgId)) {
                                    testRelations.add(new SegmentRelation(imgId, firstSegmentId, secondSegmentId, rel));
                                }
                            }
                        }
                }
            }
        }
    }

    /*******************************************************/
    // Loading Training Images
    /*******************************************************/
    private void getTrainingImages() throws IOException {

        if(readFullData) {
            String trainImage = path + "/training.mat";
            getMatData(trainImage, true);
        }
        else {
            String trainImage = path + "/sprl2017_train.xml";
            getXMLImages(trainImage, true);
        }
    }

    /*******************************************************/
    // Loading Testing Images
    /*******************************************************/
    private void getTestImages() throws IOException {
        if (readFullData) {
            String testImage = path + "/testing.mat";
            getMatData(testImage, false);
        } else {
            String testImage = path + "/sprl2017_gold.xml";
            getXMLImages(testImage, false);
        }
    }
    /*******************************************************/
    // Loading data from XML file
    // if choose = true, trainData will be populated
    // if choose = false, testData will be populated
    /*******************************************************/
    private void getXMLImages(String file, Boolean choose) throws IOException {

        File f = new File(file);

        if (!f.exists()) {
            throw new IOException(file + " does not exist!");
        }
        NlpXmlReader reader = new NlpXmlReader(file , "SCENE", "SENTENCE", null, null);
        List<Document> documentList = reader.getDocuments();

        for (Document d: documentList){
            String name = d.getPropertyFirstValue("IMAGE");
            String s = name.substring(name.lastIndexOf("/")+1);
            String[] label = s.split("\\.");
            if (choose)
                trainingData.add(label[0]);
            else
                testData.add(label[0]);
        }
    }
    /*******************************************************/
    // Loading data from Mat file
    // if choose = true, trainData will be populated
    // if choose = false, testData will be populated
    /*******************************************************/
    private void getMatData(String file, Boolean choose) throws IOException {

        File f = new File(file);

        if (!f.exists()) {
            throw new IOException(file + " does not exist!");
        }
        MatFileReader matReader = new MatFileReader(file);

        double[][] data;

        if(choose)
            data = ((MLDouble) matReader.getMLArray("training")).getArray();
        else
            data = ((MLDouble) matReader.getMLArray("testing")).getArray();

        if (data.length > 1) {
            for (int i = 0; i < data.length; i++) {
                int imageId = (int) data[i][0];
                if(choose)
                    trainingData.add(Integer.toString(imageId));
                else
                    testData.add(Integer.toString(imageId));
            }
        }
    }
}
