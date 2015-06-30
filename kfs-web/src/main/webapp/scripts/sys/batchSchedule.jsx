import React from 'react/addons';
import Router from 'react-router';
import Reactable from 'reactable';
import { DefaultRoute, HashHistory, Link, Route, RouteHandler } from 'react-router';
import $ from 'jquery';
import Moment from 'moment';
import URL from 'url-parse';

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
                console.error(this.props.url, status, err.toString());
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
            return row
        })
        return (
            <Table data={rows}
                   columns={[{key: 'modifyUrl', label: 'Actions'},{key: 'namespaceCode', label: 'Namespace'},{key: 'name', label: 'Name'}, {key: 'group', label: 'Group'},{key: 'formattedNextRunDate', label: 'Next Run Date'},{ key: 'stepList', label: 'Steps'},{key: 'dependencyList',label: 'Dependencies'}]}
                   sortable={true}
                   filterable={['namespaceCode','name','group']}/>
        );
    }
});

var JobDetail = React.createClass({
    componentDidMount: function() {
        //$.ajax({
        //    url: this.props.url,
        //    dataType: 'json',
        //    type: 'GET',
        //    success: function(job) {
        //        this.setState({job: job});
        //    }.bind(this),
        //    error: function(xhr, status, err) {
        //        console.error(this.props.url, status, err.toString());
        //    }.bind(this)
        //});
        this.setState({job: {
            "name": "pdpSendAchAdviceNotificationsJob",
            "group": "unscheduled",
            "status": null,
            "running": false,
            "fullName": "unscheduled.pdpSendAchAdviceNotificationsJob",
            "namespaceCode": "KFS-PDP",
            "scheduled": true,
            "dependencies": {},
            "dependencyList": "",
            "stepList": "pdpSendAchAdviceNotificationsStep \n",
            "numSteps": 1,
            "nextRunDate": null
        }})
    },
    getInitialState: function () {
        return {job: {}};
    },
    render: function() {
        return (
            <JobActionForm/>,
            <JobInfo job={this.state.job}/>
        )
    }
})

var JobActionForm = React.createClass({
    render: function() {
        return (
            <div></div>
        )
    }
})

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
                    <li>Steps: {this.props.job.stepList}</li>
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
    render () {
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

