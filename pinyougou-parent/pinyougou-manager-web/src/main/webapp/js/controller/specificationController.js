var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{specification:{},optionList:[]},
        specStatus:['未审核','已审核','审核未通过','已删除'],
        ids:[],
        searchEntity:{}
    },
    methods: {

        //文件上传
        //1.模拟表单 设置数据
        //2.发送aajx请求 上传图片
        uploadFile: function () {

            //模拟创建一个表单对象
            var formData = new FormData();
            //参数formData.append('file' 中的file 为表单的参数名  必须和 后台的file一致
            //file.files[0]  中的file 指定的时候页面中的input="file"的id的值 files 指定的是选中的图片所在的文件对象数组，这里只有一个就选中[0]
            formData.append('file', file.files[0]);


            axios({
                url: '/specification/uploadSpecification.shtml',
                //data就是表单数据
                data: formData,
                method: 'post',
                //指定头信息：
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                /* //开启跨域请求携带相关认证信息
                 withCredentials: true*/
            }).then(function (response) {
                if (response.data.success) {
                    alert("success")
                } else {
                    alert(response.data.message);
                }
            })

        },

        //文件上传
        //1.模拟表单 设置数据
        //2.发送aajx请求 上传图片
        uploadOptionFile: function () {

            //模拟创建一个表单对象
            var formData = new FormData();
            //参数formData.append('file' 中的file 为表单的参数名  必须和 后台的file一致
            //file.files[0]  中的file 指定的时候页面中的input="file"的id的值 files 指定的是选中的图片所在的文件对象数组，这里只有一个就选中[0]
            formData.append('optionFile', optionFile.files[0]);


            axios({
                url: '/specificationOption/uploadSpecificationOption.shtml',
                //data就是表单数据
                data: formData,
                method: 'post',
                //指定头信息：
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                /* //开启跨域请求携带相关认证信息
                 withCredentials: true*/
            }).then(function (response) {
                if (response.data.success) {
                    alert("success")
                } else {
                    alert(response.data.message);
                }
            })

        },


        //上传成功提示
        handleSuccess(response, file) {
            if(response.flag){
                this.$message({
                    message: response.message,
                    type: 'success'
                });
            }else{
                this.$message.error(response.message);
            }
            console.log(response, file);
        },

        //上传之前进行文件格式校验
        beforeUpload(file){
            var isXLS = file.type === 'application/vnd.ms-excel';
            if(isXLS){
                return true;
            }
            var isXLSX = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
            if (isXLSX) {
                return true;
            }
            this.$message.error('上传文件只能是xls或者xlsx格式!');
            return false;
        },
        //下载模板文件
        downloadTemplate(){
            window.location.href="../template/specification_template.xlsx";
        },
        //下载模板文件
        downloadOptionTemplate(){
            window.location.href="../template/specificationOption_template .xlsx";
        },

        searchList:function (curPage) {
            axios.post('/specification/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/specification/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/specification/findPage.shtml',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        save:function () {
            if(this.entity.specification.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/specification/findOne/'+id+'.shtml').then(function (response) {
                app.entity = response.data;
                console.log(app.entity);
                console.log(1211111)
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/specification/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.ids = [];
                    app.searchList(app.pageNo);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        //审核商品规格
        updateStatus:function (status) {
            axios.get('/specification/updateStatus.shtml?',{
                params:{
                    Id:this.entity.specification.id,
                    status:status
                }
            }).then(
                function (response) {
                    if(response.data.success){
                        app.searchList(1);
                    }else{
                        alert(response.data.message);
                    }
                }
            )
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);

    }

})
