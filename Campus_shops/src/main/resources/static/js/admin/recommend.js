layui.use(['form', 'element', 'util', 'carousel', 'laypage', 'layer', 'table'], function () {
    var element = layui.element;
    var util = layui.util;
    var form = layui.form;
    var laypage = layui.laypage
        , layer = layui.layer;
    form.on('select(types)', function (data) {
        var indexGID = data.elem.selectedIndex;
        lookallrecommend(data.elem[indexGID].title);
    });
});
function lookallrecommend(recomstatus) {
    layui.use(['form', 'element', 'util', 'carousel', 'laypage', 'layer','table'], function () {
        var table = layui.table;
        table.render({
            elem: '#recommend'
            , url: basePath+'/admin/recommend/'+recomstatus
            , page: {
                layout: ['limit', 'count', 'prev', 'page', 'next', 'skip']
                , groups: 3
                , limits: [20, 50, 100]
                , limit: 20
            }, cols: [[
                {field: 'qid', title: 'ID',width:80, align:'center'}
                , {field: 'commid', title: '商品id', width: 150, align:'center'}
                , {field: 'recomname', title: '推荐名称', width: 150, align:'center'}
                , {field: 'recomimg', title: '推荐图片', width: 150, align:'center'}
                , {field: 'recomdesc', title: '推荐描述', width: 200, align:'center'}
                , {field: 'updatetime', title: '时间', width: 200,sort: true, align:'center'}
                , {field: 'recomstatus', title: '商品推荐状态',  toolbar: '#recommendStatusDemo', width: 150, align:'center'}
                , {fixed: 'right', title: '操作', toolbar: '#barDemo', width:200, align:'center'}
            ]], done: function (res, curr, count) {
                var i=1;
                $("[data-field='qid']").children().each(function () {
                    if($(this).text() == 'ID') {
                        $(this).text("ID")
                    }else{
                        $(this).text(i++)
                    }
                });
            }
        });
        //监听行工具事件
        table.on('tool(test)', function (obj) {
            var data = obj.data;
            if (obj.event === 'xiangqing') {
                window.open(basePath+"/product-detail/"+data.commid)
            }else if(obj.event === 'weigui'){
                layer.confirm('确认审核不通过吗？', {
                    btn: ['确定','算了'], //按钮
                    title:"推荐商品审核不通过",
                    offset:"50px"
                }, function(){
                    layer.closeAll();
                    $.ajax({
                        url: basePath+'/admin/changerecomstatus/'+data.commid+"/2",
                        data: "",
                        contentType: "application/json;charset=UTF-8", //发送数据的格式
                        type: "put",
                        dataType: "json", //回调
                        beforeSend: function () {
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
            }else if (obj.event === 'shenhe') {
                layer.confirm('确认审核通过吗？', {
                    btn: ['确定','算了'], //按钮
                    title:"推荐商品审核通过",
                    offset:"50px"
                }, function(){
                    layer.closeAll();
                    $.ajax({
                        url: basePath+'/admin/changerecomstatus/'+data.commid+"/1",
                        data: "",
                        contentType: "application/json;charset=UTF-8", //发送数据的格式
                        type: "put",
                        dataType: "json", //回调
                        beforeSend: function () {
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
                                    // offset: '50px'
                                }, function () {
                                    location.reload();
                                });
                            }else {
                                layer.msg(data.message, {
                                    time: 1000,
                                    icon: 2,
                                    // offset: '50px'
                                });
                            }
                        }
                    });
                }, function(){
                });
            }
        });
    });
}
lookallrecommend(100);