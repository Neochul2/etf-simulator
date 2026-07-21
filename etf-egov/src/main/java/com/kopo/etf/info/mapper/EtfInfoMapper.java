package com.kopo.etf.info.mapper;

import java.util.List;

import com.kopo.etf.info.vo.EtfInfoVO;

public interface EtfInfoMapper {

    EtfInfoVO selectEtfInfo(String symbol);

    /** 전체 ETF 목록 조회 (드롭다운용, 시총 내림차순) */
    List<EtfInfoVO> selectAllEtfList();

}