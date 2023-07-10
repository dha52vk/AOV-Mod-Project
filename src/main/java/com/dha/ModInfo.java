package com.dha;

import java.util.List;

public class ModInfo {
    public List<Skin> targetSkins;
    public Skin newSkin;
    public ModSettings modSettings;

    public ModInfo(List<Skin> targetSkins, Skin newSkin, ModSettings modSettings){
        this.targetSkins = targetSkins;
        this.newSkin = newSkin;
        this.modSettings = modSettings;
    }

    @Override
    public String toString(){
        return targetSkins.size() + " skins to " + newSkin;
    }
}
