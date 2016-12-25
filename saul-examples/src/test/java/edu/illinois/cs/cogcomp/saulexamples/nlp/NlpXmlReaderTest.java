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
        reader = new NlpXmlReader(getResourcePath("SpRL/2017/test.xml"));
        documents = reader.getDocuments("SCENE");
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
        List<Sentence> sentences = reader.getSentences("SENTENCE", docId);
        assertEquals("Document 1 sentence count", 2, sentences.size());
    }

    @Test
    public void phrase() {
        String docId = documents.get(0).getId();
        List<Phrase> phrases = reader.getPhrases("TRAJECTOR", docId);
        assertEquals("Document 1 Trajector phrase count", 3, phrases.size());
        reader.addPropertiesFromTag("TESTPROP", docId, phrases);
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
        List<Relation> relations = reader.getAllRelations("RELATION", NlpBaseElementTypes.Phrase, "trajector_id", NlpBaseElementTypes.Phrase,
                "spatial_indicator_id");
        List<Relation> doc1Relations = reader.getRelations("RELATION", NlpBaseElementTypes.Phrase, "trajector_id", NlpBaseElementTypes.Phrase,
                "spatial_indicator_id", docId);

        assertEquals("Relations count", 8, relations.size());
        assertEquals("first doc relations count", 3, doc1Relations.size());
        assertEquals("first relation trajector id", "T1", relations.get(0).getFirstId());
        assertEquals("first relation sparial indicator id", "S1", relations.get(0).getSecondId());
        assertEquals("first relation trajector type", NlpBaseElementTypes.Phrase, relations.get(0).getFirstType());
        assertEquals("first relation spatial indicator type", NlpBaseElementTypes.Phrase, relations.get(0).getSecondType());
        assertEquals("first relation RCC8_value", "behind", relations.get(0).getProperty("RCC8_value"));
    }

    private String getResourcePath(String relativePath) {
        return getClass().getClassLoader().getResource(relativePath).getPath();
    }
}
