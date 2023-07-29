package com.dha;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictCompress;

public class AOVAnalyzer {

    public static byte[] AOVDecompress(byte[] compressed) {
        String zstdDictPath = "zstd_dict.bin";
        byte[] zstdDict = DHAExtension.ReadAllBytes(zstdDictPath);

        int start = DHAExtension.indexOf(compressed, new byte[] { 40, (byte) 181, 47, (byte) 253 });
        if (start == -1)
            return null;
        compressed = Arrays.copyOfRange(compressed, start, compressed.length);
        return Zstd.decompress(compressed, zstdDict, (int) Zstd.decompressedSize(compressed));
    }

    public static byte[] AOVCompress(byte[] uncompress) {
        String zstdDictPath = "zstd_dict.bin";
        byte[] zstdDict = DHAExtension.ReadAllBytes(zstdDictPath);
        ZstdDictCompress dict = new ZstdDictCompress(zstdDict, 17);

        byte[] bytes = ByteBuffer.allocate(4).putInt(uncompress.length).array();
        byte[] cachebyte = new byte[8];
        cachebyte[0] = (byte) 34;
        cachebyte[1] = (byte) 74;
        cachebyte[2] = (byte) 0;
        cachebyte[3] = (byte) 239;
        cachebyte[4] = (byte) (bytes[3] < 0 ? 256 + bytes[3] : bytes[3]);
        cachebyte[5] = (byte) (bytes[2] < 0 ? 256 + bytes[2] : bytes[2]);
        cachebyte[6] = (byte) (bytes[1] < 0 ? 256 + bytes[1] : bytes[1]);
        cachebyte[7] = (byte) (bytes[0] < 0 ? 256 + bytes[0] : bytes[0]);

        return DHAExtension.mergeBytes(cachebyte, Zstd.compress(uncompress, dict));
    }
}

class AnalyzerType {
    public static String string = "TypeSystem.String";
    public static String int32 = "TypeSystem.Int32";
    public static String bool = "TypeSystem.Boolean";
    public static String stringArr = "TypeSystem.String[]";
}

class ProjectXML {
    public Document doc;

    public ProjectXML(String xml) throws Exception {
        doc = convertStringToDocument(xml);
        doc.getDocumentElement().normalize();
        if (!doc.getDocumentElement().getNodeName().equals("Project"))
            throw new Exception("isn't Project XML");
    }

    public void addComment(String commentText) {
        Comment comment = doc.createComment(commentText);
        doc.getElementsByTagName("Action").item(0).appendChild(comment);
    }

    public void insertActionChild(int index, Node child) {
        Node actionNode = doc.getElementsByTagName("Action").item(0);
        child = doc.importNode(child, true);
        int trackCount = 0;
        for (int i = 0; i < actionNode.getChildNodes().getLength(); i++) {
            if (actionNode.getChildNodes().item(i).getNodeName().equals("Track")) {
                trackCount++;
                if (trackCount == index + 1) {
                    index = i;
                    break;
                }
            }
        }
        actionNode.insertBefore(child, actionNode.getChildNodes().item(index));
    }

    public void appendActionChild(Node child) {
        Node actionNode = doc.getElementsByTagName("Action").item(0);
        child = doc.importNode(child, true);
        actionNode.appendChild(child);
    }

    public NodeList getNodeListByTagName(String tagName) {
        return doc.getElementsByTagName(tagName);
    }

    public List<Node> getTrackNodeByName(String trackName) {
        return getTrackNodeByName(trackName, true);
    }

    public List<Node> getTrackNodeByName(String trackName, boolean clone) {
        NodeList trackList = doc.getElementsByTagName("Track");
        List<Node> targetList = new ArrayList<>();
        for (int i = 0; i < trackList.getLength(); i++) {
            Node node = trackList.item(i);
            if (node.getAttributes().getNamedItem("trackName").getNodeValue().equals(trackName)) {
                if (clone)
                    targetList.add(node.cloneNode(true));
                else
                    targetList.add(node);
            }
        }
        return targetList;
    }

    public List<Node> getTrackNodeByType(String trackType) {
        return getTrackNodeByType(trackType, true);
    }

    public List<Node> getTrackNodeByType(String trackType, boolean clone) {
        NodeList trackList = doc.getElementsByTagName("Track");
        List<Node> targetList = new ArrayList<>();
        for (int i = 0; i < trackList.getLength(); i++) {
            Node node = trackList.item(i);
            if (node.getAttributes().getNamedItem("eventType").getNodeValue().equals(trackType)) {
                if (clone)
                    targetList.add(node.cloneNode(true));
                else
                    targetList.add(node);
            }
        }
        return targetList;
    }

    public List<Integer> getTrackIndexByType(String trackType) {
        NodeList trackList = doc.getElementsByTagName("Track");
        List<Integer> targetList = new ArrayList<>();
        for (int i = 0; i < trackList.getLength(); i++) {
            Node node = trackList.item(i);
            if (node.getAttributes().getNamedItem("eventType").getNodeValue().equals(trackType)) {
                targetList.add(i);
            }
        }
        return targetList;
    }

