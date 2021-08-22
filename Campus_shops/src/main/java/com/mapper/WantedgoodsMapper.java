package com.mapper;

import com.entity.Wantedgoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  求购商品Mapper接口
 * </p>
 */
public interface WantedgoodsMapper {
    /**新增记录*/
    Integer insertWantgoods(Wantedgoods wantedgoods);
     /**修改状态or删除。
     * 自己下架or管理员审核*/
    Integer updateWantStatus(Wantedgoods wantedgoods);
    /**根据id查询*/
    Wantedgoods queryWantgoodsById(Wantedgoods wantedgoods);
    /**查看所有待处理记录（在后台给管理员使用）
     select * from wantedschool where wantstatus=0 */
    List<Wantedgoods> queryAllUnprocessWantgoods();
    /** 查询所有待处理记录的总数（在后台给管理员使用）    select count(*)*/
    Integer queryAllUnprocessWantgoodsCount();
    /**用户中心分页查看不同状态的求购记录 */
    List<Wantedgoods> queryWantgoodsByStatus(@Param("page") Integer page, @Param("count") Integer count, @Param("wantstatus")Integer wantstatus);
    /**用户中心分页查看不同状态的求购记录总数    select count(*) */
    Integer queryWantgoodsCountByStatus(Wantedgoods wantedgoods);

    List<Wantedgoods> queryAdminAllByStatus(@Param("page") Integer page, @Param("count") Integer count, @Param("wantstatus") Integer wantstatus);

    Integer queryAdminCountByStatus(@Param("wantstatus") Integer wantstatus);

    /**根据分类查询*/
    List<Wantedgoods> queryWantgoodsByCategory(Wantedgoods wantedgoods);
}