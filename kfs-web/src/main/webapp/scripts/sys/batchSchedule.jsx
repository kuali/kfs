import React from 'react/addons';
import Router from 'react-router';
import Reactable from 'reactable';
import { DefaultRoute, HashHistory, Link, Route, RouteHandler } from 'react-router';
import $ from 'jquery';
import Moment from 'moment';
import URL from 'url-parse';
import DateTimeField from 'react-bootstrap-datetimepicker';

var Table = Reactable.Table;
var path = getUrlPathPrefix('/batchSchedule.html') + "/batch/jobs";

var JobTable = React.createClass({
    handleSearchSubmit: function() {
        $.ajax({
            url: path,
            dataType: 'json',
            type: 'GET',
            success: function(searchResults) {
                this.setState({searchResults: searchResults});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(path, status, err.toString()+"; this should never happen!!!!");
            }.bind(this)
        });
    },
    componentDidMount: function() {
        this.handleSearchSubmit();
    },
    getInitialState: function () {
        return {searchResults: {'unscheduled': [], 'scheduled': []}};
    },
    render: function() {
        console.log("render parent");
        return (
            <div>
                <JobList searchResults={this.state.searchResults}/>
            </div>
        );
    }
});

function prettifyStepNames(stepNames) {
    if (!stepNames) {
        return (<span></span>)
    }
    return (
        <ul>
            {stepNames.map(function(ele, idx) {
                return (<li>{idx+1}: {ele}</li>)
            })}
        </ul>
    )
}

function prettifyDependencies(dependencies) {
    var rows = [];
    for (var dependency in dependencies) {
        if (dependencies.hasOwnProperty(dependency)) {
            rows.push(dependency + " (" + dependencies[dependency] + ")");
        }
    }
    return (
        <ul>
            {rows.map(function(row) {
                return (<li>{row}</li>)
            })}
        </ul>
    )
}

var JobList = React.createClass({
    render: function() {
        console.log("render JobList");
        var scheduled = this.props.searchResults.scheduled;
        var unscheduled = this.props.searchResults.unscheduled;
        var rows = scheduled.concat(unscheduled)
        rows = rows.map(function(row) {
            var formattedNextRunDate = (row.nextRunDate ? Moment(row.nextRunDate).format('M/DD/YYYY, h:mm:ss a') : '');
            row['formattedNextRunDate'] = formattedNextRunDate
            row['modifyUrl'] = <Link to="detail" params={{jobName: row.name, groupId: row.group}}>modify</Link>;
            row['steps'] = prettifyStepNames(row.stepNames)
            row['dependencyList'] = prettifyDependencies(row.dependencies)
            return row
        })
        return (
            <Table data={rows}
                   columns={[{key: 'modifyUrl', label: 'Actions'},{key: 'namespaceCode', label: 'Namespace'},{key: 'name', label: 'Name'}, {key: 'group', label: 'Group'},{key: 'formattedNextRunDate', label: 'Next Run Date'},{ key: 'steps', label: 'Steps'},{key: 'dependencyList',label: 'Dependencies'}]}
                   sortable={['namespaceCode','name','group','formattedNextRunDate']}
                   filterable={['namespaceCode','name','group']}/>
        );
    }
});

var JobDetail = React.createClass({
    componentDidMount: function() {
        var jobDetailEndpoint = getUrlPathPrefix('/batchSchedule.html') + "/batch/job/"+this.props.params.groupId+"/"+this.props.params.jobName;
        $.ajax({
            url: jobDetailEndpoint,
            dataType: 'json',
            type: 'GET',
            success: function(job) {
                var endSteps = job.stepNames.map(function (ele, idx) {
                    return {index: idx, stepName: ele}
                });

                this.setState({job: job, endSteps: endSteps});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(jobDetailEndpoint, status, err.toString());
            }.bind(this)
        })
    },
    getInitialState: function () {
        return {job: {}, endSteps: []};
    },
    updateEndSteps: function(startStep) {
        console.log("update endSteps");
        var endSteps = (this.state.job.stepNames) ? this.state.job.stepNames.map(function (ele, idx) {
            return {index: idx, stepName: ele}
        }).filter(function (ele, idx) {
            return !(startStep && ele.index+1 < startStep)
        }) : [];
        //var endSteps = (this.state.job.stepNames) ? this.state.job.stepNames.filter(function (ele, idx) {
        //    return !(startStep && idx < startStep)
        //}).map(function (ele, idx) {
        //    console.log("index: " + idx + ", stepName: " + ele);
        //    return {index: idx, stepName: ele}
        //}) : [];
        this.setState({endSteps: endSteps});
    },
    render: function() {
        return (
            <div>
                <ScheduledGroupMembershipToggle job={this.state.job}/>
                <UnscheduledJobForm job={this.state.job} endSteps={this.state.endSteps} updateEndSteps={this.updateEndSteps}/>
                <JobInfo job={this.state.job}/>
            </div>
        )
    }
});

