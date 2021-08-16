package com.controller;


import com.alibaba.fastjson.JSONObject;
import com.entity.*;
import com.service.*;
import com.util.GetDate;
import com.util.KeyUtil;
import com.util.StatusCode;
import com.vo.LayuiPageVo;
import com.vo.PageVo;
import com.vo.ResultVo;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 */
@Controller
public class CommodityController {
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private CommimagesService commimagesService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private SoldrecordService soldrecordService;
    @Autowired
    private CollectService collectService;
    @Autowired
    private NoticesService noticesService;
    /**
     *售出商品时查询是否首页推荐，是则取消首页推荐
     */
    @Autowired
    private RecommendService recommendService;


    /**
     * 跳转到发布商品
     * 个人中心----商品管理--发布闲置
     */
    @GetMapping("/user/relgoods")
    public String torelgoods(HttpSession session){
        /*String userid = (String)session.getAttribute("userid");
        if(userid==null){
            return "redirect:/:";
        }*/
        return "/user/product/relgoods";
    }


/*===============================个人中心----商品管理--商品清单start=================================*/

    /**
     * 跳转到修改商品
     *  --不能修改已删除、已完成的商品
     *  1、查询商品详情
     *  2、查询商品的其他图
     *
     *  个人中心----商品管理--商品清单--“编辑”按钮，显示商品的编辑界面
     *  （商品的原有数据需要显示出来，所以这里查询了数据表commodity和commimages，
     *      后者是商品其他图(与主图相对)，前者是商品的其他信息
     *  http://localhost:8996/user/editgoods/1577792919764240135
     */
    @GetMapping("/user/editgoods/{commid}")
    public String toeditgoods(@PathVariable("commid")String commid, HttpSession session, ModelMap modelMap){
        /*String userid = (String)session.getAttribute("userid");
        if(userid==null){
            return "redirect:/:";
        }*/
        Commodity commodity=commodityService.LookCommodity(new Commodity().setCommid(commid));
        if(commodity.getCommstatus().equals(2) || commodity.getCommstatus().equals(4)){
            return "/error/404";//商品已被删除或已完成交易
        }

        //common→常用选项：自提，可小刀，不议价等选项
        //common和common2的区别见Commodity实体类注释
        String[] commons=commodity.getCommon().split("、");
        commodity.setCommon(commons[0]).setCommon2(commons[1]);

        /*modelMap是参数
        ModelMap继承LinkedHashMap，spring框架自动创建实例并作为controller的入参，用户无需自己创建
            https://www.cnblogs.com/nongzihong/p/10071223.html
        通过addAttribute(String key,Object value)向页面传递参数
            https://blog.csdn.net/qq_26411021/article/details/79493308         */
        //对象commodity有一些其他的属性，用于显示在界面上
        modelMap.put("goods",commodity);
        modelMap.put("otherimg",commimagesService.LookGoodImages(commid));
        return "/user/product/changegoods";
    }

