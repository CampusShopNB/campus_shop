package com.controller;

import com.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * 学校控制器
 * </p>
 */
@Controller
public class SchoolController {
    @Autowired
    private SchoolService schoolService;

    /**
     * 新增
     */

    /**
     * 查询所有学校，显示在perfectinfo.html
     * (废弃)，在UserController方法perfectInfo中：modelMap.put("schoollist",schoolList);
     */



}