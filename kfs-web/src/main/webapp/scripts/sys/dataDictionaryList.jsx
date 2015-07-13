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
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        })
    },
    updateFieldValue: function(name, event) {
        console.log("updating state")
        console.log(name)
        console.log(event.target.value)
        var s = {}
        s.entry = this.state.entry
        s.entry[name] = event.target.value
        this.setState(s)
    },
    updateBusinessObjectEntry: function() {
        console.log("updating boe")
        $.ajax({
            url: getUrlPathPrefix("/sys/DataDictionaryList.html") + "/core/datadictionary/businessObjectEntry/" + this.props.params.name,
            dataType: 'json',
            type: 'PUT',
            data: JSON.stringify(this.state.entry),
            success: function() {
                alert('Congrats')
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        })

    },
    render: function() {
        var fields = buildFieldArray(this.state.entry, this.props.params.editable, this.updateFieldValue)
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

function buildFieldArray(map, editable, updateFieldValue) {
    var fields = [];
    for (var key in map) {
        if (map.hasOwnProperty(key)) {
            fields.push(<FormField name={key} value={map[key]} editable={editable} updateFieldValue={updateFieldValue}/>)
        }
    }
    return fields;
}

var FormField = React.createClass({
    render: function() {
        var attributeValue
        if (this.props.name === "attributes" || this.props.name === "inquiryFields" || this.props.name === "lookupFields" || this.props.name === "resultFields") {
            attributeValue = <AttributeTable attributes={this.props.value} editable={this.props.editable} updateFieldValue={this.props.updateFieldValue}/>
        } else if (this.props.name === "defaultSort") {
            var fields = buildFieldArray(this.props.value, this.props.editable, this.props.updateFieldValue)
            attributeValue = <table>{fields}</table>
        } else if (this.props.name === "inquirySections") {
            var attributeValues = [];
            for (var i=0;i<this.props.value.length;i++) {
                var fields = buildFieldArray(this.props.value[i], this.props.editable, this.props.updateFieldValue)
                attributeValues.push(<tr><td><table>{fields}</table></td></tr>)
            }
            attributeValue = <table>{attributeValues}</table>
        } else if (this.props.name === "inquiryDefinition" || this.props.name === "lookupDefinition") {
            var fields = buildFieldArray(this.props.value, this.props.editable, this.props.updateFieldValue)
            attributeValue = <table>{fields}</table>
        } else {
            attributeValue = <InputField editable={this.props.editable} value={this.props.value} name={this.props.name} updateFieldValue={this.props.updateFieldValue}/>
                //determineFieldValue(this.props.editable, this.props.value, this.props.name, this.props.updateFieldValue)
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
            fields.push(<AttributeFormField attribute={this.props.attributes[i]} editable={this.props.editable} updateFieldValue={this.props.updateFieldValue}/>)
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
        console.log('rendering attribute form field')
        var fields = [];
        for (var key in this.props.attribute) {
            if (this.props.attribute.hasOwnProperty(key)) {
                if (key === "control") {
                    fields.push(<td><AttributeTable editable={this.props.editable} attributes={[this.props.attribute[key]]} updateFieldValue={this.props.updateFieldValue}/></td>)

                } else {
                    var attributeValue = <InputField editable={this.props.editable} value={this.props.attribute[key]} name={key} updateFieldValue={this.props.updateFieldValue}/>
                        //determineFieldValue(this.props.editable, this.props.attribute[key], key, this.props.updateFieldValue)
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
            return <input type={type} value={this.props.value} checked={this.props.value} onChange={this.props.updateFieldValue.bind(this, this.props.name)}/>
        }
    }
})

function determineFieldValue(editable, value, name, onBlurFunction) {
    if (!editable || editable === 'false') {
        return typeof value === "boolean" ? value.toString() : value
    } else {
        var type = "text"
        if (typeof value === "boolean") {
            type = "checkbox"
        }
        return <input type={type} value={value} checked={value} onBlur={onBlurFunction.bind(this, name)}/>
    }
}

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