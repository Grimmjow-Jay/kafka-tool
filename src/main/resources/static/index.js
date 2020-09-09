;!function () {
    const element = layui.element;
    const layer = layui.layer;
    const $ = layui.$;
    const table = layui.table;
    const form = layui.form;

    let layerShade;
    let currentCluster;
    let monitorChart;

    const urls = {
        getCluster: '/cluster/clusters',
        getBrokers: '/cluster/nodes/{clusterName}',
        getTopics: '/topic/topics/{clusterName}',
        getTopicDetail: '/topic/detail/{clusterName}/{topic}',
        getConsumers: '/consumer/consumers/{clusterName}',
        getConsumerOffsets: '/consumer/offsets/{clusterName}/{consumerName}',
        addCluster: '/cluster',
        addMonitor: '/monitor/add',
        removeMonitor: '/monitor/remove/{id}',
        activeMonitor: '/monitor/active/{id}',
        disableMonitor: '/monitor/disable/{id}',
        getMonitorData: '/monitor/offset-data',
        listMonitor: '/monitor/list'
    };

    const tableCol = {
        monitorTask: [[
            {field: 'consumer', title: '消费者'},
            {field: 'topic', title: 'Topic'},
            {field: 'interval', title: '时间间隔(秒)'},
            {field: 'isActive', title: '操作', align: 'center', templet: showOperate}
        ]]
    };

    updateCluster();
    initAddClusterBtn();
    initAddMonitorBtn();
    initShowMonitorChart();
    initManageMonitorSelectBtn();

    form.render();
    layui.laydate.render({
        elem: '#show-monitor-time-range',
        type: 'datetime',
        range: true
    });

    function updateCluster() {
        ajaxGet(urls.getCluster, showClusterSelect);
    }

    function initAddClusterBtn() {
        $('#add-cluster-btn').on('click', function () {
            layer.open({
                type: 1,
                title: "添加集群",
                area: ["650px", "300px"],
                content: $('#add-cluster-div').html(),
                success: addCluster
            });
        });
    }

    function addCluster(dom, index) {
        form.render(null, 'add-cluster-filter');
        form.on('submit(add-cluster-filter)', function (parameters) {
            const data = parameters.field;
            const url = urls.addCluster;
            ajaxPost(url, JSON.stringify(data), function () {
                layer.msg('添加成功');
                updateCluster();
                layer.close(index);
            });
            return false;
        });
    }

    function initAddMonitorBtn() {
        $('#add-monitor-btn').on('click', function () {
            if (!currentCluster) {
                showErrorInfo("请先选择集群");
                return;
            }
            layer.open({
                type: 1,
                title: "添加监控 --  " + currentCluster,
                area: ["650px", "350px"],
                content: $('#add-monitor-div').html(),
                success: addMonitor
            });
        });
    }

    function addMonitor(dom, index) {
        form.render();
        form.on('submit(add-monitor-filter)', function (parameters) {
            const data = parameters.field;
            data["clusterName"] = currentCluster;
            const url = urls.addMonitor;
            ajaxPost(url, JSON.stringify(data), function () {
                layer.msg('添加成功');
                layer.close(index);
            });
            return false;
        });
    }

    function showClusterSelect(clusterList) {

        let clusterSelectHtml = '';
        clusterSelectHtml += '<select lay-filter="cluster" name="cluster">';
        clusterSelectHtml += '<option value=""></option>';
        for (let i = 0; i < clusterList.length; i++) {
            let clusterName = clusterList[i]["clusterName"];
            clusterSelectHtml += '<option value="' + clusterName + '">' + clusterName + '</option>';
        }

        clusterSelectHtml += '</select>';
        $("#cluster-select").html(clusterSelectHtml);

        form.on('select(cluster)', function (elem) {
            if (elem && elem.value) {
                currentCluster = elem.value;
                updateTopics();
                updateBrokers();
                updateConsumers();
            }
        });
        form.render();
    }

    function updateBrokers() {
        ajaxGet(urls.getBrokers.replace(/{clusterName}/, currentCluster), showBrokers);
    }

    function showBrokers(brokers) {
        let $brokersTable = $("#brokers-table");
        if (!brokers) {
            $brokersTable.html('');
            return;
        }
        let brokersTableHtml = '';
        for (let i = 0; i < brokers.length; i++) {
            let broker = brokers[i];
            brokersTableHtml += '<tr><td>' + broker["id"] + '</td>';
            brokersTableHtml += '<td>' + broker["host"] + '</td>';
            brokersTableHtml += '<td>' + broker["port"] + '</td></tr>';
        }
        $brokersTable.html(brokersTableHtml);
        element.render();
    }

    function updateTopics() {
        ajaxGet(urls.getTopics.replace(/{clusterName}/, currentCluster), showTopics);
    }

    function showTopics(topicList) {
        let $topicList = $("#topic-list");
        let $addMonitorTopicSelect = $("#add-monitor-topic-select");
        let $showMonitorTopicSelect = $("#show-monitor-topic-select");
        let $manageMonitorTopicSelect = $("#manage-monitor-topic-select");
        if (!topicList) {
            $topicList.html('');
            element.render();
            return;
        }

        let topicListHtml = '';
        let topicSelectHtml = '<option value="">选择Topic</option>';
        for (let i = 0; i < topicList.length; i++) {
            topicListHtml += '<dd><a href="javascript:;" title="' + topicList[i] + '">' + topicList[i] + '</a></dd>';
            topicSelectHtml += '<option value="' + topicList[i] + '">' + topicList[i] + '</option>';
        }

        $topicList.html(topicListHtml);
        element.render();

        $addMonitorTopicSelect.html(topicSelectHtml);
        $showMonitorTopicSelect.html(topicSelectHtml);
        $manageMonitorTopicSelect.html(topicSelectHtml);
        form.render();

        element.on('nav(topic-nav)', function (elem) {
            const topic = elem.text();
            updateTopicDetail(topic);
        });
    }

    function updateTopicDetail(topic) {
        let updateTopicDetailUrl = urls.getTopicDetail
            .replace(/{clusterName}/, currentCluster)
            .replace(/{topic}/, topic);
        ajaxGet(updateTopicDetailUrl, showTopicDetail);
    }

    function showTopicDetail(topicDetail) {
        let $topicDetailTable = $("#topic-detail-table");
        if (!topicDetail) {
            $topicDetailTable.html('');
            return;
        }
        let brokersTableHtml = '';
        let topicName = topicDetail["name"];
        let partitions = topicDetail["partitions"];
        for (let i = 0; i < partitions.length; i++) {
            const partition = partitions[i];
            brokersTableHtml += '<tr><td>' + topicName + '</td>';
            brokersTableHtml += '<td>' + partition["partition"] + '</td>';
            brokersTableHtml += '<td>' + partition["offset"] + '</td>';
            brokersTableHtml += '<td>' + concatPartition(partition["replicas"]) + '</td>';
            const leader = partition["leader"];
            brokersTableHtml += '<td>' + leader["host"] + ':' + leader["port"] + '</td></tr>';
        }
        $topicDetailTable.html(brokersTableHtml);
        element.render();
    }

    function concatPartition(nodeList) {
        let partitionHtml = '';
        let length = nodeList.length;
        for (let i = 0; i < length; i++) {
            let node = nodeList[i];
            partitionHtml += node["host"] + ':' + node["port"];
            if (i < length) {
                partitionHtml += "<br/>"
            }
        }
        return partitionHtml;
    }

    function updateConsumers() {
        const getConsumersUrl = urls.getConsumers.replace(/{clusterName}/, currentCluster);
        ajaxGet(getConsumersUrl, showConsumers);
    }

    function showConsumers(consumerList) {
        let $consumerList = $("#consumer-list");
        let $addMonitorConsumerSelect = $("#add-monitor-consumer-select");
        let $showMonitorConsumerSelect = $("#show-monitor-consumer-select");
        let $manageMonitorConsumerSelect = $("#manage-monitor-consumer-select");
        if (!consumerList) {
            $consumerList.html('');
            element.render();
            return;
        }

        let consumerListHtml = '';
        let consumerSelectHtml = '<option value="">选择消费者</option>';
        for (let i = 0; i < consumerList.length; i++) {
            consumerListHtml += '<dd><a href="javascript:;">' + consumerList[i] + '</a></dd>';
            consumerSelectHtml += '<option value="' + consumerList[i] + '">' + consumerList[i] + '</option>';
        }

        $consumerList.html(consumerListHtml);
        element.render();

        $addMonitorConsumerSelect.html(consumerSelectHtml);
        $showMonitorConsumerSelect.html(consumerSelectHtml);
        $manageMonitorConsumerSelect.html(consumerSelectHtml);
        form.render();

        element.on('nav(consumer-nav)', function (elem) {
            const consumer = elem.text();
            updateConsumerOffsets(consumer);
        });
    }

    function updateConsumerOffsets(consumer) {
        let getConsumerOffsetsUrl = urls.getConsumerOffsets
            .replace(/{clusterName}/, currentCluster)
            .replace(/{consumerName}/, consumer);
        ajaxGet(getConsumerOffsetsUrl, showConsumerOffsets);
    }

    function showConsumerOffsets(consumerOffsetList) {
        let $consumerTopicTable = $("#consumer-topic-table");
        if (!consumerOffsetList) {
            $consumerTopicTable.html('');
            return;
        }
        let consumerOffsetsTableHtml = '';
        for (let i = 0; i < consumerOffsetList.length; i++) {
            const consumerOffset = consumerOffsetList[i];
            consumerOffsetsTableHtml += '<tr><td>' + consumerOffset["consumer"] + '</td>';
            consumerOffsetsTableHtml += '<td>' + consumerOffset["topic"] + '</td>';
            consumerOffsetsTableHtml += '<td>' + consumerOffset["partition"] + '</td>';
            consumerOffsetsTableHtml += '<td>' + consumerOffset["offset"] + '</td>';
            consumerOffsetsTableHtml += '<td>' + consumerOffset["logSize"] + '</td>';
            consumerOffsetsTableHtml += '<td>' + consumerOffset["lag"] + '</td></tr>';
        }
        $consumerTopicTable.html(consumerOffsetsTableHtml);
        element.render();
    }

    function initShowMonitorChart() {
        monitorChart = echarts.init($("#monitor-chart")[0]);
        form.on('submit(show-monitor)', function (parameters) {
            const data = parameters.field;
            let timeRange = data['timeRange'];
            if (!timeRange) {
                showErrorInfo("请选择时间范围");
                return false;
            }
            let timeRangeArr = timeRange.split(' - ');
            data['startTime'] = timeRangeArr[0];
            data['endTime'] = timeRangeArr[1];
            showMonitorChart(data);
            return false;
        });
    }

    function showMonitorChart(requestData) {
        let monitorDataUrl = urls.getMonitorData;
        const consumer = requestData['consumer'];
        const topic = requestData['topic'];
        const interval = requestData['interval'];
        const startTime = requestData['startTime'];
        const endTime = requestData['endTime'];

        monitorDataUrl += '?clusterName=' + currentCluster;
        monitorDataUrl += '&consumer=' + consumer;
        monitorDataUrl += '&topic=' + topic;
        monitorDataUrl += '&interval=' + interval;
        monitorDataUrl += '&startTime=' + startTime;
        monitorDataUrl += '&endTime=' + endTime;
        ajaxGet(monitorDataUrl, function (monitorDataList) {
            const subtext = 'Topic: ' + requestData['topic'] + '   Consumer: ' + requestData['consumer'];

            let xAxisData = [];
            let offsetMax = null;
            let offsetMin = null;
            let lagMax = null;
            let lagMin = null;
            const offsetSeriesData = [];
            const logSizeSeriesData = [];
            const lagSeriesData = [];
            for (let i = 0; i < monitorDataList.length; i++) {
                let monitorData = monitorDataList[i];
                xAxisData.push(monitorData['date']);
                const offset = monitorData['offset'];
                const logSize = monitorData['logSize'];
                const lag = monitorData['lag'];
                offsetMax = offsetMax === null ? Math.max(offset, logSize) : Math.max(offsetMax, offset, logSize);
                offsetMin = offsetMin == null ? Math.min(offset, logSize) : Math.min(offsetMin, offset, logSize);
                lagMax = lagMax == null ? lag : Math.max(lagMax, lag);
                lagMin = lagMin == null ? lag : Math.min(lagMin, lag);
                offsetSeriesData.push(offset);
                logSizeSeriesData.push(logSize);
                lagSeriesData.push(lag);
            }
            offsetMax = offsetMax == null ? 1 : offsetMax;
            offsetMin = offsetMin == null ? 0 : offsetMin;
            lagMax = lagMax == null ? 1 : lagMax;
            lagMin = lagMin == null ? 0 : lagMin;

            let offsetDiff = offsetMax - offsetMin;
            let lagDiff = lagMax - lagMin;

            offsetMax = Math.ceil(offsetDiff * 0.1 + offsetMax);
            offsetMin = Math.max(Math.floor(offsetMin - offsetDiff * 0.1), 0);
            lagMax = Math.ceil(lagDiff * 0.1 + lagMax);
            lagMin = Math.max(Math.floor(lagMin - lagDiff * 0.1), 0);

            const chartOption = {
                title: {
                    text: 'Offset History',
                    subtext: subtext,
                    left: 'center',
                    align: 'right'
                },
                toolbox: {
                    feature: {
                        dataZoom: {
                            yAxisIndex: 'none'
                        },
                        restore: {}
                    }
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        animation: false,
                        label: {
                            backgroundColor: '#505765'
                        }
                    }
                },
                legend: {
                    data: ['Offset', 'LogSize', 'Lag'],
                    left: 50
                },
                dataZoom: [
                    {
                        type: 'slider',
                        xAxisIndex: 0,
                        filterMode: 'filter'
                    },
                    {
                        type: 'slider',
                        yAxisIndex: 0,
                        filterMode: 'filter',
                        left: '3%'
                    },
                    {
                        type: 'slider',
                        yAxisIndex: 1,
                        filterMode: 'filter',
                        right: '3%'
                    },
                    {
                        type: 'inside',
                        xAxisIndex: 0,
                        filterMode: 'filter'
                    }
                ],
                xAxis: [
                    {
                        type: 'category',
                        boundaryGap: false,
                        axisLine: {onZero: false},
                        data: xAxisData
                    }
                ],
                yAxis: [
                    {
                        name: 'Offset/LogSize',
                        type: 'value',
                        max: offsetMax,
                        min: offsetMin
                    },
                    {
                        name: 'Lag',
                        nameLocation: 'start',
                        max: lagMax,
                        min: lagMin,
                        type: 'value',
                        inverse: true
                    }
                ],
                series: [
                    {
                        name: 'Offset',
                        type: 'line',
                        animation: false,
                        lineStyle: {
                            width: 1
                        },
                        markArea: {
                            silent: true,
                            data: []
                        },
                        data: offsetSeriesData
                    },
                    {
                        name: 'LogSize',
                        type: 'line',
                        animation: false,
                        lineStyle: {
                            width: 1
                        },
                        markArea: {
                            silent: true,
                            data: []
                        },
                        data: logSizeSeriesData
                    },
                    {
                        name: 'Lag',
                        type: 'line',
                        yAxisIndex: 1,
                        animation: false,
                        areaStyle: {},
                        lineStyle: {
                            width: 1
                        },
                        markArea: {
                            silent: true,
                            data: []
                        },
                        data: lagSeriesData
                    }
                ]
            };
            monitorChart.clear();
            monitorChart.setOption(chartOption);
        });
    }

    function initManageMonitorSelectBtn() {
        form.on('submit(manage-monitor-select)', function (parameters) {
            const data = parameters.field;
            showManageMonitorTable(data);
            return false;
        });
    }

    function showOperate(data) {
        const checked = data['isActive'] ? 'checked' : '';
        return '<form class="layui-form" lay-filter="is-active-filter-' + data['id'] + '">' +
            '<input type="checkbox" id="monitor-task-' + data['id'] + '" name="isActive" ' +
            'lay-skin="switch" lay-text="启用|禁用" lay-filter="is-active-checkbox" ' + checked + '>  ' +
            '<a id="monitor-task-delete-' + data['id'] + '" class="layui-btn layui-btn-danger layui-btn-xs" ' +
            'lay-filter="monitor-task-delete-filter" lay-submit>删除</a>' +
            '</form>';
    }

    function showManageMonitorTable(requestData) {
        let listMonitorUrl = urls.listMonitor;
        listMonitorUrl += '?clusterName=' + currentCluster;
        listMonitorUrl += '&consumer=' + requestData['consumer'];
        listMonitorUrl += '&topic=' + requestData['topic'];

        ajaxGet(listMonitorUrl, function (monitorTaskList) {
            table.render({
                elem: '#monitor-task-table',
                data: monitorTaskList,
                page: true,
                cols: tableCol.monitorTask
            });
            form.on('switch(is-active-checkbox)', function (obj) {
                let url = obj.elem.checked ? urls.activeMonitor : urls.disableMonitor;
                const monitorTaskId = obj.elem.id.replace(/monitor-task-/, '');
                url = url.replace(/{id}/, monitorTaskId);
                ajaxPut(url, null, doNothing, function (errorMsg) {
                    layer.open({
                        title: 'Error',
                        content: errorMsg,
                        end: function () {
                            form.val('is-active-filter-' + monitorTaskId, {
                                "isActive": !obj.elem.checked
                            })
                        }
                    });
                });
            });

            form.on('submit(monitor-task-delete-filter)', function (obj) {
                const monitorTaskId = obj.elem.id.replace(/monitor-task-delete-/, '');
                layer.open({
                    type: 1,
                    title: '提示',
                    area: ["260px", "160px"],
                    content: '<div style="padding:20px">确认删除?</div>',
                    btn: ['确认', '取消'],
                    yes: function (index) {
                        const url = urls.removeMonitor.replace(/{id}/, monitorTaskId);
                        ajaxDelete(url,
                            function () {
                                layer.close(index);
                                showManageMonitorTable(requestData);
                            }, function (errorMsg) {
                                showErrorInfo(errorMsg);
                            });
                    }
                });
                return false;
            })
        });
    }

    function ajaxGet(url, callback, errorCallback) {
        $.ajax({
            url: url,
            type: 'GET',
            dataType: 'json',
            success: function (result) {
                ajaxCallback(result, callback)
            },
            beforeSend: function () {
                layerShade = layer.load(1, {
                    shade: [0.5, '#393D49']
                });
            },
            complete: function () {
                layer.close(layerShade);
            },
            error: function (XMLHttpRequest, errorMsg, error) {
                if (errorCallback) {
                    errorCallback(errorMsg, error);
                } else {
                    showErrorInfo(errorMsg);
                }
            }
        });
    }

    function ajaxPost(url, data, callback, errorCallback) {
        $.ajax({
            url: url,
            type: 'POST',
            data: data,
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            success: function (result) {
                ajaxCallback(result, callback)
            },
            beforeSend: function () {
                layerShade = layer.load(1, {
                    shade: [0.5, '#393D49']
                });
            },
            complete: function () {
                layer.close(layerShade);
            },
            error: function (XMLHttpRequest, errorMsg, error) {
                if (errorCallback) {
                    errorCallback(errorMsg, error);
                } else {
                    showErrorInfo(errorMsg);
                }
            }
        });
    }

    function ajaxPut(url, data, callback, errorCallback) {
        $.ajax({
            url: url,
            type: 'PUT',
            data: data,
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            success: function (result) {
                ajaxCallback(result, callback, errorCallback)
            },
            beforeSend: function () {
                layerShade = layer.load(1, {
                    shade: [0.5, '#393D49']
                });
            },
            complete: function () {
                layer.close(layerShade);
            },
            error: function (XMLHttpRequest, errorMsg, error) {
                if (errorCallback) {
                    errorCallback(errorMsg, error);
                } else {
                    showErrorInfo(errorMsg);
                }
            }
        });
    }

    function ajaxDelete(url, callback, errorCallback) {
        $.ajax({
            url: url,
            type: 'DELETE',
            dataType: 'json',
            success: function (result) {
                ajaxCallback(result, callback, errorCallback)
            },
            beforeSend: function () {
                layerShade = layer.load(1, {
                    shade: [0.5, '#393D49']
                });
            },
            complete: function () {
                layer.close(layerShade);
            },
            error: function (XMLHttpRequest, errorMsg, error) {
                if (errorCallback) {
                    errorCallback(errorMsg, error);
                } else {
                    showErrorInfo(errorMsg);
                }
            }
        });
    }

    function ajaxCallback(result, callback, errorCallback) {
        if (!result) {
            showErrorInfo('调用失败');
            return
        }
        if (result.success) {
            callback(result.data);
            return;
        }
        console.log(result.message);
        if (errorCallback) {
            errorCallback(result.message)
        } else {
            showErrorInfo(result.message);
        }
    }

    function showErrorInfo(errorMsg) {
        errorMsg = errorMsg ? errorMsg : '未知错误';
        layer.msg(errorMsg, {
            offset: 't',
            anim: 5,
            time: 5000
        });
    }

    function doNothing() {
    }
}();