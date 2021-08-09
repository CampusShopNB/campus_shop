layui.use(['form', 'element', 'util', 'carousel', 'laypage', 'layer', 'table'], function () {
    var element = layui.element;
    var util = layui.util;
    var form = layui.form;
    var laypage = layui.laypage
        , layer = layui.layer;
    form.on('select(types)', function (data) {
        var indexGID = data.elem.selectedIndex;
        lookallschool(data.elem[indexGID].title);
    });
});
function lookallschool() {
    layui.use(['form', 'element', 'util', 'carousel', 'laypage', 'layer','table'], function () {
        var table = layui.table;
        table.render({
            elem: '#schooltable'
            , url: basePath+'/admin/schoollist'
            , page: {
                layout: ['limit', 'count', 'prev', 'page', 'next', 'skip']
                , groups: 3
                , limits: [20, 50, 100]
                , limit: 20
            }, cols: [[
                {field: 'id', title: 'ID',width:240, align:'center'}
                , {field: 'schoolname', title: '学校名称', width: 300, align:'center'}
                , {field: 'userid', title: '用户id', width: 400, align:'center'}
                , {fixed: 'right', title: '操作', toolbar: '#barDemo', width:350, align:'center'}
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
            if (obj.event === 'shenhe') {
                layer.confirm('确认通过该申请吗？', {
                    btn: ['确定','算了'], //按钮
                    title:"审核学校",
                    offset:"50px"
                }, function(){
                    layer.closeAll();
                    $.ajax({
                        url: basePath+'/admin/updateschoolstatus/'+data.id,
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
//上面只是定义，这里要调用
lookallschool();