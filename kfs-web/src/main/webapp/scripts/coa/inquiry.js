var Parent = React.createClass({
    loadInquiryData: function() {
        var url = getUrlParameter("url");
        $.ajax({
            url: url,
            dataType: 'json',
            type: 'GET',
            success: function(searchResults) {
                this.setState({inquiryData: searchResults});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    getInitialState: function () {
        return {inquiryData: {}};
    },
    componentDidMount: function() {
        this.loadInquiryData();
    },
    render: function() {
        return (
            <div>
                <ResultsBox inquiryData={this.state.inquiryData}/>
            </div>
        );
    }
});

var ResultsBox = React.createClass({
    render: function() {
        var rows = [];
        this.props.inquiryData.forEach(function(result) {
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
                <td><InquiryField field={this.props.chart.chartOfAccountsCode} /></td>
                <td>{this.props.chart.finChartOfAccountDescription}</td>
                <td>{(this.props.chart.active ? 'Yes' : 'No')}</td>
                <td><InquiryField field={this.props.chart.financialCashObjectCode} /></td>
                <td><InquiryField field={this.props.chart.finAccountsPayableObjectCode} /></td>
                <td><InquiryField field={this.props.chart['finCoaManager.name']} /></td>
            </tr>
        );
    }
});

function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++)
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
}

React.render(
    <Parent url="/kfs-dev/inquiry/coa/" />,
    document.getElementById('main')
);
