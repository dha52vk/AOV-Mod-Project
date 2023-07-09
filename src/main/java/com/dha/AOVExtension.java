package com.dha;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictCompress;

public class AOVExtension {
    public static String InfosParentPath = "F:/This PC/Documents/AOV/Resources/1.50.1/Prefab_Characters/";
    public static String ActionsParentPath = "F:/This PC/Documents/AOV/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/";
    public static String DatabinPath = "F:/This PC/Documents/AOV/Resources/1.50.1/Databin/Client/";
    public static String AssetRefsPath = "F:/This PC/Documents/AOV/Resources/1.50.1/AssetRefs/";
    public static String LanguagePath = "F:/This PC/Documents/AOV/Resources/1.50.1/Languages/VN_Garena_VN/";
    public static String SpecialPath = ".special/";

    public static List<String> idNotSwap = new ArrayList<>(Arrays.asList(new String[] {
            "19014", "11213", "13211", "50118", "16711", "19610", "13610", "11813", "5157", "5255",
            "1135", "1913", "5069", "5483", "1696", "1209", "5464", "16712"
    }));

    public static List<String> skinNotSwapCheck = new ArrayList<>(Arrays.asList(new String[] {
            "13211", "14112", "1678"
    }));

    public static List<String> skinAwaken = new ArrayList<>(Arrays.asList(new String[] {
            "13312", "1678"
    }));

    public static void ModSkin(Hero hero, Skin skin) {
        String id = skin.id;
        System.out.println("  Dang mod ngoai hinh trang phuc " + skin.name + "...");
        switch (hero.id) {
            case "111":
                ModOrgan(hero.id + "3", skin.id);
                break;
            case "141":
                ModOrgan(hero.id + "2", skin.id);
                break;
            case "150":
                ModOrgan(hero.id + "1", skin.id);
                break;
            case "501":
                ModOrgan(hero.id + "2", skin.id);
                break;
        }
        String heroId = id.substring(0, 3);
        AOVExtension.ModInfoAdvanced(heroId + "1", id);
        String[] heroHasComponent = new String[] { "116" };
        switch (skin.label) {
            case S:
            case S_HH:
                System.out.println("  Dang mod hieu ung trang phuc " + skin.name + "...");
                AOVExtension.ModAction(id, false, skin.changeAnim, skin.hasDeathEffect, skin.isAwakeSkin,
                        skin.levelSFXUnlock, skin.levelVOXUnlock, skin.specialChangeOld,
                        skin.specialChangeNew,
                        skin.filenameNotMod);
                AOVExtension.ModAssetRef(skin.id, hero.specialAssetRefOld, hero.specialAssetRefNew);
                break;
            case S_Plus:
            case S_Plus_HH:
            case SS:
            case SS_HH:
            case SS_Chroma:
            case SSS_HH:
                System.out.println("  Dang mod hieu ung va am thanh trang phuc " + skin.name + "...");
                AOVExtension.ModAction(id, true, skin.changeAnim, skin.hasDeathEffect, skin.isAwakeSkin,
                        skin.levelSFXUnlock, skin.levelVOXUnlock, skin.specialChangeOld,
                        skin.specialChangeNew,
                        skin.filenameNotMod);
                AOVExtension.ModAssetRef(skin.id, hero.specialAssetRefOld, hero.specialAssetRefNew);
                break;
            default:
                break;
        }
        if (Arrays.asList(heroHasComponent).contains(hero.id))
            AOVExtension.RemoveSkinComponent(hero.id + "1");
        System.out.println("  Dang mod icon, ten va nen xuat hien trang phuc " + skin.name + "...");
        AOVExtension.ModIcon_Name(id);
        System.out.println("  Dang mod bac trang phuc " + skin.name + "...");
        AOVExtension.ModLabel(id);
        if (skin.label != SkinLabel.A && skin.label != SkinLabel.A_HH) {
            System.out.println("  Dang mod tren cac skin bac a:");
            for (Skin skina : hero.skins) {
                if ((skina.label == SkinLabel.A || skina.label == SkinLabel.A_HH) && skina.id != skin.id) {
                    System.out.println("   + " + skina.name + "(" + skina.id + ")");
                    ModInfoAdvanced(skina.id, id);
                    RemoveSkinComponent(skina.id);
                    ModIcon_Name(skina.id, id);
                    ModLabel(skina.id, id);
                }
            }
        }
    }

