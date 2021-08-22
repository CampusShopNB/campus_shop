package com.controller;

import com.entity.*;
import com.service.NoticesService;
import com.service.UserInfoService;
import com.service.WantedgoodsService;
import com.service.WantedschoolService;
import com.util.GetDate;
import com.util.KeyUtil;
import com.util.StatusCode;
import com.vo.LayuiPageVo;
import com.vo.PageVo;
import com.vo.ResultVo;
import jnr.ffi.annotations.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 求购商品控制器
 * </p>
 */
@Controller
public class WantedgoodsController {
    @Autowired
    private WantedgoodsService wantedgoodsService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private NoticesService noticesService;

    /**
     * 前台页面跳转。
     * 跳转到求购清单列表
     * <p>
     * 暂时不查询数据，这里先跳转，然后让js发送请求到这里，查询数据再显示
     * 否则无法点击顶部栏进行跳转。。
     */
    @GetMapping("/wantgoodslist")
    public String toproductlisting() {
        return "/common/wantgoodslists";
    }

    /**
     * 数据查询1。
     * 只查询数据总记录
     * 参考CommodityController.java的方法productListNumber
     */
    @GetMapping("/indexwantgoodslist/showallandpage")
    @ResponseBody
    public PageVo showWantGoodsNum() {
        Wantedgoods wantedgoods = new Wantedgoods().setWantstatus(1);
        Integer dataNumber = wantedgoodsService.queryWantgoodsCountByStatus(wantedgoods);
        /*sql执行结果为1，但是返回的竟然是0（System.out.println(dataNumber)是0）
        明白了，一开始只有一条待审核的记录，但是我们传入的wantstatus是1
         ==>  Preparing: select count(*) from wantedgoods where wantstatus=?
         ==>  Parameters: 1(Integer)
         <==  Total: 1*/
        return new PageVo(StatusCode.OK, "查询成功", dataNumber);
    }


    /**
     * 数据查询2。
     * 参考CommodityController.java的方法productlisting
     */
    @GetMapping("/indexwantgoodslist/{page}/{wantstatus}")
    @ResponseBody
    public ResultVo showWantGoodsLits(@PathVariable("page") Integer page, @PathVariable("wantstatus") Integer wantstatus, ModelMap modelMap) {
        //查询所有求购列表
        List<Wantedgoods> wantedgoodsList = wantedgoodsService.queryWantgoodsByStatus((page - 1) * 16, 16, wantstatus);

        // modelMap.put("wantedgoodsList", wantedgoodsList);

        //本来以为“需要根据结构，来重新编制数据，便于在前台使用”。
        // 发现CommodityController.java的方法productlisting中直接用set来设置，那就需要修改实体类WantedGoods
        for (Wantedgoods wg : wantedgoodsList) {
            UserInfo u = userInfoService.LookUserinfo(wg.getUserid());
            wg.setUsername(u.getUsername());
            wg.setPhone(u.getMobilephone());
            wg.setEmail(u.getEmail());
            wg.setUimage(u.getUimage());
        }
        return new ResultVo(true, StatusCode.OK, "查询成功", wantedgoodsList);
    }

    /**
     * 页面跳转，个人中心
     * 用户发布求购
     */
    @GetMapping("/user/publishwantgoods")
    public String toPublishWantGoods() {
        return "/user/wantgoods/pubwantgoods";
    }

    /**
     * 页面跳转，个人中心
     * 用户的求购列表
     */
    @GetMapping("/user/mywantgoodslist")
    public String toMyWantGoodsList() {
        return "/user/wantgoods/wantgoodslist";
    }

