


$(document).ready(function () {
    $('[data-toggle="popover"]').popover();
    addNewTask();
});


function addNewTask() {
    console.log("test for ajax");
    // var oXL = new ActiveXObject("Application");
    // oXL.visibility = true;
    // var oWB = oXL.Workbooks.Add();
    // var oSheet = oWB.ActiveSheet;
    var tmp = 0;
    $.ajax({
        url:"http://localhost:8081/GetSimpleTaskServlet",
        type:"get",
        async:false,
        dataType:"jsonp",
        jsonp:"callback",
        jsonpCallback:"data",
        success:function (json) {
            var data = json.data;
            var obj = JSON.parse(data);
            // console.log(obj.simpleTaskVOS[0].taskDesc);
            // var simpleTaskVOs = data.simpleTaskVOS;
            // alert(simpleTaskVOs);
            // alert(data2);

            for (var i in obj.simpleTaskVOS)
            {
                console.log(obj.simpleTaskVOS[i].taskDesc);
                var detail = "location:" + obj.simpleTaskVOS[i].locationDesc + " bonus:" + obj.simpleTaskVOS[i].bonus + " PublishTime:" + obj.simpleTaskVOS[i].publishTime
                + " Duration:" + obj.simpleTaskVOS[i].duration;
                console.log(detail);
                var buttonId = "detail-button" + i;
                console.log(buttonId);
                $("#task_table").append("<tr><td>" + i + "</td><td>" + obj.simpleTaskVOS[i].userId + "</td><td>" + obj.simpleTaskVOS[i].taskDesc
                    + "</td><td><button type='button' id='"+buttonId+"' class='btn btn-default' data-container='body' data-toggle='popover' data-placement = 'right' " +
                    " >task detail</button></td><td><button type='button' id='accept' class='btn btn-default' onclick='accept(15/*回来要改成真正的userId，这个数是编的，可以从前端控件里get*/, "+obj.simpleTaskVOS[i].taskId+")'>Accept</button> </td></tr>");
                // $("#task_table").append("<button type=\"button\" class=\"btn btn-lg btn-danger\" data-toggle=\"popover\" title=\"Popover title\" data-content=\"And here's some amazing content. It's very engaging. Right?\">点我弹出/隐藏弹出框</button>")
                // var test = "#" + buttonId;

                $("#"+buttonId).popover({
                    trigger:'click',
                    placement:'right',
                    content:detail,
                    animation:false,
                    title:'Detail',
                })

            }
        },
        error:function (msg) {
            console.log(msg);
        }
        
    })
}

function accept(userId, taskId) {
    $.ajax({
        url: "http://localhost:8081/AcceptSimpleTaskServlet",
        type: "post",
        async: false,
        // contentType:"application/x-www-form-urlencoded",
        data: "data={\"userId\":"+userId+", \"taskId\":"+taskId+"}",
        dataType: "jsonp",
        jsonp: "callback",
        jsonpCallback: "data",
        error: function (msg) {
            // console.log(userId + "  " + taskId)
            console.log(msg);
        },
        success: function (json) {
            // count++;
            var result = json.result;
            console.log(result);
        }
    });
}

function addMyTask(userId) {
    $.ajax({
        url: "http://localhost:8081/GetAcceptSimpleTaskServlet",
        type: "post",
        async: false,
        data: "data={\"userId\":"+userId+"}",
        dataType: "jsonp",
        jsonp: "callback",
        jsonpCallback: "data",
        error: function (msg) {
            console.log(msg);
        },
        success: function (json) {
            var data = json.data;
            var obj = JSON.parse(data);

            for (var i in obj.simpleTasks)
            {
                console.log(obj.simpleTasks[i].taskDesc);
                var detail = "location:" + obj.simpleTasks[i].locationDesc + " bonus:" + obj.simpleTasks[i].bonus + " PublishTime:" + obj.simpleTasks[i].publishTime
                    + " Duration:" + obj.simpleTasks[i].duration;
                console.log(detail);
                var buttonId = "detail-button" + i;
                console.log(buttonId);
                $("#task_table").append("<tr><td>" + i + "</td><td>" + obj.simpleTasks[i].userId + "</td><td>" + obj.simpleTasks[i].taskDesc
                    + "</td><td><button type='button' id='"+buttonId+"' class='btn btn-default' data-container='body' data-toggle='popover' data-placement = 'right' " +
                    " >task detail</button></td><td><button type='button' id='complete' class='btn btn-default' onclick='complete(15/*回来要改成真正的userId，这个数是编的，可以从前端控件里get*/, "+obj.simpleTasks[i].taskId+")'>Complete</button></td></tr>");
                // $("#task_table").append("<button type=\"button\" class=\"btn btn-lg btn-danger\" data-toggle=\"popover\" title=\"Popover title\" data-content=\"And here's some amazing content. It's very engaging. Right?\">点我弹出/隐藏弹出框</button>")
                // var test = "#" + buttonId;
                $("#"+buttonId).popover({
                    trigger:'click',
                    placement:'right',
                    content:detail,
                    animation:false,
                    title:'Detail',
                })
                $("#process").popover({
                    trigger:'click',
                    placement:'right',
                    content:getProcess(),
                    animation:false,
                    title:'Process',
                })

            }
        }
    });

}

function complete(userId, taskId) {
    $.ajax({
        url: "http://localhost:8081/CompleteSimpleTaskServlet",
        type: "post",
        async: false,
        data: "data={\"userId\":"+userId+",\"taskId\":"+taskId+"}",
        dataType: "jsonp",
        jsonp: "callback",
        jsonpCallback: "data",
        error: function (msg) {
            console.log(msg);
        },
        success: function (json) {
            var res = json.result;
            console.log(res);
        }
    });

}

//获取参数
function getQueryVariable(variable)
{
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return("");
}

function getSubProcess() {
    $.ajax({
        url: "http://localhost:8080/task/getOwlTaskList",
        type: "get",
        async: false,
        data:{"type":1},
        error: function (msg) {
            console.log(msg)
        },
        success: function (json) {

        }
    });
}