    public static void removeTrackCondition(Node track, int condiIndex) {
        if (track.getChildNodes() == null)
            return;
        for (int i = 0; i < track.getChildNodes().getLength(); i++) {
            if (track.getChildNodes().item(i).getNodeName().equals("Condition")) {
                if (Integer.parseInt(CustomNode.getAttribute(track.getChildNodes().item(i), "id")) == condiIndex) {
                    track.removeChild(track.getChildNodes().item(i));
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

    public static Node getOrConditionNode(int orConditionIndex, String guid, ConditionInfo[] conditionInfos) {
        String xml = "<Track trackName=\"CombinationConditionDuration" + orConditionIndex
                + "\" eventType=\"CombinationConditionDuration\" guid=\"" + guid
                + "\" enabled=\"true\" useRefParam=\"false\" refParamName=\"\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\"><Event eventName=\"CombinationConditionDuration\" time=\"0.000\" isDuration=\"false\" guid=\"e77a9cb5-f0bb-4bce-979c-8f51cbc83b95\"> <Array name=\"CondiTrackIds\" refParamName=\"\" useRefParam=\"false\" type=\"TrackObject\">";
        for (int i = 0; i < conditionInfos.length; i++) {
            xml += "<TrackObject id=\"" + conditionInfos[i].index + "\" guid=\"" + guid + "\" />";
        }
        xml += "</Array><Array name=\"CheckCondiTrackStatus\" refParamName=\"\" useRefParam=\"false\" type=\"bool\">";
        for (int i = 0; i < conditionInfos.length; i++) {
            xml += "<bool value=\"" + conditionInfos[i].status + "\" />";
        }
        xml += "</Array><Array name=\"IsOrConditions\" refParamName=\"\" useRefParam=\"false\" type=\"bool\">";
        for (int i = 0; i < conditionInfos.length; i++) {
            xml += "<bool value=\"" + conditionInfos[i].status + "\" />";
        }
        xml += "</Array></Event></Track>";
        return convertStringToDocument(xml).getDocumentElement();
    }

    public static Node getConditionNode(int conditionIndex, String guid) {
        return getConditionNode(conditionIndex, guid, true);
    }

    public static Node getConditionNode(int conditionIndex, String guid, boolean status) {
        return getConditionNode(new ConditionInfo(conditionIndex, guid, status));
    }

    public static Node getConditionNode(ConditionInfo info) {
        return convertStringToDocument(
                "      <Condition id=\"" + info.index + "\" guid=\"" + info.guid + "\" status=\"" + (info.status)
                        + "\"/>")
                .getDocumentElement().cloneNode(true);
    }

    public static Node getCheckSkinTickNode(int trackIndex, String guid, int skinId, int type) {
        String objId = "", objName = "";
        switch (type) {
            case 0:
                objId = "0";
                objName = "self";
                break;
            case 1:
                objId = "1";
                objName = "target";
                break;
        }
        return convertStringToDocument("<Track trackName=\"CheckSkinIdTick" + trackIndex
                + "\" eventType=\"CheckSkinIdTick\" guid=\"" + guid
                + "\" enabled=\"true\" r=\"0.000\" g=\"0.000\" b=\"0.000\" stopAfterLastEvent=\"true\">"
                + "\n  <Event eventName=\"CheckSkinIdTick\" time=\"0.000\" isDuration=\"false\">"
                + "\n    <TemplateObject name=\"targetId\" id=\"" + objId + "\" objectName=\"" + objName
                + "\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />"
                + "\n    <int name=\"skinId\" value=\"" + skinId + "\" refParamName=\"\" useRefParam=\"false\" />"
                + "\n    <bool name=\"bSkipLogicCheck\" value=\"true\" refParamName=\"\" useRefParam=\"false\" />"
                + "\n  </Event>"
                + "\n</Track>").getDocumentElement().cloneNode(true);
    }

    public static Node getCheckHeroTickNode(int trackIndex, int heroId) {

        return convertStringToDocument("<Track trackName=\"CheckHeroIdTick" + trackIndex
                + "\" eventType=\"CheckHeroIdTick\" guid=\"Mod_by_" + AOVModHelper.ChannelName + "_" + heroId
                + "\" enabled=\"true\" r=\"0.000\" g=\"0.000\" b=\"0.000\" stopAfterLastEvent=\"true\">"
                + "\n  <Event eventName=\"CheckHeroIdTick\" time=\"0.000\" isDuration=\"false\">"
                + "\n    <TemplateObject name=\"targetId\" id=\"1\" objectName=\"target\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />"
                + "\n    <int name=\"heroId\" value=\"" + heroId
                + "\" refParamName=\"\" useRefParam=\"false\" />"
                + "\n  </Event>"
                + "\n</Track>").getDocumentElement().cloneNode(true);
    }

    public void setValue(String tagname, String name, String newValue) {
        setValue(tagname, name, "", newValue);
    }

    public void replaceValue(String tagname, String name, String regex, String replace) {
        replaceValue(tagname, name, "", regex, replace);
    }

    public void setValue(String tagname, String name, String parentName, String newValue) {
        NodeList nodeList = doc.getElementsByTagName(tagname);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Node parentNameNode = node.getParentNode().getAttributes()
                    .getNamedItem(node.getParentNode().getNodeName().toLowerCase() + "Name");
            if (parentName.equals("") || (parentNameNode != null && parentNameNode.getNodeValue().equals(parentName))) {
                Node nameNode = node.getAttributes().getNamedItem("name");
                if (nameNode != null && nameNode.getNodeValue().equals(name)) {
                    node.getAttributes().getNamedItem("value").setNodeValue(newValue);
                }
            }
        }
    }

    public void setValue(String tagname, String name, StringOperator operator) {
        setValue(tagname, new String[] { name }, "", operator);
    }

    public void setValue(String tagname, String[] nameArr, StringOperator operator) {
        setValue(tagname, nameArr, "", operator);
    }

    public void setValue(String tagname, String name, String parentName, StringOperator operator) {
        setValue(tagname, new String[] { name }, parentName, operator);
    }

    public void setValue(String tagname, String[] nameArr, String parentName, StringOperator operator) {
        NodeList nodeList = doc.getElementsByTagName(tagname);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Node parentNameNode = node.getParentNode().getAttributes()
                    .getNamedItem(node.getParentNode().getNodeName().toLowerCase() + "Name");
            if (parentName.equals("") || (parentNameNode != null && parentNameNode.getNodeValue().equals(parentName))) {
                Node nameNode = node.getAttributes().getNamedItem("name");
                if (nameNode != null
                        && (nameArr.length == 0 || Arrays.asList(nameArr).contains(nameNode.getNodeValue()))) {
                    node.getAttributes().getNamedItem("value")
                            .setNodeValue(operator.handle(node.getAttributes().getNamedItem("value").getNodeValue()));
                }
            }
        }
    }

    public void replaceValue(String tagname, String name, String parentName, String regex, String replace) {
        NodeList nodeList = doc.getElementsByTagName(tagname);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Node parentNameNode = node.getParentNode().getAttributes()
                    .getNamedItem(node.getParentNode().getNodeName().toLowerCase() + "Name");
            if (parentName.equals("") || (parentNameNode != null && parentNameNode.getNodeValue().equals(parentName))) {
                Node nameNode = node.getAttributes().getNamedItem("name");
                if (nameNode != null && nameNode.getNodeValue().equals(name)) {
                    node.getAttributes().getNamedItem("value").setNodeValue(
                            node.getAttributes().getNamedItem("value").getNodeValue().replaceAll(regex, replace));
                }
            }
        }
    }

    public void replaceActionResourceNames(String oldResourceName, String newResourceName) {
        NodeList stringNode = doc.getElementsByTagName("String");
        for (int i = 0; i < stringNode.getLength(); i++) {
            Node node = stringNode.item(i);
            if (!node.getParentNode().getAttributes().getNamedItem("eventName").getNodeValue()
                    .equals("TriggerParticleTick")
                    || !node.getAttributes().getNamedItem("name").getNodeValue().equals("resourceName"))
                continue;
            node.getAttributes().getNamedItem("value")
                    .setNodeValue(node.getAttributes().getNamedItem("value").getNodeValue() + "/testsuccess2");
        }
    }

    public void changeCheckVirtual() {
        List<Node> trackList = getTrackNodeByType("CheckSkinIdVirtualTick", false);
        for (int i = 0; i < trackList.size(); i++) {
            Node track = trackList.get(i);
            track.getAttributes().getNamedItem("eventType").setNodeValue(track.getAttributes().getNamedItem("eventType")
                    .getNodeValue().replaceAll("(?i)CheckSkinIdVirtualTick", "CheckSkinIdTick"));
            track.getAttributes().getNamedItem("trackName").setNodeValue(track.getAttributes().getNamedItem("trackName")
                    .getNodeValue().replaceAll("(?i)CheckSkinIdVirtualTick", "CheckSkinIdTick"));
            Node event = track.getChildNodes().item(track.getChildNodes().getLength() - 2);
            event.getAttributes().getNamedItem("eventName").setNodeValue(event.getAttributes().getNamedItem("eventName")
                    .getNodeValue().replaceAll("(?i)CheckSkinIdVirtualTick", "CheckSkinIdTick"));
            NodeList eventChild = event.getChildNodes();
            for (int j = 0; j < eventChild.getLength(); j++) {
                Node child = eventChild.item(j);
                if (child.getNodeName().equals("bool")
                        && child.getAttributes().getNamedItem("name").getNodeValue().equals("useNegateValue")) {
                    child.getAttributes().getNamedItem("name").setNodeValue("bEqual");
                    if (child.getAttributes().getNamedItem("value").getNodeValue().equals("true")) {
                        child.getAttributes().getNamedItem("value").setNodeValue("false");
                    } else {
                        event.removeChild(child);
                        j--;
                    }
                }
            }
        }
    }

    public void modEffect(int idMod, boolean isAwakeSkin) {
        setValue("String", new String[] { "resourceName", "resourceName2", "prefabName", "prefab" },
                (StringOperator) (value) -> {
                    if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/"))
                        return value;
                    String[] split = value.split("/");
                    String newValue;
                    if (!isAwakeSkin) {
                        newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod + "/"
                                + split[split.length - 1];
                    } else {
                        newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5/"
                                + split[split.length - 1];
                    }
                    return newValue;
                });
        setValue("bool", "bAllowEmptyEffect", "false");
    }

    public void modSound(int idMod){
        modSound(idMod, -1, -1);
    }

    public void modSound(int idMod, int levelSFXUnlock, int levelVOXUnlock) {
        List<Node> playSoundTick = getTrackNodeByType("PlayHeroSoundTick", true);
        int skin = idMod % 100;
        setValue("String", "eventName", "PlayHeroSoundTick", (value) -> {
            if (value.toLowerCase().contains("_skin"))
                return value;
            if (levelSFXUnlock == -1 && levelVOXUnlock == -1) {
                return value + "_Skin" + skin;
            } else {
                if (value.contains("_VO") || value.toLowerCase().contains("voice")) {
                    return value + "_Skin" + skin + "_AW" + levelVOXUnlock;
                } else {
                    return value + "_Skin" + skin + "_AW" + levelSFXUnlock;
                }
            }
        });
        for (Node node : playSoundTick) {
            appendActionChild(node);
        }
    }

    public String getXmlString() {
        return convertDocumentToString(doc);
    }

    public static String nodeToString(Node node) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            tf.setAttribute("indent-number", 2);
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(out));
            return out.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String prettierXml(String xml) {
        return convertDocumentToString(convertStringToDocument(xml));
    }

