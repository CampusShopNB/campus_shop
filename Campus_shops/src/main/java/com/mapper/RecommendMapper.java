package com.mapper;

import com.entity.Order;
import com.entity.Recommend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单OrderMapper接口
 * </p>
 * 注意没有delete操作。如果取消收藏，直接update，修改相关信息和收藏状态即可，以此减少数据库的操作
 */
public interface RecommendMapper {
    /**
     * 添加推荐商品insert
     */
    Integer addRecommend(Recommend recommend);

    /**
     * 查询首页展示的2个被推荐商品
     * SELECT * FROM `recommend` where recomstatus=2 ORDER BY updatetime LIMIT 0,2;
     */
    List<Recommend> queryIndexRecommendCommodity();

    /**后台。根据recomstatus状态，分页查看所有推荐商品的内容*/
    List<Recommend> queryRecommendByRecomStatus(@Param("page") Integer page, @Param("count") Integer count, @Param("recomstatus") Integer recomstatus);

    /**根据状态，查询推荐商品总数，用于分页的。*/
    Integer queryRecommendCountByRecomStatus(@Param("recomstatus") Integer recomstatus);

    /**修改推荐商品状态，包含删除，即修改状态为4*/
    Integer updateRecommendStatus(Recommend recommend);

}