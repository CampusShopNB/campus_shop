package com.controller;

import com.entity.School;
import com.entity.UserInfo;
import com.entity.Wantedschool;
import com.service.SchoolService;
import com.service.UserInfoService;
import com.service.WantedschoolService;
import com.util.KeyUtil;
import com.util.StatusCode;
import com.vo.LayuiPageVo;
import com.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 请求新增学校控制器
 * </p>
 */
@Controller
public class WantedschoolController {
    @Autowired
    private WantedschoolService wantedschoolService;
    /**
     * 修改个人信息的学校字段
     */
    @Autowired
    private UserInfoService userInfoService;
    /**
     * 用于插入school表
     */
    @Autowired
    private SchoolService schoolService;

    /**
     * 新增记录
     */
    @ResponseBody
    @PutMapping("/addschool/{schoolname}/{schoolcode}")
    public ResultVo addSchool(@PathVariable("schoolname") String schoolname, @PathVariable("schoolcode") String schoolcode, HttpSession session) {
        String id = KeyUtil.genUniqueKey();
        String userid = (String) session.getAttribute("userid");
        Wantedschool wantedschool = new Wantedschool().setId(id).setUserid(userid).setSchoolname(schoolname).setProcessstatus(0).setSchoolcode(schoolcode);
        Integer ifSchoolExist = wantedschoolService.querySchoolname(schoolname);

        //修改user_info的字段applyschoolstatus为1，表示已提交申请但待通过
        UserInfo userInfo = new UserInfo().setUserid(userid).setApplyschoolstatus(1);
        Integer userRe = userInfoService.UpdateUserInfo(userInfo);

        if(ifSchoolExist==0){
            //还没有同学提交过申请
            Integer i = wantedschoolService.addSchool(wantedschool);
            if (i == 1 && userRe==1) {
                return new ResultVo(true, StatusCode.OK, "提交新增学校申请成功");
            }
//            return new ResultVo(false, StatusCode.ERROR, "提交新增学校申请失败");
        }else {
            //已经有同学提交过申请了
            if (userRe==1) {
                //修改字段成功，返回提示。
                return new ResultVo(true, StatusCode.ALREADYEXIST, "已经有同学提交过申请啦，无需重复提交");
            }
        }
        return new ResultVo(false, StatusCode.ERROR, "提交新增学校申请失败");
    }



    /**
     * 分页查看所有待审核的记录和数目
     * 前端传入页码、分页数量
     */
    // @GetMapping("/queryallwantedschool")
    @GetMapping("/admin/schoollist")
    @ResponseBody
    public LayuiPageVo showWantedSchoolList(int limit, int page) {
        //查询所有记录
        List<Wantedschool> wantedschoolList = wantedschoolService.queryAllUnprocessSchool((page - 1) * limit, limit);
        //查询总数
        Integer dataNumber = wantedschoolService.queryAllUnprocessSchoolCount();
        return new LayuiPageVo("", 0, dataNumber, wantedschoolList);
    }

    /**审核通过
     * 更新状态
     *
     * 注意这里不需要参数int page,int count来分页。上一个方法才要。
     */
    @PutMapping("/admin/updateschoolstatus/{id}")
    @ResponseBody
    public ResultVo updateSchoolStatus(@PathVariable("id") String id) {
        //0.为了避免传输中文乱码，还是从数据库查询学校名称，另外，顺便查询userid，懒得从前端获取了
        Wantedschool wantedschool = wantedschoolService.queryAllFielById(id);
        //从数据库查询回来的，状态为0，要改为1再作为参数传入updateSchoolStatus方法
        wantedschool.setProcessstatus(1);
        //1.根据id，修改wantedschool表的字段processstatus为1
        Integer wantedschoolRe = wantedschoolService.updateSchoolStatus(wantedschool);
        //临时补充：要像数据表School中插入记录
        School newschool = new School()
                .setId(KeyUtil.genUniqueKey())
                .setSchoolname(wantedschool.getSchoolname())
                .setStatus(1)
                .setSchoolcode(wantedschool.getSchoolcode());
        Integer schoolI= schoolService.addSchool(newschool);


        //2.根据user_id，修改user_info的school字段（废弃，不管用户是否提交申请，school字段都写好。要么在下拉列表选，要么就按提交的学校名称）
//        UserInfo userInfo = new UserInfo().setUserid(wantedschool.getUserid()).setSchool(wantedschool.getSchoolname());
//        Integer userRe = userInfoService.UpdateUserInfo(userInfo);
        //2.根据列表的user_id修改user_info的applyschoolstatus字段为2表示已经通过
        UserInfo userInfo = new UserInfo().setUserid(wantedschool.getUserid()).setApplyschoolstatus(2);
        Integer userRe = userInfoService.UpdateUserInfo(userInfo);

        if (wantedschoolRe==1 && schoolI==1 && userRe==1) {
            //3.根据school，修改user_info中applyschoolstatus字段为1的，修改为2
            //还是要先查询有没有，如果没有就没必要更新。因为不清楚如果报错会返回什么。。
            Integer applyCount = userInfoService.queryApplyCountBySchool(wantedschool.getSchoolname());
            //还有其他同学申请，要继续修改
            if(applyCount!=0){
                //更改多条记录
                Integer userAllRe = userInfoService.updateApplyStatusBySchoolName(wantedschool.getSchoolname());
                if (userAllRe!=0) {
                    //算了。不要了。那么多用户每条都发，sql执行次数太多了，本来这个方法操作的数据表就多
//                    /**发出待审核系统通知*/
//                    Notices notices = new Notices()
//                            .setId(KeyUtil.genUniqueKey())
//                            .setUserid(wantedschool.getUserid())
//                            .setTpname("学校审核")
//                            .setWhys("您提交的学校申请已经通过了");
//                    //插入一条记录
//                    noticesService.insertNotices(notices);

                    return new ResultVo(true, StatusCode.OK, "审核成功");
                }
            }


            //没有其他同学申请，直接返回成功
//            /**发出待审核系统通知*/
//            Notices notices = new Notices()
//                    .setId(KeyUtil.genUniqueKey())
//                    .setUserid(userid)
//                    .setTpname("商品审核")
//                    .setWhys("您的商品 <a href=/product-detail/"+commid+" style=\"color:#08bf91\" target=\"_blank\" >"+commodity.getCommname()+"</a> 进入待审核队列，请您耐心等待。");
//            //插入一条记录
//            noticesService.insertNotices(notices);
            return new ResultVo(true, StatusCode.OK, "审核成功");


        }
        //数据库操作失败，系统出错了
        return new ResultVo(false, StatusCode.ERROR, "审核失败");
    }
}