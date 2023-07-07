package com.dha;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

/**
 * Hello world!
 *
 */
public class App {
    public static Scanner scanner;
    public static Gson gson;
    public static String modPackName = "";
    public static HeroList heroList = new HeroList();

    public static void main(String[] args) {
        List<String> skinidList = new ArrayList<>();
        List<Skin> skinList = new ArrayList<>();
        gson = new GsonBuilder().setPrettyPrinting().create();
        scanner = new Scanner(System.in);

        System.out.println("Vui long cho...");
        heroList = gson.fromJson(DHAExtension.ReadAllText("D:/skinlist(label).json"), HeroList.class);
        // for (int i = 0; i < heroList.heros.size(); i++){
        //     for (int j = 0; j < heroList.heros.get(i).skins.size(); j++){
        //         if (heroList.heros.get(i).skins.get(j).label == SkinLabel.SSS_HH){
        //             heroList.heros.get(i).skins.get(j).hasDeathEffect = true;
        //         }
        //     }
        // }
        DHAExtension.WriteAllText("D:/test.json", gson.toJson(heroList));
        for (Hero hero : heroList.heros) {
            for (Skin skin : hero.skins) {
                skinidList.add(skin.id);
                Skin skin2 = skin;
                skin.name = hero.name + " " + skin.name;
                skinList.add(skin2);
            }
        }

        while (true) {
            modPackName = "";
            System.out.println("-------------------------------------------------------------------");
            System.out.print("Lua chon 0: Thoat\nLua chon 1: Mod skin le\nLua chon 2: Mod Pack\n  Nhap lua chon: ");
            String luachon = scanner.nextLine();
            while (!luachon.equals("1") && !luachon.equals("2") && !luachon.equals("0")) {
                System.out.print("Nhap lai lua chon: ");
                luachon = scanner.nextLine();
            }
            if (luachon.equals("0")) {
                return;
            }
            if (luachon.equals("1")) {
                String id = "", originid = "";
                System.out.print("Nhap id skin muon mod: ");
                while (!skinidList.contains(id = scanner.nextLine())) {
                    System.out.print("Nhap lai id skin muon mod: ");
                }
                DHAExtension.deleteDir("F:/This PC/Documents/AOV/pack_" + id);
                App.modPackName = "pack_" + id;
                Skin skin = skinList.get(skinidList.indexOf(id));
                Hero hero = null;
                for (Hero herocheck : heroList.heros) {
                    if (herocheck.id.equals(id.substring(0, 3))) {
                        hero = herocheck;
                    }
                }
                AOVExtension.ModSkin(hero, skin);
                switch (skin.label) {
                    case SS:
                    case SS_HH:
                    case SS_Chroma:
                        System.out.println("Dang mod hieu ung bien ve...");
                        AOVExtension.ModBack(new String[]{id}, new boolean[]{skin.isAwakeSkin});
                        break;
                    case SSS_HH:
                        System.out.println("Dang mod hieu ung bien ve...");
                        AOVExtension.ModBack(new String[]{id}, new boolean[]{skin.isAwakeSkin});
                        System.out.println("Dang mod hieu ung gia toc...");
                        AOVExtension.ModHaste(new String[] { id }, new String[] { skin.hasteName },
                                new String[] { skin.hasteNameRun }, new String[] { skin.hasteNameEnd });
                        break;
                    default:
                        break;
                }
                System.out.println("Dang tao credit...");
                AOVExtension.MakeCredit(new String[] { skin.id },
                        new String[] { "<color=#FFFF00>Mod skin</color> <color=#0096FF>" + skin.name
                                + "</color> bởi <color=#4CBB17>AH MOD AOV</color>" });
                DHAExtension.WriteAllText("F:/This PC/Documents/AOV/pack_" + skin.id + "/packinfo.txt",
                        "\n   + " + skin.name);
                continue;
            } else if (luachon.equals("2")) {
                System.out.print("Nhap ten pack: ");
                modPackName = scanner.nextLine().trim();
                System.out.print("Nhap cac id skin: ");
                List<Skin> modList = new ArrayList<>();
                String[] idlist = scanner.nextLine().trim().split(" ");
                List<String> idModBack = new ArrayList<>();
                List<Boolean> isAwakeSkinModBack = new ArrayList<>();
                List<String> idModHaste = new ArrayList<>();
                List<String> hasteNames = new ArrayList<>();
                List<String> hasteNameRuns = new ArrayList<>();
                List<String> hasteNameEnds = new ArrayList<>();
                List<Hero> heromods = new ArrayList<>();
                boolean thieu = false, trungtuong = false;
                List<String> skinidCredit = new ArrayList<>();
                List<String> creditList = new ArrayList<>();

                String packinfo = "";
                for (String id : idlist) {
                    for (Hero hero : heromods) {
                        if (hero.id == id.substring(0, 3)) {
                            trungtuong = true;
                            break;
                        }
                    }
                    if (trungtuong)
                        break;
                    if (skinidList.indexOf(id) != -1) {
                        skinidCredit.add(id);
                        for (Hero herocheck : heroList.heros) {
                            if (herocheck.id.equals(id.substring(0, 3))) {
                                heromods.add(herocheck);
                            }
                        }
                        Skin skin = skinList.get(skinidList.indexOf(id));
                        creditList.add("<color=#FFFF00>Mod skin</color>  <color=#0096FF>" + skin.name
                                + "</color>  bởi <color=#4CBB17>AH MOD AOV</color>");
                        switch (skin.label) {
                            case SS:
                            case SS_HH:
                            case SS_Chroma:
                                idModBack.add(skin.id);
                                isAwakeSkinModBack.add(skin.isAwakeSkin);
                                break;
                            case SSS_HH:
                                idModBack.add(skin.id);
                                idModHaste.add(skin.id);
                                hasteNames.add(skin.hasteName);
                                hasteNameRuns.add(skin.hasteNameRun);
                                hasteNameEnds.add(skin.hasteNameEnd);
                                break;
                            default:
                                break;
                        }
                        modList.add(skin);
                        packinfo += "\n   + " + skin.name;
                    } else {
                        System.out.println("Khong tim thay id " + id);
                        thieu = true;
                    }
                }
                if (trungtuong) {
                    System.out.println("Co 2 skin trung tuong");
                    continue;
                }
                if (thieu) {
                    System.out.print("Ban co muon tiep tuc? (C/k): ");
                    luachon = scanner.nextLine().toLowerCase();
                    while (!luachon.equals("c") && !luachon.equals("k")) {
                        System.out.print("Nhap lai lua chon: ");
                        luachon = scanner.nextLine().toLowerCase();
                    }
                    if (luachon.equals("k"))
                        continue;
                }
                if (new File("F:/This PC/Documents/AOV/" + modPackName).exists()) {
                    System.out.print("Da co pack " + modPackName + " ban co muon loai bo pack cu ? (C/k): ");
                    luachon = scanner.nextLine().toLowerCase();
                    while (!luachon.equals("c") && !luachon.equals("k")) {
                        System.out.print("Nhap lai lua chon: ");
                        luachon = scanner.nextLine().toLowerCase();
                    }
                    if (luachon.equals("k"))
                        continue;
                    DHAExtension.deleteDir("F:/This PC/Documents/AOV/" + modPackName);
                }
                long start = System.currentTimeMillis();
                new File("F:/This PC/Documents/AOV/" + modPackName).mkdirs();
                for (int i = 0; i < modList.size(); i++) {
                    System.out.println("- Modding " + (i + 1) + "/" + modList.size());
                    Skin skin = modList.get(i);
                    AOVExtension.ModSkin(heromods.get(i), skin);
                }
                System.out.println("Dang tao credit mod...");
                AOVExtension.MakeCredit(skinidCredit.toArray(new String[0]), creditList.toArray(new String[0]));
                DHAExtension.copy("F:/This PC/Documents/AOV/" + modPackName,
                        "F:/This PC/Documents/AOV/" + modPackName + "(may yeu)");
                if (idModBack.size() != 0) {
                    System.out.println("Dang mod hieu ung bien ve...");
                    boolean[] isAwakeSkins = new boolean[isAwakeSkinModBack.size()];
                    for (int i =0;i < isAwakeSkinModBack.size(); i++){
                        isAwakeSkins[i] = isAwakeSkinModBack.get(i);
                    }
                    AOVExtension.ModBack(idModBack.toArray(new String[0]), isAwakeSkins);
                }
                if (idModHaste.size() != 0) {
                    System.out.println("Dang mod hieu ung gia toc...");
                    AOVExtension.ModHaste(idModHaste.toArray(new String[0]), hasteNames.toArray(new String[0]),
                            hasteNameRuns.toArray(new String[0]), hasteNameEnds.toArray(new String[0]));
                }
                DHAExtension.WriteAllText("F:/This PC/Documents/AOV/" + modPackName + "/packinfo.txt", packinfo);
                long end = System.currentTimeMillis();
                System.out.println("Mod xong sau " + (end - start) / 1000f + " sec");
            }
        }

        // scanner.close();
    }

