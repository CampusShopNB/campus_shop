layui.use(['form', 'element', 'util', 'carousel', 'laypage', 'layer','table'], function () {
    var table = layui.table;
    table.render({
        elem: '#adminsold'
        , url: basePath+'/admin/queryallorder'
        , page: {
            layout: ['limit', 'count', 'prev', 'page', 'next', 'skip']
            , groups: 3
            , limits: [20, 50, 100]
            , limit: 20
        }, cols: [[
            {field: 'qid', title: 'ID',width:50, align:'center'}
            , {field: 'id', title: '订单号',width:180, align:'center'}
            , {field: 'commname', title: '名称', width: 174, align:'center'}
            , {field: 'commdesc', title: '描述', width: 290, align:'center'}
            , {field: 'thinkmoney', title: '售价', width: 80, align:'center'}
            , {field: 'buyername', title: '买家', width: 80, align:'center'}
            , {field: 'sellername', title: '卖家', width: 80, align:'center'}
            , {field: 'orderstatus', title: '订单状态', toolbar: '#orderStatusDemo', width: 90, align:'center'}
            , {field: 'soldtime', title: '售出时间', width: 160,sort: true, align:'center'}
            , {fixed: 'right', title: '操作', toolbar: '#barDemo', width:100, align:'center'}
        ]]
        ,height: 500
        , done: function (res, curr, count) {
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
        }
    });
});