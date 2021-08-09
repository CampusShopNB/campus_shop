layui.config({
    base: '../../../layuiadmin/' //静态资源所在路径
}).extend({
    index: 'lib/index' //主入口模块
}).use(['index', 'set']);

layui.use('laydate', function(){
    var laydate = layui.laydate;

    //年选择器 限定可选日期
    var ins22 = laydate.render({
        elem: '#time'
        ,type: 'year'
        ,theme: 'molv'
        ,min: '2000-01-01'
        ,max: maxDate()
        ,btns: ['confirm']
    });
});
function maxDate() {
    var now = new Date();
    return now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate();
}

layui.use(['form', 'element', 'util', 'carousel', 'form', 'laypage', 'layer','table','upload'], function () {
    var element = layui.element;
    var util = layui.util;
    var carousel = layui.carousel;
    var form = layui.form;
    var laypage = layui.laypage
        , layer = layui.layer
        , upload=layui.upload;
    //普通图片上传
    upload.render({
        elem: '#uimage'
        ,url: basePath+'/user/updateuimg'
        ,before: function(obj){
            layer.load(1, { //icon支持传入0-2
                content: '上传中',
                success: function (layero) {
                    layero.find('.layui-layer-content').css({
                        'padding-top': '39px',
                        'width': '60px'
                    });
                }
            });
        },done: function(res){
            layer.closeAll('loading');
            layer.msg('上传成功', {
                time: 1000,
                icon: 1,
                // offset: '150px'
            }, function () {
                //如果刷新下面就被清空了，但是不刷新就无法显示图片
                location.reload();
            });
        },error: function(){
            layer.msg('上传失败');
        }
    });

    //上传学生证
    upload.render({
        elem: '#stuidcard'
        ,url: basePath+'/user/updatestuidcard'
        ,before: function(obj){
            layer.load(1, { //icon支持传入0-2
                content: '上传中',
                success: function (layero) {
                    layero.find('.layui-layer-content').css({
                        'padding-top': '39px',
                        'width': '60px'
                    });
                }
            });
        },done: function(res){
            layer.closeAll('loading');
            layer.msg('上传成功', {
                time: 1000,
                icon: 1,
                // offset: '150px'
            }, function () {
                //如果刷新，会把刚才填写的学校院系都清空。
                // location.reload();
                // layer.msg('上传成功');//前面已经有上传成功的msg了
                //隐藏上传按钮
                $("#stuidcard").hide();
                //显示成功的图标
                $("#stuidcardsuccess").show();
                //显示成功的文本提示
                $("#addstuidcardtips").show();
            });
        },error: function(){
            layer.msg('上传失败');
        }
    });


    form.on('submit(demo1)', function(data){
        var object = new Object();
        object["sign"] = data.field.sign;
        object["school"] = data.field.school;
        object["faculty"] = data.field.faculty;
        object["startime"] = data.field.startime;
        object["sex"] = data.field.sex;

        //其中某个没有填写
        var case1 = (object["school"] == "" || object["school"] == null);
        var case2 = (object["faculty"] == "" || object["faculty"] == null);
        var case3 = (object["startime"] == "" || object["startime"] == null);
        var case4 = (object["sex"] == "" || object["sex"] == null);
        //任何一个没填
        var case5 = (case1 || case2 || case3 || case4);
        
        if( case1 && ($('#addschoolok').css('display') == 'none')){
            layer.msg('请填写学校');
        }
        if( case2 ){
            layer.msg('请填写院系');
        }
        if( case3 ){
            layer.msg('请填写入学年份');
        }
        if( case4 ){
            layer.msg('请选择性别');
        }
        // else{
        if(!case5 || ($('#addschoolok').css('display') !== 'none')){


        if($('#stuidcard').css('display') !== 'none' && $('#stuidcardsuccess').css('display') === 'none'){
            //说明还没有上传学生证，不允许提交
            layer.msg('请上传学生证');
        }else{
            //如果图标显示了，就重新设置school的值为暂不设置，即隐藏域的值
            if($('#addschoolok').css('display') !== 'none'){
                object["school"] = $("#finalschoolname").val();
            }
            var jsonData = JSON.stringify(object);
            $.ajax({
                url: basePath + "/user/updateinfo",
                data: jsonData,
                contentType: "application/json;charset=UTF-8",
                type: "post",
                dataType: "json",
                beforeSend: function () {
                    layer.load(1, { //icon支持传入0-2
                        content: '完善中...',
                        success: function (layero) {
                            layero.find('.layui-layer-content').css({
                                'padding-top': '39px',
                                'width': '60px'
                            });
                        }
                    });
                },
                complete: function () {
                    layer.closeAll('loading');
                },
                success: function (data) {
                    if (data.status == 200) {
                        layer.msg(data.message, {
                            time: 1000,
                            icon: 1,
                            offset: '100px'
                        }, function () {
                            var mylay = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(mylay);
                        });
                    } else {
                        layer.msg(data.message, {
                            time: 1000,
                            icon: 2,
                            offset: '100px'
                        });
                    }
                },error:function () {
                    layer.msg('系统异常');
                }
            });
            //ajax end
        }

    }
    
        //else end
        return false;
    });
    //form.on
});


