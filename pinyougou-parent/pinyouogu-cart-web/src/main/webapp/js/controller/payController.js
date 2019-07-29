var app = new Vue({
    el: "#app",
    data: {
        totalMoney: 0,
        out_trade_no: '',
        userId:''
    },
    methods: {
        //页面加载的时候发送请求 获取code_url 生成二维码 展示  展示金额 和 支付订单号
        createNative: function () {
            axios.get('/pay/createNative.shtml').then(
                function (response) {
                    if (response.data) {
                        //有数据
                        app.totalMoney = response.data.total_fee / 100;
                        app.out_trade_no = response.data.out_trade_no;
                        console.log(app.out_trade_no);
                        console.log(app.totalMoney);

                        var qrious = new QRious({
                            element: document.getElementById("qrious"),
                            level: "H",
                            size: 250,
                            value: response.data.code_url
                        })

                        app.queryPayStatus(response.data.out_trade_no);
                    } else {
                        //没有数据
                    }
                }
            )
        },
        queryPayStatus: function (out_trade_no) {

            axios.get('/pay/queryStatus.shtml?out_trade_no=' + out_trade_no).then(
                function (response) {
                    if (response.data.success) {
                        //支付成功
                        window.location.href = "paysuccess.html?money=" + app.totalMoney;
                    } else {
                        if (response.data.message == '超时') {
                           /* //重新生成二维码
                            app.createNative();*/

                            window.location.href="payfail.html";

                        }else {
                            window.location.href="payfail.html";
                        }
                    }
                })
        },
        getName:function () {
            axios.get('/pay/getName.shtml').then(function (response) {

                    app.userId = response.data;

            })
        }
    },


    created: function () {
        //页面一加载就应当调用
        if(window.location.href.indexOf("pay.html")!=-1){
            this.createNative();
            this.getName();
        }else {
            let urlParamObject = this.getUrlParam();
            if(urlParamObject.money)
                this.totalMoney=urlParamObject.money;
        }

    }


});