var Parent = React.createClass({
    handleSearchSubmit: function(code) {
        $.ajax({
            url: this.props.url,
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
        var rows = [];
        var scheduled = this.props.searchResults.scheduled;
        var unscheduled = this.props.searchResults.unscheduled;
        scheduled.forEach(function(result) {
            rows.push(<ResultsRow job={result} />);
        }.bind(this));
        unscheduled.forEach(function(result) {
            rows.push(<ResultsRow job={result} />);
        }.bind(this));
        return (
            <table>
                <thead>
                <tr>
                    <th>Actions</th>
                    <th>Namespace</th>
                    <th>Job Name</th>
                    <th>Job Group</th>
                    <th>Job Status</th>
                    <th>Next Runtime</th>
                    <th>Steps</th>
                    <th>Dependencies</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </table>
        );
    }
});

var ResultsRow = React.createClass({
    render: function() {
        var nextRunDate = (this.props.job.nextRunDate ? moment(this.props.job.nextRunDate).format('M/DD/YYYY, h:mm:ss a') : '');
        return (
            <tr>
                <td>modify</td>
                <td>{this.props.job.namespaceCode}</td>
                <td>{this.props.job.name}</td>
                <td>{this.props.job.group}</td>
                <td>{this.props.job.status}</td>
                <td>{nextRunDate}</td>
                <td>{this.props.job.stepList}</td>
                <td>{this.props.job.dependencyList}</td>
            </tr>
        );
    }
});

React.render(
    <Parent url="/kfs-dev/batch/jobs" />,
    document.getElementById('main')
);