    /**
     * 发布求购商品
     * <p>
     * 个人中心----求购管理--发布求购闲置信息
     */
    @PostMapping("/wantgoods/pub")
    @ResponseBody
    public String pubWantGoods(@RequestBody Wantedgoods wantedgoods, HttpSession session) {
        //通过session获取用户id
        String userid = (String) session.getAttribute("userid");
        //LookUserinfo():根据userid查询用户信息（主要是查询用户名、头像、手机号、邮箱）
        UserInfo userInfo = userInfoService.LookUserinfo(userid);
        //生成唯一的主键作为wantedgoods记录的主键
        String wantgoodsId = KeyUtil.genUniqueKey();
        //当前时间，作为createtime
        Date createtime = GetDate.strToDate();
        //链式赋值.updatetime初始和createtime一致
        wantedgoods
                .setId(wantgoodsId)
                .setUserid(userid)
                .setCreatetime(createtime)
                .setUpdatetime(createtime)
                .setWantstatus(0);
        //执行插入
        wantedgoodsService.insertWantgoods(wantedgoods);


        //和修改商品类似，提交后也要经过审核。这里实例化一个notices对象然后链式赋值，不再赘述。
        /**发出待审核系统通知*/
        Notices notices = new Notices().setId(KeyUtil.genUniqueKey()).setUserid(userid).setTpname("求购审核")
                .setWhys("您的求购信息【" + wantedgoods.getWanttitle() + "】，类别【" + wantedgoods.getWantcategory() + "】进入待审核队列，请您耐心等待。");
        //插入一条记录
        noticesService.insertNotices(notices);
        return "0";
    }


    /**
     * 后台页面跳转
     * 跳转求购审核列表。显示所有待审核项
     * 只是跳转，不查询数据
     */
    @GetMapping("/admin/wantgoodslist")
    public String toAdminWantgoodsList() {
        return "/admin/wantgoods/wantgoods";
    }


    /**
     * 根据状态显示数据
     */
    @GetMapping("/admin/showwantgoodslist/{wantstatus}")
    @ResponseBody
    public LayuiPageVo adminShowWantgoodsList(@PathVariable("wantstatus") Integer wantstatus, int limit, int page) {
        //查询所有待审核求购列表
        if (wantstatus == 100) {
            //全部商品，则wantstatus传入null
            List<Wantedgoods> wantedgoodsList = wantedgoodsService.queryAdminAllByStatus((page - 1) * limit, limit, null);
            Integer dataNumber = wantedgoodsService.queryAdminCountByStatus(null);
            return new LayuiPageVo("", 0, dataNumber, wantedgoodsList);
        } else {
            List<Wantedgoods> wantedgoodsList = wantedgoodsService.queryAdminAllByStatus((page - 1) * limit, limit, wantstatus);
            Integer dataNumber = wantedgoodsService.queryAdminCountByStatus(wantstatus);
            return new LayuiPageVo("", 0, dataNumber, wantedgoodsList);
        }
    }

    /**
     * 通过审核和不通过审核（修改状态）
     * 用户删除也可以用这个，所以删掉admin
     */
    @PutMapping("/changewantstatus/{wantgoodsid}/{wantstatus}")
    @ResponseBody
    public ResultVo changeWantStatus(@PathVariable("wantgoodsid") String wantgoodsid, @PathVariable("wantstatus") Integer wantstatus) {
        Wantedgoods oriWantedgoods = new Wantedgoods().setId(wantgoodsid).setWantstatus(wantstatus).setUpdatetime(GetDate.strToDate());
        Integer i = wantedgoodsService.updateWantStatus(oriWantedgoods);
        if (i == 1) {
            //查询userid
            Wantedgoods newWantedgoods = wantedgoodsService.queryWantgoodsById(oriWantedgoods);
            /**发出商品审核结果的系统通知*/
            if (wantstatus == 2) {
                Notices notices = new Notices().setId(KeyUtil.genUniqueKey()).setUserid(newWantedgoods.getUserid()).setTpname("求购审核")
                        .setWhys("您的求购信息【" + newWantedgoods.getWanttitle() + "】类别【" + newWantedgoods.getWantcategory() + "】未通过审核");
                noticesService.insertNotices(notices);
            } else if (wantstatus == 1) {
                Notices notices = new Notices().setId(KeyUtil.genUniqueKey()).setUserid(newWantedgoods.getUserid()).setTpname("求购审核")
                        .setWhys("您的求购信息【" + newWantedgoods.getWanttitle() + "】类别【" + newWantedgoods.getWantcategory() + "】通过审核");
                noticesService.insertNotices(notices);
            }
            //3不需要通知
            return new ResultVo(true, StatusCode.OK, "操作成功");
        }
        return new ResultVo(false, StatusCode.ERROR, "操作失败");
    }


}