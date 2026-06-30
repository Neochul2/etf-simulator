package com.kopo.etf.mapper;

import java.util.List;

import com.kopo.etf.vo.EtfInfoVO;

public interface EtfInfoMapper {

    EtfInfoVO selectEtfInfo(String symbol);

    /** 전체 ETF 목록 조회 (드롭다운용, 시총 내림차순) */
    List<EtfInfoVO> selectAllEtfList();

}