;!function () {
    const $ = layui.$;
    const tree = layui.tree;

    const articleId = $("#articleId").text();
    let layerShade;

    // 通过articleId查询评论
    const url = "/blog/getDiscussionTreeData?articleId=" + articleId;
    $.ajax({
        url: url,
        type: 'GET',
        dataType: 'json',
        success: function (result) {
            tree.render({
                elem: '#discussions',
                data: result.data
            });
        },
        beforeSend: function (XMLHttpRequest) {
            layerShade = layer.load(1, {
                shade: [0.5, '#393D49']
            });
        },
        complete: function (XMLHttpRequest, textStatus) {
            // 关闭loading
            layer.close(layerShade);
        }
    });
}();