    public static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            doc.setXmlStandalone(true);
            tf.setAttribute("indent-number", 2);
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(out));
            return out.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Document convertStringToDocument(String xmlStr) {
        xmlStr = xmlStr.trim().replaceAll("^([\\W]+)<", "<");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

class ConditionInfo {
    int index;
    String guid;
    boolean status;

    public ConditionInfo(int index, String guid, boolean status) {
        this.index = index;
        this.guid = guid;
        this.status = status;
    }

    public ConditionInfo changeStatus(boolean status) {
        return new ConditionInfo(index, guid, status);
    }
}

interface StringOperator {
    public String handle(String s);
}

class LanguageMap {
    Map<String, String> languageMap;

    public LanguageMap(String s) {
        languageMap = new HashMap<>();

        String[] lines = s.split("\\r?\\n|\\r");
        for (String line : lines) {
            String[] split = line.split(" = ");
            languageMap.put(split[0], split[1]);
        }
    }

    public String getValue(String key) {
        if (languageMap.containsKey(key)) {
            return languageMap.get(key);
        } else {
            return null;
        }
    }
}

class ListDeviceSupport {
    public List<String> deviceList;

    public ListDeviceSupport() {
        deviceList = new ArrayList<>();
    }

    public ListDeviceSupport(byte[] bytes) {
        deviceList = new ArrayList<>();
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S')
            start = DHAExtension.bytesToInt(bytes, 132);
        else
            start = 0;
        if (start == bytes.length)
            return;
        int count;
        while (start < bytes.length) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            String device = new String(Arrays.copyOfRange(bytes, start + 12, start + count + 2));
            deviceList.add(device);
            start += count;
        }
    }

    public void addNewDevice(String deviceCode) {
        deviceList.add(deviceCode);
    }

    public byte[] getBytes() {
        byte[] bytes = DHAExtension.ReadAllBytes(".special/newmses.bytes");
        for (int i = 0; i < deviceList.size(); i++) {
            bytes = DHAExtension.mergeBytes(bytes, DHAExtension.toBytes(deviceList.get(i).length() + 10),
                    DHAExtension.toBytes(i + 1),
                    DHAExtension.toBytes(deviceList.get(i).length() + 1),
                    deviceList.get(i).getBytes(), new byte[] { 0, 0 });
        }
        bytes = DHAExtension.replaceBytes(bytes, 12, 16, DHAExtension.toBytes(deviceList.size()));
        return bytes;
    }
}

class ListMarkElement {
    private byte[] bytes;
    public List<MarkElement> markElements;
    private List<Integer> listHeroId;

    public ListMarkElement(byte[] bytes) {
        this.bytes = bytes.clone();
        markElements = new ArrayList<>();
        listHeroId = new ArrayList<>();
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S')
            start = DHAExtension.bytesToInt(bytes, 132);
        else
            start = 0;
        if (start == bytes.length)
            return;
        int count;
        while (start < bytes.length) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            MarkElement m = new MarkElement(Arrays.copyOfRange(bytes, start, start + count));
            if (m.markEffects.size() != 0)
                listHeroId.add(m.getHeroId());
            markElements.add(m);
            start += count;
        }
    }

    public boolean containsHeroId(int heroId) {
        return listHeroId.contains(heroId);
    }

    public void replaceMarkEffect(int heroId, String regex, String replace) {
        for (int i = 0; i < markElements.size(); i++) {
            if (markElements.get(i).getHeroId() == heroId) {
                markElements.get(i).replaceMarkEffect(regex, replace);
            }
        }
    }

    public byte[] getBytes() {
        byte[] childBytes = new byte[0];
        for (MarkElement m : markElements) {
            childBytes = DHAExtension.mergeBytes(childBytes, m.getBytes());
        }
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S') {
            start = DHAExtension.bytesToInt(bytes, 132);
            bytes = DHAExtension.replaceBytes(bytes, 12, 16, DHAExtension.toBytes(markElements.size()));
        } else
            start = 0;
        return DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, start), childBytes);
    }
}

class MarkElement {
    private byte[] bytes;
    public int markId;
    public String markPath;
    public List<String> markEffects;
    public List<Integer> markEffectStarts;

