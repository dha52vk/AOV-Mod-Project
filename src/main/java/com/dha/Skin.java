package com.dha;

public class Skin {
    public String id;
    public String name;
    public SkinLabel label;
    public boolean changeAnim;
    public boolean hasDeathEffect;
    public boolean isAwakeSkin;
    public boolean isComponentSkin;
    public String componentEffectId;
    public int componentLevel;
    public int levelSFXUnlock;
    public int levelVOXUnlock;
    public String specialBackAnim;
    public String hasteName;
    public String hasteNameRun;
    public String hasteNameEnd;
    public String[] filenameNotMod;
    public String[] filenameNotModCheckId;

    public Skin(){}

    public Skin(String skinId, SkinLabel label){
        id = skinId;
    }

    public int getSkinLevel(){
        switch(id){
            case "5434":
            case "1805":
                return SkinLabel.S.skinLevel;
            case "5213":
                return SkinLabel.S_Plus.skinLevel;
            case "1118":
            case "19010":
                return SkinLabel.SS.skinLevel;
            default:
                return label.skinLevel;
        }
    }

    @Override
    public String toString(){
        return name + "(" + id + ")";
    }
}

enum SkinLabel{
    Default(0),
    A(1), S(2), S_Plus(3), SS(4), SSS_HH(5),
    A_HH(1), S_HH(2), S_Plus_HH(3), SS_HH(4), SS_Chroma(4),
    FMVP(3), One_Punch_Man(5);

    final int skinLevel;

    private SkinLabel(int skinLevel){
        this.skinLevel = skinLevel;
    }
}