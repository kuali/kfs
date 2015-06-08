var Parent = React.createClass({
    handleSearchSubmit: function(code) {
        $.ajax({
            url: this.props.url + code,
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
    getInitialState: function () {
        return {searchResults: []};
    },
    render: function() {
        return (
            <div>
                <SearchBar onSearchSubmit={this.handleSearchSubmit}/>
                <ResultsBox searchResults={this.state.searchResults}/>
            </div>
        );
    }
});

var SearchBar = React.createClass({
    handleSubmit: function(e) {
        e.preventDefault();
        var code = React.findDOMNode(this.refs.code).value.trim();
        if (!code) {
            // TODO - make this empty string or * or something
        }
        this.props.onSearchSubmit(code);
        return;
    },
    render: function() {
        return (
            <form className="searchForm" onSubmit={this.handleSubmit}>
                Chart Code: <input ref="code" type="text"/>
            </form>
        );
    }
});

var ResultsBox = React.createClass({
    render: function() {
        var rows = [];
        this.props.searchResults.forEach(function(result) {
            rows.push(<ResultsRow chart={result} />);
        }.bind(this));
        return (
          <table>
              <thead>
                  <tr>
                      <th>Chart Code</th>
                      <th>Chart Description</th>
                      <th>Chart Active Indicator</th>
                      <th>Cash Object Code</th>
                      <th>Accounts Payable Object Code</th>
                      <th>Chart Manager Name</th>
                  </tr>
              </thead>
              <tbody>{rows}</tbody>
          </table>
        );
    }
});

var ResultsRow = React.createClass({
    render: function() {
        return (
            <tr>
                <td>{this.props.chart.code}</td>
                <td>{this.props.chart.name}</td>
                <td>{this.props.chart.active}</td>
                <td>{this.props.chart.financialCashObjectCode}</td>
                <td>{this.props.chart.finAccountsPayableObjectCode}</td>
                <td>{this.props.chart.finCoaManagerPrincipalId}</td>
            </tr>
        );
    }
});

React.render(
    <Parent url="/kfs-dev/lookup/coa/chart/" />,
    document.getElementById('main')
);