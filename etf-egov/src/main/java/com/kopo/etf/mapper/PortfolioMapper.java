package com.kopo.etf.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.kopo.etf.vo.PortfolioVO;

public interface PortfolioMapper {
    List<PortfolioVO> selectPortfolioList();
    int insertPortfolio(PortfolioVO vo);
    int deletePortfolio(Long id);
    int updatePortfolio(@Param("id") Long id, @Param("investAmt") BigDecimal investAmt);
}