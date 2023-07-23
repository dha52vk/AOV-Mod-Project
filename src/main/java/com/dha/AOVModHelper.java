package com.dha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

public class AOVModHelper {
    public static String heroListJsonPath = "D:/skinlist(label).json";
    public static String ChannelName = "IzumiTv";
    public static String YoutubeLink = "https://www.youtube.com/@MiyamuraModAOV";

    public static String AOVversion = "1.51.1";
    public static String ExtraPath = "F:\\This PC\\Documents\\AOV\\Extra\\2019.V2\\";
    public static String SkinBattlePath = "F:\\This PC\\Documents\\AOV\\Extra\\2019.V2\\assetbundle\\battle\\skin\\";
    public static String InfosParentPath = "F:/This PC/Documents/AOV/Resources/" + AOVversion + "/Prefab_Characters/";
    public static String ActionsParentPath = "F:/This PC/Documents/AOV/Resources/" + AOVversion
            + "/Ages/Prefab_Characters/Prefab_Hero/";
    public static String DatabinPath = "F:/This PC/Documents/AOV/Resources/" + AOVversion + "/Databin/Client/";
    public static String AssetRefsPath = "F:/This PC/Documents/AOV/Resources/" + AOVversion + "/AssetRefs/";
    public static String LanguageCode = "VN_Garena_VN"; // "CHT_Garena_TW";
    public static String LanguagePath = "F:/This PC/Documents/AOV/Resources/" + AOVversion + "/Languages/"
            + LanguageCode + "/";
    public static String SpecialPath = ".special/";
    public static String saveModPath = "F:/This PC/Documents/AOV/";
    public static String cacheModPath = "F:/This PC/Documents/AOV/cachemod/";
    public static String cacheModPath2 = "F:/This PC/Documents/AOV/cachemod2/";

    // String[] nameElementModToDefault = new String[] {
    // "ArtSkinPrefabLOD", "ArtSkinPrefabLODEx", "ArtSkinLobbyShowLOD",
    // "ArtSkinLobbyIdleShowLOD", "ArtSkinLobbyShowCamera", "useNewMecanim",
    // "LobbyScale", "ArtSkinLobbyNode"
    // };
    List<String> skinNotSupportMod = Arrays.asList(new String[] {});
    List<String> skinHasOrgan = Arrays.asList(new String[] { "1118", "14112", "15010", "5019" });
    List<String> originSkinOrgan = Arrays.asList(new String[] { "1113", "1412", "1501", "5012" });
    List<String> trackTypeRemoveCheckSkinId = Arrays
            .asList(new String[] { "TriggerParticle", "TriggerParticleTick",
                    "BattleUIAnimationDuration" });// "PlayAnimDuration", , "HitTriggerTick" });
    List<String> trackTypeNotRemoveCheckSkinId = Arrays
            .asList(new String[] { "CheckRandomRangeTick", "ChangeSkillTriggerTick" });

    public static List<String> idNotSwap = new ArrayList<>(Arrays.asList(new String[] {
            "19014", "11213", "13211", "50118", "16711", "19610", "13610", "11813", "5157", "5255",
            "1135", "1913", "5069", "5483", "1696", "1209", "5464", "16712", "10618", "11617", "11812", "50114", "1669",
            "15213"
    }));

    public static List<String> idChangeVirtualCheck = new ArrayList<>(Arrays.asList(new String[] {
            "13012"
    }));

    Map<Integer, Integer> skinSoundSpecial = new HashMap<Integer, Integer>() {
        {
            put(54303, 54302);
        }
    };

    String modPackName;
    boolean echo;
    boolean highlightMod = false;
    boolean copyBattleFile = true;

    public AOVModHelper() {
    }

    public void setModPackName(String modPackName) {
        this.modPackName = modPackName;
    }

    public void setEcho(boolean echo) {
        this.echo = echo;
    }

    public void modSkin(ModInfo modInfo) {
        modSkin(Arrays.asList(new ModInfo[] { modInfo }));
    }

