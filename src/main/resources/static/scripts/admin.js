$(function() {
    toastr.options = {
        "closeButton": true, //是否显示关闭按钮
        "debug": false, //是否使用debug模式
        "positionClass": "toast-top-right",//弹出窗的位置
        "showDuration": "100",//显示的动画时间
        "hideDuration": "1000",//消失的动画时间
        "timeOut": "1000", //展现时间
        "extendedTimeOut": "1000",//加长展示时间
        "showEasing": "swing",//显示时的动画缓冲方式
        "hideEasing": "linear",//消失时的动画缓冲方式
        "showMethod": "fadeIn",//显示时的动画方式
        "hideMethod": "fadeOut" //消失时的动画方式
    };
    var stompClient = null;
})

$("iframe[name=nm_iframe]").on("load", function() {
    var jsonResponse = JSON.parse($("iframe")[0].contentDocument.body.getElementsByTagName("pre")[0].innerText);
    if (jsonResponse['success']) {
        toastr.success("设置成功");
    } else {
        toastr.error(jsonResponse['msg']);
    }
})

/**
 * 检查异常分数
 */
function detectAbnormalScores() {
    var url = "/admin/abnormals";
    var req = new XMLHttpRequest();
    req.overrideMimeType("application/json");
    req.open('GET', url, true);
    req.onload = function() {
        var jsonResponse = JSON.parse(req.responseText);
        if (jsonResponse['success'] == true) {
            var html = "";
            var tableBody = document.getElementById("abnormal-table-body");
            var content = jsonResponse['content'];
            for (var i = 0; i < content.length; ++i) {
                var abnormalJson = content[i];
                var productId = abnormalJson['productId'];
                var criteriaId = abnormalJson['criteriaId'];

                html += ("<tr id='abnormal-row-" + productId + "-" + criteriaId+ "'>");
                html += ("<td>作品" + productId + "</td>");
                html += ("<td>" + abnormalJson['content1'] + "</td>");
                html += ("<td>" + abnormalJson['content2'] + "</td>");
                html += ("<td>" + abnormalJson['content3'] + "</td>");
                html += ("<td>" + abnormalJson['partGrade'] + "</td>");
                html += ("<td>" + abnormalJson['illustration'] + "</td>");
                html += ("<td>" + abnormalJson['juryAName'] + "</td>");
                html += ("<td><div><input type=number class='form-control' style='width:100px' placeholder='必填' value='" + abnormalJson['scoreA'] + "' " + "min='0' max='" + abnormalJson['partGrade'] + "' id='abnormal-score-input-" + i + "-" + abnormalJson['juryIdA'] + "'></div></td>");
                html += ("<td>" + abnormalJson['juryBName'] + "</td>");
                html += ("<td><div><input type=number class='form-control' style='width:100px' placeholder='必填' value='" + abnormalJson['scoreB'] + "' " + "min='0' max='" + abnormalJson['partGrade'] + "' id='abnormal-score-input-" + i + "-" + abnormalJson['juryIdB'] + "'></div></td>");
                html += ("<td><div><input type=number class='form-control' style='width:100px' placeholder='必填' value='" + abnormalJson['scoreAdmin'] + "' " + "min='0' max='" + abnormalJson['partGrade'] + "' id='abnormal-score-input-" + i + "-admin'></div></td></tr>");
            }
            tableBody.innerHTML = html;
        } else {
            toastr.error(jsonResponse['msg']);
        }
    };
    req.send();
}

/**
 * 提交异常分数
 */
