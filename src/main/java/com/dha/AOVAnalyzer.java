package com.dha;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictCompress;

public class AOVAnalyzer {
    public static Element replaceStringValue(Element element, String type, String target, String replace) {
        if (element.jtType == JT.Pri) {
            if (element.typeS.equals(type)) {
                element.setValue(element.valueS.replaceAll(target, replace).getBytes());
            }
            return element;
        }

        for (int i = 0; i < element.childList.size(); i++) {
            element.childList.set(i, replaceStringValue(element.childList.get(i), type, target, replace));
        }

        return element;
    }

    public static byte[] AOVDecompress(byte[] compressed) {
        String zstdDictPath = "D:/zstd_dict.bin";
        byte[] zstdDict = DHAExtension.ReadAllBytes(zstdDictPath);

        int start = DHAExtension.indexOf(compressed, new byte[] { 40, (byte) 181, 47, (byte) 253 });
        if (start == -1)
            return null;
        compressed = Arrays.copyOfRange(compressed, start, compressed.length);
        return Zstd.decompress(compressed, zstdDict, (int) Zstd.decompressedSize(compressed));
    }

    public static byte[] AOVCompress(byte[] uncompress) {
        String zstdDictPath = "D:/zstd_dict.bin";
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

class AnalyzerType{
    public static String string = "TypeSystem.String";
    public static String int32 = "TypeSystem.Int32";
    public static String bool = "TypeSystem.Boolean";
    public static String stringArr = "TypeSystem.String[]";
}

class ListLabelElement {
    private byte[] bytes;
    public List<LabelElement> labelElements;

    public ListLabelElement(byte[] bytes) {
        this.bytes = bytes;
        labelElements = new ArrayList<>();
        int start = DHAExtension.bytesToInt(bytes, 132);
        int count;
        while (start < bytes.length) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            labelElements.add(new LabelElement(Arrays.copyOfRange(bytes, start, start + count)));
            start += count;
        }
    }

    public byte[] getBytes() {
        byte[] childBytes = new byte[0];
        for (LabelElement e : labelElements) {
            childBytes = DHAExtension.mergeBytes(childBytes, e.getBytes());
        }
        return DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, DHAExtension.bytesToInt(bytes, 132)), childBytes);
    }
}

class LabelElement {
    private byte[] bytes;
    public int labelId;
    public int labelIndex;
    public int heroId;

    public LabelElement(byte[] bytes) {
        this.bytes = bytes;

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
        this.bytes = bytes;
        iconElements = new ArrayList<IconElement>();
        iconIndexDict = new HashMap<Integer, Integer>();
        int start = DHAExtension.bytesToInt(bytes, 132);
        int count;
        while (start < bytes.length) {
            count = DHAExtension.bytesToInt(bytes, start) + 4;
            IconElement ic = new IconElement(Arrays.copyOfRange(bytes, start, start + count));
            iconIndexDict.put(ic.iconId, iconElements.size());
            iconElements.add(ic);
            start += count;
        }
    }

    public void copyIcon(int sourceId, int targetId) throws Exception {
        if (!iconIndexDict.containsKey(sourceId) || !iconIndexDict.containsKey(targetId)) {
            throw new Exception("not found id " + sourceId + " or " + targetId);
        }

        byte[] bytes = iconElements.get(iconIndexDict.get(targetId)).getBytes();
        iconElements.set(iconIndexDict.get(sourceId), new IconElement(bytes));
        iconElements.get(iconIndexDict.get(sourceId)).setIconIndex(sourceId % 100);
        iconElements.get(iconIndexDict.get(sourceId)).setHeroId(sourceId / 100);
        if (sourceId % 100 == 0) {
            iconElements.get(iconIndexDict.get(targetId)).setHeroId(sourceId / 100);
            iconElements.get(iconIndexDict.get(targetId)).setIconId(sourceId);
            iconElements.get(iconIndexDict.get(targetId)).setIconIndex(targetId % 100);
            iconElements.get(iconIndexDict.get(targetId)).setIconCode("30" + (sourceId / 100) + (sourceId % 100));
        } else {
            iconElements.get(iconIndexDict.get(sourceId)).setIconId(sourceId);
        }
        DHAExtension.WriteAllBytes("D:/test.bytes", iconElements.get(iconIndexDict.get(sourceId)).getBytes());
    }

    public byte[] getBytes() {
        byte[] childBytes = new byte[0];
        for (IconElement e : iconElements) {
            childBytes = DHAExtension.mergeBytes(childBytes, e.getBytes());
        }
        return DHAExtension.mergeBytes(Arrays.copyOfRange(bytes, 0, DHAExtension.bytesToInt(bytes, 132)), childBytes);
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
        this.bytes = Arrays.copyOfRange(bytes,0,bytes.length);

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

    public Element(JT type) throws Exception {
        bytes = DHAExtension.ReadAllBytes("resources/jt" + type + ".bytes");
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

                start += 4;
                for (int i = 0; i < childCount; i++) {
                    count = DHAExtension.bytesToInt(bytes, start);
                    if (start + count > bytes.length) {
                        throw new Exception("invalid size");
                    }
                    childList.add(new Element(Arrays.copyOfRange(bytes, start, start + count)));
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
            throw new NullPointerException();
        }
        if (bytes.length != DHAExtension.bytesToInt(bytes, 0)) {
            throw new Exception("invalid size: " + bytes.length + " - " + DHAExtension.bytesToInt(bytes, 0));
        }
        this.bytes = bytes;

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

                start += 4;
                for (int i = 0; i < childCount; i++) {
                    count = DHAExtension.bytesToInt(bytes, start);
                    if (start + count > bytes.length) {
                        throw new Exception("invalid size");
                    }
                    childList.add(new Element(Arrays.copyOfRange(bytes, start, start + count)));
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
}

enum JT {
    Com, Arr, Cus, Pri
}