    /**
     * 修改商品
     * 1、修改商品信息
     * 2、修改商品的其他图的状态
     * 3、插入商品的其他图
     */
    @PostMapping("/changegoods/rel")
    @ResponseBody
    public String changegoods(@RequestBody Commodity commodity, HttpSession session){
        String userid = (String) session.getAttribute("userid");
        //链式赋值。设置修改时间和商品状态。3为待审核
        // ////待补充。关于审核。如果评分高于4.5就不审核，直接上架（修改状态为1，表示正常）
        commodity.setUpdatetime(new Date())
                .setCommstatus(3);
        //常用选项拼接。///即把common和common2用、拼接在一起，然后赋值给字段common
        commodity.setCommon(commodity.getCommon()+"、"+commodity.getCommon2());
        //ChangeCommodity():根据主键commid修改商品相关信息，update一些属性（如果不为null）
        commodityService.ChangeCommodity(commodity);
        /*根据商品id，修改图片的imagestatus状态字段为2，表示删除某个商品的其他图，但不是真正意义上的删除
        注意调用对象是商品其他图commimagesService，不是商品commodityService*/
        commimagesService.DelGoodImages(commodity.getCommid());
        //实例化一个列表，用于存放商品的其他图片，便于作为参数，执行insert语句
        List<Commimages> commimagesList=new ArrayList<>();
        //getOtherimg()获取商品其他图集合
        for (String list:commodity.getOtherimg()) {
            //add往列表中添加图片item。
            // 通过new得到每个item，用链式赋值来设置属性，便于等下执行insert语句
            commimagesList.add(
                    new Commimages()
                            .setId(KeyUtil.genUniqueKey())
                            .setCommid(commodity.getCommid())
                            .setImage(list)
            );
        }
        //执行insert语句，上传商品的其他图
        commimagesService.InsertGoodImages(commimagesList);

        /**发出待审核系统通知*/
        //这里实例化一个Notices对象，设置相应的属性，然后执行insert语句执行插入操作
        Notices notices = new Notices()
                .setId(KeyUtil.genUniqueKey())
                .setUserid(userid)
                .setTpname("商品审核")
                .setWhys("您的商品 <a href=/product-detail/"+commodity.getCommid()+" style=\"color:#08bf91\" target=\"_blank\" >"+commodity.getCommname()+"</a> 进入待审核队列，请您耐心等待。");
        noticesService.insertNotices(notices);

        ///待补充，不知道这个"0"是干嘛的。changegoods.html最后有个changegoods.js
        return "0";
    }

    /**
     * 个人对商品的操作     之售出、删除
     * 前端传入商品id（commid）
     * 前端传入操作的商品状态（commstatus）-->删除:2  已完成:4
     *
     *
     * http://localhost:8996/user/changecommstatus/1577792919764240135/0
     * {"flag":true,"status":200,"message":"操作成功","data":null}
     *
     * 个人中心----商品管理--商品列表--已售按钮、删除按钮
     * */
    @ResponseBody
    @GetMapping("/user/changecommstatus/{commid}/{commstatus}")
    public ResultVo ChangeCommstatus(@PathVariable("commid") String commid, @PathVariable("commstatus") Integer commstatus, HttpSession session) {
        //ChangeCommstatus():根据主键commid，只修改状态commstatus。返回修改影响的行数
        Integer i = commodityService.ChangeCommstatus(commid, commstatus);
        //修改成功
        if (i == 1){
            /**如果商品已售出，则需要向售出数据表soldrecord添加一条记录
             * commstatus为4表示商品已经完成交易*/
            if (commstatus == 4){
                //查询首页推荐，如果商品在首页推荐，需要根据commoid修改recommend表字段recomstatus=3.表示商品已经下架
                Recommend recommend = new Recommend().setCommid(commid).setRecomstatus(3).setUpdatetime(GetDate.strToDate());
                int recomUpdate = recommendService.updateRecommendStatus(recommend);

                //由于soldrecord数据表废弃，以下操作废弃。
                /**如果商品已售出，则需要向售出数据表soldrecord添加一条记录
                 * commstatus为4表示商品已经完成交易*/
                String userid = (String) session.getAttribute("userid");
                /**查询售出商品的信息
                 * 查询commodity数据表是为了获取商品的相关信息，便于等下赋值给数据表soldrecord记录的字段。*/
                Commodity commodity = commodityService.LookCommodity(new Commodity().setCommid(commid));
                //实例化一个soldrecord对象，等下执行insert语句时要用到
                Soldrecord soldrecord = new Soldrecord();
                /**将商品信息添加到售出记录中*/
                soldrecord
                        .setId(KeyUtil.genUniqueKey())
                        .setCommid(commid)
                        .setCommname(commodity.getCommname())
                        .setCommdesc(commodity.getCommdesc())
                        .setThinkmoney(commodity.getThinkmoney())
                        .setUserid(userid);
                /**添加售出记录*/
                soldrecordService.insertSold(soldrecord);
            }

            /*商品尚未售出。其实commstatus!=4，在这里一般就是commstatus=2，即删除按钮
            待处理，这里有个问题，虽然update成功了，状态改为2了，但是删除之后列表还有。
            估计前台那里要改一下，状态为2的不显示*/
            return new ResultVo(true,StatusCode.OK,"操作成功");
        }
        //修改失败update语句执行失败
        return new ResultVo(false,StatusCode.ERROR,"操作失败");
    }
    /*===============================个人中心----商品管理--商品清单end=================================*/



