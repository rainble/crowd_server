<%--
  Created by IntelliJ IDEA.
  User: sunruoyu
  Date: 2018/11/8
  Time: 4:59 PM
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userId = request.getParameter("userId");
%>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>Publish Task</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 shim 和 Respond.js 是为了让 IE8 支持 HTML5 元素和媒体查询（media queries）功能 -->
    <!-- 警告：通过 file:// 协议（就是直接将 html 页面拖拽到浏览器中）访问页面时 Respond.js 不起作用 -->
    <!--[if lt IE 9]>
    <script src="https://cdn.jsdelivr.net/npm/html5shiv@3.7.3/dist/html5shiv.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/respond.js@1.4.2/dest/respond.min.js"></script>
    <![endif]-->


    <script src="js/TaskController.js"></script>

    <script>
        function getProcess() {
            console.log(12355);
            $.ajax({
                url: "http://localhost:8080/task/getOntologyService",
                type: "post",
                async: false,
                // data:{"userId":5,"puid":5},
                data:"userId=5&&puid=5",
                error: function (xhr, status, errorThrown) {
                    console.log("Error: " + errorThrown);
                    console.log("Status: " + status);
                    console.log(xhr);
                },
                success: function (json) {
                    console.log(json.toString());
                    for (var i in json) {
                        console.log(json[i].serviceName);
                        $.ajax({
                            url: "http://localhost:8080/task/saveTaskByServiceIdAndUserId",
                            type: "post",
                            async: false,
                            // data:{"serviceId":json[i].serviceName,"userId":5,"taskName":json[i].serviceTitle},
                            data:"serviceId="+json[i].serviceName+"&&userId=5&&taskName="+json[i].serviceTitle+"",
                            error: function (xhr, status, errorThrown) {
                                console.log("Error: " + errorThrown);
                                console.log("Status: " + status);
                                console.log(xhr);
                            },
                            success: function (jsonn) {
                                var arr = jsonn.split("=");
                                console.log(arr[1]);
                                $("#detail-panel").append("<div class='radio'><label><input type='radio' name='optionsRadios' id='optionsRadios1' value='"+arr[1]+"' checked>" +
                                    "This service's name is [ "+json[i].serviceName+" ], you can user this service to [ "+json[i].serviceTitle+" ].</label></div>  ")
                            }
                        });
                    }
                }
            });
        }
    </script>

</head>

<body>
<!-- jQuery (Bootstrap 的所有 JavaScript 插件都依赖 jQuery，所以必须放在前边) -->
<script src="https://cdn.jsdelivr.net/npm/jquery@1.12.4/dist/jquery.min.js"></script>
<!-- 加载 Bootstrap 的所有 JavaScript 插件。你也可以根据需要只加载单个插件。 -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/js/bootstrap.min.js"></script>



<%--导航栏--%>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Brand</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Link <span class="sr-only">(current)</span></a></li>
                <li><a href="#">Link</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Dropdown <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="index.jsp">Back</a></li>
                        <li><a href="#">Another action</a></li>
                        <li><a href="#">Something else here</a></li>
                        <li role="separator" class="divider"></li>
                        <li><a href="#">Separated link</a></li>
                        <li role="separator" class="divider"></li>
                        <li><a href="#">One more separated link</a></li>
                    </ul>
                </li>
            </ul>
            <form class="navbar-form navbar-left">
                <div class="form-group">
                    <input type="text" class="form-control" placeholder="Search">
                </div>
                <button type="submit" class="btn btn-default">Submit</button>
                <button type="button" class="btn btn-default" onclick="addMyTask(<%=userId%>)">Refresh</button>
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#ProcessDisplay" onclick="getProcess()">Process</button>
                <button type="button" class="btn btn-default" onclick="getProcess()">Function Test</button>
            </form>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="#">Link</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Dropdown <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="#">Action</a></li>
                        <li><a href="#">Another action</a></li>
                        <li><a href="#">Something else here</a></li>
                        <li role="separator" class="divider"></li>
                        <li><a href="#">Separated link</a></li>
                    </ul>
                </li>
            </ul>
        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
</nav>

<div class="modal fade" id="ProcessDisplay" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Modal title</h4>
            </div>
            <div class="modal-body" id="detail-panel">



                <%--测试用--%>
                    <div class="radio">
                        <label>
                            <input type="radio" name="optionsRadios" id="optionsRadios1" value="0" checked>
                            Option one is this and that&mdash;be sure to include why it's great
                        </label>
                    </div>
                    <div class="radio">
                        <label>
                            <input type="radio" name="optionsRadios" id="optionsRadios2" value="135">
                            Option two can be something else and selecting it will deselect option one
                        </label>
                    </div>
                    <div class="radio disabled">
                        <label>
                            <input type="radio" name="optionsRadios" id="optionsRadios3" value="2" disabled>
                            Option three is disabled
                        </label>
                    </div>

                <%--<script>--%>
                    <%--function getProcess() {--%>
                        <%--$.ajax({--%>
                            <%--url: "http://localhost:8080/task/getOntologyService",--%>
                            <%--type: "post",--%>
                            <%--async: false,--%>
                            <%--data:{"userId":"<%=userId%>"},--%>
                            <%--error: function (xhr, status, errorThrown) {--%>
                                <%--console.log("Error: " + errorThrown);--%>
                                <%--console.log("Status: " + status);--%>
                                <%--console.log(xhr);--%>
                            <%--},--%>
                            <%--success: function (json) {--%>
                                <%--var obj = JSON.parse(json);--%>
                                <%--for (var i in obj) {--%>
                                    <%--$("#detail-panel").html("<tr><td>i</td><td></td><td><button type='button' class='btn btn-default' onclick=''  ");--%>
                                <%--}--%>
                            <%--}--%>
                        <%--});--%>
                    <%--}--%>
                <%--</script>--%>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" onclick="jump()">Confirm</button>
                <script>
                    function jump() {
                        var id = $("input[name='optionsRadios']:checked").val();
                        window.location='TaskDisplay.jsp?taskId='+id+'';
                    }

                </script>
            </div>
        </div>
    </div>
</div>



<%--展示用的表格--%>
<div class="col-md-6">
    <table class="table table-striped">
        <thead>
        <tr>
            <th>#</th>
            <th>Publisher ID</th>
            <th>Task Describe</th>
            <th>Task Detail</th>
            <th>Complete Task</th>
        </tr>
        </thead>
        <tbody id="task_table">
        <tr>
            <td>1</td>
            <td>yufan</td>
            <td>play board game</td>
        </tr>

        </tbody>
    </table>
</div>




</body>
</html>
