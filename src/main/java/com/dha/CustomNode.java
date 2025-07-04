package com.dha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class CustomNode {
    public static boolean replaceTrackStopContains(Node node, int oldIndex, int newIndex){
        if ((node.getNodeName().equals("Track") && !getAttribute(node, "eventType").equals("StopTrack"))
            || (node.getNodeName().equals("Event") && !getAttribute(node, "eventName").equals("StopTrack"))){
                return false;
        }
        List<Integer> listTrackIndex = new ArrayList<>();
        Node event = node;
        if (!node.getNodeName().equals("Event"))
            event = getChild(node, "Event");
        for (int i = 0; i < event.getChildNodes().getLength(); i++){
            Node child = event.getChildNodes().item(i);
            if (child.getNodeName().equals("TrackObject")){
                if (oldIndex == Integer.parseInt(getAttribute(child, "id"))){
                    child.getAttributes().getNamedItem("id").setNodeValue(newIndex+"");
                    return true;
                }
            }
        }
        return false;
    }

    public static Integer[] getTrackStopContains(Node node){
        if ((node.getNodeName().equals("Track") && !getAttribute(node, "eventType").equals("StopTrack"))
            || (node.getNodeName().equals("Event") && !getAttribute(node, "eventName").equals("StopTrack"))){
                return null;
        }
        List<Integer> listTrackIndex = new ArrayList<>();
        Node event = node;
        if (!node.getNodeName().equals("Event"))
            event = getChild(node, "Event");
        for (int i = 0; i < event.getChildNodes().getLength(); i++){
            Node child = event.getChildNodes().item(i);
            if (child.getNodeName().equals("TrackObject")){
                listTrackIndex.add(Integer.parseInt(getAttribute(child, "id")));
            }
        }
        return listTrackIndex.toArray(new Integer[0]);
    }

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

    public static String getEventChildAttr(Node node, String tagName, String childName, String attr) {
        return getChildAttr(getChild(node, "Event"), tagName, childName, attr);
    }

    public static String getEventChildValue(Node node, String tagName, String childName) {
        return getChildValue(getChild(node, "Event"), tagName, childName);
    }

    public static String getChildValue(Node node, String tagName, String childName) {
        return getChildAttr(node, tagName, childName, "value");
    }

    public static String getChildAttr(Node node, String tagName, String childName, String attrName) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (tagName.equals("") || node.getChildNodes().item(i).getNodeName().equals(tagName)) {
                if (node.getChildNodes().item(i).getAttributes() != null &&
                    node.getChildNodes().item(i).getAttributes().getNamedItem("name").getNodeValue()
                        .equals(childName)) {
                    return node.getChildNodes().item(i).getAttributes().getNamedItem(attrName).getNodeValue();
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

    public static boolean setEventChildValue(Node node, String tagName, String childName, String changeAttr, StringOperator newValue){
        return setChildValue(getChild(node, "Event"), tagName, new String[]{childName}, changeAttr, newValue);
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
        return setChildValue(node, tagName, childName, "value", operator);
    }

    public static boolean setChildValue(Node node, String tagName, String[] childName, String changeAttr, StringOperator operator) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (node.getChildNodes().item(i).getNodeName().equals(tagName)) {
                if (Arrays.asList(childName).contains(node.getChildNodes().item(i).getAttributes().getNamedItem("name").getNodeValue())) {
                    Node attr = node.getChildNodes().item(i).getAttributes().getNamedItem(changeAttr);
                    attr.setNodeValue(operator.handle(node.getChildNodes().item(i).getAttributes().getNamedItem("value").getNodeValue()));
                    return true;
                }
            }
        }
        return false;
    }

    public static void appendEventNode(Node parent, Node newChild) {
        parent = getChild(parent, "Event");
        newChild = parent.getOwnerDocument().importNode(newChild, true);
        parent.appendChild(newChild);
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

    public static void addCondition(Node node, ConditionInfo info){
        Node condition = ProjectXML.getConditionNode(info);
        condition = node.getOwnerDocument().importNode(condition, true);
        node.insertBefore(condition, node.getFirstChild());
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
        if (node.getChildNodes() == null)
            return;
        for (int i = 0; i < node.getChildNodes().getLength(); i++){
            if (node.getChildNodes().item(i).getNodeName().equals("Condition")){
                if (Arrays.asList(indexList).contains(Integer.parseInt(node.getChildNodes().item(i).getAttributes().getNamedItem("id").getNodeValue()))){
                    node.removeChild(node.getChildNodes().item(i));
                    i--;
                }
            }
        }
    }

    public static Map<Integer, Boolean> getTrackConditions(Node track) {
        if (track.getChildNodes() == null)
            return new HashMap<>();
        Map<Integer, Boolean> listConditionIndex = new HashMap<>();
        for (int i = 0; i < track.getChildNodes().getLength(); i++) {
            Node child = track.getChildNodes().item(i);
            if (child.getNodeName().equals("Condition")) {
                listConditionIndex.put(Integer.parseInt(CustomNode.getAttribute(child, "id")),
                        CustomNode.getAttribute(child, "status").equals("true"));
            }
        }
        return listConditionIndex;
    }

    public static Node newNode(String nodeString){
        return ProjectXML.convertStringToDocument(nodeString).getDocumentElement();
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
