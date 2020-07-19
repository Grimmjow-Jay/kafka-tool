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
        getConsumers: '/consumer/consumers/{clusterName}',
        getConsumerOffsets: '/consumer/offsets/{clusterName}/{consumerName}'
    };

    const consumersTableCol = [[
        {field: 'topic', title: 'topic'},
        {field: 'consumer', title: 'consumer'},
        {field: 'partition', title: 'partition'},
        {field: 'offset', title: 'offset'},
        {field: 'logSize', title: 'logSize'},
        {field: 'lag', title: 'lag'},
        {field: 'lastCommit', title: 'last commit'}
    ]];

    getCluster();

    function getCluster() {
        $.ajax({
            url: urls.getCluster,
            type: 'GET',
            dataType: 'json',
            success: function (result) {
                updateClusterList(result.data)
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

    function updateClusterList(clusterList) {
        let $clusterList = $("#cluster-list");
        if (!clusterList) {
            $clusterList.html('');
            element.render();
            return;
        }

        let clusterListHtml = '';
        for (const index in clusterList) {
            if (!clusterList.hasOwnProperty(index)) {
                continue;
            }
            let cluster = clusterList[index];
            clusterListHtml += '<dd><a href="javascript:;">' + cluster["clusterName"] + '</a></dd>';
        }

        $clusterList.html(clusterListHtml);
        element.render();

        element.on('nav(cluster-nav)', function (elem) {
            currentCluster = elem.text();
            updateBrokers();
            updateTopics();
            updateConsumers();
        });
    }

    function updateBrokers() {
        $.ajax({
            url: urls.getBrokers.replace(/{clusterName}/, currentCluster),
            type: 'GET',
            dataType: 'json',
            success: function (result) {
                showBrokers(result.data);
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

    function showBrokers(brokers) {
        let $brokersTable = $("#brokers-table");
        if (!brokers) {
            $brokersTable.html('');
        }
        let brokersTableHtml = '';
        for (let index in brokers) {
            if (!brokers.hasOwnProperty(index)) {
                continue;
            }
            let broker = brokers[index];
            brokersTableHtml += '<tr><td>' + broker["id"] + '</td>';
            brokersTableHtml += '<td>' + broker["host"] + '</td>';
            brokersTableHtml += '<td>' + broker["port"] + '</td></tr>';
        }
        $brokersTable.html(brokersTableHtml);
        element.render();
    }

    function updateTopics() {
        $.ajax({
            url: urls.getBrokers.replace(/{clusterName}/, currentCluster),
            type: 'GET',
            dataType: 'json',
            success: function (result) {
                showBrokers(result.data);
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

    function updateConsumers() {
        $.ajax({
            url: urls.getBrokers.replace(/{clusterName}/, currentCluster),
            type: 'GET',
            dataType: 'json',
            success: function (result) {
                showBrokers(result.data);
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

    function showConsumers() {

    }
}();