    public static void MakeCredit(String[] skinids, String[] credit) {
        try {

            String inputPath = LanguagePath + "languageMap_Xls.txt";
            String outputPath1, outputPath2;

            if (App.modPackName.equals("")) {
                outputPath1 = "F:/This PC/Documents/AOV/pack_" + skinids[0]
                        + "/files/Resources/1.50.1/Languages/VN_Garena_VN/languageMap_Xls.txt";
            } else {
                outputPath1 = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Languages/VN_Garena_VN/languageMap_Xls.txt";
            }
            if (new File(outputPath1).exists())
                inputPath = outputPath1;

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVDecompress(inputBytes);

            String content = new String(outputBytes, StandardCharsets.UTF_8);
            inputPath = DatabinPath + "Actor/hero.bytes";

            if (App.modPackName.equals("")) {
                outputPath2 = "F:/This PC/Documents/AOV/pack_" + skinids[0]
                        + "/files/Resources/1.50.1/Databin/Client/Actor/hero.bytes";
            } else {
                outputPath2 = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Databin/Client/Actor/hero.bytes";
            }
            if (new File(outputPath2).exists())
                inputPath = outputPath2;

            inputBytes = DHAExtension.ReadAllBytes(inputPath);
            outputBytes = AOVDecompress(inputBytes);

            for (int i = 0; i < skinids.length; i++) {
                String heroid = skinids[i].substring(0, 3);

                int start = DHAExtension.indexOf(outputBytes, ("Hero_" + heroid + "0").getBytes()) - 4;
                int end = start - 70;
                boolean kt = false;
                while (end < outputBytes.length && !kt) {
                    if ((outputBytes[end] >= 'A' && outputBytes[end] <= 'Z')
                            || (outputBytes[end] >= '0' && outputBytes[end] <= '9')) {
                        for (int j = 0; j < 16; j++) {
                            if (!((outputBytes[end + j] >= 'A' && outputBytes[end + j] <= 'Z')
                                    || (outputBytes[end + j] >= '0' && outputBytes[end + j] <= '9'))) {
                                kt = false;
                                break;
                            }
                            if (outputBytes[end + j] >= 'A' && outputBytes[end + j] <= 'Z') {
                                kt = true;
                            }
                        }
                    }
                    end--;
                }
                end++;
                String creditCode = "MODBYAHMODAOV" + heroid;
                String baseCode = new String(Arrays.copyOfRange(outputBytes, end, end + 16));

                for (int j = 0; j < 16; j++) {
                    outputBytes[end + j] = (byte) creditCode.charAt(j);
                }
                int index = content.indexOf(baseCode);
                // System.out.println(index + ": " + baseCode + " - " + creditCode.length());
                if (index < 0)
                    return;
                content = content.substring(0, index) + creditCode + "_## = " + credit[i] + "\\n\\n"
                        + content.substring(index + creditCode.length() + 6);
            }
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath2, outputBytes);
            // DHAExtension.WriteAllBytes("D:/hero.bytes", outputBytes);

            outputBytes = content.getBytes();
            DHAExtension.WriteAllBytes("D:/languageMap_Xls.txt", outputBytes);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath1, outputBytes);
        } catch (Exception e) {
            System.out.println("Error while making credit!");
            e.printStackTrace();
        }
    }

    public static void RemoveSkinComponent(String targetId) {
        try {
            String id = targetId;

            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int start, end = 0;

            int skinidmod = (Integer.parseInt(skinId) - 1);
            byte[] skinCode = DHAExtension.toBytes(Integer.parseInt(heroId) * 100 + skinidmod);

            String inputPath = DatabinPath + "Character/ResCharacterComponent.bytes";
            String outputPath;

            if (App.modPackName.equals("")) {
                outputPath = "F:/This PC/Documents/AOV/pack_" + id
                        + "/files/Resources/1.50.1/Databin/Client/Character/ResCharacterComponent.bytes";
            } else {
                outputPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Databin/Client/Character/ResCharacterComponent.bytes";
            }
            if (new File(outputPath).exists())
                inputPath = outputPath;

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVDecompress(inputBytes);

            while ((start = DHAExtension.indexOf(outputBytes, skinCode, end) - 98) > 0) {
                while (!(DHAExtension.bytesToInt(outputBytes, start) > 100
                        && DHAExtension.bytesToInt(outputBytes, start) < 1500
                        && DHAExtension.bytesToInt(outputBytes, start) != Integer.parseInt(heroId)
                        // && DHAExtension.bytesToInt(outputBytes, start) % 256 != 0
                        // && DHAExtension.bytesToInt(outputBytes, start) != 257
                        && outputBytes[start] != 0
                        && outputBytes[start] != 1
                        && outputBytes[start] != 2
                        && outputBytes[start] != 4)) {
                    start--;
                }
                end = start + DHAExtension.bytesToInt(outputBytes, start) + 4;
                outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, start),
                        Arrays.copyOfRange(outputBytes, end, outputBytes.length));
                // System.out.println(id + ": " + start + "-" + end);
                end = start;
            }

            // DHAExtension.WriteAllBytes("D:/testlabel.bytes", outputBytes);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);
        } catch (Exception e) {
            System.out.print("Error while remove component " + targetId + " to " + targetId + ": ");
            e.printStackTrace();
        }
    }

    public static void ModInfoAdvanced(String originId, String targetId) {
        try {
            byte[] creditBytes = new byte[] { 89, 0, 0, 0, 14, 0, 0, 0, 67, 104, 97, 110, 110, 101, 108, 89, 84, 66, 63,
                    0, 0, 0, 3, 0, 0, 0, 13, 0, 0, 0, 6, 0, 0, 0, 74, 84, 80, 114, 105, 25, 0, 0, 0, 8, 0, 0, 0, 84,
                    121, 112, 101, 83, 121, 115, 116, 101, 109, 46, 83, 116, 114, 105, 110, 103, 17, 0, 0, 0, 5, 0, 0,
                    0, 86, 65, 72, 77, 79, 68, 65, 79, 86, 4, 0, 0, 0, 4, 0, 0, 0, 111, 0, 0, 0, 11, 0, 0, 0, 89, 84,
                    66, 76, 105, 110, 107, 88, 0, 0, 0, 3, 0, 0, 0, 13, 0, 0, 0, 6, 0, 0, 0, 74, 84, 80, 114, 105, 25,
                    0, 0, 0, 8, 0, 0, 0, 84, 121, 112, 101, 83, 121, 115, 116, 101, 109, 46, 83, 116, 114, 105, 110,
                    103, 42, 0, 0, 0, 5, 0, 0, 0, 86, 104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119, 46, 121, 111,
                    117, 116, 117, 98, 101, 46, 99, 111, 109, 47, 64, 65, 72, 77, 79, 68, 65, 79, 86, 4, 0, 0, 0, 4, 0,
                    0, 0 };

            String heroId = targetId.substring(0, 3);
            String skinId = targetId.substring(3, targetId.length());

            String inputZipPath = InfosParentPath;
            if (App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/pack_" + targetId
                    + "/files/Resources/1.50.1/Prefab_Characters/Actor_" + heroId + "_Infos.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/pack_" + targetId
                        + "/files/Resources/1.50.1/Prefab_Characters/Actor_" + heroId + "_Infos.pkg.bytes";
            } else if (!App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/" + App.modPackName
                    + "/files/Resources/1.50.1/Prefab_Characters/Actor_" + heroId
                    + "_Infos.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Prefab_Characters/Actor_" + heroId
                        + "_Infos.pkg.bytes";
            } else {
                inputZipPath += "Actor_" + heroId + "_Infos.pkg.bytes";
            }

            if (new File("F:/This PC/Documents/AOV/cachemod/").exists()) {
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/cachemod/");
            }
            new File("F:/This PC/Documents/AOV/cachemod/").mkdirs();
            ZipExtension.unzip(inputZipPath, "F:/This PC/Documents/AOV/cachemod/");

            String filemodName = "Prefab_Hero/";
            String heroCodeName = new File("F:/This PC/Documents/AOV/cachemod/" + filemodName).list()[0];
            filemodName = filemodName + heroCodeName + "/" + heroCodeName + "_actorinfo.bytes";

            String inputPath = "F:/This PC/Documents/AOV/cachemod/" + filemodName;
            String outputPath = inputPath;

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVDecompress(inputBytes);
            // DHAExtension.WriteAllBytes("D:/testinfos.bytes", outputBytes);
            int originIndex = 0, targetIndex = 0, nowIndex = 0;
            String previousId = "1";

            int start = 0, end = 30;
            String code = "";
            int skinPrefabGStart = DHAExtension.indexOf(outputBytes, "SkinPrefabG".getBytes()) - 8;
            end = skinPrefabGStart;
            int startOrigin = -1, endOrigin = -1;
            byte[] targetElement = null;
            while ((start = DHAExtension.indexOf(outputBytes, "VPrefab_Characters/Prefab_Hero".getBytes(),
                    end)) != -1) {
                end = start + 37 + heroCodeName.length() * 2;
                while (end < outputBytes.length && outputBytes[end] != 4) {
                    end++;
                }
                if (end == outputBytes.length) {
                    continue;
                }
                code = new String(Arrays.copyOfRange(outputBytes, start, end));
                if (code.endsWith("LOD1")) {
                    String nowId = code.split("/")[3].split("_")[0];
                    if (!previousId.equals(nowId)) {
                        previousId = nowId;
                        nowIndex++;
                    }
                    if (code.contains(originId + "_")) {
                        originIndex = nowIndex;
                        startOrigin = start - 249;
                        // System.out.println("Start " + startOrigin);
                        endOrigin = startOrigin + DHAExtension
                                .bytesToInt(new byte[] { outputBytes[startOrigin], outputBytes[startOrigin + 1],
                                        outputBytes[startOrigin + 2], outputBytes[startOrigin + 3] });
                        if (targetElement != null)
                            break;
                    } else if (code.contains(targetId + "_")) {
                        targetIndex = nowIndex;
                        int start2 = start - 249;
                        end = start2 + DHAExtension
                                .bytesToInt(new byte[] { outputBytes[start2], outputBytes[start2 + 1],
                                        outputBytes[start2 + 2], outputBytes[start2 + 3] });
                        targetElement = Arrays.copyOfRange(outputBytes, start2, end);
                        if (startOrigin != -1 && endOrigin != -1)
                            break;
                    }
                }
            }
            if (new File(SpecialPath + "infos/" + targetId + ".bytes").exists()) {
                targetElement = DHAExtension.ReadAllBytes(SpecialPath + "infos/" + targetId + ".bytes");
            }
            if (originId.equals(heroId + "1")) {
                int targetSize = targetElement[92];
                targetElement = Arrays.copyOfRange(targetElement, 96, targetElement.length);
                String[] artList = new String[] { "ArtSkinPrefabLOD", "ArtSkinPrefabLODEx",
                        "ArtSkinLobbyShowLOD" };
                for (String art : artList) {
                    start = DHAExtension.indexOf(targetElement, art.getBytes());
                    if (start < 0)
                        continue;
                    byte[] bytes = DHAExtension.toBytes(DHAExtension.bytesToInt(targetElement, start - 8) - 4);
                    for (int i = 0; i < 4; i++) {
                        targetElement[start - 8 + i] = bytes[i];
                    }
                    bytes = DHAExtension.toBytes(DHAExtension.bytesToInt(targetElement, start - 4) - 4);
                    for (int i = 0; i < 4; i++) {
                        targetElement[start - 4 + i] = bytes[i];
                    }
                    targetElement = DHAExtension.mergeBytes(Arrays.copyOfRange(targetElement, 0, start + 3),
                            Arrays.copyOfRange(targetElement, start + 7, targetElement.length));
                }
                int size = 0;
                start = 92 + DHAExtension.bytesToInt(outputBytes, 92);
                int originStart = start;
                int originEnd = skinPrefabGStart;
                while (start < skinPrefabGStart) {
                    start = start + DHAExtension.bytesToInt(outputBytes, start);
                    size++;
                }
                byte[] bytes = DHAExtension.toBytes(DHAExtension.bytesToInt(outputBytes, 88) + targetSize - size + 2);
                for (int i = 0; i < 4; i++) {
                    outputBytes[88 + i] = bytes[i];
                }
                targetElement = DHAExtension.mergeBytes(creditBytes, targetElement);
                outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, originStart),
                        targetElement, Arrays.copyOfRange(outputBytes, originEnd, outputBytes.length));
            } else {
                byte[] bytes = DHAExtension.toBytes(
                        DHAExtension.bytesToInt(outputBytes[skinPrefabGStart], outputBytes[skinPrefabGStart + 1],
                                outputBytes[skinPrefabGStart + 2], outputBytes[skinPrefabGStart + 3])
                                + targetElement.length - (endOrigin - startOrigin));
                outputBytes[skinPrefabGStart] = bytes[0];
                outputBytes[skinPrefabGStart + 1] = bytes[1];
                outputBytes[skinPrefabGStart + 2] = bytes[2];
                outputBytes[skinPrefabGStart + 3] = bytes[3];

                skinPrefabGStart = skinPrefabGStart + 93;
                bytes = DHAExtension.toBytes(
                        DHAExtension.bytesToInt(outputBytes[skinPrefabGStart], outputBytes[skinPrefabGStart + 1],
                                outputBytes[skinPrefabGStart + 2], outputBytes[skinPrefabGStart + 3])
                                + targetElement.length - (endOrigin - startOrigin));
                outputBytes[skinPrefabGStart] = bytes[0];
                outputBytes[skinPrefabGStart + 1] = bytes[1];
                outputBytes[skinPrefabGStart + 2] = bytes[2];
                outputBytes[skinPrefabGStart + 3] = bytes[3];

                outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, startOrigin),
                        targetElement, Arrays.copyOfRange(outputBytes, endOrigin, outputBytes.length));
            }

            // DHAExtension.WriteAllBytes("F:\\This PC\\Documents\\AOV\\New folder\\" +
            // targetId + ".bytes", outputBytes);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);

            filemodName = "Prefab_Hero/";
            heroCodeName = new File("F:/This PC/Documents/AOV/cachemod/" + filemodName).list()[0];
            filemodName = filemodName + heroCodeName + "/" + heroCodeName + "_trap_actorinfo.bytes";

            inputPath = "F:/This PC/Documents/AOV/cachemod/" + filemodName;
            skinId = (Integer.parseInt(skinId) - 1) + "";
            String skillId = heroId + (skinId.length() == 2 ? skinId : "0" + skinId);
            if (new File(inputPath).exists()) {
                // System.out.println(targetId + " has trap!");
                outputPath = inputPath;

                inputBytes = DHAExtension.ReadAllBytes(inputPath);
                outputBytes = AOVDecompress(inputBytes);
                end = 30;
                if (originId.equals(heroId + "1")) {
                    int originPrefabLODstart, originPrefabLODend;
                    byte[] targetPrefabLOD = null;

                    originPrefabLODstart = DHAExtension.indexOf(outputBytes, "ArtPrefabLOD".getBytes()) - 8;
                    originPrefabLODend = originPrefabLODstart + DHAExtension.bytesToInt(
                            new byte[] { outputBytes[originPrefabLODstart], outputBytes[originPrefabLODstart + 1],
                                    outputBytes[originPrefabLODstart + 2], outputBytes[originPrefabLODstart + 3] });
                    while ((start = DHAExtension.indexOf(outputBytes,
                            "VPrefab_Skill_Effects/Hero_Skill_Effects/".getBytes(),
                            end)) != -1) {
                        end = start + 37 + heroCodeName.length() * 2;
                        while (end < outputBytes.length && outputBytes[end] != 4) {
                            end++;
                        }
                        code = new String(Arrays.copyOfRange(outputBytes, start, end));
                        if (code.contains(skillId)) {
                            if (code.endsWith("_LOD1")) {
                                start = start - 153;
                                end = start + DHAExtension
                                        .bytesToInt(new byte[] { outputBytes[start], outputBytes[start + 1],
                                                outputBytes[start + 2], outputBytes[start + 3] });
                                targetPrefabLOD = Arrays.copyOfRange(outputBytes, start, end);
                                byte[] bytes = DHAExtension
                                        .toBytes(DHAExtension.bytesToInt(targetPrefabLOD[0], targetPrefabLOD[1],
                                                targetPrefabLOD[2], targetPrefabLOD[3]) - 4);
                                targetPrefabLOD[0] = bytes[0];
                                targetPrefabLOD[1] = bytes[1];
                                targetPrefabLOD[2] = bytes[2];
                                targetPrefabLOD[3] = bytes[3];
                                targetPrefabLOD[4] -= 4;
                                targetPrefabLOD = DHAExtension.mergeBytes(Arrays.copyOfRange(targetPrefabLOD, 0, 11),
                                        Arrays.copyOfRange(targetPrefabLOD, 15, targetPrefabLOD.length));
                            }
                        } else if (targetPrefabLOD != null) {
                            break;
                        }
                    }

                    outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, originPrefabLODstart),
                            targetPrefabLOD, Arrays.copyOfRange(outputBytes, originPrefabLODend, outputBytes.length));
                } else {
                    code = "";
                    skinPrefabGStart = DHAExtension.indexOf(outputBytes, "SkinPrefabG".getBytes());
                    end = skinPrefabGStart + 93;
                    startOrigin = -1;
                    endOrigin = -1;
                    targetElement = null;

                    nowIndex = 0;
                    while ((start = DHAExtension.indexOf(outputBytes,
                            "TypeAssets.Scripts.GameLogic.SkinElement".getBytes(),
                            end)) != -1) {
                        nowIndex++;
                        end = start + 1;
                        // System.out.println(nowIndex + ": " + start + " - " + originIndex +
                        // "-"+targetIndex);
                        if (nowIndex == originIndex) {
                            startOrigin = start - 44;
                            // System.out.println("Start " + startOrigin);
                            endOrigin = startOrigin + DHAExtension
                                    .bytesToInt(new byte[] { outputBytes[startOrigin], outputBytes[startOrigin + 1],
                                            outputBytes[startOrigin + 2], outputBytes[startOrigin + 3] });
                            if (targetElement != null)
                                break;
                        } else if (nowIndex == targetIndex) {
                            int start2 = start - 44;
                            end = start2 + DHAExtension
                                    .bytesToInt(new byte[] { outputBytes[start2], outputBytes[start2 + 1],
                                            outputBytes[start2 + 2], outputBytes[start2 + 3] });
                            targetElement = Arrays.copyOfRange(outputBytes, start2, end);
                            if (startOrigin != -1 && endOrigin != -1)
                                break;
                        }
                    }

                    skinPrefabGStart = skinPrefabGStart - 8;
                    byte[] bytes = DHAExtension.toBytes(
                            DHAExtension.bytesToInt(outputBytes[skinPrefabGStart], outputBytes[skinPrefabGStart + 1],
                                    outputBytes[skinPrefabGStart + 2], outputBytes[skinPrefabGStart + 3])
                                    + targetElement.length - (endOrigin - startOrigin));
                    outputBytes[skinPrefabGStart] = bytes[0];
                    outputBytes[skinPrefabGStart + 1] = bytes[1];
                    outputBytes[skinPrefabGStart + 2] = bytes[2];
                    outputBytes[skinPrefabGStart + 3] = bytes[3];

                    skinPrefabGStart = skinPrefabGStart + 93;
                    bytes = DHAExtension.toBytes(
                            DHAExtension.bytesToInt(outputBytes[skinPrefabGStart], outputBytes[skinPrefabGStart + 1],
                                    outputBytes[skinPrefabGStart + 2], outputBytes[skinPrefabGStart + 3])
                                    + targetElement.length - (endOrigin - startOrigin));
                    outputBytes[skinPrefabGStart] = bytes[0];
                    outputBytes[skinPrefabGStart + 1] = bytes[1];
                    outputBytes[skinPrefabGStart + 2] = bytes[2];
                    outputBytes[skinPrefabGStart + 3] = bytes[3];

                    outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, startOrigin),
                            targetElement, Arrays.copyOfRange(outputBytes, endOrigin, outputBytes.length));
                }

                // DHAExtension.WriteAllBytes("D:/testtrap.bytes", outputBytes);
                outputBytes = AOVCompress(outputBytes);
                DHAExtension.WriteAllBytes(outputPath, outputBytes);
            }

            if (App.modPackName.equals("")) {
                ZipExtension.zipDir("F:/This PC/Documents/AOV/cachemod/" +
                        filemodName.split("/")[0],
                        "F:/This PC/Documents/AOV/pack_" + targetId +
                                "/files/Resources/1.50.1/Prefab_Characters/Actor_"
                                + heroId
                                + "_Infos.pkg.bytes");
            } else {
                ZipExtension.zipDir("F:/This PC/Documents/AOV/cachemod/" +
                        filemodName.split("/")[0],
                        "F:/This PC/Documents/AOV/" + App.modPackName
                                + "/files/Resources/1.50.1/Prefab_Characters/Actor_" + heroId
                                + "_Infos.pkg.bytes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModOrgan(String originId, String targetId) {
        try {
            List<String> skinHasOrgan = new ArrayList<>();
            skinHasOrgan.add("1118");
            skinHasOrgan.add("14112");
            skinHasOrgan.add("15010");
            skinHasOrgan.add("5019");
            if (!skinHasOrgan.contains(targetId))
                return;
            String id = targetId;

            originId = originId.substring(0, 3) + (Integer.parseInt(originId.substring(3, originId.length())) - 1) + "";

            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());

            String inputPath = DatabinPath + "Actor/heroSkin.bytes";

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            int start;
            byte[] outputBytes = AOVDecompress(inputBytes);

            byte[] oldCode = new byte[2], newCode = new byte[2];
            String skinidmod = (Integer.parseInt(skinId) - 1) + "";
            start = DHAExtension.indexOf(outputBytes,
                    DHAExtension.mergeBytes(new byte[] { '3', '0' }, heroId.getBytes(), skinidmod.getBytes())) - 68;
            int kt2 = DHAExtension.indexOf(outputBytes,
                    DHAExtension.mergeBytes(new byte[] { '3', '0' }, heroId.getBytes(), skinidmod.getBytes(),
                            new byte[] { 0 }),
                    start + 100) - 68;
            if (kt2 > 0 && (DHAExtension.indexOf(outputBytes,
                    DHAExtension.mergeBytes(
                            ("Share_" + heroId + (skinidmod.length() == 1 ? "0" + skinidmod : skinidmod)).getBytes()),
                    start) == kt2 + 204))
                start = kt2;
            newCode[0] = outputBytes[start + 4];
            newCode[1] = outputBytes[start + 5];
            start = DHAExtension.indexOf(outputBytes,
                    DHAExtension.mergeBytes(new byte[] { '3', '0' }, originId.getBytes())) - 68;
            kt2 = DHAExtension.indexOf(outputBytes,
                    DHAExtension.mergeBytes(new byte[] { '3', '0' }, originId.getBytes(), new byte[] { 0 }),
                    start + 100) - 68;
            if (kt2 > 0 && (DHAExtension.indexOf(outputBytes,
                    DHAExtension.mergeBytes(("Share_" + originId.substring(0, 3)
                            + (originId.length() == 4 ? "0" + originId.charAt(3) : originId.substring(3))).getBytes()),
                    start) == kt2 + 204))
                start = kt2;
            oldCode[0] = outputBytes[start + 4];
            oldCode[1] = outputBytes[start + 5];

            inputPath = DatabinPath + "Actor/organSkin.bytes";
            String outputPath;

            if (App.modPackName.equals("")) {
                outputPath = "F:/This PC/Documents/AOV/pack_" + id
                        + "/files/Resources/1.50.1/Databin/Client/Actor/organSkin.bytes";
            } else {
                outputPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Databin/Client/Actor/organSkin.bytes";
            }
            if (new File(outputPath).exists())
                inputPath = outputPath;

            inputBytes = DHAExtension.ReadAllBytes(inputPath);
            outputBytes = AOVDecompress(inputBytes);

            start = 0;
            while ((start = DHAExtension.indexOf(outputBytes, newCode, start)) > 0) {
                outputBytes[start] = oldCode[0];
                outputBytes[start + 1] = oldCode[1];
            }

            // DHAExtension.WriteAllBytes("D:/testorgan.bytes", outputBytes);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);
        } catch (Exception e) {
            System.out.print("Error while mod organ " + originId + " to " + targetId + ": ");
            e.printStackTrace();
        }
    }

    public static void ModAction(String id) {
        ModAction(id, false);
    }

    public static void ModAction(String id, boolean haveSound) {
        ModAction(id, haveSound, false, false, false, 0, 0, null, null, null);
    }

    public static void ModAction(String id, boolean haveSound, boolean changeAnim) {
        ModAction(id, haveSound, changeAnim, false, false, 0, 0, null, null, null);
    }

    public static void ModAction(String id, boolean haveSound, boolean changeAnim, boolean hasDeathEffect) {
        ModAction(id, haveSound, changeAnim, hasDeathEffect, false, 0, 0, null, null, null);
    }

    public static void ModAction(String id, boolean haveSound, boolean changeAnim, boolean isAwakeSkin,
            int levelSFXUnlock, int levelVOXUnlock) {
        ModAction(id, haveSound, changeAnim, false, isAwakeSkin, levelSFXUnlock, levelVOXUnlock, null, null, null);
    }

    public static void ModAction(String id, boolean haveSound, boolean changeAnim, boolean hasDeathEffect,
            boolean isAwakeSkin, int levelSFXUnlock, int levelVOXUnlock,
            String[] specialOld,
            String[] specialNew, String[] filenameNotMods) {
        try {
            String inputZipPath = ActionsParentPath;

            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            {
                File[] children = new File(ActionsParentPath).listFiles();
                for (File child : children) {
                    if (child.getName().contains(heroId) && child.getName().endsWith("_Actions.pkg.bytes")) {
                        inputZipPath += child.getName();
                    }
                }
            }

            if (new File("F:/This PC/Documents/AOV/cachemod/").exists()) {
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/cachemod/");
            }
            new File("F:/This PC/Documents/AOV/cachemod/").mkdirs();
            ZipExtension.unzip(inputZipPath, "F:/This PC/Documents/AOV/cachemod/");

            String filemodName = "";
            for (int i = 0; i < new File("F:/This PC/Documents/AOV/cachemod/" + filemodName).list().length; i++) {
                String filePath = new File("F:/This PC/Documents/AOV/cachemod/" + filemodName).list()[i];
                if (new File("F:/This PC/Documents/AOV/cachemod/" + filemodName + filePath).isDirectory()) {
                    filemodName += filePath + "/";
                    i = -1;
                } else {
                    break;
                }
            }
            String heroCodeName = filemodName.split("/")[0];

            List<String> linesDance = new ArrayList<>();
            for (String filename : new File("F:/This PC/Documents/AOV/cachemod/" + filemodName).list()) {
                if (filename.toLowerCase().contains("back") || filename.toLowerCase().contains("born")
                        || (filenameNotMods != null
                                && Arrays.asList(filenameNotMods).contains(filename.toLowerCase()))
                        || (filename.toLowerCase().contains("death") && !hasDeathEffect)) {
                    // System.out.println("skiped " + filename);
                    continue;
                }
                String inputPath = "F:/This PC/Documents/AOV/cachemod/" + filemodName + filename;
                String outputPath = inputPath;

                byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
                byte[] outputBytes = AOVDecompress(inputBytes);
                if (outputBytes == null)
                    continue;

                String content = new String(outputBytes, StandardCharsets.UTF_8);

                String[] lines = content.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i].toLowerCase()
                            .contains("prefab_skill_effects/hero_skill_effects/" + heroCodeName.toLowerCase())) {
                        String[] split = lines[i].split("\"");
                        String code = String.join("/", Arrays.copyOfRange(split[3].split("/"), 0, 3)) + "/"
                                + split[3].split("/")[split[3].split("/").length - 1];
                        String newCode;
                        if (!isAwakeSkin) {
                            newCode = String.join("/", Arrays.copyOfRange(split[3].split("/"), 0, 3)) + "/" + heroId
                                    + (skin < 10 ? "0" + skin : skin) + "/"
                                    + split[3].split("/")[split[3].split("/").length - 1];
                        } else {
                            newCode = "Prefab_Skill_Effects/Component_Effects/" + heroId
                                    + (skin < 10 ? "0" + skin : skin) + "/" + heroId + (skin < 10 ? "0" + skin : skin)
                                    + "_5/" + split[3].split("/")[split[3].split("/").length - 1];
                        }
                        // System.out.println(newCode);
                        lines[i] = lines[i].replace(code, newCode);
                    } else if (lines[i].contains("bAllowEmptyEffect")) {
                        lines[i] = lines[i].replaceAll("(?i)value=\"true\"", "value=\"false\"");
                    } else if (lines[i].contains("CheckSkinIdTick")) {
                        if (skinNotSwapCheck.contains(id)) {
                            // System.out.println("can't swap check skin id " + id);
                            continue;
                        }
                        int k;
                        boolean kt = false;
                        for (k = i + 1; k < lines.length; k++) {
                            if (lines[k].contains("name=\"skinId\"")) {
                                if (lines[k].contains(heroId + (skin < 10 ? "0" + skin : skin))) {
                                    kt = true;
                                }
                                break;
                            }
                        }
                        if (kt) {
                            lines[i] = lines[i].replaceAll("(?i)CheckSkinIdTick", "CheckHeroIdTick");
                            lines[i + 1] = lines[i + 1].replaceAll("(?i)CheckSkinIdTick", "CheckHeroIdTick");
                            lines[k] = lines[k].replaceAll("(?i)skinId", "heroId")
                                    .replaceAll(heroId + (skin < 10 ? "0" + skin : skin), heroId);
                        }
                        i = k;
                    } else if (changeAnim && lines[i].contains("clipName")) {
                        String[] split = lines[i].split("\"");
                        String code = "";
                        for (int j = 0; j < split.length; j++) {
                            if (split[j].endsWith("value=")) {
                                split[j + 1] = heroId + (skin < 10 ? "0" + skin : skin) + "/" + split[j + 1];
                                code = split[j + 1];
                                break;
                            }
                        }
                        lines[i] = String.join("\"", split);
                        int startAdd = linesDance.size();
                        int d = i;
                        // if (!String.join("\n", linesDance).contains(code.split("_")[0])) {
                        while (!lines[d + 1].contains("<Track")) {
                            if (!lines[d].contains("<Condition"))
                                linesDance.add(startAdd, lines[d]);// .replace(code, code.split("_")[0]));
                            d--;
                        }
                        linesDance.add("      </Event>");
                        linesDance.add("    </Track>");
                        // }
                    } else if (haveSound && lines[i].contains("eventName=\"PlayHeroSoundTick\"")) {
                        while (!lines[i].contains("<String name=\"eventName\""))
                            i++;
                        String[] split = lines[i].split("\"");
                        if (split[3].toLowerCase().contains("_skin"))
                            continue;
                        if (!isAwakeSkin) {
                            split[3] += "_Skin" + (Integer.parseInt(skinId) - 1);
                        } else {
                            if (split[3].toLowerCase().contains("_vo") || split[3].toLowerCase().contains("voice")) {
                                split[3] += "_Skin" + (Integer.parseInt(skinId) - 1) + "_AW" + levelVOXUnlock;
                            } else {
                                split[3] += "_Skin" + (Integer.parseInt(skinId) - 1) + "_AW" + levelSFXUnlock;
                            }
                        }
                        lines[i] = String.join("\"", split);
                    }
                }
                content = String.join("\n", lines);//.replaceAll("\n", "\n");
                if (specialOld != null && specialNew != null && specialOld.length == specialNew.length) {
                    for (int i = 0; i < specialOld.length; i++) {
                        content = content.replaceAll("(?i)" + specialOld[i], specialNew[i]);
                    }
                }

                outputBytes = content.getBytes(StandardCharsets.UTF_8);
                outputBytes = AOVCompress(outputBytes);
                DHAExtension.WriteAllBytes(outputPath, outputBytes);
            }
            if (App.modPackName.equals("")) {
                ZipExtension.zipDir("F:/This PC/Documents/AOV/cachemod/" + filemodName.split("/")[0],
                        "F:/This PC/Documents/AOV/pack_" + id
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/Actor_"
                                + heroId + "_Actions.pkg.bytes");
            } else {
                ZipExtension.zipDir("F:/This PC/Documents/AOV/cachemod/" + filemodName.split("/")[0],
                        "F:/This PC/Documents/AOV/" + App.modPackName
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/Actor_"
                                + heroId + "_Actions.pkg.bytes");
            }
            ModLiteBullet(id);
            ModSkillMark(id);
            if (haveSound)
                ModSoundNew(heroId + "1", id);

            if (!changeAnim)
                return;

            inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
            if (!App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/" + App.modPackName
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
            } else if (App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/pack_" + id
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/pack_" + id
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
            }

            if (new File("F:/This PC/Documents/AOV/cachemod/").exists()) {
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/cachemod/");
            }
            new File("F:/This PC/Documents/AOV/cachemod/").mkdirs();
            ZipExtension.unzip(inputZipPath, "F:/This PC/Documents/AOV/cachemod/");
            filemodName = "commonresource/Dance.xml";
            String inputPath = "F:/This PC/Documents/AOV/cachemod/" + filemodName;
            String outputPath = inputPath;

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVDecompress(inputBytes);
            if (outputBytes == null)
                return;

            String content = new String(outputBytes, StandardCharsets.UTF_8);

            content = content.replaceFirst("(?i)  </Action>", String.join("\n", linesDance) + "  </Action>");

            outputBytes = content.getBytes(StandardCharsets.UTF_8);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);

            String[] subDir = new File("F:/This PC/Documents/AOV/cachemod/").list();

            for (int i = 0; i < subDir.length; i++) {
                subDir[i] = "F:/This PC/Documents/AOV/cachemod/" + subDir[i];
            }

            if (App.modPackName.equals(""))
                ZipExtension.zipDir(subDir,
                        "F:/This PC/Documents/AOV/pack_" + id
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            else {
                ZipExtension.zipDir(subDir,
                        "F:/This PC/Documents/AOV/" + App.modPackName
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModActionMulti(String[] originIds, String[] targetIds, boolean[] haveSounds,
            boolean[] changeAnims) {
        try {
            boolean hasChangeAnim = false;
            for (int i = 0; i < changeAnims.length; i++) {
                if (changeAnims[i]) {
                    hasChangeAnim = true;
                    break;
                }
            }

            String inputZipPath = ActionsParentPath;

            String heroId = originIds[0].substring(0, 3);
            {
                File[] children = new File(ActionsParentPath).listFiles();
                for (File child : children) {
                    if (child.getName().contains(heroId) && child.getName().endsWith("_Actions.pkg.bytes")) {
                        inputZipPath += child.getName();
                    }
                }
            }

            if (new File("F:/This PC/Documents/AOV/cachemod/").exists()) {
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/cachemod/");
            }
            new File("F:/This PC/Documents/AOV/cachemod/").mkdirs();
            ZipExtension.unzip(inputZipPath, "F:/This PC/Documents/AOV/cachemod/");

            String filemodName = "";
            for (int i = 0; i < new File("F:/This PC/Documents/AOV/cachemod/" + filemodName).list().length; i++) {
                String filePath = new File("F:/This PC/Documents/AOV/cachemod/" + filemodName).list()[i];
                if (new File("F:/This PC/Documents/AOV/cachemod/" + filemodName + filePath).isDirectory()) {
                    filemodName += filePath + "/";
                    i = -1;
                } else {
                    break;
                }
            }
            String heroCodeName = filemodName.split("/")[0];
            List<String> linesDance = new ArrayList<>();

            for (String filename : new File("F:/This PC/Documents/AOV/cachemod/" + filemodName).list()) {
                if (filename.contains("back") || filename.contains("born"))
                    continue;
                String inputPath = "F:/This PC/Documents/AOV/cachemod/" + filemodName + filename;
                String outputPath = inputPath;

                byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
                byte[] outputBytes = AOVDecompress(inputBytes);
                if (outputBytes == null)
                    continue;

                String content = new String(outputBytes, StandardCharsets.UTF_8);
                List<String> lines = new ArrayList<>(Arrays.asList(content.split("\n")));
                List<String> baseTrackEffectLines = new ArrayList<>();
                List<String> baseTrackSoundLines = new ArrayList<>();
                List<String> baseClipAnimLines = new ArrayList<>();
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).toLowerCase().contains("prefab_skill_effects/hero_skill_effects/")) {
                        int addIndex = baseTrackEffectLines.size();
                        baseTrackEffectLines.add(addIndex, lines.get(i));
                        int j = i - 1;
                        while (!lines.get(j + 1).contains("<Track ")) {
                            baseTrackEffectLines.add(addIndex, lines.get(j));
                            j--;
                        }
                        j = i + 1;
                        while (!lines.get(j - 1).contains("</Track")) {
                            baseTrackEffectLines.add(lines.get(j));
                            j++;
                        }
                        int startEvent = i - 1;
                        while (!lines.get(startEvent).contains("<Event")) {
                            startEvent--;
                        }
                        int endEvent = i + 1;
                        while (!lines.get(endEvent).contains("</Event")) {
                            endEvent++;
                        }
                        for (int k = startEvent + 1; k < endEvent; k++) {
                            lines.remove(startEvent + 1);
                        }
                        i = startEvent;
                    } else if (lines.get(i).contains("eventName=\"PlayHeroSoundTick\"")) {
                        int addIndex = baseTrackSoundLines.size();
                        baseTrackSoundLines.add(addIndex, lines.get(i));
                        int j = i - 1;
                        while (!lines.get(j + 1).contains("<Track ")) {
                            baseTrackSoundLines.add(addIndex, lines.get(j));
                            j--;
                        }
                        int start = j + 1;
                        j = i + 1;
                        while (!lines.get(j - 1).contains("</Track")) {
                            baseTrackSoundLines.add(lines.get(j));
                            j++;
                        }
                        for (int k = start; k < j; k++) {
                            // System.out.println(new File(inputPath).getName() + ": " + lines.get(start));
                            lines.remove(start);
                        }
                        i = start;
                    } else if (hasChangeAnim && lines.get(i).contains("clipName")) {
                        int addIndex = baseClipAnimLines.size();
                        baseClipAnimLines.add(addIndex, lines.get(i));
                        int j = i - 1;
                        while (!lines.get(j + 1).contains("<Track ")) {
                            baseClipAnimLines.add(addIndex, lines.get(j));
                            j--;
                        }
                        int start = j + 1;
                        j = i + 1;
                        while (!lines.get(j - 1).contains("</Track")) {
                            baseClipAnimLines.add(lines.get(j));
                            j++;
                        }
                        // for (int k = start; k < j; k++) {
                        // lines.remove(start);
                        // }
                        i = j;
                        // i = start;
                    }
                }

                List<String> checkSkinIdLines = new ArrayList<>();
                List<String> trackEffectLines = new ArrayList<>();
                List<String> trackSoundLines = new ArrayList<>();
                List<String> clipAnimLines = new ArrayList<>();
                int trackCount = 0;
                for (int j = 0; j < lines.size(); j++) {
                    if (lines.get(j).contains("<Track ")) {
                        trackCount++;
                    }
                }
                for (int f = 0; f < originIds.length; f++) {
                    String originId = originIds[f],
                            id = targetIds[f];
                    boolean haveSound = haveSounds[f],
                            changeAnim = changeAnims[f];
                    String skinId = id.substring(3, id.length());
                    int skin = Integer.parseInt(skinId) - 1;
                    String skinIdCode = heroId + (skin < 10 ? "0" + skin : skin);
                    int origin = Integer.parseInt(originId.substring(3)) - 1;
                    String originIdCode = heroId + (origin < 10 ? "0" + origin : origin);

                    for (int i = 0; i < lines.size(); i++) {
                        if (lines.get(i).contains("CheckSkinIdTick") && !skinNotSwapCheck.contains(id)) {
                            int k;
                            boolean kt = false;
                            for (k = i + 1; k < lines.size(); k++) {
                                if (lines.get(k).contains("name=\"skinId\"")) {
                                    if (lines.get(k).contains(skinIdCode)) {
                                        kt = true;
                                    }
                                    break;
                                }
                            }
                            if (kt) {
                                lines.set(k, lines.get(k)
                                        .replaceAll(skinIdCode, originIdCode));
                            }
                            i = k;
                        }
                    }

                    if (hasChangeAnim) {
                        if (changeAnim) {
                            for (int j = 0; j < baseClipAnimLines.size(); j++) {
                                if (baseClipAnimLines.get(j).contains("<Track ")) {
                                    clipAnimLines.add(baseClipAnimLines.get(j));
                                    clipAnimLines
                                            .add("      <Condition id=\"" + (trackCount + f) + "\" guid=\"ModByAHMODAOV"
                                                    + id + "\" status=\"true\"/>");
                                } else if (baseClipAnimLines.get(j).contains("clipName")) {
                                    String[] split = baseClipAnimLines.get(j).split("\"");
                                    String code = "";
                                    for (int i = 0; i < split.length; i++) {
                                        if (split[i].endsWith("value=")) {
                                            split[i + 1] = skinIdCode + "/" + split[i + 1];
                                            code = split[i + 1];
                                            break;
                                        }
                                    }
                                    clipAnimLines.add(String.join("\"", split));
                                    int startAdd = linesDance.size();
                                    int d = j;
                                    if (!String.join("\n", linesDance).contains(code.split("_")[0])) {
                                        while (!baseClipAnimLines.get(d + 1).contains("<Track")) {
                                            if (!baseClipAnimLines.get(d).contains("<Condition"))
                                                linesDance.add(startAdd,
                                                        baseClipAnimLines.get(d).replace(code.split("/")[1],
                                                                code.split("_")[0]));
                                            d--;
                                        }
                                        linesDance.add("      </Event>");
                                        linesDance.add("    </Track>");
                                    }
                                } else {
                                    clipAnimLines.add(baseClipAnimLines.get(j));
                                }
                            }
                        } else {
                            // for (int j = 0; j < baseClipAnimLines.size(); j++) {
                            // if (baseClipAnimLines.get(j).contains("<Track ")) {
                            // clipAnimLines.add(baseClipAnimLines.get(j));
                            // clipAnimLines
                            // .add(" <Condition id=\"" + (trackCount + f) + "\" guid=\"ModByAHMODAOV"
                            // + id + "\" status=\"true\"/>");
                            // }else{
                            // clipAnimLines.add(baseClipAnimLines.get(j));
                            // }
                            // }
                        }
                    }

                    checkSkinIdLines.add(
                            "    <Track trackName=\"CheckSkinIdTick0\" eventType=\"CheckSkinIdTick\" guid=\"ModByAHMODAOV"
                                    + id
                                    + "\" enabled=\"true\" useRefParam=\"false\" refParamName=\"\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">"
                                    +
                                    "\n      <Event eventName=\"CheckSkinIdTick\" time=\"0.000\" isDuration=\"false\" guid=\"b1f22478-174b-4906-a66d-7396859623b8\">"
                                    +
                                    "\n        <TemplateObject name=\"targetId\" id=\"0\" objectName=\"self\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />"
                                    +
                                    "\n        <int name=\"skinId\" value=\"" + originIdCode
                                    + "\" refParamName=\"\" useRefParam=\"false\" />" +
                                    "\n        <bool name=\"bSkipLogicCheck\" value=\"true\" refParamName=\"\" useRefParam=\"false\" />"
                                    +
                                    "\n      </Event>" +
                                    "\n    </Track>");
                    for (int j = 0; j < baseTrackEffectLines.size(); j++) {
                        if (baseTrackEffectLines.get(j).toLowerCase()
                                .contains("prefab_skill_effects/hero_skill_effects/")) {
                            String[] split = baseTrackEffectLines.get(j).split("/");
                            trackEffectLines.add(baseTrackEffectLines.get(j).replaceAll(
                                    "(?i)prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/"
                                            + split[split.length - 2].split("\"")[0],
                                    "prefab_skill_effects/hero_skill_effects/" + heroCodeName + "/" + skinIdCode + "/"
                                            + split[split.length - 2].split("\"")[0]));
                        } else if (baseTrackEffectLines.get(j).contains("<Track ")) {
                            trackEffectLines.add(baseTrackEffectLines.get(j));
                            trackEffectLines.add("      <Condition id=\"" + (trackCount + f) + "\" guid=\"ModByAHMODAOV"
                                    + id + "\" status=\"true\"/>");
                        } else {
                            trackEffectLines.add(baseTrackEffectLines.get(j));
                        }
                    }
                    for (int j = 0; j < baseTrackSoundLines.size(); j++) {
                        if (baseTrackSoundLines.get(j).contains("<Track ")) {
                            trackSoundLines.add(baseTrackSoundLines.get(j));
                            trackSoundLines
                                    .add("      <Condition id=\"" + (trackCount + f) + "\" guid=\"ModByAHMODAOV"
                                            + id + "\" status=\"true\"/>");
                        } else if (baseTrackSoundLines.get(j).contains("<String name=\"eventName\"")) {
                            String[] split = baseTrackSoundLines.get(j).split("\"");
                            split[3] += "_Skin" + (Integer.parseInt(skinId) - 1);
                            trackSoundLines.add(String.join("\"", split));
                        } else {
                            trackSoundLines.add(baseTrackSoundLines.get(j));
                        }
                    }
                }

                content = String.join("\n", lines);

                // content = content.replaceFirst("(?i) <Track", String.join("\n",
                // checkSkinIdLines) + "\n <Track");

                if (trackEffectLines.size() == 0 && trackSoundLines.size() == 0 && clipAnimLines.size()==0)
                    continue;
                content = content.replaceFirst("(?i)  </Action", String.join("\n", checkSkinIdLines) + "\n  </Action");
                if (trackEffectLines.size() != 0) {
                    content = content.replace("  </Action", String.join("\n", trackEffectLines) + "\n  </Action");
                }
                if (trackSoundLines.size() != 0) {
                    content = content.replace(" </Action", String.join("\n", trackSoundLines) +
                            "\n </Action");
                }
                if (hasChangeAnim && clipAnimLines.size() != 0) {
                    content = content.replace(" </Action", String.join("\n", clipAnimLines) +
                            "\n </Action");
                }
                outputBytes = content.getBytes(StandardCharsets.UTF_8);
                outputBytes = AOVCompress(outputBytes);
                DHAExtension.WriteAllBytes(outputPath, outputBytes);
            }
            // if (App.modPackName.equals("")) {
            ZipExtension.zipDir("F:/This PC/Documents/AOV/cachemod/" +
                    filemodName.split("/")[0],
                    "F:/This PC/Documents/AOV/multipack_" + heroId
                            + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/Actor_"
                            + heroId + "_Actions.pkg.bytes");
            // } else {
            // ZipExtension.zipDir("F:/This PC/Documents/AOV/cachemod/" +
            // filemodName.split("/")[0],
            // "F:/This PC/Documents/AOV/" + App.modPackName
            // + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/Actor_"
            // + heroId + "_Actions.pkg.bytes");
            // }

            if (!hasChangeAnim)
                return;

            inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";

            if (new File("F:/This PC/Documents/AOV/cachemod/").exists()) {
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/cachemod/");
            }
            new File("F:/This PC/Documents/AOV/cachemod/").mkdirs();
            ZipExtension.unzip(inputZipPath, "F:/This PC/Documents/AOV/cachemod/");
            filemodName = "commonresource/Dance.xml";
            String inputPath = "F:/This PC/Documents/AOV/cachemod/" + filemodName;
            String outputPath = inputPath;

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVDecompress(inputBytes);
            if (outputBytes == null)
                return;

            String content = new String(outputBytes, StandardCharsets.UTF_8);

            content = content.replaceFirst("(?i)  </Action>", String.join("\n", linesDance) + "  </Action>");

            outputBytes = content.getBytes(StandardCharsets.UTF_8);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);

            String[] subDir = new File("F:/This PC/Documents/AOV/cachemod/").list();

            for (int i = 0; i < subDir.length; i++) {
                subDir[i] = "F:/This PC/Documents/AOV/cachemod/" + subDir[i];
            }

            // if (App.modPackName.equals(""))
            ZipExtension.zipDir(subDir,
                    "F:/This PC/Documents/AOV/multipack_" + heroId
                            + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            // else {
            // ZipExtension.zipDir(subDir,
            // "F:/This PC/Documents/AOV/" + App.modPackName
            // +
            // "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModAssetRef(String id) {
        ModAssetRef(id, null, null);
    }

    public static void ModAssetRef(String id, String[] specialOld,
            String[] specialNew) {
        try {
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            String addcode = "/" + heroId + (skin < 10 ? "0" + skin : skin);

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
            String heroCodeName = zis.getNextEntry().getName().split("/")[1];
            zis.close();

            String inputPath = AssetRefsPath + "Hero/" + heroId + "_AssetRef.bytes";
            String outputPath;

            if (App.modPackName.equals("")) {
                outputPath = "F:/This PC/Documents/AOV/pack_" + id
                        + "/files/Resources/1.50.1/AssetRefs/Hero/" + heroId + "_AssetRef.bytes";
            } else {
                outputPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/AssetRefs/Hero/" + heroId + "_AssetRef.bytes";
                if (new File(outputPath).exists())
                    inputPath = outputPath;
            }

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);

            byte[] outputBytes = AOVDecompress(inputBytes);

            int[] allStart = new int[4];

            String[] searchCode = new String[] { "particlesInFirstLayer",
                    "hurtParticlesInFirstLayer", "particlesInOtherLayer", "hurtParticlesInOtherLayer" };

            for (int i = 0; i < 4; i++) {
                allStart[i] = DHAExtension.indexOf(outputBytes, searchCode[i].getBytes());
            }

            int start = 0, step = 0;
            int[] addto = new int[4];
            int alladd = 0;
            int maxStart = DHAExtension.bytesToInt(outputBytes[91], outputBytes[92],
                    outputBytes[93], outputBytes[94]);

            if (specialOld != null && specialNew != null && specialOld.length == specialNew.length) {
                for (int i = 0; i < specialOld.length; i++) {
                    String oldb = specialOld[i], newb = specialNew[i];
                    if (!oldb.toLowerCase().startsWith("prefab_skill_effects/hero_skill_effects")
                            || !newb.toLowerCase().startsWith("prefab_skill_effects/hero_skill_effects")) {
                        continue;
                    }
                    while ((start = DHAExtension.indexOf(outputBytes, oldb.getBytes(), start + 1)) > 0) {
                        outputBytes[start - 9] += newb.length() - oldb.length();
                        outputBytes[start - 55] += newb.length() - oldb.length();
                        outputBytes[start - 65] += newb.length() - oldb.length();
                        outputBytes[start - 73] += newb.length() - oldb.length();
                        byte[] bytes = DHAExtension
                                .toBytes(DHAExtension.bytesToInt(outputBytes[start - 176], outputBytes[start - 175],
                                        outputBytes[start - 174], outputBytes[start - 173]) + newb.length()
                                        - oldb.length());
                        outputBytes[start - 176] = bytes[0];
                        outputBytes[start - 175] = bytes[1];
                        outputBytes[start - 174] = bytes[2];
                        outputBytes[start - 173] = bytes[3];
                        for (int j = step + 1; j < 4; j++) {
                            if (start > allStart[j]) {
                                step = j;
                            }
                        }
                        // System.out.println("replaced special " + i + " at step " + step);
                        addto[step] = addto[step] + newb.length() - oldb.length();
                        alladd = alladd + newb.length() - oldb.length();

                        for (int j = step + 1; j < 4; j++) {
                            allStart[j] = allStart[j] + newb.length() - oldb.length();
                        }
                        outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, start),
                                newb.getBytes(),
                                Arrays.copyOfRange(outputBytes, start + oldb.length(), outputBytes.length));
                    }
                }
            }
            start = allStart[0];
            step = 0;
            // int maxreplace = 34, replaced = 0;
            while ((start = DHAExtension.indexOf(outputBytes,
                    ("prefab_skill_effects/hero_skill_effects/").getBytes(),
                    start + 1)) > -1) {
                // if (replaced == maxreplace)
                // break;
                // replaced++;

                if (start > maxStart)
                    break;

                for (int i = step + 1; i < 4; i++) {
                    if (allStart[i] != -1 && start > allStart[i]) {
                        step = i;
                    }
                }

                outputBytes[start - 9] += addcode.length();
                outputBytes[start - 55] += addcode.length();
                outputBytes[start - 65] += addcode.length();
                outputBytes[start - 73] += addcode.length();
                byte[] bytes = DHAExtension
                        .toBytes(DHAExtension.bytesToInt(outputBytes[start - 176], outputBytes[start - 175],
                                outputBytes[start - 174], outputBytes[start - 173]) + addcode.length());
                outputBytes[start - 176] = bytes[0];
                outputBytes[start - 175] = bytes[1];
                outputBytes[start - 174] = bytes[2];
                outputBytes[start - 173] = bytes[3];

                start += 40 + heroCodeName.length();

                outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, start), addcode.getBytes(),
                        Arrays.copyOfRange(outputBytes, start, outputBytes.length));
                addto[step] += addcode.length();
                alladd += addcode.length();

                for (int i = step + 1; i < 4; i++) {
                    allStart[i] += addcode.length();
                }
            }

            for (int i = 0; i < 4; i++) {
                start = allStart[i] - 8;
                if (start < 0) {
                    return;
                }
                // System.out.println(searchCode[i] + " " + start + ": "
                // + DHAExtension.bytesToInt(outputBytes[start], outputBytes[start + 1],
                // outputBytes[start + 2], outputBytes[start + 3])
                // + "+" + addto[i]);
                byte[] bytes = DHAExtension
                        .toBytes(DHAExtension.bytesToInt(outputBytes[start], outputBytes[start + 1],
                                outputBytes[start + 2], outputBytes[start + 3]) + addto[i]);
                outputBytes[start] = bytes[0];
                outputBytes[start + 1] = bytes[1];
                outputBytes[start + 2] = bytes[2];
                outputBytes[start + 3] = bytes[3];

                start = allStart[i] + 123 + searchCode[i].length();
                bytes = DHAExtension
                        .toBytes(DHAExtension.bytesToInt(outputBytes[start], outputBytes[start + 1],
                                outputBytes[start + 2], outputBytes[start + 3]) + addto[i]);
                outputBytes[start] = bytes[0];
                outputBytes[start + 1] = bytes[1];
                outputBytes[start + 2] = bytes[2];
                outputBytes[start + 3] = bytes[3];
            }

            start = 0;
            byte[] bytes = DHAExtension.toBytes(outputBytes.length);
            outputBytes[start] = bytes[0];
            outputBytes[start + 1] = bytes[1];
            outputBytes[start + 2] = bytes[2];
            outputBytes[start + 3] = bytes[3];

            start = 83;
            bytes = DHAExtension.toBytes(outputBytes.length - start);
            outputBytes[start] = bytes[0];
            outputBytes[start + 1] = bytes[1];
            outputBytes[start + 2] = bytes[2];
            outputBytes[start + 3] = bytes[3];

            start = 91;
            bytes = DHAExtension
                    .toBytes(DHAExtension.bytesToInt(outputBytes[start], outputBytes[start + 1],
                            outputBytes[start + 2], outputBytes[start + 3]) + alladd);
            outputBytes[start] = bytes[0];
            outputBytes[start + 1] = bytes[1];
            outputBytes[start + 2] = bytes[2];
            outputBytes[start + 3] = bytes[3];

            // DHAExtension.WriteAllBytes("D:/test.bytes", outputBytes);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);
            // DHAExtension.WriteAllBytes("D:/" + heroId + "_AssetRef.bytes", outputBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModLiteBullet(String id) {
        try {
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            String addcode = "/" + heroId + (skin < 10 ? "0" + skin : skin);

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
            String heroCodeName = zis.getNextEntry().getName().split("/")[1];
            zis.close();

            String inputPath = DatabinPath + "Skill/liteBulletCfg.bytes";
            String outputPath;

            if (App.modPackName.equals("")) {
                outputPath = "F:/This PC/Documents/AOV/pack_" + id
                        + "/files/Resources/1.50.1/Databin/Client/Skill/liteBulletCfg.bytes";
            } else {
                outputPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Databin/Client/Skill/liteBulletCfg.bytes";
                if (new File(outputPath).exists())
                    inputPath = outputPath;
            }

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVDecompress(inputBytes);

            int start = 0;
            String[] endCodes = new String[] { "a", "s", "u" };

            for (String endc : endCodes) {
                String findCode = heroId + endc;
                while ((start = DHAExtension.indexOf(outputBytes, findCode.getBytes(), start + 1)) > -1) {
                    int baseStart = start;
                    if (outputBytes[baseStart - 2] != 0)
                        continue;
                    while ((outputBytes[start] >= '0' && outputBytes[start] <= '9')
                            || (outputBytes[start] >= 'a' && outputBytes[start] <= 'z'))
                        start++;
                    if (outputBytes[baseStart - 1] == 0) {
                        outputBytes[baseStart - 13] = (byte) (outputBytes[baseStart - 13] + addcode.length());
                    } else {
                        outputBytes[baseStart - 14] = (byte) (outputBytes[baseStart - 14] + addcode.length());
                    }
                    outputBytes[start + 42] = (byte) (outputBytes[start + 42] + addcode.length());

                    start += 86 + heroCodeName.length();
                    outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, start), addcode.getBytes(),
                            Arrays.copyOfRange(outputBytes, start, outputBytes.length));
                }
            }

            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModSkillMark(String id) {
        try {
            List<String> skinNotModMark = new ArrayList<>(Arrays.asList(new String[] {
                    "1274", "1275"
            }));
            if (skinNotModMark.contains(id)) {
                return;
            }

            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            int skin = Integer.parseInt(skinId) - 1;
            String addcode = "/" + heroId + (skin < 10 ? "0" + skin : skin);

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
            String heroCodeName = zis.getNextEntry().getName().split("/")[1];
            zis.close();

            String inputPath = DatabinPath + "Skill/skillmark.bytes";
            String outputPath;

            if (App.modPackName.equals("")) {
                outputPath = "F:/This PC/Documents/AOV/pack_" + id
                        + "/files/Resources/1.50.1/Databin/Client/Skill/skillmark.bytes";
            } else {
                outputPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Databin/Client/Skill/skillmark.bytes";
                if (new File(outputPath).exists())
                    inputPath = outputPath;
            }

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVDecompress(inputBytes);

            int fisrtFound = -1, addto = 0, nextFirstFound = -1;
            int start = 0, previous = -1;

            while ((start = DHAExtension.indexOf(outputBytes,
                    ("prefab_skill_effects/hero_skill_effects/" + heroCodeName).getBytes(), start)) > -1) {
                if (fisrtFound == -1) {
                    fisrtFound = start;
                    nextFirstFound = DHAExtension.indexOf(outputBytes,
                            ("Prefab_Characters/Prefab_Hero/").getBytes(),
                            fisrtFound);
                }

                if (nextFirstFound > 0 && start > nextFirstFound) {
                    if (fisrtFound != -1) {
                        int addindex = DHAExtension.indexOfBefore(outputBytes,
                                ("Prefab_Characters/Prefab_Hero/" + heroCodeName).getBytes(),
                                fisrtFound) - 1;

                        boolean check = false;
                        while (!check) {
                            check = true;
                            for (int i = 1; i < 21; i++) {
                                if (outputBytes[addindex - i] != 0 && outputBytes[addindex - i] != 1
                                        && outputBytes[addindex - i] != -1
                                        && outputBytes[addindex - i] != (byte) 23
                                        && outputBytes[addindex - i] != (byte) 188
                                        && outputBytes[addindex - i] != (byte) 5
                                        && outputBytes[addindex - i] != (byte) -19
                                        && outputBytes[addindex - i] != (byte) -118) {
                                    // System.out.println((addindex-i) + ": " + outputBytes[addindex-i]);
                                    check = false;
                                }
                            }
                            if (!check)
                                addindex--;
                        }
                        // System.out.println(addindex);

                        byte[] bytes = DHAExtension
                                .toBytes(DHAExtension.bytesToInt(outputBytes[addindex], outputBytes[addindex + 1],
                                        outputBytes[addindex + 2], outputBytes[addindex + 3]) + addto);
                        outputBytes[addindex] = bytes[0];
                        outputBytes[addindex + 1] = bytes[1];
                        outputBytes[addindex + 2] = bytes[2];
                        outputBytes[addindex + 3] = bytes[3];
                    }
                    fisrtFound = start;
                    nextFirstFound = DHAExtension.indexOf(outputBytes,
                            "Prefab_Characters/Prefab_Hero/".getBytes(),
                            fisrtFound);
                    addto = 0;
                }

                // if (previous > -1 && (start - previous) > 70 + heroCodeName.length())
                // break;

                byte[] bytes = DHAExtension
                        .toBytes(DHAExtension.bytesToInt(outputBytes[start - 4], outputBytes[start - 3],
                                outputBytes[start - 2], outputBytes[start - 1]) + addcode.length());
                outputBytes[start - 4] = bytes[0];
                outputBytes[start - 3] = bytes[1];
                outputBytes[start - 2] = bytes[2];
                outputBytes[start - 1] = bytes[3];

                addto += addcode.length();
                start += 40 + heroCodeName.length();
                outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, start), addcode.getBytes(),
                        Arrays.copyOfRange(outputBytes, start, outputBytes.length));
                previous = start;
            }

            if (fisrtFound == -1) {
                return;
            }
            int addindex = DHAExtension.indexOfBefore(outputBytes,
                    ("Prefab_Characters/Prefab_Hero/" + heroCodeName).getBytes(),
                    fisrtFound) - 1;
            if (addindex < 0)
                addindex = fisrtFound - 40;

            boolean check = false;
            while (!check) {
                check = true;
                for (int i = 1; i < 21; i++) {
                    if (outputBytes[addindex - i] != 0 && outputBytes[addindex - i] != 1
                            && outputBytes[addindex - i] != -1
                            && outputBytes[addindex - i] != (byte) 23
                            && outputBytes[addindex - i] != (byte) 188
                            && outputBytes[addindex - i] != (byte) 5
                            && outputBytes[addindex - i] != (byte) -19
                            && outputBytes[addindex - i] != (byte) -118) {
                        check = false;
                    }
                }
                if (!check)
                    addindex--;
            }
            // System.out.println(addindex);

            byte[] bytes = DHAExtension
                    .toBytes(DHAExtension.bytesToInt(outputBytes[addindex], outputBytes[addindex + 1],
                            outputBytes[addindex + 2], outputBytes[addindex + 3]) + addto);
            outputBytes[addindex] = bytes[0];
            outputBytes[addindex + 1] = bytes[1];
            outputBytes[addindex + 2] = bytes[2];
            outputBytes[addindex + 3] = bytes[3];

            // DHAExtension.WriteAllBytes("F:\\This PC\\Documents\\AOV\\New folder\\" + id +
            // ".bytes", outputBytes);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModIcon_Name(String id) {
        ModIcon_Name(id.substring(0, 3) + "1", id);
    }

    public static void ModIcon_Name(String originId, String targetId) {
        try {
            String id = targetId;

            originId = originId.substring(0, 3) + (Integer.parseInt(originId.substring(3, originId.length())) - 1) + "";

            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());

            String inputPath = DatabinPath + "Actor/heroSkin.bytes";
            String outputPath;

            if (App.modPackName.equals("")) {
                outputPath = "F:/This PC/Documents/AOV/pack_" + id
                        + "/files/Resources/1.50.1/Databin/Client/Actor/heroSkin.bytes";
            } else {
                outputPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Databin/Client/Actor/heroSkin.bytes";
            }
            if (new File(outputPath).exists())
                inputPath = outputPath;

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);

            int start, end, changeindex;
            boolean kt2;
            byte[] outputBytes = AOVDecompress(inputBytes);
            // DHAExtension.WriteAllBytes("D:/testicon.bytes", outputBytes);

            byte[] replace;
            String skinidmod = (Integer.parseInt(skinId) - 1) + "";
            int targetLength = 0;
            if (new File(SpecialPath + "actor/" + targetId + ".bytes").exists()) {
                replace = DHAExtension.ReadAllBytes(SpecialPath + "actor/" + targetId + ".bytes");
                changeindex = -1;
                while ((replace[68 + targetLength] >= '0' && replace[68 + targetLength] <= '9')
                        || replace[68 + targetLength] == '_') {
                    targetLength++;
                }
                targetLength -= 2;
            } else {
                start = DHAExtension.indexOf(outputBytes,
                        ("Share_" + heroId + (skinidmod.length() == 1 ? "0" + skinidmod : skinidmod)).getBytes()) - 130;
                while ((outputBytes[start - targetLength - 1] >= '0' && outputBytes[start - targetLength - 1] <= '9')
                        || outputBytes[start - targetLength - 1] == '_') {
                    targetLength++;
                }
                targetLength -= 2;
                start -= targetLength + 70;

                end = DHAExtension.indexOf(outputBytes,
                        DHAExtension.mergeBytes(new byte[] { '3', '0' }, heroId.getBytes(), skinidmod.getBytes(),
                                ".jpg".getBytes()),
                        start + 100);
                kt2 = false;
                while (end < outputBytes.length && !kt2) {
                    if ((outputBytes[end] >= 'A' && outputBytes[end] <= 'Z')
                            || (outputBytes[end] >= '0' && outputBytes[end] <= '9')) {
                        for (int i = 0; i < 16; i++) {
                            if (!((outputBytes[end + i] >= 'A' && outputBytes[end + i] <= 'Z')
                                    || (outputBytes[end + i] >= '0' && outputBytes[end + i] <= '9'))) {
                                kt2 = false;
                                break;
                            }
                            if (outputBytes[end + i] >= 'A' && outputBytes[end + i] <= 'Z') {
                                kt2 = true;
                            }
                        }
                    }
                    end++;
                }
                end = end - 17;
                // System.out.print("Replaced " + start + "-" + end);
                changeindex = start + 4;
                replace = Arrays.copyOfRange(outputBytes, start, end);
            }

            int originLength = 0;
            start = DHAExtension.indexOf(outputBytes,
                    ("Share_" + originId.substring(0, 3) + (originId.substring(3).length() == 2 ? originId.substring(3)
                            : "0" + originId.substring(3))).getBytes())
                    - 130;
            while (outputBytes[start - originLength - 1] >= '0' && outputBytes[start - originLength - 1] <= '9') {
                originLength++;
            }
            originLength -= 2;
            start -= originLength + 70;

            if (originId.equals(heroId + "0")) {
                end = DHAExtension.indexOf(outputBytes, ("Hero_" + originId).getBytes(), start + 100);
            } else {
                end = DHAExtension.indexOf(outputBytes, ("30" + originId + ".jpg").getBytes(), start + 100);
            }
            if (end < 0) {
                System.out.println("*Mod Icon Error: not found end for " + originId);
                return;
            }
            kt2 = false;
            while (end < outputBytes.length && !kt2) {
                if ((outputBytes[end] >= 'A' && outputBytes[end] <= 'Z')
                        || (outputBytes[end] >= '0' && outputBytes[end] <= '9')) {
                    for (int i = 0; i < 16; i++) {
                        if (!((outputBytes[end + i] >= 'A' && outputBytes[end + i] <= 'Z')
                                || (outputBytes[end + i] >= '0' && outputBytes[end + i] <= '9'))) {
                            kt2 = false;
                            break;
                        }
                        if (outputBytes[end + i] >= 'A' && outputBytes[end + i] <= 'Z') {
                            kt2 = true;
                        }
                    }
                }
                end++;
            }
            end = end - 17;
            // System.out.println(" instead of: " + start + "-" + end);
            if (changeindex > 0 && originId.equals(heroId + "0") && !idNotSwap.contains(targetId)) {
                outputBytes[changeindex] = outputBytes[start + 4];
                outputBytes[changeindex + 1] = outputBytes[start + 5];
                // outputBytes[changeindex + 69] = (byte) originId.charAt(3);
                outputBytes[changeindex + 60] = (byte) (outputBytes[changeindex + 60]
                        - (targetLength - originId.length()));
                byte[] bytes = DHAExtension
                        .toBytes(DHAExtension.bytesToInt(outputBytes[changeindex - 4], outputBytes[changeindex - 3],
                                outputBytes[changeindex - 2], outputBytes[changeindex - 1])
                                - (targetLength - originId.length()));
                for (int i = 0; i < 4; i++) {
                    outputBytes[changeindex + i - 4] = bytes[i];
                }
                outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, changeindex + 64),
                        ("30" + originId).getBytes(),
                        Arrays.copyOfRange(outputBytes, changeindex + 66 + targetLength,
                                outputBytes.length));
                replace[36] = (byte) 0;
                // DHAExtension.WriteAllBytes("D:/replace.bytes", replace);
            } else {
                replace[4] = outputBytes[start + 4];
                replace[5] = outputBytes[start + 5];
                replace[36] = (byte) Integer.parseInt(originId.substring(3));
                replace[64] = (byte) (replace[64] - (targetLength - originId.length()));
                byte[] bytes = DHAExtension.toBytes(DHAExtension.bytesToInt(replace[0], replace[1],
                        replace[2], replace[3])
                        - (targetLength - originId.length()));
                for (int i = 0; i < 4; i++) {
                    replace[i] = bytes[i];
                }
                replace = DHAExtension.mergeBytes(Arrays.copyOfRange(replace, 0, 68), ("30" + originId).getBytes(),
                        Arrays.copyOfRange(replace, 70 + targetLength, replace.length));
                // DHAExtension.WriteAllBytes("D:/replace.bytes", replace);
            }

            outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, start), replace,
                    Arrays.copyOfRange(outputBytes, end, outputBytes.length));
            // DHAExtension.WriteAllBytes("D:/testicon.bytes", outputBytes);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModLabel(String id) {
        ModLabel(id.substring(0, 3) + "1", id);
    }

    public static void ModLabel(String originId, String targetId) {
        try {
            String id = targetId;

            originId = originId.substring(0, 3) + (Integer.parseInt(originId.substring(3, originId.length())) - 1) + "";

            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());

            String inputPath;
            byte[] inputBytes, outputBytes;

            byte[] oldCode = new byte[2], newCode = new byte[2], bytes, replace;
            int start, end;

            int skinidmod = (Integer.parseInt(skinId) - 1);
            newCode = DHAExtension.toBytes(Integer.parseInt(heroId) * 100 + skinidmod);
            // bytes = DHAExtension.toBytes(Integer.parseInt(heroId) * 100 + skinidmod);
            // newCode[0] = bytes[0];
            // newCode[1] = bytes[1];

            oldCode = DHAExtension.toBytes(Integer.parseInt(heroId) * 100 + Integer.parseInt(originId.substring(3)));
            // bytes = DHAExtension.toBytes(Integer.parseInt(heroId) * 100 +
            // Integer.parseInt(originId.substring(3)));
            // oldCode[0] = bytes[0];
            // oldCode[1] = bytes[1];

            inputPath = DatabinPath + "Shop/HeroSkinShop.bytes";
            String outputPath;

            if (App.modPackName.equals("")) {
                outputPath = "F:/This PC/Documents/AOV/pack_" + id
                        + "/files/Resources/1.50.1/Databin/Client/Shop/HeroSkinShop.bytes";
            } else {
                outputPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Databin/Client/Shop/HeroSkinShop.bytes";
            }
            if (new File(outputPath).exists())
                inputPath = outputPath;

            inputBytes = DHAExtension.ReadAllBytes(inputPath);
            outputBytes = AOVDecompress(inputBytes);

            if (new File(SpecialPath + "shop/" + targetId + ".bytes").exists()) {
                replace = DHAExtension.ReadAllBytes(SpecialPath + "shop/" + targetId + ".bytes");
            } else {
                start = DHAExtension.indexOf(outputBytes,
                        DHAExtension.mergeBytes(newCode, DHAExtension.toBytes(Integer.parseInt(heroId)))) - 4;
                if (start < 0) {
                    Skin skin = null;

                    for (Hero hero : App.heroList.heros) {
                        for (Skin s : hero.skins) {
                            if (id.equals(s.id)) {
                                skin = s;
                                break;
                            }
                        }
                        if (skin != null)
                            break;
                    }

                    boolean kt = false;
                    if (skin != null) {
                        for (Hero hero : App.heroList.heros) {
                            for (Skin s : hero.skins) {
                                if (skin.label == s.label) {
                                    newCode = DHAExtension.toBytes(
                                            Integer.parseInt(hero.id) * 100 + Integer.parseInt(s.id.substring(3)) - 1);

                                    if ((start = DHAExtension.indexOf(outputBytes,
                                            DHAExtension.mergeBytes(newCode,
                                                    DHAExtension.toBytes(Integer.parseInt(s.id.substring(0,3)))))
                                            - 4) > 0) {
                                        System.out
                                                .println("not found start for id " + targetId + " so changed to " + s.id
                                                        + " with label " + s.label);
                                        kt = true;
                                        break;
                                    }
                                }
                            }
                            if (kt)
                                break;
                        }
                    }

                    if (!kt) {
                        System.out.println(
                                " *Mod label error: not found start for id " + targetId + "(" + newCode[0] + ","
                                        + newCode[1] + ") label " + skin.label);
                        return;
                    }
                }
                end = start + DHAExtension.bytesToInt(outputBytes, start) + 4;
                replace = Arrays.copyOfRange(outputBytes, start, end);
            }

            start = DHAExtension.indexOf(outputBytes, DHAExtension.mergeBytes(oldCode,
                    DHAExtension.toBytes(Integer.parseInt(originId.substring(0, 3))))) - 4;
            if (start < 0) {
                System.out.println(" *Mod label error: not found start for id " + originId + "(" + oldCode[0] + ","
                        + oldCode[1] + ")");
                return;
            }
            end = start + DHAExtension.bytesToInt(outputBytes, start) + 4;
            replace[4] = oldCode[0];
            replace[5] = oldCode[1];
            bytes = DHAExtension.toBytes(Integer.parseInt(heroId));
            for (int i = 0; i < 4; i++) {
                replace[8 + i] = bytes[i];
            }
            replace[36] = (byte) Integer.parseInt(originId.substring(3));

            outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0, start), replace,
                    Arrays.copyOfRange(outputBytes, end, outputBytes.length));
            // DHAExtension.WriteAllBytes("D:/testlabel.bytes", outputBytes);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);
        } catch (Exception e) {
            System.out.print("Error while mod label " + originId + " to " + targetId + ": ");
            e.printStackTrace();
        }
    }

    public static void ModSound(String id) {
        String heroId = id.substring(0, 3);
        String originId = heroId + "1";
        ModSound(originId, id);
    }

    public static void ModSound(String originId, String id) {
        try {
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            originId = originId.substring(0, 3) + (Integer.parseInt(originId.substring(3, originId.length())) - 1) + "";

            String inputPath = DatabinPath + "Actor/heroSkin.bytes";

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVDecompress(inputBytes);

            byte[] oldCode = new byte[2], newCode = new byte[2], defaultCode = new byte[2];
            int start;

            int skinidmod = (Integer.parseInt(skinId) - 1);
            byte[] bytes = DHAExtension.toBytes(Integer.parseInt(heroId) * 100 + skinidmod);
            newCode[0] = bytes[0];
            newCode[1] = bytes[1];

            bytes = DHAExtension.toBytes(Integer.parseInt(heroId) * 100 + Integer.parseInt(originId.substring(3)));
            oldCode[0] = bytes[0];
            oldCode[1] = bytes[1];

            bytes = DHAExtension.toBytes(Integer.parseInt(heroId) * 100);
            defaultCode[0] = bytes[0];
            defaultCode[1] = bytes[1];

            String[] inputPaths = new String[] { DatabinPath + "Sound/BattleBank.bytes",
                    DatabinPath + "Sound/ChatSound.bytes",
                    DatabinPath + "Sound/HeroSound.bytes",
                    DatabinPath + "Sound/LobbyBank.bytes",
                    DatabinPath + "Sound/LobbySound.bytes"
            };

            int end;
            for (int l = 0; l < inputPaths.length; l++) {
                start = -1;
                end = -1;
                inputPath = inputPaths[l];
                String outputPath;

                if (App.modPackName.equals("")) {
                    outputPath = "F:/This PC/Documents/AOV/pack_" + id
                            + "/files/Resources/1.50.1/Databin/Client/Sound/"
                            + new File(inputPath).getName();
                } else {
                    outputPath = "F:/This PC/Documents/AOV/" + App.modPackName
                            + "/files/Resources/1.50.1/Databin/Client/Sound/"
                            + new File(inputPath).getName();
                    if (new File(outputPath).exists())
                        inputPath = outputPath;
                }

                inputBytes = DHAExtension.ReadAllBytes(inputPath);
                outputBytes = AOVDecompress(inputBytes);

                if (l == 0) {
                    while ((start = DHAExtension.indexOf(outputBytes,
                            DHAExtension.mergeBytes(new byte[] { 0 }, oldCode, new byte[] { 0 })) - 18) > 0) {

                        end = start + 50;
                        while (end < outputBytes.length && !(outputBytes[end] == 'V' &&
                                outputBytes[end + 1] == 'O')) {
                            end++;
                        }
                        end = end + 2;
                        outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0,
                                start),
                                Arrays.copyOfRange(outputBytes, end, outputBytes.length));
                    }
                } // else if (l == 1 || l == 2) {// || l == 4) {
                  // while ((end = DHAExtension.indexOf(outputBytes,
                  // DHAExtension.mergeBytes(new byte[] { 0 }, oldCode, new byte[] { 0 })) + 3) >
                  // 3) {
                  // start = end - 1;
                  // boolean xh = false;
                  // while (start > 0 && !(outputBytes[start - 3] == 'P'
                  // && outputBytes[start - 2] == 'l'
                  // && outputBytes[start - 1] == 'a'
                  // && outputBytes[start] == 'y')
                  // || !xh) {
                  // if (outputBytes[start - 3] == 'P'
                  // && outputBytes[start - 2] == 'l'
                  // && outputBytes[start - 1] == 'a'
                  // && outputBytes[start] == 'y') {
                  // xh = true;
                  // }
                  // start--;
                  // }
                  // while ((outputBytes[start] >= 'A' && outputBytes[start] <= 'Z')
                  // || (outputBytes[start] >= 'a' && outputBytes[start] <= 'z')
                  // || (outputBytes[start] >= '0' && outputBytes[start] <= '9')
                  // || (outputBytes[start] == '_')) {
                  // start++;
                  // }
                  // start = start + 3;
                  // // System.out.println("File " + new File(inputPath).getName() + " removed
                  // from "
                  // // + start + " to " + end);
                  // outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0,
                  // start),
                  // Arrays.copyOfRange(outputBytes, end, outputBytes.length));
                  // }
                  // } else if (l == 3) {
                  // while ((start = DHAExtension.indexOf(outputBytes,
                  // DHAExtension.mergeBytes(new byte[] { 0 }, oldCode, new byte[] { 0 })) - 18) >
                  // 0) {
                  // end = start + 1;
                  // while (end < outputBytes.length
                  // && !(outputBytes[end] == 'S'
                  // && outputBytes[end + 1] == 'h'
                  // && outputBytes[end + 2] == 'o'
                  // && outputBytes[end + 3] == 'w')) {
                  // end++;
                  // }
                  // end = end + 4;
                  // // System.out.println("File " + new File(inputPath).getName() + " removed
                  // from "
                  // // + start + " to " + end);
                  // outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0,
                  // start),
                  // Arrays.copyOfRange(outputBytes, end, outputBytes.length));
                  // }
                  // }

                // if (start != -1 && end != -1) {
                // outputBytes = DHAExtension.mergeBytes(Arrays.copyOfRange(outputBytes, 0,
                // start),
                // Arrays.copyOfRange(outputBytes, end, outputBytes.length));
                // } else
                // continue;

                int find = DHAExtension.indexOf(outputBytes,
                        DHAExtension.mergeBytes(new byte[] { 0 }, newCode, new byte[] { 0 }));
                if (l != 0) {
                    while (find != -1) {
                        find++;
                        outputBytes[find] = oldCode[0];
                        outputBytes[find + 1] = oldCode[1];
                        find = DHAExtension.indexOf(outputBytes,
                                DHAExtension.mergeBytes(new byte[] { 0 }, newCode, new byte[] { 0 }));
                    }
                } else {
                    while (find != -1) {
                        find++;
                        outputBytes[find] = defaultCode[0];
                        outputBytes[find + 1] = defaultCode[1];
                        find = DHAExtension.indexOf(outputBytes,
                                DHAExtension.mergeBytes(new byte[] { 0 }, newCode, new byte[] { 0 }));
                    }
                }

                // DHAExtension.WriteAllBytes("D:/" + new File(inputPath).getName(),
                // outputBytes);
                outputBytes = AOVCompress(outputBytes);
                DHAExtension.WriteAllBytes(outputPath, outputBytes);
            }
            // System.out.println("Moded sound " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModSoundNew(String originId, String id) {
        try {
            String heroId = id.substring(0, 3);
            String skinId = id.substring(3, id.length());
            originId = originId.substring(0, 3) + (Integer.parseInt(originId.substring(3, originId.length())) - 1) + "";

            byte[] inputBytes, outputBytes, bytes;

            int originModCode = Integer.parseInt(heroId) * 100 + Integer.parseInt(originId.substring(3));
            int targetModCode = Integer.parseInt(heroId) * 100 + (Integer.parseInt(skinId) - 1);
            int defaultModCode = Integer.parseInt(heroId) * 100;

            String[] inputPaths = new String[] { DatabinPath + "Sound/BattleBank.bytes",
                    DatabinPath + "Sound/ChatSound.bytes",
                    DatabinPath + "Sound/HeroSound.bytes",
                    DatabinPath + "Sound/LobbyBank.bytes",
                    DatabinPath + "Sound/LobbySound.bytes"
            };

            int start, end;
            for (int l = 0; l < inputPaths.length; l++) {
                String inputPath = inputPaths[l];
                String outputPath;

                if (App.modPackName.equals("")) {
                    outputPath = "F:/This PC/Documents/AOV/pack_" + id
                            + "/files/Resources/1.50.1/Databin/Client/Sound/"
                            + new File(inputPath).getName();
                } else {
                    outputPath = "F:/This PC/Documents/AOV/" + App.modPackName
                            + "/files/Resources/1.50.1/Databin/Client/Sound/"
                            + new File(inputPath).getName();
                    if (new File(outputPath).exists())
                        inputPath = outputPath;
                }

                inputBytes = DHAExtension.ReadAllBytes(inputPath);
                outputBytes = AOVDecompress(inputBytes);

                byte[] newSound = new byte[0];

                String[] endcs;
                if (l == 0) {
                    endcs = new String[] { "20", "21", "22" };
                } else if (l == 1) {
                    endcs = new String[] { "5" };
                } else if (l == 2) {
                    endcs = new String[] { "4", "81", "83", "91", "93" };
                } else if (l == 3) {
                    endcs = new String[] { "00" };
                } else {
                    endcs = new String[] { "10", "11", "12", "13" };
                }
                boolean specialExists = new File(SpecialPath + "sound/" + id).exists();
                if (specialExists) {
                    if (new File(SpecialPath + "sound/" + id + "/" + new File(inputPath).getName()).exists()) {
                        newSound = DHAExtension
                                .ReadAllBytes(SpecialPath + "sound/" + id + "/" + new File(inputPath).getName());
                        for (String endc : endcs) {
                            newSound = DHAExtension.replaceBytes(newSound, DHAExtension.toBytes(targetModCode),
                                    DHAExtension.toBytes(originModCode));
                            newSound = DHAExtension.replaceBytes(newSound,
                                    DHAExtension.toBytes(Integer.parseInt(targetModCode + "" + endc)),
                                    DHAExtension.toBytes(Integer.parseInt(originModCode + "" + endc)));
                            if (skinAwaken.contains(id)) {
                                for (int i = 1; i < 6; i++) {
                                    // System.out.println((targetModCode * 100 + i) + "-" + (originModCode));
                                    // System.out.println((targetModCode + "0" + i + endc) + "-" + (originModCode +
                                    // endc));
                                    newSound = DHAExtension.replaceBytes(newSound,
                                            DHAExtension.toBytes(targetModCode * 100 + i),
                                            DHAExtension.toBytes(originModCode));
                                    newSound = DHAExtension.replaceBytes(newSound,
                                            DHAExtension.toBytes(Integer.parseInt(targetModCode + "0" + i + endc)),
                                            DHAExtension.toBytes(Integer.parseInt(originModCode + endc)));
                                }
                            }
                        }
                    } else {
                        continue;
                    }
                }
                for (String endc : endcs) {
                    if (!specialExists) {
                        start = DHAExtension.indexOf(outputBytes,
                                DHAExtension.toBytes(Integer.parseInt(targetModCode + "" + endc))) - 4;
                        if (start > 0) {
                            end = start + DHAExtension.bytesToInt(outputBytes, start) + 4;
                            bytes = Arrays.copyOfRange(outputBytes, start, end);
                            bytes = DHAExtension.replaceBytes(bytes, DHAExtension.toBytes(targetModCode),
                                    DHAExtension.toBytes(originModCode));
                            bytes = DHAExtension.replaceBytes(bytes,
                                    DHAExtension.toBytes(Integer.parseInt(targetModCode + "" + endc)),
                                    DHAExtension.toBytes(Integer.parseInt(originModCode + "" + endc)));
                            if (skinAwaken.contains(id)) {
                                for (int i = 1; i < 6; i++) {
                                    bytes = DHAExtension.replaceBytes(bytes,
                                            DHAExtension.toBytes(targetModCode * 10 + i),
                                            DHAExtension.toBytes(originModCode * 10 + i));
                                    bytes = DHAExtension.replaceBytes(bytes,
                                            DHAExtension.toBytes(Integer.parseInt(targetModCode + "0" + i + endc)),
                                            DHAExtension.toBytes(Integer.parseInt(originModCode + "0" + i + endc)));
                                }
                            }
                            newSound = DHAExtension.mergeBytes(newSound, bytes);
                        }
                    }

                    start = DHAExtension.indexOf(outputBytes,
                            DHAExtension.toBytes(Integer.parseInt(originModCode + "" + endc))) - 4;
                    // System.out.println(start + ": " + defaultModCode + endc);
                    if (start > 0) {
                        end = start + DHAExtension.bytesToInt(outputBytes, start) + 4;
                    } else {
                        start = DHAExtension.indexOf(outputBytes,
                                DHAExtension.toBytes(Integer.parseInt(defaultModCode + "" + endc))) - 4;
                        if (start < 0) {
                            continue;
                        }
                        start = start + DHAExtension.bytesToInt(outputBytes, start) + 4;
                        end = start;
                    }
                    outputBytes = DHAExtension.replaceBytes(outputBytes, start, end, new byte[0]);
                    // outputBytes = DHAExtension.replaceBytes(outputBytes, start, end, newSound);
                }
                outputBytes = DHAExtension.mergeBytes(outputBytes, newSound);

                // DHAExtension.WriteAllBytes(
                // "F:\\This PC\\Documents\\AOV\\New folder\\" + id + "\\" + new
                // File(outputPath).getName(),
                // outputBytes);
                outputBytes = AOVCompress(outputBytes);
                DHAExtension.WriteAllBytes(outputPath, outputBytes);
            }
            // System.out.println("Moded sound " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModBack(String[] targetid){
        ModBack(targetid, null);
    }

    public static void ModBack(String[] targetid, boolean[] isAwakeSkins) {
        try {
            String inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
            if (!App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/" + App.modPackName
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
            } else if (App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/pack_" + targetid[0]
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/pack_" + targetid[0]
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
            }

            if (new File("F:/This PC/Documents/AOV/cachemod/").exists()) {
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/cachemod/");
            }
            new File("F:/This PC/Documents/AOV/cachemod/").mkdirs();
            ZipExtension.unzip(inputZipPath, "F:/This PC/Documents/AOV/cachemod/");
            String filemodName = "commonresource/Back.xml";
            String inputPath = "F:/This PC/Documents/AOV/cachemod/" + filemodName;
            String outputPath = inputPath;

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVDecompress(inputBytes);
            if (outputBytes == null)
                return;

            String content = new String(outputBytes, StandardCharsets.UTF_8);

            int moded = DHAExtension.countMatches(content, "CheckHeroIdTick") / 3;

            List<String> listConditions = new ArrayList<>();

            for (int l = 0; l < targetid.length; l++) {
                String id = targetid[l];
                String heroId = id.substring(0, 3);
                String skinId = id.substring(3, id.length());
                int skin = Integer.parseInt(skinId) - 1;

                List<String> setObjectTracks = new ArrayList<>();
                ZipInputStream zipin = new ZipInputStream(
                        new FileInputStream(ActionsParentPath + "Actor_" + heroId + "_Actions.pkg.bytes"));
                ZipEntry entry;
                while ((entry = zipin.getNextEntry()) != null) {
                    if (entry.getName().endsWith("/" + heroId + (skin < 10 ? "0" + skin : skin) + "_Back.xml")) {
                        byte[] bytes = zipin.readAllBytes();
                        String[] backLines = new String(AOVExtension.AOVDecompress(bytes), StandardCharsets.UTF_8)
                                .split("\n");
                        for (int i = 0; i < backLines.length; i++) {
                            // if (backLines[i].contains("SetObjectDirectionTick")
                            // || backLines[i].contains("ChangeHDHeightDuration"))
                            if (backLines[i].contains("<Track ") && !backLines[i].toLowerCase().contains("checkskin")
                                    && !backLines[i].contains("GetResource")
                                    && !backLines[i].contains("TriggerParticle")) {
                                setObjectTracks.add(backLines[i]);
                                setObjectTracks
                                        .add("      <Condition id=\"" + (moded + 1) + "\" guid=\"Mod_by_DHA_" + heroId
                                                + "\" status=\"true\"/>");
                                i++;
                                while (!backLines[i].contains("</Track")) {
                                    if (!backLines[i].contains("<Condition ")) {
                                        setObjectTracks.add(backLines[i]);
                                    }
                                    i++;
                                }
                                setObjectTracks.add(backLines[i]);
                            }
                        }
                        break;
                    }
                }
                zipin.close();
                String objectSetDirection;
                if (setObjectTracks.size() == 0) {
                    objectSetDirection = "";
                } else {
                    objectSetDirection = "\n" + String.join("\n", setObjectTracks);
                }

                content = content.replace("    <Track trackName=\"HitTriggerTick0\"",
                        "    <Track trackName=\"CheckHeroIdTick" + moded
                                + "\" eventType=\"CheckHeroIdTick\" guid=\"Mod_by_DHA_" + heroId
                                + "\" enabled=\"true\" r=\"0.000\" g=\"0.000\" b=\"0.000\" stopAfterLastEvent=\"true\">"
                                + "\n      <Event eventName=\"CheckHeroIdTick\" time=\"0.000\" isDuration=\"false\">"
                                + "\n        <TemplateObject name=\"targetId\" id=\"1\" objectName=\"target\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n        <int name=\"heroId\" value=\"" + heroId
                                + "\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n      </Event>"
                                + "\n    </Track>"
                                + "\n    <Track trackName=\"HitTriggerTick0\"");

                ZipInputStream zis = new ZipInputStream(
                        new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
                String heroCodeName = zis.getNextEntry().getName().split("/")[1];
                zis.close();

                boolean isAwakeSkin = isAwakeSkins != null && isAwakeSkins[l];
                if (isAwakeSkin) {
                    heroCodeName = heroId + (skin < 10 ? "0" + skin : skin);
                    objectSetDirection += 
                            "\n    <Track trackName=\"PlayAnimDuration2\" eventType=\"PlayAnimDuration\" guid=\"972d1382-031a-4c10-8eeb-d10b3fc76f47\" enabled=\"true\" useRefParam=\"false\" refParamName=\"\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">\n" + //
                            "\n      <Condition id=\"" + (moded + 1) + "\" guid=\"Mod_by_DHA_" + heroId + "\" status=\"true\"/>" +
                            "      <Event eventName=\"PlayAnimDuration\" time=\"7.000\" length=\"1.500\" isDuration=\"true\" guid=\"620b137f-72fa-4602-a653-72ae85944d33\">\n" + //
                            "        <TemplateObject name=\"targetId\" id=\"0\" objectName=\"self\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />\n" + //
                            "        <String name=\"clipName\" value=\"" + heroId + (skin < 10 ? "0" + skin : skin) + "/Awaken/Gohome\" refParamName=\"\" useRefParam=\"false\" />\n" + //
                            "        <int name=\"layer\" value=\"2\" refParamName=\"\" useRefParam=\"false\" />\n" + //
                            "        <bool name=\"alwaysAnimate\" value=\"true\" refParamName=\"\" useRefParam=\"false\" />\n" + //
                            "      </Event>\n" + //
                            "    </Track>\n" + //
                            "    <Track trackName=\"PlayAnimDuration0\" eventType=\"PlayAnimDuration\" guid=\"664523ad-bc5e-4796-94a7-003b758fb8c4\" enabled=\"true\" useRefParam=\"false\" refParamName=\"\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">\n" + //
                            "\n      <Condition id=\"" + (moded + 1) + "\" guid=\"Mod_by_DHA_" + heroId + "\" status=\"true\"/>" +
                            "      <Event eventName=\"PlayAnimDuration\" time=\"0.000\" length=\"7.000\" isDuration=\"true\" guid=\"ff3c2242-829b-4cfd-aaad-8e70a5e03ba2\">\n" + //
                            "        <TemplateObject name=\"targetId\" id=\"0\" objectName=\"self\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />\n" + //
                            "        <String name=\"clipName\" value=\"" + heroId + (skin < 10 ? "0" + skin : skin) + "/Awaken/Home\" refParamName=\"\" useRefParam=\"false\" />\n" + //
                            "        <int name=\"layer\" value=\"2\" refParamName=\"\" useRefParam=\"false\" />\n" + //
                            "        <bool name=\"bLoop\" value=\"true\" refParamName=\"\" useRefParam=\"false\" />\n" + //
                            "        <float name=\"crossFadeTime\" value=\"0.100\" refParamName=\"\" useRefParam=\"false\" />\n" + //
                            "        <bool name=\"alwaysAnimate\" value=\"true\" refParamName=\"\" useRefParam=\"false\" />\n" + //
                            "      </Event>\n" + //
                            "    </Track>";
                }

                listConditions.add("      <Condition id=\"" + (moded + 1) + "\" guid=\"Mod_by_DHA_" + heroId
                        + "\" status=\"false\"/>");

                content = content.replace("  </Action>",
                        "    <Track trackName=\"TriggerParticleTick1\" eventType=\"TriggerParticleTick\" guid=\"modskinb-yahm-odao-vk3g-ama3k57mn"
                                + heroId + "\" enabled=\"true\" r=\"1\" g=\"0.6\" b=\"0\" stopAfterLastEvent=\"true\">"
                                + "\n      <Condition id=\"" + (moded + 1) + "\" guid=\"Mod_by_DHA_" + heroId
                                + "\" status=\"true\"/>"
                                + "\n      <Event eventName=\"TriggerParticleTick\" time=\"7\" isDuration=\"false\">"
                                + "\n        <TemplateObject name=\"targetId\" id=\"-1\" objectName=\"None\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                + "\n        <TemplateObject name=\"objectSpaceId\" id=\"0\" objectName=\"self\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                + "\n        <String name=\"parentResourceName\" value=\"born_back_reborn/huijidi_01\" refParamName=\"\" useRefParam=\"false\"/>"
                                + "\n      	<String name=\"resourceName\" value=\"prefab_skill_effects/"
                                + (isAwakeSkin ? "component_effects" : "hero_skill_effects") + "/"
                                + heroCodeName + "/" + heroId + (skin < 10 ? "0" + skin : skin)
                                + (isAwakeSkin ? "_5" : "")
                                + "/huijidi_01\" useRefParam=\"true\"/>"
                                + "\n      </Event>"
                                + "\n    </Track>"
                                + "\n    <Track trackName=\"TriggerParticle1\" eventType=\"TriggerParticle\" guid=\"ahmodaov-k3ga-ma3k-57do-notreupok"
                                + heroId
                                + "\" enabled=\"true\" useRefParam=\"false\" refParamName=\"\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">"
                                + "\n      <Condition id=\"" + (moded + 1) + "\" guid=\"Mod_by_DHA_" + heroId
                                + "\" status=\"true\"/>"
                                + "\n      <Event eventName=\"TriggerParticle\" time=\"0.000\" length=\"7.000\" isDuration=\"true\" guid=\"6057cb60-5062-42e9-a6cd-09c8e9e7eb05\">"
                                + "\n		<TemplateObject name=\"targetId\" id=\"0\" objectName=\"self\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n		<TemplateObject name=\"objectSpaceId\" id=\"0\" objectName=\"self\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n		<String name=\"parentResourceName\" value=\"prefab_skill_effects/tongyong_effects/tongyong_hurt/born_back_reborn/huicheng_tongyong_01\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n		<String name=\"resourceName\" value=\"prefab_skill_effects/"
                                + (isAwakeSkin ? "component_effects" : "hero_skill_effects") + "/"
                                + heroCodeName + "/" + heroId + (skin < 10 ? "0" + skin : skin)
                                + (isAwakeSkin ? "_5" : "")
                                + "/huicheng_tongyong_01\" useRefParam=\"true\" />"
                                + "\n		<Vector3 name=\"bindPosOffset\" x=\"0.000\" y=\"-0.300\" z=\"0.000\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n      </Event>"
                                + "\n    </Track>"
                                + objectSetDirection
                                + "\n  </Action>");
                moded++;
            }

            List<String> lines = new ArrayList<>(Arrays.asList(content.split("\n")));
            for (int i = 0; i < lines.size(); i++) {
                // System.out.println( i + " - " + lines.size());
                if (lines.get(i).contains("GetResource") || lines.get(i).contains("TriggerParticle0")) {
                    lines.addAll(i + 1, listConditions);
                    i += listConditions.size();
                }
            }

            content = String.join("\n", lines);
            outputBytes = content.getBytes(StandardCharsets.UTF_8);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);

            String[] subDir = new File("F:/This PC/Documents/AOV/cachemod/").list();

            for (int i = 0; i < subDir.length; i++) {
                subDir[i] = "F:/This PC/Documents/AOV/cachemod/" + subDir[i];
            }

            if (App.modPackName.equals(""))
                ZipExtension.zipDir(subDir,
                        "F:/This PC/Documents/AOV/pack_" + targetid[0]
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            else {
                ZipExtension.zipDir(subDir,
                        "F:/This PC/Documents/AOV/" + App.modPackName
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModBackMulti(String[] originid, String[] targetid) {
        try {
            String inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
            if (!App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/" + App.modPackName
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
            } else if (App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/pack_" + targetid[0]
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/pack_" + targetid[0]
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
            }

            if (new File("F:/This PC/Documents/AOV/cachemod/").exists()) {
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/cachemod/");
            }
            new File("F:/This PC/Documents/AOV/cachemod/").mkdirs();
            ZipExtension.unzip(inputZipPath, "F:/This PC/Documents/AOV/cachemod/");
            String filemodName = "commonresource/Back.xml";
            String inputPath = "F:/This PC/Documents/AOV/cachemod/" + filemodName;
            String outputPath = inputPath;

            byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVDecompress(inputBytes);
            if (outputBytes == null)
                return;

            String content = new String(outputBytes, StandardCharsets.UTF_8);

            int moded = DHAExtension.countMatches(content, "CheckHeroIdTick") / 3;

            List<String> listConditions = new ArrayList<>();

            for (int l = 0; l < targetid.length; l++) {
                String id = targetid[l];
                String heroId = id.substring(0, 3);
                String skinId = id.substring(3, id.length());
                int skin = Integer.parseInt(skinId) - 1;

                int origin = Integer.parseInt(originid[l].substring(3)) - 1;
                String originIdCode = heroId + (origin < 10 ? "0" + origin : origin);

                List<String> setObjectTracks = new ArrayList<>();
                ZipInputStream zipin = new ZipInputStream(
                        new FileInputStream(ActionsParentPath + "Actor_" + heroId + "_Actions.pkg.bytes"));
                ZipEntry entry;
                while ((entry = zipin.getNextEntry()) != null) {
                    if (entry.getName().endsWith("/" + heroId + (skin < 10 ? "0" + skin : skin) + "_Back.xml")) {
                        byte[] bytes = zipin.readAllBytes();
                        String[] backLines = new String(AOVExtension.AOVDecompress(bytes), StandardCharsets.UTF_8)
                                .split("\n");
                        for (int i = 0; i < backLines.length; i++) {
                            if (backLines[i].contains("SetObjectDirectionTick")
                                    || backLines[i].contains("ChangeHDHeightDuration")) {
                                setObjectTracks.add(backLines[i]);
                                setObjectTracks
                                        .add("      <Condition id=\"" + (moded + 1) + "\" guid=\"Mod_by_DHA_" + heroId
                                                + "\" status=\"true\"/>");
                                i++;
                                while (!backLines[i].contains("</Track")) {
                                    setObjectTracks.add(backLines[i]);
                                    i++;
                                }
                                setObjectTracks.add(backLines[i]);
                            }
                        }
                        break;
                    }
                }
                zipin.close();
                String objectSetDirection;
                if (setObjectTracks.size() == 0) {
                    objectSetDirection = "";
                } else {
                    objectSetDirection = "\n" + String.join("\n", setObjectTracks);
                }

                content = content.replace("    <Track trackName=\"HitTriggerTick0\"",
                        "    <Track trackName=\"CheckSkinIdTick" + moded
                                + "\" eventType=\"CheckSkinIdTick\" guid=\"Mod_by_DHA_" + originIdCode
                                + "\" enabled=\"true\" r=\"0.000\" g=\"0.000\" b=\"0.000\" stopAfterLastEvent=\"true\">"
                                + "\n      <Event eventName=\"CheckSkinIdTick\" time=\"0.000\" isDuration=\"false\">"
                                + "\n        <TemplateObject name=\"targetId\" id=\"0\" objectName=\"target\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n        <int name=\"skinId\" value=\"" + originIdCode
                                + "\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n      </Event>"
                                + "\n    </Track>"
                                + "\n    <Track trackName=\"HitTriggerTick0\"");

                ZipInputStream zis = new ZipInputStream(
                        new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
                String heroCodeName = zis.getNextEntry().getName().split("/")[1];
                zis.close();

                listConditions.add("      <Condition id=\"" + (moded + 1) + "\" guid=\"Mod_by_DHA_" + heroId
                        + "\" status=\"false\"/>");

                content = content.replace("  </Action>",
                        "    <Track trackName=\"TriggerParticleTick1\" eventType=\"TriggerParticleTick\" guid=\"modskinb-yahm-odao-vk3g-ama3k57mn"
                                + heroId + "\" enabled=\"true\" r=\"1\" g=\"0.6\" b=\"0\" stopAfterLastEvent=\"true\">"
                                + "\n      <Condition id=\"" + (moded + 1) + "\" guid=\"Mod_by_DHA_" + heroId
                                + "\" status=\"true\"/>"
                                + "\n      <Event eventName=\"TriggerParticleTick\" time=\"7\" isDuration=\"false\">"
                                + "\n      	<String name=\"resourceName\" value=\"prefab_skill_effects/hero_skill_effects/"
                                + heroCodeName + "/" + heroId + (skin < 10 ? "0" + skin : skin)
                                + "/huijidi_01\" useRefParam=\"true\"/>"
                                + "\n      </Event>"
                                + "\n    </Track>"
                                + "\n    <Track trackName=\"TriggerParticle1\" eventType=\"TriggerParticle\" guid=\"ahmodaov-k3ga-ma3k-57do-notreupok"
                                + heroId
                                + "\" enabled=\"true\" useRefParam=\"false\" refParamName=\"\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">"
                                + "\n      <Condition id=\"" + (moded + 1) + "\" guid=\"Mod_by_DHA_" + heroId
                                + "\" status=\"true\"/>"
                                + "\n      <Event eventName=\"TriggerParticle\" time=\"0.000\" length=\"7.000\" isDuration=\"true\" guid=\"6057cb60-5062-42e9-a6cd-09c8e9e7eb05\">"
                                + "\n		<TemplateObject name=\"targetId\" id=\"0\" objectName=\"self\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n		<TemplateObject name=\"objectSpaceId\" id=\"0\" objectName=\"self\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n		<String name=\"parentResourceName\" value=\"prefab_skill_effects/tongyong_effects/tongyong_hurt/born_back_reborn/huicheng_tongyong_01\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n		<String name=\"resourceName\" value=\"prefab_skill_effects/hero_skill_effects/"
                                + heroCodeName + "/" + heroId + (skin < 10 ? "0" + skin : skin)
                                + "/huicheng_tongyong_01\" useRefParam=\"true\" />"
                                + "\n		<Vector3 name=\"bindPosOffset\" x=\"0.000\" y=\"-0.300\" z=\"0.000\" refParamName=\"\" useRefParam=\"false\" />"
                                + "\n      </Event>"
                                + "\n    </Track>"
                                + objectSetDirection
                                + "\n  </Action>");
                moded++;
            }

            List<String> lines = new ArrayList<>(Arrays.asList(content.split("\n")));
            for (int i = 0; i < lines.size(); i++) {
                // System.out.println( i + " - " + lines.size());
                if (lines.get(i).contains("GetResource") || lines.get(i).contains("TriggerParticle0")) {
                    lines.addAll(i + 1, listConditions);
                    i += listConditions.size();
                }
            }

            content = String.join("\n", lines);
            outputBytes = content.getBytes(StandardCharsets.UTF_8);
            outputBytes = AOVCompress(outputBytes);
            DHAExtension.WriteAllBytes(outputPath, outputBytes);

            String[] subDir = new File("F:/This PC/Documents/AOV/cachemod/").list();

            for (int i = 0; i < subDir.length; i++) {
                subDir[i] = "F:/This PC/Documents/AOV/cachemod/" + subDir[i];
            }

            if (App.modPackName.equals(""))
                ZipExtension.zipDir(subDir,
                        "F:/This PC/Documents/AOV/pack_" + targetid[0]
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            else {
                ZipExtension.zipDir(subDir,
                        "F:/This PC/Documents/AOV/" + App.modPackName
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModHaste(String[] targetid) {
        ModHaste(targetid, null, null, null);
    }

    public static void ModHaste(String[] targetid, String[] hasteNames, String[] hasteNameRuns,
            String[] hasteNameEnds) {
        try {
            String inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
            if (!App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/" + App.modPackName
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
            } else if (App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/pack_" + targetid[0]
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/pack_" + targetid[0]
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
            }

            if (new File("F:/This PC/Documents/AOV/cachemod/").exists()) {
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/cachemod/");
            }
            new File("F:/This PC/Documents/AOV/cachemod/").mkdirs();
            ZipExtension.unzip(inputZipPath, "F:/This PC/Documents/AOV/cachemod/");
            String[] filemodNames = new String[] { "commonresource/HasteE1.xml", "commonresource/HasteE1_leave.xml" };
            for (int f = 0; f < filemodNames.length; f++) {
                String filemodName = filemodNames[f];
                String inputPath = "F:/This PC/Documents/AOV/cachemod/" + filemodName;
                String outputPath = inputPath;

                byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
                byte[] outputBytes = AOVDecompress(inputBytes);
                if (outputBytes == null)
                    return;

                String content = new String(outputBytes, StandardCharsets.UTF_8);

                List<String> listConditions = new ArrayList<>();

                int moded = DHAExtension.countMatches(content, "CheckHeroIdTick") / 3;

                int in = 0;
                for (String id : targetid) {

                    String heroId = id.substring(0, 3);
                    String skinId = id.substring(3, id.length());
                    int skin = Integer.parseInt(skinId) - 1;

                    ZipInputStream zis = new ZipInputStream(
                            new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
                    String heroCodeName = zis.getNextEntry().getName().split("/")[1];
                    zis.close();

                    String hasteName, vectorBind, hasteNameRun, hasteNameEnd = null;
                    if (hasteNames == null || hasteNames[in] == null) {
                        hasteName = "jiasu_tongyong_01";
                        hasteNameRun = "jiasu_tongyong_02";
                        listConditions.add("      <Condition id=\"" + (f == 0 ? moded + 1 : moded + 1)
                                + "\" guid=\"Mod_by_DHA_" + heroId
                                + "\" status=\"false\"/>");
                        vectorBind = "\n        <Vector3 name=\"bindPosOffset\" x=\"0\" y=\"0.7\" z=\"-0.6\" refParamName=\"\" useRefParam=\"false\" />";
                    } else {
                        hasteName = hasteNames[in];
                        if (hasteNameRuns == null || hasteNameRuns[in] == null) {
                            hasteNameRun = "jiasu_tongyong_01";
                        } else {
                            hasteNameRun = hasteNameRuns[in];
                            listConditions.add("      <Condition id=\"" + (f == 0 ? moded + 1 : moded + 1)
                                    + "\" guid=\"Mod_by_DHA_" + heroId
                                    + "\" status=\"false\"/>");
                        }
                        vectorBind = "";
                    }

                    content = content.replace("<Track trackName=\"SkillFuncPerioidc0\"",
                            "  <Track trackName=\"CheckHeroIdTick" + moded
                                    + "\" eventType=\"CheckHeroIdTick\" guid=\"Mod_by_DHA_" + heroId
                                    + "\" enabled=\"true\" refParamName=\"\" useRefParam=\"false\" r=\"0.667\" g=\"1.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">"
                                    + "\n      <Event eventName=\"CheckHeroIdTick\" time=\"0.000\" isDuration=\"false\">"
                                    + "\n        <TemplateObject name=\"targetId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + "\n        <int name=\"heroId\" value=\"" + heroId
                                    + "\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + "\n      </Event>"
                                    + "\n    </Track>"
                                    + "\n  <Track trackName=\"SkillFuncPerioidc0\"");

                    if (hasteNameEnds != null && hasteNameEnds[in] != null) {
                        hasteNameEnd = hasteNameEnds[in];
                        content = content.replace("  </Action>",
                                "    <Track trackName=\"TriggerParticle0\" eventType=\"TriggerParticle\" guid=\"412ea073-5944-46e4-ae5e-3037e855fda7"
                                        + heroId
                                        + "\" enabled=\"true\" refParamName=\"\" useRefParam=\"false\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">"
                                        + "\n      <Condition id=\"" + (f == 0 ? moded + 1 : moded + 1)
                                        + "\" guid=\"Mod_by_DHA_" + heroId
                                        + "\" status=\"true\"/>"
                                        + "\n      <Event eventName=\"TriggerParticle\" time=\"0.000\" length=\"4.000\" isDuration=\"true\" guid=\"3f4a326c-6b74-4d7e-b9ac-f36378e06052\""
                                        + ">"
                                        + "\n        <TemplateObject name=\"targetId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                        + "\n        <TemplateObject name=\"objectSpaceId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                        + "\n        <String name=\"resourceName\" value=\"prefab_skill_effects/hero_skill_effects/"
                                        + heroCodeName + "/" + heroId + (skin < 10 ? "0" + skin : skin)
                                        + "/" + hasteName + "\" refParamName=\"\" useRefParam=\"false\"/>"
                                        + vectorBind
                                        + "\n      </Event>"
                                        + "\n    </Track>"
                                        + "\n  </Action>");
                        content = content.replace("  </Action>",
                                "    <Track trackName=\"TriggerParticle0\" eventType=\"TriggerParticle\" guid=\"412ea073-5944-46e4-ae5e-3037e855fda7"
                                        + heroId
                                        + "\" enabled=\"true\" refParamName=\"\" useRefParam=\"false\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">"
                                        + "\n      <Condition id=\"" + (f == 0 ? moded + 1 : moded + 1)
                                        + "\" guid=\"Mod_by_DHA_" + heroId
                                        + "\" status=\"true\"/>"
                                        + "\n      <Event eventName=\"TriggerParticle\" time=\"4.000\" length=\"1.000\" isDuration=\"true\" guid=\"3f4a326c-6b74-4d7e-b9ac-f36378e06052\""
                                        + ">"
                                        + "\n        <TemplateObject name=\"targetId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                        + "\n        <TemplateObject name=\"objectSpaceId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                        + "\n        <String name=\"resourceName\" value=\"prefab_skill_effects/hero_skill_effects/"
                                        + heroCodeName + "/" + heroId + (skin < 10 ? "0" + skin : skin)
                                        + "/" + hasteNameEnd + "\" refParamName=\"\" useRefParam=\"false\"/>"
                                        + vectorBind
                                        + "\n      </Event>"
                                        + "\n    </Track>"
                                        + "\n  </Action>");
                    } else {
                        content = content.replace("  </Action>",
                                "    <Track trackName=\"TriggerParticle0\" eventType=\"TriggerParticle\" guid=\"412ea073-5944-46e4-ae5e-3037e855fda7"
                                        + heroId
                                        + "\" enabled=\"true\" refParamName=\"\" useRefParam=\"false\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">"
                                        + "\n      <Condition id=\"" + (f == 0 ? moded + 1 : moded + 1)
                                        + "\" guid=\"Mod_by_DHA_" + heroId
                                        + "\" status=\"true\"/>"
                                        + "\n      <Event eventName=\"TriggerParticle\" time=\"0.000\" length=\"5.000\" isDuration=\"true\" guid=\"3f4a326c-6b74-4d7e-b9ac-f36378e06052\""
                                        + ">"
                                        + "\n        <TemplateObject name=\"targetId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                        + "\n        <TemplateObject name=\"objectSpaceId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                        + "\n        <String name=\"resourceName\" value=\"prefab_skill_effects/hero_skill_effects/"
                                        + heroCodeName + "/" + heroId + (skin < 10 ? "0" + skin : skin)
                                        + "/" + hasteName + "\" refParamName=\"\" useRefParam=\"false\"/>"
                                        + vectorBind
                                        + "\n      </Event>"
                                        + "\n    </Track>"
                                        + "\n  </Action>");
                    }
                    content = content.replace("  </Action>",
                            "    <Track trackName=\"TriggerParticle0\" eventType=\"TriggerParticle\" guid=\"412ea073-5944-46e4-ae5e-3037e855fda7"
                                    + heroId
                                    + "\" enabled=\"true\" refParamName=\"\" useRefParam=\"false\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">"
                                    + "\n      <Condition id=\"" + (f == 0 ? moded + 1 : moded + 1)
                                    + "\" guid=\"Mod_by_DHA_" + heroId
                                    + "\" status=\"true\"/>"
                                    + "\n      <Event eventName=\"TriggerParticle\" time=\"0.000\" length=\"5.000\" isDuration=\"true\" guid=\"3f4a326c-6b74-4d7e-b9ac-f36378e06052\""
                                    + ">"
                                    + "\n        <TemplateObject name=\"targetId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + "\n        <TemplateObject name=\"objectSpaceId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + "\n        <String name=\"resourceName\" value=\"prefab_skill_effects/hero_skill_effects/"
                                    + heroCodeName + "/" + heroId + (skin < 10 ? "0" + skin : skin)
                                    + "/" + hasteNameRun + "\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + "\n        <Vector3 name=\"bindPosOffset\" x=\"0\" y=\"0.7\" z=\"-0.6\" refParamName=\"\" useRefParam=\"false\" />"
                                    + "\n      </Event>"
                                    + "\n    </Track>"
                                    + "\n  </Action>");
                    moded++;
                    in++;
                }

                List<String> lines = new ArrayList<>(Arrays.asList(content.split("\n")));
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains("<Track trackName=\"TriggerParticle")
                            && !(lines.get(i + 1).contains("Condition") && lines.get(i + 1).contains("Mod_by_DHA"))) {
                        lines.addAll(i + 1, listConditions);
                        i += listConditions.size();
                    } else if (lines.get(i).contains("<Condition") && !lines.get(i).contains("Mod_by_DHA")) {
                        String[] split = lines.get(i).split("\"");
                        for (int j = 0; j < split.length; j++) {
                            if (split[j].contains(" id=")) {
                                split[j + 1] = (Integer.parseInt(split[j + 1]) + targetid.length) + "";
                            }
                        }
                        lines.set(i, String.join("\"", split));
                    }
                }

                content = String.join("\n", lines);
                outputBytes = content.getBytes(StandardCharsets.UTF_8);
                DHAExtension.WriteAllBytes("D:/" + new File(inputPath).getName(),
                        outputBytes);
                outputBytes = AOVCompress(outputBytes);
                DHAExtension.WriteAllBytes(outputPath, outputBytes);
            }
            String[] subDir = new File("F:/This PC/Documents/AOV/cachemod/").list();

            for (int i = 0; i < subDir.length; i++) {
                subDir[i] = "F:/This PC/Documents/AOV/cachemod/" + subDir[i];
            }

            if (App.modPackName.equals("")) {
                ZipExtension.zipDir(subDir,
                        "F:/This PC/Documents/AOV/pack_" + targetid[0]
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            } else {
                // if (new File("F:/This PC/Documents/AOV/" + App.modPackName
                // +
                // "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                // .exists()) {
                // new File("F:/This PC/Documents/AOV/" + App.modPackName
                // +
                // "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                // .delete();
                // }
                ZipExtension.zipDir(subDir,
                        "F:/This PC/Documents/AOV/" + App.modPackName
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ModHasteMulti(String[] originId, String[] targetid, String[] hasteNames, Vector3[] hastePoses) {
        try {
            String inputZipPath = ActionsParentPath + "CommonActions.pkg.bytes";
            if (!App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/" + App.modPackName
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/" + App.modPackName
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
            } else if (App.modPackName.equals("") && new File("F:/This PC/Documents/AOV/pack_" + targetid[0]
                    + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes").exists()) {
                inputZipPath = "F:/This PC/Documents/AOV/pack_" + targetid[0]
                        + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes";
            }

            if (new File("F:/This PC/Documents/AOV/cachemod/").exists()) {
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/cachemod/");
            }
            new File("F:/This PC/Documents/AOV/cachemod/").mkdirs();
            ZipExtension.unzip(inputZipPath, "F:/This PC/Documents/AOV/cachemod/");
            String[] filemodNames = new String[] { "commonresource/HasteE1.xml", "commonresource/HasteE1_leave.xml" };
            for (int f = 0; f < filemodNames.length; f++) {
                String filemodName = filemodNames[f];
                String inputPath = "F:/This PC/Documents/AOV/cachemod/" + filemodName;
                String outputPath = inputPath;

                byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
                byte[] outputBytes = AOVDecompress(inputBytes);
                if (outputBytes == null)
                    return;

                String content = new String(outputBytes, StandardCharsets.UTF_8);

                List<String> listConditions = new ArrayList<>();

                int moded = DHAExtension.countMatches(content, "CheckHeroIdTick") / 3;

                int in = 0;
                for (int l = 0; l < targetid.length; l++) {
                    String id = targetid[l];
                    String heroId = id.substring(0, 3);
                    String skinId = id.substring(3, id.length());
                    int skin = Integer.parseInt(skinId) - 1;

                    int origin = Integer.parseInt(originId[l].substring(3)) - 1;
                    String originIdCode = heroId + (origin < 10 ? "0" + origin : origin);

                    ZipInputStream zis = new ZipInputStream(
                            new FileInputStream(AOVExtension.InfosParentPath + "Actor_" + heroId + "_Infos.pkg.bytes"));
                    String heroCodeName = zis.getNextEntry().getName().split("/")[1];
                    zis.close();

                    String hasteName, vectorBind;
                    if (hasteNames == null || hasteNames[in] == null) {
                        hasteName = "jiasu_tongyong_01";
                        vectorBind = "\n        <Vector3 name=\"bindPosOffset\" x=\"0\" y=\"0.7\" z=\"-0.6\" refParamName=\"\" useRefParam=\"false\" />";
                    } else {
                        hasteName = hasteNames[in];
                        vectorBind = "";
                    }
                    if (hastePoses == null || hastePoses[in] == null) {
                        // vectorBind = "";
                    } else {
                        vectorBind = "\n        <Vector3 name=\"bindPosOffset\" x=\"" + hastePoses[in].x + "\" y=\""
                                + hastePoses[in].y + "\" z=\"" + hastePoses[in].z
                                + "\" refParamName=\"\" useRefParam=\"false\" />";
                    }

                    content = content.replace("    <Track trackName=\"SkillFuncPerioidc0\"",
                            "    <Track trackName=\"CheckSkinIdTick" + moded
                                    + "\" eventType=\"CheckSkinIdTick\" guid=\"Mod_by_DHA_" + originIdCode
                                    + "\" enabled=\"true\" r=\"0.000\" g=\"0.000\" b=\"0.000\" stopAfterLastEvent=\"true\">"
                                    + "\n      <Event eventName=\"CheckSkinIdTick\" time=\"0.000\" isDuration=\"false\">"
                                    + "\n        <TemplateObject name=\"targetId\" id=\"1\" objectName=\"target\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\" />"
                                    + "\n        <int name=\"skinId\" value=\"" + originIdCode
                                    + "\" refParamName=\"\" useRefParam=\"false\" />"
                                    + "\n        <bool name=\"bSkipLogicCheck\" value=\"true\" refParamName=\"\" useRefParam=\"false\" />"
                                    + "\n      </Event>"
                                    + "\n    </Track>"
                                    + "\n    <Track trackName=\"SkillFuncPerioidc0\"");
                    listConditions.add("      <Condition id=\"" + (moded + 1)
                            + "\" guid=\"Mod_by_DHA_" + originIdCode
                            + "\" status=\"false\"/>");
                    content = content.replace("  </Action>",
                            "    <Track trackName=\"TriggerParticle0\" eventType=\"TriggerParticle\" guid=\"412ea073-5944-46e4-ae5e-3037e855fda7"
                                    + heroId
                                    + "\" enabled=\"true\" refParamName=\"\" useRefParam=\"false\" r=\"0.000\" g=\"0.000\" b=\"0.000\" execOnForceStopped=\"false\" execOnActionCompleted=\"false\" stopAfterLastEvent=\"true\">"
                                    + "\n      <Condition id=\"" + (moded + 1)
                                    + "\" guid=\"Mod_by_DHA_" + originIdCode
                                    + "\" status=\"true\"/>"
                                    + "\n      <Event eventName=\"TriggerParticle\" time=\"0.000\" length=\"5.000\" isDuration=\"true\" guid=\"3f4a326c-6b74-4d7e-b9ac-f36378e06052\""
                                    + ">"
                                    + "\n        <TemplateObject name=\"targetId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + "\n        <TemplateObject name=\"objectSpaceId\" objectName=\"target\" id=\"1\" isTemp=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + "\n        <uint name=\"RefLiteBulletID\" value=\"0\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + "\n        <bool name=\"bChooseResourceNameByCamp\" value=\"false\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + "\n        <String name=\"parentResourceName\" value=\"\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + "\n        <String name=\"resourceName\" value=\"prefab_skill_effects/hero_skill_effects/"
                                    + heroCodeName + "/" + heroId + (skin < 10 ? "0" + skin : skin)
                                    + "/" + hasteName + "\" refParamName=\"\" useRefParam=\"false\"/>"
                                    + vectorBind
                                    + "\n      </Event>"
                                    + "\n    </Track>"
                                    + "\n  </Action>");
                    moded++;
                    in++;
                }

                List<String> lines = new ArrayList<>(Arrays.asList(content.split("\n")));
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains("<Track trackName=\"TriggerParticle")
                            && !(lines.get(i + 1).contains("Condition") && lines.get(i + 1).contains("Mod_by_DHA"))) {
                        lines.addAll(i + 1, listConditions);
                        i += listConditions.size();
                    } else if (lines.get(i).contains("<Condition") && !lines.get(i).contains("Mod_by_DHA")) {
                        String[] split = lines.get(i).split("\"");
                        for (int j = 0; j < split.length; j++) {
                            if (split[j].contains(" id=")) {
                                split[j + 1] = (Integer.parseInt(split[j + 1]) + targetid.length) + "";
                            }
                        }
                        lines.set(i, String.join("\"", split));
                    }
                }

                content = String.join("\n", lines);
                outputBytes = content.getBytes(StandardCharsets.UTF_8);
                DHAExtension.WriteAllBytes("D:/" + new File(inputPath).getName(),
                        outputBytes);
                outputBytes = AOVCompress(outputBytes);
                DHAExtension.WriteAllBytes(outputPath, outputBytes);
            }
            String[] subDir = new File("F:/This PC/Documents/AOV/cachemod/").list();

            for (int i = 0; i < subDir.length; i++) {
                subDir[i] = "F:/This PC/Documents/AOV/cachemod/" + subDir[i];
            }

            if (App.modPackName.equals("")) {
                ZipExtension.zipDir(subDir,
                        "F:/This PC/Documents/AOV/pack_" + targetid[0]
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            } else {
                // if (new File("F:/This PC/Documents/AOV/" + App.modPackName
                // +
                // "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                // .exists()) {
                // new File("F:/This PC/Documents/AOV/" + App.modPackName
                // +
                // "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes")
                // .delete();
                // }
                ZipExtension.zipDir(subDir,
                        "F:/This PC/Documents/AOV/" + App.modPackName
                                + "/files/Resources/1.50.1/Ages/Prefab_Characters/Prefab_Hero/CommonActions.pkg.bytes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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