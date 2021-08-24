layui.use(['form', 'element', 'util', 'carousel', 'laypage', 'layer', 'table'], function () {
    var element = layui.element;
    var util = layui.util;
    var form = layui.form;
    var laypage = layui.laypage
        , layer = layui.layer;
    form.on('select(types)', function (data) {
        var indexGID = data.elem.selectedIndex;
        lookallwantgoods(data.elem[indexGID].title);
    });
});
function lookallwantgoods(wantstatus) {
    layui.use(['form', 'element', 'util', 'carousel', 'laypage', 'layer','table','jquery'], function () {
        var table = layui.table;
        table.render({
            //不能直接用后台的接口。。否则会查到别的用户的记录
            elem: '#wantgoodslist'
            , url: basePath+'/showmywglist/'+wantstatus
            , page: {
                layout: ['limit', 'count', 'prev', 'page', 'next', 'skip']
                , groups: 3
                , limits: [20, 50, 100]
                , limit: 20
            }, cols: [[
                //qid是done那里的
                {field: 'qid', title: 'ID',width:45, align:'center'}
                , {field: 'id', title: '项ID', width: 90, align:'center'}
                , {field: 'userid', title: '用户ID', width: 90, align:'center'}
                // , {field: 'username', title: '用户名', width: 300, align:'center'}
                , {field: 'wanttitle', title: '求购标题', width: 150, align:'center'}
                , {field: 'wantcontent', title: '求购内容', width: 150, align:'center'}
                , {field: 'wantcategory', title: '分类', width: 60, align:'center'}
                , {field: 'expectprice', title: '价格', width: 60, align:'center'}
                , {field: 'expectplace', title: '交易地点', width: 140, align:'center'}
                , {field: 'createtime', title: '发布时间', width: 130, align:'center'}
                , {field: 'updatetime', title: '修改时间', width: 130, align:'center'}
                , {field: 'wantstatus', title: '状态', toolbar: '#wantStatusDemo', width: 98, align:'center'}
                , {fixed: 'right', title: '操作', toolbar: '#barDemo', width:139, align:'center'}
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
            if(obj.event === 'shanchu'){
                layer.confirm('确认删除吗？', {
                    btn: ['确定','算了'], //按钮
                    title:"删除",
                    offset:"50px"
                }, function(){
                    layer.closeAll();
                    $.ajax({
                        //感觉可以直接用后台接口
                        url: basePath+'/changewantstatus/'+data.id+"/3",
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
            }

        });
    });
}
lookallwantgoods(100);