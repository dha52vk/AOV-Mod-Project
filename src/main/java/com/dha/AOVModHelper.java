package com.dha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

public class AOVModHelper {
    public static String heroListJsonPath = "D:/skinlist(label).json";
    public static String ChannelName = "AHMODAOV";
    public static String YoutubeLink = "https://www.youtube.com/@AHMODAOV";

    public String InfosParentPath = "F:/This PC/Documents/AOV/Resources/1.50.1/Prefab_Characters/";
    public String ActionsParentPath = "F:/This PC/Documents/AOV/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/";
    public String DatabinPath = "F:/This PC/Documents/AOV/Resources/1.50.1/Databin/Client/";
    public String AssetRefsPath = "F:/This PC/Documents/AOV/Resources/1.50.1/AssetRefs/";
    public String LanguagePath = "F:/This PC/Documents/AOV/Resources/1.50.1/Languages/VN_Garena_VN/";
    public String SpecialPath = ".special/";
    public static String saveModPath = "F:/This PC/Documents/AOV/";
    public static String cacheModPath = "F:/This PC/Documents/AOV/cachemod/";
    public static String cacheModPath2 = "F:/This PC/Documents/AOV/cachemod2/";

    // String[] nameElementModToDefault = new String[] {
    // "ArtSkinPrefabLOD", "ArtSkinPrefabLODEx", "ArtSkinLobbyShowLOD",
    // "ArtSkinLobbyIdleShowLOD", "ArtSkinLobbyShowCamera", "useNewMecanim",
    // "LobbyScale", "ArtSkinLobbyNode"
    // };
    List<String> skinNotSupportMod = Arrays.asList(new String[] { "13012" });
    List<String> skinHasOrgan = Arrays.asList(new String[] { "1118", "14112", "15010", "5019" });
    List<String> originSkinOrgan = Arrays.asList(new String[] { "1113", "1412", "1501", "5012" });
    List<String> trackTypeRemoveCheckSkinId = Arrays
            .asList(new String[] { "PlayAnimDuration", "TriggerParticle", "TriggerParticleTick",
                    "BattleUIAnimationDuration" });// , "HitTriggerTick" });

    public static List<String> idNotSwap = new ArrayList<>(Arrays.asList(new String[] {
            "19014", "11213", "13211", "50118", "16711", "19610", "13610", "11813", "5157", "5255",
            "1135", "1913", "5069", "5483", "1696", "1209", "5464", "16712"
    }));
    
    Map<Integer, Integer> skinSoundSpecial = new HashMap<Integer, Integer>(){{
        put(54303, 54302);
    }};

