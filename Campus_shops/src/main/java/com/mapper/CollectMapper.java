package com.mapper;

import com.entity.Collect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  收藏 Mapper 接口
 * </p>
 * 注意没有delete操作。如果取消收藏，直接update，修改相关信息和收藏状态即可，以此减少数据库的操作
 */
public interface CollectMapper {
    /**添加收藏*/
    Integer insertCollect(Collect collect);
    /**分页查看所有收藏内容*/
    List<Collect> queryAllCollect(@Param("page") Integer page, @Param("count") Integer count, @Param("couserid") String couserid);
    /**修改收藏状态*/
    Integer updateCollect(Collect collect);
    /**查询商品是否被用户收藏*/
    Collect queryCollectStatus(Collect collect);
    /**查询我的收藏的总数*/
    Integer queryCollectCount(String couserid);
}