    public MarkElement(byte[] bytes) {
        this.bytes = bytes.clone();
        markId = DHAExtension.bytesToInt(bytes, 4);
        int start, count;
        start = 12;
        count = DHAExtension.bytesToInt(bytes, start) + 4;
        start = start + count;
        count = DHAExtension.bytesToInt(bytes, start) + 4;
        start = start + count;
        count = DHAExtension.bytesToInt(bytes, start) + 4;
        markPath = new String(Arrays.copyOfRange(bytes, start + 4, start + count - 1));
        start = start + count + 42;
        markEffects = new ArrayList<>();
        markEffectStarts = new ArrayList<>();
        while (start < bytes.length && DHAExtension.bytesToInt(bytes, start) > 0
                && DHAExtension.bytesToInt(bytes, start) < bytes.length - start) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            String effect = new String(Arrays.copyOfRange(bytes, start + 4, start + count - 1));
            if (!effect.equals("")) {
                markEffects.add(effect);
                markEffectStarts.add(start);
            }
            start = start + count;
        }
    }

    public void replaceMarkEffect(String regex, String replace) {
        for (int i = 0; i < markEffects.size(); i++) {
            setMarkEffect(i, markEffects.get(i).replaceAll(regex, replace));
        }
    }

    public void setMarkEffect(int index, String newMark) {
        if (index < 0 || index >= markEffects.size()) {
            return;
        }
        int deltalength = newMark.length() - markEffects.get(index).length();
        bytes = DHAExtension.replaceBytes(bytes, markEffectStarts.get(index) + 4,
                markEffectStarts.get(index) + DHAExtension.bytesToInt(bytes, markEffectStarts.get(index)) + 3,
                newMark.getBytes());
        markEffects.set(index, newMark);
        byte[] barr = DHAExtension.toBytes(DHAExtension.bytesToInt(bytes, markEffectStarts.get(index)) + deltalength);
        for (int i = 0; i < barr.length; i++) {
            bytes[markEffectStarts.get(index) + i] = barr[i];
        }
        for (int i = index + 1; i < markEffectStarts.size(); i++) {
            markEffectStarts.set(i, markEffectStarts.get(i) + deltalength);
        }
        barr = DHAExtension.toBytes(bytes.length - 4);
        for (int i = 0; i < barr.length; i++) {
            bytes[i] = barr[i];
        }
    }

    public int getHeroId() {
        if (markId < 10000)
            return -1;
        else if (markId < 100000)
            return markId / 100;
        else if (markId < 1000000)
            return Integer.parseInt((markId + "").substring(1, 4));
        else
            return -1;
    }

    public byte[] getBytes() {
        return bytes;
    }
}

class ListBulletElement {
    private byte[] bytes;
    public List<BulletElement> bulletElements;
    private List<Integer> heroIdList;

    public ListBulletElement(byte[] bytes) {
        this.bytes = bytes.clone();
        bulletElements = new ArrayList<>();
        heroIdList = new ArrayList<>();
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S') {
            start = DHAExtension.bytesToInt(bytes, 132);
        } else
            start = 0;
        if (start == bytes.length)
            return;
        int count;
        while (start < bytes.length) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            BulletElement b = new BulletElement(Arrays.copyOfRange(bytes, start, start + count));
            heroIdList.add(b.getHeroId());
            bulletElements.add(b);
            start += count;
        }
    }

    public boolean containsHeroId(int heroId) {
        return heroIdList.contains(heroId);
    }

    public void replaceBulletEffect(int heroId, String regex, String replace) {
        for (int i = 0; i < bulletElements.size(); i++) {
            if (bulletElements.get(i).getHeroId() == heroId) {
                bulletElements.get(i).setEffectName((value) -> value.replaceAll(regex, replace));
            }
        }
    }

    public byte[] getBytes() {
        byte[] childBytes = new byte[0];
        for (BulletElement b : bulletElements) {
            childBytes = DHAExtension.mergeBytes(childBytes, b.getBytes());
        }
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S') {
            start = DHAExtension.bytesToInt(bytes, 132);
            bytes = DHAExtension.replaceBytes(bytes, 12, 16, DHAExtension.toBytes(bulletElements.size()));
        } else
            start = 0;
        return DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, start), childBytes);
    }
}

class BulletElement {
    private byte[] bytes;
    private int effectStart;
    public int bulletId;
    public byte[] bulletName;
    public String effectName;

    public BulletElement(byte[] bytes) {
        this.bytes = bytes.clone();
        bulletId = DHAExtension.bytesToInt(bytes, 4);
        int start, count;
        start = 9;
        count = DHAExtension.bytesToInt(bytes, start) + 4;
        bulletName = Arrays.copyOfRange(bytes, start + 4, start + count - 1);
        start = start + count + 41;
        effectStart = start;
        count = DHAExtension.bytesToInt(bytes, start) + 4;
        effectName = new String(Arrays.copyOfRange(bytes, start + 4, start + count - 1));
    }

    public void setEffectName(StringOperator operator) {
        String effect = new String(Arrays.copyOfRange(bytes, effectStart + 4,
                effectStart + DHAExtension.bytesToInt(bytes, effectStart) + 3));
        effect = operator.handle(effect);
        bytes = DHAExtension.replaceBytes(bytes, effectStart + 4,
                effectStart + DHAExtension.bytesToInt(bytes, effectStart) + 3, effect.getBytes());
        int[] indexChanges = new int[] { 0, effectStart };
        for (int changeAt : indexChanges) {
            byte[] barr = DHAExtension.toBytes(bytes.length - changeAt - 4);
            for (int i = 0; i < barr.length; i++) {
                bytes[changeAt + i] = barr[i];
            }
        }
    }

    public String getBulletName() {
        return new String(bulletName);
    }

    public int getHeroId() {
        String id = "";
        String name = new String(bulletName);
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) >= '0' && name.charAt(i) <= '9') {
                id += name.charAt(i);
            } else {
                break;
            }
        }
        if (id.length() == 4 && id.charAt(0) == '3') {
            id = id.substring(1);
        }
        if (id.equals(""))
            id = "-1";
        return Integer.parseInt(id);
    }

    public byte[] getBytes() {
        return bytes;
    }
}

class ListMotionElement {
    private byte[] bytes;
    public List<MotionElement> motionElements;

    public ListMotionElement(byte[] bytes) throws Exception {
        this.bytes = bytes.clone();
        motionElements = new ArrayList<>();
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S')
            start = DHAExtension.bytesToInt(bytes, 132);
        else
            start = 0;
        if (start == bytes.length)
            return;
        int count;
        while (start < bytes.length) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            MotionElement s = new MotionElement(Arrays.copyOfRange(bytes, start, start + count));
            motionElements.add(s);
            start += count;
        }
    }

    public void showMotionCodes(int heroId){
        showMotionCodes(heroId, 0);
    }

    public void showMotionCodes(int heroId, int space){
        String s = "";
        for (int i = 0; i < space; i++){
            s+=" ";
        }
        for (MotionElement m : motionElements){
            if (m.getHeroId() == heroId){
                System.out.println(s + m.motionCodes);
            }
        }
    }

    public void copyMotion(int heroId, String baseMotionCode, String newMotionCode) throws Exception{
        copyMotion(heroId, new String[]{baseMotionCode}, newMotionCode);
    }

    public void copyMotion(int heroId, String[] baseMotionCode, String newMotionCode) throws Exception{
        List<Integer> baseIndexs=null;
        int newIndex=-1;
        for (int i = 0; i < motionElements.size(); i++){
            if (motionElements.get(i).getHeroId() == heroId){
                if (motionElements.get(i).motionCodes.contains(newMotionCode)){
                    newIndex = i;
                }else if (DHAExtension.listContainsElementFromOther(motionElements.get(i).motionCodes.toArray(new String[0]), baseMotionCode)){
                    if (baseIndexs == null)
                        baseIndexs = new ArrayList<>();
                    baseIndexs.add(i);
                }
            }
        }
        if (baseIndexs != null && newIndex != -1){
            for (int baseIndex : baseIndexs){
                int oldIndex = motionElements.get(baseIndex).getIndex();
                motionElements.set(baseIndex, new MotionElement(motionElements.get(newIndex).getBytes()));
                motionElements.get(baseIndex).setIndex(oldIndex);
            }
        }else{
            throw new Exception("not found code " + baseMotionCode + " or " + newMotionCode);
        }
    }

    public byte[] getBytes() {
        byte[] childBytes = new byte[0];
        for (int i = 0; i < motionElements.size(); i++) {
            // motionElements.get(i).setIndex(i+1);
            childBytes = DHAExtension.mergeBytes(childBytes, motionElements.get(i).getBytes());
        }
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S') {
            start = DHAExtension.bytesToInt(bytes, 132);
            bytes = DHAExtension.replaceBytes(bytes, 12, 16, DHAExtension.toBytes(motionElements.size()));
        } else
            start = 0;
        return DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, start), childBytes);
    }
}