function submitAbnormalScores() {
    var tableObj = document.getElementById("abnormal-table-body");
    var abnormalDtoList = [];
    for (var i = 0; i < tableObj.rows.length; i++) {    //遍历Table的所有Row
        var productId = tableObj.rows[i].id.split("-")[2];
        var criteriaId = tableObj.rows[i].id.split("-")[3];
        var juryIdA = tableObj.rows[i].cells[7].firstElementChild.firstElementChild.id.split("-")[4];
        var scoreA = tableObj.rows[i].cells[7].firstElementChild.firstElementChild.value;
        var juryIdB = tableObj.rows[i].cells[9].firstElementChild.firstElementChild.id.split("-")[4];
        var scoreB = tableObj.rows[i].cells[9].firstElementChild.firstElementChild.value;
        var scoreAdmin = tableObj.rows[i].cells[10].firstElementChild.firstElementChild.value;

        var item = {
            productId: productId,
            criteriaId: criteriaId,
            juryIdA: juryIdA,
            scoreA: scoreA,
            juryIdB: juryIdB,
            scoreB: scoreB,
            scoreAdmin: scoreAdmin
        };
        abnormalDtoList.push(item);
    }

    var url  = "/admin/abnormals";
    var req = new XMLHttpRequest();
    req.overrideMimeType("application/json");
    req.open('POST', url, true);
    req.setRequestHeader('content-type', 'application/json');
    req.onload = function() {
        var jsonResponse = JSON.parse(req.responseText);
        if (jsonResponse['success']) {
            toastr.success("评分成功");
        } else {
            toastr.error(jsonResponse['msg']);
        }
    }
    req.send(JSON.stringify(abnormalDtoList));
}

function openMonitor() {
    var url = "/admin/monitor";
    var req = new XMLHttpRequest();
    req.overrideMimeType("application/json");
    req.open('GET', url, true);
    req.onload = function() {
        var jsonResponse = JSON.parse(req.responseText);
        if (jsonResponse['success']) {
            toastr.success("监控初始化成功");
            var content = jsonResponse['content'];
            var juryMapJson = content['juryIdJuryNameMap'];
            var headHtml = "<tr><th>#</th>";
            var tableHead = document.getElementById("monitor-table-head");
            for (var juryId in juryMapJson) {
                headHtml += ("<th>" + juryMapJson[juryId] + "</th>");
            }
            headHtml += "</tr>";
            tableHead.innerHTML = headHtml;

            var bodyHtml = "";
            var tableBody = document.getElementById("monitor-table-body");
            var productIds = content['productIds'];
            for (var i in productIds) {
                bodyHtml += "<tr>"
                bodyHtml += ("<td>作品" + productIds[i] + "</td>");
                for (var juryId in juryMapJson) {
                    bodyHtml += ("<td id='monitor-"+ juryId + "-" + productIds[i] + "'>等待</td>");
                }
                bodyHtml += "</tr>";
            }
            tableBody.innerHTML = bodyHtml;

            var judgeMessages = content['judgeMessages'];
            for (var i in judgeMessages) {
                var juryId = judgeMessages[i]['juryId'];
                var productId = judgeMessages[i]['productId'];
                document.getElementById("monitor-" + juryId + "-" + productId).className = "table-success";
                document.getElementById("monitor-" + juryId + "-" + productId).innerHTML = "<span class='fui-check'></span>";
            }

            var socket = new SockJS("/monitor");//连接SockJS的endpoint名称为"/endpoint"
            stompClient = Stomp.over(socket);//使用STOMP子协议的WebSocket客户端
            stompClient.connect({}, function (frame) {//连接WebSocket服务端
                stompClient.subscribe("/topic/judge", function (msg) {//通过stopmClient.subscribe订阅"/topic/response"目标发送的消息，这个路径是在控制器的@SendTo中定义的
                    console.log(msg);
                    var msgJson = JSON.parse(msg['body'])
                    for (var i in msgJson) {
                        var juryId = msgJson[i]['juryId'];
                        var productId = msgJson[i]['productId'];
                        document.getElementById("monitor-" + juryId + "-" + productId).className = "table-success";
                        document.getElementById("monitor-" + juryId + "-" + productId).innerHTML = "<span class='fui-check'></span>";
                    }
                });
            });
        } else {
            console.log("监控初始化失败");
        }

    }
    req.send();


}

function closeMonitor() {
    if (stompClient != null) {
        stompClient.disconnect();
        toastr.success("已关闭监控");
    }
}