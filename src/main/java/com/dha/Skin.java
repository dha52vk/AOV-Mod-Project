package com.dha;

public class Skin {
    public String id;
    public String name;
    public SkinLabel label;
    public boolean changedLabel;
    public boolean changeAnim;
    public boolean hasDeathEffect;
    public boolean isAwakeSkin;
    public int levelSFXUnlock;
    public int levelVOXUnlock;
    public String hasteName;
    public String hasteNameRun;
    public String hasteNameEnd;
    public String[] specialChangeOld;
    public String[] specialChangeNew;
    public String[] filenameNotMod;    
}

enum SkinLabel{
    Default,
    A, S, S_Plus, SS, SSS_HH,
    A_HH, S_HH, S_Plus_HH, SS_HH, SS_Chroma
}