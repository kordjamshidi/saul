package edu.illinois.cs.cogcomp.saulexamples.data;

/**
 * Created by Umar Manzoor on 29/12/2016.
 */
public class Segment {
    private int segmentCode;
    private String segmentFeatures;
    private String segmentConcept;
    private String imageID;
    public Segment(String iD, int sC, String sF, String sCon)
    {
        imageID = iD;
        segmentCode = sC;
        segmentFeatures = sF;
        segmentConcept = sCon;
    }

    public String getAssociatedImageID()
    {
        return imageID;
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
        return imageID + ", " + segmentCode + ", " + segmentFeatures + ", " + segmentConcept;
    }

}
