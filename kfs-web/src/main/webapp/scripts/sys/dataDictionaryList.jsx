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
        var value = event.target.value
        if (event.target.type === 'checkbox') {
            value = event.target.checked
        }

        var s = {}
        s.entry = this.state.entry
        if (prefix) {
            setValue(prefix + '.' + name, value, s.entry)
        } else {
            s.entry[name] = value
        }

        this.setState(s)
    },
    addListItem: function(prefix, item) {
        var list = getValueByPath(prefix, this.state.entry)
        var s = {}
        s.entry = this.state.entry
        if (list && Array.isArray(list)) {
            list.push(item)
            setValue(prefix, list, s.entry)
            this.setState(s)
        }
    },
    updateBusinessObjectEntry: function() {
        $.ajax({
            url: getUrlPathPrefix("/sys/DataDictionaryList.html") + "/core/datadictionary/businessObjectEntry/" + this.props.params.name,
            dataType: 'json',
            contentType: 'application/json',
            type: 'PUT',
            data: JSON.stringify(this.state.entry),
            success: function(response) {
                alert('Congrats! ' + response.message)
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })

    },
    render: function() {
        var attributeNames = []
        if (this.state.entry.attributes) {
            attributeNames = this.state.entry.attributes.map(function(elem) {
                return elem.name
            })
        }
        var prefix;
        var fields = buildFieldArray(prefix, this.state.entry, this.props.params.editable, this.updateFieldValue, attributeNames, this.addListItem)
        var updateButton;
        if (this.props.params.editable && this.props.params.editable  === "true") {
            updateButton = <button type="button" onClick={this.updateBusinessObjectEntry.bind(this)}>Update</button>;
        }
        return (
            <div>
                <Link to="table">go back</Link>
                <table>
                    <tbody>
                    {fields}
                    </tbody>
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

function getValueByPath(path, obj) {
    var fields = path.split('.');
    var result = obj;
    for (var i = 0, n = fields.length; i < n && result !== undefined; i++) {
        var field = fields[i];
        if (typeof result[field] === 'undefined' || typeof result[field] !== 'object') {
            return undefined
        }
        result = result[field];
    }
    return result;
}

function buildFieldArray(prefix, map, editable, updateFieldValue, attributeNames, addListItem) {
    var fields = [];
    for (var key in map) {
        if (map.hasOwnProperty(key)) {
            fields.push(<FormField prefix={prefix} name={key} value={map[key]} editable={editable} updateFieldValue={updateFieldValue} attributeNames={attributeNames} addListItem={addListItem}/>)
        }
    }
    return fields;
}

var FormField = React.createClass({
    render: function() {
        var attributeValue
        var prefix = this.props.name
        if (this.props.prefix) {
            prefix = this.props.prefix + "." + this.props.name
        }
        if (this.props.name === "attributes" || this.props.name === "inquiryFields" || this.props.name === "lookupFields" || this.props.name === "resultFields") {
            attributeValue = <AttributeTable prefix={prefix} attributes={this.props.value} editable={this.props.editable} updateFieldValue={this.props.updateFieldValue} attributeNames={this.props.attributeNames} addListItem={this.props.addListItem}/>
        } else if (this.props.name === "defaultSort") {
            var fields = buildFieldArray(prefix, this.props.value, this.props.editable, this.props.updateFieldValue, this.props.attributeNames, this.props.addListItem)
            attributeValue = <table><tbody>{fields}</tbody></table>
        } else if (this.props.name === "inquirySections") {
            var attributeValues = [];
            for (var i=0;i<this.props.value.length;i++) {
                var fields = buildFieldArray(prefix, this.props.value[i], this.props.editable, this.props.updateFieldValue, this.props.attributeNames, this.props.addListItem)
                attributeValues.push(<tr><td><table><tbody>{fields}</tbody></table></td></tr>)
            }
            attributeValue = <table><tbody>{attributeValues}</tbody></table>
        } else if (this.props.name === "inquiryDefinition" || this.props.name === "lookupDefinition") {
            var fields = buildFieldArray(prefix, this.props.value, this.props.editable, this.props.updateFieldValue, this.props.attributeNames, this.props.addListItem)
            attributeValue = <table><tbody>{fields}</tbody></table>
        } else {
            attributeValue = <InputField prefix={undefined} editable={this.props.editable} value={this.props.value} name={this.props.name} updateFieldValue={this.props.updateFieldValue}/>
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
        var usedAttributeNames = this.props.attributes.map(function (attr) {
            return attr.attributeName
        })
        var nonUsedAttributeNames = this.props.attributeNames.filter(function(attrName) {
            return usedAttributeNames.indexOf(attrName) < 0
        })
        for (var i=0; i<this.props.attributes.length; i++) {
            var prefix = this.props.prefix + '.' + i
            fields.push(<AttributeFormField prefix={prefix} attribute={this.props.attributes[i]} editable={this.props.editable} updateFieldValue={this.props.updateFieldValue} attributeNames={this.props.attributeNames} addListItem={this.props.addListItem}/>)
        }
        if (this.props.prefix === 'lookupDefinition.lookupFields' || this.props.prefix === 'lookupDefinition.resultFields') {
            fields.push(<AddField attributeNames={nonUsedAttributeNames} prefix={this.props.prefix} addListItem={this.props.addListItem}/>)
        }
        return (
            <table>
                <tbody>
                {fields}
                </tbody>
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
                    fields.push(<td><AttributeTable prefix={prefix} editable={this.props.editable} attributes={[this.props.attribute[key]]} updateFieldValue={this.props.updateFieldValue} attributeNames={this.props.attributeNames} addListItem={this.props.addListItem}/></td>)

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
            var value = typeof this.props.value === "boolean" ? this.props.value.toString() : this.props.value
            return <div>{value}</div>
        } else {
            if (typeof this.props.value === "boolean") {
                return <input type='checkbox' value={this.props.value} checked={this.props.value} onChange={this.props.updateFieldValue.bind(this, this.props.prefix, this.props.name)}/>
            }
            return <input type='text' value={this.props.value}onChange={this.props.updateFieldValue.bind(this, this.props.prefix, this.props.name)}/>
        }
    }
})

var AddField = React.createClass({
    getInitialState: function() {
        return {addLine: {
            attributeName: this.props.attributeNames[0],
            required: false,
            forceInquiry: false,
            noInquiry: false,
            noDirectInquiry: false,
            forceLookup: false,
            noLookup: false,
            useShortLabel: false,
            defaultValue: null,
            quickfinderParameterString: null,
            displayEditMode: null,
            hidden: false,
            readOnly: false,
            treatWildcardsAndOperatorsAsLiteral: false,
            alternateDisplayAttributeName: null,
            additionalDisplayAttributeName: null,
            triggerOnChange: false,
            total: false
        }}
    },
    updateFieldValue: function(name, event) {
        var value = event.target.value
        if (event.target.type === 'checkbox') {
            value = event.target.checked
        }

        var s = {addLine: this.state.addLine}
        s.addLine[name] = value

        this.setState(s)
    },
    addItem: function() {
        this.props.addListItem(this.props.prefix, this.state.addLine)
    },
    render: function() {
        if (this.props.attributeNames && this.props.attributeNames.length > 0) {
            var attributeNameOptions = []
            for (var i = 0; i < this.props.attributeNames.length; i++) {
                attributeNameOptions.push(<option value={this.props.attributeNames[i]}>{this.props.attributeNames[i]}</option>)
            }
            return (
                <tr>
                    <td><select onChange={this.updateFieldValue.bind(this, "attributeName")}>{attributeNameOptions}</select></td>
                    <td><input type="checkbox" value={this.state.addLine.required} onChange={this.updateFieldValue.bind(this, "required")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "forceInquiry")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "noInquiry")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "noDirectInquiry")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "forceLookup")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "noLookup")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "useShortLabel")}/></td>
                    <td><input type="text" onChange={this.updateFieldValue.bind(this, "defaultValue")}/></td>
                    <td><input type="text" onChange={this.updateFieldValue.bind(this, "quickFinderParameterString")}/></td>
                    <td><input type="text" onChange={this.updateFieldValue.bind(this, "displayEditMode")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "hidden")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "readOnly")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "treatWildcardsAndOperatorsAsLiteral")}/></td>
                    <td><input type="text" onChange={this.updateFieldValue.bind(this, "alternateDisplayAttributeName")}/></td>
                    <td><input type="text" onChange={this.updateFieldValue.bind(this, "additionalDisplayAttributeName")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "triggerOnChange")}/></td>
                    <td><input type="checkbox" value="false" onChange={this.updateFieldValue.bind(this, "total")}/></td>
                    <td><button type="button" onClick={this.addItem.bind(this)}>Kaboom!</button></td>
                </tr>
            )
        } else {
            return (<tr style={{display:'none'}}><td></td></tr>)
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