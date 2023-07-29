package com.dha;

import java.util.Arrays;

import org.w3c.dom.Node;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class CustomNode {
    public static boolean checkEventType(Node node, String name){
        return checkEventType(node, new String[]{name});
    } 
    
    public static boolean checkEventType(Node node, String[] names){
        if (!node.getNodeName().equals("Track")){
            return false;
        }
        Node type = node.getAttributes().getNamedItem("eventType");
        return type != null && Arrays.asList(names).contains(type.getNodeValue());
    }

    public static Node stringToNode(String str){
        return ProjectXML.convertStringToDocument(str).getDocumentElement();
    }

    public static Node getChild(@This Node node, String tagName) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (node.getChildNodes().item(i).getNodeName().equals(tagName)) {
                return node.getChildNodes().item(i);
            }
        }
        return null;
    }

    public static String getChildValue(Node node, String tagName, String childName) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (node.getChildNodes().item(i).getNodeName().equals(tagName)) {
                if (node.getChildNodes().item(i).getAttributes().getNamedItem("name").getNodeValue()
                        .equals(childName)) {
                    return node.getChildNodes().item(i).getAttributes().getNamedItem("value").getNodeValue();
                }
            }
        }
        return null;
    }

    public static String getAttribute(Node node, String attrName) {
        try {
            return node.getAttributes().getNamedItem(attrName).getNodeValue();
        } catch (Exception e) {
            return null;
        }
    }

    public static void setEventDuration(Node node, float duration){
        Node event = node;
        if (event.getNodeName().equals("Track"))
            event = getChild(node, "Event");
        event.getAttributes().getNamedItem("length").setNodeValue(duration+"");
        event.getAttributes().getNamedItem("isDuration").setNodeValue("true");
    }

    public static void setEventTime(Node node, float time){
        Node event = node;
        if (event.getNodeName().equals("Track"))
            event = getChild(node, "Event");
        event.getAttributes().getNamedItem("time").setNodeValue(time+"");
    }

    public static boolean setEventChildValue(Node node, String tagName, String childName, String newValue){
        return setChildValue(getChild(node, "Event"), tagName, new String[]{childName}, newValue);
    }

    public static boolean setEventChildValue(Node node, String tagName, String childName, StringOperator newValue){
        return setChildValue(getChild(node, "Event"), tagName, new String[]{childName}, newValue);
    }

    public static boolean setEventChildValue(Node node, String tagName, String[] childName, String newValue){
        return setChildValue(getChild(node, "Event"), tagName, childName, newValue);
    }

    public static boolean setEventChildValue(Node node, String tagName, String[] childName, StringOperator newValue){
        return setChildValue(getChild(node, "Event"), tagName, childName, newValue);
    }

    public static boolean setChildValue(Node node, String tagName, String childName, String newValue) {
        return setChildValue(node, tagName, new String[]{childName}, newValue);
    }

    public static boolean setChildValue(Node node, String tagName, String childName, StringOperator newValue) {
        return setChildValue(node, tagName, new String[]{childName}, newValue);
    }

    public static boolean setChildValue(Node node, String tagName, String[] childName, String newValue) {
        return setChildValue(node, tagName, childName, (value)->newValue);
    }

    public static boolean setChildValue(Node node, String tagName, String[] childName, StringOperator operator) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (node.getChildNodes().item(i).getNodeName().equals(tagName)) {
                if (Arrays.asList(childName).contains(node.getChildNodes().item(i).getAttributes().getNamedItem("name").getNodeValue())) {
                    node.getChildNodes().item(i).getAttributes().getNamedItem("value").setNodeValue(operator.handle(node.getChildNodes().item(i).getAttributes().getNamedItem("value").getNodeValue()));
                    return true;
                }
            }
        }
        return false;
    }

    public static void appendNode(Node parent, Node newChild) {
        newChild = parent.getOwnerDocument().importNode(newChild, true);
        parent.appendChild(newChild);
    }

    public static void insert(Node parent, int index, Node newChild) throws Exception {
        int i = -1, j;
        for (j = 0; j < parent.getChildNodes().getLength(); j++) {
            if (!parent.getChildNodes().item(j).getNodeName().equals("#text")) {
                i++;
                if (i == index) {
                    break;
                }
            }
        }
        if (i < index) {
            throw new Exception("index too large");
        } else {
            newChild = parent.getOwnerDocument().importNode(newChild, true);
            parent.insertBefore(newChild, parent.getChildNodes().item(j));
        }
    }

    public static void remove(Node parent, int index) throws Exception {
        int i = -1, j;
        for (j = 0; j < parent.getChildNodes().getLength(); j++) {
            if (!parent.getChildNodes().item(j).getNodeName().equals("#text")) {
                i++;
                if (i == index) {
                    break;
                }
            }
        }
        if (i < index) {
            throw new Exception("index too large");
        } else {
            parent.removeChild(parent.getChildNodes().item(j));
        }
    }

    public static void remove(Node parent, String childTagName, String childName) {
        for (int j = 0; j < parent.getChildNodes().getLength(); j++) {
            if (parent.getChildNodes().item(j).getNodeName().equals(childTagName)
                    && parent.getChildNodes().item(j).getAttributes().getNamedItem("name").getNodeValue()
                            .equals(childName)) {
                parent.removeChild(parent.getChildNodes().item(j));
                break;
            }
        }
    }

    public static void clearEvent(Node parentNode){
        Node event;
        if (parentNode.getNodeName().equals("Event")){
            event = parentNode;
        }else{
            event = getChild(parentNode, "Event");
        }
        while(event.getChildNodes().getLength()!=0){
            event.removeChild(event.getFirstChild());
        }
    }

    public static void clearCondition(Node node){
        for (int i = 0; i < node.getChildNodes().getLength(); i++){
            if (node.getChildNodes().item(i).getNodeName().equals("Condition")){
                node.removeChild(node.getChildNodes().item(i));
                i--;
            }
        }
    }

    public static void removeCondition(Node node, int index){
        removeCondition(node, new Integer[]{index});
    }

    public static void removeCondition(Node node, Integer[] indexList){
        for (int i = 0; i < node.getChildNodes().getLength(); i++){
            if (node.getChildNodes().item(i).getNodeName().equals("Condition")){
                if (Arrays.asList(indexList).contains(Integer.parseInt(node.getChildNodes().item(i).getAttributes().getNamedItem("id").getNodeValue()))){
                    node.removeChild(node.getChildNodes().item(i));
                    i--;
                }
            }
        }
    }

    public static Node newNode(String tagName, Attribute[] attributes) {

        String xml = "";
        if (attributes != null) {
            xml = "<" + tagName;
            for (int i = 0; i < attributes.length; i++) {
                xml += " " + attributes[i].name + "=\"" + attributes[i].value + "\"";
            }
            xml += "/>";
        } else {
            xml = "<" + tagName + "/>";
        }
        Node node = ProjectXML.convertStringToDocument(xml).getDocumentElement();

        return node;
    }
}

class Attribute {
    String name;
    String value;

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
