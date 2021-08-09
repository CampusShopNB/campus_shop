package com.controller;

import com.entity.UserInfo;
import com.service.UserInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class IndexController {
    @Autowired
    private UserInfoService userInfoService;
    /*    ======================前台跳转start======================     */
    /**
     * 网站首页
     * */
    @GetMapping("/")
    public String index(){
        return "/index";
    }

    /**
     * 首页公告清单
     * 前台首页----网站公告
     * */
    @GetMapping("/user/newslist")
    public String userNews(){
        return "/common/listnews";
    }

    /**
     * 联系我们
     * */
    @GetMapping("/contacts")
    public String contacts(){
        return "/common/contacts";
    }

    /**
     * 关于我们
     * */
    @GetMapping("/about")
    public String about(){
        return "/common/about";
    }

    /**
     * 跳转到产品清单界面
     * 前台
     * */
    @GetMapping("/product-listing")
    public String toproductlisting() {
        return "/common/product-listing";
    }

    /**
     * 跳转到产品清单搜索界面
     * 前台http://localhost:8996/product-search?keys=python
     * */
    @GetMapping("/product-search")
    public String toProductSearchs(String keys, ModelMap modelMap) {
        if(keys==null){
            //输入为空
            return "/error/404";
        }
        modelMap.put("keys",keys);
        return "/common/product-search";
    }

    /**
     * 用户登录注册
     * */
    @GetMapping("/login")
    public String login(){
        return "/user/logreg";
    }

    /**
     * 用户忘记密码
     * */
    @GetMapping("/forget")
    public String forget(){
        return "/user/forget";
    }
    /*    ======================前台跳转end======================     */






    /*    ======================后台跳转start======================     */
    /**
     * 后台管理首页
     * */
    @GetMapping("/admin/index")
    public String adminindex(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String admin = (String) session.getAttribute("admin");
        /**拦截器：如果不是管理员，则进行重定向*/
        if (StringUtils.isEmpty(admin)){
            response.sendRedirect(request.getContextPath() + "/");//重定向
        }
        return "/admin/index";
    }
    /**
     * 销量列表
     * 后台----销售分析--销量列表
     * */
    @GetMapping("/admin/sold")
    public String adminSold(){
        return "/admin/sold/soldrecord";
    }

    /**
     * 管理员公告列表
     * 后台----公告管理--公告清单
     * */
    @GetMapping("/admin/newslist")
    public String adminNews(){
        return "/admin/news/newslist";
    }


    /*    ======================后台跳转end======================     */





    /*    ======================个人中心start======================     */
    /**
     * 个人中心页面
     * */
    @GetMapping("/user/center")
    public String usercenter(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userid = (String) session.getAttribute("userid");
        /**拦截器：如果不是用户角色登录，则进行重定向*/
        if (StringUtils.isEmpty(userid)){
            response.sendRedirect(request.getContextPath() + "/");//重定向
        }
        //返回resources/templates/user/user-center.html页面
        return "/user/user-center";
    }

    /**
     * 用户收藏列表
     * 个人中心----收藏管理--我的收藏
     * */
    @GetMapping("/user/collect")
    public String usercollect(){
        //返回resources/templates/user/collect/collectlist.html页面
        return "/user/collect/collectlist";
    }

    /**
     * 用户商品列表
     * 个人中心----商品管理--商品清单
     * */
    @GetMapping("/user/product")
    public String userproduct(){
        //返回resources/templates/user/product/productlist.html页面
        return "/user/product/productlist";
    }

    /**
     * 通知消息
     * 个人中心----消息中心--通知消息
     * */
    @GetMapping("/user/message")
    public String commonmessage(){
        //返回resources/templates/user/message/message.html页面
        return "/user/message/message";
    }

    /**
     * 弹出式通知消息
     * 个人中心----右上角图标按钮
     * */
    @GetMapping("/user/alertmessage")
    public String alertmessage(){
        //返回resources/templates/user/message/alertmessage.html页面
        return "/user/message/alertmessage";
    }

    /**
     * 用户售出记录
     * 个人中心----售出记录--我的售出记录
     * */
    @GetMapping("/user/sold")
    public String sold(){
        //返回resources/templates/user/sold/soldrecord.html页面
        return "/user/sold/soldrecord";
    }


/**===================================订单管理start===================================*/
    /**
     * 购入订单
     * 个人中心----订单管理--购入订单
     * */
    @GetMapping("/user/buyinorder")
    public String toBuyInOrderList(){
        //返回resources/templates/user/order/buyinorder.html页面
        return "/user/order/buyinorder";
    }

    /**
     * 售出订单
     * 个人中心----订单管理--售出订单
     * */
    @GetMapping("/user/selloutorder")
    public String toSellOutOrderList(){
        //返回resources/templates/user/order/selloutorder.html页面
        return "/user/order/selloutorder";
    }
/**===================================订单管理end===================================*/




    /**
     * 用户修改密码
     * 个人中心----个人信息--修改密码
     * */
    @RequiresPermissions("user:userinfo")
    @GetMapping("/user/pass")
    public String userinfo(){
        //返回resources/templates/user/updatepass.html页面
        return "/user/updatepass";
    }

    /**
     * 用户修改个性签名
     * 个人中心----个人信息--修改个性签名
     * */
    @RequiresPermissions("user:userinfo")
    @GetMapping("/user/sign")
    public String usersign(HttpSession session, ModelMap modelMap){
        //通过session获取userid
        String userid = (String) session.getAttribute("userid");
        //查询用户信息
        UserInfo userInfo = userInfoService.LookUserinfo(userid);

        modelMap.put("userInfo",userInfo);

        //返回resources/templates/user/updatesign.html页面
        return "/user/updatesign";
    }

    /**
     * 用户更换手机号
     * 个人中心----个人信息--更换手机号
     * */
    @RequiresPermissions("user:userinfo")
    @GetMapping("/user/phone")
    public String userphone(){
        //返回resources/templates/user/updatephone.html页面
        return "/user/updatephone";
    }
    /*    ======================个人中心end ======================     */






    /*    以下三个不清楚具体作用，估计只是前后台所用模板的介绍。 （//无用）    */
    /**
     * 用户个人中心默认展示图
     *  */
    @GetMapping("/home/console")
    public String homeconsole(){
        return "/admin/home/console";
    }
    /**
     * 管理员首页默认展示图
     * */
    @GetMapping("/echars/console")
    public String echars(){
        return "/admin/echars/console";
    }

    @GetMapping("/app/message/index")
    public String appmessageindex(){
        return "/admin/app/message/index";
    }
}
