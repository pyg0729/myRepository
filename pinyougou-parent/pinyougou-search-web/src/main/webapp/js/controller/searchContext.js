var app = new Vue({

    el: "#app",
    data: {
        searchMap: {
            "keywords": '',
            "category": '',
            'brand': '',
            spec: {},
            'price': '',
            'pageNo': 1,
            pageSize: 40,
            'sortField': '',
            'sortType': ''
        },
        resultMap: {brandList: []},//返回对象的结果
        searchEntity: {},
        pageLables: [],
        preDott: false,
        nextDott: false


    },
    methods: {
        search: function () {
            axios.post('/itemSearch/search.shtml', this.searchMap).then(
                function (response) {
                    app.resultMap = response.data;
                    app.buildPageLables();
                }
            );

        },
        addSearchItem: function (key, value) {
            if (key == 'category' || key == 'brand' || key == 'price') {
                this.searchMap[key] = value;
            } else {
                this.searchMap.spec[key] = value;
            }
            this.search();

        },
        removeSearchItem: function (key, value) {
            if (key == 'category' || key == 'brand' || key == 'price') {
                this.searchMap[key] = '';
            } else {
                delete this.searchMap.spec[key];
            }
            this.search();
        },
        buildPageLables: function () {
            this.pageLabels = [];

            this.preDott = false;
            this.nextDott = false;
            //显示以当前页为中心的5个页码
            let firstPage = 1;
            let lastPage = this.resultMap.totalPages;//总页数

            if (this.resultMap.totalPages > 5) {
                //判断 如果当前的页码 小于等于3  pageNo<=3      1 2 3 4 5  显示前5页
                if (this.searchMap.pageNo <= 3) {
                    firstPage = 1;
                    lastPage = 5;
                    this.preDott = false;
                    this.nextDott = true;
                } else if (this.searchMap.pageNo >= this.resultMap.totalPages - 2) {//如果当前的页码大于= 总页数-2    98 99 100
                    firstPage = this.resultMap.totalPages - 4;
                    lastPage = this.resultMap.totalPages;
                    this.preDott = true;
                    this.nextDott = false;
                } else {
                    firstPage = this.searchMap.pageNo - 2;
                    lastPage = this.searchMap.pageNo + 2;
                    this.preDott = true;
                    this.nextDott = true;

                }
            } else {
                this.preDott = false;
                this.nextDott = false;
            }
            for (let i = firstPage; i <= lastPage; i++) {
                this.pageLabels.push(i);
            }
        },

        queryByPage: function (page) {
            var number = parseInt(page);
            if (number > this.resultMap.totalPages) {
                number = this.resultMap.totalPages
            }

            if (number < 1) {
                number = 1;
            }


            this.searchMap.pageNo = number;
            this.search();
        },
        clear: function () {
            this.searchMap = {
                'keywords': this.searchMap.keywords,
                'category': '',
                'brand': '',
                spec: {},
                'price': '',
                'pageNo': 1,
                'pageSize': 40,
                'sortField': '',
                'sortType': ''
            };
        },
        doSort: function (sortField, sortType) {
            this.searchMap.sortField = sortField;
            this.searchMap.sortType = sortType;
            this.search();
        },
        isKeywordsIsBrand: function () {
            if (this.resultMap.brandList != null && this.resultMap.brandList.length > 0) {
                for (var i = 0; i < this.resultMap.brandList.length; i++) {
                    if (this.searchMap.keywords.indexOf(this.resultMap.brandList[i].text) != -1) {
                        this.searchMap.brand = this.resultMap.brandList[i].text;
                        return true;
                    }
                }
            }

            return false;
        }

    },

    created: function () {

        //1.获取URL中的参数的值
        var jsonObj = this.getUrlParam();

        //2.赋值给变量searchMap.keywords
        // decodeURIComponent 解码
        this.searchMap.keywords = decodeURIComponent(jsonObj.keywords);

        this.search();

    }


});