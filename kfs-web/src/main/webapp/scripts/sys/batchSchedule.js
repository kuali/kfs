

var url = "/kfs-dev/batch/jobs";

var Table = Reactable.Table;

var JobTable = React.createClass({
    handleSearchSubmit: function() {
        $.ajax({
            url: url,
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
            var formattedNextRunDate = (row.nextRunDate ? moment(row.nextRunDate).format('M/DD/YYYY, h:mm:ss a') : '');
            row['formattedNextRunDate'] = formattedNextRunDate
            row['modifyUrl'] = "<a href='/kfs-dev/batch/jobs/'" + row.name +  "></a>";
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
        var formattedNextRunDate = (this.props.job.nextRunDate ? moment(this.props.job.nextRunDate).format('M/DD/YYYY, h:mm:ss a') : '');
        return (
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
        )
    }
})

React.render(
    <Router history={new HashHistory}>
        <Route component={JobTable}>
            <Route path="/" component={Index}/>
            <Route path="/job/:jobName/group/:groupId" component={JobDetail}/>
        </Route>
    </Router>,
    document.getElementById('main')
);