    public static boolean tryParse(String i) {
        try {
            Integer.parseInt(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void ktFile(String filePath1, String filePath2) {
        if (new File(filePath1).isDirectory() && new File(filePath2).isDirectory()) {
            for (String child : new File(filePath1).list()) {
                if (new File(filePath2 + "/" + child).exists()) {
                    ktFile(filePath1 + "/" + child, filePath2 + "/" + child);
                }
            }
            return;
        }
        // byte[] b1 = DHAExtension.ReadAllBytes(filePath1);
        // byte[] b2 = DHAExtension.ReadAllBytes(filePath2);
        byte[] b1 = DHAExtension.ReadAllBytes(filePath1);
        byte[] b2 = DHAExtension.ReadAllBytes(filePath2);
        if (b1.equals(b2))
            return;
        int k = ktFile(b1, b2);
        if (k != -1)
            System.out.println(filePath1 + " <> " + filePath2 + " at: " + k + "(" + b1[k] + ", " + b2[k] + ")");
    }

    public static int ktFile(byte[] b1, byte[] b2) {
        int kt = -1;
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]
                    && !(b1[i] >= 'A' && b1[i] <= 'Z'
                            && b1[i] + 32 == b2[i])
                    && !(b1[i] >= 'a' && b1[i] <= 'z'
                            && b1[i] - 32 == b2[i])) {
                kt = i;
                break;
            }
        }
        return kt;
    }
}