    /**
     * 根据商品id（commid）判断收藏商品是否存在
     *
     * http://localhost:8996/product-detail-isnull/1577792919764240135
     * {"flag":true,"status":201,"message":"您收藏的商品已经不存在，建议您取消收藏","data":null}
     *
     * http://localhost:8996/product-detail-isnull/1583935025023726250
     * {"flag":true,"status":200,"message":"查询成功","data":null}
     *
     *
     *
     * 个人中心----收藏管理--我的收藏--“详情”按钮
     * 用法：以“活着”为例。
     * 1.在前台的详情页中，收藏“活着”
     * 2.在个人中心----商品管理--商品清单中，点击“活着”那一项的“删除”按钮。（实则修改comstatus=2，表示删除）
     * 3.在个人中心----收藏管理--我的收藏中，点击“活着”那一项的“详情”按钮，欲前往该项的详情页。
     * 4.弹框提示（其实是“吐司”提示）“您收藏的商品已经不存在，建议您取消收藏”
     * */
    @GetMapping("/product-detail-isnull/{commid}")
    @ResponseBody
    public ResultVo product_detail_isnull(@PathVariable("commid") String commid) {
        //根据商品id查询商品信息，注意传入的参数是已经设置id的commodity对象
        Commodity commodity = commodityService.LookCommodity(new Commodity().setCommid(commid));
        ///待处理，输入语句删除？
        System.out.println(commodity);

        //其实这里commodity != null可以删除，因为Condition 'commodity != null' is always 'true' when reached
//        if (commodity.getCommstatus().equals(1) && commodity != null) {
        //如果商品正常
        if (commodity.getCommstatus().equals(1)) {
            System.out.println("商品正常");
            return new ResultVo(true,StatusCode.OK,"查询成功");
        }else {
            System.out.println("商品不正常");
            return new ResultVo(true,StatusCode.ERROR,"您收藏的商品已经不存在，建议您取消收藏");
        }
    }




    /*===============================个人中心----商品管理--发布闲置start=================================*/
    /**
     * 发布商品
     * 1、插入商品信息
     * 2、插入商品其他图
     *
     * 个人中心----商品管理--发布闲置
     */
    @PostMapping("/relgoods/rel")
    @ResponseBody
    public String relgoods(@RequestBody Commodity commodity, HttpSession session){
        //通过session获取用户id
        String userid = (String) session.getAttribute("userid");
        //LookUserinfo():根据userid查询用户信息
        UserInfo userInfo = userInfoService.LookUserinfo(userid);
        //生成唯一的主键作为商品主键commid
        String commid = KeyUtil.genUniqueKey();
        //链式赋值
        commodity
                .setCommid(commid)
                .setUserid(userid)
                .setSchool(userInfo.getSchool());
        //拼接属性common和common2，得到字段常用选项common
        commodity.setCommon(commodity.getCommon()+"、"+commodity.getCommon2());
        //执行插入
        commodityService.InsertCommodity(commodity);
        //实例化一个list，用于存放商品的其他图片(通过getOtherimg获取)
        List<Commimages> commimagesList=new ArrayList<>();
        for (String list:commodity.getOtherimg()) {
            //往list中添加元素(先new然后链式赋值)
            commimagesList.add(
                    new Commimages()
                            .setId(KeyUtil.genUniqueKey())
                            .setCommid(commid)
                            .setImage(list)
            );
        }
        //插入commimages数据表
        commimagesService.InsertGoodImages(commimagesList);

        //和修改商品类似，提交后也要经过审核。这里实例化一个notices对象然后链式赋值，不再赘述。
        /**发出待审核系统通知*/
        Notices notices = new Notices().setId(KeyUtil.genUniqueKey()).setUserid(userid).setTpname("商品审核")
                .setWhys("您的商品 <a href=/product-detail/"+commid+" style=\"color:#08bf91\" target=\"_blank\" >"+commodity.getCommname()+"</a> 进入待审核队列，请您耐心等待。");
        //插入一条记录
        noticesService.insertNotices(notices);
        return "0";
    }

