package com.dha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipExtension {
    public static void unzip(String zipPath, String destDirPath) {
        try {
            File destDir = new File(destDirPath);
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static void zipDir(String[] dirPaths, String zipoutPath) {
        if (!new File(zipoutPath).getParentFile().exists())
            new File(zipoutPath).getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(zipoutPath);
            zipDir(dirPaths, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipDir(String dirPath, String zipoutPath) {
        if (!new File(zipoutPath).getParentFile().exists())
            new File(zipoutPath).getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(zipoutPath);
            zipDir(dirPath, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipDir(String dirPath, String zipoutPath, int compressLevel) {
        if (!new File(zipoutPath).getParentFile().exists())
            new File(zipoutPath).getParentFile().mkdirs();
        try {
            String path = "F:/This PC/Documents/AOV/modsave/Mod Skin Elsu Sứ Giả Tận Thế 11.9 (Izumi Tv).zip";
            System.out.println(zipoutPath + " - " + path +": " + zipoutPath.equals(path));
            assert zipoutPath.equals(path);
            new File(zipoutPath).createNewFile();
            FileOutputStream fos = new FileOutputStream(zipoutPath);
            zipDir(dirPath, fos, compressLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipDir(String[] dirPaths, OutputStream fos) {
        try {
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            for (String dirPath : dirPaths) {
                File fileToZip = new File(dirPath);
                zipFile(fileToZip, fileToZip.getName(), zipOut);
            }
            zipOut.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipDir(String dirPath, OutputStream fos) {
        zipDir(dirPath, fos, Deflater.NO_COMPRESSION);
    }

    public static void zipDir(String dirPath, OutputStream fos, int compressLevel){
        try {
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            zipOut.setLevel(compressLevel);
            File fileToZip = new File(dirPath);
            zipFile(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipFile(List<String> srcFiles, String zipoutPath) {
        zipFile(srcFiles, zipoutPath, 0);
    }

    public static void zipFile(List<String> srcFiles, String zipoutPath, int compressLevel) {
        try {
            FileOutputStream fos = new FileOutputStream(zipoutPath);
            zipFile(srcFiles, fos, compressLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipFile(List<String> srcFiles, OutputStream fos, int compressLevel) {
        try {
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            zipOut.setMethod(ZipOutputStream.DEFLATED);
            zipOut.setLevel(compressLevel);
            for (String srcFile : srcFiles) {
                File fileToZip = new File(srcFile);
                zipFile(fileToZip, fileToZip.getName(), zipOut);
            }
            zipOut.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            ZipEntry zipEntry;
            if (fileName.endsWith("/")) {
                zipEntry = new ZipEntry(fileName);
            } else {
                zipEntry = new ZipEntry(fileName + "/");
            }
            // zipEntry.setSize(0);
            // zipEntry.setCompressedSize(0);
            // zipEntry.setCrc(0);
            zipOut.putNextEntry(zipEntry);
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        // zipEntry.setSize(fileToZip.length());
        // zipEntry.setCompressedSize(fileToZip.length());
        // zipEntry.setCrc(computeCrc(fileToZip));
        // System.out.println("zip file " + fileToZip.getAbsolutePath() + " to " +
        // zipEntry.getName() + " size: " + fileToZip.length() + "(" + fis.available() +
        // ")");
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
