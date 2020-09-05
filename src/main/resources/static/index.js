;!function () {
    const element = layui.element;
    const layer = layui.layer;
    const laydate = layui.laydate;
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
        addMonitor: '/monitor/enable',
        getMonitorData: '/monitor/offset-data'
    };

    const tableCol = {
        consumers: [[
            {field: 'topic', title: 'topic'},
            {field: 'consumer', title: 'consumer'},
            {field: 'partition', title: 'partition'},
            {field: 'offset', title: 'offset'},
            {field: 'logSize', title: 'logSize'},
            {field: 'lag', title: 'lag'},
            {field: 'lastCommit', title: 'last commit'}
        ]],
        topic: [[
            {field: 'topic', title: 'topic'},
            {field: 'partition', title: 'partition'},
            {field: 'offset', title: 'offset'},
            {field: 'replicas', title: 'replicas'},
            {field: 'leader', title: 'leader'}
        ]]
    };

    updateCluster();

    initAddClusterBtn();

    initAddMonitorBtn();

    initShowMonitorChart();

    form.render();

    laydate.render({
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
            if (timeRange) {
                let timeRangeArr = timeRange.split(' - ');
                data['startTime'] = timeRangeArr[0];
                data['endTime'] = timeRangeArr[1];
            }
            showMonitorChart(data);
            return false;
        });
    }

    function showMonitorChart(requestData) {
        let monitorDataUrl = urls.getMonitorData;
        monitorDataUrl += '?clusterName=' + currentCluster;
        monitorDataUrl += '&consumer=' + requestData['consumer'];
        monitorDataUrl += '&topic=' + requestData['topic'];
        monitorDataUrl += '&interval=' + requestData['interval'];
        monitorDataUrl += '&startTime=' + requestData['startTime'];
        monitorDataUrl += '&endTime=' + requestData['endTime'];
        ajaxGet(monitorDataUrl, function (monitorDataList) {
            console.log(monitorDataList);
            const subtext = 'Topic: ' + requestData['topic'] + '   Consumer: ' + requestData['consumer'];

            let xAxisData = [];
            let offsetMax = 1;
            let offsetMin = 0;
            let lagMax = 1;
            let lagMin = 0;
            const offsetSeriesData = [];
            const logSizeSeriesData = [];
            const lagSeriesData = [];
            for (let i = 0; i < monitorDataList.length; i++) {
                let monitorData = monitorDataList[i];
                xAxisData.push(monitorData['date']);
                offsetMax = Math.max(offsetMax, monitorData['offset'], monitorData['logSize']);
                offsetMin = Math.min(offsetMax, monitorData['offset'], monitorData['logSize']);
                lagMax = Math.max(lagMax, monitorData['lag']);
                lagMin = Math.min(lagMax, monitorData['lag']);
                offsetSeriesData.push(monitorData['offset']);
                logSizeSeriesData.push(monitorData['logSize']);
                lagSeriesData.push(monitorData['lag']);
            }

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
                        show: true,
                        realtime: true
                    },
                    {
                        type: 'inside',
                        realtime: true
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
            monitorChart.setOption(chartOption);
        });
    }

    function ajaxGet(url, callback) {
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
            }
        });
    }

    function ajaxPost(url, data, callback) {
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
            }
        });
    }

    function ajaxCallback(result, callback) {
        if (!result) {
            showErrorInfo('调用失败');
            return
        }
        if (result.success) {
            callback(result.data);
        } else {
            console.log(result.message);
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
}();