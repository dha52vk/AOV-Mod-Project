package com.dha;

import java.util.ArrayList;
import java.util.List;

public class HeroList {
    public List<Hero> heros;

    public HeroList(){
        this.heros = new ArrayList<>();
    }

    public HeroList(List<Hero> heros){
        this.heros = heros;
    }
}
