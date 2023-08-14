package com.dha;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

import java.awt.Desktop;

public class AOVCustomMod {
    static ModSettings turnOnAll = new ModSettings(true, true, true, true, true, true, true, true);

    public static void main(String[] args) throws Exception {
        // taopack();
        // showSkinListHasLv(SkinLabel.SSS_HH.skinLevel);
        
        Element element = new Element(DHAExtension.ReadAllBytes("F:\\This PC\\Documents\\AOV\\Resources\\116_JingKe_actorinfo.bytes.decompressed"));
        System.out.println("Hero: ");
        Map<String, String> defaultChild = new HashMap<String, String>(); 
        for (Element child : element.childList){
            defaultChild.put(child.nameS, child.valueS);
            System.out.println(child.nameS);
        }
        System.out.println("\n\nSkin: ");
        for (Element child : element.getChild("SkinPrefab").getChild(16).childList){
            if (defaultChild.containsKey(child.nameS))
                System.out.println(child.nameS + ": " + defaultChild.get(child.nameS) + " - " + child.valueS);
        }
    }

    public static void MultiMod(String baseSkin, String newSkin) throws Exception{
        baseSkin = baseSkin.trim();
        newSkin = newSkin.trim();
        AOVModHelper helper = new AOVModHelper();
        helper.setEcho(true);
        Map<String, Skin> skinMap = new HashMap<>();
        List<Hero> heroList = new Gson().fromJson(DHAExtension.ReadAllText(AOVModHelper.heroListJsonPath),
                HeroList.class).heros;
        for (Hero hero : heroList) {
            for (Skin skin : hero.skins) {
                skinMap.put(skin.id, skin);
            }
        }
        helper.setModPackName("multipack" + baseSkin.substring(0, 3));
        DHAExtension.deleteDir(AOVModHelper.saveModPath + helper.modPackName);
        String[] baseSkins = baseSkin.split(" ");
        String[] newSkins = newSkin.split(" ");
        List<ModInfo> modList = new ArrayList<>();
        for (int i = 0; i < baseSkins.length; i++){
            modList.add(new ModInfo(new ArrayList<>(Arrays.asList(new Skin[] {
                        new Skin(baseSkins[i], SkinLabel.Default)
                })), skinMap.get(newSkins[i]), turnOnAll));
        }
        helper.modIcon(modList);
        helper.modLabel(modList);
        helper.modSound(modList);
        helper.modInfos(modList);
        helper.modActionsMulti(modList);
        helper.modBackMulti(modList);
        helper.modHasteMulti(modList);
    }

    public static void CustomMod(String baseSkin, String newSkin) throws Exception{
        System.out.println("Finding all resource nane...");
        DHAExtension.WriteAllText("D:/allresources.txt", getAllResourcesForSkin(newSkin.split(" "), false));
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File("D:/allresources.txt"));