    public void modSkin(List<ModInfo> modList) {
        try {
            if (echo) {
                update("\nDang mod pack " + modPackName + "...");
            }
            DHAExtension.deleteDir(saveModPath + modPackName);
            DHAExtension.deleteDir(saveModPath + modPackName + " (may yeu)");
            modIcon(modList);
            modLabel(modList);
            modInfos(modList);
            modOrgan(modList);
            modActions(modList);
            modLiteBullet(modList);
            modSkillMark(modList);
            modSound(modList);
            if (modList.size() > 4) {
                update("Creating low pack...");
                DHAExtension.copy(saveModPath + modPackName, saveModPath + modPackName + " (may yeu)");
            }
            modBack(modList);
            modHaste(modList);
            if (highlightMod) {
                update("Creating highlight pack...");
                DHAExtension.copy(saveModPath + modPackName, saveModPath + modPackName + " (highlight)");
                String actionsPath = saveModPath + modPackName + " (highlight)"
                        + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/Actor_"
                        + modList.get(0).newSkin.id.substring(0, 3) + "_Actions.pkg.bytes";
                highlightSkill(actionsPath, 4);
            }

            // copy extra battle skin
            if (copyBattleFile) {
                update("Copying battle files...");
                String battlePath = saveModPath + modPackName + "/files/Extra/2019.V2/assetbundle/battle/skin/";
                new File(battlePath).mkdirs();
                for (ModInfo modInfo : modList) {
                    FilenameFilter filter = (parent, filename) -> {
                        return filename.startsWith(modInfo.newSkin.id + "_");
                    };
                    String[] battleFiles = new File(SkinBattlePath).list(filter);
                    for (String fileName : battleFiles) {
                        DHAExtension.copy(SkinBattlePath + fileName, battlePath + fileName);
                    }
                }
            }

            String content = "";
            for (ModInfo info : modList) {
                content += "\n   + " + info.newSkin.name;
            }
            DHAExtension.WriteAllText(saveModPath + modPackName + "/packinfo.txt", content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modInfos(List<ModInfo> modList) throws Exception {
        update(" Dang mod ngoai hinh cua pack " + modPackName + "...");

        String inputCharPath = saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Databin/Client/Character/ResCharacterComponent.bytes";
        String outputCharPath = inputCharPath;
        if (!new File(inputCharPath).exists()) {
            inputCharPath = DatabinPath + "Character/ResCharacterComponent.bytes";
        }
        ListCharComponent listCharComponent = new ListCharComponent(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputCharPath)));

        Element channelNameElement = new Element(JT.Pri);
        channelNameElement.setName("ChannelName");
        channelNameElement.setType(AnalyzerType.string);
        channelNameElement.setValue(ChannelName);
        Element YTBLink = new Element(JT.Pri);
        YTBLink.setName("YoutubeLink");
        YTBLink.setType(AnalyzerType.string);
        YTBLink.setValue(YoutubeLink);
        Element[] creditElements = new Element[] {
                channelNameElement, YTBLink
        };
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modInfo);
        for (int l = 0; l < modList.size(); l++) {
            Element element = null, trapElement = null;
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modInfo)
            // continue;

            String newId = modInfo.newSkin.id;

            String heroId = newId.substring(0, 3);
            update("    + Modding infos " + (l + 1) + "/" + modList.size() + ": " + modInfo);

            String inputZipPath = saveModPath + modPackName
                    + "/files/Resources/" + AOVversion + "/Prefab_Characters/Actor_" + heroId
                    + "_Infos.pkg.bytes";
            String outputZipPath = inputZipPath;
            if (!new File(inputZipPath).exists()) {
                inputZipPath = InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes";
            }
            if (new File(cacheModPath).exists()) {
                DHAExtension.deleteDir(cacheModPath);
            }
            new File(cacheModPath).mkdirs();
            ZipExtension.unzip(inputZipPath, cacheModPath);

            String inputPath = "";
            String heroCodeName = new File(cacheModPath + "Prefab_Hero/").list()[0];
            inputPath = cacheModPath + "Prefab_Hero/" + heroCodeName + "/" + heroCodeName + "_actorinfo.bytes";
            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            element = new Element(AOVAnalyzer.AOVDecompress(inputBytes));
            String trapInputPath = cacheModPath + "Prefab_Hero/" + heroCodeName + "/" + heroCodeName
                    + "_trap_actorinfo.bytes";
            if (new File(trapInputPath).exists()) {
                // System.out.println("has trap");
                inputBytes = DHAExtension.ReadAllBytes(trapInputPath);
                trapElement = new Element(AOVAnalyzer.AOVDecompress(inputBytes));
            }

            if (!element.containsChild("YTBLink")) {
                for (Element creditElement : creditElements) {
                    element.addChild(1, creditElement);
                    if (trapElement != null)
                        trapElement.addChild(1, creditElement);
                }
            }
            int removeAt = 1 + creditElements.length;
            for (Skin targetSkin : modInfo.targetSkins) {
                int targetIndex = -1, newIndex = -1;
                String targetId = targetSkin.id;
                int comId = Integer.parseInt(targetId.substring(3)) - 1;
                listCharComponent.removeSkinComponent(Integer.parseInt(heroId) * 100 + comId);
                if (targetSkin.id.equals(heroId + "1")) {
                    targetIndex = -1;
                    while (!element.getChild(removeAt).nameS.equals("SkinPrefab")) {
                        if (!element.getChild(removeAt).nameS.equals("useMecanim")
                                && !element.getChild(removeAt).nameS.equals("useNewMecanim")
                                && !element.getChild(removeAt).nameS.equals("oriSkinUseNewMecanim"))
                            element.removeChildAt(removeAt);
                        else
                            removeAt++;
                    }
                    for (int s = 0; s < element.getChild("SkinPrefab").getChildLength(); s++) {
                        Element skin = element.getChild("SkinPrefab").getChild(s);
                        String code = skin.getChild(0).getChild(0).valueS;
                        if (code == null)
                            continue;
                        String[] split = code.split("/");
                        String id = split[split.length - 1].split("_")[0];
                        if (id.equals(newId)) {
                            newIndex = s;
                            break;
                        }
                    }
                    Element skin;
                    if (!new File(SpecialPath + "infos/" + modInfo.newSkin.id + ".bytes").exists()) {
                        skin = element.getChild("SkinPrefab").getChild(newIndex);
                    } else {
                        skin = new Element(
                                DHAExtension.ReadAllBytes(SpecialPath + "infos/" + modInfo.newSkin.id + ".bytes"));
                    }
                    if (skin.containsChild("useNewMecanim")) {
                        // for (int s = 0; s < element.getChild("SkinPrefab").getChildLength(); s++) {
                        // if (newIndex == s) {
                        // continue;
                        // }
                        // Element skin2 = element.getChild("SkinPrefab").getChild(s);
                        // String code = skin2.getChild(0).getChild(0).valueS;
                        // if (code == null)
                        // continue;
                        // String[] split = code.split("/");
                        // String id = split[split.length - 1].split("_")[0];
                        // boolean kt = false;
                        // for (int i = 0; i < modInfo.targetSkins.size(); i++) {
                        // if (id.equals(modInfo.targetSkins.get(i).id)) {
                        // kt = true;
                        // }
                        // }
                        // if (kt) {
                        // continue;
                        // }
                        // if (!skin2.containsChild("useNewMecanim")) {
                        // skin2.setChild(0, skin.getChild(0));
                        // // update(s + ": " + id + " replace because don't use new mec anim");
                        // element.getChild("SkinPrefab").setChild(s, skin2);
                        // }
                        // }
                    }
                    String[] nameElementRemove = new String[] { "ArtSkinPrefabLOD", "ArtSkinPrefabLODEx",
                            "ArtSkinLobbyShowLOD" };
                    for (int i = 0; i < skin.getChildLength(); i++) {
                        Element e = skin.getChild(i).clone();
                        // if (!Arrays.asList(nameElementModToDefault).contains(e.nameS))
                        // continue;
                        if (Arrays.asList(nameElementRemove).contains(e.nameS))
                            e.setName(e.nameS.replace("Skin", ""));
                        if (e.nameS.equals("useNewMecanim")) {
                            e.setName("oriSkinUseNewMecanim");
                        }
                        element.addChild(removeAt, e);
                    }
                } else {
                    Element newSkin = null;
                    for (int i = 0; i < element.getChild("SkinPrefab").getChildLength(); i++) {
                        Element skin = element.getChild("SkinPrefab").getChild(i);
                        String code = skin.getChild(2).getChild(0).valueS;
                        if (code == null)
                            continue;
                        String[] split = code.split("/");
                        String id = split[split.length - 1].split("_")[0];
                        if (id.equals(newId)) {
                            newIndex = i;
                            newSkin = skin.clone();
                            if (targetIndex != -1)
                                break;
                        } else if (id.equals(targetId)) {
                            targetIndex = i;
                            if (newSkin != null)
                                break;
                        }
                    }
                    if (targetIndex == -1)
                        continue;
                    if (!new File(SpecialPath + "infos/" + modInfo.newSkin.id + ".bytes").exists()) {
                        newSkin = element.getChild("SkinPrefab").getChild(newIndex);
                    } else {
                        newSkin = new Element(
                                DHAExtension.ReadAllBytes(SpecialPath + "infos/" + modInfo.newSkin.id + ".bytes"));
                    }
                    element.getChild("SkinPrefab").setChild(targetIndex, newSkin);
                }

                if (trapElement != null) {
                    if (targetIndex == -1) {
                        String[] nameElementRemove = new String[] { "ArtSkinPrefabLOD", "ArtSkinPrefabLODEx",
                                "ArtSkinLobbyShowLOD" };
                        for (int i = 0; i < trapElement.getChild("SkinPrefab").getChild(newIndex)
                                .getChildLength(); i++) {
                            Element e = trapElement.getChild("SkinPrefab").getChild(newIndex).getChild(i).clone();
                            if (Arrays.asList(nameElementRemove).contains(e.nameS))
                                e.setName(e.nameS.replace("Skin", ""));
                            trapElement.addChild(removeAt, e);
                        }
                    } else {
                        Element newTrap = trapElement.getChild("SkinPrefab").getChild(newIndex).clone();
                        trapElement.getChild("SkinPrefab").setChild(targetIndex, newTrap);
                    }
                }
            }
            // DHAExtension.WriteAllBytes("D:/test.bytes", element.getBytes());
            DHAExtension.WriteAllBytes(inputPath, AOVAnalyzer.AOVCompress(element.getBytes()));
            if (trapElement != null) {
                DHAExtension.WriteAllBytes(trapInputPath, AOVAnalyzer.AOVCompress(trapElement.getBytes()));
            }
            ZipExtension.zipDir(cacheModPath + new File(cacheModPath).list()[0],
                    outputZipPath);
        }
        DHAExtension.WriteAllBytes(outputCharPath, AOVAnalyzer.AOVCompress(listCharComponent.getBytes()));
    }

    public void modOrgan(List<ModInfo> modList) {
        update(" Dang mod hieu ung ve than pack " + modPackName);

        String inputPath = DatabinPath + "Actor/organSkin.bytes";
        String outputPath = saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Databin/Client/Actor/organSkin.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;

        byte[] outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));

        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modOrgan || !skinHasOrgan.contains(modInfo.newSkin.id));
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            String id = modInfo.newSkin.id;
            // if (!modInfo.modSettings.modOrgan || !skinHasOrgan.contains(id)) {
            // continue;
            // }
            update("    + Modding organs " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String originId = originSkinOrgan.get(skinHasOrgan.indexOf(id));

            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int newId = Integer.parseInt(heroId) * 100 + Integer.parseInt(skinId) - 1;
            int targetId = Integer.parseInt(heroId) * 100 + Integer.parseInt(originId.substring(3)) - 1;
            outputBytes = DHAExtension.replaceBytes(outputBytes, DHAExtension.toBytes(newId),
                    DHAExtension.toBytes(targetId));
        }
        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(outputBytes));
    }

    public void modActions(List<ModInfo> modList) throws Exception {
        update(" Dang mod hieu ung pack " + modPackName);
        String inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
        if (new File(saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                .exists())
            inputZipPath = saveModPath + modPackName
                    + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";

        if (new File(cacheModPath2).exists()) {
            DHAExtension.deleteDir(cacheModPath2);
        }
        new File(cacheModPath2).mkdirs();
        ZipExtension.unzip(inputZipPath, cacheModPath2);
        String filemodName = "commonresource/Dance.xml";
        String inputPath = cacheModPath2 + filemodName;
        String outputPath = inputPath;

        byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
        byte[] outputBytes = AOVAnalyzer.AOVDecompress(inputBytes);
        if (outputBytes == null)
            return;

        ProjectXML danceXml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));

        List<Node> animTrackList = new ArrayList<>();
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2);
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
            // continue;
            // }
            if (skinNotSupportMod.contains(modInfo.newSkin.id)) {
                update("   + Skin not support mod: " + modInfo.newSkin);
                continue;
            }
            update("    + Modding actions " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            inputZipPath = ActionsParentPath + "Actor_" + heroId + "_Actions.pkg.bytes";

            if (new File(cacheModPath).exists()) {
                DHAExtension.deleteDir(cacheModPath);
            }
            new File(cacheModPath).mkdirs();
            ZipExtension.unzip(inputZipPath, cacheModPath);

            filemodName = "";
            for (int i = 0; i < new File(cacheModPath + filemodName).list().length; i++) {
                String filePath = new File(cacheModPath + filemodName).list()[i];
                if (new File(cacheModPath + filemodName + filePath).isDirectory()) {
                    filemodName += filePath + "/";
                    i = -1;
                } else {
                    break;
                }
            }

            for (String filename : new File(cacheModPath + filemodName).list()) {
                if (filename.toLowerCase().contains("back") || filename.toLowerCase().contains("born")
                        || (filename.toLowerCase().contains("death") && !modInfo.newSkin.hasDeathEffect)) {
                    // System.out.println("skiped " + filename);
                    continue;
                }
                inputPath = cacheModPath + filemodName + filename;
                outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
                if (outputBytes == null)
                    continue;

                ProjectXML xml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));
                // if (idChangeVirtualCheck.contains(modInfo.newSkin.id))
                xml.changeCheckVirtual();
                if (!(modInfo.newSkin.filenameNotMod != null
                        && Arrays.asList(modInfo.newSkin.filenameNotMod).contains(filename.toLowerCase()))) {
                    xml.setValue("String", new String[] { "resourceName", "resourceName2", "prefabName", "prefab" },
                            (StringOperator) (value) -> {
                                if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/"))
                                    return value;
                                String[] split = value.split("/");
                                String newValue;
                                if (!modInfo.newSkin.isAwakeSkin) {
                                    newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod + "/"
                                            + split[split.length - 1];
                                } else {
                                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5/"
                                            + split[split.length - 1];
                                }
                                return newValue;
                            });
                    xml.setValue("bool", "bAllowEmptyEffect", "false");
                    if (modInfo.modSettings.modSound && modInfo.newSkin.getSkinLevel() > 2) {
                        // List<Node> playSoundTick = xml.getTrackNodeByType("PlayHeroSoundTick", true);
                        xml.setValue("String", "eventName", "PlayHeroSoundTick", (value) -> {
                            if (!modInfo.newSkin.isAwakeSkin) {
                                return value + "_Skin" + skin;
                            } else {
                                if (value.contains("_VO") || value.toLowerCase().contains("voice")) {
                                    return value + "_Skin" + skin + "_AW" + modInfo.newSkin.levelVOXUnlock;
                                } else {
                                    return value + "_Skin" + skin + "_AW" + modInfo.newSkin.levelSFXUnlock;
                                }
                            }
                        });
                        // for (Node node : playSoundTick){
                        // xml.appendActionChild(node);
                        // }
                    }
                    List<Integer> listIndex = xml.getTrackIndexByType("CheckSkinIdTick");
                    List<Integer> listVirtualIndex = xml.getTrackIndexByType("CheckSkinIdVirtualTick");
                    listIndex.addAll(listVirtualIndex);
                    boolean hasVirtual = listVirtualIndex.size() != 0;
                    // hasVirtual=false;
                    List<Integer> listIndexNot = new ArrayList<>();
                    NodeList trackList = xml.getNodeListByTagName("Track");
                    for (int j = 0; j < listIndex.size(); j++) {
                        Node event = trackList.item(listIndex.get(j)).getChildNodes()
                                .item(trackList.item(listIndex.get(j)).getChildNodes().getLength() - 2);
                        NodeList eventChild = event.getChildNodes();
                        boolean ok = false;
                        for (int i = 0; i < eventChild.getLength(); i++) {
                            NamedNodeMap attr = eventChild.item(i).getAttributes();
                            if (attr != null && attr.getNamedItem("name").getNodeValue().equals("skinId")) {
                                ok = true;
                                if (!attr.getNamedItem("value").getNodeValue().equals(idMod + "")) {
                                    listIndex.remove(j);
                                    j--;
                                } else {
                                    if (!hasVirtual) {
                                        attr.getNamedItem("value").setNodeValue(heroId + "98");
                                        // attr.getNamedItem("value").setNodeValue((29300 + skin) + "");
                                        if (eventChild.item(i + 2) != null) {
                                            NamedNodeMap attr3 = eventChild.item(i + 2).getAttributes();
                                            if (attr3 != null
                                                    && attr3.getNamedItem("name").getNodeValue()
                                                            .equals("bSkipLogicCheck")) {
                                                // attr3.getNamedItem("value").setNodeValue("false");
                                                i += 2;
                                            }
                                        }
                                    }
                                    if (eventChild.item(i + 2) != null) {
                                        NamedNodeMap attr2 = eventChild.item(i + 2).getAttributes();
                                        if (attr2.getNamedItem("name").getNodeValue().equals("bEqual")) {
                                            if (attr2.getNamedItem("value").getNodeValue().equals("false")) {
                                                if (!hasVirtual) {
                                                    event.removeChild(eventChild.item(i + 2));
                                                    // attr2.getNamedItem("value").setNodeValue("true");
                                                }
                                                listIndexNot.add(listIndex.get(j));
                                                listIndex.remove(j);
                                                j--;
                                            }
                                        }
                                    } else if (!hasVirtual) {
                                        Node node = eventChild.item(3).cloneNode(true);
                                        node.getOwnerDocument().renameNode(node, null, "bool");
                                        node.getAttributes().getNamedItem("name").setNodeValue("bEqual");
                                        node.getAttributes().getNamedItem("value").setNodeValue("false");
                                        node = event.getOwnerDocument().importNode(node, true);
                                        event.insertBefore(node, eventChild.item(5));
                                    }
                                }
                                break;
                            }
                        }
                        if (!ok) {
                            listIndex.remove(j);
                            j--;
                        }
                    }
                    // update(" *" + filename + ": " + listIndex +", " + listIndexNot);
                    NodeList particle = xml.getNodeListByTagName("Track");
                    if (hasVirtual)
                        update(filename + " has virtual");
                    for (String type : trackTypeNotRemoveCheckSkinId){
                        if (xml.getTrackNodeByType(type).size()>0){
                            hasVirtual = true;
                            update(filename + " has track can't remove condition!");
                            fix update check skin id 
                        }
                    }
                    for (int j = 0; j < particle.getLength(); j++) {
                        if (trackTypeNotRemoveCheckSkinId.contains(particle.item(j).getAttributes()
                                .getNamedItem("eventType").getNodeValue())) {
                            continue;
                        }
                        if (filename.equals("A2.xml"))
                            update(particle.item(j).getAttributes().getNamedItem("trackName").getNodeValue());
                        // if (!trackTypeRemoveCheckSkinId.contains(particle.item(j).getAttributes()
                        // .getNamedItem("eventType").getNodeValue())) {
                        // continue;
                        // }
                        NodeList children = particle.item(j).getChildNodes();
                        for (int i = 0; i < children.getLength(); i++) {
                            if (children.item(i).getNodeName().equals("Condition")) {
                                if (children.item(i).getAttributes().getNamedItem("status").getNodeValue()
                                        .equals("true")) {
                                    if (listIndex.contains(Integer
                                            .parseInt(
                                                    children.item(i).getAttributes().getNamedItem("id")
                                                            .getNodeValue()))) {
                                        update("       *" + filename + ": "
                                                + "removed condition "
                                                + particle.item(j).getAttributes().getNamedItem("trackName")
                                                        .getNodeValue()
                                                + " (type: "
                                                + particle.item(j).getAttributes().getNamedItem("eventType")
                                                        .getNodeValue()
                                                + ")");
                                        if (hasVirtual)
                                            particle.item(j).removeChild(children.item(i));
                                    } else if (listIndexNot.contains(Integer
                                            .parseInt(
                                                    children.item(i).getAttributes().getNamedItem("id")
                                                            .getNodeValue()))) {
                                        update("       *" + filename + ": "
                                                + "disabled "
                                                + particle.item(j).getAttributes().getNamedItem("trackName")
                                                        .getNodeValue()
                                                + " (type: "
                                                + particle.item(j).getAttributes().getNamedItem("eventType")
                                                        .getNodeValue()
                                                + ")");
                                        if (hasVirtual)
                                            particle.item(j).getAttributes().getNamedItem("enabled")
                                                    .setNodeValue("false");
                                        break;
                                    }
                                } else {
                                    if (listIndex.contains(Integer
                                            .parseInt(
                                                    children.item(i).getAttributes().getNamedItem("id")
                                                            .getNodeValue()))) {
                                        update("       *" + filename + ": "
                                                + "disabled "
                                                + particle.item(j).getAttributes().getNamedItem("trackName")
                                                        .getNodeValue()
                                                + " (type: "
                                                + particle.item(j).getAttributes().getNamedItem("eventType")
                                                        .getNodeValue()
                                                + ")");
                                        if (hasVirtual)
                                            particle.item(j).getAttributes().getNamedItem("enabled")
                                                    .setNodeValue("false");
                                    } else if (listIndexNot.contains(Integer
                                            .parseInt(
                                                    children.item(i).getAttributes().getNamedItem("id")
                                                            .getNodeValue()))) {
                                        update("       *" + filename + ": "
                                                + "removed condition "
                                                + particle.item(j).getAttributes().getNamedItem("trackName")
                                                        .getNodeValue()
                                                + " (type: "
                                                + particle.item(j).getAttributes().getNamedItem("eventType")
                                                        .getNodeValue()
                                                + ")");
                                        if (hasVirtual)
                                            particle.item(j).removeChild(children.item(i));
                                    }
                                }
                            }
                        }
                    }
                    if (modInfo.newSkin.changeAnim) {
                        xml.setValue("String", "clipName", "PlayAnimDuration", (value) -> {
                            return idMod + "/" + value;
                        });
                        List<Node> listAnimTrack = xml.getTrackNodeByType("PlayAnimDuration");
                        for (Node animTrack : listAnimTrack) {
                            while (!animTrack.getChildNodes().item(0).getNodeName().equals("Event")) {
                                animTrack.removeChild(animTrack.getFirstChild());
                            }
                            Node event = animTrack.getChildNodes().item(0);
                            NodeList children = event.getChildNodes();
                            for (int i = 0; i < children.getLength(); i++) {
                                if (children.item(i).getNodeName().equals("#text")
                                        || (!children.item(i).getAttributes().getNamedItem("name").getNodeValue()
                                                .equals("targetId")
                                                && !children.item(i).getAttributes().getNamedItem("name").getNodeValue()
                                                        .equals("clipName"))) {
                                    event.removeChild(children.item(i));
                                    i--;
                                }
                            }
                            animTrackList.add(animTrack);
                        }
                    }
                }

                // xml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + "
                // ");
                xml = specialModAction(xml, inputPath, idMod);

                // DHAExtension.WriteAllBytes(inputPath, xml.getXmlString().getBytes());
                DHAExtension.WriteAllBytes(inputPath,
                        AOVAnalyzer.AOVCompress(xml.getXmlString().getBytes()));
            }

            ZipExtension.zipDir(cacheModPath + filemodName.split("/")[0],
                    saveModPath + modPackName
                            + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/Actor_"
                            + heroId + "_Actions.pkg.bytes");
        }

        for (Node node : animTrackList) {
            danceXml.appendActionChild(node);
        }

        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(danceXml.getXmlString().getBytes()));

        String[] subDir = new File(cacheModPath2).list();
        for (int i = 0; i < subDir.length; i++) {
            subDir[i] = cacheModPath2 + subDir[i];
        }
        ZipExtension.zipDir(subDir,
                saveModPath + modPackName
                        + "/files/Resources/" + AOVversion
                        + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");

        update(" Fix khung...");
        modAssetRef(modList);
    }

    public void modActionsNew(List<ModInfo> modList) throws Exception {
        update(" Dang mod hieu ung pack " + modPackName);
        String inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
        if (new File(saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                .exists())
            inputZipPath = saveModPath + modPackName
                    + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";

        if (new File(cacheModPath2).exists()) {
            DHAExtension.deleteDir(cacheModPath2);
        }
        new File(cacheModPath2).mkdirs();
        ZipExtension.unzip(inputZipPath, cacheModPath2);
        String filemodName = "commonresource/Dance.xml";
        String inputPath = cacheModPath2 + filemodName;
        String outputPath = inputPath;

        byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
        byte[] outputBytes = AOVAnalyzer.AOVDecompress(inputBytes);
        if (outputBytes == null)
            return;

        ProjectXML danceXml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));

        List<Node> animTrackList = new ArrayList<>();
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2);
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            update("    + Modding actions " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            inputZipPath = ActionsParentPath + "Actor_" + heroId + "_Actions.pkg.bytes";

            if (new File(cacheModPath).exists()) {
                DHAExtension.deleteDir(cacheModPath);
            }
            new File(cacheModPath).mkdirs();
            ZipExtension.unzip(inputZipPath, cacheModPath);

            filemodName = "";
            for (int i = 0; i < new File(cacheModPath + filemodName).list().length; i++) {
                String filePath = new File(cacheModPath + filemodName).list()[i];
                if (new File(cacheModPath + filemodName + filePath).isDirectory()) {
                    filemodName += filePath + "/";
                    i = -1;
                } else {
                    break;
                }
            }

            for (String filename : new File(cacheModPath + filemodName).list()) {
                if (filename.toLowerCase().contains("back") || filename.toLowerCase().contains("born")
                        || (filename.toLowerCase().contains("death") && !modInfo.newSkin.hasDeathEffect)) {
                    continue;
                }
                inputPath = cacheModPath + filemodName + filename;
                outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
                if (outputBytes == null)
                    continue;

                ProjectXML xml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));

                if (!(modInfo.newSkin.filenameNotMod != null
                        && Arrays.asList(modInfo.newSkin.filenameNotMod).contains(filename.toLowerCase()))) {
                    xml.setValue("String", new String[] { "resourceName", "resourceName2", "prefabName", "prefab" },
                            (StringOperator) (value) -> {
                                if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/"))
                                    return value;
                                String[] split = value.split("/");
                                String newValue;
                                if (!modInfo.newSkin.isAwakeSkin) {
                                    newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod + "/"
                                            + split[split.length - 1];
                                } else {
                                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5/"
                                            + split[split.length - 1];
                                }
                                return newValue;
                            });
                    xml.setValue("bool", "bAllowEmptyEffect", "false");
                    if (modInfo.modSettings.modSound && modInfo.newSkin.getSkinLevel() > 2) {
                        xml.setValue("String", "eventName", "PlayHeroSoundTick", (value) -> {
                            if (!modInfo.newSkin.isAwakeSkin) {
                                return value + "_Skin" + skin;
                            } else {
                                if (value.contains("_VO") || value.toLowerCase().contains("voice")) {
                                    return value + "_Skin" + skin + "_AW" + modInfo.newSkin.levelVOXUnlock;
                                } else {
                                    return value + "_Skin" + skin + "_AW" + modInfo.newSkin.levelSFXUnlock;
                                }
                            }
                        });
                    }

                    if (modInfo.newSkin.changeAnim) {
                        xml.setValue("String", "clipName", "PlayAnimDuration", (value) -> {
                            return idMod + "/" + value;
                        });
                        List<Node> listAnimTrack = xml.getTrackNodeByType("PlayAnimDuration");
                        for (Node animTrack : listAnimTrack) {
                            while (!animTrack.getChildNodes().item(0).getNodeName().equals("Event")) {
                                animTrack.removeChild(animTrack.getFirstChild());
                            }
                            Node event = animTrack.getChildNodes().item(0);
                            NodeList children = event.getChildNodes();
                            for (int i = 0; i < children.getLength(); i++) {
                                if (children.item(i).getNodeName().equals("#text")
                                        || (!children.item(i).getAttributes().getNamedItem("name").getNodeValue()
                                                .equals("targetId")
                                                && !children.item(i).getAttributes().getNamedItem("name").getNodeValue()
                                                        .equals("clipName"))) {
                                    event.removeChild(children.item(i));
                                    i--;
                                }
                            }
                            animTrackList.add(animTrack);
                        }
                    }

                    List<Integer> checkSkinIdTickIndexs = xml.getTrackIndexByType("CheckSkinIdTick");
                    List<Integer> checkSkinIdTickNotEqualIndexs = new ArrayList<>();
                    NodeList tracks = xml.getNodeListByTagName("Track");
                    for (int i = 0; i < checkSkinIdTickIndexs.size(); i++) {
                        Node checkSkinIdTick = tracks.item(checkSkinIdTickIndexs.get(i));
                        String idCheck = CustomNode.getChildValue(CustomNode.getChild(checkSkinIdTick, "Event"),
                                "int", "skinId");
                        if (!idCheck.equals(idMod + "")) {
                            checkSkinIdTickIndexs.remove(i);
                            i--;
                            continue;
                        }
                        String bEqual = CustomNode.getChildValue(CustomNode.getChild(checkSkinIdTick, "Event"),
                                "bool", "bEqual");
                        if (bEqual != null && bEqual.equals("false")) {
                            checkSkinIdTickNotEqualIndexs.add(checkSkinIdTickIndexs.get(i));
                            checkSkinIdTickIndexs.remove(i);
                            i--;
                        }
                    }
                    // update(filename +": " + checkSkinIdTickIndexs +" , " +
                    // checkSkinIdTickNotEqualIndexs);
                    NodeList conditions = xml.getNodeListByTagName("Condition");
                    int condiLen = Integer.valueOf(conditions.getLength());
                    for (int j = 0; j < condiLen; j++) {
                        String trackName = CustomNode.getAttribute(conditions.item(j).getParentNode(),
                                "trackName");
                        String trackType = CustomNode.getAttribute(conditions.item(j).getParentNode(),
                                "eventType");
                        if (trackType.toLowerCase().contains("check"))
                            continue;
                        if (checkSkinIdTickIndexs
                                .contains(Integer.parseInt(CustomNode.getAttribute(conditions.item(j), "id")))) {
                            if (CustomNode.getAttribute(conditions.item(j), "status").equals("true")) {
                                update(filename + ": removed condition " + trackName + "(type: " + trackType + ")");
                                Node duplicate = conditions.item(j).getParentNode().cloneNode(true);
                                for (int i = 0; i < duplicate.getChildNodes().getLength(); i++) {
                                    if (duplicate.getChildNodes().item(i).getNodeName().equals("Condition")) {
                                        if (checkSkinIdTickIndexs
                                                .contains(Integer.parseInt(CustomNode
                                                        .getAttribute(duplicate.getChildNodes().item(i), "id")))
                                                && CustomNode.getAttribute(duplicate.getChildNodes().item(i), "status")
                                                        .equals("true")) {
                                            // duplicate.removeChild(duplicate.getChildNodes().item(i));
                                            duplicate.getChildNodes().item(i).getAttributes().getNamedItem("status")
                                                    .setNodeValue("false");
                                            // duplicate.getAttributes().getNamedItem("enabled").setNodeValue("true");
                                        }
                                    }
                                }
                                xml.appendActionChild(duplicate);
                            } else {
                                update(filename + ": disable track " + trackName + "(type: " + trackType + ")");
                                conditions.item(j).getParentNode().getAttributes().getNamedItem("enabled")
                                        .setNodeValue("false");
                            }
                        } else if (checkSkinIdTickNotEqualIndexs
                                .contains(Integer.parseInt(CustomNode.getAttribute(conditions.item(j), "id")))) {
                            update(filename + ": disable track " + trackName + "(type: " + trackType + ")");
                            conditions.item(j).getParentNode().getAttributes().getNamedItem("enabled")
                                    .setNodeValue("false");
                        }
                    }
                }

                xml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + " ");
                xml = specialModAction(xml, inputPath, idMod);

                DHAExtension.WriteAllBytes(inputPath, xml.getXmlString().getBytes());
                // DHAExtension.WriteAllBytes(inputPath,
                // AOVAnalyzer.AOVCompress(xml.getXmlString().getBytes()));
            }

            ZipExtension.zipDir(cacheModPath + filemodName.split("/")[0],
                    saveModPath + modPackName
                            + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/Actor_"
                            + heroId + "_Actions.pkg.bytes");
        }

        for (Node node : animTrackList) {
            danceXml.appendActionChild(node);
        }

        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(danceXml.getXmlString().getBytes()));

        String[] subDir = new File(cacheModPath2).list();
        for (int i = 0; i < subDir.length; i++) {
            subDir[i] = cacheModPath2 + subDir[i];
        }
        ZipExtension.zipDir(subDir,
                saveModPath + modPackName
                        + "/files/Resources/" + AOVversion
                        + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");

        update(" Fix khung...");
        modAssetRef(modList);
    }

    public void highlightSkill(String sourceActionsPath, int hightlightLevel) throws Exception {
        highlightSkill(Arrays.asList(new String[] { sourceActionsPath }),
                Arrays.asList(new Integer[] { hightlightLevel }));
    }

    public void highlightSkill(List<String> sourceActionsPaths, List<Integer> hightlightLevels) throws Exception {
        for (int l = 0; l < sourceActionsPaths.size(); l++) {
            String inputZipPath = sourceActionsPaths.get(l);

            if (new File(cacheModPath).exists()) {
                DHAExtension.deleteDir(cacheModPath);
            }
            new File(cacheModPath).mkdirs();
            ZipExtension.unzip(inputZipPath, cacheModPath);

            String filemodName = "";
            for (int i = 0; i < new File(cacheModPath + filemodName).list().length; i++) {
                String filePath = new File(cacheModPath + filemodName).list()[i];
                if (new File(cacheModPath + filemodName + filePath).isDirectory()) {
                    filemodName += filePath + "/";
                    i = -1;
                } else {
                    break;
                }
            }

            for (String filename : new File(cacheModPath + filemodName).list()) {
                if (filename.toLowerCase().contains("back") || filename.toLowerCase().contains("haste"))
                    continue;
                String inputPath = cacheModPath + filemodName + filename;
                byte[] outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
                if (outputBytes == null)
                    continue;

                ProjectXML xml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));
                List<Node> trackEffects = new ArrayList<>();
                NodeList strList = xml.getNodeListByTagName("String");
                for (int i = 0; i < strList.getLength(); i++) {
                    if (strList.item(i).getAttributes().getNamedItem("value").getNodeValue().toLowerCase()
                            .contains("prefab_skill_effects/hero_skill_effects/")) {
                        Node parentTrack = strList.item(i);
                        while (!(parentTrack = parentTrack.getParentNode()).getNodeName().equals("Track"))
                            ;
                        trackEffects.add(parentTrack.cloneNode(true));
                    }
                }
                int highlightLv = hightlightLevels.get(l); // trackEffects.size() < 4 ? 2 :
                                                           // 1;//getHighlightLevel(filename);
                if (filename.contains("E"))
                    highlightLv = 0;
                // update(filename + ": " + highlightLv);
                for (Node track : trackEffects) {
                    Node event = CustomNode.getChild(track, "Event");
                    if (event == null
                            || event.getAttributes().getNamedItem("isDuration").getNodeValue().equals("true")) {
                        continue;
                    }
                    for (int i = 0; i < highlightLv; i++)
                        xml.appendActionChild(track);
                }
                xml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + " ");

                // DHAExtension.WriteAllBytes(inputPath, xml.getXmlString().getBytes());
                DHAExtension.WriteAllBytes(inputPath,
                        AOVAnalyzer.AOVCompress(xml.getXmlString().getBytes()));
            }

            ZipExtension.zipDir(cacheModPath + filemodName.split("/")[0],
                    inputZipPath);
        }
    }

    public void modAssetRef(List<ModInfo> modList) throws Exception {
        Element channelNameElement = new Element(JT.Pri);
        channelNameElement.setName("ChannelName");
        channelNameElement.setType(AnalyzerType.string);
        channelNameElement.setValue(ChannelName);
        Element YTBLink = new Element(JT.Pri);
        YTBLink.setName("YoutubeLink");
        YTBLink.setType(AnalyzerType.string);
        YTBLink.setValue(YoutubeLink);
        Element[] creditElements = new Element[] {
                channelNameElement, YTBLink
        };
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2);
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
            // continue;
            // }

            String id = modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            String inputPath = AssetRefsPath + "Hero/" + heroId + "_AssetRef.bytes";
            String outputPath;
            outputPath = saveModPath + modPackName
                    + "/files/Resources/" + AOVversion + "/AssetRefs/Hero/" + heroId + "_AssetRef.bytes";
            if (new File(outputPath).exists())
                inputPath = outputPath;
            byte[] outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
            Element assetRef = new Element(outputBytes);
            for (Element creditElement : creditElements) {
                assetRef.addChild(0, creditElement);
            }
            assetRef = assetRef.replaceValue(AnalyzerType.string, (value) -> {
                if (!value.toLowerCase().contains("prefab_skill_effect"))
                    return value;
                String[] split = value.split("/");
                if (tryParse(split[split.length - 1].split("_")[0])) {
                    if (split[split.length - 1].split("_")[0].length() == 5) {
                        return value;
                    }
                }
                String newValue;
                if (!modInfo.newSkin.isAwakeSkin) {
                    newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod + "/"
                            + split[split.length - 1];
                } else {
                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5/"
                            + split[split.length - 1];
                }
                return newValue;
            });
            DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(assetRef.getBytes()));
        }
    }

    public void modLiteBullet(List<ModInfo> modList) throws IOException {
        update(" Dang mod danh thuong pack " + modPackName);

        String inputPath = DatabinPath + "Skill/liteBulletCfg.bytes";
        String outputPath;
        outputPath = saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Databin/Client/Skill/liteBulletCfg.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;
        ListBulletElement listBullet = new ListBulletElement(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath)));
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2
                || !listBullet.containsHeroId(Integer.parseInt(modInfo.newSkin.id.substring(0, 3))));
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
            // continue;
            // }
            update("    + Modding lite bullets " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
            String heroCodeName = zis.getNextEntry().getName().split("/")[1];
            zis.close();

            String newCode, oldCode = "(?i)prefab_skill_effects/hero_skill_effects/" + heroCodeName;
            if (!modInfo.newSkin.isAwakeSkin) {
                newCode = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" + idMod;
            } else {
                newCode = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5";
            }
            listBullet.replaceBulletEffect(Integer.parseInt(heroId), oldCode, newCode);
        }
        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(listBullet.getBytes()));
    }

    public void modSkillMark(List<ModInfo> modList) throws IOException {
        update(" Dang mod dau an pack " + modPackName);

        String inputPath = DatabinPath + "Skill/skillmark.bytes";
        String outputPath;
        outputPath = saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Databin/Client/Skill/skillmark.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;
        ListMarkElement listMark = new ListMarkElement(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath)));
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2
                || !listMark.containsHeroId(Integer.parseInt(modInfo.newSkin.id.substring(0, 3))));
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
            // continue;
            // }
            update("    + Modding skill marks " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
            String heroCodeName = zis.getNextEntry().getName().split("/")[1];
            zis.close();

            String newCode, oldCode = "(?i)prefab_skill_effects/hero_skill_effects/" + heroCodeName;
            if (!modInfo.newSkin.isAwakeSkin) {
                newCode = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" + idMod;
            } else {
                newCode = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5";
            }
            listMark.replaceMarkEffect(Integer.parseInt(heroId), oldCode, newCode);
        }
        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(listMark.getBytes()));
    }

    public void modSound(List<ModInfo> modList) {
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modSound || modInfo.newSkin.getSkinLevel() < 3);
        if (modList.size() == 0)
            return;
        update(" Dang mod am thanh pack " + modPackName);

        String[] inputPaths = new String[] { DatabinPath + "Sound/BattleBank.bytes",
                DatabinPath + "Sound/ChatSound.bytes",
                DatabinPath + "Sound/HeroSound.bytes",
                DatabinPath + "Sound/LobbyBank.bytes",
                DatabinPath + "Sound/LobbySound.bytes"
        };
        String[] outputPaths = new String[inputPaths.length];
        ListSoundElement[] soundListArr = new ListSoundElement[inputPaths.length];
        for (int i = 0; i < inputPaths.length; i++) {
            outputPaths[i] = saveModPath + modPackName
                    + "/files/Resources/" + AOVversion + "/Databin/Client/Sound/"
                    + new File(inputPaths[i]).getName();
            if (new File(outputPaths[i]).exists())
                inputPaths[i] = outputPaths[i];
            soundListArr[i] = new ListSoundElement(AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPaths[i])));
        }
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modSound || modInfo.newSkin.getSkinLevel() < 3) {
            // continue;
            // }
            update("    + Modding sound " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);
            int heroId = Integer.parseInt(modInfo.newSkin.id.substring(0, 3));
            int targetId = heroId * 100 + Integer.parseInt(modInfo.newSkin.id.substring(3)) - 1;
            if (skinSoundSpecial.containsKey(targetId)) {
                targetId = skinSoundSpecial.get(targetId);
                update(targetId + "");
            }
            int baseId = heroId * 100 + Integer.parseInt(modInfo.targetSkins.get(0).id.substring(3)) - 1;
            for (int i = 0; i < soundListArr.length; i++) {
                ListSoundElement targetSounds = null;
                String soundSpecial = SpecialPath + "sound/" + modInfo.newSkin.id + "/"
                        + new File(inputPaths[i]).getName();
                if (new File(soundSpecial).exists()) {
                    targetSounds = new ListSoundElement(DHAExtension.ReadAllBytes(soundSpecial));
                }
                if (targetSounds == null)
                    soundListArr[i].copySound(baseId, targetId);
                else
                    soundListArr[i].setSound(baseId, targetSounds.soundElements);
            }
        }
        for (int i = 0; i < outputPaths.length; i++) {
            DHAExtension.WriteAllBytes(outputPaths[i], AOVAnalyzer.AOVCompress(soundListArr[i].getBytes()));
        }
    }

    public void modIcon(List<ModInfo> modList) throws Exception {
        update(" Dang mod icon, ten va man xuat hien pack " + modPackName);

        String inputPath = DatabinPath + "Actor/heroSkin.bytes";
        String outputPath = saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Databin/Client/Actor/heroSkin.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;

        ListIconElement listIconElement = new ListIconElement(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath)));
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modIcon);
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modIcon)
            // continue;
            update("    + Modding icons " + (l + 1) + "/" + modList.size() + ": " + modInfo);
            int heroId = Integer.parseInt(modInfo.newSkin.id.substring(0, 3));
            int targetId = heroId * 100 + Integer.parseInt(modInfo.newSkin.id.substring(3)) - 1;
            for (Skin skin : modInfo.targetSkins) {
                int baseId = heroId * 100 + Integer.parseInt(skin.id.substring(3)) - 1;
                listIconElement.copyIcon(baseId, targetId, !idNotSwap.contains(modInfo.newSkin.id));
            }
        }
        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(listIconElement.getBytes()));
    }

    public void modLabel(List<ModInfo> modList) throws Exception {
        update(" Dang mod bac skin pack " + modPackName);

        List<Hero> heroList = new Gson().fromJson(DHAExtension.ReadAllText(heroListJsonPath), HeroList.class).heros;
        String inputPath = DatabinPath + "Shop/HeroSkinShop.bytes";
        String outputPath = saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Databin/Client/Shop/HeroSkinShop.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;

        ListLabelElement listLabelElement = new ListLabelElement(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath)));
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modIcon);
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modIcon)
            // continue;
            update("    + Modding label " + (l + 1) + "/" + modList.size() + ": " + modInfo);
            int heroId = Integer.parseInt(modInfo.newSkin.id.substring(0, 3));
            int targetId = heroId * 100 + Integer.parseInt(modInfo.newSkin.id.substring(3)) - 1;
            for (Skin skin : modInfo.targetSkins) {
                int baseId = heroId * 100 + Integer.parseInt(skin.id.substring(3)) - 1;
                int result = listLabelElement.copyLabel(baseId, targetId);
                boolean notfound = false;
                while (result == 2) {
                    notfound = true;
                    for (Hero hero : heroList) {
                        for (Skin skin2 : hero.skins) {
                            if (skin2.label == modInfo.newSkin.label) {
                                targetId = Integer.parseInt(skin2.id.substring(0, 3)) * 100
                                        + Integer.parseInt(skin2.id.substring(3)) - 1;
                                result = listLabelElement.copyLabel(baseId, targetId);
                                if (result != 2) {
                                    break;
                                }
                            }
                        }
                        if (result != 2) {
                            break;
                        }
                    }
                }
                if (notfound) {
                    update("      *changed new label to " + targetId + "(" + modInfo.newSkin.label + ")");
                }
            }
        }
        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(listLabelElement.getBytes()));
    }

    public void modBack(List<ModInfo> modList) throws Exception {
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modBack || modInfo.newSkin.getSkinLevel() < 4);
        if (modList.size() == 0)
            return;
        update(" Dang mod hieu ung bien ve pack " + modPackName);
        String inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
        if (new File(saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                .exists())
            inputZipPath = saveModPath + modPackName
                    + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";

        if (new File(cacheModPath2).exists()) {
            DHAExtension.deleteDir(cacheModPath2);
        }
        new File(cacheModPath2).mkdirs();
        ZipExtension.unzip(inputZipPath, cacheModPath2);
        String filemodName = "commonresource/Back.xml";
        String inputPath = cacheModPath2 + filemodName;
        String outputPath = inputPath;

        byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
        byte[] outputBytes = AOVAnalyzer.AOVDecompress(inputBytes);
        if (outputBytes == null)
            return;

        ProjectXML backXml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));

        List<Node> baseTrack = new ArrayList<>();
        baseTrack.addAll(backXml.getTrackNodeByType("TriggerParticleTick", false));
        baseTrack.addAll(backXml.getTrackNodeByType("TriggerParticle", false));

        List<Node> baseAnimTrack = backXml.getTrackNodeByType("PlayAnimDuration");

        List<Node> conditionList = new ArrayList<>();
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modBack || modInfo.newSkin.getSkinLevel() < 4) {
            // continue;
            // }
            update("    + Modding back " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            int hero = Integer.parseInt(modInfo.newSkin.id.substring(0, 3));

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(InfosParentPath + "Actor_" + hero + "_Infos.pkg.bytes"));
            String heroCodeName = zis.getNextEntry().getName().split("/")[1];
            zis.close();

            String skinId = modInfo.newSkin.id.substring(3, modInfo.newSkin.id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = hero * 100 + skin;
            backXml.insertActionChild(l + 1, ProjectXML.getCheckHeroTickNode(l, hero));
            conditionList.add(ProjectXML.getConditionNode(l + 1, "Mod_by_" + ChannelName + "_" + hero, false));

            ZipInputStream zipin = new ZipInputStream(
                    new FileInputStream(ActionsParentPath + "Actor_" + hero + "_Actions.pkg.bytes"));
            ZipEntry entry;
            NodeList nodeList = null;
            while ((entry = zipin.getNextEntry()) != null) {
                if (entry.getName().endsWith("/" + idMod + "_Back.xml")) {
                    byte[] bytes = AOVAnalyzer.AOVDecompress(zipin.readAllBytes());
                    ProjectXML skinBackXml = new ProjectXML(new String(bytes));
                    skinBackXml.setValue("String", "eventName", "PlayHeroSoundTick", (value) -> {
                        if (!modInfo.newSkin.isAwakeSkin) {
                            return value + "_Skin" + skin;
                        } else {
                            if (value.contains("_VO") || value.toLowerCase().contains("voice")) {
                                return value + "_Skin" + skin + "_AW" + modInfo.newSkin.levelVOXUnlock;
                            } else {
                                return value + "_Skin" + skin + "_AW" + modInfo.newSkin.levelSFXUnlock;
                            }
                        }
                    });
                    nodeList = skinBackXml.getNodeListByTagName("Track");
                    break;
                }
            }
            zipin.close();

            List<Node> baseBackTrack = new ArrayList<>();
            baseBackTrack.addAll(baseTrack);
            if (nodeList != null) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    if (!nodeList.item(i).getAttributes().getNamedItem("eventType").getNodeValue().toLowerCase()
                            .contains("check")
                            && !nodeList.item(i).getAttributes().getNamedItem("eventType").getNodeValue().toLowerCase()
                                    .contains("stoptrack")
                            && !nodeList.item(i).getAttributes().getNamedItem("trackName").getNodeValue().toLowerCase()
                                    .contains("getresource")
                            && !nodeList.item(i).getAttributes().getNamedItem("eventType").getNodeValue().toLowerCase()
                                    .contains("triggerparticle")) {
                        for (int j = 0; j < nodeList.item(i).getChildNodes().getLength(); j++) {
                            Node node = nodeList.item(i).getChildNodes().item(j);
                            if (node.getNodeName().equals("Condition"))
                                nodeList.item(i).removeChild(node);
                        }
                        baseBackTrack.add(nodeList.item(i));
                    }
                }
            }
            if (modInfo.newSkin.isAwakeSkin) {
                for (int i = 0; i < baseAnimTrack.size(); i++) {
                    Node track = baseAnimTrack.get(i).cloneNode(true);
                    for (int j = 0; j < track.getChildNodes().item(track.getChildNodes().getLength() - 2)
                            .getChildNodes()
                            .getLength(); j++) {
                        Node node = track.getChildNodes().item(track.getChildNodes().getLength() - 2).getChildNodes()
                                .item(j);
                        if (node.getAttributes() != null) {
                            if (node.getAttributes().getNamedItem("name").getNodeValue().equals("clipName")) {
                                String value = node.getAttributes().getNamedItem("value").getNodeValue();
                                node.getAttributes().getNamedItem("value").setNodeValue(idMod + "/Awaken/" + value);
                            }
                        }
                    }
                    baseBackTrack.add(track);
                }
            }
            for (int i = 0; i < baseBackTrack.size(); i++) {
                Node track = baseBackTrack.get(i).cloneNode(true);
                // if
                // (track.getAttributes().getNamedItem("trackName").getNodeValue().toLowerCase()
                // .contains("triggerparticle")) {
                // track.getAttributes().getNamedItem("trackName").setNodeValue(
                // track.getAttributes().getNamedItem("trackName").getNodeValue().replaceAll("0",
                // "1"));
                // if
                // (track.getAttributes().getNamedItem("trackName").getNodeValue().toLowerCase()
                // .contains("triggerparticletick")) {
                // track.getAttributes().removeNamedItem("useRefParam");
                // track.getAttributes().removeNamedItem("refParamName");
                // track.getAttributes().removeNamedItem("execOnActionCompleted");
                // track.getAttributes().removeNamedItem("execOnForceStopped");
                // track.getAttributes().getNamedItem("r").setNodeValue("1");
                // track.getAttributes().getNamedItem("g").setNodeValue("0.6");
                // track.getAttributes().getNamedItem("b").setNodeValue("0");
                // track.getChildNodes().item(track.getChildNodes().getLength() -
                // 2).getAttributes()
                // .removeNamedItem("guid");
                // }
                // }
                Node condition = ProjectXML.getConditionNode(l + 1, "Mod_by_" + ChannelName + "_" + hero);
                condition = track.getOwnerDocument().importNode(condition, true);
                track.insertBefore(condition, track.getFirstChild());
                String value = "";
                for (int j = 0; j < track.getChildNodes().item(track.getChildNodes().getLength() - 2).getChildNodes()
                        .getLength(); j++) {
                    Node node = track.getChildNodes().item(track.getChildNodes().getLength() - 2).getChildNodes()
                            .item(j);
                    if (node.getAttributes() != null) {
                        if (node.getAttributes().getNamedItem("name").getNodeValue().equals("resourceName")) {
                            node.getAttributes().getNamedItem("useRefParam").setNodeValue("true");
                            node.getAttributes().removeNamedItem("refParamName");
                            // node.getAttributes().getNamedItem("refParamName").setNodeValue("");
                            String[] split = value.split("/");
                            String newValue;
                            if (!modInfo.newSkin.isAwakeSkin) {
                                newValue = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" + idMod + "/"
                                        + split[split.length - 1];
                            } else {
                                newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5/"
                                        + split[split.length - 1];
                            }
                            node.getAttributes().getNamedItem("value").setNodeValue(newValue);
                            j++;
                            if (track.getChildNodes().item(track.getChildNodes().getLength() - 2).getChildNodes()
                                    .item(j + 1).getAttributes().getNamedItem("name").getNodeValue()
                                    .equals("bindPosOffset")) {
                                j += 2;
                            }
                            while (j < track.getChildNodes().item(track.getChildNodes().getLength() - 2).getChildNodes()
                                    .getLength()) {
                                track.getChildNodes().item(track.getChildNodes().getLength() - 2)
                                        .removeChild(track.getChildNodes().item(track.getChildNodes().getLength() - 2)
                                                .getChildNodes().item(j));
                            }
                        } else if (node.getAttributes().getNamedItem("name").getNodeValue()
                                .equals("parentResourceName")) {
                            value = node.getAttributes().getNamedItem("value").getNodeValue();
                        }
                    }
                }
                backXml.appendActionChild(track);
            }
        }
        for (int i = 0; i < baseTrack.size(); i++) {
            Node track = baseTrack.get(i);
            for (Node condition : conditionList) {
                condition = track.getOwnerDocument().importNode(condition, true);
                track.insertBefore(condition, track.getFirstChild());
            }
        }

        backXml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + "  ");
        // DHAExtension.WriteAllBytes("D:/test.xml", backXml.getXmlString().getBytes());
        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(backXml.getXmlString().getBytes()));

        String[] subDir = new File(cacheModPath2).list();
        for (int i = 0; i < subDir.length; i++) {
            subDir[i] = cacheModPath2 + subDir[i];
        }
        ZipExtension.zipDir(subDir,
                saveModPath + modPackName
                        + "/files/Resources/" + AOVversion
                        + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");

    }

    public void modHaste(List<ModInfo> modList) throws Exception {
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modHaste || modInfo.newSkin.getSkinLevel() < 5);
        if (modList.size() == 0)
            return;
        update(" Dang mod hieu ung gia toc pack " + modPackName);
        String inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
        if (new File(saveModPath + modPackName
                +
                "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                .exists())
            inputZipPath = saveModPath + modPackName
                    +
                    "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";

        if (new File(cacheModPath2).exists()) {
            DHAExtension.deleteDir(cacheModPath2);
        }
        new File(cacheModPath2).mkdirs();
        ZipExtension.unzip(inputZipPath, cacheModPath2);
        String[] inputPaths = new String[] {
                cacheModPath2 + "commonresource/HasteE1.xml",
                cacheModPath2 + "commonresource/HasteE1_leave.xml"
        };
        for (int f = 0; f < inputPaths.length; f++) {
            String inputPath = inputPaths[f];
            String outputPath = inputPath;
            byte[] outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
            if (outputBytes == null)
                return;
            ProjectXML hasteXml = new ProjectXML(new String(outputBytes,
                    StandardCharsets.UTF_8));
            List<Integer> listCheckIndex = hasteXml.getTrackIndexByType("CheckSkinIdTick");
            Map<Integer, Integer> skinIdCheckMap1 = new HashMap<>();
            Map<Integer, Integer> skinIdCheckMapNotEqual1 = new HashMap<>();
            NodeList trackList = hasteXml.getNodeListByTagName("Track");
            for (int i = 0; i < listCheckIndex.size(); i++) {
                NodeList eventChild = trackList.item(listCheckIndex.get(i)).getChildNodes()
                        .item(trackList.item(listCheckIndex.get(i)).getChildNodes().getLength() - 2).getChildNodes();
                int id = 0;
                for (int j = 0; j < eventChild.getLength(); j++) {
                    NamedNodeMap attr = eventChild.item(j).getAttributes();
                    if (attr != null) {
                        if (attr.getNamedItem("name").getNodeValue().equals("bEqual")
                                && attr.getNamedItem("value").getNodeValue().equals("false")) {
                            skinIdCheckMapNotEqual1.put(listCheckIndex.get(i) + modList.size(), id);
                            id = -1;
                            break;
                        } else if (attr.getNamedItem("name").getNodeValue().equals("skinId")) {
                            if (id != -1)
                                id = Integer.parseInt(attr.getNamedItem("value").getNodeValue());
                        }
                    }
                }
                if (id != -1) {
                    // update(listCheckIndex.get(i) + ": " + id);
                    skinIdCheckMap1.put(listCheckIndex.get(i) + modList.size(), id);
                }
                listCheckIndex.set(i, listCheckIndex.get(i) + modList.size());
            }

            List<Node> baseTrack = new ArrayList<>(hasteXml.getTrackNodeByName("TriggerParticle0", false));

            NodeList conditions = hasteXml.getNodeListByTagName("Condition");
            for (int i = 0; i < conditions.getLength(); i++) {
                conditions.item(i).getAttributes().getNamedItem("id").setNodeValue("" +
                        (Integer.parseInt(conditions.item(i).getAttributes().getNamedItem("id").getNodeValue())
                                + modList.size()));
                if (listCheckIndex.contains(
                        Integer.parseInt(conditions.item(i).getAttributes().getNamedItem("id").getNodeValue()))) {
                    String parentName = conditions.item(i).getParentNode().getAttributes().getNamedItem("trackName")
                            .getNodeValue();
                    if (!parentName.equals("TriggerParticle0") && !parentName.toLowerCase().contains("check")) {
                        // update(ProjectXML.nodeToString(conditions.item(i).getParentNode()));
                        baseTrack.add(conditions.item(i).getParentNode());
                    }
                }
            }
            // NodeList conditionCheck = hasteXml.getNodeListByTagName("Condition");
            // for (int i = 0; i < conditionCheck.getLength(); i++){
            // if
            // (listCheckIndex.contains(Integer.parseInt(conditionCheck.item(i).getAttributes().getNamedItem("id").getNodeValue()))){
            // baseTrack.add(conditionCheck.item(i).getParentNode());
            // }
            // }
            // List<Node> tracks = new
            // ArrayList<>(hasteXml.getTrackNodeByType("PlayAnimationTick", true));
            // tracks.addAll(hasteXml.getTrackNodeByType("ChangeHDHeightDuration", true));
            // for (int i = 0; i < tracks.size(); i++) {
            // boolean kt = false;
            // for (int j = 0; j < tracks.get(i).getChildNodes().getLength(); j++) {
            // Node child = tracks.get(i).getChildNodes().item(j);
            // if (child.getNodeName().equals("Condition")
            // && listCheckIndex.contains(
            // Integer.parseInt(child.getAttributes().getNamedItem("id").getNodeValue()))) {
            // kt = true;
            // }
            // }
            // if (!kt) {
            // tracks.remove(i);
            // i--;
            // }
            // }
            // baseTrack.addAll(tracks);

            // int checkUseSkillTrackIndex =
            // hasteXml.getTrackIndexByType("CombinationConditionDuration").get(0);
            // // update(checkUseSkillTrackIndex+"");
            // Node baseStopTrack = null;
            // List<Node> stopTracks = hasteXml.getTrackNodeByType("StopTrack", false);
            // for (int i = 0; i < stopTracks.size(); i++) {
            // Node condition = CustomNode.getChild(stopTracks.get(i), "Condition");
            // if (condition != null
            // && condition.getAttributes().getNamedItem("id").getNodeValue()
            // .equals(checkUseSkillTrackIndex + "")) {
            // baseStopTrack = stopTracks.get(i);
            // // update(ProjectXML.nodeToString(stopTracks.get(i)));
            // break;
            // }
            // }

            List<Node> conditionList = new ArrayList<>();
            for (int l = 0; l < modList.size(); l++) {
                ModInfo modInfo = modList.get(l);
                // if (!modInfo.modSettings.modHaste || modInfo.newSkin.getSkinLevel() < 5) {
                // continue;
                // }
                update("    + Modding " + new File(inputPath).getName() + " " + (l + 1) + "/" + modList.size() + ": " +
                        modInfo.newSkin);

                int hero = Integer.parseInt(modInfo.newSkin.id.substring(0, 3));

                ZipInputStream zis = new ZipInputStream(
                        new FileInputStream(InfosParentPath + "Actor_" + hero +
                                "_Infos.pkg.bytes"));
                String heroCodeName = zis.getNextEntry().getName().split("/")[1];
                zis.close();

                String skinId = modInfo.newSkin.id.substring(3, modInfo.newSkin.id.length());
                int skin = Integer.parseInt(skinId) - 1;
                int idMod = hero * 100 + skin;
                hasteXml.insertActionChild(l + 1, ProjectXML.getCheckHeroTickNode(l, hero));
                int conditionIndex = l + 1;// hasteXml.getNodeListByTagName("Track").getLength() - 1;
                conditionList.add(ProjectXML.getConditionNode(conditionIndex,
                        "Mod_by_" + ChannelName + "_" + hero, false));

                ZipInputStream zipin = new ZipInputStream(
                        new FileInputStream(ActionsParentPath + "Actor_" + hero +
                                "_Actions.pkg.bytes"));
                ZipEntry entry;
                NodeList nodeList = null;
                while ((entry = zipin.getNextEntry()) != null) {
                    if (entry.getName().endsWith("/" + idMod + "_Haste.xml")) {
                        byte[] bytes = AOVAnalyzer.AOVDecompress(zipin.readAllBytes());
                        ProjectXML skinBackXml = new ProjectXML(new String(bytes));

                        skinBackXml.setValue("String", new String[] { "resourceName", "resourceName2", "prefabName" },
                                (StringOperator) (value) -> {
                                    if (!value.toLowerCase().contains("prefab_skill_effect"))
                                        return value;
                                    String[] split = value.split("/");
                                    String newValue;
                                    if (!modInfo.newSkin.isAwakeSkin) {
                                        newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod + "/"
                                                + split[split.length - 1];
                                    } else {
                                        newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod
                                                + "_5/"
                                                + split[split.length - 1];
                                    }
                                    return newValue;
                                });
                        skinBackXml.setValue("String", "eventName", "PlayHeroSoundTick", (value) -> {
                            if (!modInfo.newSkin.isAwakeSkin) {
                                return value + "_Skin" + skin;
                            } else {
                                if (value.contains("_VO") || value.toLowerCase().contains("voice")) {
                                    return value + "_Skin" + skin + "_AW" + modInfo.newSkin.levelVOXUnlock;
                                } else {
                                    return value + "_Skin" + skin + "_AW" + modInfo.newSkin.levelSFXUnlock;
                                }
                            }
                        });
                        nodeList = skinBackXml.getNodeListByTagName("Track");
                        break;
                    }
                }
                zipin.close();

                List<Node> baseHasteTrack = new ArrayList<>();
                baseHasteTrack.addAll(baseTrack);
                if (nodeList != null) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        if (!nodeList.item(i).getAttributes().getNamedItem("trackName").getNodeValue().toLowerCase()
                                .contains("check")) {
                            for (int j = 0; j < nodeList.item(i).getChildNodes().getLength(); j++) {
                                Node node = nodeList.item(i).getChildNodes().item(j);
                                if (node.getNodeName().equals("Condition"))
                                    nodeList.item(i).removeChild(node);
                            }
                            baseHasteTrack.add(nodeList.item(i));
                        }
                    }
                }
                for (int i = 0; i < baseHasteTrack.size(); i++) {
                    Node track = baseHasteTrack.get(i).cloneNode(true);
                    boolean kt = true;
                    for (int j = 0; j < track.getChildNodes().getLength(); j++) {
                        Node child = track.getChildNodes().item(j);
                        if (child.getNodeName().equals("Condition")) {
                            if (child.getAttributes().getNamedItem("guid").getNodeValue().toLowerCase()
                                    .contains("mod_by")) {
                                continue;
                            }
                            // update(child.getAttributes().getNamedItem("id").getNodeValue() + ":
                            // "+skinIdCheckMap1.get(Integer.parseInt(child.getAttributes().getNamedItem("id").getNodeValue())));
                            if (listCheckIndex
                                    .contains(Integer.parseInt(child.getAttributes().getNamedItem("id").getNodeValue()))
                                    && child.getAttributes().getNamedItem("status").getNodeValue().equals("true")
                                    && ((skinIdCheckMapNotEqual1
                                            .containsKey(Integer
                                                    .parseInt(child.getAttributes().getNamedItem("id").getNodeValue()))
                                            && skinIdCheckMapNotEqual1
                                                    .get(Integer.parseInt(
                                                            child.getAttributes().getNamedItem("id").getNodeValue()))
                                                    .equals(idMod))
                                            || (skinIdCheckMap1
                                                    .containsKey(Integer.parseInt(
                                                            child.getAttributes().getNamedItem("id").getNodeValue()))
                                                    && !skinIdCheckMap1
                                                            .get(Integer.parseInt(child.getAttributes()
                                                                    .getNamedItem("id").getNodeValue()))
                                                            .equals(idMod)))) {
                                kt = false;
                                // update(child.getAttributes().getNamedItem("id").getNodeValue() + ": " +
                                // skinIdCheckMap1
                                // .containsKey(Integer.parseInt(child.getAttributes().getNamedItem("id").getNodeValue()))
                                // + "(" + skinIdCheckMap1
                                // .get(Integer.parseInt(child.getAttributes().getNamedItem("id").getNodeValue()))
                                // + ")"
                                // + " - " + skinIdCheckMapNotEqual1
                                // .containsKey(Integer.parseInt(child.getAttributes().getNamedItem("id").getNodeValue()))
                                // + "(" + skinIdCheckMapNotEqual1
                                // .get(Integer.parseInt(child.getAttributes().getNamedItem("id").getNodeValue()))
                                // + ")");
                            }
                            track.removeChild(child);
                            j--;
                        }
                    }
                    if (!kt) {
                        continue;
                    }
                    Node condition = ProjectXML.getConditionNode(conditionIndex, "Mod_by_" + ChannelName + "_" + hero);

                    condition = track.getOwnerDocument().importNode(condition, true);
                    track.insertBefore(condition, track.getFirstChild());
                    for (int j = 0; j < track.getChildNodes().item(track.getChildNodes().getLength() -
                            2).getChildNodes().getLength(); j++) {
                        Node node = track.getChildNodes().item(track.getChildNodes().getLength() -
                                2).getChildNodes()
                                .item(j);
                        if (node.getAttributes() != null) {
                            if (node.getAttributes().getNamedItem("name").getNodeValue().equals("resourceName")) {
                                String value = node.getAttributes().getNamedItem("value").getNodeValue();
                                String[] split = value.split("/");
                                String newValue;
                                String endEffect = split[split.length - 1];
                                if (modInfo.newSkin.hasteName != null) {
                                    endEffect = modInfo.newSkin.hasteName;
                                }
                                if (!modInfo.newSkin.isAwakeSkin) {
                                    newValue = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" +
                                            idMod + "/" + endEffect;
                                } else {
                                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod +
                                            "_5/" + endEffect;
                                }
                                node.getAttributes().getNamedItem("value").setNodeValue(newValue);
                                j++;
                                if (modInfo.newSkin.hasteName == null && track.getChildNodes()
                                        .item(track.getChildNodes().getLength() - 2).getChildNodes()
                                        .item(j + 1) != null
                                        && modInfo.newSkin.hasteName == null && track.getChildNodes()
                                                .item(track.getChildNodes().getLength() - 2).getChildNodes()
                                                .item(j + 1).getAttributes().getNamedItem("name").getNodeValue()
                                                .equals("bindPosOffset")) {
                                    j += 2;
                                }
                                while (j < track.getChildNodes().item(track.getChildNodes().getLength() -
                                        2).getChildNodes()
                                        .getLength()) {
                                    track.getChildNodes().item(track.getChildNodes().getLength() - 2)
                                            .removeChild(track.getChildNodes().item(track.getChildNodes().getLength() -
                                                    2)
                                                    .getChildNodes().item(j));
                                }
                            }
                        }
                    }
                    hasteXml.appendActionChild(track);

                    Node condition2 = ProjectXML.getConditionNode(conditionIndex, "Mod_by_" + ChannelName +
                            "_" + hero, false);
                    if (i != 0 && i < baseTrack.size()) {
                        condition2 = baseHasteTrack.get(i).getOwnerDocument().importNode(condition2, true);
                        baseHasteTrack.get(i).insertBefore(condition2, baseHasteTrack.get(i).getFirstChild());
                    }
                    // Node stopTrack = baseStopTrack.cloneNode(true);
                    // Node event = CustomNode.getChild(stopTrack, "Event");
                    // Node trackObj = CustomNode.getChild(event, "TrackObject");
                    // trackObj.getAttributes().getNamedItem("guid")
                    // .setNodeValue(track.getAttributes().getNamedItem("guid").getNodeValue());
                    // trackObj.getAttributes().getNamedItem("id")
                    // .setNodeValue((hasteXml.getNodeListByTagName("Track").getLength() - 1) + "");
                    // hasteXml.appendActionChild(stopTrack);
                }
                hasteXml = specialModHaste(hasteXml, new File(inputPath).getName(), idMod);
            }
            // for (Node condition : conditionList) {
            // condition = baseStopTrack.getOwnerDocument().importNode(condition, true);
            // baseStopTrack.insertBefore(condition, baseStopTrack.getFirstChild());
            // }
            for (Node condition : conditionList) {
                condition = baseTrack.get(0).getOwnerDocument().importNode(condition, true);
                baseTrack.get(0).insertBefore(condition, baseTrack.get(0).getFirstChild());
            }
            // for (int i = 0; i < baseTrack.size(); i++) {
            // Node trackC = baseTrack.get(i);// .cloneNode(true);
            // for (Node condition : conditionList) {
            // condition = trackC.getOwnerDocument().importNode(condition, true);
            // trackC.insertBefore(condition, trackC.getFirstChild());
            // }
            // hasteXml.appendActionChild(trackC);
            // Node track = baseTrack.get(i);
            // for (int j = 0; j < track.getChildNodes().getLength(); j++) {
            // if (track.getChildNodes().item(j).getNodeName().equals("Event")) {
            // Node event = track.getChildNodes().item(j);
            // while (event.getChildNodes().getLength() != 0) {
            // event.removeChild(event.getChildNodes().item(0));
            // }
            // } else {
            // track.removeChild(track.getChildNodes().item(j));
            // j--;
            // }
            // }
            // }

            hasteXml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + " ");
            // DHAExtension.WriteAllBytes("D:/" + new File(outputPath).getName(),
            // hasteXml.getXmlString().getBytes());
            DHAExtension.WriteAllBytes(outputPath,
                    AOVAnalyzer.AOVCompress(hasteXml.getXmlString().getBytes()));
        }

        String[] subDir = new File(cacheModPath2).list();
        for (int i = 0; i < subDir.length; i++) {
            subDir[i] = cacheModPath2 + subDir[i];
        }
        ZipExtension.zipDir(subDir,
                saveModPath + modPackName +
                        "/files/Resources/" + AOVversion
                        + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");

    }

    public ProjectXML specialModAction(ProjectXML xml, String filePath, int idMod) throws Exception {
        String parentPath = new File(filePath).getParent();
        String fileName = new File(filePath).getName();
        Node newTrack = null;
        switch (idMod) {
            case 13011:
                switch (fileName) {
                    case "S2B1.xml":
                    case "S21.xml":
                    case "S22.xml":
                        String targetPath = parentPath + "/";
                        int trackIndex = 0;
                        if (fileName.equals("S2B1.xml")) {
                            targetPath += "S2B1_13011.xml";
                            trackIndex = 0;
                        } else if (fileName.equals("S21.xml")) {
                            targetPath += "S2B2_13011.xml";
                        } else if (fileName.equals("S22.xml")) {
                            targetPath += "S2B3_13011.xml";
                        }

                        xml.setValue("String", new String[] { "resourceName", "resourceName2", "prefabName", "prefab" },
                                (StringOperator) (value) -> {
                                    if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/"))
                                        return value;
                                    String[] split = value.split("/");
                                    String newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod
                                            + "/"
                                            + split[split.length - 1];
                                    return newValue;
                                });
                        xml.setValue("bool", "bAllowEmptyEffect", "false");
                        // List<Node> playSoundTick = xml.getTrackNodeByType("PlayHeroSoundTick", true);
                        xml.setValue("String", "eventName", "PlayHeroSoundTick", (value) -> {
                            return value + "_Skin" + idMod % 100;
                        });
                        List<Node> tracks = xml.getTrackNodeByName("TriggerParticleTick1", false);
                        if (tracks.size() != 0)
                            tracks.get(0).getAttributes().getNamedItem("enabled").setNodeValue("false");
                        tracks = xml.getTrackNodeByName("PlayAnimDuration0", false);
                        if (tracks.size() != 0)
                            tracks.get(0).getAttributes().getNamedItem("enabled").setNodeValue("false");

                        List<Node> trackCheck = xml.getTrackNodeByType("CheckSkinIdTick", false);
                        for (int i = 0; i < trackCheck.size(); i++) {
                            String trackName = trackCheck.get(i).getAttributes().getNamedItem("trackName")
                                    .getNodeValue();
                            if (trackName.contains("T2skin") && trackName.contains("true")) {
                                Node event = trackCheck.get(i).getChildNodes()
                                        .item(trackCheck.get(i).getChildNodes().getLength() - 2);
                                NodeList eventChild = event.getChildNodes();
                                for (int j = 0; j < eventChild.getLength(); j++) {
                                    Node child = eventChild.item(j);
                                    if (!child.getNodeName().equals("#text") &&
                                            child.getAttributes().getNamedItem("name").getNodeValue()
                                                    .equals("skinId")) {
                                        child.getAttributes().getNamedItem("value").setNodeValue((idMod / 100) + "98");
                                    }
                                }
                                Node bool = ProjectXML.convertStringToDocument(
                                        "<bool name=\"bEqual\" refParamName=\"\" useRefParam=\"false\" value=\"false\"/>")
                                        .getDocumentElement();
                                bool = event.getOwnerDocument().importNode(bool, true);
                                event.insertBefore(bool, eventChild.item(5));
                            }
                        }

                        byte[] bytes = DHAExtension.ReadAllBytes(targetPath);
                        byte[] decompress = AOVAnalyzer.AOVDecompress(bytes);
                        if (decompress != null) {
                            bytes = decompress;
                        }
                        ProjectXML xml2 = new ProjectXML(new String(bytes));
                        xml2.setValue("String", new String[] { "resourceName", "resourceName2", "prefabName" },
                                (StringOperator) (value) -> {
                                    if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/"))
                                        return value;
                                    String[] split = value.split("/");
                                    String newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod
                                            + "/"
                                            + split[split.length - 1];
                                    return newValue;
                                });
                        newTrack = xml2.getTrackNodeByName("TriggerParticle0").get(0);

                        for (int i = 0; i < newTrack.getChildNodes().getLength(); i++) {
                            if (newTrack.getChildNodes().item(i).getNodeName().equals("Condition")) {
                                newTrack.removeChild(newTrack.getChildNodes().item(i));
                                i--;
                            } else if (newTrack.getChildNodes().item(i).getNodeName().equals("Event")) {
                                NodeList children = newTrack.getChildNodes().item(i).getChildNodes();
                                for (int j = 0; j < children.getLength(); j++) {
                                    if (children.item(j).getNodeName().equals("TemplateObject")) {
                                        children.item(j).getAttributes().getNamedItem("id").setNodeValue("0");
                                        children.item(j).getAttributes().getNamedItem("objectName")
                                                .setNodeValue("self");
                                    }
                                }
                            }
                        }
                        List<Node> baseTrackList = xml.getTrackNodeByName("TriggerParticle0", false);
                        Node baseTrack = baseTrackList.get(trackIndex);
                        for (int i = 0; i < baseTrack.getChildNodes().getLength(); i++) {
                            Node trackChild = baseTrack.getChildNodes().item(i);
                            if (trackChild.getNodeName().equals("Event")) {
                                while (trackChild.getChildNodes().getLength() != 0) {
                                    trackChild.removeChild(trackChild.getChildNodes().item(0));
                                }
                            }
                        }
                        break;
                }
                break;
            case 54402:
                xml.setValue("String", "resourceName", (value) -> {
                    String newValue;
                    String[] split = value.split("/");
                    if (value.toLowerCase().endsWith("Painter_Atk4_blue".toLowerCase())
                            || value.endsWith("Painter_Atk4_red".toLowerCase())) {
                        newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/"
                                + split[split.length - 1];
                    } else {
                        newValue = value;
                    }
                    return newValue;
                });
                break;
            case 18004:
                xml.setValue("String", "resourceName", (value) -> {
                    String newValue;
                    String[] split = value.split("/");
                    if (value.toLowerCase().contains("_loop_")) {
                        newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/"
                                + split[split.length - 1];
                    } else {
                        newValue = value;
                    }
                    return newValue;
                });
                break;
        }
        if (newTrack != null) {
            xml.appendActionChild(newTrack);
            // update(fileName);
            // DHAExtension.WriteAllText("D:/test.xml", xml.getXmlString());
        }
        return xml;
    }

    public ProjectXML specialModHaste(ProjectXML xml, String fileName, int idMod) {
        switch (idMod) {

        }
        return xml;
    }

    public boolean tryParse(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void update(String content) {
        if (echo)
            System.out.println(content);
    }
}