    String modPackName;
    boolean echo;

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
            if (new File(saveModPath, modPackName).exists()) {
                DHAExtension.deleteDir(saveModPath + modPackName);
            }
            modIcon(modList);
            modLabel(modList);
            modInfos(modList);
            modOrgan(modList);
            modActions(modList);
            modLiteBullet(modList);
            modSkillMark(modList);
            modSound(modList);
            modBack(modList);
            modHaste(modList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modInfos(List<ModInfo> modList) throws Exception {
        update(" Dang mod ngoai hinh cua pack " + modPackName + "...");

        String inputCharPath = saveModPath + modPackName
                + "/files/Resources/1.50.1/Databin/Client/Character/ResCharacterComponent.bytes";
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
        for (int l = 0; l < modList.size(); l++) {
            Element element = null, trapElement = null;
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modInfo)
                continue;

            String newId = modInfo.newSkin.id;

            String heroId = newId.substring(0, 3);
            update("    + Modding infos " + (l + 1) + "/" + modList.size() + ": " + modInfo);

            String inputZipPath = saveModPath + modPackName
                    + "/files/Resources/1.50.1/Prefab_Characters/Actor_" + heroId
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
                        element.removeChildAt(removeAt);
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
                        for (int s = 0; s < element.getChild("SkinPrefab").getChildLength(); s++) {
                            if (newIndex == s) {
                                continue;
                            }
                            Element skin2 = element.getChild("SkinPrefab").getChild(s);
                            String code = skin2.getChild(0).getChild(0).valueS;
                            if (code == null)
                                continue;
                            String[] split = code.split("/");
                            String id = split[split.length - 1].split("_")[0];
                            boolean kt = false;
                            for (int i = 0; i < modInfo.targetSkins.size(); i++) {
                                if (id.equals(modInfo.targetSkins.get(i).id)) {
                                    kt = true;
                                }
                            }
                            if (kt) {
                                continue;
                            }
                            if (!skin2.containsChild("useNewMecanim")) {
                                skin2.setChild(0, skin.getChild(0));
                                // update(s + ": " + id + " replace because don't use new mec anim");
                                element.getChild("SkinPrefab").setChild(s, skin2);
                            }
                        }
                    }
                    String[] nameElementRemove = new String[] { "ArtSkinPrefabLOD", "ArtSkinPrefabLODEx",
                            "ArtSkinLobbyShowLOD" };
                    for (int i = 0; i < skin.getChildLength(); i++) {
                        Element e = skin.getChild(i).clone();
                        // if (!Arrays.asList(nameElementModToDefault).contains(e.nameS))
                        // continue;
                        if (Arrays.asList(nameElementRemove).contains(e.nameS))
                            e.setName(e.nameS.replace("Skin", ""));
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
                            element.addChild(removeAt, e);
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
                + "/files/Resources/1.50.1/Databin/Client/Actor/organSkin.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;

        byte[] outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));

        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            String id = modInfo.newSkin.id;
            if (!modInfo.modSettings.modOrgan || !skinHasOrgan.contains(id)) {
                continue;
            }
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
                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                .exists())
            inputZipPath = saveModPath + modPackName
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";

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
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
                continue;
            }
            if (skinNotSupportMod.contains(modInfo.newSkin.id)) {
                update("   + Skin not support mod: " + modInfo.newSkin);
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
                        || (modInfo.newSkin.filenameNotMod != null
                                && Arrays.asList(modInfo.newSkin.filenameNotMod).contains(filename.toLowerCase()))
                        || (filename.toLowerCase().contains("death") && !modInfo.newSkin.hasDeathEffect)) {
                    // System.out.println("skiped " + filename);
                    continue;
                }
                inputPath = cacheModPath + filemodName + filename;
                outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
                if (outputBytes == null)
                    continue;

                ProjectXML xml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));

                xml.setValue("String", new String[] { "resourceName", "resourceName2", "prefabName" },
                        (StringOperator) (value) -> {
                            if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/"))
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
                List<Integer> listIndex = xml.getTrackIndexByType("CheckSkinIdTick");
                List<Integer> listIndexNot = new ArrayList<>();
                NodeList trackList = xml.getNodeListByTagName("Track");
                for (int j = 0; j < listIndex.size(); j++) {
                    NodeList eventChild = trackList.item(listIndex.get(j)).getChildNodes().item(1).getChildNodes();
                    for (int i = 0; i < eventChild.getLength(); i++) {
                        NamedNodeMap attr = eventChild.item(i).getAttributes();
                        if (attr != null && attr.getNamedItem("name").getNodeValue().equals("skinId")) {
                            if (!attr.getNamedItem("value").getNodeValue().equals(idMod + "")) {
                                listIndex.remove(j);
                                j--;
                            } else {
                                if (eventChild.item(i + 2) != null) {
                                    NamedNodeMap attr2 = eventChild.item(i + 2).getAttributes();
                                    if (attr2.getNamedItem("name").getNodeValue().equals("bEqual")) {
                                        if (attr2.getNamedItem("value").getNodeValue().equals("false")) {
                                            listIndexNot.add(listIndex.get(j));
                                            listIndex.remove(j);
                                            j--;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                NodeList particle = xml.getNodeListByTagName("Track");
                for (int j = 0; j < particle.getLength(); j++) {
                    if (!trackTypeRemoveCheckSkinId.contains(particle.item(j).getAttributes()
                            .getNamedItem("eventType").getNodeValue())) {
                        continue;
                    }
                    NodeList children = particle.item(j).getChildNodes();
                    for (int i = 0; i < children.getLength(); i++) {
                        if (children.item(i).getNodeName().equals("Condition")) {
                            if (listIndex.contains(Integer
                                    .parseInt(children.item(i).getAttributes().getNamedItem("id").getNodeValue()))) {
                                // update("removed condition "
                                // + particle.item(j).getAttributes().getNamedItem("trackName").getNodeValue());
                                particle.item(j).removeChild(children.item(i));
                            } else if (listIndexNot.contains(Integer
                                    .parseInt(children.item(i).getAttributes().getNamedItem("id").getNodeValue()))) {
                                update("disabled "
                                        + particle.item(j).getAttributes().getNamedItem("trackName").getNodeValue()
                                        + " (type: "
                                        + particle.item(j).getAttributes().getNamedItem("eventType").getNodeValue()
                                        + ")");
                                particle.item(j).getAttributes().getNamedItem("enabled").setNodeValue("false");
                                break;
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

                xml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + "  ");
                xml = specialModAction(xml, inputPath, idMod);

                DHAExtension.WriteAllBytes(inputPath, AOVAnalyzer.AOVCompress(xml.getXmlString().getBytes()));
            }

            ZipExtension.zipDir(cacheModPath + filemodName.split("/")[0],
                    saveModPath + modPackName
                            + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/Actor_"
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
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");

        update(" Fix khung...");
        modAssetRef(modList);
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
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
                continue;
            }

            String id = modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            String inputPath = AssetRefsPath + "Hero/" + heroId + "_AssetRef.bytes";
            String outputPath;
            outputPath = saveModPath + modPackName
                    + "/files/Resources/1.50.1/AssetRefs/Hero/" + heroId + "_AssetRef.bytes";
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
                + "/files/Resources/1.50.1/Databin/Client/Skill/liteBulletCfg.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;
        ListBulletElement listBullet = new ListBulletElement(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath)));
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
                continue;
            }
            update("    + Modding lite bullets " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
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
                + "/files/Resources/1.50.1/Databin/Client/Skill/skillmark.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;
        ListMarkElement listMark = new ListMarkElement(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath)));
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
                continue;
            }
            update("    + Modding skill marks " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
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
                    + "/files/Resources/1.50.1/Databin/Client/Sound/"
                    + new File(inputPaths[i]).getName();
            if (new File(outputPaths[i]).exists())
                inputPaths[i] = outputPaths[i];
            soundListArr[i] = new ListSoundElement(AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPaths[i])));
        }
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modSound || modInfo.newSkin.getSkinLevel() < 3) {
                continue;
            }
            update("    + Modding sound " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);
            int heroId = Integer.parseInt(modInfo.newSkin.id.substring(0, 3));
            int targetId = heroId * 100 + Integer.parseInt(modInfo.newSkin.id.substring(3)) - 1;
            if (skinSoundSpecial.containsKey(targetId)){
                targetId = skinSoundSpecial.get(targetId);
                update(targetId+"");
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
                + "/files/Resources/1.50.1/Databin/Client/Actor/heroSkin.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;

        ListIconElement listIconElement = new ListIconElement(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath)));
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modIcon)
                continue;
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
                + "/files/Resources/1.50.1/Databin/Client/Shop/HeroSkinShop.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;

        ListLabelElement listLabelElement = new ListLabelElement(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath)));
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modIcon)
                continue;
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
                    update("changed new label to " + targetId + "(" + modInfo.newSkin.label + ")");
                }
            }
        }
        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(listLabelElement.getBytes()));
    }

    public void modBack(List<ModInfo> modList) throws Exception {
        update(" Dang mod hieu ung bien ve pack " + modPackName);
        String inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
        if (new File(saveModPath + modPackName
                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                .exists())
            inputZipPath = saveModPath + modPackName
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";

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
        baseTrack.addAll(backXml.getTrackNodeByType("TriggerParticle", false));
        baseTrack.addAll(backXml.getTrackNodeByType("TriggerParticleTick", false));

        List<Node> baseAnimTrack = backXml.getTrackNodeByType("PlayAnimDuration");
        Node baseHeroSoundTick = backXml.getTrackNodeByType("PlayHeroSoundTick").get(0);

        List<Node> conditionList = new ArrayList<>();
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modBack || modInfo.newSkin.getSkinLevel() < 4) {
                continue;
            }
            update("    + Modding back " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            int hero = Integer.parseInt(modInfo.newSkin.id.substring(0, 3));

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + hero + "_Infos.pkg.bytes"));
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

                    skinBackXml.setValue("String", new String[] { "resourceName", "resourceName2", "prefabName" },
                            (StringOperator) (value) -> {
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
                    if (!nodeList.item(i).getAttributes().getNamedItem("trackName").getNodeValue().toLowerCase()
                            .contains("check")) {
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
                            node.getAttributes().getNamedItem("useRefParam").setNodeValue("false");
                            node.getAttributes().getNamedItem("refParamName").setNodeValue("");
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
        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(backXml.getXmlString().getBytes()));

        String[] subDir = new File(cacheModPath2).list();
        for (int i = 0; i < subDir.length; i++) {
            subDir[i] = cacheModPath2 + subDir[i];
        }
        ZipExtension.zipDir(subDir,
                saveModPath + modPackName
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");

    }

    public void modHaste(List<ModInfo> modList) throws Exception {
        update(" Dang mod hieu ung gia toc pack " + modPackName);
        String inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
        if (new File(saveModPath + modPackName
                +
                "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                .exists())
            inputZipPath = saveModPath + modPackName
                    +
                    "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";

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
                            listCheckIndex.remove(i);
                            i--;
                            id = -1;
                            break;
                        } else if (attr.getNamedItem("name").getNodeValue().equals("skinId")) {
                            if (id != -1)
                                id = Integer.parseInt(attr.getNamedItem("value").getNodeValue());
                        }
                    }
                }
                if (id != -1) {
                    // update(listCheckIndex1.get(i) + ": " + id);
                    skinIdCheckMap1.put(listCheckIndex.get(i), id);
                }
            }

            List<Node> baseTrack = new ArrayList<>(hasteXml.getTrackNodeByName("TriggerParticle0", false));

            List<Node> conditionList = new ArrayList<>();
            for (int l = 0; l < modList.size(); l++) {
                ModInfo modInfo = modList.get(l);
                if (!modInfo.modSettings.modHaste || modInfo.newSkin.getSkinLevel() < 5) {
                    continue;
                }
                update("    + Modding " + new File(inputPath).getName() + " " + (l + 1) + "/" + modList.size() + ": " +
                        modInfo.newSkin);

                int hero = Integer.parseInt(modInfo.newSkin.id.substring(0, 3));

                ZipInputStream zis = new ZipInputStream(
                        new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + hero +
                                "_Infos.pkg.bytes"));
                String heroCodeName = zis.getNextEntry().getName().split("/")[1];
                zis.close();

                String skinId = modInfo.newSkin.id.substring(3, modInfo.newSkin.id.length());
                int skin = Integer.parseInt(skinId) - 1;
                int idMod = hero * 100 + skin;
                hasteXml.appendActionChild(ProjectXML.getCheckHeroTickNode(l, hero));
                int conditionIndex = hasteXml.getNodeListByTagName("Track").getLength() - 1;
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
                            // update(child.getAttributes().getNamedItem("id").getNodeValue() + ":
                            // "+skinIdCheckMap1.get(Integer.parseInt(child.getAttributes().getNamedItem("id").getNodeValue())));
                            if (listCheckIndex
                                    .contains(Integer.parseInt(child.getAttributes().getNamedItem("id").getNodeValue()))
                                    && child.getAttributes().getNamedItem("status").getNodeValue().equals("true")
                                    && !skinIdCheckMap1
                                            .get(Integer
                                                    .parseInt(child.getAttributes().getNamedItem("id").getNodeValue()))
                                            .equals(idMod)) {
                                kt = false;
                            }
                            track.removeChild(child);
                            j--;
                        }
                    }
                    if (!kt) {
                        continue;
                    }
                    Node condition = ProjectXML.getConditionNode(conditionIndex, "Mod_by_" + ChannelName +
                            "_" + hero);
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
                }
                hasteXml = specialModHaste(hasteXml, new File(inputPath).getName(), idMod);
            }
            for (int i = 0; i < baseTrack.size(); i++) {
                Node track = baseTrack.get(i);
                for (Node condition : conditionList) {
                    condition = track.getOwnerDocument().importNode(condition, true);
                    track.insertBefore(condition, track.getFirstChild());
                }
                hasteXml.appendActionChild(track);
                for (int j = 0; j < track.getChildNodes().getLength(); j++) {
                    if (track.getChildNodes().item(j).getNodeName().equals("Event")) {
                        Node event = track.getChildNodes().item(j);
                        while (event.getChildNodes().getLength() != 0) {
                            event.removeChild(event.getChildNodes().item(0));
                        }
                    } else {
                        track.removeChild(track.getChildNodes().item(j));
                    }
                }
            }

            hasteXml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + " ");
            DHAExtension.WriteAllBytes(outputPath,
                    AOVAnalyzer.AOVCompress(hasteXml.getXmlString().getBytes()));
        }

        String[] subDir = new File(cacheModPath2).list();
        for (int i = 0; i < subDir.length; i++) {
            subDir[i] = cacheModPath2 + subDir[i];
        }
        ZipExtension.zipDir(subDir,
                saveModPath + modPackName +
                        "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");

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
                        if (fileName.equals("S2B1.xml")) {
                            targetPath += "S2B1_13011.xml";
                        } else if (fileName.equals("S21.xml")) {
                            targetPath += "S2B2_13011.xml";
                        } else if (fileName.equals("S22.xml")) {
                            targetPath += "S2B3_13011.xml";
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
                                    if (tryParse(split[split.length - 1].split("_")[0])) {
                                        if (split[split.length - 1].split("_")[0].length() == 5) {
                                            return value;
                                        }
                                    }
                                    String newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod
                                            + "/"
                                            + split[split.length - 1];
                                    return newValue;
                                });
                        newTrack = xml2.getTrackNodeByName("TriggerParticle0").get(0);
                        List<Node> baseTrackList = xml.getTrackNodeByName("TriggerParticle0", false);
                        for (int i = 0; i < baseTrackList.size(); i++) {
                            Node baseTrack = baseTrackList.get(i);
                            while (baseTrack.getChildNodes().getLength() != 0) {
                                baseTrack.removeChild(baseTrack.getChildNodes().item(0));
                            }
                        }
                        // DHAExtension.WriteAllText("D:/test2.xml", ProjectXML.nodeToString(newTrack));
                        break;
                    // case "S2B1_13011.xml":
                    // case "S2B2_13011.xml":
                    // case "S2B3_13011.xml":
                    // NodeList trackList = xml.getNodeListByTagName("Track");
                    // Node trackDisable=trackList.item(0);
                    // String conditionGUID =
                    // trackList.item(1).getAttributes().getNamedItem("guid").getNodeValue();
                    // if (trackDisable != null){
                    // trackDisable.getParentNode().removeChild(trackDisable);
                    // }
                    // Node condition = ProjectXML.getConditionNode(0, conditionGUID);
                    // condition = trackDisable.getOwnerDocument().importNode(condition, true);
                    // trackDisable.insertBefore(condition, trackDisable.getChildNodes().item(1));
                    // xml.appendActionChild(trackDisable);
                    // DHAExtension.WriteAllText("D:/test.xml", xml.getXmlString());
                    // break;
                }
                break;
        }
        if (newTrack != null) {
            for (int i = 0; i < newTrack.getChildNodes().getLength(); i++) {
                if (newTrack.getChildNodes().item(i).getNodeName().equals("Condition")) {
                    newTrack.removeChild(newTrack.getChildNodes().item(i));
                    i--;
                } else if (newTrack.getChildNodes().item(i).getNodeName().equals("Event")) {
                    NodeList children = newTrack.getChildNodes().item(i).getChildNodes();
                    for (int j = 0; j < children.getLength(); j++) {
                        if (children.item(j).getNodeName().equals("TemplateObject")) {
                            children.item(j).getAttributes().getNamedItem("id").setNodeValue("0");
                            children.item(j).getAttributes().getNamedItem("objectName").setNodeValue("self");
                        }
                    }
                }
            }
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
