package com.controller;
import com.entity.Order;
import com.entity.UserInfo;
import com.service.CommodityService;
import com.service.OrderService;
import com.service.UserInfoService;
import com.util.StatusCode;
import com.vo.LayuiPageVo;
import com.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * <p>
 * 订单控制器
 * </p>
 */
@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CommodityService commodityService;


    /**
     * 用于计算用户评分和修改用户评分
     */
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 分页查看用户所有购入订单
     * 1.前端传入页码、分页数量
     * 2.查询分页数据
     */
    @ResponseBody
    @GetMapping("/buyinorder/querybuyinorder")

    public LayuiPageVo queryBuyInOrder(int limit, int page, HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        //查询数据
        List<Order> orderList = orderService.queryAllBuyInOrder((page - 1) * limit, limit, userid);
        //查询记录总数
        Integer dataNumber = orderService.queryBuyInOrderCount(userid);
        return new LayuiPageVo("", 0, dataNumber, orderList);
    }

    /**
     * 分页查看用户所有售出订单
     * 1.前端传入页码、分页数量
     * 2.查询分页数据
     */
    @ResponseBody
    @GetMapping("/selloutorder/queryselloutorder")
    public LayuiPageVo querySellOutOrder(int limit, int page, HttpSession session) {
        String userid = (String) session.getAttribute("userid");
        //查询数据  用户查看售出订单，此时userid其实充当了sellerid
        List<Order> orderList = orderService.queryAllSellOutOrder((page - 1) * limit, limit, userid);
        //查询记录总数
        Integer dataNumber = orderService.querySellOutOrderCount(userid);
        return new LayuiPageVo("", 0, dataNumber, orderList);
    }

    /**
     * 修改订单的订单状态
     * 1.前端传入需删除记录的id（id）       id其实就是orderid
     * 2.判断是否是本人
     */
    @ResponseBody
    @PutMapping("/updateorder/{id}/{operation}")
    public ResultVo updateOrderStatus(@PathVariable("id") String id, @PathVariable("operation") String operation) {
        //orderStatus存放即将得到的订单状态
        int orderStatus = -1;
        if ("shanchujilu".equals(operation)) {
            orderStatus = 7;
        } else if ("receive".equals(operation)) {
            orderStatus = 3;
        } else if ("deliver".equals(operation)) {
            orderStatus = 2;
        } else if ("offtheshelf".equals(operation)) {
            //如果是下架，还需要操作商品表！修改commstatus为4，表示已经售出，不再显示和被搜索了
            orderStatus = 4;
        }
        if (orderStatus != -1) {
            Order order = new Order().setId(id).setOrderstatus(orderStatus);
            Integer i = orderService.updateOrder(order);
            if (orderStatus == 4) {
                //查询商品id。便于修改商品状态
                String commid = orderService.queryCommidById(id);
                //修改商品状态
                Integer updateReturn = commodityService.ChangeCommstatus(commid, 4);
                if (i == 1 && updateReturn == 1) {
                    return new ResultVo(true, StatusCode.OK, "修改订单状态成功");
                }
            }
            return new ResultVo(true, StatusCode.OK, "修改订单状态成功");
        }
        return new ResultVo(false, StatusCode.ERROR, "修改订单状态失败");
    }


