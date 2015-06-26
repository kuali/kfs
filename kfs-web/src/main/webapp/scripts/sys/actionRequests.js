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

function setupChangeResponse(divName, chart, startLimit, sourceUrl) {
    $("#"+divName+"-label").html(startLimit)
    $("#"+divName).val(startLimit)
    $("#"+divName).change(function (ele) {
        var updatedLimit = $(this).val()
        $.get(sourceUrl+"?limit=" + updatedLimit, function (response) {
            var cols = principalsToArray(response)
            chart.unload()
            chart.load({columns: cols})
            $("#"+divName+"-label").html(updatedLimit)
        })
    })
}

function setupTypeChart(data, divName) {
    var cols = Object.keys(data).map(function (ele, idx) {
        return [ele, data[ele]]
    })
    var chart = c3.generate({
        bindto: "#"+divName,
        data: {
            type: 'donut',
            columns: cols
        }
    })
    return chart;
}

$.get("/kfs-snd/docStats/uncompletedActionRequestsByPrincipalName?limit=5", function (response) {
    var uncompletedActionRequests = setupPrincipalChart(response, "uncompleted-requests")
    setupChangeResponse("uncompleted-limit", uncompletedActionRequests, 5, "/kfs-snd/docStats/uncompletedActionRequestsByPrincipalName")
})

$.get("/kfs-snd/docStats/completedActionRequestsByPrincipalName?limit=5", function (response) {
    var completedActionRequests = setupPrincipalChart(response, "completed-requests")
    setupChangeResponse("completed-limit",completedActionRequests, 5,"/kfs-snd/docStats/completedActionRequestsByPrincipalName")
})

$.get("/kfs-snd/docStats/uncompletedActionRequestsByType", function (response) {
    var uncompletedActionRequestsByType = setupTypeChart(response, "uncompleted-requests-by-type")
})
$.get("/kfs-snd/docStats/completedActionRequestsByType", function (response) {
    var completedActionRequestsbyType = setupTypeChart(response, "completed-requests-by-type")
})