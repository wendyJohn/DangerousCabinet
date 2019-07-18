package com.sanleng.dangerouscabinet.model;

import com.sanleng.dangerouscabinet.ui.bean.DangerousChemicals;

import java.util.List;

public interface StorckModel {
    void StokSuccess(String state, List<DangerousChemicals> stocklist);
}