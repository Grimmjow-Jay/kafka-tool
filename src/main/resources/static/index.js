;!function () {
    const element = layui.element;
    const layer = layui.layer;
    const $ = layui.$;
    const table = layui.table;
    const form = layui.form;
    form.render();
    let layerShade;

    let currentCluster;

    const urls = {
        getCluster: '/cluster/clusters',
        getBrokers: '/cluster/nodes/{clusterName}',
        getTopics: '/topic/topics/{clusterName}',
        getTopicDetail: '/topic/detail/{clusterName}/{topic}',
        getConsumers: '/consumer/consumers/{clusterName}',
        getConsumerOffsets: '/consumer/offsets/{clusterName}/{consumerName}',
        addCluster: '/cluster'
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

    function updateCluster() {
        ajaxGet(urls.getCluster, showClusterSelect);
    }

    function initAddClusterBtn() {
        $('#add-cluster-btn').on('click', function () {
            layer.open({
                type: 1,
                title: "添加集群",
                area: ["800px", "300px"],
                content: $('#add-cluster-div').html(),
                success: addCluster
            });
        });
    }

    function addCluster(dom, index) {
        form.on('submit(addClusterFilter)', function (parameters) {
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
        if (!topicList) {
            $topicList.html('');
            element.render();
            return;
        }

        let topicListHtml = '';
        for (let i = 0; i < topicList.length; i++) {
            topicListHtml += '<dd><a href="javascript:;">' + topicList[i] + '</a></dd>';
        }

        $topicList.html(topicListHtml);
        element.render();

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
        if (!consumerList) {
            $consumerList.html('');
            element.render();
            return;
        }

        let consumerListHtml = '';
        for (let i = 0; i < consumerList.length; i++) {
            consumerListHtml += '<dd><a href="javascript:;">' + consumerList[i] + '</a></dd>';
        }

        $consumerList.html(consumerListHtml);
        element.render();

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
        layer.msg(errorMsg, {
            offset: 't',
            anim: 5,
            time: 5000
        });
    }
}();