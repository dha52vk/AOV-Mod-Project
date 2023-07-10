package com.dha;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AOVModHelper {
    public String ChannelName = "AHMODAOV";
    public String YoutubeLink = "https://www.youtube.com/@AHMODAOV";

    public String InfosParentPath = "F:/This PC/Documents/AOV/Resources/1.50.1/Prefab_Characters/";
    public String ActionsParentPath = "F:/This PC/Documents/AOV/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/";
    public String DatabinPath = "F:/This PC/Documents/AOV/Resources/1.50.1/Databin/Client/";
    public String AssetRefsPath = "F:/This PC/Documents/AOV/Resources/1.50.1/AssetRefs/";
    public String LanguagePath = "F:/This PC/Documents/AOV/Resources/1.50.1/Languages/VN_Garena_VN/";
    public String SpecialPath = ".special/";
    public String saveModPath = "F:/This PC/Documents/AOV/";
    public String cacheModPath = "F:/This PC/Documents/AOV/cachemod/";

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
            modInfos(modList);
            modActions(modList);
            modSound(modList);
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
                System.out.println("has trap");
                inputBytes = DHAExtension.ReadAllBytes(trapInputPath);
                trapElement = new Element(AOVAnalyzer.AOVDecompress(inputBytes));
            }

            if (!element.containsChild("YTBLink")) {
                for (Element creditElement : creditElements)
                    element.addChild(1, creditElement);
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
                    String[] nameElementRemove = new String[] { "ArtSkinPrefabLOD", "ArtSkinPrefabLODEx",
                            "ArtSkinLobbyShowLOD" };
                    for (int i = 0; i < skin.getChildLength(); i++) {
                        Element e = skin.getChild(i).clone();
                        if (Arrays.asList(nameElementRemove).contains(e.nameS))
                            e.setName(e.nameS.replace("Skin", ""));
                        element.addChild(removeAt, e);
                    }
                } else {
                    Element newSkin = null;
                    for (int i = 0; i < element.getChild("SkinPrefab").getChildLength(); i++) {
                        Element skin = element.getChild("SkinPrefab").getChild(i);
                        String code = skin.getChild(0).getChild(0).valueS;
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
            DHAExtension.WriteAllBytes(inputPath, AOVAnalyzer.AOVCompress(element.getBytes()));
            if (trapElement != null) {
                DHAExtension.WriteAllBytes(trapInputPath, AOVAnalyzer.AOVCompress(trapElement.getBytes()));
            }
            ZipExtension.zipDir(cacheModPath + new File(cacheModPath).list()[0],
                    outputZipPath);
        }
        DHAExtension.WriteAllBytes(outputCharPath, AOVAnalyzer.AOVCompress(listCharComponent.getBytes()));
    }

    public void modActions(List<ModInfo> modList) throws Exception {
        update(" Dang mod hieu ung pack " + modPackName);
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modAction || modInfo.newSkin.label.skinLevel < 2) {
                continue;
            }
            update("    + Modding actions " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            String inputZipPath = ActionsParentPath + "Actor_" + heroId + "_Actions.pkg.bytes";

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

            List<Node> animTrackList = new ArrayList<>();
            for (String filename : new File(cacheModPath + filemodName).list()) {
                if (filename.toLowerCase().contains("back") || filename.toLowerCase().contains("born")
                        || (modInfo.newSkin.filenameNotMod != null
                                && Arrays.asList(modInfo.newSkin.filenameNotMod).contains(filename.toLowerCase()))
                        || (filename.toLowerCase().contains("death") && !modInfo.newSkin.hasDeathEffect)) {
                    // System.out.println("skiped " + filename);
                    continue;
                }
                String inputPath = cacheModPath + filemodName + filename;
                byte[] outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
                if (outputBytes == null)
                    continue;

                ProjectXML xml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));

                xml.setValue("String", "resourceName", (StringOperator) (value) -> {
                    String[] split = value.split("/");
                    if (tryParse(split[split.length - 1].split("_")[0])) {
                        if (split[split.length - 1].split("_")[0].length() == 5)
                            return value;
                    }
                    if (!modInfo.newSkin.isAwakeSkin) {
                        return String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod + "/"
                                + split[split.length - 1];
                    } else {
                        return "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5/"
                                + split[split.length - 1];
                    }
                });
                xml.setValue("bool", "bAllowEmptyEffect", "false");
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
                List<Integer> listIndex = xml.getTrackIndexByType("CheckSkinIdTick");
                NodeList particle = xml.getNodeListByTagName("Track");
                for (int j = 0; j < particle.getLength(); j++) {
                    NodeList children = particle.item(j).getChildNodes();
                    for (int i = 0; i < children.getLength(); i++) {
                        if (children.item(i).getNodeName().equals("Condition")) {
                            if (listIndex.contains(Integer
                                    .parseInt(children.item(i).getAttributes().getNamedItem("id").getNodeValue()))) {
                                particle.item(j).removeChild(children.item(i));
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

                // xml.replaceValue("String", "resourceName",
                // "(?i)prefab_skill_effects/hero_skill_effects/" + heroCodeName,
                // "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" + idMod);

                xml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + "  ");

                DHAExtension.WriteAllBytes(inputPath, AOVAnalyzer.AOVCompress(xml.getXmlString().getBytes()));
            }
            ZipExtension.zipDir(cacheModPath + filemodName.split("/")[0],
                    saveModPath + modPackName
                            + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/Actor_"
                            + heroId + "_Actions.pkg.bytes");
            if (modInfo.newSkin.changeAnim) {
                inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
                if (!App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                        .exists()) {
                    inputZipPath = "F:/This PC/Documents/AOV/" + App.modPackName
                            + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
                } else if (App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/pack_" + id
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                        .exists()) {
                    inputZipPath = "F:/This PC/Documents/AOV/pack_" + id
                            + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
                }

                if (new File(cacheModPath).exists()) {
                    DHAExtension.deleteDir(cacheModPath);
                }
                new File(cacheModPath).mkdirs();
                ZipExtension.unzip(inputZipPath, cacheModPath);
                filemodName = "commonresource/Dance.xml";
                String inputPath = cacheModPath + filemodName;
                String outputPath = inputPath;

                byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
                byte[] outputBytes = AOVAnalyzer.AOVDecompress(inputBytes);
                if (outputBytes == null)
                    return;

                ProjectXML danceXml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));

                for (Node node : animTrackList) {
                    danceXml.appendActionChild(node);
                }

                DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(danceXml.getXmlString().getBytes()));

                String[] subDir = new File(cacheModPath).list();
                for (int i = 0; i < subDir.length; i++) {
                    subDir[i] = cacheModPath + subDir[i];
                }
                ZipExtension.zipDir(subDir,
                        saveModPath + modPackName
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            }
        }
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
            if (!modInfo.modSettings.modSound || modInfo.newSkin.label.skinLevel < 3) {
                continue;
            }
            update("    + Modding sound " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);
            int heroId = Integer.parseInt(modInfo.newSkin.id.substring(0, 3));
            int targetId = heroId * 100 + Integer.parseInt(modInfo.newSkin.id.substring(3)) - 1;
            for (Skin skin : modInfo.targetSkins) {
                int baseId = heroId * 100 + Integer.parseInt(skin.id.substring(3)) - 1;
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
        }
        for (int i = 0; i < outputPaths.length; i++) {
            DHAExtension.WriteAllBytes(outputPaths[i], AOVAnalyzer.AOVCompress(soundListArr[i].getBytes()));
        }
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
