var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        timeString: '',
        seckillId: 0,
        count: '',
        messageInfo:'',
        searchEntity: {}
    },
    methods: {

        convertTimeString: function (alltime) {
            var allsecond = Math.floor(alltime / 1000);//毫秒数转成 秒数。
            var days = Math.floor(allsecond / (60 * 60 * 24));//天数
            var hours = Math.floor((allsecond - days * 60 * 60 * 24) / (60 * 60));//小数数
            var minutes = Math.floor((allsecond - days * 60 * 60 * 24 - hours * 60 * 60) / 60);//分钟数
            var seconds = allsecond - days * 60 * 60 * 24 - hours * 60 * 60 - minutes * 60; //秒数

            if (days > 0 && days < 10) {
                days = "0" + days + "天 ";
            }
            if (days > 10) {
                days = days + "天 ";
            }

            if (days == 0) {
                days = "";
            }
            if (hours < 10) {
                hours = "0" + hours;
            }
            if (minutes < 10) {
                minutes = "0" + minutes;
            }
            if (seconds < 10) {
                seconds = "0" + seconds;
            }
            return days + hours + ":" + minutes + ":" + seconds;
        },
        //倒计时
        caculate: function (alltime) {
            var clock = window.setInterval(function () {
                alltime = alltime - 1000;
                //反复被执行的函数
                app.timeString = app.convertTimeString(alltime);
                if (alltime <= 0) {
                    //取消
                    window.clearInterval(clock);
                }
            }, 1000);//相隔1000执行一次。
        },


        //方法当点击立即抢购的时候调用
        submitOrder: function () {
            console.log("下单的id的值为:" + this.seckillId);
            axios.get('/seckillOrder/submitOrder.shtml?id=' + this.seckillId).then(
                function (response) {//response.data=result
                    if (response.data.success) {
                        //提示 真正排队中
                        app.messageInfo=response.data.message;
                    } else {
                        if (response.data.message == '403') {
                            //要去登录
                            alert("要登录");
                            var url = window.location.href;//获取当前浏览器中的URL的地址
                            window.location.href = "http://localhost:9109/page/login.shtml?url=" + url;
                        } else {
                            alert(response.data.message);
                            app.messageInfo = response.data.message;
                        }
                    }
                }
            )
        },

        //点击立即抢购就是需要调用了
        queryStatus:function () {
            var count=10;
            let queryObject = window.setInterval(function () {
                count-=2;
                axios.get('/seckillOrder/queryOrderStatus.shtml').then(
                    function (response) {
                        console.log("正在查询.............状态值"+response.data.message);
                        if(response.data.success){
                            //去支付
                            window.location.href="pay/pay.html";
                        }else{
                            app.messageInfo=response.data.message+"......"+count;
                        }
                    }
                )
            },2000);
        },
        getGoodsById: function () {
            axios.get('/seckillGoods/getGoodsById.shtml?id=' + this.seckillId).then(
                function (response) {
                    app.caculate(response.data.time);//参数是毫秒数
                    app.count = response.data.count;//剩余库存
                }
            )
        }

    },


    created: function () {


        var urlParam = this.getUrlParam();
        this.seckillId = urlParam.id;
        this.getGoodsById();

    }
});