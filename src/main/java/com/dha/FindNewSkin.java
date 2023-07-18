package com.dha;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FindNewSkin {
    static List<String> skinIdNotAccept = Arrays.asList(new String[] {
            "5032", "1863"
    });

    public static void main(String[] args) throws Exception {
        // autoFind();
        // changeLabel();
        autoFindNew();
        changeLabelNew();
    }

    public static void autoFindNew() throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<String> skinIdList = new ArrayList<>();
        Map<String, String> skinName = new HashMap<>();
        LanguageMap languageMap = new LanguageMap(new String(
                AOVAnalyzer.AOVDecompress(DHAExtension.ReadAllBytes(AOVModHelper.LanguagePath + "languageMap_Xls.txt")))
                + "\n" + new String(AOVAnalyzer.AOVDecompress(
                        DHAExtension.ReadAllBytes(AOVModHelper.LanguagePath + "lanMapIncremental.txt"))));
        ListIconElement iconList = new ListIconElement(AOVAnalyzer
                .AOVDecompress(DHAExtension.ReadAllBytes(AOVModHelper.DatabinPath + "Actor/heroSkin.bytes")));
        List<Hero> heroList = gson.fromJson(DHAExtension.ReadAllText("D:/skinlist(label).json"), HeroList.class).heros;
        for (Hero hero : heroList) {
            for (Skin skin : hero.skins) {
                skinIdList.add(skin.id);
                skinName.put(skin.id, skin.name);
            }
        }
        String skinlistTxt = "";

        for (String child : new File(AOVModHelper.InfosParentPath).list()) {
            String inputZipPath = AOVModHelper.InfosParentPath + child;
            String heroId = child.substring(6, 9);
            try {
                if (Integer.parseInt(heroId) > 600)
                    continue;
            } catch (Exception e) {
                continue;
            }
            if (new File(AOVModHelper.cacheModPath).exists()) {
                DHAExtension.deleteDir(AOVModHelper.cacheModPath);
            }
            new File(AOVModHelper.cacheModPath).mkdirs();
            ZipExtension.unzip(inputZipPath, AOVModHelper.cacheModPath);

            String filemodName = "Prefab_Hero/";
            String heroCodeName = new File(AOVModHelper.cacheModPath + filemodName).list()[0];
            filemodName = filemodName + heroCodeName + "/" + heroCodeName + "_actorinfo.bytes";
            byte[] outputBytes = AOVAnalyzer
                    .AOVDecompress(DHAExtension.ReadAllBytes(AOVModHelper.cacheModPath + filemodName));
            if (outputBytes == null)
                continue;
            Hero hero = null;
            for (int i = 0; i < heroList.size(); i++) {
                if (heroList.get(i).id.equals(heroId)) {
                    hero = heroList.get(i);
                }
            }
            if (hero == null) {
                hero = new Hero();
                hero.id = heroId;
                hero.name = null;
            }

            Element element = new Element(outputBytes);
            for (Element skin : element.getChild("SkinPrefab").childList) {
                String code = skin.getChild("ArtSkinPrefabLOD").getChild(0).valueS;
                if (code == null || !code.contains("/"))
                    continue;
                String[] split = code.split("/");
                String id = split[split.length - 1].split("_")[0];
                if (id.length() < 4 || id.equals(id.substring(0, 3) + "1") || skinIdNotAccept.contains(id))
                    continue;
                if (skinIdList.contains(id)) {
                    // System.out.println(id + ": " + hero.name + " " + skinName.get(id));
                    // skinlistTxt += "\n" + id + ": " + hero.name + " " + skinName.get(id);
                    continue;
                }
                skinIdList.add(id);

                IconElement icon = iconList.get(Integer.parseInt(heroId) * 100 + Integer.parseInt(id.substring(3)) - 1);
                if (icon == null || icon.heronamecode == null || icon.skinnamecode == null
                        || languageMap.getValue(icon.skinnamecode) == null
                        || languageMap.getValue(icon.skinnamecode).contains("[ex]"))
                    continue;
                System.out.println(" *" + id + ": " + languageMap.getValue(icon.heronamecode) + " "
                        + languageMap.getValue(icon.skinnamecode));
                skinlistTxt += "\n *" + id + ": " + languageMap.getValue(icon.heronamecode) + " "
                        + languageMap.getValue(icon.skinnamecode);
                if (hero.name == null) {
                    hero.name = languageMap.getValue(icon.heronamecode);
                }
                Skin sk = new Skin();
                if (skin.containsChild("PreloadAnimatorEffects")) {
                    Element c = skin.getChild("PreloadAnimatorEffects").getChild(0);
                    if (c != null) {
                        String[] split2 = c.valueS.split("/");
                        sk.hasteName = split2[split2.length - 1];
                    }
                }
                sk.id = id;
                sk.name = languageMap.getValue(icon.skinnamecode);
                hero.skins.add(sk);
            }
        }

        DHAExtension.WriteAllText("D:/skinlist(label)(new).json", gson.toJson(new HeroList(heroList)));
        DHAExtension.WriteAllText("D:/skinlist.txt", skinlistTxt);
    }

    public static void changeLabelNew() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Scanner scanner = new Scanner(System.in);
        List<String> labelAccepts = Arrays.asList(new String[] {
                "a", "s", "s+", "ss", "sss",
                "a2", "s2", "s+2", "ss2",
                "0", "n"
        });
        List<Hero> heroList = gson.fromJson(DHAExtension.ReadAllText("D:/skinlist(label)(new).json"),
                HeroList.class).heros;
        for (int i = 0; i < heroList.size(); i++) {
            Hero hero = heroList.get(i);
            List<Skin> skinList = hero.skins;
            for (int j = 0; j < skinList.size(); j++) {
                if (skinList.get(j).label == null) {
                    System.out.print(
                            "Nhap bac cho skin " + hero.name + skinList.get(j).name + "(" + skinList.get(j).id + "): ");
                    String label;
                    while (!labelAccepts.contains(label = scanner.nextLine())) {
                        System.out.print("Nhap lai bac cho skin " + hero.name + skinList.get(j).name + "("
                                + skinList.get(j).id + "): ");
                    }
                    switch (label) {
                        case "a":
                            skinList.get(j).label = SkinLabel.A;
                            break;
                        case "a2":
                            skinList.get(j).label = SkinLabel.A_HH;
                            break;
                        case "s":
                            skinList.get(j).label = SkinLabel.S;
                            break;
                        case "s2":
                            skinList.get(j).label = SkinLabel.S_HH;
                            break;
                        case "s+":
                            skinList.get(j).label = SkinLabel.S_Plus;
                            break;
                        case "s+2":
                            skinList.get(j).label = SkinLabel.S_Plus_HH;
                            break;
                        case "ss":
                            skinList.get(j).label = SkinLabel.SS;
                            break;
                        case "ss2":
                            skinList.get(j).label = SkinLabel.SS_HH;
                            break;
                        case "sss":
                            skinList.get(j).label = SkinLabel.SSS_HH;
                            skinList.get(j).hasDeathEffect = true;
                            break;
                        case "0":
                            return;
                        case "n":
                            skinList.remove(j);
                            j--;
                            break;
                    }
                    DHAExtension.WriteAllText("D:/skinlist(label)(new).json", gson.toJson(new HeroList(heroList)));
                }
            }
        }
        scanner.close();
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
