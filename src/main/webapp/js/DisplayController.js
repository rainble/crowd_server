
var taskNodeList;
var taskEdgeList;
var url = "http://localhost:8080/task/getNodeAndEdgeByTaskId";

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
function getTaskId() {
    var taskId = getQueryVariable("taskId");
    return taskId;
}

setInterval(function () {
    $.ajax({
        type:"post",
        url:url,
        data: {"taskId":getTaskId()},
        error: function (msg) {
            alert(msg);
            console.log(msg);
        },
        success: function (result) {
            taskNodeList = result.taskNodeList;
            taskEdgeList = result.taskEdgeList;
            showGraph();
        }
    })
}, 5000);



// Create the input graph
var g = new dagreD3.graphlib.Graph()
    .setGraph({})
    .setDefaultEdgeLabel(function () {
        return {};
    });
// Create the renderer
var render = new dagreD3.render();
// Add our custom shape (a house)
render.shapes().house = function(parent, bbox, node) {
    var w = bbox.width,
        h = bbox.height,
        points = [
            { x:   0, y:   -h/3},
            { x:   w/2, y:   0},
            { x:   w, y:   -h/3},
            { x:   w/2, y:   -h*2/3},
        ];
    shapeSvg = parent.insert("polygon", ":first-child")
        .attr("points", points.map(function(d) { return d.x + "," + d.y; }).join(" "))
        .attr("transform", "translate(" + (-w/2) + "," + (h /3) + ")");

    node.intersect = function(point) {
        return dagreD3.intersect.polygon(node, points, point);
    };

    return shapeSvg;
};

// Set up an SVG group so that we can translate the final graph.
//    var svg = d3.select("svg"),
//        svgGroup = svg.append("g");
var svg = d3.select("svg"),
    inner = svg.select("g");


// Set up zoom support
var zoom = d3.zoom().on("zoom", function () {
    inner.attr("transform", d3.event.transform);
});
svg.call(zoom);

function  showActionSheet(node) {
    var selectedNode=g.node(node);
    if(selectedNode.class.indexOf("warn")>=0){
        weui.actionSheet([
            {
                label: '查看节点需要的反馈',
                onClick: function () {
                    console.log('拍照');
                }
            }, {
                label: '查看节点详细信息',
                onClick: function () {
                    console.log('从相册选择');
                }
            },
        ], [
            {
                label: '取消',
                onClick: function () {
                    console.log('取消');
                }
            }
        ], {
            className: 'custom-classname',
            onClose: function(){
                console.log('关闭');
            }
        });
    }else if(selectedNode.class.indexOf("completed")>=0){
        weui.actionSheet([
            {
                label: '查看完成时间',
                onClick: function () {
                    console.log('从相册选择');
                }
            }, {
                label: '查看节点详情',
                onClick: function () {
                    console.log('其他');
                }
            }
        ], [
            {
                label: '取消',
                onClick: function () {
                    console.log('取消');
                }
            }
        ], {
            className: 'custom-classname',
            onClose: function(){
                console.log('关闭');
            }
        });
    }

}

function showGraph() {
    var windowWidth=$(window).width();
    var windowHeight=$(window).height();
    svg.attr('width',windowWidth);
    svg.attr('height',windowHeight);
    // Here we"re setting nodeclass, which is used by our custom drawNodes function
    // below.
    $.each(taskNodeList, function (i, item) {
        var className = item.style;
        var description = item.completeTime;
        if(item.state==1){
            //未完成，并警告
            className += " completed";
        }if(item.state==2){
            //未完成，并警告
            className += " warn";
        }else {
            description="未完成";
        }
        if((item.style=="StartEvent")||(item.style=="EndEvent")){
            g.setNode(item.id, {shape: "circle",label: "  ", class: className,description: description});
        }else if(item.style=="ParallelGateway"){
            g.setNode(item.id, {shape: "house",label: "+", class: className,description: description});
        }else if(item.style=="ExclusiveGateway"){
            g.setNode(item.id, {shape: "house",label: "X", class: className,description: description});
        }else {
            g.setNode(item.id, {shape: "rect",label: item.label, class: className,description: description});
        }
    });

    // Set up edges, no special attributes.
    $.each(taskEdgeList, function (i, item) {
        g.setEdge(item.parent, item.child, {label: item.label,curve: d3.curveBasis});
    });

    g.nodes().forEach(function (v) {
        var node = g.node(v);
        // Round the corners of the nodes
        node.rx = node.ry = 5;
    });

    // Run the renderer. This is what draws the final graph.
    //render(d3.select("svg g"), g);
    render(inner, g);

    //set Tooltip on Hover
    inner.selectAll("g.node").on('click', function (node) { showActionSheet(node); });

    // Center the graph
    // Zoom and scale to fit
    var graphWidth = g.graph().width + 80;
    var graphHeight = g.graph().height + 40;
    var width = parseInt(svg.style("width").replace(/px/, ""));
    var height = parseInt(svg.style("height").replace(/px/, ""));
    var zoomScale = Math.min(width / graphWidth, height / graphHeight);
    var translateX = (width / 2) - ((graphWidth * zoomScale) / 2)
    var translateY = (height / 2) - ((graphHeight * zoomScale) / 2);
    var svgZoom = true ? svg.transition().duration(2000) : svg;
    svgZoom.call(zoom.transform, d3.zoomIdentity.translate(translateX, translateY).scale(zoomScale));


    //var initialScale = 0.75;
    //svg.call(zoom.transform, d3.zoomIdentity.translate((svg.attr("width") - g.graph().width * initialScale) / 2, 20).scale(initialScale));
    //svg.attr('height', g.graph().height * initialScale + 40);
}