class MotionElement {
    private byte[] bytes;
    public List<String> motionCodes;

    public MotionElement(byte[] bytes) throws Exception {
        this.bytes = bytes.clone();
        motionCodes = new ArrayList<>();

        int index=9;
        while (index < bytes.length-5) {
            int length = DHAExtension.bytesToInt(bytes, index);
            if (length == 0)
                throw new Exception("Length error");
            if (length == 1){
                index += 5;
                continue;
            }
            motionCodes.add(new String(Arrays.copyOfRange(bytes, index+4, index+length+3)));
            index += length + 4;
        }
    }

    public int getHeroId(){
        return DHAExtension.bytesToInt(bytes, bytes.length-5);
    }

    public void setIndex(int index){
        bytes = DHAExtension.replaceBytes(bytes, 4, 8, DHAExtension.toBytes(index));
    }

    public int getIndex(){
        return DHAExtension.bytesToInt(bytes, 4);
    }

    public byte[] getBytes() {
        return bytes;
    }
}

class ListCharComponent {
    private byte[] bytes;
    public List<CharComponent> charComponents;

    public ListCharComponent(byte[] bytes) {
        this.bytes = bytes.clone();
        charComponents = new ArrayList<>();
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S')
            start = DHAExtension.bytesToInt(bytes, 132);
        else
            start = 0;
        if (start == bytes.length)
            return;
        int count;
        while (start < bytes.length) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            CharComponent s = new CharComponent(Arrays.copyOfRange(bytes, start, start + count));
            charComponents.add(s);
            start += count;
        }
    }

    public void removeSkinComponent(int skinId) {
        for (int i = 0; i < charComponents.size(); i++) {
            if (charComponents.get(i).containsId(skinId)) {
                charComponents.remove(i);
                i--;
            }
        }
    }

    public byte[] getBytes() {
        byte[] childBytes = new byte[0];
        for (CharComponent e : charComponents) {
            childBytes = DHAExtension.mergeBytes(childBytes, e.getBytes());
        }
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S') {
            start = DHAExtension.bytesToInt(bytes, 132);
            bytes = DHAExtension.replaceBytes(bytes, 12, 16, DHAExtension.toBytes(charComponents.size()));
        } else
            start = 0;
        return DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, start), childBytes);
    }
}

class CharComponent {
    private byte[] bytes;
    public final int componentId;
    public List<Integer> skinIdList;

    public CharComponent(byte[] bytes) {
        this.bytes = bytes.clone();
        skinIdList = new ArrayList<>();
        componentId = DHAExtension.bytesToInt(bytes, 4);

        int start = 155;
        if (DHAExtension.countMatches(bytes, "_##".getBytes()) == 2) {
            start = 174;
        }
        int skinId;
        while ((skinId = DHAExtension.bytesToInt(bytes, start)) > 9999 && skinId < 100000) {
            skinIdList.add(skinId);
            start += 29;
        }
    }

    public boolean containsId(int skinId) {
        return skinIdList.contains(skinId);
    }

    public byte[] getBytes() {
        return bytes;
    }
}

class ListSoundElement {
    private byte[] bytes;
    public List<SoundElement> soundElements;

    public ListSoundElement(byte[] bytes) {
        this.bytes = bytes.clone();
        soundElements = new ArrayList<>();
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S')
            start = DHAExtension.bytesToInt(bytes, 132);
        else
            start = 0;
        if (start == bytes.length)
            return;
        int count;
        while (start < bytes.length) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            SoundElement s = new SoundElement(Arrays.copyOfRange(bytes, start, start + count));
            soundElements.add(s);
            start += count;
        }
    }

    public void copySound(int baseId, int targetId) {
        copySound(baseId, targetId, true);
    }

    public void copySound(int baseId, int targetId, boolean removeOldSound) {
        List<SoundElement> targetSounds = new ArrayList<>();
        for (int i = 0; i < soundElements.size(); i++) {
            if (soundElements.get(i).skinId == baseId) {
                if (removeOldSound) {
                    soundElements.remove(i);
                    i--;
                }
            } else if (soundElements.get(i).skinId == targetId) {
                SoundElement sound = new SoundElement(soundElements.get(i).getBytes());
                sound.setSkinId(baseId, removeOldSound);
                targetSounds.add(sound);
            }
        }
        soundElements.addAll(targetSounds);
    }

    public void setSound(int baseId, List<SoundElement> targetSounds) {
        for (int i = 0; i < soundElements.size(); i++) {
            if (soundElements.get(i).skinId == baseId) {
                soundElements.remove(i);
                i--;
            }
        }
        for (int i = 0; i < targetSounds.size(); i++) {
            targetSounds.get(i).setSkinId(baseId);
        }
        soundElements.addAll(targetSounds);
    }

    public byte[] getBytes() {
        byte[] childBytes = new byte[0];
        for (SoundElement e : soundElements) {
            childBytes = DHAExtension.mergeBytes(childBytes, e.getBytes());
        }
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S') {
            start = DHAExtension.bytesToInt(bytes, 132);
            bytes = DHAExtension.replaceBytes(bytes, 12, 16, DHAExtension.toBytes(soundElements.size()));
        } else
            start = 0;
        return DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, start), childBytes);
    }
}

class SoundElement {
    private byte[] bytes;
    public final String soundId;
    public int skinId;

    public SoundElement(byte[] bytes) {
        this.bytes = bytes.clone();
        int i = DHAExtension.bytesToInt(bytes, 4);
        if (i > 99999) {
            if (i < 10000000) {
                soundId = (i + "").substring(5);
                skinId = Integer.parseInt((i + "").substring(0, 5));
            } else {
                soundId = (i + "").substring(7);
                skinId = Integer.parseInt((i + "").substring(0, 7));
            }
        } else {
            soundId = i + "";
            skinId = 0;
        }
    }

    public void setSkinId(int skinId) {
        setSkinId(skinId, true);
    }

