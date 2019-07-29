var app = new Vue({
    el: "#app",
    data: {
        totalMoney: 0,
        out_trade_no: '',
        userId: '',
        messageInfo:''
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

                        var qrious = new QRious({
                            element: document.getElementById("qrious"),
                            level: "H",
                            size: 250,
                            value: response.data.code_url
                        })

                        if (qrious) {
                            app.queryPayStatus(response.data.out_trade_no);
                        }
                    } else {
                        //没有数据
                    }
                }
            )
        },
        /*queryPayStatus: function (out_trade_no) {

            axios.get('/pay/queryStatus.shtml?out_trade_no=' + out_trade_no).then(
                function (response) {
                    if (response.data.success) {
                        //支付成功
                        window.location.href = "paysuccess.html?money=" + app.totalMoney;
                    } else {
                        if (response.data.message == '超时') {
                            alert("支付超时订单已关闭!")
                            window.location.href="payfail.html";

                        }else {
                            window.location.href="payfail.html";
                        }
                    }
                })
        },*/


        queryPayStatus: function (out_trade_no) {

            var count = 0;
            var time = window.setInterval(function () {
                count += 1;
                console.log(count);
                if (count == 31) {
                    window.clearInterval(time);
                    axios.get('/pay/closePay.shtml?out_trade_no=' + out_trade_no).then(
                        function (response) {
                            if (response.data.success) {
                                //订单关闭
                                app.messageInfo = "订单超时,二维码已过期。"
                            }else {
                                if (response.data.message == '支付成功!') {
                                    //支付成功
                                    window.location.href = "paysuccess.html?money=" + app.totalMoney;
                                }
                            }
                        })


                } else {
                    axios.get('/pay/queryStatus.shtml?out_trade_no=' + out_trade_no).then(
                        function (response) {
                            if (response.data.success) {
                                //支付成功
                                window.location.href = "paysuccess.html?money=" + app.totalMoney;
                            }else {
                                console.log(response.data.message);
                            }

                        })
                }

            }, 2000);


        },


        getName: function () {
            axios.get('/pay/getName.shtml').then(function (response) {

                app.userId = response.data;

            })
        }
    },


    created: function () {
        //页面一加载就应当调用
        if (window.location.href.indexOf("pay.html") != -1) {
            this.createNative();
            this.getName();
        } else {
            let urlParamObject = this.getUrlParam();
            if (urlParamObject.money)
                this.totalMoney = urlParamObject.money;
        }

    }


});