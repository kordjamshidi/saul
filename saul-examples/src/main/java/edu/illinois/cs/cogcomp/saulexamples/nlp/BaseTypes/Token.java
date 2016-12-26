package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-18.
 */
public class Token extends NlpBaseElement {

    private String documentId;
    private String sentenceId;
    private String phraseId;

    public Token() {

    }

    public Token(String documentId, String sentenceId, String phraseId, String id, Integer start, Integer end, String text) {
        super(id, start, end, text);
        this.setDocumentId(documentId);
        this.setSentenceId(sentenceId);
        this.setPhraseId(phraseId);
    }

    @Override
    public NlpBaseElementTypes getType() {
        return NlpBaseElementTypes.Token;
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

    public String getPhraseId() {
        return phraseId;
    }

    public void setPhraseId(String phraseId) {
        this.phraseId = phraseId;
    }
}
