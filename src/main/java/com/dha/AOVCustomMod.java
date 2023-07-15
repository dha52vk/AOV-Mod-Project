package com.dha;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.w3c.dom.Document;

import com.github.luben.zstd.Zstd;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AOVCustomMod {

    public static void main(String[] args) throws Exception {
        // findAllActionsResource("F:/This
        // PC/Documents/AOV/CustomPack/custompack_150/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/150_HanXin/skill");
        // taopack();
        
        // saveHeroListTo("D:/herolisttested.txt");

        // ListDeviceSupport listDevices = new ListDeviceSupport();
        // listDevices.addNewDevice("Xiaomi 2201117TG");
        // DHAExtension.WriteAllBytes("D:/VeryHighFrameModeBlackList.bytes", listDevices.getBytes());
    }
    
    public static void saveHeroListTo(String path){
        List<Hero> heroList = new Gson().fromJson(DHAExtension.ReadAllText("D:/skinlist(label).json"), HeroList.class).heros;
        String content="";
        for(Hero hero : heroList){
            content+="\n"+hero.name;
        }
        DHAExtension.WriteAllText(path, content);

    }

    public static void findAllActionsResource(String path) {
        List<String> listEffectName = new ArrayList<>();
        List<String> listClipName = new ArrayList<>();
        List<String> listSoundName = new ArrayList<>();
        for (String child : new File(path).list()) {
            String childPath = path + child;
            String lines[] = DHAExtension.ReadAllLines(childPath);
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].contains("resourceName")) {
                    String[] split = lines[i].split("\"");
                    for (int j = 0; j < split.length; j++) {
                        if (split[j].endsWith("value=")) {
                            listEffectName.add(child + ": " + split[j + 1]);
                            break;
                        }
                    }
                } else if (lines[i].contains("clipName")) {
                    String[] split = lines[i].split("\"");
                    for (int j = 0; j < split.length; j++) {
                        if (split[j].endsWith("value=")) {
                            listClipName.add(child + ": " + split[j + 1]);
                            break;
                        }
                    }
                } else if (lines[i].contains("<Track trackName=\"PlayHeroSoundTick")) {
                    while (!lines[i].contains("\"eventName\""))
                        i++;
                    String[] split = lines[i].split("\"");
                    for (int j = 0; j < split.length; j++) {
                        if (split[j].endsWith("value=")) {
                            listSoundName.add(child + ": " + split[j + 1]);
                            break;
                        }
                    }
                }
            }
        }
        DHAExtension.WriteAllLines("D:/listeffect.txt", listEffectName.toArray(new String[0]));
        DHAExtension.WriteAllLines("D:/listsound.txt", listSoundName.toArray(new String[0]));
        DHAExtension.WriteAllLines("D:/listclip.txt", listClipName.toArray(new String[0]));
    }

    public static void ModMulti(String[] originIds, String[] targetIds, boolean[] haveSounds, boolean[] changeAnims) {
        DHAExtension.deleteDir("F:\\This PC\\Documents\\AOV\\multipack_" + originIds[0].substring(0, 3));
        App.modPackName = "multipack_" + originIds[0].substring(0, 3);
        for (int i = 0; i < originIds.length; i++) {
            AOVExtension.ModInfoAdvanced(originIds[i], targetIds[i]);
            AOVExtension.ModSound(originIds[i], targetIds[i]);
            AOVExtension.ModIcon_Name(originIds[i], targetIds[i]);
            AOVExtension.ModLabel(originIds[i], targetIds[i]);
        }
        AOVExtension.ModActionMulti(originIds, targetIds, haveSounds, changeAnims);
        AOVExtension.ModBackMulti(originIds, targetIds);
        AOVExtension.ModHasteMulti(originIds, targetIds, null, null);
        AOVExtension.ModAssetRef(targetIds[0]);
        AOVExtension.MakeCredit(new String[] { "1901" },
                new String[] { "<color=#0096FF>[Thử nghiệm]Mod Skin</color> bởi <color=#C41E3A>AH MOD AOV</color>"
                        + "\\n<color=#E4D00A>Mặc định</color> <color=#CF9FFF>=></color> <color=#00FF00>Long thần soái</color>"
                        + "\\n<color=#E4D00A>Cung thủ bóng đêm</color> <color=#CF9FFF>=></color> <color=#00FF00>Vệ Binh Ngân Hà</color>"
                        + "\\n<color=#E4D00A>Thần thoại hy lạp</color> <color=#CF9FFF>=></color> <color=#00FF00>Thế tử nguyệt tộc</color>"
                        + "\\n<color=#E4D00A>Lục nguyệt cung</color> <color=#CF9FFF>=></color> <color=#00FF00>Long thần soái</color>"
                        + "\\n<color=#E4D00A>Tiệc bãi biển</color> <color=#CF9FFF>=></color> <color=#00FF00>Vệ Binh Ngân Hà</color>" });
    }

    public static void taopack() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Nhap duong dan thu muc/tep: ");
            String path = scanner.nextLine();
            if (!path.contains("/") && !path.contains("/")) {
                if (path.length() == 3) {
                    String id = path;
                    path = "F:/This PC/Documents/AOV/CustomPack/custompack_" + id;
                    if (new File(path).exists()) {
                        DHAExtension.deleteDir(path);
                    }
                    new File(path).mkdirs();
                    String resourcesPath = "F:/This PC/Documents/AOV/Resources/1.50.1";
                    String[] filePaths = new String[] {
                            "/Prefab_Characters/Actor_" + id + "_Infos.pkg.bytes",
                            "/Ages/Prefab_Characters/Prefab_Hero/Actor_" + id + "_Actions.pkg.bytes",
                            "/Databin/Client/Actor/heroSkin.bytes",
                            "/Databin/Client/Shop/HeroSkinShop.bytes",
                            "/Databin/Client/Sound/BattleBank.bytes",
                            "/Databin/Client/Sound/ChatSound.bytes",
                            "/Databin/Client/Sound/HeroSound.bytes",
                            "/Databin/Client/Sound/LobbyBank.bytes",
                            "/Databin/Client/Sound/LobbySound.bytes"
                    };
                    new File(path + "/files/Resources/1.50.1/Prefab_Characters").mkdirs();
                    new File(path + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero").mkdirs();
                    for (String filePath : filePaths) {
                        if (filePath.contains("Actor_")) {
                            ZipExtension.unzip(resourcesPath + filePath,
                                    new File(path + "/files/Resources/1.50.1" + filePath).getParent());
                        } else {
                            DHAExtension.WriteAllBytes(path + "/files/Resources/1.50.1" + filePath,
                                    DHAExtension.ReadAllBytes(resourcesPath + filePath));
                        }
                    }
                }
            }
            MaHoa(path);
        }
    }

    public static void MaHoa(String path) {
        File file = new File(path);
        if (file.isFile()) {
            byte[] bytes = DHAExtension.ReadAllBytes(path);
            if (bytes != new byte[0]) {
                if (DHAExtension.indexOf(bytes, new byte[] { 40, (byte) 181, 47, (byte) 253 }) > 0) {
                    DHAExtension.WriteAllBytes(path, AOVExtension.AOVDecompress(bytes));
                    // System.out.println("Giai ma " + path);
                } else {
                    DHAExtension.WriteAllBytes(path, AOVExtension.AOVCompress(bytes));
                    System.out.println("Ma hoa " + path);
                }
            }
            return;
        }
        if (file.list() == null)
            return;
        for (String filename : file.list()) {
            String childPath = path + "/" + filename;
            MaHoa(childPath);
        }
    }
}
