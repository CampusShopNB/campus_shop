package com.controller.Seller;

import com.entity.Login;
import com.entity.UserInfo;
import com.service.LoginService;
import com.service.UserInfoService;
import com.util.*;
import com.vo.ResultVo;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 商家中心 控制器
 * </p>
 *
 * @author hlt
 * @since 2019-12-21
 */
@Controller
public class SellerController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoService userInfoService;



    /**
     * 展示用户头像昵称
     */
    @ResponseBody
    @PostMapping("/seller/avatar")
    public ResultVo sellerAvatar( HttpSession session) {
        String userid = (String) session.getAttribute("goodUser");
        UserInfo userInfo = userInfoService.queryPartInfo(userid);
        return new ResultVo(true, StatusCode.OK, "查询头像成功",userInfo);
    }


    /**
     * 展示个人信息
     */
    @GetMapping("/seller/lookinfo")
    public String sellerlookinfo(HttpSession session, ModelMap modelMap) {
        String userid = (String) session.getAttribute("goodUser");
        UserInfo userInfo = userInfoService.LookUserinfo(userid);
        modelMap.put("userInfo",userInfo);
        return "/seller/sellerinfo";
    }




}