        AOVModHelper helper = new AOVModHelper();
        helper.setEcho(true);
        Map<String, Skin> skinMap = new HashMap<>();
        List<Hero> heroList = new Gson().fromJson(DHAExtension.ReadAllText(AOVModHelper.heroListJsonPath),
                HeroList.class).heros;
        for (Hero hero : heroList) {
            for (Skin skin : hero.skins) {
                skinMap.put(skin.id, skin);
            }
        }
        helper.setModPackName("custompack"+baseSkin.split(" ")[0] + " - " + newSkin.split(" ")[0]);
        DHAExtension.deleteDir(AOVModHelper.saveModPath + helper.modPackName);
        List<ModInfo> modList = new ArrayList<>();
        List<Skin> baseSkinList = new ArrayList<>();
        for (String id : baseSkin.split(" ")){
            if (id.equals(id.substring(0, 3) + "1")){
                baseSkinList.add(new Skin(id, SkinLabel.Default));
            }else{
                baseSkinList.add(skinMap.get(id));
            }
        }
        modList.add(new ModInfo(baseSkinList, skinMap.get(newSkin.split(" ")[0]), turnOnAll));
        helper.modInfosCustom(modList);
        helper.modIcon(modList);
        helper.modLabel(modList);
        helper.modSound(modList);
        helper.modBack(modList);
        helper.modHaste(modList);
        helper.modActionsCustom(modList);
        DHAExtension.deleteDir("D:/allresources.txt");
    }

    public static String getAllResourcesForSkin(String[] idList){
        return getAllResourcesForSkin(idList, true);
    }

    public static String getAllResourcesForSkin(String[] idList, boolean showFileName){
        StringBuilder content = new StringBuilder();
        AOVModHelper helper = new AOVModHelper();
        helper.setEcho(false);
        Map<String, Skin> skinMap = new HashMap<>();
        List<Hero> heroList = new Gson().fromJson(DHAExtension.ReadAllText(AOVModHelper.heroListJsonPath),
                HeroList.class).heros;
        for (Hero hero : heroList) {
            for (Skin skin : hero.skins) {
                skinMap.put(skin.id, skin);
            }
        }
        helper.setModPackName("cachepack");
        DHAExtension.deleteDir(AOVModHelper.saveModPath + helper.modPackName);
        for (String id : idList){
            try {
                helper.modActions(Arrays.asList(new ModInfo[]{
                    new ModInfo(new ArrayList<>(Arrays.asList(new Skin[] {
                                new Skin(id.substring(0, 3)+"1", SkinLabel.Default)
                                })), skinMap.get(id), turnOnAll)
                }));
                String path = AOVModHelper.cacheModPath;
                path = path + "/" + new File(path).list()[0];
                path = path + "/" + new File(path).list()[0];
                content.append("\n\n" + id + ": " + findAllActionsResource(path, showFileName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DHAExtension.deleteDir(AOVModHelper.saveModPath + helper.modPackName);
        return content.toString();
    }

    public static void prettierXML(String path) {
        if (!new File(path).exists())
            return;
        if (new File(path).isFile()) {
            try {
                DHAExtension.WriteAllText(path, ProjectXML.prettierXml(DHAExtension.ReadAllText(path)));
            } catch (Exception e) {

            }
            return;
        }
        for (String child : new File(path).list()) {
            String childPath = path + "/" + child;
            prettierXML(childPath);
        }
    }

    public static void showSkinListHasLv(int skinLevel) {
        List<Hero> heroList = new Gson().fromJson(DHAExtension.ReadAllText(AOVModHelper.heroListJsonPath),
                HeroList.class).heros;
        for (Hero hero : heroList) {
            for (Skin skin : hero.skins){
                if (skin.getSkinLevel() == skinLevel){
                    System.out.println(hero.name + " - " + skin+"");
                }
            }
        }
    }

    public static void saveHeroListTo(String path) {
        List<Hero> heroList = new Gson().fromJson(DHAExtension.ReadAllText(AOVModHelper.heroListJsonPath),
                HeroList.class).heros;
        String content = "";
        for (Hero hero : heroList) {
            content += "\n" + hero.name;
        }
        DHAExtension.WriteAllText(path, content);

    }

    public static String findAllActionsResource(String path) {
        return findAllActionsResource(path, true);
    }

    public static String findAllActionsResource(String path, boolean showFileName) {
        StringBuilder content = new StringBuilder("");
        for (String child : new File(path).list()) {
            String childPath = (path.endsWith("/") ? path : path+"/") + child;
            String content2 = "";
            byte[] bytes = DHAExtension.ReadAllBytes(childPath);
            byte[] decompress = AOVAnalyzer.AOVDecompress(bytes);
            if (decompress == null){
                content2 = new String(bytes);
            }else{
                content2 = new String(decompress);
            }
            String lines[] = content2.split("\\r?\\n|\\r");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].contains("resourceName")) {
                    String[] split = lines[i].split("\"");
                    for (int j = 0; j < split.length; j++) {
                        if (split[j].endsWith("value=")) {
                            content.append("\n" + (showFileName ? child + ": " : "") + split[j + 1]);
                            break;
                        }
                    }
                } else if (lines[i].contains("clipName")) {
                    String[] split = lines[i].split("\"");
                    for (int j = 0; j < split.length; j++) {
                        if (split[j].endsWith("value=")) {
                            content.append("\n" + (showFileName ? child + ": " : "") + split[j + 1]);
                            break;
                        }
                    }
                } else if (lines[i].contains("trackName=\"PlayHeroSoundTick")) {
                    while (!lines[i].contains("\"eventName\""))
                        i++;
                    String[] split = lines[i].split("\"");
                    for (int j = 0; j < split.length; j++) {
                        if (split[j].endsWith("value=")) {
                            content.append("\n" + (showFileName ? child + ": " : "") + split[j + 1]);
                            break;
                        }
                    }
                }
            }
        }
        return content.toString();
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
                    DHAExtension.WriteAllBytes(path, AOVAnalyzer.AOVDecompress(bytes));
                    // System.out.println("Giai ma " + path);
                } else {
                    DHAExtension.WriteAllBytes(path, AOVAnalyzer.AOVCompress(bytes));
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
