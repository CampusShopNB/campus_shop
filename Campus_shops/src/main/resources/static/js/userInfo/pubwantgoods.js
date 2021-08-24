var app = new Vue({
    el: '#goodsform',
});

let player = null;
layui.use(['form', 'element'], function () {
    var form=layui.form;
    form.on('submit(demo1)', function (data) {
        $("#demo1").addClass("layui-btn-disabled");
        $("#demo1").attr("disabled", true);
        if(data.field.wanttitle.length>200){
            layer.msg('求购标题过长', {
                time: 1000,
                icon: 2,
                offset: '150px'
            });
            return false;
        }
        var object = new Object();
        object["wanttitle"] = data.field.wanttitle;
        object["wantcontent"] = data.field.wantcontent;
        object["wantcategory"] = data.field.wantcategory;
        object["expectprice"] = data.field.expectprice;
        object["category"] = data.field.category;
        object["expectplace"] = data.field.expectplace;
        var jsonData = JSON.stringify(object);
        $.ajax({
            url: basePath + "/wantgoods/pub",
            data: jsonData,
            contentType: "application/json;charset=UTF-8",
            type: "post",
            dataType: "json",
            beforeSend: function () {
                layer.load(1, {
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
                layer.msg("提交成功，请等待审核", {
                    time: 1000,
                    icon: 1,
                    offset: '100px'
                }, function () {
                    window.location.reload();
                });
            },error:function () {
                layer.msg('提交失败', {
                    time: 1000,
                    icon: 2,
                    offset: '150px'
                });
            }
        });
        return false;
    });
});