package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Umar Manzoor on 02/01/2017.
 */
public class SegmentRelation {
    private String imageID;
    private int segment_ID1;
    private int segment_ID2;
    private String relation;

    public SegmentRelation(String imgID, int sg_id1, int sg_id2, String rel)
    {
        imageID = imgID;
        segment_ID1 = sg_id1;
        segment_ID2 = sg_id2;
        relation = rel;
    }

    public String getRelation()
    {
        return relation;
    }

    public int getSegment_ID1()
    {
        return segment_ID1;
    }

    public int getSegment_ID2()
    {
        return segment_ID2;
    }

}
