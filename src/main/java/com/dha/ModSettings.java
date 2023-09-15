package com.dha;

public class ModSettings {
    public boolean modIcon;
    public boolean modInfo;
    public boolean modOrgan;
    public boolean modAction;
    public boolean modSound;
    public boolean modBack;
    public boolean modHaste;
    public boolean modMotion;

    public ModSettings(){}

    public ModSettings(boolean modIcon, boolean modInfo, boolean modOrgan, boolean modAction, boolean modSound, boolean modBack, boolean modHaste, boolean modMotion){
        this.modIcon = modIcon;
        this.modInfo = modInfo;
        this.modOrgan = modOrgan;
        this.modAction = modAction;
        this.modSound = modSound;
        this.modBack = modBack;
        this.modHaste = modHaste;
        this.modMotion = modMotion;
    }
}
