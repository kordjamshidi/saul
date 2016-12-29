package edu.illinois.cs.cogcomp.saulexamples.data;

/**
 * Created by Umar Manzoor on 29/12/2016.
 */
public class Segment {
    private int segmentCode;
    private String segmentFeatures;
    private String segmentConcept;
    public Segment(int sC, String sF, String sCon)
    {
        segmentCode = sC;
        segmentFeatures = sF;
        segmentConcept = sCon;
    }

    public String getSegmentConcept()
    {
        return segmentConcept;
    }

    public String getSegmentFeatures()
    {
        return segmentFeatures;
    }
    public  int getSegmentCode()
    {
        return segmentCode;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return segmentCode + ", " + segmentFeatures + ", " + segmentConcept;
    }

}
