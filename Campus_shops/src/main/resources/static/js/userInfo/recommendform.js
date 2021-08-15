layui.config({
    base: '../../../layuiadmin/' //静态资源所在路径
}).extend({
    index: 'lib/index' //主入口模块
}).use(['index', 'set']);


layui.use(['form', 'element', 'util', 'carousel', 'form', 'laypage', 'layer','table','upload'], function () {
    var element = layui.element;
    var util = layui.util;
    var carousel = layui.carousel;
    var form = layui.form;
    var laypage = layui.laypage
        , layer = layui.layer
        , upload=layui.upload;
    //上传商品图片
    // upload.render({
    //     elem: '#recomimg'
    //     ,url: basePath+'/recommend/updaterecomimg'
    //     ,before: function(obj){
    //         layer.load(1, { //icon支持传入0-2
    //             content: '上传中',
    //             success: function (layero) {
    //                 layero.find('.layui-layer-content').css({
    //                     'padding-top': '39px',
    //                     'width': '60px'
    //                 });
    //             }
    //         });
    //     },done: function(res){
    //         layer.closeAll('loading');
    //         //将res中的图片路径取出来，存放到隐藏域中
    //         $("#recomimgvalue").val(''+res['recomimg']);
    //
    //         layer.msg('上传成功', {
    //             time: 1000,
    //             icon: 1,
    //             offset: '150px'
    //         }, function () {
    //             location.reload();
    //         });
    //     },error: function(){
    //         layer.msg('上传失败');
    //     }
    // });

    //上传推荐商品图片
    upload.render({
        elem: '#recomimg'
        ,url: basePath+'/recommend/updaterecomimg'
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
            //将后台生成的图片路径存在隐藏域中
            $("#recomimgvalue").val(''+res['recomimg']);
            // alert($("#recomimgvalue").val());

            layer.closeAll('loading');
            layer.msg('上传成功', {
                time: 1000,
                icon: 1,
                // offset: '150px'
            }, function () {
                
                //隐藏上传按钮
                $("#recomimg").hide();
                //显示成功的图标
                $("#recomimgsuccess").show();
                //显示成功的文本提示
                $("#addrecomimgtips").show();
            });
        },error: function(){
            layer.msg('上传失败');
        }
    });

    form.on('submit(demo1)', function(data){
        var object = new Object();
        object["commid"] = data.field.commid;
        object["recomname"] = data.field.recomname;
        if($("#recomimgvalue").val()!=null || $("#recomimgvalue").val()!=""){
            object["recomimg"] = $("#recomimgvalue").val();
        }else{
            object["recomimg"] = null;
        }


        
        // object["recomimg"] = $("#recomimg").attr("src");
        // object["recomimg"] = data.field.recomimg;
        object["recomdesc"] = data.field.recomdesc;

        //其中某个没有填写
        var case1 = (object["recomname"] == "" || object["recomname"] == null);
        var case2 = (object["recomimg"] == "" || object["recomimg"] == null);
        var case3 = (object["recomdesc"] == "" || object["recomdesc"] == null);
        //任何一个没填
        var case4 = (case1 || case2 || case3);

        if( case1 ){
            layer.msg('请填写商品推荐名称');
        }
        if( case2 ){
            layer.msg('请上传商品推荐图片');
        }
        if( case3 ){
            layer.msg('请填写商品推荐描述');
        }
        // else{
         if(case4){
             layer.msg('请填写完整');
         }else {
            var jsonData = JSON.stringify(object);
            $.ajax({
                url: basePath + "/recommend/updaterecommendinfo",
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
                }, error: function () {
                    layer.msg('系统异常');
                }

            });
        }
         //}
         
                //ajax end
        return false;
    });
    //form.on
});