    /**
     * 上传视频和主图
     *
     * 注意参数只是一个file，不是数组
     *
     * 关于注解RequestParam(value = "file", required = false)
     * Spring支持web应用中的分段文件上传。这种支持是由即插即用的MultipartRsolver来实现。
     *      https://blog.csdn.net/u010839779/article/details/44680349
     *
     * 稍微看了一下，感觉和php上传文件差不多，估计可以直接拿来用，
     *      这里JSONObject应该和前端的dataType:json有一定关系？
     */
    @PostMapping("/relgoods/video")
    @ResponseBody
    public JSONObject relgoodsvideo(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject resUrl = new JSONObject();
        //生UUID.randomUUID()成唯一识别码。replaceAll则是去掉-
        String filename = UUID.randomUUID().toString().replaceAll("-", "");
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String filenames = filename + "." + ext;
        //路径名称，注意这里是硬盘绝对路径
        String pathname = "C:\\campusshops\\file\\" + filenames;
        file.transferTo(new File(pathname));
        //下面这里看起来和数据表中的img字段一样，不知道是不是上面file文件夹下的pic文件夹
        resUrl.put("src", "/pic/"+filenames);
        res.put("msg", "");
        res.put("code", 0);
        res.put("data", resUrl);
        return res;
    }

    /**
     * 上传其他图片
     *
     * 商品除了主图还有一些其他图片，这里上传的就是其他图片，一般用数组存放
     * 所以这里是数组。其他估计和上面一个方法差不多
     */
    @PostMapping(value="/relgoods/images")
    @ResponseBody
    public JSONObject relgoodsimages(@RequestParam(value = "file", required = false) MultipartFile[] file) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject resUrl = new JSONObject();
        //相比上一个方法，这里多了这一步，实例化一个列表，用于存放多张图片
        List<String> imageurls=new ArrayList<>();
        //遍历，注意这里类型是MultipartFile（既是数组元素类型，也和上一个方法一样）
        for (MultipartFile files:file){
            String filename = UUID.randomUUID().toString().replaceAll("-", "");
            String ext = FilenameUtils.getExtension(files.getOriginalFilename());
            String filenames = filename + "." + ext;
            //注意这里是拼接图片路径。前面是硬盘绝对路径
            String pathname = "C:\\campusshops\\file\\" + filenames;
            files.transferTo(new File(pathname));
            //下面这一步和上一个方法有出入
            //下面这里看起来和数据表中的img字段一样，不知道是不是上面file文件夹下的pic文件夹
            imageurls.add("/pic/"+filenames);
            res.put("msg", "");
            res.put("code", 0);
        }
        //下面这一步和上一个方法有出入，存放的列表，列表中是多个图片的url，不单单是一个图片。
        resUrl.put("src", imageurls);
        res.put("data", resUrl);
        return res;
    }
    /*===============================个人中心----商品管理--发布闲置end=================================*/


    /**
     * 产品清单分页数据
     * 前端传入商品类别（category）、区域（area）
     * 最低价（minmoney）、最高价（maxmoney）
     * 后端根据session查出个人本校信息（school）
     * */

    @GetMapping("/list-number/{category}/{area}/{minmoney}/{maxmoney}")
    @ResponseBody
    public PageVo productListNumber(@PathVariable("category") String category, @PathVariable("area") String area,
                                    @PathVariable("minmoney") BigDecimal minmoney, @PathVariable("maxmoney") BigDecimal maxmoney,
                                    HttpSession session) {
        String school=null;
        if(!area.equals("全部")){
            String userid = (String) session.getAttribute("userid");
            UserInfo userInfo = userInfoService.LookUserinfo(userid);
            school = userInfo.getSchool();
        }
        Integer dataNumber = commodityService.queryAllCommodityByCategoryCount(area, school, category, minmoney, maxmoney);
        return new PageVo(StatusCode.OK,"查询成功",dataNumber);
    }

    /**
     * 产品清单界面
     * 前端传入商品类别（category）、当前页码（nowPaging）、区域（area）
     * 最低价（minmoney）、最高价（maxmoney）、价格升序降序（price：0.不排序 1.升序 2.降序）
     * 后端根据session查出个人本校信息（school）
     * */

    @GetMapping("/product-listing/{category}/{nowPaging}/{area}/{minmoney}/{maxmoney}/{price}")
    @ResponseBody
    public ResultVo productlisting(@PathVariable("category") String category, @PathVariable("nowPaging") Integer page,
                                 @PathVariable("area") String area, @PathVariable("minmoney") BigDecimal minmoney, @PathVariable("maxmoney") BigDecimal maxmoney,
                                 @PathVariable("price") Integer price, HttpSession session) {
        String school=null;
        if(!area.equals("全部")) {
            String userid = (String) session.getAttribute("userid");
            UserInfo userInfo = userInfoService.LookUserinfo(userid);
            school = userInfo.getSchool();
        }
        List<Commodity> commodityList = commodityService.queryAllCommodityByCategory((page - 1) * 16, 16, area, school, category, minmoney, maxmoney);
        for (Commodity commodity : commodityList) {
            /**查询商品对应的其它图片*/
            List<String> imagesList = commimagesService.LookGoodImages(commodity.getCommid());
            commodity.setOtherimg(imagesList);
        }

        /**自定义排序*/
        if (price != 0){
            if (price == 1){
                /**升序*/
                Collections.sort(commodityList, new Comparator<Commodity>() {//此处创建了一个匿名内部类
                    int i;
                    @Override
                    public int compare(Commodity o1, Commodity o2) {
                        if (o1.getThinkmoney().compareTo(o2.getThinkmoney()) > -1) {
                            System.out.println("===o1大于等于o2===");
                            i = 1;
                        } else if (o1.getThinkmoney().compareTo(o2.getThinkmoney()) < 1) {
                            i = -1;
                            System.out.println("===o1小于等于o2===");
                        }
                        return i;
                    }
                });
            }else if (price == 2){
                /**降序*/
                Collections.sort(commodityList, new Comparator<Commodity>() {//此处创建了一个匿名内部类
                    int i;
                    @Override
                    public int compare(Commodity o1, Commodity o2) {
                        if (o1.getThinkmoney().compareTo(o2.getThinkmoney()) > -1) {
                            System.out.println("===o1大于等于o2===");
                            i = -1;
                        } else if (o1.getThinkmoney().compareTo(o2.getThinkmoney()) < 1) {
                            System.out.println("===o1小于等于o2===");
                            i = 1;
                        }
                        return i;
                    }
                });
            }
        }
        return new ResultVo(true,StatusCode.OK,"查询成功",commodityList);
    }

    /**
     * 分页展示个人各类商品信息
     *前端传入页码、分页数量
     *前端传入商品信息状态码（commstatus）-->全部:100，已审核:1，待审核:3，违规:0，已完成:4
     *
     *
     * 暂时不清楚，难道是个人中心--商品管理--商品清单的根据商品状态进行分页查询？
     * 感觉应该是，我以为已经写过了，原来是之前先看了mapper.xml定义的sql语句
     *
     *
     * 不明白这个方法有何意义。
     * 直接访问地址访问不到，返回500，原因不明。。
     * 而且这里还要给一个假id（虽然测试不到）
     */

    @GetMapping("/user/commodity/{commstatus}")
    @ResponseBody
    public LayuiPageVo userCommodity(@PathVariable("commstatus") Integer commstatus, int limit, int page, HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        /*如果未登录，给一个假id
        为什么要给一个假id，登录了才能进入个人中心。
        除非登录过期了，但是如果给假的id，如何判断是本人发布的商品？*/
        if(StringUtils.isEmpty(userid)){
            userid = "123456";
        }

        //存放商品
        List<Commodity> commodityList=null;
        //分页总数
        Integer dataNumber;

        //看productlist.html，100表示全部商品
        if(commstatus==100){
            //状态“全部”，直接select所有语句，commstatus为null
            commodityList = commodityService.queryAllCommodity((page - 1) * limit, limit, userid,null);
            dataNumber = commodityService.queryCommodityCount(userid,null);
        }else{
            //其他状态，则根据commstatus进行查询
            commodityList = commodityService.queryAllCommodity((page - 1) * limit, limit, userid,commstatus);
            dataNumber = commodityService.queryCommodityCount(userid,commstatus);
        }
        return new LayuiPageVo("",0,dataNumber,commodityList);
    }



    /*===============================前台start===============================*/
    /*===============================商品详情页start===============================*/

    /**
     * 分页展示商家个人各类商品信息
     *前端传入页码、分页数量
     *前端传入商品信息状态码（commstatus）-->全部:100，已审核:1，待审核:3，违规:0，已完成:4
     */
    @GetMapping("/seller/commodity/{commstatus}")
    @ResponseBody
    public LayuiPageVo sellerCommodity(@PathVariable("commstatus") Integer commstatus, int limit, int page, HttpSession session) {
        String userid = (String) session.getAttribute("goodUser");
        System.out.println("gooduserid: "+userid);
        //如果未登录，给一个假id
        if(StringUtils.isEmpty(userid)){
            userid = "123456";
        }
        List<Commodity> commodityList=null;
        Integer dataNumber;
        if(commstatus==100){
            commodityList = commodityService.queryAllCommodity((page - 1) * limit, limit, userid,null);
            dataNumber = commodityService.queryCommodityCount(userid,null);
        }else{
            commodityList = commodityService.queryAllCommodity((page - 1) * limit, limit, userid,commstatus);
            dataNumber = commodityService.queryCommodityCount(userid,commstatus);
        }
        return new LayuiPageVo("",0,dataNumber,commodityList);
    }

    /**
     * 商品详情
     * 根据商品id（commid）查询商品信息、用户昵称及头像
     * 用户可以查看正常的商品
     * 商品发布者和管理员可以查看
     *
     *
     * 直接跳转到前台商品详情页
     * http://localhost:8996/product-detail/1577792919764240135
     * */
    @GetMapping("/product-detail/{commid}")
    public String product_detail(@PathVariable("commid") String commid, ModelMap modelMap,HttpSession session){
        String couserid = (String) session.getAttribute("userid");

        Commodity commodity = commodityService.LookCommodity(new Commodity().setCommid(commid).setCommstatus(1));
        int i = 0;
        if (commodity.getCommstatus().equals(1)){//如果商品正常
            i=1;
        }else if (!StringUtils.isEmpty(couserid)){//如果用户已登录
            Login login = loginService.userLogin(new Login().setUserid(couserid));
            /**商品为违规状态时：本人和管理员可查看*/
            if (commodity.getCommstatus().equals(0) && (commodity.getUserid().equals(couserid) || (login.getRoleid().equals(2) || login.getRoleid().equals(3)))){
                i=1;
                /**商品为待审核状态时：本人和管理员可查看*/
            }else if (commodity.getCommstatus().equals(3) && (commodity.getUserid().equals(couserid) || (login.getRoleid().equals(2) || login.getRoleid().equals(3)))){
                i=1;
                /**商品为已售出状态时：本人和管理员可查看*/
            }else if (commodity.getCommstatus().equals(4) && (commodity.getUserid().equals(couserid) || (login.getRoleid().equals(2) || login.getRoleid().equals(3)))){
                i=1;
            }
        }
        if(i==1){
            commodity.setOtherimg(commimagesService.LookGoodImages(commid));
            /**商品浏览量+1*/
            commodityService.ChangeCommodity(new Commodity().setCommid(commid).setRednumber(1));
            modelMap.put("userinfo",userInfoService.queryPartInfo(commodity.getUserid()));
            modelMap.put("gddetail",commodity);
            //如果没有用户登录
            if (StringUtils.isEmpty(couserid)){
                modelMap.put("collectstatus",2);
            }else {
                Collect collect = collectService.queryCollectStatus(new Collect().setCommid(commid).setCouserid(couserid));
                if(collect!=null){
                    if (collect.getCollstatus() == 2){
                        modelMap.put("collectstatus",2);
                    }else {
                        modelMap.put("collectstatus",1);
                    }
                }else {
                    modelMap.put("collectstatus",2);
                }
            }
            session.setAttribute("goodUser",commodity.getUserid());
            return "/common/product-detail";
        }else{
            return "/error/404";
        }
    }
    /*===============================商品详情页end===============================*/

    /*===============================首页，分类start===============================*/
    /**
     * 查询最新发布的8条商品
     *
     * 前台首页下方。共8个商品。
     * 如果直接访问地址，将返回很多数据，刚好和前台8个商品信息一致
     * http://localhost:8996/product/latest
     * */
    @ResponseBody
    @GetMapping("/product/latest")
    public ResultVo latestCommodity() {
        String category = "全部";
        List<Commodity> commodityList = commodityService.queryCommodityByCategory(category);
        for (Commodity commodity : commodityList) {
            /**查询商品对应的其它图片*/
            List<String> imagesList = commimagesService.LookGoodImages(commodity.getCommid());
            commodity.setOtherimg(imagesList);
        }
        return new ResultVo(true,StatusCode.OK,"查询成功",commodityList);
    }

    /**
     * 首页分类展示商品 --> 按照分类查询商品
     * 前端传入商品类别（category）
     *
     * 前台首页，点击分类，查询（如果是“全部”，则结果与latestCommodity方法一致，感觉方法latestCommodity冗余了？）
     * http://localhost:8996/index/product/3C%E6%95%B0%E7%A0%81
     * */
    @ResponseBody
    @GetMapping("/index/product/{category}")
    public ResultVo indexCommodity(@PathVariable("category") String category) {
        List<Commodity> commodityList = commodityService.queryCommodityByCategory(category);
        for (Commodity commodity : commodityList) {
            /**查询商品对应的其它图片*/
            List<String> imagesList = commimagesService.LookGoodImages(commodity.getCommid());
            commodity.setOtherimg(imagesList);
        }
        return new ResultVo(true,StatusCode.OK,"查询成功",commodityList);
    }
    /*===============================首页，分类end===============================*/

    /*===============================商品清单页面，分类start===============================*/
    /**
     * 搜索商品分页数据
     * 前端传入搜索的商品名（commname）
     *
     * http://localhost:8996/product/search/number/Python 3网络爬虫开发实战
     * {"status":200,"message":"查询成功","pages":null,"dataNumber":1,"data":null}
     *
     * 注意和直接搜索“Python 3网络爬虫开发实战”不同
     * http://localhost:8996/product-search?keys=Python+3%E7%BD%91%E7%BB%9C%E7%88%AC%E8%99%AB%E5%BC%80%E5%8F%91%E5%AE%9E%E6%88%98
     *
     *
     * http://localhost:8996/product/search/number/Python
     *{"status":200,"message":"查询成功","pages":null,"dataNumber":3,"data":null}
     *
     * 估计是前台搜索后，分页按钮旁边的共X页的X
     * */
    @GetMapping("/product/search/number/{commname}")
    @ResponseBody
    public PageVo searchCommodityNumber(@PathVariable("commname") String commname){
        //queryCommodityByNameCount模糊查询商品总数
        Integer dataNumber = commodityService.queryCommodityByNameCount(commname);
        return new PageVo(StatusCode.OK,"查询成功",dataNumber);
    }

    /**
     * 搜索商品
     * 前端传入当前页数（nowPaging）、搜索的商品名（commname）
     * */
    @GetMapping("/product/search/{nowPaging}/{commname}")
    @ResponseBody
    public ResultVo searchCommodity(@PathVariable("nowPaging") Integer page, @PathVariable("commname") String commname){
        List<Commodity> commodityList = commodityService.queryCommodityByName((page - 1) * 20, 20, commname);

        if(!StringUtils.isEmpty(commodityList)){//如果有对应商品
            for (Commodity commodity : commodityList) {
                /**查询商品对应的其它图片*/
                List<String> imagesList = commimagesService.LookGoodImages(commodity.getCommid());
                commodity.setOtherimg(imagesList);
            }
            return new ResultVo(true,StatusCode.OK,"查询成功",commodityList);
        }else{
            return new ResultVo(true,StatusCode.ERROR,"没有相关商品");
        }
    }

}

