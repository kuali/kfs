function setupPrincipalChart(data, idName) {
    var cols = data.map(function(obj, i){
        var principalName = Object.keys(obj)[0]
        return [principalName, obj[principalName]]
    })

    var chart = c3.generate({
        data: {
            type: 'bar',
            columns: cols
        },
        bindto: "#"+idName
    })

    return chart;
}

$.get("/kfs-snd/docStats/uncompletedActionRequestsByPrincipalName", function (response) {
    var uncompletedActionRequests = setupPrincipalChart(response, "uncompleted-requests");
});

$.get("/kfs-snd/docStats/completedActionRequestsByPrincipalName", function (response) {
    var completedActionRequests = setupPrincipalChart(response, "completed-requests");
});

