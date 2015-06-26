function principalsToArray(prncpls) {
    var ary = prncpls.map(function(obj, i){
        var principalName = Object.keys(obj)[0]
        return [principalName, obj[principalName]]
    })
    return ary
}

function setupPrincipalChart(data, idName) {
    var cols = principalsToArray(data)

    var chart = c3.generate({
        data: {
            type: 'bar',
            columns: cols
        },
        bindto: "#"+idName
    })

    return chart;
}

var uncompletedLimit = 5;
var completedLimit = 5;
var uncompletedActionRequests;
var completedActionRequests;

$.get("/kfs-snd/docStats/uncompletedActionRequestsByPrincipalName?limit="+uncompletedLimit, function (response) {
    uncompletedActionRequests = setupPrincipalChart(response, "uncompleted-requests");
});

$.get("/kfs-snd/docStats/completedActionRequestsByPrincipalName?limit="+completedLimit, function (response) {
    completedActionRequests = setupPrincipalChart(response, "completed-requests");
});

$(document).ready(function () {
    $("#completed-limit-label").html(completedLimit)
    $("#completed-limit").val(completedLimit)
    $("#completed-limit").change(function (ele) {
        completedLimit = $( this ).val()
        $.get("/kfs-snd/docStats/completedActionRequestsByPrincipalName?limit="+completedLimit, function (response) {
            var cols = principalsToArray(response)
            completedActionRequests.load({columns: cols})
            $("#completed-limit-label").html(completedLimit)
        });
    });

    $("#uncompleted-limit-label").html(uncompletedLimit)
    $("#uncompleted-limit").val(uncompletedLimit)
    $("#uncompleted-limit").change(function (ele) {
        uncompletedLimit = $( this ).val()
        $.get("/kfs-snd/docStats/uncompletedActionRequestsByPrincipalName?limit="+uncompletedLimit, function (response) {
            var cols = principalsToArray(response)
            uncompletedActionRequests.load({columns: cols})
            $("#uncompleted-limit-label").html(uncompletedLimit)
        });
    });
})