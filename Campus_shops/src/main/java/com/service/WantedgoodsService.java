package com.service;

import com.entity.Wantedgoods;
import com.mapper.WantedgoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  求购商品 服务类
 * </p>
 */
@Service
@Transactional
public class WantedgoodsService {
    @Autowired
    private WantedgoodsMapper wantedgoodsMapper;
    /**插入记录*/
    public Integer insertWantgoods(Wantedgoods wantedgoods) {
        return wantedgoodsMapper.insertWantgoods(wantedgoods);
    }
    /**修改状态or删除。
     * 自己下架or管理员审核*/
    public Integer updateWantStatus(Wantedgoods wantedgoods) {
        return wantedgoodsMapper.updateWantStatus(wantedgoods);
    }
    /**根据id查询*/
    public Wantedgoods queryWantgoodsById(Wantedgoods wantedgoods) {
        return wantedgoodsMapper.queryWantgoodsById(wantedgoods);
    }
    /**根据分类查询*/
    public List<Wantedgoods> queryWantgoodsByCategory(Wantedgoods wantedgoods) {
        return wantedgoodsMapper.queryWantgoodsByCategory(wantedgoods);
    }

    /**不分页。查看所有待处理记录（在后台给管理员使用）
     select * from wantedschool where wantstatus=0 */
    public List<Wantedgoods> queryAllUnprocessWantgoods() {
        return wantedgoodsMapper.queryAllUnprocessWantgoods();
    }
    /** 查询所有待处理记录的总数（在后台给管理员使用）    select count(*)*/
    public Integer queryAllUnprocessWantgoodsCount() {
        return wantedgoodsMapper.queryAllUnprocessWantgoodsCount();
    }
    /**根据状态查询记录。需要分页。所以用Integer做参数，因为不知道对象怎么用param注解
     * ①用户中心。分页查看不同状态的求购记录
     * ②首页的列表也用此方法 */
    public List<Wantedgoods> queryWantgoodsByStatus(Integer page,Integer count,Integer wantstatus,String userid) {
        return wantedgoodsMapper.queryWantgoodsByStatus(page,count,wantstatus,userid);
    }
    /**用户中心分页查看不同状态的求购记录总数    select count(*) */
    public Integer queryWantgoodsCountByStatus(Wantedgoods wantedgoods) {
        return wantedgoodsMapper.queryWantgoodsCountByStatus(wantedgoods);
    }


    public List<Wantedgoods> queryAdminAllByStatus(Integer page, Integer count, Integer wantstatus){
        return wantedgoodsMapper.queryAdminAllByStatus(page,count,wantstatus);
    }
    public Integer queryAdminCountByStatus(Integer wantstatus){
        return wantedgoodsMapper.queryAdminCountByStatus(wantstatus);
    }
}
