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

    var chart = c3.generate({
        bindto: '#chart',
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

