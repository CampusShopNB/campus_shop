layui.use(['form'], function () {
    var form = layui.form
        , layer = layui.layer;
    var $ = layui.$
});

var productList = new Vue({
    el: '#wantgoodslist',
    data() {
        return {
            wantgoodslistData: []
        }
    },
    mounted: function () {
        this.gouzaopage();
        window.gouzaopage = this.gouzaopage;
        window.lookwantgoodslistData = this.lookwantgoodslistData;
    },
    methods: {
        //创建layui的分页
        gouzaopage:function () {
            $.ajax({
                url: basePath + "/indexwantgoodslist/showallandpage",
                data: "",
                contentType: "application/json;charset=UTF-8", //发送数据的格式
                type: "get",
                dataType: "json", //回调
                success: function (data) {
                    layui.use(['laypage', 'layer'], function () {
                        var laypage = layui.laypage
                            , layer = layui.layer;
                        //完整功能
                        //dataNumber应该是后台方法showWantGoodsNum传过来的
                        laypage.render({
                            elem: 'layuipage'
                            , count: data.dataNumber
                            ,limit: 16
                            ,limits: [16, 32, 48]
                            , layout: ['count', 'prev', 'page', 'next']
                            , jump: function (obj) {
                                //共N条的背景色，与页面背景色保持一致
                                $(".layui-laypage-count").css("background-color","#F3EBE9");
                                lookwantgoodslistData(obj.curr);
                            }
                        });
                    });
                },error:function () {
                    layer.msg("系统错误，分页显示失败", {
                        time: 1000,
                        icon: 2,
                        offset: '100px'
                    });
                }
            });
        },//前台分页查询
        lookwantgoodslistData:function (page) {
            var that=this;
            $.ajax({
                //1表示审核通过
                url: basePath + "/indexwantgoodslist/"+page+"/"+1,
                data: "",
                contentType: "application/json;charset=UTF-8", //发送数据的格式
                type: "get",
                dataType: "json", //回调
                beforeSend: function () {
                    layer.load(1, { //icon支持传入0-2
                        content: '查询中...',
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
                    that.wantgoodslistData=data.data;
                },error:function () {
                    layer.msg("系统错误，数据查询失败", {
                        time: 1000,
                        icon: 2,
                        offset: '100px'
                    });
                }
            });
        }
    }
})