var ScheduledGroupMembershipToggle = React.createClass({
    handleClick: function(name, event) {
        console.log(name+"!")
        var jobDetailEndpoint = getUrlPathPrefix('/batchSchedule.html') + "/batch/job/"+name+"r/"+this.props.job.group+"/"+this.props.job.name;
        $.ajax({
            url: jobDetailEndpoint,
            contentType: 'application/json',
            dataType: 'json',
            type: 'POST',
            data: JSON.stringify({command: name}),
            success: function(job) {
                this.setState({job: job});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(jobDetailEndpoint, status, err.toString());
            }.bind(this)
        })
    },
    render:function() {
        if (this.props.job && this.props.job.group === "unscheduled") {
            return (
                <button onClick={this.handleClick.bind(this, "schedule")} value="schedule" type="button">Schedule</button>
            )
        } else if (this.props.job && this.props.job.scheduled) {
            return (
                <button onClick={this.handleClick.bind(this, "unschedule")} value="unschedule" type="button">Unschedule</button>
            )
        } else {
            return (<span/>) // this should never happen!!! : - >
        }
    }
});

var UnscheduledJobForm = React.createClass({
    getInitialState: function () {
        var now = new Date()
        return {startStep: "", endStep: "", startDate: Moment(now).format("x"), startTime: Moment(now).format("x"), resultsEMail: ""};
    },
    handleClick: function() {
        console.log("Handle Click!")
        var jobDetailEndpoint = getUrlPathPrefix('/batchSchedule.html') + "/batch/job/runner/"+this.props.job.group+"/"+this.props.job.name;
        var data = {
            startStep: this.state.startStep,
            endStep: this.state.endStep,
            startDate: Moment(this.state.startDate).format("MM/DD/YYYY hh:mm a"),
            startTime: Moment(this.state.startTime).format("MM/DD/YYYY hh:mm a"),
            resultsEMail: this.state.resultsEMail
        }
        $.ajax({
            url: jobDetailEndpoint,
            contentType: 'application/json',
            dataType: 'json',
            type: 'POST',
            data: JSON.stringify(data),
            success: function(job) {
                this.setState({job: job});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(jobDetailEndpoint, status, err.toString());
            }.bind(this)
        })
    },
    handleTextChange: function(name, event) {
        console.log("on change: "+name)
        var stateUpdate = {}
        stateUpdate[name] = event.target.value
        if (name === "startStep") {
            this.props.updateEndSteps(event.target.value);
        }
        this.setState(stateUpdate)
    },
    handleDateChange: function(name, millis) {
        var stateUpdate = {}
        stateUpdate[name] = new Date(parseInt(millis))
        this.setState(stateUpdate)
    },
    render:function() {
        var startStepOptions = (this.props.job.stepNames) ? this.props.job.stepNames.map(function (ele, idx) {
            return (<option value={idx+1}>{idx+1}: {ele}</option>)
        }) : "";
        var endStepOptions = (this.props.endSteps) ? this.props.endSteps.map(function (ele, idx) {
            return (<option value={ele.index+1}>{ele.index+1}: {ele.stepName}</option>)
        }) : "";
        if (this.props.job.group === 'unscheduled') {
            return (
                <div>
                    <p>
                        <label htmlFor="startStep">Start Step</label>
                        <UpdatableSelect key={startStepOptions.length} step={this.state.startStep} handleTextChange={this.handleTextChange.bind(this,"startStep")} options={startStepOptions}/>
                    </p>
                    <p>
                        <label htmlFor="endStep">End Step</label>
                        <UpdatableSelect key={endStepOptions.length} step={this.state.endStep} handleTextChange={this.handleTextChange.bind(this,"endStep")} options={endStepOptions}/>
                    </p>
                    <p>
                        <label htmlFor="startDateTime">Start Date/Time</label>
                        <DateTimeField key="Start Date" dateTime={this.state.startDate} onChange={this.handleDateChange.bind(this,"startDate")} mode="date" inputFormat="MM/DD/YY"/>
                        <DateTimeField key="Start Time" dateTime={this.state.startTime} onChange={this.handleDateChange.bind(this,"startTime")} mode="time" inputFormat="h:mm A"/>
                    </p>
                    <p>
                        <label htmlFor="resultsEMail">Results E-Mail Address</label>
                        <input type="text" value={this.state.resultsEMail} size="50" onChange={this.handleTextChange.bind(this,"resultsEMail")}/>
                    </p>
                    <button onClick={this.handleClick} value="schedule" type="button">New Schedule</button>
                </div>
            )
        } else {
            return (
                <span/>
            )
        }
    }
});

var UpdatableSelect = React.createClass({
    render:function() {
         return (
            <select value={this.props.step} onChange={this.props.handleTextChange}>
                <option value=""></option>
                {this.props.options}
            </select>
         )
    }
});

var JobInfo = React.createClass({
    render: function() {
        var formattedNextRunDate = (this.props.job.nextRunDate ? Moment(this.props.job.nextRunDate).format('M/DD/YYYY, h:mm:ss a') : '');
        return (
            <div>
                <ul>
                    <li>Namespace: {this.props.job.namespaceCode}</li>
                    <li>Name: {this.props.job.name}</li>
                    <li>Group: {this.props.job.group}</li>
                    <li>Status: {this.props.job.status}</li>
                    <li>Next Run Date: {formattedNextRunDate}</li>
                    <li>Steps: {prettifyStepNames(this.props.job.stepNames)}</li>
                    <li>Number of Steps: {this.props.job.numSteps}</li>
                    <li>Dependencies: {this.props.job.dependencyList}</li>
                </ul>
                <Link to="table">back</Link>
            </div>
        )
    }
})

function getUrlPathPrefix(page) {
    var path = URL(window.location.href).pathname;
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
        <Route name="table" path="/" handler={JobTable}/>
        <Route name="detail" path="/job/:jobName/group/:groupId" handler={JobDetail}/>
    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.getElementById('main'));
});

