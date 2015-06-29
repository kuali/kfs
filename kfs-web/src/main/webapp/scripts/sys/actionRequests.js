function principalsSortedByCount(principals) {
    var principalTotals = Object.keys(principals).map(function(principalName) {
        var countsForPrincipal =  principals[principalName]
        var sum = Object.keys(countsForPrincipal).reduce(function (sum, actionRequestType) {
            var count = countsForPrincipal[actionRequestType]
            return sum + count
        }, 0)
        return {principalName : principalName, sum : sum}
    })
    principalTotals.sort(function (principalTotalA, principalTotalB) {
        var countA = principalTotalA["sum"]
        var countB = principalTotalB["sum"]
        return countB - countA
    })
    var sortedPrincipals = principalTotals.map(function (ele) {
        return ele["principalName"]
    })
    return sortedPrincipals
}

function principalsToArray(principals) {
    function pushResult(m, key, a) {
        var count = m[key]
        if (count) {
            a.push(count)
        } else {
            a.push(0)
        }
    }

    var sortedPrincipals = principalsSortedByCount(principals)
    var completes = ["COMPLETE"]
    var approves = ["APPROVE"]
    var acknowledgements = ["ACKNOWLEDGE"]
    var fyis = ["FYI"]
    sortedPrincipals.forEach(function (principalName) {
        var principalCounts = principals[principalName]
        pushResult(principalCounts, "COMPLETE", completes)
        pushResult(principalCounts, "APPROVE", approves)
        pushResult(principalCounts, "ACKNOWLEDGE", acknowledgements)
        pushResult(principalCounts, "FYI", fyis)
    })
    return [completes, approves, acknowledgements, fyis]
}

function setupPrincipalChart(data, idName) {
    var cols = principalsToArray(data)

    var chart = c3.generate({
        data: {
            type: 'bar',
            columns: cols,
            groups: [
                ["COMPLETE","APPROVE","ACKNOWLEDGE","FYI"]
            ]
        },
        bindto: "#"+idName,
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