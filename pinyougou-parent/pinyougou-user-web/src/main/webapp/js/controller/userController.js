var app = new Vue({
    el: "#app",
    data: {
        entity:{},
        smsCode:'',
        name:''

    },
    methods: {
        //注册
        formSubmit:function () {
            var that=this;
            this.$validator.validate().then(
                function (result) {
                    if(result){
                        console.log(that);
                        axios.post('/user/add/'+that.smsCode+'.shtml?',that.entity).then(function (response) {
                            if(response.data.success){
                                //跳转到其用户后台的首页
                                window.location.href="home-index.html";
                            }else{
                                that.$validator.errors.add(response.data.errorsList);
                            }
                        }).catch(function (error) {
                            console.log("1231312131321");
                        });
                    }
                }
            )
        },
        createSmsCode:function () {
            axios.get('/user/sendCode/'+this.entity.phone+'.shtml?').then(
                function (response) {
                    if (response.data.success) {
                        alert(response.data.message);
                    }else {
                        alert(response.data.message);
                    }
                }
            )
        },
        getName:function () {
            axios.get('/login/name.shtml').then(function (response) {
                app.name = response.data;
            }).catch(function (error) {
                console.log(error.data);
            })
        }



    },
    //钩子函数 初始化了事件和
    created: function () {

        this.getName();

    }

});
