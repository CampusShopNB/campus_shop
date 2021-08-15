package com.service;

import com.entity.Order;
import com.entity.Recommend;
import com.mapper.OrderMapper;
import com.mapper.RecommendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  推荐商品服务类
 * </p>
 */
@Service
@Transactional
public class RecommendService {

    @Autowired
    private RecommendMapper recommendMapper;

    /**添加推荐商品insert*/
    public Integer addRecommend(Recommend recommend){
        return recommendMapper.addRecommend(recommend);
    }

    /**查询首页展示的2个被推荐商品
     * 废弃select * from recommend where recomstatus=1
     * SELECT * FROM `recommend` where recomstatus=2 ORDER BY updatetime LIMIT 0,2;*/
    public List<Recommend> queryIndexRecommendCommodity(){
        return recommendMapper.queryIndexRecommendCommodity();
    }

    /**后台。根据recomstatus状态，分页查看所有推荐商品的内容*/
    public List<Recommend> queryRecommendByRecomStatus(Integer page, Integer count, Integer recomstatus){
        return recommendMapper.queryRecommendByRecomStatus(page,count,recomstatus);
    }
    /**根据状态，查询推荐商品总数，用于分页的。*/
    public Integer queryRecommendCountByRecomStatus(Integer recomstatus){
        return recommendMapper.queryRecommendCountByRecomStatus(recomstatus);
    }


    /**删除订单，看似delete，实则update状态为4，这里在商品下架后被调用
     * （而且要选择状态为2的显示在首页中，即已经审核通过但是待推荐的商品，要挑一个显示在首页
     * 合并到修改状态update方法中*/

    /**修改推荐商品信息的状态*/
    public Integer updateRecommendStatus(Recommend recommend){
        return recommendMapper.updateRecommendStatus(recommend);
    }
}