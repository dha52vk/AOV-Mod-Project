package com.dha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

public class AOVModHelper {
    public static String zipNameFormat = "Mod Skin %s 17.8 (Izumi Tv).zip";
    public static String seasonName = "S3Y23";
    public static String heroListJsonPath = "D:/skinlist(new).json";
    public static String ChannelName = "IzumiTv";
    public static String YoutubeLink = "https://www.youtube.com/@MiyamuraModAOV";

    public static String AOVversion = "1.51.1";
    public static String ExtraVersion = "2019.V2";
    public static String ExtraPath = "F:\\This PC\\Documents\\AOV\\Extra\\" + ExtraVersion + "\\";
    public static String SkinBattlePath = "F:\\This PC\\Documents\\AOV\\Extra\\" + ExtraVersion
            + "\\assetbundle\\battle\\skin\\";
    public static String ResourcesPath = "F:/This PC/Documents/AOV/Resources/" + AOVversion + "/";
    public static String InfosParentPath = ResourcesPath + "Prefab_Characters/";
    public static String ActionsParentPath = ResourcesPath + "Ages/Prefab_Characters/Prefab_Hero/";
    public static String DatabinPath = ResourcesPath + "Databin/Client/";
    public static String AssetRefsPath = ResourcesPath + "AssetRefs/";
    public static String LanguageCode = "VN_Garena_VN"; // "CHT_Garena_TW";
    public static String LanguagePath = ResourcesPath + "Languages/" + LanguageCode + "/";
    public static String SpecialPath = ".special/";
    public static String saveModPath = "F:/This PC/Documents/AOV/modsave/";
    public static String cacheModPath = "F:/This PC/Documents/AOV/cachemod/";
    public static String cacheModPath2 = "F:/This PC/Documents/AOV/cachemod2/";

    // String[] nameElementModToDefault = new String[] {
    // "ArtSkinPrefabLOD", "ArtSkinPrefabLODEx", "ArtSkinLobbyShowLOD",
    // "ArtSkinLobbyIdleShowLOD", "ArtSkinLobbyShowCamera", "useNewMecanim",
    // "LobbyScale", "ArtSkinLobbyNode"
    // };
    List<String> skinHasOrgan = Arrays.asList(new String[] { "1118", "14112", "15010", "5019" });
    List<String> originSkinOrgan = Arrays.asList(new String[] { "1113", "1412", "1501", "5012" });
    // List<String> trackTypeRemoveCheckSkinId = Arrays
    // .asList(new String[] { "TriggerParticle", "TriggerParticleTick",
    // "BattleUIAnimationDuration", "HitTriggerTick" });// "PlayAnimDuration" });
    Map<String, List<String>> skinIdAllowTrackCantRemoveMap = new HashMap<String, List<String>>() {
        {
            put("13211", Arrays.asList(new String[] { "" }));
        }
    };
    List<String> trackTypeNotRemoveCheckSkinId = Arrays
            .asList(new String[] { "CheckRandomRangeTick", "ChangeSkillTriggerTick", "CreateRandomNumTick"
            // , "HitTriggerTick", "HitTriggerDuration", "RemoveBuffTick"
            // , "SpawnBulletTick", "SpawnObjectDuration", "SetCollisionTick",
            // "MoveBulletDuration",
            // , "CheckSkillCombineConditionTick",
            });

    Map<String, String> trackChildValueNotRemoveCheckSkinId = new HashMap<String, String>() {
        {
            put("bOnlyFollowPos", "true");
        }
    };

    List<String> notBugChildName = Arrays.asList(new String[] {
            // type TriggerParticle
            "targetId", "resourceName", "scalingInt", "bAllowEmptyEffect", "syncAnimationName", "customTagName",
            "objectSpaceId", "lifeTime", "bindPosOffset", "bUseRealScaling", "applyActionSpeedToParticle",
            "bindPointName",
            "applyActionSpeedToAnimation", "bNoDynamicLod", "lookTargetId", "bHideWhenDead", "bIgnoreWhenHideModel",
            "bForceShowParticle", "bBullerPosDir", "enableMaxFollowTime", "maxFollowTime", "bindRotOffset",
            "bUseRootBoneScaling",
            "bReverseRotOffsetWhenCameraMirro", "bOnlySetAlpha", "resourceName2", "resourceName3", "scaling",
            "dontShowIfNoBindPoint",
            "bBulletDir", "bForceIngoreCull", "bTrailProtect", "bUseClearTrailProtect", "bReverseXWhenCameraMirror",
            "bReverseXWhenCameraMirro", "showInStatus",
            // type MoveBulletDuration
            "MoveType", "offsetDir", "distance", "velocity",
            // type MoveBeamDuration
            "sourceId", "bindDestOffet", "textureScale", "keyColor",
            // type SetActorNodeActiveDuration
            "node", "enabled",
            // type PlayAnimDuration
            "clipName", "crossFadeTime", "bUseFadeOutTime", "fadeOutTime", "applyActionSpeed",
            // type ChangeAnimDuration
            "originalAnimName", "changedAnimName", "bChangeBlendTime", "fadeInBlendTime", "fadeOutBlendTime",
            "runAnimName1",
            "runAnimName2", "runAnimName3", "runAnimName4", "runAnimName5",
            // type StopTrack
            "trackId", "trackIds", "alsoStopNotStartedTrack",
            // type SpawnBulletTick
            "ActionName", "bDeadRemove", "bulletTypeId", "bImmeExcute", "bAgeEternal",
            // type SpawnLiteObjDuration
            "OutputLiteBulletName", "ConfigID", "ReferenceID", "TargetID",
            // type SpawnObjectDuration
            "prefabName", "translation", "modifyDirection", "direction", "materialParentActorId", "modifyScaling",
            "visionActorId",
            "bUseSkin", "recordTargeID",
            // type SetCollisionTick
            "type", "Size", "Rotation",
            // type CheckSkillCombineConditionTick
            "skillCombineId", "checkOPType", "skillCombineLevel",
            // type HitTriggerTick
            "SelfSkillCombineID_1", "SelfSkillCombineID_2", "SelfSkillCombineID_3", "SelfSkillCombineID_3",
            "SelfSkillCombineID_4",
            "SelfSkillCombineID_5", "SelfSkillCombineID_6", "triggerId", "lastHit", "TargetSkillCombine_1",
            "TargetSkillCombine_3", "TargetSkillCombine_2", "TargetSkillCombine_4", "TargetSkillCombine_5",
            "TargetSkillCombine_6", "attackerId", "triggerInterval",
            "TriggerActorInterval", "bFileterMonter", "bFileterOrgan", "bulletID", "TriggerActorCount",
            "CollideMaxCount", "CollideMaxCount",
            "bTriggerBullet", "BulletActionName", "hitTargetId",
            // type RemoveBuffTick
            "buffId", "BuffLayer",
            // type PlayMaterialEffectDuration
            "effectType", "texPath", "highLitColor", "hightLitGloss", "higthLitPeriod", "materialNamePattern",
            // type MaterialSwitchDuration
            "bAddMaterial",
            // type BattleUIAnimationDuration
            "prefab", "animName",
            // type CameraShakeDuration
            "useMainCamera", "filter_self", "canBeCover", "shakeRange"
    });

    String[] nameElementRemoveSkinInName = new String[] { "ArtSkinPrefabLOD", "ArtSkinPrefabLODEx",
            "ArtSkinLobbyShowLOD" };

    List<String> nameElementNotAddToDefaultAll = Arrays.asList(new String[] {

    });

    Map<String, String[]> nameElementNotAddToDefault = new HashMap<String, String[]>() {
        {
            put("5443", new String[] { "TransConfigs" });
            // put("11612", new String[] { "bDisableDirLight", "LookAt", "LightConfig" });
        }
    };

    public static List<String> idNotSwap = new ArrayList<>(Arrays.asList(new String[] {
            "19014", "11213", "13211", "50118", "16711", "19610", "13610", "11813", "5157", "5255",
            "1135", "1913", "5069", "5483", "1696", "1209", "5464", "16712", "10618", "11617", "11812", "50114", "1669",
            "15213", "15013", "1959", "15712", "52111", "5359", "1056", "52011", "1375", "50117", "5137", "1499",
            "1697", "1925", "52610", "5407"// , "1239", "13212", "1739", "51811", "5236"
    }));

    Map<Integer, Integer> skinSoundSpecial = new HashMap<Integer, Integer>() {
        {
            put(54303, 54302);
        }
    };

    Map<Integer, List<String>> effectNotmod = new HashMap<Integer, List<String>>() {
        {
            put(54402, Arrays.asList(new String[] { "/Painter_Atk4_blue", "/Painter_Atk4_red" }));
            put(18004, Arrays.asList(new String[] { "_loop_" }));
        }
    };

    Map<Integer, List<String>> heroDanceCode = new HashMap<Integer, List<String>>() {
        {
            put(106, Arrays.asList(new String[] { "Dance01", "Dance02" }));
            put(108, Arrays.asList(new String[] { "Dance01", "Dance02" }));
            put(109, Arrays.asList(new String[] { "Dance01" }));
            put(116, Arrays
                    .asList(new String[] { "Play_Dance_Butterfly", "Play_Dance02_Butterfly", "Play_Dance_FangFang_116",
                            "Play_Dance_FangFang_116_Butterfly" }));
            put(131, Arrays.asList(new String[] { "Play_Dance_Murad", "Play_Dance03_Murad" }));
            put(133, Arrays
                    .asList(new String[] { "Play_Dance_Vanhelsing", "Play_Dance03_Valhein", "Play_Dance_MJ_133" }));
            put(150, Arrays.asList(new String[] { "Play_Dance_Nakroth", "Play_Dance02_3Jumps_Nakroth" }));
            put(152, Arrays.asList(new String[] { "Play_Dance_DiaoChan", "Play_Dance02_3Jumps_DiaoChan" }));
            put(154, Arrays
                    .asList(new String[] { "Play_Dance_Yena", "Play_Dance02_3Jumps_Yena", "Play_Dance_FangFang_154" }));
            put(163, Arrays.asList(
                    new String[] { "Play_Dance_Ryoma", "Play_Dance02_3Jumps_Ryoma", "Play_HeiRenTaiGuan_preview_02" }));
            put(166, Arrays.asList(new String[] { "Play_Dance_YaSe", "Play_Dance03_Arthur" }));
            put(167, Arrays.asList(new String[] { "Play_Dance_WuKong" }));
            put(503, Arrays.asList(new String[] { "Play_Dance_Zuka", "Play_Dance_Zuka_503_dance03" }));
            put(510, Arrays.asList(new String[] { "Play_Dance_Liliana", "Play_Dance03_Liliana" }));
            put(535, Arrays.asList(new String[] { "Play_Dance_Sinestrea", "Play_Dance_FangFang_535" }));
        }
    };

