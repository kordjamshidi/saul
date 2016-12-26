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
    private String documentTagName;
    private String sentenceTagName;
    private String phraseTagName;
    private String tokenTagName;
    private String startTagName = "start";
    private String endTagName = "end";
    private String textTagName = "text";
    private String idTagName = "id";

    public NlpXmlReader(String path, String documentTagName, String sentenceTagName, String phraseTagName, String tokenTagName) {
        this(new File(path), documentTagName, sentenceTagName, phraseTagName, tokenTagName);
    }

    public NlpXmlReader(File file, String documentTagName, String sentenceTagName, String phraseTagName, String tokenTagName) {
        this.setDocumentTagName(documentTagName);
        this.setSentenceTagName(sentenceTagName);
        this.setPhraseTagName(phraseTagName);
        this.setTokenTagName(tokenTagName);
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

    public String getDocumentTagName() {
        return documentTagName;
    }

    public void setDocumentTagName(String documentTagName) {
        this.documentTagName = documentTagName;
    }

    public String getSentenceTagName() {
        return sentenceTagName;
    }

    public void setSentenceTagName(String sentenceTagName) {
        this.sentenceTagName = sentenceTagName;
    }

    public String getPhraseTagName() {
        return phraseTagName;
    }

    public void setPhraseTagName(String phraseTagName) {
        this.phraseTagName = phraseTagName;
    }

    public String getTokenTagName() {
        return tokenTagName;
    }

    public void setTokenTagName(String tokenTagName) {
        this.tokenTagName = tokenTagName;
    }

    public String getStartTagName() {
        return startTagName;
    }

    public void setStartTagName(String startTagName) {
        this.startTagName = startTagName;
    }

    public String getEndTagName() {
        return endTagName;
    }

    public void setEndTagName(String endTagName) {
        this.endTagName = endTagName;
    }

    public String getTextTagName() {
        return textTagName;
    }

    public void setTextTagName(String textTagName) {
        this.textTagName = textTagName;
    }

    public String getIdTagName() {
        return idTagName;
    }

    public void setIdTagName(String idTagName) {
        this.idTagName = idTagName;
    }

    public List<Document> getDocuments(String... addPropertiesFromTag) {
        return getElementList(getDocumentTagName(), null, NlpBaseElementTypes.Document, addPropertiesFromTag);
    }

    public List<Sentence> getSentences(String... addPropertiesFromTag) {
        return getSentencesByParentId(null, addPropertiesFromTag);
    }

    public List<Sentence> getSentencesByParentId(String parentId, String... addPropertiesFromTag) {
        return getElementList(getSentenceTagName(), parentId, NlpBaseElementTypes.Sentence, addPropertiesFromTag);
    }

    public List<Phrase> getPhrases(String... addPropertiesFromTag) {
        return getPhrasesByParentId(null, addPropertiesFromTag);
    }

    public List<Phrase> getPhrasesByParentId(String parentId, String... addPropertiesFromTag) {
        return getElementList(getPhraseTagName(), parentId, NlpBaseElementTypes.Phrase, addPropertiesFromTag);
    }

    public List<Token> getTokens(String... addPropertiesFromTag) {
        return getTokensByParentId(null, addPropertiesFromTag);
    }

    public List<Token> getTokensByParentId(String parentId, String... addPropertiesFromTag) {
        return getElementList(getTokenTagName(), parentId, NlpBaseElementTypes.Token, addPropertiesFromTag);
    }

    public List<Relation> getRelations(String tagName, String name) {
        return getRelationsByParentId(tagName, name, null);
    }

    public List<Relation> getRelationsByParentId(String tagName, String name, String parentId) {

        NodeList nodes = parentId == null ?
                getNodeList(tagName) :
                getNodeList(parentId, tagName);

        List<Relation> list = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element e = (Element) nodes.item(i);
                list.add(getRelation(name, e));
            }
        }
        return list;
    }

    public <T extends NlpBaseElement> void addPropertiesFromTag(String tagName, List<T> list) {
        String pId = null;
        for (T e : list) {
            if (pId == null) {
                switch (e.getType()) {
                    case Document:
                        break;
                    case Sentence:
                        pId = ((Sentence) e).getDocumentId();
                        break;
                    case Phrase:
                        pId = ((Phrase) e).getDocumentId();
                        break;
                    case Token:
                        pId = ((Token) e).getDocumentId();
                        break;
                }
            }
            Node n = getNodeBySpan(tagName, e.getStart(), e.getEnd(), pId);
            if (n != null) {
                for (int i = 0; i < n.getAttributes().getLength(); i++) {
                    e.setProperty(tagName + "_" + n.getAttributes().item(i).getNodeName(), n.getAttributes().item(i).getNodeValue());
                }
            }
        }
    }

    private Relation getRelation(String name, Element e) {
        Relation r = new Relation(name);
        NamedNodeMap attributes = e.getAttributes();
        for (int j = 0; j < attributes.getLength(); j++) {
            r.setProperty(attributes.item(j).getNodeName(), attributes.item(j).getNodeValue());
        }
        r.setId(r.getProperty("id"));
        return r;
    }

    private <T extends NlpBaseElement> List<T> getElementList(String tagName, String parentId, NlpBaseElementTypes type, String... addPropertiesFromTag) {
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
        if (list.size() > 0) {
            for (String t : addPropertiesFromTag) {
                addPropertiesFromTag(t, list);
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
                Sentence s = new Sentence();
                s.setDocumentId(getStringAttribute(getAncestor(e, documentTagName), idTagName));
                element = s;
                break;
            case Phrase:
                Phrase p = new Phrase();
                p.setDocumentId(getStringAttribute(getAncestor(e, documentTagName), idTagName));
                p.setSentenceId(getStringAttribute(getAncestor(e, sentenceTagName), idTagName));
                element = p;
                break;
            case Token:
                Token t = new Token();
                t.setDocumentId(getStringAttribute(getAncestor(e, documentTagName), idTagName));
                t.setSentenceId(getStringAttribute(getAncestor(e, sentenceTagName), idTagName));
                t.setPhraseId(getStringAttribute(getAncestor(e, sentenceTagName), idTagName));
                element = t;
                break;
        }

        element.setId(getStringAttribute(e, getIdTagName()));
        element.setStart(getIntAttribute(e, getStartTagName()));
        element.setEnd(getIntAttribute(e, getEndTagName()));
        element.setText(getStringAttribute(e, getTextTagName()));
        NamedNodeMap attributes = e.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            element.setProperty(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }

        return element;
    }

    private Element getAncestor(Element n, String ancestorTagName) {
        if (n == null || ancestorTagName == null || n.getNodeName() == ancestorTagName)
            return n;
        Node parent = n.getParentNode();
        if (parent == null)
            return null;
        while (parent != null && parent.getNodeType() != Node.ELEMENT_NODE)
            parent = parent.getParentNode();
        return getAncestor((Element) parent, ancestorTagName);
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
