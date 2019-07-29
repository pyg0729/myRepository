var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        totalMoney:0,//总金额
        totalNum:0,//总数量
        cartList: [],
        order:{paymentType:'1'},
        entity: {},
        address:{},
        userName:{},
        addressList:[],
        ids: [],
        searchEntity: {}
    },
    methods:{
        //查询所有的购物车的列表数据
        findCartList:function () {
            axios.get('/cart/findCartList.shtml').then(
                function (response) {
                    app.cartList=response.data;//List<Cart>   cart { List<ORDERiMTE> }
                    app.totalMoney=0;
                    app.totalNum=0;
                    for(var i=0;i<response.data.length;i++){
                        var obj = response.data[i];//Cart
                        for(var n=0;n<obj.orderItemList.length;n++){
                            var objx = obj.orderItemList[n];//ORDERiMTE
                            app.totalMoney+=objx.totalFee;
                            app.totalNum+=objx.num;
                        }
                    }

                }
            )
        },
        //向已有的购物车中添加商品
        addGoodsToCartList:function (itemId,num) {
            axios.get('/cart/addGoodsToCartList.shtml?itemId='+itemId+'&num='+num).then(
                function (response) {
                    if(response.data.success){
                        //
                        app.findCartList();
                    }
                }
            )
        },

        selectAddress:function(address) {
          this.address = address;
        },

        isSelectedAddress:function(address) {
            if (address == this.address) {
                return true;
            }
            return false;


        },

        findAddressList:function () {
            axios.get('/address/findAddressListByUserId.shtml').then(
                function (response) {
                    app.addressList = response.data;

                    for (var i = 0; i < app.addressList.length; i++) {
                        if (app.address = app.addressList[i].isDefault == '1') {
                            app.address = app.addressList[i];
                            app.userName = address.userId;
                            app.$set(userName,'username',address.userId);
                            break;
                        }
                    }
                }
            )},
        selectType:function (type) {
            this.$set(this.order,"paymentType",type);
            console.log(this.order);
        },
        //方法 当点击提交订单的时候调用
        submitOrder:function () {
            //先获取地址的信息 赋值给变量order
            this.order.receiverAreaName = this.address.address;//详细地址
            this.order.receiverMobile = this.address.mobile;//电话
            this.order.receiver = this.address.contact;//联系人

            axios.post('/order/add.shtml',this.order).then(
                function (response) {
                    if (response.data.success) {
                        //跳转到支付的页面
                        window.location.href = "/pay.html";
                    }
                }
            )}

    },






    created:function () {
        this.findCartList();
        
        var href = window.location.href;
        
        if (href.indexOf("getOrderInfo.html") != -1) {
            this.findAddressList();
        }
    }
});