    Map<String, String> skinDanceCode = new HashMap<String, String>() {
        {
            put("1068", "Play_10607_dance04");
            put("11612", "Play_Dance_Butterfly_SAO_LiSiNa_Knife");
            put("1319", "Play_Dance_Murad_Skin8");
            put("13110", "Dance04_Mojian");
            put("13111", "Dance04_Mojian");
            put("13311", "dance_10_texiao");
            put("13312", "Dance08_Gushiji");
            put("15010", "dance_05_texiao");
            put("1525", "Play_15204_Dance_WAVE");
            put("15410", "Play_15409_Dance_WAVE");
            put("1638", "Play_16307_dance04");
            put("1668", "Stop_16607_m_Dance_JiXieWu");
            put("1676", "Play_Dance_WuKong_Skin5");
            put("1678", "dance_03_texiao");
            put("5039", "Dance04_Run");
            put("51010", "Play_51009_Dance_WAVE");
            put("5354", "Play_53503_Dance_WAVE");
        }
    };

    String modPackName;
    boolean echo;
    boolean highlightMod = false;
    boolean copyBattleFile = false;
    boolean zipModFile = true;
    boolean modSingleInPack = false;

    public AOVModHelper() {
        echo = true;
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
            update("\nDang mod pack " + modPackName + "...");
            DHAExtension.deleteDir(saveModPath + modPackName);
            DHAExtension.deleteDir(saveModPath + modPackName + " (may yeu)");

            Collator collator = Collator.getInstance(Locale.of("vi"));
            modList.sort((info1, info2) -> collator.compare(info1.newSkin.name, info2.newSkin.name));

            modIcon(modList);
            modLabel(modList);
            modInfos(modList);
            modOrgan(modList);
            modActions(modList);
            modLiteBullet(modList);
            modSkillMark(modList);
            modSound(modList);
            modMotion(modList);

            // copy extra battle skin
            if (copyBattleFile && modList.size() < 6) {
                update(" Copying battle files...");
                String battlePath = saveModPath + modPackName +
                        "/files/Extra/" + ExtraVersion + "/assetbundle/battle/skin/";
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
            List<ModInfo> modList2 = new ArrayList<>(modList);
            modList2.removeIf(modInfo -> !modInfo.modSettings.modBack || modInfo.newSkin.getSkinLevel() < 4);
            if (modList2.size() > 3) {
                update(" Creating low pack...");
                DHAExtension.copy(saveModPath + modPackName, saveModPath + modPackName + " (may yeu)");
                if (zipModFile) {
                    String zipPath = saveModPath + String.format(zipNameFormat,
                            DHAExtension.convertToTitleCase(
                                    modList.size() == 1 ? modList.get(0).newSkin.name : modPackName) + " Máy Yếu");
                    if (new File(zipPath).exists())
                        new File(zipPath).delete();
                    ZipExtension.zipDir(saveModPath + modPackName + " (may yeu)" + "/files",
                            zipPath,
                            Deflater.BEST_COMPRESSION);
                }
            }
            modBack(modList);
            modHaste(modList);
            if (highlightMod) {
                update("Creating highlight pack...");
                DHAExtension.copy(saveModPath + modPackName, saveModPath + modPackName + " (highlight)");
                String actionsPath = saveModPath + modPackName + " (highlight)"
                        + "/files/Resources/" + AOVversion +
                        "/Ages/Prefab_Characters/Prefab_Hero/Actor_"
                        + modList.get(0).newSkin.id.substring(0, 3) + "_Actions.pkg.bytes";
                highlightSkill(actionsPath, 4);
                if (zipModFile) {
                    String zipPath = saveModPath + String.format(zipNameFormat,
                            DHAExtension.convertToTitleCase(
                                    modList.size() == 1 ? modList.get(0).newSkin.name : modPackName)
                                    + " Skill Sáng Đậm");
                    if (new File(zipPath).exists())
                        new File(zipPath).delete();
                    ZipExtension.zipDir(saveModPath + modPackName + " (highlight)" + "/files",
                            zipPath,
                            Deflater.BEST_COMPRESSION);
                }
            }

            if (zipModFile) {
                String zipPath = saveModPath + String.format(zipNameFormat,
                        DHAExtension
                                .convertToTitleCase(modList.size() == 1 ? modList.get(0).newSkin.name : modPackName));
                if (new File(zipPath).exists())
                    new File(zipPath).delete();
                ZipExtension.zipDir(saveModPath + modPackName + "/files",
                        zipPath,
                        Deflater.DEFLATED);
            }

            String content = "";
            for (ModInfo info : modList) {
                content += "\n   + " + DHAExtension.convertToTitleCase(info.newSkin.name);
            }
            DHAExtension.WriteAllText(saveModPath + modPackName + "/packinfo.txt", content);

            if (modSingleInPack && modList.size() > 1) {
                for (ModInfo info : modList) {
                    setModPackName("pack_" + info.newSkin.id);
                    modSkin(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modMapHD(boolean saveBaseMap) {
        update(" Dang mod map hd...");
        String hdMapModFolderName = "mod_map_hd";
        DHAExtension.deleteDir(saveModPath + hdMapModFolderName);
        DHAExtension.deleteDir(saveModPath + hdMapModFolderName + "_reverse");
        String[] hdMapExtraPath = new String[] {
                "assetbundle\\scene\\scene_artist_5v5_v4_hd.assetbundle",
                "assetbundle\\scene\\scene_artist_5v5_v4_hd_raw.assetbundle",
                "assetbundle\\scene\\scene_artist_5v5_v4_raw.assetbundle"
        };
        String[] hdMapResourcesPath = new String[] {
                "assetbundle\\scene\\scene_artist_5v5_v4_low_raw.assetbundle"
        };
        String[] lowMapExtraPath = new String[] {
                "assetbundle\\scene\\scene_artist_5v5_v4.assetbundle"
        };
        String[] lowMapResourcesPath = new String[] {
                "assetbundle\\scene\\scene_artist_5v5_v4_low.assetbundle"
        };
        for (String subpath : hdMapExtraPath) {
            DHAExtension.copy(ExtraPath + subpath,
                    saveModPath + hdMapModFolderName + "\\files\\Extra\\" + ExtraVersion + "\\" + subpath);
        }
        for (String subpath : hdMapResourcesPath) {
            DHAExtension.copy(ResourcesPath + subpath,
                    saveModPath + hdMapModFolderName + "\\files\\Resources\\" + AOVversion + "\\" + subpath);
        }
        if (saveBaseMap) {
            update("  - Dang tao file khoi phuc map goc...");
            for (String subpath : lowMapExtraPath) {
                DHAExtension.copy(ExtraPath + subpath,
                        saveModPath + hdMapModFolderName + "\\files\\Extra\\" + ExtraVersion + "\\" + subpath);
            }
            for (String subpath : lowMapResourcesPath) {
                DHAExtension.copy(ResourcesPath + subpath,
                        saveModPath + hdMapModFolderName + "\\files\\Resources\\" + AOVversion + "\\" + subpath);
            }
            DHAExtension.copy(saveModPath + hdMapModFolderName, saveModPath + hdMapModFolderName + "_reverse");
            String saveZipPath = saveModPath + "Khôi Phục Map Gốc " + seasonName + " (" + ChannelName + ").zip";
            DHAExtension.deleteDir(saveZipPath);
            ZipExtension.zipDir(saveModPath + hdMapModFolderName + "_reverse/files", saveZipPath, Deflater.BEST_SPEED);
        }
        update("  - Dang tao file map hd...");
        for (String subpath : lowMapExtraPath) {
            DHAExtension.WriteAllText(
                    saveModPath + hdMapModFolderName + "\\files\\Extra\\" + ExtraVersion + "\\" + subpath,
                    "Mod Map HD By " + ChannelName + " - " + YoutubeLink);
        }
        for (String subpath : lowMapResourcesPath) {
            DHAExtension.WriteAllText(
                    saveModPath + hdMapModFolderName + "\\files\\Resources\\" + AOVversion + "\\" + subpath,
                    "Mod Map HD By " + ChannelName + " - " + YoutubeLink);
        }
        String saveZipPath = saveModPath + "Mod Map HD " + seasonName + " (" + ChannelName + ").zip";
        DHAExtension.deleteDir(saveZipPath);
        ZipExtension.zipDir(saveModPath + hdMapModFolderName + "/files", saveZipPath, Deflater.BEST_SPEED);
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

            String newId = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;

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
                    if (!new File(SpecialPath + "infos/" + newId + ".bytes").exists()) {
                        skin = element.getChild("SkinPrefab").getChild(newIndex);
                    } else {
                        skin = new Element(
                                DHAExtension.ReadAllBytes(SpecialPath + "infos/" + newId + ".bytes"));
                        element.getChild("SkinPrefab").setChild(newIndex, skin);
                    }
                    for (int i = 0; i < skin.getChildLength(); i++) {
                        Element e = skin.getChild(i).clone();
                        // if (!Arrays.asList(nameElementModToDefault).contains(e.nameS))
                        // continue;
                        if ((nameElementNotAddToDefault.containsKey(modInfo.newSkin.id)
                                && Arrays.asList(nameElementNotAddToDefault.get(modInfo.newSkin.id)).contains(e.nameS))
                                || nameElementNotAddToDefaultAll.contains(e.nameS))
                            continue;
                        if (Arrays.asList(nameElementRemoveSkinInName).contains(e.nameS)) {
                            e.setName(e.nameS.replace("Skin", ""));
                            if (modInfo.newSkin.isComponentSkin) {
                                int componentId = Integer.parseInt(modInfo.newSkin.id
                                        .substring(modInfo.newSkin.id.length() - 2, modInfo.newSkin.id.length()));
                                for (int j = 0; j < e.getChildLength(); j++) {
                                    Element child = e.getChild(j);
                                    String[] split1 = child.valueS.split("/");
                                    String[] split2 = split1[split1.length - 1].split("_");
                                    if (split1.length < 3 || split2.length < 3)
                                        continue;
                                    List<String> paths = new ArrayList<>(
                                            Arrays.asList(Arrays.copyOfRange(split1, 0, 3)));
                                    paths.add("Component");
                                    List<String> paths2 = new ArrayList<>(
                                            Arrays.asList(Arrays.copyOfRange(split2, 0, 2)));
                                    paths2.add("RT_" + componentId);
                                    paths2.add(split2[split2.length - 1]);
                                    paths.add(String.join("_", paths2));
                                    child.setValue(String.join("/", paths));
                                }
                            }
                        }
                        if (e.nameS.equals("useNewMecanim")) {
                            e.setName("oriSkinUseNewMecanim");
                        }
                        element.removeChild(e.nameS);
                        element.addChild(removeAt, e);
                    }
                } else {
                    Element newSkin = null;
                    for (int i = 0; i < element.getChild("SkinPrefab").getChildLength(); i++) {
                        Element skin = element.getChild("SkinPrefab").getChild(i);
                        String code = skin.getChild("ArtSkinPrefabLOD").getChild(0).valueS;
                        if (code == null)
                            continue;
                        String[] split = code.split("/");
                        String id = split[split.length - 1].split("_")[0];
                        // update(targetId + ": " + id + " - " + newId);
                        if (id.equals(newId)) {
                            newIndex = i;
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
                    if (!new File(SpecialPath + "infos/" + newId + ".bytes").exists()) {
                        newSkin = element.getChild("SkinPrefab").getChild(newIndex);
                    } else {
                        newSkin = new Element(
                                DHAExtension.ReadAllBytes(SpecialPath + "infos/" + newId + ".bytes"));
                    }
                    if (modInfo.newSkin.isComponentSkin) {
                        newSkin = newSkin.clone();
                        for (int i = 0; i < newSkin.getChildLength(); i++) {
                            Element e = newSkin.getChild(i);
                            if (Arrays.asList(nameElementRemoveSkinInName).contains(e.nameS)) {
                                e.setName(e.nameS.replace("Skin", ""));
                                int componentId = Integer.parseInt(modInfo.newSkin.id
                                        .substring(modInfo.newSkin.id.length() - 2, modInfo.newSkin.id.length()));
                                for (int j = 0; j < e.getChildLength(); j++) {
                                    Element child = e.getChild(j);
                                    String[] split1 = child.valueS.split("/");
                                    String[] split2 = split1[split1.length - 1].split("_");
                                    if (split1.length < 3 || split2.length < 3)
                                        continue;
                                    List<String> paths = new ArrayList<>(
                                            Arrays.asList(Arrays.copyOfRange(split1, 0, 3)));
                                    paths.add("Component");
                                    List<String> paths2 = new ArrayList<>(
                                            Arrays.asList(Arrays.copyOfRange(split2, 0, 2)));
                                    paths2.add("RT_" + componentId);
                                    paths2.add(split2[split2.length - 1]);
                                    paths.add(String.join("_", paths2));
                                    child.setValue(String.join("/", paths));
                                }
                            }
                        }
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
            String[] dirPaths = new File(cacheModPath).list();
            for (int i = 0; i < dirPaths.length; i++) {
                dirPaths[i] = cacheModPath + dirPaths[i];
            }
            ZipExtension.zipDir(dirPaths, outputZipPath);
        }
        DHAExtension.WriteAllBytes(outputCharPath, AOVAnalyzer.AOVCompress(listCharComponent.getBytes()));
    }

    public void modInfosCustom(List<ModInfo> modList) throws Exception {

        update(" Dang mod ngoai hinh custom cua pack " + modPackName + "...");

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
            Element element = null;
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modInfo)
            // continue;

            String newId = modInfo.newSkin.id;

            String heroId = modInfo.targetSkins.get(0).id.substring(0, 3);
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

            if (new File(cacheModPath2).exists()) {
                DHAExtension.deleteDir(cacheModPath2);
            }
            new File(cacheModPath2).mkdirs();
            String inputZipPath2 = saveModPath + modPackName
                    + "/files/Resources/" + AOVversion + "/Prefab_Characters/Actor_" + newId.substring(0, 3)
                    + "_Infos.pkg.bytes";
            if (!new File(inputZipPath2).exists()) {
                inputZipPath2 = InfosParentPath + "Actor_" + newId.substring(0, 3) + "_Infos.pkg.bytes";
            }
            ZipExtension.unzip(inputZipPath2, cacheModPath2);
            String inputPath2 = "";
            String heroCodeName2 = new File(cacheModPath2 + "Prefab_Hero/").list()[0];
            inputPath2 = cacheModPath2 + "Prefab_Hero/" + heroCodeName2 + "/" + heroCodeName2 + "_actorinfo.bytes";
            byte[] inputBytes2 = DHAExtension.ReadAllBytes(inputPath2);
            Element element2 = new Element(AOVAnalyzer.AOVDecompress(inputBytes2));

            if (!element.containsChild("YTBLink")) {
                for (Element creditElement : creditElements) {
                    element.addChild(1, creditElement);
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
                    for (int s = 0; s < element2.getChild("SkinPrefab").getChildLength(); s++) {
                        Element skin = element2.getChild("SkinPrefab").getChild(s);
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
                        skin = element2.getChild("SkinPrefab").getChild(newIndex);
                    } else {
                        skin = new Element(
                                DHAExtension.ReadAllBytes(SpecialPath + "infos/" + modInfo.newSkin.id + ".bytes"));
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
                    for (int i = 0; i < element2.getChild("SkinPrefab").getChildLength(); i++) {
                        Element skin = element2.getChild("SkinPrefab").getChild(i);
                        String code = skin.getChild("ArtSkinPrefabLOD").getChild(0).valueS;
                        if (code == null)
                            continue;
                        String[] split = code.split("/");
                        String id = split[split.length - 1].split("_")[0];
                        // update(targetId + ": " + id + " - " + newId);
                        if (id.equals(newId)) {
                            newIndex = i;
                            break;
                        }
                    }
                    for (int i = 0; i < element.getChild("SkinPrefab").getChildLength(); i++) {
                        Element skin = element.getChild("SkinPrefab").getChild(i);
                        String code = skin.getChild("ArtSkinPrefabLOD").getChild(0).valueS;
                        if (code == null)
                            continue;
                        String[] split = code.split("/");
                        String id = split[split.length - 1].split("_")[0];
                        if (id.equals(targetId)) {
                            targetIndex = i;
                            break;
                        }
                    }
                    if (targetIndex == -1)
                        continue;
                    if (!new File(SpecialPath + "infos/" + modInfo.newSkin.id + ".bytes").exists()) {
                        newSkin = element2.getChild("SkinPrefab").getChild(newIndex);
                    } else {
                        newSkin = new Element(
                                DHAExtension.ReadAllBytes(SpecialPath + "infos/" + modInfo.newSkin.id + ".bytes"));
                    }
                    element.getChild("SkinPrefab").setChild(targetIndex, newSkin);
                }
            }
            // DHAExtension.WriteAllBytes("D:/test.bytes", element.getBytes());
            DHAExtension.WriteAllBytes(inputPath, AOVAnalyzer.AOVCompress(element.getBytes()));
            String[] dirPaths = new File(cacheModPath).list();
            for (int i = 0; i < dirPaths.length; i++) {
                dirPaths[i] = cacheModPath + dirPaths[i];
            }
            ZipExtension.zipDir(dirPaths, outputZipPath);
        }
        DHAExtension.WriteAllBytes(outputCharPath, AOVAnalyzer.AOVCompress(listCharComponent.getBytes()));
    }

    public void modOrgan(List<ModInfo> modList) {
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modOrgan || !skinHasOrgan.contains(modInfo.newSkin.id));
        if (modList.size() == 0)
            return;
        update(" Dang mod hieu ung ve than pack " + modPackName);

        String inputPath = DatabinPath + "Actor/organSkin.bytes";
        String outputPath = saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Databin/Client/Actor/organSkin.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;

        byte[] outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));

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
            update("    + Modding actions " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;
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
                if (new File(SpecialPath + "actions/" + id).exists()) {
                    String filePath = SpecialPath + "actions/" + id + "/" + filename;
                    if (new File(filePath).exists())
                        DHAExtension.WriteAllBytes(inputPath,
                                AOVAnalyzer.AOVCompress(DHAExtension.ReadAllBytes(filePath)));
                    update("        *Special mod file " + filename);
                    continue;
                }
                outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
                if (outputBytes == null)
                    continue;

                ProjectXML xml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));
                xml.changeCheckVirtual();
                if (modInfo.newSkin.filenameNotMod == null
                        || !Arrays.asList(modInfo.newSkin.filenameNotMod).stream()
                                .anyMatch(name -> name.toLowerCase().equals(filename.toLowerCase()))) {
                    // NodeList stringList = xml.getNodeListByTagName("String");
                    // for (int i = 0; i < stringList.getLength(); i++){
                    // if (CustomNode.getAttribute(stringList.item(i),
                    // "value").toLowerCase().contains("prefab_skill_effects/hero_skill_effects/")){
                    // Node parent = stringList.item(i).getParentNode();
                    // String[] nodeString = new String[]{};
                    // for (String s : nodeString){
                    // Node newChild = CustomNode.newNode(s);
                    // newChild = parent.getOwnerDocument().importNode(newChild, true);
                    // parent.appendChild(newChild);
                    // }
                    // }
                    // }
                    xml.setValue("String", new String[] { "resourceName", "resourceName2", "prefabName", "prefab" },
                            (StringOperator) (value) -> {
                                if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/")
                                        || (effectNotmod.containsKey(idMod) &&
                                                effectNotmod.get(idMod).stream().anyMatch((subeffect) -> value
                                                        .toLowerCase().contains(subeffect.toLowerCase())))) {
                                    // update("skipped " +value);
                                    return value;
                                }
                                String[] split = value.split("/");
                                String newValue;
                                if (modInfo.newSkin.isComponentSkin
                                        && modInfo.newSkin.componentLevel > SkinLabel.A.skinLevel) {
                                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/"
                                            + modInfo.newSkin.componentEffectId + "/"
                                            + split[split.length - 1];
                                } else {
                                    if (!modInfo.newSkin.isAwakeSkin) {
                                        newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod + "/"
                                                + split[split.length - 1];
                                    } else {
                                        newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod
                                                + "_5/"
                                                + split[split.length - 1];
                                    }
                                }
                                return newValue;
                            });
                    xml.setValue("bool", "bAllowEmptyEffect", "false");
                    if (modInfo.modSettings.modSound && modInfo.newSkin.getSkinLevel() > 2) {
                        List<Node> playSoundTick = xml.getTrackNodeByType("PlayHeroSoundTick", true);
                        xml.setValue("String", "eventName", "PlayHeroSoundTick", (value) -> {
                            if (value.toLowerCase().contains("_skin")) {
                                return value;
                            }
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
                        for (Node node : playSoundTick) {
                            String eventName = CustomNode.getEventChildValue(node, "String", "eventName");
                            if (eventName == null || eventName.toLowerCase().contains("_skin")) {
                                continue;
                            }
                            xml.appendActionChild(node);
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

                    if (modInfo.newSkin.filenameNotModCheckId == null
                            || !Arrays.asList(modInfo.newSkin.filenameNotModCheckId).stream()
                                    .anyMatch(name -> name.toLowerCase().equals(filename.toLowerCase()))) {
                        List<Integer> listIndex = xml.getTrackIndexByType("CheckSkinIdTick");
                        // hasVirtual=false;
                        List<Integer> listIndexNot = new ArrayList<>();
                        NodeList particle = xml.getNodeListByTagName("Track");
                        for (int j = 0; j < listIndex.size(); j++) {
                            Node event = CustomNode.getChild(particle.item(listIndex.get(j)), "Event");
                            String checkIdStr = CustomNode.getChildValue(event, "int", "skinId");
                            if (checkIdStr != null && Integer.parseInt(checkIdStr) == idMod) {
                                if (CustomNode.getChildValue(event, "bool", "bEqual") != null) {
                                    listIndexNot.add(listIndex.get(j));
                                    listIndex.remove(j);
                                    j--;
                                }
                            } else {
                                listIndex.remove(j);
                                j--;
                            }
                        }
                        // if (!(listIndex.size()==0 && listIndexNot.size()==0))
                        // update(" *" + filename + ": " + listIndex +", " + listIndexNot);
                        boolean hasTrackCantRemove = false;
                        for (String type : trackTypeNotRemoveCheckSkinId) {
                            if (skinIdAllowTrackCantRemoveMap.containsKey(id)
                                    && skinIdAllowTrackCantRemoveMap.get(id).contains(type)) {
                                continue;
                            }
                            List<Node> trackCantRemove = xml.getTrackNodeByType(type);
                            for (Node track : trackCantRemove) {
                                Map<Integer, Boolean> conditions = ProjectXML.getTrackConditions(track);
                                List<Integer> listCheckIndex = new ArrayList<>();
                                listCheckIndex.addAll(listIndex);
                                listCheckIndex.addAll(listIndexNot);
                                for (int checkIndex : listCheckIndex) {
                                    if (conditions.containsKey(checkIndex)) {
                                        if (conditions.get(checkIndex)) {
                                            update("        *" + filename + " has track can't remove: " + type);
                                            hasTrackCantRemove = true;
                                            break;
                                        }
                                    }
                                }
                                if (hasTrackCantRemove)
                                    break;
                            }
                        }
                        List<String> guidNotCheckId = new ArrayList<>();
                        {
                            NodeList conditions = xml.getNodeListByTagName("Condition");
                            for (int i = 0; i < conditions.getLength(); i++) {
                                Node condition = conditions.item(i);
                                int condiId = Integer.parseInt(CustomNode.getAttribute(condition, "id"));
                                if (listIndex.contains(condiId) || listIndexNot.contains(condiId)) {
                                    Node event = CustomNode.getChild(condition.getParentNode(), "Event");
                                    for (String name : trackChildValueNotRemoveCheckSkinId.keySet()) {
                                        String value = CustomNode.getChildValue(event, "", name);
                                        if (value != null
                                                && value.equals(trackChildValueNotRemoveCheckSkinId.get(name))) {
                                            hasTrackCantRemove = true;
                                            update("        *" + filename + " has track can't remove: "
                                                    + CustomNode.getAttribute(condition.getParentNode(), "eventType")
                                                    + "(" + CustomNode.getAttribute(condition.getParentNode(), "guid")
                                                    + ")" + " has child " + name);
                                            guidNotCheckId
                                                    .add(CustomNode.getAttribute(condition.getParentNode(), "guid"));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (!hasTrackCantRemove) {
                            for (int index : listIndex) {
                                Node event = CustomNode.getChild(particle.item(index), "Event");
                                CustomNode.setChildValue(event, "int", "skinId", heroId + "98");
                                Node bEqual = ProjectXML.convertStringToDocument(
                                        "<bool name=\"bEqual\" refParamName=\"\" useRefParam=\"false\" value=\"false\"/>")
                                        .getDocumentElement();
                                bEqual = event.getOwnerDocument().importNode(bEqual, true);
                                event.appendChild(bEqual);
                            }
                            for (int index : listIndexNot) {
                                Node event = CustomNode.getChild(particle.item(index), "Event");
                                CustomNode.setChildValue(event, "int", "skinId", heroId + "98");
                                CustomNode.remove(event, "bool", "bEqual");
                            }
                        }
                        for (int j = 0; j < particle.getLength(); j++) {
                            String eventType = particle.item(j).getAttributes()
                                    .getNamedItem("eventType").getNodeValue();
                            if (!(skinIdAllowTrackCantRemoveMap.containsKey(id)
                                    && skinIdAllowTrackCantRemoveMap.get(id).contains(eventType))
                                    && trackTypeNotRemoveCheckSkinId.contains(eventType)
                                    || guidNotCheckId.contains(
                                            particle.item(j).getAttributes().getNamedItem("guid").getNodeValue())) {
                                continue;
                            }
                            Map<Integer, Boolean> conditions = ProjectXML.getTrackConditions(particle.item(j));
                            boolean enable = false, disable = false;
                            int enIndex = -1, disIndex = -1;
                            for (int index : listIndex) {
                                if (conditions.containsKey(index) && conditions.get(index)) {
                                    enable = true;
                                    enIndex = index;
                                    break;
                                }
                                if (conditions.containsKey(index) && !conditions.get(index)) {
                                    disable = true;
                                    break;
                                }
                            }
                            for (int index : listIndexNot) {
                                if (conditions.containsKey(index) && conditions.get(index)) {
                                    disable = true;
                                    break;
                                }
                                if (conditions.containsKey(index) && !conditions.get(index)) {
                                    enable = true;
                                    enIndex = index;
                                    break;
                                }
                            }
                            if (enable) {
                                update("        *" + filename + ": enabled "
                                        + CustomNode.getAttribute(particle.item(j), "trackName") +
                                        "(type: " + CustomNode.getAttribute(particle.item(j), "eventType") + ")");
                                Node event = CustomNode.getChild(particle.item(j), "Event");
                                for (int i = 0; i < event.getChildNodes().getLength(); i++) {
                                    Node child = event.getChildNodes().item(i);
                                    if (child.getAttributes() != null
                                            && !notBugChildName.stream().anyMatch((childName) -> childName.toLowerCase()
                                                    .equals(CustomNode.getAttribute(child, "name").toLowerCase()))) {
                                        update("New child name found: " + CustomNode.getAttribute(child, "name"));
                                    }
                                }
                                if (hasTrackCantRemove) {
                                    ProjectXML.removeTrackCondition(particle.item(j), enIndex);
                                }
                            }
                            if (disable) {
                                update("        *" + filename + ": disabled "
                                        + CustomNode.getAttribute(particle.item(j), "trackName") +
                                        "(type: " + CustomNode.getAttribute(particle.item(j), "eventType") + ")");
                                if (hasTrackCantRemove) {
                                    particle.item(j).getAttributes().getNamedItem("enabled").setNodeValue("false");
                                }
                            }
                        }
                    }
                }

                xml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + " ");
                xml = specialModAction(xml, inputPath, idMod);

                // DHAExtension.WriteAllBytes(inputPath, xml.getXmlString().getBytes());
                DHAExtension.WriteAllBytes(inputPath, AOVAnalyzer.AOVCompress(xml.getXmlString().getBytes()));
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

    public void modActionsCustom(List<ModInfo> modList) throws Exception {
        update(" Dang mod hieu ung pack " + modPackName);

        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2);

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
        Scanner scanner = new Scanner(System.in);
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
            // continue;
            // }
            update("    + Modding actions " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            String inputZipPath = ActionsParentPath + "Actor_"
                    + Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3)) + "_Actions.pkg.bytes";

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

            Map<String, String> newValueMap = new HashMap<String, String>();
            for (String filename : new File(cacheModPath + filemodName).list()) {
                if (filename.toLowerCase().contains("back") || filename.toLowerCase().contains("born")
                        || (filename.toLowerCase().contains("death") && !modInfo.newSkin.hasDeathEffect)) {
                    continue;
                }
                String inputPath = cacheModPath + filemodName + filename;
                byte[] outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
                if (outputBytes == null)
                    continue;

                ProjectXML xml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));
                xml.changeCheckVirtual();
                if (!(modInfo.newSkin.filenameNotMod != null
                        && Arrays.asList(modInfo.newSkin.filenameNotMod).contains(filename.toLowerCase()))) {
                    xml.setValue("String", new String[] { "resourceName", "resourceName2", "prefabName", "prefab" },
                            (StringOperator) (value) -> {
                                if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/"))
                                    return value;
                                System.out.print(filename + ": Nhap resources moi cho " + value + ": ");
                                String newName = scanner.nextLine().trim();
                                if (newName.equals(""))
                                    newName = value;
                                newValueMap.put(value.toLowerCase(), newName);
                                return newName;
                            });
                    xml.setValue("bool", "bAllowEmptyEffect", "false");
                    List<Node> playSoundTick = xml.getTrackNodeByType("PlayHeroSoundTick", true);
                    xml.setValue("String", "eventName", "PlayHeroSoundTick", (value) -> {
                        System.out.print(filename + ": Nhap event moi cho " + value + ": ");
                        String newName = scanner.nextLine().trim();
                        if (newName.equals(""))
                            newName = value;
                        return newName;
                    });
                    xml.setValue("String", "clipName", "PlayAnimDuration", (value) -> {
                        System.out.print(filename + ": Nhap clip moi cho " + value + ": ");
                        String newName = scanner.nextLine().trim();
                        if (newName.equals(""))
                            newName = value;
                        return newName;
                    });
                    for (Node node : playSoundTick) {
                        xml.appendActionChild(node);
                    }
                }

                xml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + " ");
                xml = specialModAction(xml, inputPath, idMod);

                // DHAExtension.WriteAllBytes(inputPath, xml.getXmlString().getBytes());
                DHAExtension.WriteAllBytes(inputPath, AOVAnalyzer.AOVCompress(xml.getXmlString().getBytes()));
            }

            ZipExtension.zipDir(cacheModPath + filemodName.split("/")[0],
                    saveModPath + modPackName
                            + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/Actor_"
                            + Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3)) + "_Actions.pkg.bytes");

            String inputPath = AssetRefsPath + "Hero/" + Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3))
                    + "_AssetRef.bytes";
            String outputPath;
            outputPath = saveModPath + modPackName
                    + "/files/Resources/" + AOVversion + "/AssetRefs/Hero/"
                    + Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3)) + "_AssetRef.bytes";
            if (new File(outputPath).exists())
                inputPath = outputPath;
            byte[] outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
            Element assetRef = new Element(outputBytes);
            for (Element creditElement : creditElements) {
                assetRef.addChild(0, creditElement);
            }
            assetRef = assetRef.replaceValue(AnalyzerType.string, (value) -> {
                if (newValueMap.containsKey(value.toLowerCase().substring(1))) {
                    return "V" + newValueMap.get(value.toLowerCase().substring(1));
                } else {
                    return value;
                }
            });
            DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(assetRef.getBytes()));
        }
        scanner.close();
    }

    public void modActionsMulti(List<ModInfo> modList) throws Exception {
        update(" Dang mod hieu ung khong trung pack " + modPackName);
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

        String heroId = modList.get(0).newSkin.id.substring(0, 3);
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
            if (filename.toLowerCase().contains("back") || filename.toLowerCase().contains("born")) {
                continue;
            }
            inputPath = cacheModPath + filemodName + filename;
            outputBytes = AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath));
            if (outputBytes == null)
                continue;

            ProjectXML xml = new ProjectXML(new String(outputBytes, StandardCharsets.UTF_8));
            xml.changeCheckVirtual();

            int baseConditionLength = Integer.valueOf(xml.getNodeListByTagName("Condition").getLength());
            NodeList tracks = xml.getNodeListByTagName("Track");
            List<Integer> effectTrackIndexs = xml.getTrackIndexByType("TriggerParticle");
            effectTrackIndexs.addAll(xml.getTrackIndexByType("TriggerParticleTick"));
            List<Integer> soundTrackIndexs = xml.getTrackIndexByType("PlayHeroSoundTick");
            List<Integer> animTrackIndexs = xml.getTrackIndexByType("PlayAnimDuration");
            List<ConditionInfo> listEffectBaseCondition = new ArrayList<>();
            List<ConditionInfo> listSoundBaseCondition = new ArrayList<>();
            List<ConditionInfo> listAnimBaseCondition = new ArrayList<>();
            List<Node> effectTracks = new ArrayList<>();
            List<Node> soundTracks = new ArrayList<>();
            List<Node> animTracks = new ArrayList<>();

            for (int k = 0; k < effectTrackIndexs.size(); k++) {
                int index = effectTrackIndexs.get(k);
                Node track = tracks.item(index).cloneNode(true);
                boolean hasCheckId = false;
                for (int i = 0; i < track.getChildNodes().getLength(); i++) {
                    if (track.getChildNodes().item(i).getNodeName().equals("Condition")) {
                        int id = Integer.parseInt(
                                track.getChildNodes().item(i).getAttributes().getNamedItem("id").getNodeValue());
                        Node checkTrack = tracks.item(id);
                        if (CustomNode.getAttribute(checkTrack, "eventType").equals("CheckSkinIdTick")) {
                            Node event = CustomNode.getChild(checkTrack, "Event");
                            if (CustomNode.getChildValue(event, "bool", "bEqual") == null) {
                                hasCheckId = true;
                            }
                        }
                    }
                }
                if (!hasCheckId) {
                    effectTracks.add(track);
                    // CustomNode.clearEvent(tracks.item(index));
                    tracks.item(index).getAttributes().getNamedItem("enabled").setNodeValue("false");
                } else {
                    effectTrackIndexs.remove(k);
                    k--;
                }
            }
            for (int k = 0; k < soundTrackIndexs.size(); k++) {
                int index = soundTrackIndexs.get(k);
                Node track = tracks.item(index).cloneNode(true);
                boolean hasCheckId = false;
                for (int i = 0; i < track.getChildNodes().getLength(); i++) {
                    if (track.getChildNodes().item(i).getNodeName().equals("Condition")) {
                        int id = Integer.parseInt(
                                track.getChildNodes().item(i).getAttributes().getNamedItem("id").getNodeValue());
                        Node checkTrack = tracks.item(id);
                        if (CustomNode.getAttribute(checkTrack, "eventType").equals("CheckSkinIdTick")) {
                            Node event = CustomNode.getChild(checkTrack, "Event");
                            if (CustomNode.getChildValue(event, "bool", "bEqual") != null) {
                                hasCheckId = true;
                            }
                        }
                    }
                }
                if (!hasCheckId) {
                    soundTracks.add(tracks.item(index).cloneNode(true));
                    // CustomNode.clearEvent(tracks.item(index));
                    tracks.item(index).getAttributes().getNamedItem("enabled").setNodeValue("false");
                } else {
                    soundTrackIndexs.remove(k);
                    k--;
                }
            }
            if (modList.stream().filter((modInfo) -> modInfo.newSkin.changeAnim) != null) {
                for (int index : animTrackIndexs) {
                    Node track = tracks.item(index).cloneNode(true);
                    boolean hasCheckId = false;
                    for (int i = 0; i < track.getChildNodes().getLength(); i++) {
                        if (track.getChildNodes().item(i).getNodeName().equals("Condition")) {
                            int id = Integer.parseInt(
                                    track.getChildNodes().item(i).getAttributes().getNamedItem("id").getNodeValue());
                            Node checkTrack = tracks.item(id);
                            if (CustomNode.getAttribute(checkTrack, "eventType").equals("CheckSkinIdTick")) {
                                Node event = CustomNode.getChild(checkTrack, "Event");
                                String idStr = CustomNode.getChildValue(event, "int", "skinId");
                                if (idStr != null && CustomNode.getChildValue(event, "bool", "bEqual") != null) {
                                    hasCheckId = true;
                                }
                            }
                        }
                    }
                    if (!hasCheckId) {
                        animTracks.add(tracks.item(index).cloneNode(true));
                        // CustomNode.clearEvent(tracks.item(index));
                        tracks.item(index).getAttributes().getNamedItem("enabled").setNodeValue("false");
                    }
                }
            }

            Map<Integer, Node> disableTrack = new HashMap<>();
            for (ModInfo modInfo : modList) {
                String id = modInfo.newSkin.id;
                String skinId = id.substring(3, id.length());
                int skin = Integer.parseInt(skinId) - 1;
                int idMod = Integer.parseInt(heroId) * 100 + skin;
                int oriIdMod = Integer.parseInt(heroId) * 100
                        + Integer.parseInt(modInfo.targetSkins.get(0).id.substring(3)) - 1;

                if (!(modInfo.newSkin.filenameNotMod != null
                        && Arrays.asList(modInfo.newSkin.filenameNotMod).contains(filename.toLowerCase()))) {
                    List<Integer> listIndex = xml.getTrackIndexByType("CheckSkinIdTick");
                    List<Integer> listIndexNot = new ArrayList<>();
                    NodeList particle = xml.getNodeListByTagName("Track");
                    for (int j = 0; j < listIndex.size(); j++) {
                        Node event = CustomNode.getChild(particle.item(listIndex.get(j)), "Event");
                        String checkIdStr = CustomNode.getChildValue(event, "int", "skinId");
                        if (checkIdStr != null && Integer.parseInt(checkIdStr) == idMod) {
                            if (CustomNode.getChildValue(event, "bool", "bEqual") != null) {
                                listIndexNot.add(listIndex.get(j));
                                listIndex.remove(j);
                                j--;
                            }
                        } else {
                            listIndex.remove(j);
                            j--;
                        }
                    }

                    List<Node> baseTrack = new ArrayList<>();
                    Map<Integer, Integer> newTrackIndex = new HashMap<Integer, Integer>();
                    ConditionInfo conditionInfo = new ConditionInfo(xml.getNodeListByTagName("Track").getLength(),
                            "MOD_BY_" + ChannelName + "_Skin" + oriIdMod, true);
                    if (modInfo.newSkin.getSkinLevel() >= SkinLabel.S.skinLevel) {
                        baseTrack.addAll(effectTracks);
                        listEffectBaseCondition.add(conditionInfo.changeStatus(false));
                        if (modInfo.newSkin.getSkinLevel() >= SkinLabel.S_Plus.skinLevel) {
                            baseTrack.addAll(soundTracks);
                            listSoundBaseCondition.add(conditionInfo.changeStatus(false));
                        }
                        if (modInfo.newSkin.changeAnim) {
                            baseTrack.addAll(animTracks);
                            listAnimBaseCondition.add(conditionInfo.changeStatus(false));
                        }
                    }

                    NodeList conditionList = xml.getNodeListByTagName("Condition");
                    for (int i = 0; i < baseConditionLength; i++) {
                        if ((listIndex.contains(Integer.parseInt(CustomNode.getAttribute(conditionList.item(i), "id")))
                                && CustomNode.getAttribute(conditionList.item(i), "status").equals("true"))
                                || (listIndexNot.contains(
                                        Integer.parseInt(CustomNode.getAttribute(conditionList.item(i), "id")))
                                        && CustomNode.getAttribute(conditionList.item(i), "status").equals("false"))) {
                            Node track = conditionList.item(i).getParentNode().cloneNode(true);
                            CustomNode.removeCondition(track, listIndex.toArray(new Integer[0]));
                            baseTrack.add(track);
                        } else if ((listIndex
                                .contains(Integer.parseInt(CustomNode.getAttribute(conditionList.item(i), "id")))
                                && CustomNode.getAttribute(conditionList.item(i), "status").equals("false"))
                                || (listIndexNot.contains(
                                        Integer.parseInt(CustomNode.getAttribute(conditionList.item(i), "id")))
                                        && CustomNode.getAttribute(conditionList.item(i), "status").equals("true"))) {
                            Node track = conditionList.item(i).getParentNode().cloneNode(true);
                            CustomNode.insert(track, 0, ProjectXML.getConditionNode(conditionInfo.changeStatus(false)));
                            String guid = CustomNode.getAttribute(track, "guid");
                            for (int j = 0; j < baseTrack.size(); j++) {
                                if (CustomNode.getAttribute(baseTrack.get(j), "guid").equals(guid)) {
                                    track.getAttributes().getNamedItem("enabled")
                                            .setNodeValue(CustomNode.getAttribute(baseTrack.get(j), "enabled"));
                                    baseTrack.remove(j);
                                    break;
                                }
                            }
                            baseTrack.add(track);
                            conditionList.item(i).getParentNode().getAttributes().getNamedItem("enabled")
                                    .setNodeValue("false");
                        }
                    }

                    Node conditionTrue = ProjectXML.getConditionNode(conditionInfo);
                    xml.addComment(ChannelName + " mod for skin " + oriIdMod);
                    Node checkSkinIdTick = ProjectXML
                            .getCheckSkinTickNode(xml.getTrackNodeByType("CheckSkinIdTick").size(),
                                    "MOD_BY_" + ChannelName + "_Skin" + oriIdMod, oriIdMod, 0);
                    xml.appendActionChild(checkSkinIdTick);
                    for (Node node : baseTrack) {
                        Node track = node.cloneNode(true);
                        if (trackTypeNotRemoveCheckSkinId.contains(CustomNode.getAttribute(track, "eventType")))
                            continue;
                        int oldIndex = -1, newIndex = xml.getNodeListByTagName("Track").getLength();
                        for (int i = 0; i < tracks.getLength(); i++) {
                            if (CustomNode.getAttribute(track, "guid")
                                    .equals(CustomNode.getAttribute(tracks.item(i), "guid"))) {
                                oldIndex = i;
                                break;
                            }
                        }
                        newTrackIndex.put(oldIndex, newIndex);
                        if (!ProjectXML.getTrackConditions(track).containsKey(conditionInfo.index)) {
                            // continue;
                            CustomNode.insert(track, 0, conditionTrue);
                        }
                        Node event = CustomNode.getChild(track, "Event");
                        CustomNode.setChildValue(event, "String",
                                new String[] { "resourceName", "resourceName2", "prefabName", "prefab" },
                                (StringOperator) (value) -> {
                                    if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/"))
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

                        CustomNode.setChildValue(event, "bool", "bAllowEmptyEffect", "false");
                        if (CustomNode.getAttribute(track, "eventType").equals("PlayHeroSoundTick")) {
                            CustomNode.setChildValue(event, "String", "eventName", (value) -> {
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
                        } else if (CustomNode.getAttribute(track, "eventType").equals("PlayAnimDuration")) {
                            if (modInfo.newSkin.changeAnim) {
                                CustomNode.setChildValue(event, "String", "clipName", (value) -> {
                                    return idMod + "/" + value;
                                });
                                animTrackList.add(track.cloneNode(true));
                            }
                        }
                        xml.appendActionChild(track);
                    }
                    // NodeList conditionList2 = xml.getNodeListByTagName("Condition");
                    // for (int i = 0; i < conditionList2.getLength(); i++){
                    // if
                    // (newTrackIndex.containsKey(Integer.parseInt(CustomNode.getAttribute(conditionList2.item(i),
                    // "id")))){
                    // update(CustomNode.getAttribute(conditionList2.item(i).getParentNode(),
                    // "eventType"));
                    // }
                    // }
                    List<Node> stopTrackList = xml.getTrackNodeByType("StopTrack");
                    for (int i = 0; i < stopTrackList.size(); i++) {
                        Node stopTrack = stopTrackList.get(i).cloneNode(true);
                        if (DHAExtension.listContainsElementFromOther(newTrackIndex.keySet().toArray(),
                                CustomNode.getTrackStopContains(stopTrack))) {
                            for (int key : newTrackIndex.keySet()) {
                                if (CustomNode.replaceTrackStopContains(stopTrack, key, newTrackIndex.get(key))) {
                                    xml.appendActionChild(stopTrack);
                                    // update(filename + ": replaced " + key + " to " +
                                    // CustomNode.getEventChildAttr(stopTrack, "TrackObject", "trackId", "id"));
                                }
                            }
                        }
                    }
                }

                xml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + " ");
                xml = specialModAction(xml, inputPath, idMod);
            }

            xml.addComment("Mod for default");
            Map<Integer, Integer> newTrackIndexDefault = new HashMap<Integer, Integer>();
            for (int i = 0; i < effectTrackIndexs.size(); i++) {
                newTrackIndexDefault.put(effectTrackIndexs.get(i), xml.getNodeListByTagName("Track").getLength());
                Node track = effectTracks.get(i).cloneNode(true);
                // track.getAttributes().getNamedItem("enabled").setNodeValue("true");
                for (ConditionInfo info : listEffectBaseCondition) {
                    CustomNode.insert(track, 0, ProjectXML.getConditionNode(info));
                }
                xml.appendActionChild(track);
            }
            List<Node> stopTrackList = xml.getTrackNodeByType("StopTrack");
            for (int i = 0; i < stopTrackList.size(); i++) {
                Node stopTrack = stopTrackList.get(i).cloneNode(true);
                if (DHAExtension.listContainsElementFromOther(newTrackIndexDefault.keySet().toArray(),
                        CustomNode.getTrackStopContains(stopTrack))) {
                    for (int key : newTrackIndexDefault.keySet()) {
                        if (CustomNode.replaceTrackStopContains(stopTrack, key, newTrackIndexDefault.get(key))) {
                            xml.appendActionChild(stopTrack);
                            // update(filename + ": replaced " + key + " to " +
                            // CustomNode.getEventChildAttr(stopTrack, "TrackObject", "trackId", "id"));
                        }
                    }
                }
            }

            for (Node node : soundTracks) {
                Node track = node.cloneNode(true);
                // track.getAttributes().getNamedItem("enabled").setNodeValue("true");
                for (ConditionInfo info : listSoundBaseCondition) {
                    CustomNode.insert(track, 0, ProjectXML.getConditionNode(info));
                }
                xml.appendActionChild(track);
            }

            for (Node node : animTracks) {
                Node track = node.cloneNode(true);
                // track.getAttributes().getNamedItem("enabled").setNodeValue("true");
                for (ConditionInfo info : listAnimBaseCondition) {
                    CustomNode.insert(track, 0, ProjectXML.getConditionNode(info));
                }
                xml.appendActionChild(track);
            }

            xml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + " ");
            // DHAExtension.WriteAllBytes(inputPath, xml.getXmlString().getBytes());
            DHAExtension.WriteAllBytes(inputPath, AOVAnalyzer.AOVCompress(xml.getXmlString().getBytes()));
        }

        ZipExtension.zipDir(cacheModPath + filemodName.split("/")[0],
                saveModPath + modPackName
                        + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/Actor_"
                        + heroId + "_Actions.pkg.bytes");

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

        // update(" Fix khung...");
        // modAssetRefMulti(modList);
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
                    for (int i = 0; i < highlightLv; i++) {
                        xml.appendActionChild(track);
                    }
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

            String id = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;
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
                if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/")
                        || (effectNotmod.containsKey(idMod) &&
                                effectNotmod.get(idMod).stream().anyMatch(
                                        (subeffect) -> value.toLowerCase().contains(subeffect.toLowerCase())))) {
                    // update("skipped " +value);
                    return value;
                }
                String[] split = value.split("/");
                String newValue;
                if (modInfo.newSkin.isComponentSkin && modInfo.newSkin.componentLevel > SkinLabel.A.skinLevel) {
                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/"
                            + modInfo.newSkin.componentEffectId + "/"
                            + split[split.length - 1];
                } else {
                    if (!modInfo.newSkin.isAwakeSkin) {
                        newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod + "/"
                                + split[split.length - 1];
                    } else {
                        newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5/"
                                + split[split.length - 1];
                    }
                }
                return newValue;
            });
            DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(assetRef.getBytes()));
        }
    }

    public void modAssetRefMulti(List<ModInfo> modList) throws Exception {
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
        if (modList.size() == 0)
            return;
        update(" Dang mod danh thuong pack " + modPackName);
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
            // continue;
            // }
            update("    + Modding lite bullets " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
            ZipEntry entryGetName;
            while ((entryGetName = zis.getNextEntry()).getName().split("/").length < 2)
                ;
            String heroCodeName = entryGetName.getName().split("/")[1];
            zis.close();

            String newCode, oldCode = "(?i)prefab_skill_effects/hero_skill_effects/" + heroCodeName;

            if (modInfo.newSkin.isComponentSkin && modInfo.newSkin.componentLevel > SkinLabel.A.skinLevel) {
                newCode = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + modInfo.newSkin.componentEffectId;
            } else {
                if (!modInfo.newSkin.isAwakeSkin) {
                    newCode = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" + idMod;
                } else {
                    newCode = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5";
                }
            }
            listBullet.replaceBulletEffect(Integer.parseInt(heroId), oldCode, newCode);
        }
        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(listBullet.getBytes()));
    }

    public void modSkillMark(List<ModInfo> modList) throws IOException {

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
        if (modList.size() == 0)
            return;
        update(" Dang mod dau an pack " + modPackName);
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modAction || modInfo.newSkin.getSkinLevel() < 2) {
            // continue;
            // }
            update("    + Modding skill marks " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);

            String id = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = Integer.parseInt(heroId) * 100 + skin;

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
            ZipEntry entryGetName;
            while ((entryGetName = zis.getNextEntry()).getName().split("/").length < 2)
                ;
            String heroCodeName = entryGetName.getName().split("/")[1];
            zis.close();

            String newCode, oldCode = "(?i)prefab_skill_effects/hero_skill_effects/" + heroCodeName;
            if (modInfo.newSkin.isComponentSkin && modInfo.newSkin.componentLevel > SkinLabel.A.skinLevel) {
                newCode = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + modInfo.newSkin.componentEffectId;
            } else {
                if (!modInfo.newSkin.isAwakeSkin) {
                    newCode = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" + idMod;
                } else {
                    newCode = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5";
                }
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
            String id = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;
            update("    + Modding sound " + (l + 1) + "/" + modList.size() + ": " + modInfo.newSkin);
            int heroId = Integer.parseInt(id.substring(0, 3));
            int targetId = heroId * 100 + Integer.parseInt(id.substring(3)) - 1;
            if (skinSoundSpecial.containsKey(targetId)) {
                targetId = skinSoundSpecial.get(targetId);
                // update(targetId + "");
            }
            // for (int f = 0; f < modInfo.targetSkins.size(); f++) {
            int f = 0;
            int baseId = Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3)) * 100
                    + Integer.parseInt(modInfo.targetSkins.get(f).id.substring(3)) - 1;
            for (int i = 0; i < soundListArr.length; i++) {
                ListSoundElement targetSounds = null;
                String soundSpecial = SpecialPath + "sound/" + id + "/"
                        + new File(inputPaths[i]).getName();
                if (new File(soundSpecial).exists()) {
                    targetSounds = new ListSoundElement(DHAExtension.ReadAllBytes(soundSpecial));
                }
                if (Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3)) == heroId
                        && baseId != heroId * 100
                        && (modInfo.targetSkins.get(f).label == null
                                || modInfo.targetSkins.get(f).getSkinLevel() < SkinLabel.S_Plus.skinLevel)
                        && i == 0) {
                    soundListArr[i].copySound(heroId * 100, targetId, false);
                } else {
                    if (targetSounds == null) {
                        soundListArr[i].copySound(baseId, targetId);
                    } else
                        soundListArr[i].setSound(baseId, targetSounds.soundElements);
                }
            }
            // }
        }
        for (int i = 0; i < outputPaths.length; i++) {
            DHAExtension.WriteAllBytes(outputPaths[i], AOVAnalyzer.AOVCompress(soundListArr[i].getBytes()));
        }
    }

    public void modMotion(List<ModInfo> modList) throws Exception {
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !skinDanceCode.containsKey(
                modInfo.newSkin.isComponentSkin ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                        : modInfo.newSkin.id)
                || !modInfo.modSettings.modMotion);
        if (modList.size() == 0)
            return;
        update(" Dang mod dieu nhay pack " + modPackName);

        String inputPath = DatabinPath + "Motion/ResMotionShow.bytes";
        String outputPath = saveModPath + modPackName
                + "/files/Resources/" + AOVversion + "/Databin/Client/Motion/ResMotionShow.bytes";
        if (new File(outputPath).exists())
            inputPath = outputPath;

        ListMotionElement listMotionElement = new ListMotionElement(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(inputPath)));
        for (int l = 0; l < modList.size(); l++) {
            ModInfo modInfo = modList.get(l);
            // if (!modInfo.modSettings.modIcon)
            // continue;
            update("    + Modding motion " + (l + 1) + "/" + modList.size() + ": " + modInfo);
            String id = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;
            int heroId = Integer.parseInt(modInfo.newSkin.id.substring(0, 3));
            listMotionElement.copyMotion(heroId, heroDanceCode.get(heroId).toArray(new String[0]),
                    skinDanceCode.get(id));
        }
        DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(listMotionElement.getBytes()));
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
            String id = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;
            int heroId = Integer.parseInt(id.substring(0, 3));
            int targetId = heroId * 100 + Integer.parseInt(id.substring(3)) - 1;
            byte[] iconBytes = null;
            if (new File(SpecialPath + "actor/" + id + ".bytes").exists()) {
                iconBytes = DHAExtension.ReadAllBytes(SpecialPath + "actor/" + id + ".bytes");
            }
            for (Skin skin : modInfo.targetSkins) {
                int baseId = Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3)) * 100
                        + Integer.parseInt(skin.id.substring(3)) - 1;
                if (iconBytes == null) {
                    listIconElement.copyIcon(baseId, targetId, !idNotSwap.contains(id));
                } else {
                    listIconElement.setIcon(baseId, iconBytes);
                    listIconElement.setIcon(targetId, iconBytes);
                }
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
            String id = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;
            int heroId = Integer.parseInt(id.substring(0, 3));
            int targetId = heroId * 100 + Integer.parseInt(id.substring(3)) - 1;
            byte[] iconBytes = null;
            if (new File(SpecialPath + "shop/" + id + ".bytes").exists()) {
                iconBytes = DHAExtension.ReadAllBytes(SpecialPath + "shop/" + id + ".bytes");
            }
            for (Skin skin : modInfo.targetSkins) {
                int baseId = Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3)) * 100
                        + Integer.parseInt(skin.id.substring(3)) - 1;
                if (iconBytes == null) {
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
                        update("        *changed new label to " + targetId + "(" + modInfo.newSkin.label + ")");
                    }
                } else {
                    listLabelElement.setLabel(baseId, iconBytes);
                    try {
                        listLabelElement.setLabel(targetId, iconBytes);
                    } catch (Exception e) {

                    }
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
            ZipEntry entryGetName;
            while ((entryGetName = zis.getNextEntry()).getName().split("/").length < 2)
                ;
            String heroCodeName = entryGetName.getName().split("/")[1];
            zis.close();

            String id = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;
            String skinId = id.substring(3);
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = hero * 100 + skin;
            backXml.insertActionChild(l + 1, ProjectXML.getCheckHeroTickNode(l,
                    Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3))));
            conditionList.add(ProjectXML.getConditionNode(l + 1,
                    "Mod_by_" + ChannelName + "_" + modInfo.targetSkins.get(0).id.substring(0, 3), false));

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
                    // &&
                    // !nodeList.item(i).getAttributes().getNamedItem("eventType").getNodeValue().toLowerCase()
                    // .contains("triggerparticle")
                    ) {
                        for (int j = 0; j < nodeList.item(i).getChildNodes().getLength(); j++) {
                            Node node = nodeList.item(i).getChildNodes().item(j);
                            if (node.getNodeName().equals("Condition"))
                                nodeList.item(i).removeChild(node);
                        }
                        baseBackTrack.add(nodeList.item(i));
                    }
                }
            }
            if (modInfo.newSkin.specialBackAnim != null) {
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
                                node.getAttributes().getNamedItem("value")
                                        .setNodeValue(modInfo.newSkin.specialBackAnim + "/" + value);
                            }
                        }
                    }
                    baseBackTrack.add(track);
                }
            }
            for (int i = 0; i < baseBackTrack.size(); i++) {
                Node track = baseBackTrack.get(i).cloneNode(true);
                Node condition = ProjectXML.getConditionNode(l + 1,
                        "Mod_by_" + ChannelName + "_" + modInfo.targetSkins.get(0).id.substring(0, 3));
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
                            if (modInfo.newSkin.isComponentSkin
                                    && modInfo.newSkin.componentLevel >= SkinLabel.SS.skinLevel) {
                                newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/"
                                        + modInfo.newSkin.componentEffectId + "/"
                                        + split[split.length - 1];
                            } else {
                                if (!modInfo.newSkin.isAwakeSkin) {
                                    newValue = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" +
                                            idMod + "/" + split[split.length - 1];
                                } else {
                                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod + "_5/"
                                            + split[split.length - 1];
                                }
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
                    + "/files/Resources/" + AOVversion + "/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";

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
                ZipEntry entryGetName;
                while ((entryGetName = zis.getNextEntry()).getName().split("/").length < 2)
                    ;
                String heroCodeName = entryGetName.getName().split("/")[1];
                zis.close();

                String id = modInfo.newSkin.isComponentSkin
                        ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                        : modInfo.newSkin.id;
                String skinId = id.substring(3, id.length());
                int skin = Integer.parseInt(skinId) - 1;
                int idMod = hero * 100 + skin;
                hasteXml.insertActionChild(l + 1, ProjectXML.getCheckHeroTickNode(l,
                        Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3))));
                int conditionIndex = l + 1;// hasteXml.getNodeListByTagName("Track").getLength() - 1;
                conditionList.add(ProjectXML.getConditionNode(conditionIndex,
                        "Mod_by_" + ChannelName + "_" + Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3)),
                        false));

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
                                    if (modInfo.newSkin.isComponentSkin
                                            && modInfo.newSkin.componentLevel > SkinLabel.A.skinLevel) {
                                        newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/"
                                                + modInfo.newSkin.componentEffectId + "/"
                                                + split[split.length - 1];
                                    } else {
                                        if (!modInfo.newSkin.isAwakeSkin) {
                                            newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" + idMod
                                                    + "/"
                                                    + split[split.length - 1];
                                        } else {
                                            newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod
                                                    + "_5/"
                                                    + split[split.length - 1];
                                        }
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
                                if (node.getNodeName().equals("Condition")) {
                                    nodeList.item(i).removeChild(node);
                                    j--;
                                }
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
                            }
                            track.removeChild(child);
                            j--;
                        }
                    }
                    if (!kt) {
                        continue;
                    }
                    Node condition = ProjectXML.getConditionNode(conditionIndex, "Mod_by_" + ChannelName + "_"
                            + Integer.parseInt(modInfo.targetSkins.get(0).id.substring(0, 3)));

                    condition = track.getOwnerDocument().importNode(condition, true);
                    track.insertBefore(condition, track.getFirstChild());
                    for (int j = 0; j < track.getChildNodes().item(track.getChildNodes().getLength() -
                            2).getChildNodes().getLength(); j++) {
                        Node node = track.getChildNodes().item(track.getChildNodes().getLength() - 2).getChildNodes()
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
                                if (modInfo.newSkin.isComponentSkin
                                        && modInfo.newSkin.componentLevel >= SkinLabel.SSS_HH.skinLevel) {
                                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/"
                                            + modInfo.newSkin.componentEffectId + "/"
                                            + endEffect;
                                } else {
                                    if (!modInfo.newSkin.isAwakeSkin) {
                                        newValue = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" +
                                                idMod + "/" + endEffect;
                                    } else {
                                        newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod
                                                + "_5/"
                                                + endEffect;
                                    }
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
                                            .removeChild(
                                                    track.getChildNodes().item(track.getChildNodes().getLength() - 2)
                                                            .getChildNodes().item(j));
                                }
                            }
                        }
                    }
                    int totalLength = 5, endLength = 1;
                    int start = 0, end = totalLength;
                    if (f == 0 && CustomNode.checkEventType(track,
                            new String[] { "TriggerParticle", "TriggerParticleTick" })) {
                        if (modInfo.newSkin.hasteNameEnd != null) {
                            end = totalLength - endLength;
                            Node track1 = track.cloneNode(true), track2 = track.cloneNode(true);
                            CustomNode.setEventTime(track1, start);
                            CustomNode.setEventDuration(track1, end - start);
                            CustomNode.setEventChildValue(track1, "String", "resourceName", (value) -> {
                                String[] split = value.split("/");
                                String newValue;
                                String endEffect = modInfo.newSkin.hasteNameRun != null ? modInfo.newSkin.hasteNameRun
                                        : modInfo.newSkin.hasteName;
                                if (!modInfo.newSkin.isAwakeSkin) {
                                    newValue = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" +
                                            idMod + "/" + endEffect;
                                } else {
                                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod +
                                            "_5/" + endEffect;
                                }
                                return newValue;
                            });
                            CustomNode.setEventTime(track2, end);
                            CustomNode.setEventDuration(track2, endLength);
                            CustomNode.setEventChildValue(track2, "String", "resourceName", (value) -> {
                                String[] split = value.split("/");
                                String newValue;
                                String endEffect = modInfo.newSkin.hasteNameEnd;
                                if (!modInfo.newSkin.isAwakeSkin) {
                                    newValue = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" +
                                            idMod + "/" + endEffect;
                                } else {
                                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod +
                                            "_5/" + endEffect;
                                }
                                return newValue;
                            });
                            hasteXml.appendActionChild(track1);
                            hasteXml.appendActionChild(track2);
                        }
                    }
                    if (start == 0 && end == totalLength) {
                        hasteXml.appendActionChild(track);
                    }

                    Node condition2 = ProjectXML.getConditionNode(conditionIndex, "Mod_by_" + ChannelName +
                            "_" + hero, false);
                    if (i != 0 && i < baseTrack.size()) {
                        condition2 = baseHasteTrack.get(i).getOwnerDocument().importNode(condition2, true);
                        baseHasteTrack.get(i).insertBefore(condition2, baseHasteTrack.get(i).getFirstChild());
                    }
                }
                hasteXml = specialModHaste(hasteXml, new File(inputPath).getName(), idMod);
            }
            for (Node condition : conditionList) {
                condition = baseTrack.get(0).getOwnerDocument().importNode(condition, true);
                baseTrack.get(0).insertBefore(condition, baseTrack.get(0).getFirstChild());
            }

            hasteXml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + " ");
            // DHAExtension.WriteAllBytes("D:/" + new File(outputPath).getName(),
            // hasteXml.getXmlString().getBytes());
            DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(hasteXml.getXmlString().getBytes()));
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

    public void modBackMulti(List<ModInfo> modList) throws Exception {
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modBack || modInfo.newSkin.getSkinLevel() < 4);
        if (modList.size() == 0)
            return;
        update(" Dang mod hieu ung bien ve multi pack " + modPackName);
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

            String id = modInfo.newSkin.isComponentSkin
                    ? modInfo.newSkin.id.substring(0, modInfo.newSkin.id.length() - 2)
                    : modInfo.newSkin.id;
            int hero = Integer.parseInt(id.substring(0, 3));

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(InfosParentPath + "Actor_" + hero + "_Infos.pkg.bytes"));
            ZipEntry entryGetName;
            while ((entryGetName = zis.getNextEntry()).getName().split("/").length < 2)
                ;
            String heroCodeName = entryGetName.getName().split("/")[1];
            zis.close();

            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            int idMod = hero * 100 + skin;
            int oriIdMod = hero * 100
                    + Integer.parseInt(modInfo.targetSkins.get(0).id.substring(3)) - 1;
            backXml.insertActionChild(l + 1,
                    ProjectXML.getCheckSkinTickNode(l, "Mod_by_" + ChannelName + "_" + oriIdMod, oriIdMod, 1));
            conditionList.add(ProjectXML.getConditionNode(l + 1,
                    "Mod_by_" + ChannelName + "_" + oriIdMod, false));

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
                Node condition = ProjectXML.getConditionNode(l + 1,
                        "Mod_by_" + ChannelName + "_" + oriIdMod);
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

    public void modHasteMulti(List<ModInfo> modList) throws Exception {
        modList = new ArrayList<>(modList);
        modList.removeIf(modInfo -> !modInfo.modSettings.modHaste || modInfo.newSkin.getSkinLevel() < 5);
        if (modList.size() == 0)
            return;
        update(" Dang mod hieu ung gia toc multi pack " + modPackName);
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
                ZipEntry entryGetName;
                while ((entryGetName = zis.getNextEntry()).getName().split("/").length < 2)
                    ;
                String heroCodeName = entryGetName.getName().split("/")[1];
                zis.close();

                String skinId = modInfo.newSkin.id.substring(3, modInfo.newSkin.id.length());
                int skin = Integer.parseInt(skinId) - 1;
                int idMod = hero * 100 + skin;
                int oriIdMod = hero * 100
                        + Integer.parseInt(modInfo.targetSkins.get(0).id.substring(3)) - 1;
                hasteXml.insertActionChild(l + 1,
                        ProjectXML.getCheckSkinTickNode(l, "Mod_by_" + ChannelName + "_" + oriIdMod, oriIdMod, 1));
                int conditionIndex = l + 1;// hasteXml.getNodeListByTagName("Track").getLength() - 1;
                conditionList.add(ProjectXML.getConditionNode(conditionIndex,
                        "Mod_by_" + ChannelName + "_" + oriIdMod,
                        false));

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
                                if (node.getNodeName().equals("Condition")) {
                                    nodeList.item(i).removeChild(node);
                                    j--;
                                }
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
                            }
                            track.removeChild(child);
                            j--;
                        }
                    }
                    if (!kt) {
                        continue;
                    }
                    Node condition = ProjectXML.getConditionNode(conditionIndex, "Mod_by_" + ChannelName + "_"
                            + oriIdMod);

                    condition = track.getOwnerDocument().importNode(condition, true);
                    track.insertBefore(condition, track.getFirstChild());
                    for (int j = 0; j < track.getChildNodes().item(track.getChildNodes().getLength() -
                            2).getChildNodes().getLength(); j++) {
                        Node node = track.getChildNodes().item(track.getChildNodes().getLength() - 2).getChildNodes()
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
                                            .removeChild(
                                                    track.getChildNodes().item(track.getChildNodes().getLength() - 2)
                                                            .getChildNodes().item(j));
                                }
                            }
                        }
                    }
                    int totalLength = 5, endLength = 1;
                    int start = 0, end = totalLength;
                    if (f == 0 && CustomNode.checkEventType(track,
                            new String[] { "TriggerParticle", "TriggerParticleTick" })) {
                        if (modInfo.newSkin.hasteNameEnd != null) {
                            end = totalLength - endLength;
                            Node track1 = track.cloneNode(true), track2 = track.cloneNode(true);
                            CustomNode.setEventTime(track1, start);
                            CustomNode.setEventDuration(track1, end - start);
                            CustomNode.setEventChildValue(track1, "String", "resourceName", (value) -> {
                                String[] split = value.split("/");
                                String newValue;
                                String endEffect = modInfo.newSkin.hasteNameRun != null ? modInfo.newSkin.hasteNameRun
                                        : modInfo.newSkin.hasteName;
                                if (!modInfo.newSkin.isAwakeSkin) {
                                    newValue = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" +
                                            idMod + "/" + endEffect;
                                } else {
                                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod +
                                            "_5/" + endEffect;
                                }
                                return newValue;
                            });
                            CustomNode.setEventTime(track2, end);
                            CustomNode.setEventDuration(track2, endLength);
                            CustomNode.setEventChildValue(track2, "String", "resourceName", (value) -> {
                                String[] split = value.split("/");
                                String newValue;
                                String endEffect = modInfo.newSkin.hasteNameEnd;
                                if (!modInfo.newSkin.isAwakeSkin) {
                                    newValue = "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" +
                                            idMod + "/" + endEffect;
                                } else {
                                    newValue = "Prefab_Skill_Effects/Component_Effects/" + idMod + "/" + idMod +
                                            "_5/" + endEffect;
                                }
                                return newValue;
                            });
                            hasteXml.appendActionChild(track1);
                            hasteXml.appendActionChild(track2);
                        }
                    }
                    if (start == 0 && end == totalLength) {
                        hasteXml.appendActionChild(track);
                    }

                    Node condition2 = ProjectXML.getConditionNode(conditionIndex, "Mod_by_" + ChannelName +
                            "_" + oriIdMod, false);
                    if (i != 0 && i < baseTrack.size()) {
                        condition2 = baseHasteTrack.get(i).getOwnerDocument().importNode(condition2, true);
                        baseHasteTrack.get(i).insertBefore(condition2, baseHasteTrack.get(i).getFirstChild());
                    }
                }
                hasteXml = specialModHaste(hasteXml, new File(inputPath).getName(), idMod);
            }
            for (Node condition : conditionList) {
                condition = baseTrack.get(0).getOwnerDocument().importNode(condition, true);
                baseTrack.get(0).insertBefore(condition, baseTrack.get(0).getFirstChild());
            }

            hasteXml.addComment("Mod By " + ChannelName + "! Subscribe: " + YoutubeLink + " ");
            // DHAExtension.WriteAllBytes("D:/" + new File(outputPath).getName(),
            // hasteXml.getXmlString().getBytes());
            DHAExtension.WriteAllBytes(outputPath, AOVAnalyzer.AOVCompress(hasteXml.getXmlString().getBytes()));
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
        switch (idMod) {
            case 13210:
                switch (fileName) {
                    case "S1B0.xml":
                    case "S11B0.xml":
                    case "S12B0.xml":
                        Node node = xml.getTrackNodeByType("PlayHeroSoundTick", false).get(0);
                        node.getAttributes().getNamedItem("execOnActionCompleted").setNodeValue("false");
                        CustomNode.setEventChildValue(node, "String", "eventName",
                                "Play_13210_Hayate_SkillA_03_Skin10");
                        int changeAnimTrackIndex = -1;
                        if (fileName.equals("S1B0.xml")) {
                            changeAnimTrackIndex = 2;
                        } else {
                            changeAnimTrackIndex = 3;
                        }
                        CustomNode.appendEventNode(
                                xml.getTrackNodeByType("ChangeAnimDuration", false).get(changeAnimTrackIndex),
                                CustomNode.newNode(
                                        "<bool name=\"bUseDirectionalMove\" value=\"true\" refParamName=\"\" useRefParam=\"false\" />"));
                        break;
                }
                break;
            case 13099:
                switch (fileName) {
                    // case "S2B1.xml":
                    case "S21.xml":
                    case "S22.xml":
                        String targetPath = parentPath + "/";
                        int trackIndex = 0;
                        // if (fileName.equals("S2B1.xml")) {
                        // targetPath += "S2B1_13011.xml";
                        // trackIndex = 0;
                        // } else
                        if (fileName.equals("S21.xml")) {
                            targetPath += "S2B2_13011.xml";
                        } else if (fileName.equals("S22.xml")) {
                            targetPath += "S2B3_13011.xml";
                        }

                        xml.setValue("String", new String[] { "resourceName", "resourceName2",
                                "prefabName", "prefab" },
                                (StringOperator) (value) -> {
                                    if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/"))
                                        return value;
                                    String[] split = value.split("/");
                                    String newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" +
                                            idMod
                                            + "/"
                                            + split[split.length - 1];
                                    return newValue;
                                });
                        xml.setValue("bool", "bAllowEmptyEffect", "false");
                        List<Node> playSoundTick = xml.getTrackNodeByType("PlayHeroSoundTick", true);
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
                                        child.getAttributes().getNamedItem("value").setNodeValue((idMod / 100) +
                                                "98");
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
                        xml2.setValue("String", new String[] { "resourceName", "resourceName2",
                                "prefabName" },
                                (StringOperator) (value) -> {
                                    if (!value.toLowerCase().contains("prefab_skill_effects/hero_skill_effects/"))
                                        return value;
                                    String[] split = value.split("/");
                                    String newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/" +
                                            idMod
                                            + "/"
                                            + split[split.length - 1];
                                    return newValue;
                                });
                        Node newTrack = xml2.getTrackNodeByName("TriggerParticle0").get(0);
                        newTrack = xml.doc.importNode(newTrack, true);

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
                                        children.item(j).getAttributes().getNamedItem("isTemp").setNodeValue("false");
                                    }
                                }
                            }
                        }
                        List<Node> baseTrackList = xml.getTrackNodeByName("TriggerParticle0", false);
                        Node baseTrack = baseTrackList.get(trackIndex);
                        baseTrack.getParentNode().replaceChild(newTrack, baseTrack);
                        for (int i = 0; i < baseTrack.getChildNodes().getLength(); i++) {
                            // Node trackChild = baseTrack.getChildNodes().item(i);
                            // if (trackChild.getNodeName().equals("Event")) {
                            // while (trackChild.getChildNodes().getLength() != 0) {
                            // trackChild.removeChild(trackChild.getChildNodes().item(0));
                            // }
                            // }
                        }
                        break;
                }
                break;
            // case 54402:
            // xml.setValue("String", "resourceName", (value) -> {
            // String newValue;
            // String[] split = value.split("/");
            // if (value.toLowerCase().endsWith("Painter_Atk4_blue".toLowerCase())
            // || value.endsWith("Painter_Atk4_red".toLowerCase())) {
            // newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/"
            // + split[split.length - 1];
            // } else {
            // newValue = value;
            // }
            // return newValue;
            // });
            // break;
            // case 18004:
            // xml.setValue("String", "resourceName", (value) -> {
            // String newValue;
            // String[] split = value.split("/");
            // if (value.toLowerCase().contains("_loop_")) {
            // newValue = String.join("/", Arrays.copyOfRange(split, 0, 3)) + "/"
            // + split[split.length - 1];
            // } else {
            // newValue = value;
            // }
            // return newValue;
            // });
            // break;
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
