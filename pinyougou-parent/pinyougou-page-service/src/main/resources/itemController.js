var app = new Vue({
    el: "#app",
    data: {
        num:1,//商品的购买数量
        specificationItems:JSON.parse(JSON.stringify(skuList[0].spec)),//默认展示第一个数组元素对应的规格数据
        sku:skuList[0]//数组第一个元素就是sku的数据展示
    },
    methods:{
        selectSpecifcation:function(name,value){
            //设置值
            this.$set(this.specificationItems,name,value);
            //调用搜索匹配的方法
            this.search();
        },
        search:function(){
            for(var i=0;i<skuList.length;i++){
                var object = skuList[i];
                if(JSON.stringify(this.specificationItems)==JSON.stringify(skuList[i].spec)){
                    console.log(object);
                    this.sku=object;
                    break;
                }
            }
        }
    },

    //钩子函数 初始化了事件和
    created: function () {

    }

});