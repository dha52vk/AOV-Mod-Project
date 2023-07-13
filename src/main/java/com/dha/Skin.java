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

    public Skin(){}

    public Skin(String skinId, SkinLabel label){
        id = skinId;
    }

    public int getSkinLevel(){
        switch(id){
            case "5434":
                return 2;
            case "5213":
                return 3;
            case "1805":
                return 2;
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
    A_HH(1), S_HH(2), S_Plus_HH(3), SS_HH(4), SS_Chroma(4);

    final int skinLevel;

    private SkinLabel(int skinLevel){
        this.skinLevel = skinLevel;
    }
}