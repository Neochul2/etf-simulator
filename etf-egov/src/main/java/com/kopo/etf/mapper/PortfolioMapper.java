package com.kopo.etf.mapper;

import java.util.List;
import com.kopo.etf.vo.PortfolioVO;

public interface PortfolioMapper {
    List<PortfolioVO> selectPortfolioList();
    int insertPortfolio(PortfolioVO vo);
    int deletePortfolio(Long id);
}