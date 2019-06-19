package com.sanleng.dangerouscabinet.utils;


import com.sanleng.dangerouscabinet.ui.bean.DangerousChemicals;

import java.util.Comparator;

public class PinyinComparator implements Comparator<DangerousChemicals> {

    public int compare(DangerousChemicals o1, DangerousChemicals o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}
