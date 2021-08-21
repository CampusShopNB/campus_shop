function createOrder() {
    var object = new Object();
    object["commid"] = goodid;   //这里这里这里
    object["receiverName"] = $("#receiverName").text();
    object["receiverTel"] = $("#receiverTel").text();
    object["address"] = $("#address").text();
    var jsonData = JSON.stringify(object);
    $.ajax({
        url: "/createOrder",
        data: jsonData,
        contentType: "application/json;charset=UTF-8", //发送数据的格式
        type: "post",
        dataType: "json", //回调
        beforeSend: function () {
            layer.load(1, { //icon支持传入0-2
                content: '提交中...',
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
            layer.msg("success");
        },error:function () {
            layer.msg("系统错误", {
                time: 1000,
                icon: 2,
                offset: '100px'
            });
        }
    });
}
