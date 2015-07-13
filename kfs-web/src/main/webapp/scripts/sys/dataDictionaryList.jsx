import React from 'react';
import Griddle from 'griddle-react';
import $ from 'jQuery';
import Router from 'react-router';
import { DefaultRoute, HashHistory, Link, Route, RouteHandler, Navigation } from 'react-router';

var Root = React.createClass({
    getInitialState: function() {
        return {entries: []}
    },
    componentWillMount: function() {
        $.ajax({
            url: getUrlPathPrefix("/sys/DataDictionaryList.html") + "/core/datadictionary/businessObjectEntry",
            dataType: 'json',
            type: 'GET',
            success: function(entries) {
                this.setState({entries: entries});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })
    },
    rowGetter: function(rowIndex) {
        return this.state.entries[rowIndex]
    },
    render: function() {
        return (
            <Griddle
                results={this.state.entries}
                tableClassName="table"
                showFilter={true}
                showSettings={true}
                columns={["edit", "namespace", "className", "label", "key", "details"]}
                resultsPerPage={20}
                columnMetadata={columnMeta}
                useGriddleStyles={false}
                />
        )
    }
})

var LinkComponent = React.createClass({
    render: function(){
        return <Link to="detail" params={{name: this.props.rowData.key, editable: false}}>view more</Link>
    }
});

var EditComponent = React.createClass({
    render: function(){
        return <Link to="detail" params={{name: this.props.rowData.key, editable: true}}>edit</Link>
    }
});

var columnMeta = [
    {
        "columnName": "edit",
        "customComponent": EditComponent
    },
    {
        "columnName": "details",
        "customComponent": LinkComponent
    }
];

var Detail = React.createClass({
    getInitialState: function() {
        return {entry: {}}
    },
    componentWillMount: function() {
        $.ajax({
            url: getUrlPathPrefix("/sys/DataDictionaryList.html") + "/core/datadictionary/businessObjectEntry/" + this.props.params.name,
            dataType: 'json',
            type: 'GET',
            success: function(entry) {
                this.setState({entry: entry});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })
    },
    updateFieldValue: function(prefix, name, event) {
        var s = {}
        s.entry = this.state.entry
        if (prefix) {
            setValue(prefix + '.' + name, event.target.value, s.entry)
        } else {
            s.entry[name] = event.target.value
        }

        this.setState(s)
    },
    updateBusinessObjectEntry: function() {
        $.ajax({
            url: getUrlPathPrefix("/sys/DataDictionaryList.html") + "/core/datadictionary/businessObjectEntry/" + this.props.params.name,
            dataType: 'json',
            contentType: 'application/json',
            type: 'PUT',
            data: JSON.stringify(this.state.entry),
            success: function() {
                alert('Congrats')
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })

    },
    render: function() {
        var prefix;
        var fields = buildFieldArray(prefix, this.state.entry, this.props.params.editable, this.updateFieldValue)
        var updateButton;
        if (this.props.params.editable && this.props.params.editable  === "true") {
            updateButton = <button type="button" onClick={this.updateBusinessObjectEntry.bind(this)}>Update</button>;
        }
        return (
            <div>
                <Link to="table">go back</Link>
                <table>
                    {fields}
                </table>
                {updateButton}
            </div>
        )
    }
})

function setValue(path, val, obj) {
    var fields = path.split('.');
    var result = obj;
    for (var i = 0, n = fields.length; i < n && result !== undefined; i++) {
        var field = fields[i];
        if (i === n - 1) {
            result[field] = val;
        } else {
            if (typeof result[field] === 'undefined' || typeof result[field] !== 'object') {
                result[field] = {};
            }
            result = result[field];
        }
    }
}

function buildFieldArray(prefix, map, editable, updateFieldValue) {
    var fields = [];
    for (var key in map) {
        if (map.hasOwnProperty(key)) {
            fields.push(<FormField prefix={prefix} name={key} value={map[key]} editable={editable} updateFieldValue={updateFieldValue}/>)
        }
    }
    return fields;
}

var FormField = React.createClass({
    render: function() {
        var attributeValue
        if (this.props.name === "attributes" || this.props.name === "inquiryFields" || this.props.name === "lookupFields" || this.props.name === "resultFields") {
            attributeValue = <AttributeTable prefix={this.props.name} attributes={this.props.value} editable={this.props.editable} updateFieldValue={this.props.updateFieldValue}/>
        } else if (this.props.name === "defaultSort") {
            var fields = buildFieldArray(this.props.name, this.props.value, this.props.editable, this.props.updateFieldValue)
            attributeValue = <table>{fields}</table>
        } else if (this.props.name === "inquirySections") {
            var attributeValues = [];
            for (var i=0;i<this.props.value.length;i++) {
                var fields = buildFieldArray(this.props.name, this.props.value[i], this.props.editable, this.props.updateFieldValue)
                attributeValues.push(<tr><td><table>{fields}</table></td></tr>)
            }
            attributeValue = <table>{attributeValues}</table>
        } else if (this.props.name === "inquiryDefinition" || this.props.name === "lookupDefinition") {
            var fields = buildFieldArray(this.props.name, this.props.value, this.props.editable, this.props.updateFieldValue)
            attributeValue = <table>{fields}</table>
        } else {
            var prefix
            attributeValue = <InputField prefix={prefix} editable={this.props.editable} value={this.props.value} name={this.props.name} updateFieldValue={this.props.updateFieldValue}/>
        }
        return (
            <tr>
                <td><label>{this.props.name}</label></td>
                <td>{attributeValue}</td>
            </tr>
        )

    }
})

var AttributeTable = React.createClass({
    render: function() {
        var fields = [];
        if (this.props.attributes.length > 0) {
            fields.push(<AttributeLabelField attribute={this.props.attributes[0]}/>)
        }
        for (var i=0; i<this.props.attributes.length; i++) {
            var prefix = this.props.prefix + '.' + i
            fields.push(<AttributeFormField prefix={prefix} attribute={this.props.attributes[i]} editable={this.props.editable} updateFieldValue={this.props.updateFieldValue}/>)
        }
        return (
            <table>
                {fields}
            </table>
        )
    }
})

var AttributeLabelField = React.createClass({
    render: function() {
        var labels = [];
        for (var key in this.props.attribute) {
            if (this.props.attribute.hasOwnProperty(key)) {
                labels.push(<th>{key}</th>)
            }
        }
        return (
            <tr>
                {labels}
            </tr>
        )
    }
})

var AttributeFormField = React.createClass({
    render: function() {
        var fields = [];
        for (var key in this.props.attribute) {
            if (this.props.attribute.hasOwnProperty(key)) {
                if (key === "control") {
                    var prefix = this.props.prefix + "." + key
                    fields.push(<td><AttributeTable prefix={prefix} editable={this.props.editable} attributes={[this.props.attribute[key]]} updateFieldValue={this.props.updateFieldValue}/></td>)

                } else {
                    var attributeValue = <InputField prefix={this.props.prefix} editable={this.props.editable} value={this.props.attribute[key]} name={key} updateFieldValue={this.props.updateFieldValue}/>
                    fields.push(<td>{attributeValue}</td>)
                }
            }
        }
        return (
            <tr>
                {fields}
            </tr>
        )
    }
})

var InputField = React.createClass({
    render: function() {
        if (!this.props.editable || this.props.editable === 'false') {
            return typeof this.props.value === "boolean" ? this.props.value.toString() : this.props.value
        } else {
            var type = "text"
            if (typeof this.props.value === "boolean") {
                type = "checkbox"
            }
            return <input type={type} value={this.props.value} checked={this.props.value} onChange={this.props.updateFieldValue.bind(this, this.props.prefix, this.props.name)}/>
        }
    }
})

function getUrlPathPrefix(page) {
    var path = new URL(window.location.href).pathname;
    var index = path.indexOf(page);
    return path.substring(0, index);
}

var App = React.createClass({
    render: function() {
        return (
            <div>
                <RouteHandler/>
            </div>
        )
    }
});

let routes = (
    <Route handler={App}>
        <Route name="table" path="/" handler={Root}/>
        <Route name="detail" path="/businessObjectEntry/:name/:editable" handler={Detail}/>
    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.getElementById('main'));
});