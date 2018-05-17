$(function(){
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
    var juryId = document.getElementById("jury-id").innerText;
    initCriteriasTable();
    getJuryUnJudgedProductIds(juryId);
    getJuryJudgedProductIds(juryId);
});

/**
 * 评委已评分作品select
 * ajax GET: /jurys/{juryId}/products
 */
function getJuryJudgedProductIds(juryId) {
    var select = document.getElementById("judged-products-select");
    var url = "/jurys/" + juryId + "/products?status=1";
    var req = new XMLHttpRequest();
    req.overrideMimeType("application/json");
    req.open('GET', url, true);
    req.onload = function() {
        var jsonResponse = JSON.parse(req.responseText);
        if (jsonResponse['success']) {
            var html = "";
            var content = jsonResponse['content'];
            for (var i = 0; i < content.length; ++i) {
                var productId = content[i];
                html += "<option value=" + productId + ">已评作品" + productId + "</option>";
            }
            select.innerHTML = html;
            select.selectedIndex = -1;
        } else {
            toastr.error(jsonResponse['msg']);
        }
    };
    req.send();
}

/**
 * 评委未评分作品select
 * ajax GET: /jurys/{juryId}/products
 */
function getJuryUnJudgedProductIds(juryId) {
    var select = document.getElementById("unJudged-products-select");
    var url = "/jurys/" + juryId + "/products?status=0";
    var req = new XMLHttpRequest();
    req.overrideMimeType("application/json");
    req.open('GET', url, true);
    req.onload = function() {
        var jsonResponse = JSON.parse(req.responseText);
        if (jsonResponse['success']) {
            var html = "";
            var content = jsonResponse['content'];
            for (var i = 0; i < content.length; ++i) {
                var productId = content[i];
                html += "<option value=" + productId + ">未评作品" + productId + "</option>";
            }
            select.innerHTML = html;
            select.selectedIndex = -1;
        } else {
            toastr.error(jsonResponse['msg']);
        }
    };
    req.send();
}

/**
 * 已评作品Select被选中
 */
function judgedProductSelected() {
    var juryId = document.getElementById("jury-id").innerText;
    var select = document.getElementById("judged-products-select");
    var productId = select.options[select.selectedIndex].value;
    var url  = "/jurys/" + juryId + "/scores?productId=" + productId;
    var req = new XMLHttpRequest();
    req.overrideMimeType("application/json");
    req.open('GET', url, true);
    req.onload = function() {
        var jsonResponse = JSON.parse(req.responseText);
        if (jsonResponse['success']) {
            var content = jsonResponse['content'];
            for (var i = 0; i < content.length; ++i) {
                var scoreDtoJson = content[i];
                var criteriaId = scoreDtoJson['criteriaId'];
                var score = scoreDtoJson['score'];
                var scoreInputId = "score-input-" + criteriaId;
                document.getElementById(scoreInputId).value = score;
            }
            document.getElementById("product-desc").innerText = "作品" + productId;
            document.getElementById("product-id").innerText = productId;
        } else {
            toastr.error(jsonResponse['msg']);
        }
    };
    req.send();
    var select = document.getElementById("judged-products-select");
    select.selectedIndex = -1;
}

/**
 * 未评作品Select被选中
 */
function unJudgedProductSelected() {
    var select = document.getElementById("unJudged-products-select");
    var productId = select.options[select.selectedIndex].value;
    document.getElementById("product-desc").innerText = "作品" + productId;
    document.getElementById("product-id").innerText = productId;
    var criteriaIds = document.getElementById("criteria-ids").innerText.split(",");
    for (var i = 0; i < criteriaIds.length; ++i) {
        var scoreInputId = "score-input-" + criteriaIds[i];
        document.getElementById(scoreInputId).value = 0;
    }
    select.selectedIndex = -1;
}

