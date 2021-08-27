package com.controller.User;

import com.entity.Login;
import com.entity.School;
import com.entity.UserInfo;
import com.service.LoginService;
import com.service.SchoolService;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 个人中心 控制器
 * </p>
 */
@Controller
public class UserController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoService userInfoService;
    //查询school表
    @Autowired
    private SchoolService schoolService;
    /**手机号和更换手机号验证码map集合*/
    private static Map<String, String> phonecodemap = new HashMap<>();
    /**
     * 修改密码
     * 1.前端传入旧密码（oldpwd）、新密码（newpwd）
     * 2.判断输入旧密码和系统旧密码是否相等
     * 4.修改密码
     *
     *
     * 对应模板updatepass.html，其中json.getString()中的参数是html中的js代码，在script标签中
     * 拦截地址/user/updatepwd也是js中定义的
     */
    @ResponseBody
    @PutMapping("/user/updatepwd")
    public ResultVo updatepwd(HttpSession session, HttpServletRequest request) throws IOException {
        //注意
        JSONObject json = JsonReader.receivePost(request);
        //获取新旧密码
        String oldpwd = json.getString("oldpwd");
        String newpwd = json.getString("newpwd");
        //获取session中的userid
        String userid = (String) session.getAttribute("userid");
        //实例化实体对象，便于操作数据库
        Login login = new Login();
        UserInfo userInfo = new UserInfo();
        //为对象赋值
        login.setUserid(userid);
        //userLogin():登录及判断用户是否存在，使用select语句进行查询
        Login login1 = loginService.userLogin(login);

        /*
        加盐加密
        1.关于加盐
        https://blog.csdn.net/m0_37596145/article/details/78311558
            散列算法一般用于生成数据的摘要信息，是不可逆的算法，一般适合存储密码之类的数据，常见的散列算法如MD5、SHA等。
            如果直接对密码进行散列相对来说破解更容易。
                比如加密密码“admin”，如果直接用散列进行加密，产生的散列值是“21232f297a57a5a743894a0e4a801fc3”，
                可以到一些md5解密网站很容易的通过散列值得到密码“admin”，
            为避免破解，一般进行散列时最好提供一个salt（盐），即加一些只有系统知道的干扰数据
                如用户名和ID（即盐）；
                这样散列的对象是“密码+用户名+ID”，这样生成的散列值相对来说更难破解。

        2.为什么旧密码要加盐
        因为从前端获取的密码oldpwds和newpwd是原始密码，没有加盐。
        而从数据库的读取的login1.getPassword()是加盐过后的
        要想比对，就要对前者进行加盐，然后在对比    */
        String oldpwds = new Md5Hash(oldpwd, "Campus-shops").toString();

        //如果旧密码相等
        if (oldpwds.equals(login1.getPassword())){
            //盐加密
            String passwords = new Md5Hash(newpwd, "Campus-shops").toString();
            //设置密码
            login.setPassword(passwords);
            userInfo.setPassword(passwords).setUserid(login1.getUserid());
            //修改数据表记录
            Integer integer = loginService.updateLogin(login);
            Integer integer1 = userInfoService.UpdateUserInfo(userInfo);
            //判断是否更新成功，并返回相应提示
            if (integer == 1 && integer1 == 1) {
                return new ResultVo(true, StatusCode.OK, "修改密码成功");
            }
            return new ResultVo(false, StatusCode.ERROR, "修改密码失败");
        }
        //        return new ResultVo(false, StatusCode.LOGINERROR, "当前密码错误");
        return new ResultVo(false, StatusCode.LOGINERROR, "新密码与旧密码不能相同");
    }

    /**
     * 修改个性签名
     */
    @ResponseBody
    @PutMapping("/user/updatesign")
    public ResultVo updatesign(HttpSession session, HttpServletRequest request) throws IOException {
        //注意
        JSONObject json = JsonReader.receivePost(request);
        //获取个性签名
        String sign = json.getString("sign");
        //获取session中的userid
        String userid = (String) session.getAttribute("userid");
        //实例化实体对象，便于操作数据库
        UserInfo userInfo = new UserInfo().setUserid(userid).setSign(sign);
        UserInfo oriUser = userInfoService.LookUserinfo(userid);

        if(!sign.equals(oriUser.getSign())){
            Integer updateRe = userInfoService.UpdateUserInfo(userInfo);
            if (updateRe == 1) {
                return new ResultVo(true, StatusCode.OK, "个性签名修改成功");
            }
            return new ResultVo(false, StatusCode.ERROR, "个性签名修改失败");
        }
        return new ResultVo(false, StatusCode.ALREADYEXIST, "新旧个性签名不能相同");
    }

    /**
     * 展示用户头像昵称
     *
     * 不知道是在哪里用的。。
     */
    @ResponseBody
    @PostMapping("/user/avatar")
    public ResultVo userAvatar( HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        UserInfo userInfo = userInfoService.queryPartInfo(userid);
        return new ResultVo(true, StatusCode.OK, "查询头像成功",userInfo);
    }

    /**
     * 修改头像
     * */
    @PostMapping(value = "/user/updateuimg")
    @ResponseBody
    public JSONObject updateuimg(@RequestParam(value = "file", required = false) MultipartFile file, HttpSession session) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject resUrl = new JSONObject();
        //随机生成文件名
        String filename = UUID.randomUUID().toString().replaceAll("-", "");
        //获得文件扩展名
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        //文件全名
        String filenames = filename + "." + ext;
        //图片路径
        String pathname = "D:\\campusshops\\file\\user\\" + filenames;
        //上传文件
        file.transferTo(new File(pathname));
        resUrl.put("src", "/pic/user/"+filenames);
        res.put("msg", "");
        res.put("code", 0);
        res.put("data", resUrl);
        String uimgUrl = "/pic/user/" + filenames;

        String userid=(String) session.getAttribute("userid");
        //实例化一个用户，设置userid和头像url，便于作为参数，更新数据表
        UserInfo userInfo = new UserInfo().setUserid(userid).setUimage(uimgUrl);
        //根据主键userid，修改用户信息
        userInfoService.UpdateUserInfo(userInfo);
        return res;
    }

    /**
     * 展示个人信息
     *
     * 这个注解是shiro
     * https://blog.csdn.net/medelia/article/details/86692521
     *
     * 个人中心----个人信息--基本资料
     */
    @RequiresPermissions("user:userinfo")
    @GetMapping("/user/lookinfo")
    public String lookinfo(HttpSession session, ModelMap modelMap) {
        //通过session获取userid
        String userid = (String) session.getAttribute("userid");
        //查询用户信息
        UserInfo userInfo = userInfoService.LookUserinfo(userid);
        /*  ModelMap中的addAttribute与put方法的区别
        https://blog.csdn.net/qgfjeahn/article/details/52217551
        ModelMap对象的 addAttribute,put两个方法区别是: addAttribute不允许添加空值的key，put是允许的         */
        modelMap.put("userInfo",userInfo);
        //将信息显示在userinfo？这样实现嵌套？？
        return "/user/userinfo";
    }

    /**
     * 上传学生证
     * */
    @PostMapping(value = "/user/updatestuidcard")
    @ResponseBody
    public JSONObject updateStuIdCard(@RequestParam(value = "file", required = false) MultipartFile file, HttpSession session) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject resUrl = new JSONObject();
        //随机生成文件名
        String filename = UUID.randomUUID().toString().replaceAll("-", "");
        //获得文件扩展名
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        //文件全名
        String filenames = filename + "." + ext;
        //图片路径
        String pathname = "D:\\campusshops\\file\\" + filenames;
        //上传文件
        file.transferTo(new File(pathname));
        resUrl.put("src", "/pic/"+filenames);
        res.put("msg", "");
        res.put("code", 0);
        res.put("data", resUrl);
        String uimgUrl = "/pic/" + filenames;

        String userid=(String) session.getAttribute("userid");
        //实例化一个用户，设置userid和头像url，便于作为参数，更新数据表
        UserInfo userInfo = new UserInfo().setUserid(userid).setStuidcard(uimgUrl);
        //根据主键userid，修改用户信息
        userInfoService.UpdateUserInfo(userInfo);
        return res;
    }

    /**
     * 跳转到完善个人信息
     *
     * http://localhost:8996/user/perfectinfo
     * 感觉是注册后的要完善个人信息
     */
    @GetMapping("/user/perfectinfo")
    public String perfectInfo(HttpSession session, ModelMap modelMap) {
        //通过session获取userid
        String userid = (String) session.getAttribute("userid");
        //根据主键userid查询用户信息
        UserInfo userInfo = userInfoService.LookUserinfo(userid);
        modelMap.put("perfectInfo",userInfo);
        //查询school表
        List<School> schoolList = schoolService.queryAllSchool();
        //放到modelMap中，用于在前台使用thymeleaf获取
        modelMap.put("schoollist",schoolList);

        return "/user/perfectInfo";
    }

    /**
     * 修改个人信息
     * 1.前端传入用户昵称（username）、用户邮箱（email）、性别（sex）、学校（school）、院系（faculty）、入学时间（startime）
     * 2.前端传入变更后的字段，未变更的不传入后台
     * 3.判断更改的用户名是否已存在
     * 4.修改个人信息
     */
    @ResponseBody
    @PostMapping("/user/updateinfo")
    public ResultVo updateInfo(@RequestBody UserInfo userInfo, HttpSession session) {
        String username = userInfo.getUsername();
        String userid = (String) session.getAttribute("userid");
        Login login = new Login();
        //如果传入用户名
        if (!StringUtils.isEmpty(username)){
            login.setUsername(username);
            //userLogin登录及判断用户是否存在
            Login login1 = loginService.userLogin(login);
            //如果该用户名对应有用户
            if (!StringUtils.isEmpty(login1)){
                return new ResultVo(false, StatusCode.ERROR, "该用户名已存在");
            }
            //设置对象的userid。便于修改数据表login的记录，因为它是根据userid或主键id来修改记录的。
            login.setUserid(userid);
            //修改login表中用户名
            loginService.updateLogin(login);
        }
        //如果不是更改用户名，则给userInfo对象设置userid。注意现在没有给login对象设置任何属性。
        userInfo.setUserid(userid);
        //修改userInfo表   ///待补充，为什么不修改login表？
        Integer integer1 = userInfoService.UpdateUserInfo(userInfo);
        if (integer1 == 1) {
            return new ResultVo(true, StatusCode.OK, "修改成功");
        }
        return new ResultVo(false, StatusCode.ERROR, "修改失败");
    }

    /**更换手机号时发送短信验证码
     * 1.判断是否为更换手机号类型验证码
     * 2.判断手机号格式是否正确
     * 3.查询账号是否存在
     * 4.发送验证码
     *
     *
     * 拦截地址，详见updatephone.html和updatephone.js中的getphonecode()
     * */
    @ResponseBody
    @PostMapping("/user/sendupdatephone")
    public ResultVo sendupdatephone(HttpServletRequest request) throws IOException {
        ////待补充，又出现了
        JSONObject json = JsonReader.receivePost(request);
        final String mobilephone = json.getString("mobilephone");
        /*optInt()取值不正确时则会试图进行转化或者返回默认值，不会抛出异常*/
        Integer type = json.getInt("type");
        Login login = new Login();
        //这个2不知道是干嘛的。。前端js中也只是写object['type']=2，感觉应该是roleid=2表示管理员？
        //可是普通用户可以更改自己的手机号吧？
        if(type!=2){
            return new ResultVo(false,StatusCode.ACCESSERROR,"违规操作");
        }
        //判断输入的手机号格式是否正确
        //JustPhone自定义类，用于判断用户输入的账号是否符合规则
        if (!JustPhone.justPhone(mobilephone)) {
            return new ResultVo(false,StatusCode.ERROR,"请输入正确格式的手机号");
        }
        //查询手机号是否存在
        login.setMobilephone(mobilephone);
        Login userIsExist = loginService.userLogin(login);
        if (!StringUtils.isEmpty(userIsExist)){//若手机号已注册过
            return new ResultVo(false, StatusCode.REPERROR,"手机号已存在");
        }
        String code = GetCode.phonecode();
        Integer result = new SmsUtil().SendMsg(mobilephone, code, type);//发送验证码
        if(result == 1) {//发送成功
            phonecodemap.put(mobilephone, code);//放入map集合进行对比

/*
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    phonecodemap2.remove(phoneNum);
                    timer.cancel();
                }
            }, 5 * 60 * 1000);
*/

            //执行定时任务
            ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    phonecodemap.remove(mobilephone);
                    ((ScheduledThreadPoolExecutor) executorService).remove(this::run);
                }
            },5 * 60 * 1000,5 * 60 * 1000, TimeUnit.HOURS);



            return new ResultVo(true,StatusCode.SMS,"验证码发送成功");
        }else if(result == 2){
            return new ResultVo(false,StatusCode.ERROR,"请输入正确格式的手机号");
        }
        return new ResultVo(false,StatusCode.REMOTEERROR,"验证码发送失败");
    }

    /**
     * 修改绑定手机号
     * 1.获取session中userid
     * 2.修改login和userInfo中对应的手机号
     */
    @ResponseBody
    @PutMapping("/user/updatephone/{mobilephone}/{vercode}")
    public ResultVo updatephone(@PathVariable("mobilephone")String mobilephone,@PathVariable("vercode")String vercode,HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        String rel = phonecodemap.get(mobilephone);
        if (StringUtils.isEmpty(rel)) {//验证码到期 或者 没发送短信验证码
            return new ResultVo(false,StatusCode.ERROR,"请重新获取验证码");
        }
        if (rel.equalsIgnoreCase(vercode)) {//验证码正确
            Login login = new Login().setUserid(userid).setMobilephone(mobilephone);
            UserInfo userInfo = new UserInfo().setUserid(userid).setMobilephone(mobilephone);
            Integer integer = loginService.updateLogin(login);
            Integer integer1 = userInfoService.UpdateUserInfo(userInfo);
            if (integer == 1 && integer1 == 1) {
                return new ResultVo(true, StatusCode.OK, "更换手机号成功");
            }
            return new ResultVo(false, StatusCode.SERVERERROR, "系统错误，更换失败");
        }
        return new ResultVo(false,StatusCode.ERROR,"验证码错误");
    }


}