//点击“没有贵校？在这里填写提交申请”后弹窗（参考评分）
layui.use(['layer','jquery'], function () {
    $('#wantnewschool').on('click', function () {
        layer.open({
            title:'提交学校申请',
            //layer提供了5种层类型。可传入的值有：0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
            type: 1,
            skin: 'layui-layer-rim', //加上边框
            // content: "user/order/scorecontent.html",
            content: '<html>\n' +
                '<body>\n' +
                '<div class="layui-form-item" style="margin-top: 20px;">\n' +
                '    <label class="layui-form-label">学校名称</label>\n' +
                '    <div class="layui-input-inline">\n' +
                '        <input type="text" id="schoolname" name="schoolname" value="" lay-verify="schoolname" autocomplete="off"\n' +
                '            placeholder="请输入学校名称" class="layui-input">\n' +
                '    </div>\n' +
                '    <label class="layui-form-label" style="margin-top: 5px;">学校代号</label>\n' +
                '    <div class="layui-input-inline" style="margin-top: 5px;">\n' +
                '        <input type="text" id="schoolcode" name="schoolcode" value="" lay-verify="schoolcode" autocomplete="off"\n' +
                '        placeholder="请输入学校代号" class="layui-input">\n' +
                '    </div>\n' +
                '    <div style="text-align: center;">\n' +
                '        <button type="button" class="layui-btn" id="submitSchool" style="margin-top: 10px;">提交</button>\n' +
                '    </div>\n' +
                '</div>\n' +
                '</body>\n' +
                '</html>',
            area: ['350px', '220px'],
            // success:层弹出后的成功回调方法======弹出层加载成功后执行的函数
            success:function(layero, index) {
                var $ = layui.jquery;

                // 点击按钮获取填写的学校
                $("#submitSchool").click(function () {
                    var schoolName = $('#schoolname').val();
                    var schoolCode = $('#schoolcode').val();
                    // alert(schoolCode);
                    // alert(schoolName);
                    var c1 = (schoolName == null || schoolName == '');
                    var c2 = (schoolCode == null || schoolCode == '');
                    //都没写
                    var c3 = (c1 && c2);
                    if (c1) {
                        layer.msg("请填写你要申请新增的学校名称", {
                            time: 1000,
                            icon: 2,
                        });
                    } if (c2) {
                        layer.msg("请填写你要申请新增的学校代号", {
                            time: 1000,
                            icon: 2,
                        });
                    }

                    if (!c3) {
                        layer.confirm('确认提交吗？', {
                            btn: ['确定', '算了'], //按钮
                            title: "提交学校",
                            //不要加上offset就直接居中显示
                            // offset:"250px"//50px
                        }, function () {
                            layer.closeAll();
                            //设置隐藏域的值(如果点击确定按钮就设置)
                            $("#finalschoolname").val(schoolName);

                            $.ajax({
                                url: basePath + '/addschool/' + schoolName +'/'+ schoolCode,
                                data: "",
                                contentType: "application/json;charset=UTF-8", //发送数据的格式
                                type: "put",
                                dataType: "json", //回调
                                beforeSend: function () {
                                    //应该只是一个loading图标
                                    layer.load(1, { //icon支持传入0-2
                                        content: '请稍等...',
                                        success: function (layero) {
                                            layero.find('.layui-layer-content').css({
                                                'padding-top': '39px',
                                                'width': '60px'
                                            });
                                        }
                                    });
                                },
                                complete: function () {
                                    layer.closeAll('loading');
                                },
                                success: function (data) {
                                    console.log(data)
                                    if (data.status === 200) {
                                        layer.msg(data.message, {
                                            time: 1000,
                                            icon: 1,
                                            offset: '50px'
                                        }, function () {
                                            //reload会刷新页面。清空之前的内容
                                            // location.reload();

                                            //禁用select
                                            // $("#schoolselectoptions").css("pointer-events","none");
                                            //注意如果是true，也要加双引号！
                                            // $("#schoolselectoptions").attr("disabled","disabled");
                                            //设置select的值
                                            // $("#schoolselectoptions").prop('value' , '暂不填写');
                                            // 设置value为pxx的项选中（注意不是text）
                                            // $("#schoolselectoptions").val("暂不填写");
                                            // 设置text为pxx的项选中
                                            // $("#schoolselectoptions").find("option:contains('暂不填写')").attr("selected",true);

                                            //移除select元素
                                            // $("#schoolselectoptions").remove();
                                            //老是不行，以前也总是和select过不去。直接hide掉div得了
                                            $("#selectSchool").hide();
                                            //显示ok图标(根据他来判断用户有没有提交请求)
                                            $("#addschoolok").show();
                                            //隐藏超链接
                                            $("#wantnewschool").hide();
                                            //显示提示文字
                                            $("#addschooltips").show();
                                            //设置隐藏域的值(现在在前面设置，如果点击确定按钮就设置)
                                            // $("#finalschoolname").val("暂不填写");
                                        });
                                    }
                                    else if(data.status === 206){
                                        //直接hide掉div
                                        $("#selectSchool").hide();
                                        //显示ok图标(根据他来判断用户有没有提交请求)
                                        $("#addschoolok").show();
                                        //隐藏超链接
                                        $("#wantnewschool").hide();
                                        //显示提示文字2"已经有同学提交过申请啦，无需重复提交"
                                        $("#addschooltips2").show();

                                        layer.msg(data.message, {
                                            time: 1000,
                                            icon: 2,
                                            offset: '50px'
                                        });
                                    }
                                    //(现在不包括206了)else包含数据库操作失败和206（数据库中已经有的情况）
                                    else {
                                        layer.msg(data.message, {
                                            time: 1000,
                                            icon: 2,
                                            offset: '50px'
                                        });
                                    }
                                }
                            });
                        }, function () {
                        });
                        //confirm end
                    }
                    //if !c3
                    else{
                        layer.msg("请填写学校名称和学校代号", {
                            time: 1000,
                            icon: 2,
                            // offset: '50px'
                        });
                    }

                });
                //click end
            },
            //succes end

            //yes:确定按钮回调方法
            yes: function(index, layero){
                //do something
                layer.close(index); //如果设定了yes回调，需进行手工关闭
            }

        });



    });
});
