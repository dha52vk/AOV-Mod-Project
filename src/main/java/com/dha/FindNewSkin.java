package com.dha;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FindNewSkin {
    public static void main(String[] args) throws IOException {
        autoFind();
        changeLabel();
    }

    public static void changeLabel() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Scanner scanner = new Scanner(System.in);
        String label;
        List<String> labelsAccept = new ArrayList<>();
        labelsAccept.add("a");
        labelsAccept.add("s");
        labelsAccept.add("s+");
        labelsAccept.add("ss");
        labelsAccept.add("sss");
        labelsAccept.add("0");
        labelsAccept.add("n");
        List<Hero> heroList = gson.fromJson(DHAExtension.ReadAllText("D:/skinlist(auto).json"),
                HeroList.class).heros;
        for (int i = 0; i < heroList.size(); i++) {
            for (int j = 0; j < heroList.get(i).skins.size(); j++) {
                if (!heroList.get(i).skins.get(j).changedLabel) {
                    Skin skin = heroList.get(i).skins.get(j);
                    System.out.print(skin.id + " - " + heroList.get(i).name + " " + skin.name + ": ");
                    while (!labelsAccept.contains(label = scanner.nextLine().toLowerCase())) {
                        System.out.print("Nhap lai bac: ");
                    }
                    switch (label) {
                        case "a":
                            skin.changedLabel = true;
                            skin.label = SkinLabel.A;
                            break;
                        case "a2":
                            skin.changedLabel = true;
                            skin.label = SkinLabel.A_HH;
                            break;
                        case "s":
                            skin.changedLabel = true;
                            skin.label = SkinLabel.S;
                            break;
                        case "s2":
                            skin.changedLabel = true;
                            skin.label = SkinLabel.S_HH;
                            break;
                        case "s+":
                            skin.changedLabel = true;
                            skin.label = SkinLabel.S_Plus;
                            break;
                        case "s+2":
                            skin.changedLabel = true;
                            skin.label = SkinLabel.S_Plus_HH;
                            break;
                        case "ss":
                            skin.changedLabel = true;
                            skin.label = SkinLabel.SS;
                            break;
                        case "ss2":
                            skin.changedLabel = true;
                            skin.label = SkinLabel.SS_HH;
                            break;
                        case "sss":
                            skin.changedLabel = true;
                            skin.label = SkinLabel.SSS_HH;
                            break;
                        case "n":
                            heroList.get(i).skins.remove(j);
                            DHAExtension.WriteAllText("D:/skinlist(auto).json",
                                    gson.toJson(new HeroList(heroList)));
                            continue;
                        case "0":
                            return;
                    }
                    heroList.get(i).skins.set(j, skin);
                    DHAExtension.WriteAllText("D:/skinlist(auto).json", gson.toJson(new HeroList(heroList)));
                }
            }
        }
        DHAExtension.WriteAllText("D:/skinlist(auto).json", gson.toJson(new HeroList(heroList)));
    }

    public static void autoFind() {
        List<Hero> heroListBefore = new ArrayList<>();
        List<String> skinidList = new ArrayList<>();
        List<String> skinLines = new ArrayList<>();
        List<Hero> heroList = new ArrayList<>();
        Hero heroNow;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        heroListBefore = gson.fromJson(DHAExtension.ReadAllText("D:/skinlist(auto).json"), HeroList.class).heros;

        String inputPath = AOVExtension.DatabinPath + "Actor/heroSkin.bytes";
        byte[] inputBytes = DHAExtension.ReadAllBytes(inputPath);
        byte[] iconBytes = AOVExtension.AOVDecompress(inputBytes);

        inputPath = AOVExtension.LanguagePath + "languageMap_Xls.txt";
        inputBytes = DHAExtension.ReadAllBytes(inputPath);
        byte[] languageBytes = AOVExtension.AOVDecompress(inputBytes);

        String languageMap = new String(languageBytes, StandardCharsets.UTF_8);

        inputPath = AOVExtension.LanguagePath + "lanMapIncremental.txt";
        inputBytes = DHAExtension.ReadAllBytes(inputPath);
        byte[] languageBytes2 = AOVExtension.AOVDecompress(inputBytes);

        String languageMap2 = new String(languageBytes2, StandardCharsets.UTF_8);

        inputPath = AOVExtension.DatabinPath + "Shop/HeroSkinShop.bytes";
        inputBytes = DHAExtension.ReadAllBytes(inputPath);
        byte[] labelBytes = AOVExtension.AOVDecompress(inputBytes);

        for (String child : new File(AOVExtension.InfosParentPath).list()) {
            String inputZipPath = AOVExtension.InfosParentPath + child;
            String heroId = child.substring(6, 9);
            try {
                if (Integer.parseInt(heroId) > 550)
                    continue;
            } catch (Exception e) {
                continue;
            }
            boolean had = false;
            heroNow = new Hero();
            for (Hero hero : heroListBefore) {
                if (hero.id.equals(heroId)) {
                    heroNow = hero;
                    for (Skin skin : hero.skins) {
                        skinidList.add(skin.id);
                        skinLines.add(skin.id + ": " + hero.name + " " + skin.name + " : old");
                        System.out.println(skin.id + ": " + hero.name + " " + skin.name + " : old");
                    }
                    had = true;
                    break;
                }
            }
            if (!had) {
                heroNow.id = heroId;
                heroNow.skins = new ArrayList<>();
            }
            if (new File("F:/This PC/Documents/AOV/test/").exists()) {
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/test/");
            }
            new File("F:/This PC/Documents/AOV/test/").mkdirs();
            ZipExtension.unzip(inputZipPath, "F:/This PC/Documents/AOV/test/");

            String filemodName = "Prefab_Hero/";
            String heroCodeName = new File("F:/This PC/Documents/AOV/test/" + filemodName).list()[0];
            filemodName = filemodName + heroCodeName + "/" + heroCodeName + "_actorinfo.bytes";

            inputPath = "F:/This PC/Documents/AOV/test/" + filemodName;

            inputBytes = DHAExtension.ReadAllBytes(inputPath);
            byte[] outputBytes = AOVExtension.AOVDecompress(inputBytes);

            int start = 0, end = 30;

            String code = "";
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
                String[] splits = code.split("/");
                String skinId = splits[splits.length - 1].split("_")[0];
                if (skinId.equals("5032")
                        || skinId.equals("1863"))
                    continue;
                if (skinId.length() > 3 && tryParse(skinId) && !skinId.equals(skinId.substring(0, 3) + "1")
                        && !skinidList.contains(skinId)) {
                    // boolean had = false;
                    // for (Hero hero : heroListBefore) {
                    // for (Skin skin : hero.skins) {
                    // if (skinId.equals(skin.id)) {
                    // heroNow.name = hero.name;
                    // heroNow.skins.add(skin);
                    // skinLines.add(skinId + ": " + hero.name + " " + skin.name + " : old");
                    // System.out.println(skinId + ": " + hero.name + " " + skin.name + " : old");
                    // skinidList.add(skinId);

                    // had=true;
                    // break;
                    // }
                    // }
                    // if (had)
                    // break;
                    // }
                    // if (had)
                    // continue;

                    String originId = skinId.substring(0, 3)
                            + (Integer.parseInt(skinId.substring(3, skinId.length())) - 1) + "";

                    int originLength = 0;

                    start = DHAExtension.indexOf(iconBytes,
                            ("Share_" + originId.substring(0, 3)
                                    + (originId.substring(3).length() == 2 ? originId.substring(3)
                                            : "0" + originId.substring(3)))
                                    .getBytes())
                            - 130;
                    if (start > 0) {
                        while (iconBytes[start - originLength - 1] >= '0'
                                && iconBytes[start - originLength - 1] <= '9') {
                            originLength++;
                        }
                        originLength -= 2;
                        start -= originLength + 70;
                        String heroCode = new String(Arrays.copyOfRange(iconBytes, start + 16, start + 35),
                                StandardCharsets.UTF_8);
                        String skinCode = new String(Arrays.copyOfRange(iconBytes, start + 44, start + 63),
                                StandardCharsets.UTF_8);
                        int index = languageMap.indexOf(heroCode);
                        if (index < 0)
                            continue;
                        String lineCode = languageMap.substring(index).split("=")[1].split("_##")[0];
                        String heroName = lineCode.substring(0, lineCode.length() - 17);
                        if (heroName.contains("[")) {
                            index = languageMap2.indexOf(heroCode);
                            if (index < 0)
                                continue;
                            lineCode = languageMap2.substring(index).split("=")[1].split("_##")[0];
                            heroName = lineCode.substring(0, lineCode.length() - 17);
                            if (heroName.contains("["))
                                continue;
                        }
                        heroNow.name = heroName;
                        index = languageMap.indexOf(skinCode);
                        if (index < 0)
                            continue;
                        lineCode = languageMap.substring(index).split("=")[1].split("_##")[0];
                        String skinName = lineCode.substring(0, lineCode.length() - 17);
                        if (skinName.contains("[ex]")) {
                            index = languageMap2.indexOf(skinCode);
                            if (index < 0)
                                continue;
                            lineCode = languageMap2.substring(index).split("=")[1].split("_##")[0];
                            skinName = lineCode.substring(0, lineCode.length() - 17);
                            if (skinName.contains("[ex]")) {
                                continue;
                            }
                        }
                        heroName = heroName.substring(1);
                        skinName = skinName.substring(1);

                        byte[] iconCode = new byte[] { iconBytes[start + 4], iconBytes[start + 5] };
                        String labelName;
                        start = 0;
                        while ((start = DHAExtension.indexOf(labelBytes,
                                DHAExtension.mergeBytes(new byte[] { 0 }, iconCode, new byte[] { 0 }), start + 4)
                                - 3) > 0) {
                            if (labelBytes[start + 15] == 0
                                    && ((labelBytes[start + 16] >= 'A' && labelBytes[start + 16] <= 'Z')
                                            || (labelBytes[start + 16] >= '0' && labelBytes[start + 16] <= '9'))) {
                                break;
                            }
                        }
                        if (start < 0) {
                            // System.out.println(skinId + " (" + iconCode[0] + ", " + iconCode[1] + ")" + "
                            // not found");
                            labelName = "unknown";
                        } else {
                            // int end2 = DHAExtension.indexOf(labelBytes, "Label".getBytes(), start);
                            int end2 = start + 103;
                            start = end2;
                            while ((labelBytes[end2] >= 'A' && labelBytes[end2] <= 'Z')
                                    || (labelBytes[end2] >= 'a' && labelBytes[end2] <= 'z')
                                    || (labelBytes[end2] >= '0' && labelBytes[end2] <= '9')
                                    || labelBytes[end2] == '_'
                                    || labelBytes[end2] == '.') {
                                end2++;
                            }

                            labelName = new String(Arrays.copyOfRange(labelBytes, start, end2),
                                    StandardCharsets.UTF_8);
                        }

                        Skin skin = new Skin();
                        skin.id = skinId;
                        skin.name = skinName;
                        if (labelName.toLowerCase().contains("aic")) {
                            skin.label = SkinLabel.S_Plus;
                            skin.changedLabel = true;
                        } else
                            switch (labelName) {
                                case "Label_A_Limited.png":
                                    skin.label = SkinLabel.A_HH;
                                    break;
                                case "Label_A_new.png":
                                case "_limited_paiweisai_S6":
                                case "_xinnian":
                                    skin.label = SkinLabel.A;
                                    skin.changedLabel = true;
                                    break;
                                case "Label_S_Limited.png":
                                    skin.label = SkinLabel.S_HH;
                                    break;
                                case "Label_S_new.png":
                                case "Label_Hallween_limited_new.png":
                                case "Label_AWC_new.png":
                                case "_shengdan":
                                    skin.label = SkinLabel.S;
                                    skin.changedLabel = true;
                                    break;
                                case "Label_Splus_limit.png":
                                    skin.label = SkinLabel.S_Plus_HH;
                                    break;
                                case "Label_Splus.png":
                                case "Label_LNY_new.png":
                                case "Label_LNY_Chroma_new.png":
                                case "Label_AWC_ADC.png":
                                case "missaov.png":
                                    skin.label = SkinLabel.S_Plus;
                                    skin.changedLabel = true;
                                    break;
                                case "Label_SS_limited.png":
                                    skin.label = SkinLabel.SS_HH;
                                    break;
                                case "Label_SS_chroma_new.png":
                                    skin.label = SkinLabel.SS_Chroma;
                                    break;
                                case "Label_SS_new.png":
                                case "_lv5":
                                case "Ultraman.png":
                                case "Label_2st_Anniversary.png":
                                    skin.label = SkinLabel.SS;
                                    skin.changedLabel = true;
                                    break;
                                case "Label_T2_BF.png":
                                case "Label_SSS_Limited.png":
                                case "Label_50108_T2.png":
                                    skin.label = SkinLabel.SSS_HH;
                                    skin.changedLabel = true;
                                    break;
                            }
                        heroNow.skins.add(skin);

                        skinLines.add(" *" + skinId + ": " + heroName + " " + skinName + " :  " + labelName);
                        System.out.println(" *" + skinId + ": " + heroName + " " + skinName + " :  " + labelName);
                        skinidList.add(skinId);
                    }
                }
            }
            heroList.add(heroNow);
        }

        DHAExtension.WriteAllLines("D:/skinlist.txt", skinLines.toArray(new String[0]));
        DHAExtension.WriteAllText("D:/skinlist(auto).json", gson.toJson(new HeroList(heroList)));
    }

    public static boolean tryParse(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
