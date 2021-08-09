package com.controller.User;

import com.entity.Commodity;
import com.entity.UserInfo;
import com.service.CommodityService;
import com.service.UserInfoService;
import com.util.StatusCode;
import com.vo.LayuiPageVo;
import com.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Description: 个人主页
 * 显示个人资料和已发布的商品
 */
@Controller
public class UserHomeController {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private CommodityService commodityService;

    /**
     * 个人简介
     * 前端传入用户id（userid）
     *
     * http://localhost:8996/user/userinfo/1582184795951594874
     *
     * 感觉是个人中心----个人信息--基本资料
     * */
    @ResponseBody
    @GetMapping("/user/userinfo/{userid}")
    public ResultVo userinfo(@PathVariable("userid") String userid) {
        //LookUserinfo根据主键userid查询用户信息
        UserInfo userInfo = userInfoService.LookUserinfo(userid);
        if (!StringUtils.isEmpty(userInfo)){
            return new ResultVo(true, StatusCode.OK, "查询成功",userInfo);
        }
        return new ResultVo(false, StatusCode.ERROR, "查询失败");
    }

    /**
     * 分页展示个人已审核的商品信息（状态码：1）
     *前端传入用户id（userid）、当前页码（nowPaging）、
     *
     *http://localhost:8996/user/usercommodity/1582184795951594874
     */
    @ResponseBody
    @GetMapping("/user/usercommodity/{userid}")
    public LayuiPageVo userHomeCommodity(@PathVariable("userid") String userid,int limit, int page) {
        //queryAllCommodity应该是→？个人中心-商品管理-商品清单-选择状态（全部商品、待审核、正常、违规、已售出）
        List<Commodity> commodityList = commodityService.queryAllCommodity((page - 1) * limit, limit, userid,1);
        //queryCommodityCount查询商品各类状态的总数。应该是用于分页？
        Integer dataNumber = commodityService.queryCommodityCount(userid,1);
        return new LayuiPageVo("", 0,dataNumber,commodityList);
    }

}
