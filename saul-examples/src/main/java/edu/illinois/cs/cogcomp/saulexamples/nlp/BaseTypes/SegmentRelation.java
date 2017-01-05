package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Umar Manzoor on 02/01/2017.
 */
public class SegmentRelation {
    private String imageId;
    private int firstSegmentId;
    private int secondSegmentId;
    private String relation;

    public SegmentRelation(String imgID, int sg_id1, int sg_id2, String rel)
    {
        imageId = imgID;
        firstSegmentId = sg_id1;
        secondSegmentId = sg_id2;
        relation = rel;
    }

    public String getRelation()
    {
        return relation;
    }

    public int getFirstSegmentId()
    {
        return firstSegmentId;
    }

    public int getSecondSegmentId()
    {
        return secondSegmentId;
    }

}
