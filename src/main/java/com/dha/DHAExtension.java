package com.dha;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

public class DHAExtension {
    public static String convertToTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String WORD_SEPARATOR = " ";

        return Arrays
                .stream(text.split(WORD_SEPARATOR))
                .map(word -> word.isEmpty()
                        ? word
                        : Character.toTitleCase(word.charAt(0)) + word
                                .substring(1)
                                .toLowerCase())
                .collect(Collectors.joining(WORD_SEPARATOR));
    }

    public static boolean listContainsElementFromOther(Object[] arr1, Object[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr2.length; j++) {
                if (arr1[i].equals(arr2[j]))
                    return true;
            }
        }
        return false;
    }

    public static int pow(int n1, int n2) {
        int l = 1;
        for (int i = 0; i < n2; i++) {
            l = l * n1;
        }
        return l;
    }

    public static byte[] replaceBytesLast(byte[] bytes, byte[] targetBytes, byte[] replaceBytes) {
        int start = 0;
        if ((start = indexOfBefore(bytes, targetBytes, bytes.length - 1)) > 0) {
            bytes = replaceBytes(bytes, start, start + targetBytes.length, replaceBytes);
        }
        return bytes;
    }

    public static byte[] replaceBytesFirst(byte[] bytes, byte[] targetBytes, byte[] replaceBytes) {
        int start = 0;
        if ((start = indexOf(bytes, targetBytes)) > 0) {
            bytes = replaceBytes(bytes, start, start + targetBytes.length, replaceBytes);
        }
        return bytes;
    }

    public static byte[] replaceBytes(byte[] bytes, byte[] targetBytes, byte[] replaceBytes) {
        if (targetBytes == replaceBytes)
            return bytes;
        int start = 0;
        while ((start = indexOf(bytes, targetBytes, start)) > 0) {
            bytes = replaceBytes(bytes, start, start + targetBytes.length, replaceBytes);
        }
        return bytes;
    }

    public static byte[] replaceBytes(byte[] bytes, int start, int end, byte[] replaceBytes) {
        return mergeBytes(Arrays.copyOfRange(bytes, 0, start), replaceBytes,
                Arrays.copyOfRange(bytes, end, bytes.length));
    }

    public static int bytesToInt(byte[] bytes, int index) {
        return bytesToInt(bytes[index], bytes[index + 1], bytes[index + 2], bytes[index + 3]);
    }

    public static int bytesToInt(byte... bytes) {
        int val = (((bytes[3] & 0xff) << 24) | ((bytes[2] & 0xff) << 16) | ((bytes[1] & 0xff) << 8)
                | (bytes[0] & 0xff));
        return val;
    }

    public static byte[] toBytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i & 0xFF);
        bytes[1] = (byte) ((i >> 8) & 0xFF);
        bytes[2] = (byte) ((i >> 16) & 0xFF);
        bytes[3] = (byte) ((i >> 24) & 0xFF);
        return bytes;
    }

    public static int countMatches(String str, String substr) {
        return (str.length() - str.replace(substr, "").length()) / substr.length();
    }

    public static int countMatches(byte[] outerBytes, byte[] smallerBytes) {
        int count = 0;
        int start = -1;
        while ((start = indexOf(outerBytes, smallerBytes, start + 1)) > 0) {
            count++;
        }
        return count;
    }

    public static byte[] mergeBytes(byte[]... bytess) {
        byte[] dest = new byte[0];
        for (byte[] bytes : bytess) {
            dest = merge2ByteArrays(dest, bytes);
        }
        return dest;
    }

    private static byte[] merge2ByteArrays(byte[] bytes1, byte[] bytes2) {
        if (bytes1.length == 0)
            return bytes2;
        if (bytes2.length == 0)
            return bytes1;
        byte[] bytes = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
        System.arraycopy(bytes2, 0, bytes, bytes1.length, bytes2.length);
        return bytes;
    }

    public static void deleteDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return;
        }
        if (dir.isFile()) {
            dir.delete();
            return;
        }
        dirPath = dirPath.endsWith("/") ? dirPath : dirPath + "/";
        for (String childName : dir.list()) {
            deleteDir(dirPath + childName);
        }
        dir.delete();
    }

    public static int indexOf(byte[] outerArray, byte[] smallerArray) {
        return indexOf(outerArray, smallerArray, 0);
    }

    public static int indexOf(byte[] outerArray, byte[] smallerArray, int start) {
        for (int i = start; i < outerArray.length - smallerArray.length + 1; ++i) {
            boolean found = true;
            for (int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i + j] != smallerArray[j]
                        && !(outerArray[i + j] >= 'A' && outerArray[i + j] <= 'Z'
                                && outerArray[i + j] + 32 == smallerArray[j])
                        && !(outerArray[i + j] >= 'a' && outerArray[i + j] <= 'z'
                                && outerArray[i + j] - 32 == smallerArray[j])) {
                    found = false;
                    break;
                }
            }
            if (found)
                return i;
        }
        return -1;
    }

    public static int indexOfBefore(byte[] outerArray, byte[] smallerArray, int end) {
        for (int i = end; i >= 0; --i) {
            boolean found = true;
            for (int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i + j] != smallerArray[j]
                        && !(outerArray[i + j] >= 'A' && outerArray[i + j] <= 'Z'
                                && outerArray[i + j] + 32 == smallerArray[j])
                        && !(outerArray[i + j] >= 'a' && outerArray[i + j] <= 'z'
                                && outerArray[i + j] - 32 == smallerArray[j])) {
                    found = false;
                    break;
                }
            }
            if (found)
                return i;
        }
        return -1;
    }

    public static void copy(String source, String dest) {
        File sourceFile = new File(source);
        File destFile = new File(dest);
        if (!sourceFile.exists())
            return;
        if (sourceFile.isFile()) {
            if (!destFile.isDirectory()) {
                copyFile(source, dest);
            }
            return;
        }
        if (destFile.isFile()) {
            return;
        }

        if (!destFile.exists())
            destFile.mkdirs();
        for (String child : sourceFile.list()) {
            copy(new File(source, child).getAbsolutePath(), new File(dest, child).getAbsolutePath());
        }
    }

    private static void copyFile(String source, String dest) {
        DHAExtension.WriteAllBytes(dest, DHAExtension.ReadAllBytes(source));
    }

    public static void WriteAllBytes(String filePath, byte[] bytes) {
        if (!new File(filePath).getParentFile().exists())
            new File(filePath).getParentFile().mkdirs();
        File outputFile = new File(filePath);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void WriteAllText(String fileName, String content) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOut);
            outputStreamWriter.write(content);
            outputStreamWriter.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void WriteAllLines(String fileName, String[] lines) {
        try {
            if (!new File(fileName).getParentFile().exists()) {
                new File(fileName).getParentFile().mkdirs();
            }
            StringBuilder content = new StringBuilder();
            for (String line : lines) {
                content.append(line).append("\n");
            }
            if (lines.length > 0)
                content.deleteCharAt(content.length() - 1);
            FileOutputStream fileOut = new FileOutputStream(fileName);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOut);
            outputStreamWriter.write(content.toString());
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] ReadAllBytes(String filePath) {
        File file = new File(filePath);
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bFile;
    }

    public static byte[] ReadAllBytes(ZipInputStream zipIn) {
        try {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            while ((len = zipIn.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String ReadAllText(String filename) {
        String ret = "";
        try {
            InputStream inputStream = new FileInputStream(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = reader.readLine()) != null) {
                stringBuilder.append(receiveString).append("\n");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            inputStream.close();
            ret = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    public static String[] ReadAllLines(String filename) {
        List<String> lines = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String receiveString = "";

            while ((receiveString = reader.readLine()) != null) {
                lines.add(receiveString);
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return lines.toArray(new String[0]);
    }
}