//    /**
//     * 修改购入订单的订单状态
//     * 1.前端传入需删除记录的id（id）       id其实就是orderid
//     * 2.判断是否是本人
//     * */
//    @ResponseBody
//    @PutMapping("/buyinorder/update/{id}/{operation}")
//    public ResultVo updateBuyInOrderStatus (@PathVariable("id") String id, @PathVariable("operation") String operation) {
//        //加上参数HttpSession session
//        //通过session获取用户id
////        String userid = (String) session.getAttribute("userid");
////        if(buyerid.equals(userid)){
////        }
//
//        //orderStatus存放即将得到的订单状态
//        int orderStatus = -1;
//        if("shanchujilu".equals(operation)){
//            orderStatus = 7;
//        }else if("receive".equals(operation)){
//            orderStatus = 3;
//        }
//        if(orderStatus!=-1){
//            Order order = new Order().setId(id).setOrderstatus(orderStatus);
//            Integer i = orderService.updateOrder(order);
//            if (i == 1){
//                return new ResultVo(true, StatusCode.OK,"修改购入订单状态成功");
//            }
//        }
//
//        return new ResultVo(false, StatusCode.ERROR,"修改购入订单状态失败");
//    }
//
//    /**
//     * 修改售出订单的订单状态
//     * 1.前端传入需删除记录的id（id）       id其实就是orderid
//     * 2.判断是否是本人
//     * */
//    @ResponseBody
//    @PutMapping("/selloutorder/update/{id}/{operation}")
//    public ResultVo updateSellOutOrderStatus (@PathVariable("id") String id, @PathVariable("operation") String operation) {
//        //加上参数HttpSession session
//        //通过session获取用户id
////        String userid = (String) session.getAttribute("userid");
////        if(sellerid.equals(userid)){
////        }
//        //orderStatus存放即将得到的订单状态
//        int orderStatus = -1;
//        if("shanchujilu".equals(operation)){
//            orderStatus = 7;
//        }else if("deliver".equals(operation)){
//            orderStatus = 2;
//        }
//        else if("offtheshelf".equals(operation)){
//            orderStatus = 4;
//        }
//        if(orderStatus!=-1){
//            Order order = new Order().setId(id).setOrderstatus(orderStatus);
//            Integer i = orderService.updateOrder(order);
//            if (i == 1){
//                return new ResultVo(true, StatusCode.OK,"修改售出订单状态成功");
//            }
//        }
//        return new ResultVo(false, StatusCode.ERROR,"修改售出订单状态失败");
//    }


    //跳转到评分页面
    @GetMapping("/toscoreview/{id}")
    public String toScoreView(@PathVariable("id") String id, ModelMap modelMap, HttpSession session) {
        ////待补充。那两个参数如何处理
        return "/user/order/score";
    }


    /**
     * 修改购入订单的评分字段
     * 即对卖家进行评分
     * 1.前端传入记录的id和评分和卖家的id      id其实就是orderid
     * 2.判断是否是本人
     */
    @ResponseBody
    @PutMapping("/updatesellerstar/{id}/{starvalue}/{sellerid}")
    public ResultVo updateSellerStar(@PathVariable("id") String id, @PathVariable("starvalue") Integer starvalue, @PathVariable("sellerid") String sellerid) {
//        try {
//            int star = Integer.parseInt(starvalue);
        int star = starvalue;
        //1.获取一个order实例并赋值属性。然后更新数据表orderrecord中的字段startoseller和orderstatus
        //后者容易忘记，需要修改购入订单状态为5，表示已完成
        Order order = new Order().setId(id).setSellerid(sellerid).setStartoseller(star).setOrderstatus(5);
        //修改订单状态
//        Integer orderStatusReturn = orderService.updateOrder(order);
//        //修改给卖家的评分starttoseller
//        Integer orderStarttosellerReturn = orderService.updateStartoseller(order);
        Integer orderReturn = orderService.updateOrder(order);

        //2.修改被评分的卖家的star字段（先根据规则，计算得分，然后再修改）
        //2.1查询orderrecord近30天内的star字段(注意和用户原来的已有评分无关，直接重新算）
        List<Integer> starList = orderService.queryStarBySellerId(sellerid);
        //2.2根据返回结果，进行如下操作：①计算所有star之和 ②记录总数 ③根据公式：所有star之和÷订单记录总数=最终评分
        //注意上面先更新了字段startoseller，所以这里查询回来的包括刚才的评分
        int sum = 0;
        int num = starList.size();
        //增强的for循环
        for (Integer starVal : starList) {
//            for (int i=0; i<starList.size(); i++) {
//                sum += starList.get(i);
            sum += starVal;
        }
        //保留小数点
        double finalScore = sum / num;
        //3.更新user_info表的字段star
        UserInfo userInfo = new UserInfo().setUserid(sellerid).setStar(finalScore);
        int userReturn = userInfoService.UpdateUserInfo(userInfo);

        if (orderReturn != 0 && userReturn != 0) {
            return new ResultVo(true, StatusCode.OK, "评分成功");
        }

//        } catch (NumberFormatException e) {
//            e.printStackTrace();
////            return new ResultVo(false, StatusCode.ERROR, "评分失败");
//        }
        else {
            return new ResultVo(false, StatusCode.ERROR, "评分失败");
        }
    }



    /**
     * 修改售出订单的评分字段
     * 即对买家进行评分
     * 1.前端传入记录的id和评分和买家的id      id其实就是orderid
     * 2.判断是否是本人
     */
    @ResponseBody
    @PutMapping("/updatebuyerstar/{id}/{starvalue}/{buyerid}")
    public ResultVo updateBuyerStar(@PathVariable("id") String id, @PathVariable("starvalue") Integer starvalue, @PathVariable("buyerid") String buyerid) {
        int star = starvalue;
        //1.获取一个order实例并赋值属性。然后更新数据表orderrecord中的字段starttobuyter和orderstatus(修改购入订单状态为5，表示已完成)
        Order order = new Order().setId(id).setBuyerid(buyerid).setStartobuyer(star).setOrderstatus(5);
        //修改订单状态和给买家的评分starttobuyer
        Integer orderReturn = orderService.updateOrder(order);

        //2.修改被评分的买家的star字段（先根据规则，计算得分，然后再修改）
        //2.1查询orderrecord近30天内的star字段(注意和用户原来的已有评分无关，直接重新算）
        List<Integer> starList = orderService.queryStarByBuyerId(buyerid);
        //2.2根据返回结果，进行如下操作：①计算所有star之和 ②记录总数 ③根据公式：所有star之和÷订单记录总数=最终评分
        //注意上面先更新了字段startoseller，所以这里查询回来的包括刚才的评分
        int sum = 0;
        int num = starList.size();
        for (Integer starVal : starList) {
            sum += starVal;
        }
        //保留小数点
        double finalScore = sum / num;
        //3.更新user_info表的字段star
        UserInfo userInfo = new UserInfo().setUserid(buyerid).setStar(finalScore);
        int userReturn = userInfoService.UpdateUserInfo(userInfo);

        if (orderReturn != 0 && userReturn != 0) {
            return new ResultVo(true, StatusCode.OK, "评分成功");
        }
        else {
            return new ResultVo(false, StatusCode.ERROR, "评分失败");
        }
    }

    /**
     * 后台管理员查看所有订单分页查看用户所有售出订单
     * 1.前端传入页码、分页数量
     * 2.查询分页数据
     */
    @ResponseBody
    @GetMapping("/admin/queryallorder")
    public LayuiPageVo adminQueryAllOrder(int limit, int page) {
        //查询数据  用户查看售出订单，此时userid其实充当了sellerid
        List<Order> orderList = orderService.adminQueryAllOrder((page - 1) * limit, limit);
        //查询记录总数
        Integer dataNumber = orderService.adminQueryOrderCount();
        return new LayuiPageVo("", 0, dataNumber, orderList);
    }


    /**
     *后台统计图，按照年份月份查询
     * select * from  orderrecord where year(soldtime)='2021' and month(soldtime)='8'
     */
    @ResponseBody
    @PutMapping("/admin/drawbyym/{tyear}/{tmonth}/{tgender}")
    public LayuiPageVo showDiagramByYearMonth(@PathVariable("tyear") String tyear, @PathVariable("tmonth") String tmonth, @PathVariable("tgender") Integer tgender) {
        //根据年份月份查询记录
        List<Order> dateListByYearMonth = orderService.showDiagramByData(tyear,tmonth);
        int dataNumber=0;
        String sex = "";
        if(tgender==1){
            sex = "男";
        }else if(tgender==2){
            sex = "女";
        }
        //根据记录中的buyerid，查询user_info，查看性别
        for(Order d: dateListByYearMonth){
            UserInfo u = userInfoService.LookUserinfo(d.getBuyerid());
            if(u.getSex().equals(sex)){
                dataNumber+=1;
            }
        }
        return new LayuiPageVo("", 200, dataNumber,dataNumber);
    }


}