    public void setSkinId(int skinId, boolean changeSoundId) {
        if (this.skinId == 0)
            return;
        bytes = DHAExtension.replaceBytes(bytes, DHAExtension.toBytes(this.skinId), DHAExtension.toBytes(skinId));
        if (changeSoundId)
            bytes = DHAExtension.replaceBytes(bytes, DHAExtension.toBytes(Integer.parseInt(this.skinId + soundId)),
                    DHAExtension.toBytes(Integer.parseInt(skinId + soundId)));
        this.skinId = skinId;
    }

    public byte[] getBytes() {
        return bytes;
    }
}

class ListLabelElement {
    private byte[] bytes;
    public List<LabelElement> labelElements;
    public Map<Integer, Integer> labelIndexMap;

    public ListLabelElement(byte[] bytes) {
        this.bytes = bytes.clone();
        labelElements = new ArrayList<>();
        labelIndexMap = new HashMap<>();
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S')
            start = DHAExtension.bytesToInt(bytes, 132);
        else
            start = 0;
        if (start == bytes.length)
            return;
        int count;
        while (start < bytes.length) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            LabelElement l = new LabelElement(Arrays.copyOfRange(bytes, start, start + count));
            labelIndexMap.put(l.labelId, labelElements.size());
            labelElements.add(l);
            start += count;
        }
    }

    public void setLabel(int sourceId, byte[] labelBytes) throws Exception {
        if (!labelIndexMap.containsKey(sourceId)) {
            throw new Exception("not found label for id " + sourceId);
        }
        labelElements.set(labelIndexMap.get(sourceId), new LabelElement(labelBytes));
        labelElements.get(labelIndexMap.get(sourceId)).setLabelIndex(sourceId % 100);
        labelElements.get(labelIndexMap.get(sourceId)).setHeroId(sourceId / 100);
        labelElements.get(labelIndexMap.get(sourceId)).setLabelId(sourceId);
    }

    public int copyLabel(int sourceId, int targetId) throws Exception {
        if (!labelIndexMap.containsKey(sourceId)) {
            return 1;
        } else if (!labelIndexMap.containsKey(targetId)) {
            return 2;
        }

        byte[] bytes = labelElements.get(labelIndexMap.get(targetId)).getBytes();
        labelElements.set(labelIndexMap.get(sourceId), new LabelElement(bytes));
        labelElements.get(labelIndexMap.get(sourceId)).setLabelIndex(sourceId % 100);
        labelElements.get(labelIndexMap.get(sourceId)).setHeroId(sourceId / 100);
        labelElements.get(labelIndexMap.get(sourceId)).setLabelId(sourceId);
        return 0;
    }

    public byte[] getBytes() {
        byte[] childBytes = new byte[0];
        for (LabelElement e : labelElements) {
            childBytes = DHAExtension.mergeBytes(childBytes, e.getBytes());
        }
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S') {
            start = DHAExtension.bytesToInt(bytes, 132);
            bytes = DHAExtension.replaceBytes(bytes, 12, 16, DHAExtension.toBytes(labelElements.size()));
        } else
            start = 0;
        return DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, start), childBytes);
    }
}

class LabelElement {
    private byte[] bytes;
    public int labelId;
    public int labelIndex;
    public int heroId;

    public LabelElement(byte[] bytes) {
        this.bytes = bytes.clone();

        labelId = DHAExtension.bytesToInt(bytes, 4);
        labelIndex = DHAExtension.bytesToInt(bytes, 36);
        heroId = DHAExtension.bytesToInt(bytes, 8);
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
        byte[] barr = DHAExtension.toBytes(labelId);
        for (int i = 0; i < barr.length; i++) {
            bytes[4 + i] = barr[i];
        }
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
        byte[] barr = DHAExtension.toBytes(heroId);
        for (int i = 0; i < barr.length; i++) {
            bytes[8 + i] = barr[i];
        }
    }

    public void setLabelIndex(int labelIndex) {
        this.labelIndex = labelIndex;
        byte[] barr = DHAExtension.toBytes(labelIndex);
        for (int i = 0; i < barr.length; i++) {
            bytes[36 + i] = barr[i];
        }
    }

    public byte[] getBytes() {
        return bytes;
    }
}

class ListIconElement {
    private byte[] bytes;
    public List<IconElement> iconElements;
    public Map<Integer, Integer> iconIndexDict;

    public ListIconElement(byte[] bytes) {
        this.bytes = bytes.clone();
        iconElements = new ArrayList<IconElement>();
        iconIndexDict = new HashMap<Integer, Integer>();
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S')
            start = DHAExtension.bytesToInt(bytes, 132);
        else
            start = 0;
        if (start == bytes.length)
            return;
        int count;
        while (start < bytes.length) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            IconElement ic = new IconElement(Arrays.copyOfRange(bytes, start, start + count));
            iconIndexDict.put(ic.iconId, iconElements.size());
            iconElements.add(ic);
            start += count;
        }
    }

    public void setIcon(int sourceId, byte[] iconBytes) throws Exception {
        if (!iconIndexDict.containsKey(sourceId)) {
            throw new Exception("not found id " + sourceId);
        }
        iconElements.set(iconIndexDict.get(sourceId), new IconElement(iconBytes));
        iconElements.get(iconIndexDict.get(sourceId)).setIconIndex(sourceId % 100);
        iconElements.get(iconIndexDict.get(sourceId)).setHeroId(sourceId / 100);
        iconElements.get(iconIndexDict.get(sourceId)).setIconId(sourceId);
        iconElements.get(iconIndexDict.get(sourceId)).setIconCode("30" + (sourceId / 100) + (sourceId % 100));
    }

    public void copyIcon(int sourceId, int targetId) throws Exception {
        copyIcon(sourceId, targetId, true);
    }

    public void copyIcon(int sourceId, int targetId, boolean swap) throws Exception {
        if (!iconIndexDict.containsKey(sourceId) || !iconIndexDict.containsKey(targetId)) {
            throw new Exception("not found id " + sourceId + " or " + targetId);
        }

        byte[] bytes = iconElements.get(iconIndexDict.get(targetId)).getBytes();
        iconElements.set(iconIndexDict.get(sourceId), new IconElement(bytes));
        iconElements.get(iconIndexDict.get(sourceId)).setIconIndex(sourceId % 100);
        iconElements.get(iconIndexDict.get(sourceId)).setHeroId(sourceId / 100);
        if (sourceId % 100 == 0) {
            if (swap) {
                iconElements.get(iconIndexDict.get(targetId)).setHeroId(sourceId / 100);
                iconElements.get(iconIndexDict.get(targetId)).setIconId(sourceId);
                iconElements.get(iconIndexDict.get(targetId)).setIconIndex(targetId % 100);
                iconElements.get(iconIndexDict.get(targetId)).setIconCode("30" + (sourceId / 100) + (sourceId % 100));
            } else {
                iconElements.get(iconIndexDict.get(sourceId)).setIconId(sourceId);
                iconElements.get(iconIndexDict.get(sourceId)).setIconCode("30" + (sourceId / 100) + (sourceId % 100));
            }
        } else {
            iconElements.get(iconIndexDict.get(sourceId)).setIconId(sourceId);
            iconElements.get(iconIndexDict.get(sourceId)).setIconCode("30" + (sourceId / 100) + (sourceId % 100));
        }
    }

    public IconElement get(int iconId) {
        try {
            return iconElements.get(iconIndexDict.get(iconId));
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] getBytes() {
        byte[] childBytes = new byte[0];
        for (IconElement e : iconElements) {
            childBytes = DHAExtension.mergeBytes(childBytes, e.getBytes());
        }
        int start;
        if (bytes[0] == 'M' && bytes[1] == 'S' && bytes[2] == 'E' && bytes[3] == 'S') {
            start = DHAExtension.bytesToInt(bytes, 132);
            bytes = DHAExtension.replaceBytes(bytes, 12, 16, DHAExtension.toBytes(iconElements.size()));
        } else
            start = 0;
        return DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, start), childBytes);
    }
}

