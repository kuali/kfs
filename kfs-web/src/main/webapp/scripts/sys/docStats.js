$.get("/kfs-dev/docStats/initiatedDocumentsByDocumentType", function(data) {
    var categories = data.map(function (obj, i) {
        return Object.keys(obj)[0];
    });

    var columns = data.map(function (obj, i) {
        return obj[Object.keys(obj)[0]];
    });

    var pieColumns = columns.map(function (e, i) {
        return [categories[i], columns[i]];
    });

    columns.unshift('initiated docs');

    var chart = c3.generate({
        bindto: '#chart',
        data: {
            columns: [
                columns
            ]
        },
        axis: {
            x: {
                type: 'category',
                categories: categories
            }
        }
    });

    var pie = c3.generate({
        bindto: '#pie',
        data: {
            columns: pieColumns,
            type : 'donut'
        },
        tooltip: {
            format: {
                title: function (d) { return d; },
                value: function (value, ratio, id) {
                    return value;
                }
            }
        }
    });
});

$.get("/kfs-dev/docStats/reportNumDocsByStatusByDocType", function (response) {
    var data = response;
    var first = data[0];

    var categoriesMap = first[Object.keys(first)[0]];
    var categories = [];

    for (var key in categoriesMap) {
        if (categoriesMap.hasOwnProperty(key)) {
            categories.push(key);
        }
    }

    var columnsArray = [];

    for (var i=0; i<data.length; i++) {
        var columns = [];
        var obj = data[i];
        var docType = Object.keys(obj)[0];
        var counts = obj[Object.keys(obj)[0]];

        columns.push(docType);
        for (var key in counts) {
            if (counts.hasOwnProperty(key)) {
                columns.push(counts[key]);
            }
        }
        columnsArray[i] = columns;
    }

    var top5chart = c3.generate({
        bindto: '#top5chart',
        data: {
            columns: columnsArray
        },
        axis: {
            x: {
                type: 'category',
                categories: categories.sort()
            }
        }
    });

});

$.get("/kfs-dev/docStats/documentsStatus", function(data) {
    var statusMapping = {"X": "CANCELED", "D": "Disapproved", "R": "Enroute", "E": "Exception", "F": "Final", "I": "Initiated", "P": "Processed", "S": "Saved"};
    var pieColumns = [];
    for (var key in data) {
        if (data.hasOwnProperty(key)) {
            pieColumns.push([statusMapping[key], data[key]]);
        }
    }

    var pie = c3.generate({
        bindto: '#docs-status-pie',
        data: {
            columns: pieColumns,
            type : 'donut'
        },
        tooltip: {
            format: {
                title: function (d) { return d; },
                value: function (value, ratio, id) {
                    return value;
                }
            }
        }
    });
});

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
    var sortedPrincipals = principalsSortedByCount(data)

    var chart = c3.generate({
        data: {
            type: 'bar',
            columns: cols,
            groups: [
                ["COMPLETE","APPROVE","ACKNOWLEDGE","FYI"]
            ]
        },
        axis: {
            x: {
                type: 'category',
                categories: sortedPrincipals
            }
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
            var sortedPrincipals = principalsSortedByCount(response)
            chart.load({columns: cols})
            chart.load({categories: sortedPrincipals})
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
        },
        tooltip: {
            format: {
                title: function (d) { return d; },
                value: function (value, ratio, id) {
                    return value;
                }
            }
        }
    })
    return chart;
}

$.get("/kfs-dev/docStats/uncompletedActionRequestsByPrincipalName?limit=5", function (response) {
    var uncompletedActionRequests = setupPrincipalChart(response, "uncompleted-requests")
    setupChangeResponse("uncompleted-limit", uncompletedActionRequests, 5, "/kfs-dev/docStats/uncompletedActionRequestsByPrincipalName")
})

$.get("/kfs-dev/docStats/completedActionRequestsByPrincipalName?limit=5", function (response) {
    var completedActionRequests = setupPrincipalChart(response, "completed-requests")
    setupChangeResponse("completed-limit",completedActionRequests, 5,"/kfs-dev/docStats/completedActionRequestsByPrincipalName")
})

$.get("/kfs-dev/docStats/uncompletedActionRequestsByType", function (response) {
    var uncompletedActionRequestsByType = setupTypeChart(response, "uncompleted-requests-by-type")
})
$.get("/kfs-dev/docStats/completedActionRequestsByType", function (response) {
    var completedActionRequestsbyType = setupTypeChart(response, "completed-requests-by-type")
})