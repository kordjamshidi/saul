package edu.illinois.cs.cogcomp.saulexamples.nlp.Xml;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.*;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.*;

/**
 * Created by Taher on 2016-12-18.
 */
public class NlpXmlReader {
    private XPath xpath = null;
    org.w3c.dom.Document xmlDocument;
    private String documentTagName = "DOCUMENT";
    private String sentenceTagName = "SENTENCE";
    private String phraseTagName = "PHRASE";
    private String tokenTagName = "TOKEN";
    private String startTagName = "start";
    private String endTagName = "end";
    private String textTagName = "text";
    private String idTagName = "id";

    public NlpXmlReader(String path) {
        this(new File(path));
    }

    public NlpXmlReader(File file) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            xmlDocument = dBuilder.parse(file);
            XPathFactory factory = XPathFactory.newInstance();
            xpath = factory.newXPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Document> getDocuments() {
        return getDocuments(documentTagName);
    }

    public List<Document> getDocuments(String tagName) {
        return getElementList(tagName, null, NlpBaseElementTypes.Document);
    }

    public List<Sentence> getAllSentences() {
        return getSentences(sentenceTagName, null);
    }

    public List<Sentence> getAllSentences(String tagName) {
        return getSentences(tagName, null);
    }

    public List<Sentence> getSentences(String parentId) {
        return getSentences(sentenceTagName, parentId);
    }

    public List<Sentence> getSentences(String tagName, String parentId) {
        return getElementList(tagName, parentId, NlpBaseElementTypes.Sentence);
    }

    public List<Phrase> getAllPhrases() {
        return getPhrases(phraseTagName, null);
    }

    public List<Phrase> getAllPhrases(String tagName) {
        return getPhrases(tagName, null);
    }

    public List<Phrase> getPhrases(String parentId) {
        return getPhrases(phraseTagName, parentId);
    }

    public List<Phrase> getPhrases(String tagName, String parentId) {
        return getElementList(tagName, parentId, NlpBaseElementTypes.Phrase);
    }

    public List<Token> getAllTokens() {
        return getTokens(tokenTagName, null);
    }

    public List<Token> getAllTokens(String tagName) {
        return getTokens(tagName, null);
    }

    public List<Token> getTokens(String parentId) {
        return getTokens(tokenTagName, parentId);
    }

    public List<Token> getTokens(String tagName, String parentId) {
        return getElementList(tagName, parentId, NlpBaseElementTypes.Token);
    }

    public List<Relation> getAllRelations(String tagName, String name, String firstIdProp, String secondIdProp) {
        return getRelations(tagName, name, firstIdProp, secondIdProp, null);
    }

    public List<Relation> getRelations(String tagName, String name, String firstIdProp, String secondIdProp, String parentId) {

        NodeList nodes = parentId == null ?
                getNodeList(tagName) :
                getNodeList(parentId, tagName);

        List<Relation> list = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element e = (Element) nodes.item(i);
                list.add(getRelation(name , firstIdProp, secondIdProp, e));
            }
        }
        return list;
    }

    private Relation getRelation(String name, String firstIdProp, String secondIdProp, Element e) {
        Relation r = new Relation(name);
        NamedNodeMap attributes = e.getAttributes();
        for (int j = 0; j < attributes.getLength(); j++) {
            r.setProperty(attributes.item(j).getNodeName(), attributes.item(j).getNodeValue());
        }
        r.setFirstId(r.getProperty(firstIdProp));
        r.setSecondId(r.getProperty(secondIdProp));
        return r;
    }

    public <T extends NlpBaseElement> void addPropertiesFromTag(String tagName, String parentId, List<T> list) {
        for (T e : list) {
            Node n = getNodeBySpan(tagName, e.getStart(), e.getEnd(), parentId);
            if (n != null) {
                for (int i = 0; i < n.getAttributes().getLength(); i++) {
                    e.setProperty(tagName + "_" + n.getAttributes().item(i).getNodeName(), n.getAttributes().item(i).getNodeValue());
                }
            }
        }
    }

    private <T extends NlpBaseElement> List<T> getElementList(String tagName, String parentId, NlpBaseElementTypes type) {
        NodeList nodes = parentId == null ?
                getNodeList(tagName) :
                getNodeList(parentId, tagName);
        List<T> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) nodes.item(i);
                NlpBaseElement s = getNlpBaseElement(e, type);
                list.add((T) s);
            }
        }
        return list;
    }

    private NlpBaseElement getNlpBaseElement(Element e, NlpBaseElementTypes type) {
        NlpBaseElement element = null;
        switch (type) {
            case Document:
                element = new Document();
                break;
            case Sentence:
                element = new Sentence();
                break;
            case Phrase:
                element = new Phrase();
                break;
            case Token:
                element = new Token();
                break;
        }
        element.setId(getStringAttribute(e, idTagName));
        element.setStart(getIntAttribute(e, startTagName));
        element.setEnd(getIntAttribute(e, endTagName));
        element.setText(getStringAttribute(e, textTagName));
        NamedNodeMap attributes = e.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            element.setProperty(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }

        return element;
    }

    private Node getNodeBySpan(String tagName, int start, int end, String parentId) {
        String query = parentId == null ?
                String.format("//%s[@start='%s' and @end='%s']", tagName, start, end) :
                String.format("//*[@id='%s']//%s[@start='%s' and @end='%s']", parentId, tagName, start, end);
        try {
            return (Node) xpath.evaluate(query, xmlDocument, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Node getNodeById(String id) {
        String query = String.format("//*[@id='%s']", id);
        try {
            return (Node) xpath.evaluate(query, xmlDocument, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private NodeList getNodeList(String parentId, String tagName) {
        String query = String.format("//*[@id='%s']//%s", parentId, tagName);
        try {
            return (NodeList) xpath.evaluate(query, xmlDocument, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private NodeList getNodeList(String tagName) {
        return xmlDocument.getElementsByTagName(tagName);
    }

    private String getStringAttribute(Element e, String name) {
        if (e.hasAttribute(name))
            return e.getAttribute(name);
        NodeList innerElement = e.getElementsByTagName(name);
        if (innerElement.getLength() > 0)
            return innerElement.item(0).getTextContent();
        return null;
    }

    private Integer getIntAttribute(Element e, String name) {
        if (e.hasAttribute(name))
            return Integer.parseInt(e.getAttribute(name));
        return -1;
    }

}
