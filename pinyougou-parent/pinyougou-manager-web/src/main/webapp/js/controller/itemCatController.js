var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{parentId:''},
        entity_1:{},
        entity_2:{},
        grade:1,//默认第一级
        parentId:0,
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
                url: '/itemCat/uploadItemCat.shtml',
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
            window.location.href="../template/itemCat_template.xlsx";
        },

        searchList:function (curPage) {
            axios.post('/itemCat/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/itemCat/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/itemCat/findPage.shtml',{params:{
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
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/itemCat/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.findByparentId(app.entity.parentId);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/itemCat/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.findByparentId(app.entity.parentId);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id != null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/itemCat/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/itemCat/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.findByparentId(app.entity.parentId);
                    app.ids = [];
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findByparentId:function (parentId) {
            axios.get('/itemCat/findByParentId/'+parentId+'.shtml')
                .then(
                    function (response) {
                        app.list = response.data;
                        //记录ID
                        app.entity.parentId = parentId;
                    }
                )
                .catch(function (error) {
                    console.log(error.message)
                })
        },
        selectList:function (p_entity) {
            //如果当前等级是1
            if (this.grade == 1) {
                this.entity_1 = {};
                this.entity_2 = {};
            }
            //如果当前等级是2
            if (this.grade == 2) {
                this.entity_1 = p_entity;
                this.entity_2 = {};
            }
            //如果当前等级是3
            if (this.grade == 3) {
                this.entity_2 = p_entity;
            }
            this.findByparentId(p_entity.id)

        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.findByparentId(0);

    }

})
