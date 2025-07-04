package com.dha;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ModNew {
    static ModSettings modSettings = new ModSettings(true, true, true, true, true, true, true, true);
    static AOVModHelper modHelper = new AOVModHelper();
    static int skinLevelReplace = 2;

    public static void main(String[] args) throws Exception {
        List<String> skinidList = new ArrayList<>();
        List<Skin> skinList = new ArrayList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Vui long cho...");
        List<Hero> heroList = gson.fromJson(DHAExtension.ReadAllText(AOVModHelper.heroListJsonPath),
                HeroList.class).heros;

        for (Hero hero : heroList) {
            for (Skin skin : hero.skins) {
                Skin skin2 = skin;
                skin.name = hero.name + " " + skin.name;
                skinidList.add(skin.id);
                skinList.add(skin2);
            }
        }

        modHelper.setEcho(true);
        while (true) {
            String modPackName = "";
            System.out.println(
                    "------------------------------------------------------------------------------------------------");
            System.out.print("Lua chon 0: Thoat\nLua chon 1: Mod skin le\nLua chon 2: Mod Pack\n  Nhap lua chon: ");
            String luachon = scanner.nextLine();
            while (!luachon.equals("1") && !luachon.equals("2") && !luachon.equals("0")) {
                System.out.print("Nhap lai lua chon: ");
                luachon = scanner.nextLine();
            }
            if (luachon.equals("0")) {
                scanner.close();
                return;
            }
            if (luachon.equals("1")) {
                String id = "";
                System.out.print("Nhap id skin muon mod: ");
                while (!skinidList.contains(id = scanner.nextLine()) && !id.equals("0")) {
                    System.out.print("Nhap lai id skin muon mod: ");
                }
                if (id.equals("0")) {
                    continue;
                }
                modPackName = "pack_" + id;
                modHelper.setModPackName(modPackName);
                Skin skin = skinList.get(skinidList.indexOf(id));
                List<ModInfo> modList = new ArrayList<>();
                List<Skin> targetSkins = new ArrayList<>();
                targetSkins.add(new Skin(id.substring(0, 3) + "1", SkinLabel.Default));
                if (skin.label.skinLevel >= skinLevelReplace) {
                    for (Hero herocheck : heroList) {
                        if (herocheck.id.equals(id.substring(0, 3))) {
                            for (Skin skin2 : herocheck.skins) {
                                if (skin2.label.skinLevel < 2 && !skin2.id.equals(skin.id)) {
                                    targetSkins.add(skin2);
                                }
                            }
                            break;
                        }
                    }
                }
                modList.add(new ModInfo(targetSkins, skin, modSettings));
                long start = System.currentTimeMillis();
                modHelper.modSkin(modList);
                long end = System.currentTimeMillis();
                System.out.println("Mod xong sau " + (end - start) / 1000f + " giay");
                continue;
            } else if (luachon.equals("2")) {
                System.out.print("Nhap ten pack: ");
                while ((modPackName = scanner.nextLine().trim()).equals("")){
                    System.out.print("Nhap lai ten pack: ");
                }
                if (new File(AOVModHelper.saveModPath + modPackName).exists()) {
                    System.out.print("Da co pack " + modPackName + " ban co muon loai bo pack cu ? (C/k): ");
                    luachon = scanner.nextLine().toLowerCase();
                    while (!luachon.equals("c") && !luachon.equals("k")) {
                        System.out.print("Nhap lai lua chon: ");
                        luachon = scanner.nextLine().toLowerCase();
                    }
                    if (luachon.equals("k"))
                        continue;
                }
                modHelper.setModPackName(modPackName);
                System.out.print("Nhap cac id skin: ");
                List<ModInfo> modList = new ArrayList<>();
                List<String> heroIdMods = new ArrayList<>();
                String[] idlist = scanner.nextLine().trim().split(" ");
                if (idlist.length==1 && idlist[0].equals("0"))
                    continue;
                boolean thieu = false, trungtuong = false;
                String idtuongtrung="";

                for (String id : idlist) {
                    if (!tryParse(id)){
                        thieu = true;
                        System.out.println("Khong the xac dinh id " + id);
                    }
                        
                    if (heroIdMods.contains(id.substring(0, 3))) {
                        idtuongtrung = id.substring(0,3);
                        trungtuong = true;
                        break;
                    }
                    if (skinidList.indexOf(id) != -1) {
                        heroIdMods.add(id.substring(0, 3));
                        Skin skin = skinList.get(skinidList.indexOf(id));
                        List<Skin> targetSkins = new ArrayList<>();
                        targetSkins.add(new Skin(id.substring(0, 3) + "1", SkinLabel.Default));
                        if (skin.getSkinLevel() >= skinLevelReplace) {
                            for (Hero herocheck : heroList) {
                                if (herocheck.id.equals(id.substring(0, 3))) {
                                    for (Skin skin2 : herocheck.skins) {
                                        if (skin2.label.skinLevel < 2 && !skin2.id.equals(skin.id)) {
                                            targetSkins.add(skin2);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        modList.add(new ModInfo(targetSkins, skin, modSettings));
                    } else {
                        System.out.println("Khong tim thay id " + id);
                        thieu = true;
                    }
                }
                if (trungtuong) {
                    System.out.println("Co 2 skin trung tuong " + idtuongtrung);
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
                long start = System.currentTimeMillis();
                modHelper.modSkin(modList);
                long end = System.currentTimeMillis();

                long milliSec = end-start;
                DateFormat simple = new SimpleDateFormat(
                        "mm:ss.SSS");
                Date result = new Date(milliSec);
                System.out.println("Mod xong sau " + simple.format(result));
            }
        }
    }

    public static boolean tryParse(String id) {
        try{
            Integer.parseInt(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
