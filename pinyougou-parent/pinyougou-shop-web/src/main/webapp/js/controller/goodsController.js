var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        Dpage: 1,
        list: [],
        entity: {
            goods: {},
            goodsDesc: {itemImages: [], customAttributeItems: [], specificationItems: []},
            itemList: []
        },
        imageurl: '',
        image_entity: {url: '', color: ''},
        ids: [],
        customA: [],
        specList: [],//规格数据列表
        itemCat1List: [],//一级分类列表
        itemCat2List: [],//二级分类列表
        itemCat3List: [],//三级分类列表
        brandTextListL: [],//品牌列表
        searchEntity: {}
    },
    methods: {
        searchList: function (curPage) {
            axios.post('/goods/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/goods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/goods/findPage.shtml', {
                params: {
                    pageNo: this.pageNo
                }
            }).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data.list;
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add: function () {
            //获取富文本编辑器中的HTML的值，赋值给entity变量中的goodsDesc
            this.entity.goodsDesc.introduction = editor.html();
            axios.post('/goods/add.shtml', this.entity).then(function (response) {
                if (response.data.success) {
                    alert("添加成功！");
                    window.location.href = "goods.html";
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            //获取富文本编辑器中的HTML的值，赋值给entity变量中的goodsDesc
            this.entity.goodsDesc.introduction = editor.html();
            axios.post('/goods/update.shtml', this.entity).then(function (response) {
                alert(app.Dpage);
                console.log(response);
                if (response.data.success) {
                    alert("更新成功!");
                    window.location.href = "goods.html";
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            if (this.entity.goods.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        findOne: function (id) {
            axios.get('/goods/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;

                //获取介绍信息   赋值给富文本编辑器
                editor.html(app.entity.goodsDesc.introduction);

                //转json
                app.entity.goodsDesc.itemImages = JSON.parse(app.entity.goodsDesc.itemImages);
                app.entity.goodsDesc.customAttributeItems = JSON.parse(app.entity.goodsDesc.customAttributeItems);
                app.entity.goodsDesc.specificationItems = JSON.parse(app.entity.goodsDesc.specificationItems);

                var itemList = app.entity.itemList;

                for (var i = 0; i < itemList.length; i++) {
                    var obj = itemList[i];
                    obj.spec = JSON.parse(obj.spec);
                }

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/goods/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
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
                url: 'http://localhost:9110/upload/uploadFile.shtml',
                //data就是表单数据
                data: formData,
                method: 'post',
                //指定头信息：
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                //开启跨域请求携带相关认证信息
                withCredentials: true
            }).then(function (response) {
                if (response.data.success) {
                    //app.imageurl=response.data.message;//url地址
                    app.image_entity.url = response.data.message;
                    ;
                } else {
                    alert(response.data.message);
                }
            })

        },
        addImage_Entity: function () {
            //向数组中添加一个图片的对象
            this.entity.goodsDesc.itemImages.push(this.image_entity)
        },
        remove_Image_Entity: function (index) {
            this.entity.goodsDesc.itemImages.splice(index, 1);
        },
        Image_dele: function () {
            for (var i = this.ids.length; i >= 0; i--) {
                this.entity.goodsDesc.itemImages.splice(this.ids[i], 1);
            }
            this.ids = [];
        },
        //=====================================================
        findItemCat1List: function () {
            axios.get('/itemCat/findByParentId/0.shtml').then(function (response) {
                app.itemCat1List = response.data;

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },


        updateChecked: function ($event, specName, specValue) {
            var searchObject = this.searchObjectByKey(this.entity.goodsDesc.specificationItems, specName, 'attributeName');
            if (searchObject != null) {
                //searchObject====={"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
                if ($event.target.checked) {
                    searchObject.attributeValue.push(specValue);
                } else {
                    searchObject.attributeValue.splice(searchObject.attributeValue.indexOf(specValue), 1);
                    if (searchObject.attributeValue.length == 0) {
                        this.entity.goodsDesc.specificationItems.splice(this.entity.goodsDesc.specificationItems.indexOf(searchObject), 1);
                    }
                }
            } else {
                //[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
                this.entity.goodsDesc.specificationItems.push({
                    "attributeName": specName,
                    "attributeValue": [specValue]
                })
            }
        },

        /**
         *
         * @param list 从该数组中查询[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}]
         * @param specName  指定查询的属性的具体值 比如 网络
         * @param key  指定从哪一个属性名查找  比如：attributeName
         * @returns {*}
         */
        searchObjectByKey: function (list, specName, key) {
            //var specificationItems = this.entity.goodsDesc.specificationItems;
            for (var i = 0; i < list.length; i++) {
                var specificationItem = list[i];//{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
                if (specificationItem[key] == specName) {
                    return specificationItem;
                }

            }
            return null;
        },

        //点击复选框的时候 调用生成 sku列表的的变量
        createList: function () {
            //定义初始化的值
            this.entity.itemList = [{'spec': {}, 'price': '0', 'num': '0', 'status': '0', 'isDefault': '0'}];

            //2.循环遍历 entity.goodsDesc.specificationItems

            var specificationItems = this.entity.goodsDesc.specificationItems;

            for (var i = 0; i < specificationItems.length; i++) {

                //3.获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表

                var obj = specificationItems[i];

                this.entity.itemList = this.addColumn(
                    this.entity.itemList,
                    obj.attributeName,
                    obj.attributeValue);
            }
        },

        /**
         *获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表 方法
         * @param list
         * @param columnName  网络
         * @param columnValue  [移动3G,移动4G]
         */
        addColumn: function (list, columnName, columnValue) {
            var newList = [];

            for (var i = 0; i < list.length; i++) {
                var oldRow = list[i];
                for (var j = 0; j < columnValue.length; j++) {
                    //var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                    var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆

                    newRow.spec[columnName] = columnValue[j];

                    newList.push(newRow);
                }
            }
            return newList;
        },
        isChecked: function (specName, specValue) {
            //判断 循环到的 规格选项  是否  在已有的变量存在  如果存在就有勾选  否则就不勾选
            var specificationItems = this.entity.goodsDesc.specificationItems;

            var searchObjectByKey = this.searchObjectByKey(specificationItems, "attributeName", specName);


            if (specificationItems == null) {
                return false;
            }

            for (var i = 0; i < specificationItems.length; i++) {
                if (specificationItems[i].attributeValue.indexOf(specValue) != -1) {
                    return true;
                }
            }
        }


    },


    //使用Vue的监听机制
    watch: {
        //监听一级分类的ID变化  查询一级分类下二级分类的列表数据
        //entity.goods.category1Id 为要监听变量 ，当发生变化时 触发函数，newval 表示的是新值，oldvalue 表示的是旧值
        'entity.goods.category1Id': function (newval, olvalue) {
            //赋值为空
            this.itemCat3List = [];

            //删除属性回到原始状态
            if (this.entity.goods.id == null) {
                delete this.entity.goods.category2Id;

                delete this.entity.goods.category3Id;

                delete this.entity.typeTemplateId;
            }


            if (newval != undefined) {
                axios.get('/itemCat/findByParentId/' + newval + '.shtml').then(
                    function (response) {
                        app.itemCat2List = response.data;
                    }
                )
                    .catch(function (error) {
                        console.log(error.data.message)
                    })
            }

        },
        //监听二级分类的ID变化  查询二级分类下三级分类的列表数据
        'entity.goods.category2Id': function (newval, olvalue) {

            //删除
            if (this.entity.goods.id == null) {
                delete this.entity.goods.category3Id;
                delete this.entity.goods.typeTemplateId;
            }

            if (newval != undefined) {
                axios.get('/itemCat/findByParentId/' + newval + '.shtml').then(
                    function (response) {
                        app.itemCat3List = response.data;
                    }
                )
                    .catch(function (error) {
                        console.log(error.data.message)
                    })
            }

        },
        //监听三级分类的ID变化  查询三级分类对象里面模板id   展示到页面
        'entity.goods.category3Id': function (newval, oldval) {
            if (newval != undefined) {
                axios.get('/itemCat/findOne/' + newval + '.shtml').then(function (response) {
                    //获取列表数据   三级分类的列表
                    //app.entity.goods.typeTemplateId = response.data.typeId;
                    //第一个参数：需要改变的值的对象变量
                    //第二个参数：需要赋值的属性名
                    //第三个参数：要赋予的值
                    app.$set(app.entity.goods, 'typeTemplateId', response.data.typeId);
                    console.log(response.data.typeId);
                    console.log(app.entity.goods.typeTemplateId);

                })

            }
        },
        //监听模板id的变化 查询该模板对象   对象里面有品牌列表数据
        'entity.goods.typeTemplateId': function (newval, oldval) {
            if (newval != undefined) {
                axios.get('/typeTemplate/findOne/' + newval + '.shtml').then(function (response) {

                    //获取到模板对象
                    var typeTemplate = response.data;

                    //品牌列表赋值给brandTextListL   brandIDS拿到的是字符串需要转换为对象
                    app.brandTextListL = JSON.parse(typeTemplate.brandIds);


                    //获取模板对象中的扩展属性的值   定义一个变量

                    //判断  如果是新增就需要
                    if (app.entity.goods.id == null || app.entity.goods.id == undefined) {

                        app.entity.goodsDesc.customAttributeItems = JSON.parse(typeTemplate.customAttributeItems);
                    }


                })

                axios.get('/typeTemplate/findSpecList/' + newval + '.shtml').then(
                    function (response) {
                        app.specList = response.data;

                    })

            }
        },
        //监控数据变化 ，如果最后还剩下一个就直接删除
        'entity.itemList': function (newval, oldval) {
            //如果是相同的数据那么直接赋值为空即可
            console.log(JSON.stringify([{
                'spec': {},
                'price': '0',
                'num': '0',
                'status': '0',
                'isDefault': '0'
            }]) == JSON.stringify(newval));

            if (JSON.stringify([{
                'spec': {},
                'price': '0',
                'num': '0',
                'status': '0',
                'isDefault': '0'
            }]) == JSON.stringify(newval)) {
                this.entity.itemList = [];
            }
        }


    },


    //钩子函数 初始化了事件和
    created: function () {

        this.findItemCat1List();

        // 使用插件中的方法getUrlParam（） 返回是一个JSON对象，例如：{id:149187842867989}
        var requset = this.getUrlParam();
        //获取参数的值
        console.log(requset);
        //根据id 获取 商品的信息
        this.findOne(requset.id);
    }


});
