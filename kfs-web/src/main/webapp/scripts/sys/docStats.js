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
            type : 'pie'
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
            type : 'pie'
        }
    });
});