class IconElement {
    private byte[] bytes;
    public int iconId;
    public int iconIndex;
    public int heroId;
    public String heronamecode;
    public String skinnamecode;
    public String iconCode;

    public IconElement(byte[] bytes) {
        this.bytes = bytes.clone();

        iconId = DHAExtension.bytesToInt(bytes, 4);
        iconIndex = DHAExtension.bytesToInt(bytes, 36);
        heroId = DHAExtension.bytesToInt(bytes, 8);
        heronamecode = new String(Arrays.copyOfRange(bytes, 16, 35));
        skinnamecode = new String(Arrays.copyOfRange(bytes, 44, 63));
        iconCode = new String(Arrays.copyOfRange(bytes, 68, 67 + DHAExtension.bytesToInt(bytes, 64)));
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
        byte[] barr = DHAExtension.toBytes(iconId);
        for (int i = 0; i < barr.length; i++) {
            bytes[4 + i] = barr[i];
        }
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
        byte[] barr = DHAExtension.toBytes(heroId);
        for (int i = 0; i < barr.length; i++) {
            bytes[8 + i] = barr[i];
        }
    }

    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
        int start = 68, end = 67 + DHAExtension.bytesToInt(bytes, 64);
        bytes = DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, start), iconCode.getBytes(),
                Arrays.copyOfRange(bytes, end, bytes.length));
        byte[] barr = DHAExtension.toBytes(bytes.length - 4);
        for (int i = 0; i < barr.length; i++) {
            bytes[i] = barr[i];
        }
        barr = DHAExtension.toBytes(iconCode.length() + 1);
        for (int i = 0; i < barr.length; i++) {
            bytes[64 + i] = barr[i];
        }
    }

    public void setIconIndex(int iconIndex) {
        this.iconIndex = iconIndex;
        byte[] barr = DHAExtension.toBytes(iconIndex);
        for (int i = 0; i < barr.length; i++) {
            bytes[36 + i] = barr[i];
        }
    }

    public byte[] getBytes() {
        return bytes;
    }
}

class Element {
    private byte[] bytes;
    private byte[] name;
    private byte[] type;
    private byte[] value;
    public String nameS = null;
    public String typeS = null;
    public String valueS = null;
    public JT jtType;
    public List<Element> childList;
    public Map<String, Element> childMap;

    public Element(JT type) throws Exception {
        bytes = DHAExtension.ReadAllBytes(".special/jt" + type + ".bytes");
        int space = 4;
        int start = 4;
        int count = DHAExtension.bytesToInt(bytes, start);
        if (count < space)
            throw new Exception("invalid count at " + start);
        setName(Arrays.copyOfRange(bytes, start + space, start + count));

        space = 8;
        start += count + 8;
        count = DHAExtension.bytesToInt(bytes, start);
        if (count < space)
            throw new Exception("invalid count at " + start);
        String jt = new String(Arrays.copyOfRange(bytes, start + space, start + count));

        space = 8;
        start += count;
        count = DHAExtension.bytesToInt(bytes, start);
        if (count < space)
            throw new Exception("invalid count at " + start);
        setType(Arrays.copyOfRange(bytes, start + space, start + count));

        int childCount = -1;
        switch (jt) {
            case "JTCom":
            case "JTArr":
            case "JTCus":
                if (jt.equals("JTCom")) {
                    jtType = JT.Com;
                } else if (jt.equals("JTArr")) {
                    jtType = JT.Arr;
                } else if (jt.equals("JTCus")) {
                    jtType = JT.Cus;
                }
                start += count + 4;
                if (DHAExtension.bytesToInt(bytes, start) != bytes.length - start) {
                    throw new Exception("invalid size");
                }

                start += 4;
                if (start == bytes.length) {
                    childCount = 0;
                } else {
                    childCount = DHAExtension.bytesToInt(bytes, start);
                }
                childList = new ArrayList<Element>();
                childMap = new HashMap<>();

                start += 4;
                for (int i = 0; i < childCount; i++) {
                    count = DHAExtension.bytesToInt(bytes, start);
                    if (start + count > bytes.length) {
                        throw new Exception("invalid size");
                    }
                    Element e = new Element(Arrays.copyOfRange(bytes, start, start + count));
                    childList.add(e);
                    if (jtType == JT.Arr) {
                        childMap.put(e.nameS + i, e);
                    } else {
                        childMap.put(e.nameS, e);
                    }
                    start += count;
                }
                setValue(jt);
                break;
            case "JTPri":
                jtType = JT.Pri;
                childList = null;

                space = 8;
                start += count;
                count = DHAExtension.bytesToInt(bytes, start);
                if (count < space)
                    throw new Exception("invalid count at " + start);
                setValue(Arrays.copyOfRange(bytes, start + space, start + count));
                break;
        }
    }

    public Element(byte[] bytes) throws Exception {
        if (bytes == null) {
            throw new Exception("null exception");
        }
        if (bytes.length != DHAExtension.bytesToInt(bytes, 0)) {
            throw new Exception("invalid size: " + bytes.length + " - " + DHAExtension.bytesToInt(bytes, 0));
        }
        this.bytes = bytes.clone();

        int space = 4;
        int start = 4;
        int count = DHAExtension.bytesToInt(bytes, start);
        if (count < space)
            throw new Exception("invalid count at " + start);
        setName(Arrays.copyOfRange(bytes, start + space, start + count));

        space = 8;
        start += count + 8;
        count = DHAExtension.bytesToInt(bytes, start);
        if (count < space)
            throw new Exception("invalid count at " + start);
        String jt = new String(Arrays.copyOfRange(bytes, start + space, start + count));

        if (jt.equals("NULLY")) {
            jtType = JT.Null;
            return;
        }

        space = 8;
        start += count;
        count = DHAExtension.bytesToInt(bytes, start);
        if (count < space)
            throw new Exception("invalid count at " + start);
        setType(Arrays.copyOfRange(bytes, start + space, start + count));

        int childCount = -1;
        switch (jt) {
            case "JTCom":
            case "JTArr":
            case "JTCus":
                if (jt.equals("JTCom")) {
                    jtType = JT.Com;
                } else if (jt.equals("JTArr")) {
                    jtType = JT.Arr;
                } else if (jt.equals("JTCus")) {
                    jtType = JT.Cus;
                }
                start += count + 4;
                if (DHAExtension.bytesToInt(bytes, start) != bytes.length - start) {
                    throw new Exception("invalid size");
                }

                start += 4;
                if (start == bytes.length) {
                    childCount = 0;
                } else {
                    childCount = DHAExtension.bytesToInt(bytes, start);
                }
                childList = new ArrayList<Element>();
                childMap = new HashMap<>();

                start += 4;
                for (int i = 0; i < childCount; i++) {
                    count = DHAExtension.bytesToInt(bytes, start);
                    if (start + count > bytes.length) {
                        throw new Exception("invalid size");
                    }
                    Element e = new Element(Arrays.copyOfRange(bytes, start, start + count));
                    childList.add(e);
                    if (jtType == JT.Arr) {
                        childMap.put(e.nameS + i, e);
                    } else {
                        childMap.put(e.nameS, e);
                    }
                    start += count;
                }
                setValue(jt);
                break;
            case "JTPri":
                jtType = JT.Pri;
                childList = null;

                space = 8;
                start += count;
                count = DHAExtension.bytesToInt(bytes, start);
                if (count < space)
                    throw new Exception("invalid count at " + start);
                setValue(Arrays.copyOfRange(bytes, start + space, start + count));
                break;
        }
    }

