package com.dha;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AOVModHelper {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modInfos(List<ModInfo> modList) throws Exception {
        if (echo) {
            update(" Dang mod ngoai hinh cua pack " + modPackName + "...");
        }
        Element[] creditElements = new Element[] {
                new Element(new byte[] { 111, 0, 0, 0, 11, 0, 0, 0, 89, 84, 66, 76, 105, 110, 107, 88, 0, 0, 0, 3, 0, 0,
                        0, 13, 0, 0, 0, 6, 0, 0, 0, 74, 84, 80, 114, 105, 25, 0, 0, 0, 8, 0, 0, 0, 84, 121, 112, 101,
                        83, 121, 115, 116, 101, 109, 46, 83, 116, 114, 105, 110, 103, 42, 0, 0, 0, 5, 0, 0, 0, 86, 104,
                        116, 116, 112, 115, 58, 47, 47, 119, 119, 119, 46, 121, 111, 117, 116, 117, 98, 101, 46, 99,
                        111, 109, 47, 64, 65, 72, 77, 79, 68, 65, 79, 86, 4, 0, 0, 0, 4, 0, 0, 0 }),
                new Element(new byte[] { 89, 0, 0, 0, 14, 0, 0, 0, 67, 104, 97, 110, 110, 101, 108, 89, 84, 66, 63, 0,
                        0, 0, 3, 0, 0, 0, 13, 0, 0, 0, 6, 0, 0, 0, 74, 84, 80, 114, 105, 25, 0, 0, 0, 8, 0, 0, 0, 84,
                        121, 112, 101, 83, 121, 115, 116, 101, 109, 46, 83, 116, 114, 105, 110, 103, 17, 0, 0, 0, 5, 0,
                        0, 0, 86, 65, 72, 77, 79, 68, 65, 79, 86, 4, 0, 0, 0, 4, 0, 0, 0 })
        };
        for (int l = 0; l < modList.size(); l++) {
        Element element = null, trapElement = null;
            ModInfo modInfo = modList.get(l);
            if (!modInfo.modSettings.modInfo)
                continue;

            String newId = modInfo.newSkin.id;

            String heroId = newId.substring(0, 3);
            String skinId = newId.substring(3, newId.length());
            update("    + Modding " + (l + 1) + "/" + modList.size() + ": " + modInfo);

            String inputZipPath = saveModPath + modPackName
                    + "/files/Resources/1.50.1/Prefab_Characters/Actor_" + heroId
                    + "_Infos.pkg.bytes";
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
                            String[] nameElementRemove = new String[] { "ArtSkinPrefabLOD", "ArtSkinPrefabLODEx",
                                    "ArtSkinLobbyShowLOD" };
                            for (int i = 0; i < skin.getChildLength(); i++) {
                                Element e = skin.getChild(i).clone();
                                if (Arrays.asList(nameElementRemove).contains(e.nameS))
                                    e.setName(e.nameS.replace("Skin", ""));
                                element.addChild(removeAt, e);
                            }
                            break;
                        }
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
                    saveModPath + modPackName + "/files/Resources/1.50.1/Prefab_Characters/Actor_"
                            + new File(cacheModPath + new File(cacheModPath).list()[0]).list()[0].substring(0, 3)
                            + "_Infos.pkg.bytes");
        }
    }

    public void modActions(List<ModInfo> modListOrigin) {
        List<ModInfo> modList = new ArrayList<>(modListOrigin);
        Set<String> set = new HashSet<>(modList.size());
        modList.removeIf(modinfo -> !set.add(modinfo.newSkin.id));
    }

    public void update(String content) {
        System.out.println(content);
    }
}
