package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Umar Manzoor on 29/12/2016.
 */
public class Segment {
    private int segmentID;
    private int segmentCode;
    private String segmentFeatures;
    private String segmentConcept;
    private String imageID;
    public Segment(String pID, int sID, int sC, String sF, String sCon)
    {
        imageID = pID;
        segmentID = sID;
        segmentCode = sC;
        segmentFeatures = sF;
        segmentConcept = sCon;
    }

    public String getAssociatedImageID()
    {
        return imageID;
    }

    public  int getSegmentID()
    {
        return segmentID;
    }

    public String getSegmentFeatures()
    {
        return segmentFeatures;
    }
    public  int getSegmentCode()
    {
        return segmentCode;
    }

    public String getSegmentConcept()
    {
        return segmentConcept;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return imageID + ", " + segmentID + ", " + segmentCode + ", " + segmentFeatures + ", " + segmentConcept;
    }

}