    public Element getChild(String name) {
        if (!containsChild(name))
            return null;
        return childMap.get(name);
    }

    public Element getChild(int index) {
        if (index < 0 || index >= childList.size())
            return null;
        return childList.get(index);
    }

    public Element clone() {
        try {
            return new Element(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean containsChild(String name) {
        return childMap.containsKey(name);
    }

    public void removeChildAt(int index) {
        if (childList != null && index < childList.size()) {
            childList.remove(index);
        }
    }

    public void addChild(Element e) {
        if (childList == null)
            return;
        if (childList.size() == 0) {
            bytes = DHAExtension.mergeBytes(bytes, new byte[] { 1, 0, 0, 0 });
        }
        childList.add(e);
        childMap.put(e.nameS, e);
    }

    public void addChild(int index, Element e) {
        if (childList == null || index < 0 || index > childList.size())
            return;
        if (childList.size() == 0) {
            bytes = DHAExtension.mergeBytes(bytes, new byte[] { 1, 0, 0, 0 });
        }
        childList.add(index, e);
        childMap.put(e.nameS, e);
    }

    public void setChild(int index, Element e) {
        if (childList == null || index < 0 || index >= childList.size())
            return;
        childMap.remove(childList.get(index).nameS);
        childList.set(index, e);
        childMap.put(e.nameS, e);
    }

    public void setName(String name) {
        setName(name.getBytes());
    }

    public void setName(byte[] name) {
        if (this.name == null) {
            this.name = name;
            this.nameS = new String(name);
        } else {
            int deltalength = name.length - this.name.length;
            bytes = DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, 8), name,
                    Arrays.copyOfRange(bytes, 8 + this.name.length, bytes.length));
            byte[] barr = DHAExtension.toBytes(DHAExtension.bytesToInt(bytes, 4) + deltalength);
            for (int i = 0; i < barr.length; i++) {
                bytes[4 + i] = barr[i];
            }
            barr = DHAExtension.toBytes(DHAExtension.bytesToInt(bytes, 0) + deltalength);
            for (int i = 0; i < barr.length; i++) {
                bytes[i] = barr[i];
            }
            this.name = name;
            nameS = new String(name);
        }
    }

    public void setType(String type) {
        setType(type.getBytes());
    }

    public void setType(byte[] type) {
        if (this.type == null) {
            this.type = type;
            this.typeS = new String(type);
        } else {
            int deltalength = type.length - this.type.length;
            bytes = DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, 8 + name.length + 29), type,
                    Arrays.copyOfRange(bytes, 8 + name.length + 29 + this.type.length, bytes.length));
            int[] indexChanges = new int[] { 0, 8 + name.length, 8 + name.length + 21 };
            for (int changeAt : indexChanges) {
                byte[] barr = DHAExtension.toBytes(DHAExtension.bytesToInt(bytes, changeAt) + deltalength);
                for (int i = 0; i < barr.length; i++) {
                    bytes[changeAt + i] = barr[i];
                }
            }
            this.type = type;
            typeS = new String(type);
        }
    }

    public void setValue(String value) {
        setValue(value.getBytes());
    }

    public void setValue(byte[] value) {
        if (value[0] != (byte) 'V') {
            value = DHAExtension.mergeBytes(new byte[] { (byte) 'V' }, value);
        }
        if (this.value == null) {
            this.value = value;
            this.valueS = new String(value);
        } else {
            if (jtType != JT.Pri) {
                return;
            }
            int deltalength = value.length - this.value.length;
            int valueStart = 8 + name.length + 29 + type.length;
            bytes = DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, valueStart + 8), value,
                    Arrays.copyOfRange(bytes, valueStart + 8 + this.value.length, bytes.length));
            int[] indexChanges = new int[] { 0, 8 + name.length, valueStart };
            for (int changeAt : indexChanges) {
                byte[] barr = DHAExtension.toBytes(DHAExtension.bytesToInt(bytes, changeAt) + deltalength);
                for (int i = 0; i < barr.length; i++) {
                    bytes[changeAt + i] = barr[i];
                }
            }
            this.value = value;
            valueS = new String(value);
        }
    }

    public byte[] getBytes() {
        if (jtType != JT.Pri && childList != null && childList.size() != 0) {
            bytes = Arrays.copyOfRange(bytes, 0, 8 + name.length + 29 + type.length + 12);
            byte[] barr = DHAExtension.toBytes(childList.size());
            for (int i = 0; i < barr.length; i++) {
                bytes[8 + name.length + 29 + type.length + 8 + i] = barr[i];
            }
            for (Element child : childList) {
                bytes = DHAExtension.mergeBytes(bytes, child.getBytes());
            }
            int[] indexChanges = new int[] { 0, 8 + name.length + 29 + type.length + 4 };
            for (int changeAt : indexChanges) {
                barr = DHAExtension.toBytes(bytes.length - changeAt);
                for (int i = 0; i < barr.length; i++) {
                    bytes[changeAt + i] = barr[i];
                }
            }
        }
        return bytes;
    }

    public Element replaceValue(String type, StringOperator operator) {
        return replaceValue(this, type, operator);
    }

    private Element replaceValue(Element element, String type, StringOperator operator) {
        if (element.jtType == JT.Pri) {
            if (element.typeS.equals(type)) {
                element.setValue(operator.handle(element.valueS).getBytes());
            }
            return element;
        }

        for (int i = 0; i < element.childList.size(); i++) {
            element.childList.set(i, replaceValue(element.childList.get(i), type, operator));
        }

        return element;
    }

    public Element replaceValue(String type, String target, String replace) {
        return replaceValue(this, type, target, replace);
    }

    private Element replaceValue(Element element, String type, String target, String replace) {
        if (element.jtType == JT.Pri) {
            if (element.typeS.equals(type)) {
                element.setValue(element.valueS.replaceAll(target, replace).getBytes());
            }
            return element;
        }

        for (int i = 0; i < element.childList.size(); i++) {
            element.childList.set(i, replaceValue(element.childList.get(i), type, target, replace));
        }

        return element;
    }

    public int getChildLength() {
        return childList.size();
    }
}

enum JT {
    Com, Arr, Cus, Pri, Null
}