function initCriteriasTable() {
    var juryId = document.getElementById("jury-id").innerText;
    var url = "/jurys/" + juryId + "/criterias";
    var req = new XMLHttpRequest();
    req.overrideMimeType("application/json");
    req.open('GET', url, true);
    req.onload = function() {
        var jsonResponse = JSON.parse(req.responseText);
        if (jsonResponse['success']) {
            var html = "";
            var tableBody = document.getElementById("criteria-score-table-body");
            var content = jsonResponse['content'];
            var criteriaIdsList = [];
            for (var i = 0; i < content.length; ++i) {
                var criteriaEntityJson = content[i];
                var criteriaId = criteriaEntityJson['criteriaId'];
                criteriaIdsList.push(criteriaId);
                html += ("<tr id='criteria-row-" + criteriaId + "'>");
                html += ("<td>" + criteriaEntityJson['content1'] + "</td>");
                html += ("<td>" + criteriaEntityJson['content2'] + "</td>");
                html += ("<td>" + criteriaEntityJson['content3'] + "</td>");
                html += ("<td>" + criteriaEntityJson['type'] + "</td>");
                html += ("<td>" + criteriaEntityJson['totalGrade'] + "</td>");
                html += ("<td id='part-score-" + criteriaId + "'>" + criteriaEntityJson['partGrade'] + "</td>");
                html += ("<td>" + criteriaEntityJson['illustration'] + "</td>");
                html += ("");
                html += ("<td><div><input type=number class='form-control' style='width:100px' placeholder='必填' min='0' max='" + criteriaEntityJson['partGrade'] + "' id='score-input-" + criteriaId + "'></div></td></tr>");
            }
            document.getElementById("criteria-ids").innerText = criteriaIdsList.join(",");
            tableBody.innerHTML = html;
        } else {
            toastr.error(jsonResponse['msg']);
        }
    };
    req.send();
}

/**
 * 提交分数
 */
function submitScores() {

    var juryId = document.getElementById("jury-id").innerText;
    var productId = document.getElementById("product-id").innerText;
    if (productId == "") {
        toastr.error('请先选择作品');
        return;
    }
    var criteriaIds = document.getElementById("criteria-ids").innerText.split(",");
    var scoreDtoList = [];
    var checked = true;
    for (var i = 0; i < criteriaIds.length; ++i) {
        var criteriaId = criteriaIds[i];
        var partScoreId = "part-score-" + criteriaId;
        var scoreInputId = "score-input-" + criteriaId;
        var partScore = parseFloat(document.getElementById(partScoreId).innerText);
        var score = parseFloat(document.getElementById(scoreInputId).value);
        if (isNaN(score) || score > partScore || score < 0) {
            document.getElementById(scoreInputId).parentNode.className = "has-error";
            checked = false;
        } else {
            document.getElementById(scoreInputId).parentNode.className = "";
            var item = {
                productId: parseInt(productId),
                criteriaId: parseInt(criteriaId),
                score: score
            };
            scoreDtoList.push(item);
        }
    }
    if (!checked) {
        return;
    }

    var url  = "/jurys/" + juryId + "/scores";
    var req = new XMLHttpRequest();
    req.overrideMimeType("application/json");
    req.open('POST', url, true);
    req.setRequestHeader('content-type', 'application/json');
    req.onload = function() {

        var jsonResponse = JSON.parse(req.responseText);
        if (jsonResponse['content'] == true) {
            toastr.success('评分成功');
            var unJudgedSelect = document.getElementById("unJudged-products-select");
            var unJudgedOptions = unJudgedSelect.options;
            for (var i = 0; i < unJudgedOptions.length; ++i) {
                if (unJudgedOptions[i].value == productId) {
                    unJudgedSelect.remove(i);
                }
            }
            var judgedSelect = document.getElementById("judged-products-select");
            var judgedOptions = judgedSelect.options;
            var judgedOptionExist = false;
            for (var i = 0; i < judgedOptions.length; ++i) {
                if (judgedOptions[i].value == productId) {
                    judgedOptionExist = true;
                    break;
                }
            }
            if (!judgedOptionExist) {
                var option = document.createElement("option");
                option.text = "已评作品" + productId;
                option.value = productId;
                judgedSelect.add(option);
            }


            var criteriaIds = document.getElementById("criteria-ids").innerText.split(",");
            for (var i = 0; i < criteriaIds.length; ++i) {
                var scoreInputId = "score-input-" + criteriaIds[i];
                document.getElementById(scoreInputId).value = 0;
            }
        } else {
            window.location.href="/logout";
        }

    };
    req.send(JSON.stringify(scoreDtoList));

}

function finishJudge() {
    var juryId = document.getElementById("jury-id").innerText;
    var url = "/jurys/" + juryId + "/account";
    var req = new XMLHttpRequest();
    req.overrideMimeType("application/json");
    req.open('GET', url, true);
    req.onload = function() {
        var jsonResponse = JSON.parse(req.responseText);
        if (jsonResponse['content'] == false) {
            toastr.warn(jsonResponse['msg']);
        } else {
            window.location.href="/logout";
        }
    };
    req.send();
}