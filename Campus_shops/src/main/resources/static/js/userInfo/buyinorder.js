layui.use(['form', 'element', 'util', 'carousel', 'laypage', 'layer','table','jquery','rate'], function () {
    var table = layui.table;
    //elem:sold是table的id，elem应该是查找元素
    // 下面page的limits: [20, 50, 100]应该就是分页的。
    table.render({
        elem: '#buyinorder'
        , url: basePath+'/buyinorder/querybuyinorder'
        , page: {
            layout: ['limit', 'count', 'prev', 'page', 'next', 'skip']
            , groups: 3
            , limits: [20, 50, 100]
            , limit: 20
        }, cols: [[
            {field: 'id', title: '订单编号',width:180, align:'center'}
            , {field: 'commname', title: '商品名称', width: 150, align:'center'}
            , {field: 'commdesc', title: '商品描述', width: 200, align:'center'}
            , {field: 'thinkmoney', title: '售价', width: 80, align:'center'}
            , {field: 'sellerid', title: '卖家id', width: 100, align:'center'}
            , {field: 'sellername', title: '卖家', width: 80, align:'center'}
            , {field: 'orderstatus', title: '订单状态', toolbar: '#orderStatusDemo', width: 100, align:'center'}
            , {field: 'soldtime', title: '售出时间', width: 180, sort: true, align:'center'}
            , {fixed: 'right', title: '操作', toolbar: '#barDemo', width:220, align:'center'}
        ]]
        ,height: 500
    });
    //上面的barDemo是引用html中模板引擎的id
    //每一列的数值，这里写在field，应该是数据表字段？


    //监听行工具事件
    table.on('tool(test)', function (obj) {
        var data = obj.data;
        // console.log(data); //data就是soldrecord数据表记录所有字段
        /*
        buyerid: "11102184795951594874"
        buyername: "aa"
        commdesc: "Nintendo任天堂switch游戏机NS主机家用体感游戏机健身环大冒险套装国行续航增强版主机插卡手柄游戏掌机马力欧电视体感运动跳舞"
        commid: "1627811152227575468"
        commname: "【99新】任天堂Nintendo Switch"
        id: "2659843115164485212"
        orderstatus: 4
        sellerid: "12309884795951575474"
        sellername: "bb"
        soldtime: "2021-08-01 11:25:42"
        startobuyer: 0
        startoseller: 0
        thinkmoney: 1399        */
        //详情，这里的xiangqing和shanchujilu见html中的lay-event
        if (obj.event === 'xiangqing') {
            window.open(basePath+"/product-detail/"+data.commid)
        }else if (obj.event === 'score') {
            // window.open(basePath+"/toscoreview/"+data.id)
            layer.open({
                title:'评分',
                //layer提供了5种层类型。可传入的值有：0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
                type: 1,
                skin: 'layui-layer-rim', //加上边框
                // content: "user/order/scorecontent.html",
                content: '<html>\n' +
                    '    <body>\n' +
                    '        <div align="center">\n' +
                    '            <div id="rate"></div><br/>\n' +
                    '            <button type="button" class="layui-btn" id="getRate">提交</button>\n' +
                    '        </div>\n' +
                    '    </body>\n' +
                    '</html>',
                area: ['350px', '140px'], //宽350高360
                // success:层弹出后的成功回调方法======弹出层加载成功后执行的函数
                success:function(layero, index) {
                    var $ = layui.jquery;
                    var rate = layui.rate;
                    // 渲染评分组件
                    var instance = rate.render({
                        elem: '#rate',
                        length: 5,
                        theme: '#FFB800',
                        //'#1E9FFF',
                        choose: function (value) {
                            // 此处点击分数之后可用Ajax传递到后台并保存到数据库 具体略
                            // 评分完成之后重载实例
                            rate.render({
                                elem: '#rate',
                                length: 5,
                                theme: '#FFB800',
                                //'#1E9FFF',
                                // value值也可以是前台通过Ajax获取到的数据库中的值
                                value: value,
                                // 设置为只读防止多次评分
                                readonly: true
                            })
                        }
                    });

                    // 点击按钮获取分数
                    $("#getRate").click(function () {
                        // console.log(instance);
                        // 打印出instance可知instance.config.value即是评分值

                        // layer.msg("获取分数："+instance.config.value);
                        // alert(instance.config.value);
                        var starvalue = instance.config.value;
                        // alert(starvalue);

                        layer.confirm('确认提交吗？', {
                            btn: ['确定','算了'], //按钮
                            title:"提交评分",
                            //不要加上offset就直接居中显示
                            // offset:"250px"//50px
                        }, function(){
                            layer.closeAll();
                            $.ajax({
                                url: basePath+'/updatesellerstar/'+data.id+'/'+starvalue+'/'+data.sellerid,
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
                                    if(data.status===200){
                                        layer.msg(data.message, {
                                            time: 1000,
                                            icon: 1,
                                            offset: '50px'
                                        }, function () {
                                            location.reload();
                                        });
                                    }else {
                                        layer.msg(data.message, {
                                            time: 1000,
                                            icon: 2,
                                            offset: '50px'
                                        });
                                    }
                                }
                            });
                        }, function(){
                        });


                    });

                },
                //yes:确定按钮回调方法
                yes: function(index, layero){
                    //do something
                    layer.close(index); //如果设定了yes回调，需进行手工关闭
                }

            });
        }
        else if(obj.event === 'shanchujilu' || obj.event === 'receive'){
            //删除和确认收货都是修改订单状态，一个改为7一个改为3
            layer.confirm('确认操作吗？', {
                btn: ['确定','算了'], //按钮
                title:"变更订单状态",
                offset:"50px"
            }, function(){
                layer.closeAll();
                $.ajax({
                    // url: basePath+'/buyinorder/update/'+data.id+'/'+obj.event,
                    url: basePath+'/updateorder/'+data.id+'/'+obj.event,
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
                        if(data.status===200){
                            layer.msg(data.message, {
                                time: 1000,
                                icon: 1,
                                offset: '50px'
                            }, function () {
                                location.reload();
                            });
                        }else {
                            layer.msg(data.message, {
                                time: 1000,
                                icon: 2,
                                offset: '50px'
                            });
                        }
                    }
                });
            }, function(){
            });
        }
    });
});
//
// function openScore() {
//     layui.use(['layer','jquery','rate'], function () {
//         var $ = layui.jquery;
//         var rate = layui.rate;
//         // 渲染评分组件
//         var instance = rate.render({
//             elem: '#rate',
//             length: 5,
//             theme: '#FFB800',
//             //'#1E9FFF',
//             choose: function (value) {
//                 // 此处点击分数之后可用Ajax传递到后台并保存到数据库 具体略
//                 // 评分完成之后重载实例
//                 rate.render({
//                     elem: '#rate',
//                     length: 5,
//                     theme: '#FFB800',
//                     //'#1E9FFF',
//                     // value值也可以是前台通过Ajax获取到的数据库中的值
//                     value: value,
//                     // 设置为只读防止多次评分
//                     readonly: true
//                 })
//             }
//         });
//         // 点击按钮获取分数
//         $("#getRate").click(function(){
//             console.log(instance);
//             // 打印出instance可知instance.config.value即是评分值
//             // layer.msg("获取分数："+instance.config.value);
//             alert(instance.config.value);
//         });
//     });
//
// }
