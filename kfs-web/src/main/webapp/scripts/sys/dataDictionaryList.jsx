import React from 'react';
import Griddle from 'griddle-react';
import $ from 'jQuery';

var Root = React.createClass({
    getInitialState: function() {
        return {entries: []}
    },
    componentWillMount: function() {
        $.ajax({
            url: this.props.url,
            dataType: 'json',
            type: 'GET',
            success: function(entries) {
                this.setState({entries: entries});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        })
    },
    rowGetter: function(rowIndex) {
        return this.state.entries[rowIndex]
    },
    render: function() {
        console.log(this.state.entries.length);
        return (
            <Griddle
                results={this.state.entries}
                tableClassName="table"
                showFilter={true}
                showSettings={true}
                columns={["edit", "namespace", "className", "details"]}
                resultsPerPage={20}
                columnMetadata={columnMeta}
                useGriddleStyles={false}
                />
        )
    }
})

var LinkComponent = React.createClass({
    render: function(){
        return <a href={this.props.data.link}>view more</a>
    }
});

var EditComponent = React.createClass({
    render: function(){
        return <a href="">edit</a>
    }
});

var columnMeta = [
    {
        "columnName": "edit",
        "order": 1,
        "locked": false,
        "visible": true,
        "customComponent": EditComponent
    },
    {
        "columnName": "namespace",
        "order": 2,
        "locked": false,
        "visible": true
    },
    {
        "columnName": "className",
        "order": 3,
        "locked": false,
        "visible": true
    },
    {
        "columnName": "details",
        "order": 4,
        "locked": false,
        "visible": true,
        "customComponent": LinkComponent
    }
];

React.render(
    <Root url="/kfs-dev/core/datadictionary/businessObjectEntry" />,
    document.getElementById('main')
)