package com.controller;

import com.entity.*;
import com.service.*;
import com.util.GetDate;
import com.util.KeyUtil;
import com.util.StatusCode;
import com.vo.LayuiPageVo;
import com.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 推荐商品控制器
 * </p>
 */
@Controller
public class RecommendController {
    @Autowired
    private RecommendService recommendService;

    /**
     * 用于查询商品的userid
     */
    @Autowired
    private CommodityService commodityService;

    /**
     * 用于发布通知
     */
    @Autowired
    private NoticesService noticesService;


    /**
     * 首页显示商品
     * 查询后用model放到前台中使用
     * 见IndexController.java的index方法
     */


    /**
     * 分页管理员查看各类推荐商品的信息
     * 前端传入页码、分页数量
     * 前端传入商品信息状态码（commstatus）-->全部:100，违规:0，已审核:1，待审核:3 已完成:4
     * 因为是管理员查询，将userid设置为空
     */
    @GetMapping("/admin/recommend/{recomstatus}")
    @ResponseBody
    public LayuiPageVo showRecommendByRecomstatus(@PathVariable("recomstatus") Integer recomstatus, int limit, int page) {
        if (recomstatus == 100) {
            //全部商品，则recomstatus传入null
            List<Recommend> recommendList = recommendService.queryRecommendByRecomStatus((page - 1) * limit, limit, null);
            Integer dataNumber = recommendService.queryRecommendCountByRecomStatus(null);
            return new LayuiPageVo("", 0, dataNumber, recommendList);
        } else {
            List<Recommend> recommendList = recommendService.queryRecommendByRecomStatus((page - 1) * limit, limit, recomstatus);
            Integer dataNumber = recommendService.queryRecommendCountByRecomStatus(recomstatus);
            return new LayuiPageVo("", 0, dataNumber, recommendList);
        }
    }


    /**
     * 管理员对推荐商品的操作：审核通过、审核不通过
     * 前端传入商品id（commid）
     * 前端传入操作的推荐商品状态（recomstatus）-->不通过推荐是3，通过推荐是2(见数据表recommend的recomstatus)
     */
    @ResponseBody
    @PutMapping("/admin/changerecomstatus/{commid}/{recomstatus}")
    public ResultVo changeRecomStatus(@PathVariable("commid") String commid, @PathVariable("recomstatus") Integer recomstatus) {
        Recommend recommend = new Recommend().setCommid(commid).setRecomstatus(recomstatus).setUpdatetime(GetDate.strToDate());
        Integer i = recommendService.updateRecommendStatus(recommend);
        if (i == 1) {
            /**发出商品审核结果的系统通知*/
            //用于查询商品的发布人userid，等下在插入数据表notice用到
            Commodity commodity = commodityService.LookCommodity(new Commodity().setCommid(commid));
            if (recomstatus == 2) {
                Notices notices = new Notices().setId(KeyUtil.genUniqueKey()).setUserid(commodity.getUserid()).setTpname("推荐商品")
                        .setWhys("您的商品 <a href=/product-detail/" + commodity.getCommid() + " style=\"color:#08bf91\" target=\"_blank\" >" + commodity.getCommname() + "</a> 未通过推荐，暂时无法显示首页推荐。");
                noticesService.insertNotices(notices);
            } else if (recomstatus == 1) {
                Notices notices = new Notices().setId(KeyUtil.genUniqueKey()).setUserid(commodity.getUserid()).setTpname("推荐商品")
                        .setWhys("您的商品 <a href=/product-detail/" + commodity.getCommid() + " style=\"color:#08bf91\" target=\"_blank\" >" + commodity.getCommname() + "</a> 已通过推荐，可以在首页推荐上展示。");
                noticesService.insertNotices(notices);
            }
            return new ResultVo(true, StatusCode.OK, "审核操作成功");
        }
        return new ResultVo(false, StatusCode.ERROR, "审核操作失败");
    }


    /**
     * 跳转到表单提交。用model显示商品信息在输入框中
     * 对应productlist.js中的obj.event === 'askforrecommend'的content: basePath+'/user/recommendform',
     */
    @GetMapping("/user/recommendform/{commid}")
    public String toRecommendForm(@PathVariable("commid") String commid, ModelMap modelMap) {
        Commodity oriCom = new Commodity().setCommid(commid);
        //根据传入的commid查询商品信息（名称、图片、描述）
        Commodity commodityInfo = commodityService.LookCommodity(oriCom);
        modelMap.put("commodityInfo", commodityInfo);

        //返回templates下的user/recommendform.html
        return "/user/recommendform";
    }


    /**
     * 用户提交表单：商品推荐
     * <p>
     * 参考CommodityController.java的relgoods方法，它拦截relgoods.js
     * 后者也是通过jsonobject传数据
     * <p>
     * 这里注解requestBody应该是自定义一个名称，把json数据注入到实体类中，便于阅读。js传过来的是data: jsonData,
     */
    @ResponseBody
    @PostMapping("/recommend/updaterecommendinfo")
    public ResultVo updateRecommendInfo(@RequestBody Recommend recommendInfo) {
        //生成唯一的主键作为商品主键commid
        String recommendId = KeyUtil.genUniqueKey();
        Date updatetime = GetDate.strToDate();
        Recommend finalRecommend = new Recommend()
                .setId(recommendId)
                .setCommid(recommendInfo.getCommid())
                .setRecomname(recommendInfo.getRecomname())
                .setRecomimg(recommendInfo.getRecomimg())
                .setRecomdesc(recommendInfo.getRecomdesc())
                .setRecomstatus(0)
                .setUpdatetime(updatetime);

        Integer integer1 = recommendService.addRecommend(finalRecommend);
        //修改commodity表的字段askrecomstatus为1表示已经申请，这样就不会显示“申请推荐”按钮
        Commodity tempCom = new Commodity().setCommid(recommendInfo.getCommid()).setAskrecomstatus(1);
        Integer commUpdate = commodityService.ChangeCommodity(tempCom);
        if (integer1 == 1 && commUpdate==1) {
            return new ResultVo(true, StatusCode.OK, "申请推荐商品成功，请等待审核");
        }
        return new ResultVo(false, StatusCode.ERROR, "申请推荐商品失败");
    }

    /**
     * 用户上传推荐商品图片
     */
    @PostMapping(value = "/recommend/updaterecomimg")
    @ResponseBody
    public JSONObject updateRecomImg(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
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


        res.put("recomimg", uimgUrl);
        return res;
    }

}