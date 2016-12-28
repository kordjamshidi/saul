package edu.illinois.cs.cogcomp.saulexamples.nlp;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.*;
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Taher on 2016-12-18.
 */
public class NlpXmlReaderTest {
    NlpXmlReader reader;
    private List<Document> documents;

    @Before
    public void setup() {
        reader = new NlpXmlReader(getResourcePath("SpRL/2017/test.xml"), "SCENE", "SENTENCE", "TRAJECTOR", null);
        documents = reader.getDocuments();
    }

    @Test
    public void document() {
        assertEquals("Document count", 2, documents.size());
        assertEquals("Document 1 Id", "sc1", documents.get(0).getId());
        assertEquals("Document 2 Id", "sc2", documents.get(1).getId());
        assertEquals("Document 1 test attribute", "test", documents.get(0).getProperty("test"));
    }

    @Test
    public void sentence() {
        String docId = documents.get(0).getId();
        List<Sentence> sentences = reader.getSentencesByParentId(docId);
        assertEquals("Document 1 sentence count", 2, sentences.size());
        assertEquals("sentence 1 documentId", docId, sentences.get(0).getDocument().getId());
    }

    @Test
    public void phrase() {
        String docId = documents.get(0).getId();
        List<Phrase> phrases = reader.getPhrasesByParentId(docId, "TESTPROP");
        assertEquals("Document 1 Trajector phrase count", 3, phrases.size());
        assertEquals("phrase 1 documentId", docId, phrases.get(0).getDocument().getId());
        assertEquals("phrase 1 sentenceId", "s601", phrases.get(0).getSentence().getId());

        assertEquals("first phrase additional prop[first_value]", "1", phrases.get(0).getProperty("TESTPROP_first_value"));
        assertEquals("first phrase additional prop[second_value]", "T1", phrases.get(0).getProperty("TESTPROP_second_value"));
        assertEquals("second phrase additional prop[first_value]", "2", phrases.get(1).getProperty("TESTPROP_first_value"));
        assertEquals("second phrase additional prop[second_value]", "T2", phrases.get(1).getProperty("TESTPROP_second_value"));
        assertEquals("third phrase additional prop[first_value]", null, phrases.get(2).getProperty("TESTPROP_first_value"));
        assertEquals("third phrase additional prop[second_value]", null, phrases.get(2).getProperty("TESTPROP_second_value"));

    }

    @Test
    public void relation() {
        String docId = documents.get(0).getId();
        List<Relation> relations = reader.getRelations("RELATION");
        List<Relation> doc1Relations = reader.getRelationsByParentId("RELATION", docId);

        assertEquals("Relations count", 8, relations.size());
        assertEquals("first doc relations count", 3, doc1Relations.size());
        assertEquals("first relation trajector id", "T1", relations.get(0).getProperty("trajector_id"));
        assertEquals("first relation sparial indicator id", "S1", relations.get(0).getProperty("spatial_indicator_id"));
        assertEquals("first relation RCC8_value", "behind", relations.get(0).getProperty("RCC8_value"));
    }

    private String getResourcePath(String relativePath) {
        return getClass().getClassLoader().getResource(relativePath).getPath();
    }
}
