$.get("/kfs-dev/docStats/initiatedDocumentsByDocumentType", function (response) {
    var data = response;

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

