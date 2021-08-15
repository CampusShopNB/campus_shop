package com.controller;


import com.entity.Collect;
import com.service.CollectService;
import com.util.GetDate;
import com.util.KeyUtil;
import com.util.StatusCode;
import com.vo.LayuiPageVo;
import com.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 *  收藏控制器
 * </p>
 */
@Controller
public class CollectController {
    @Autowired
    private CollectService collectService;

    /**
     * 商品详情界面：收藏商品or取消收藏
     * 前端传入收藏操作（colloperate：1收藏，2取消收藏）,获取session中用户id信息，判断是否登录
     * (1). 收藏商品
     * 1.前端传入商品id（commid）、商品名（commname）、商品描述（commdesc）、商品用户id（cmuserid）
     *   商品用户名（username）、商品所在学校（school）
     * 2.session中获取收藏用户id（couserid）
     * 3.进行收藏操作
     * (2). 取消收藏
     * 1.前端传入商品id（commid）
     * 2.判断是否本人取消收藏
     * 3.进行取消收藏操作
     *
     * ///其中返回类型Resulto见com.vo包
     *      *
     *      *
     * 具体判断流程如下：
     * 1.判断用户是否登录
     * 2.colloperate=1表示收藏商品
     *   2.1先查询商品是否已经收藏（前端是查询数据后直接根据字段status来判断是否显示爱心）
     *     （1）如果已经收藏过（数据表中有记录），只需update修改相关信息
     *     （2）如果从未收藏过（数据表中没有任何记录），则直接执行insert语句
     * 3.colloperate!=1表示取消收藏商品（说明数据表中已经有记录了）
     *   3.1先判断是否为本人操作（//个人感觉有点多余）
     *     （1）是本人：执行update语句，修改相关信息。注意不是直接delete掉
     *     （2）不是本人：禁止操作
     */
    @ResponseBody
    @PostMapping("/collect/operate")
    public ResultVo insertcollect(@RequestBody Collect collect, HttpSession session){
        //收藏用户id
        String couserid = (String) session.getAttribute("userid");
        //getColloperate获取用户的收藏操作
        Integer colloperate = collect.getColloperate();
        //设置收藏用户id
        collect.setCouserid(couserid);

        //判断是否登录
        //StringUtils是 JDK 提供的 String 类型操作方法的补充
        if (StringUtils.isEmpty(couserid)){
            //通过有参构造(有2个,区别在于参数data)实例化一个ResultVo对象。com.util.StatusCode为状态码。
            return new ResultVo(false, StatusCode.ACCESSERROR,"请先登录");
        }
        //colloperate为1表示收藏
        if (colloperate == 1){
            //queryCollectStatus查询商品是否被用户收藏。resultType="com.entity.Collect"
            Collect collect1 = collectService.queryCollectStatus(collect);
            //数据表collect中有记录，说明之前已经收藏过该商品
            //？？返回不是collect对象？为何可以用StringUtils
            if(!StringUtils.isEmpty(collect1)){
                /**更改原来的收藏信息和状态
                 * Java中的链式设置属性。（实现：在setter中return this，就可以继续setXX()
                 * https://blog.csdn.net/weixin_42541479/article/details/117772915
                 * Collect.java中加了注解@Accessors(chain = true)
                 * */
                collect1.setCommname(collect.getCommname()).setCommdesc(collect.getCommdesc()).setSchool(collect.getSchool())
                        .setSoldtime(GetDate.strToDate());
                //update修改收藏状态。返回的数字表示修改情况是否成功
                Integer i = collectService.updateCollect(collect);
                if (i == 1){
                    return new ResultVo(true, StatusCode.OK,"收藏成功");
                }
                return new ResultVo(false,StatusCode.ERROR,"收藏失败");
            }else{
                //数据表collect中无记录，说明之前没有收藏该商品
                //自定义工具类com.util.KeyUtil获取唯一主键。
                collect.setId(KeyUtil.genUniqueKey());
                //执行insert插入操作
                Integer i = collectService.insertCollect(collect);
                //返回数字表示insert语句执行情况
                if (i == 1){
                    return new ResultVo(true, StatusCode.OK,"收藏成功");
                }
                return new ResultVo(false,StatusCode.ERROR,"收藏失败");
            }
        //colloperate不为1（实际上是2），表示取消收藏
        }else {
            //查询商品是否被用户收藏
            Collect collect1 = collectService.queryCollectStatus(collect);
            /**判断是否为本人操作*/
            if (collect1.getCouserid().equals(couserid)){
                Integer i = collectService.updateCollect(collect);
                if (i == 1){
                    return new ResultVo(true, StatusCode.OK,"取消成功");
                }
                return new ResultVo(false,StatusCode.ERROR,"取消失败");
            }
            //不是本人，禁止修改
            return new ResultVo(false,StatusCode.ACCESSERROR,"禁止操作");
        }
    }

    /**个人中心页面的收藏列表，取消收藏按钮
     *
     *
     * 收藏列表界面取消收藏
     * 1.前端传入收藏id（id）
     * 2.判断是否本人取消收藏
     * 3.进行取消收藏操作
     *
     *
     * PostMapping和PutMapping两者差别不是很明显，都是用来向服务器提交信息。
     *      如果是添加信息，倾向于用@PostMapping，如果是更新信息，倾向于用@PutMapping。
     *      https://blog.csdn.net/q290994/article/details/84101829
     *
     */
    @ResponseBody
    @PutMapping("/collect/delete/{id}")
    public ResultVo deletecollect(@PathVariable("id") String id,HttpSession session){
        String couserid = (String) session.getAttribute("userid");
        Collect collect = new Collect().setId(id).setCouserid(couserid);
        Collect collect1 = collectService.queryCollectStatus(collect);
        /**判断是否为本人操作*/
        if (collect1.getCouserid().equals(couserid)){
            collect.setColloperate(2);
            Integer i = collectService.updateCollect(collect);
            if (i == 1){
                return new ResultVo(true, StatusCode.OK,"取消成功");
            }
            return new ResultVo(false,StatusCode.ERROR,"取消失败");
        }
        return new ResultVo(false,StatusCode.ACCESSERROR,"禁止操作");
    }

    /**
     * 分页查看用户所有收藏内容
     * 前端传入页码、分页数量
     * 查询分页数据
     */
    @ResponseBody
    @GetMapping("/user/collect/queryall")
    public LayuiPageVo usercollect(int limit, int page, HttpSession session) {
        String couserid = (String) session.getAttribute("userid");
        List<Collect> collectList = collectService.queryAllCollect((page - 1) * limit, limit, couserid);
        Integer dataNumber = collectService.queryCollectCount(couserid);
        return new LayuiPageVo("",0,dataNumber,collectList);
    }
}

