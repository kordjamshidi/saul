package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-24.
 */
public class Phrase extends NlpBaseElement{

    private String documentId;
    private String sentenceId;

    public Phrase(){

    }

    public Phrase(String documentId, String sentenceId, String id, Integer start, Integer end, String text) {
        super(id, start, end, text);
        this.documentId = documentId;
        this.sentenceId = sentenceId;
    }

    @Override
    public NlpBaseElementTypes getType() {
        return NlpBaseElementTypes.Phrase;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(String sentenceId) {
        this.sentenceId = sentenceId;
    }
}