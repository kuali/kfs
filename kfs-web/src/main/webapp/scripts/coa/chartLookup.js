// Search Bar

// Results Row
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

// Results Box
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

searchResults=[{
  "versionNumber": 1,
  "objectId": "014F3DAF7489A448E043814FD28EA448",
  "newCollectionRecord": false,
  "extension": null,
  "finChartOfAccountDescription": "BLOOMINGTON AUX",
  "active": true,
  "finCoaManagerPrincipalId": "2168808105",
  "reportsToChartOfAccountsCode": "BL",
  "chartOfAccountsCode": "BA",
  "finAccountsPayableObjectCode": "9041",
  "finExternalEncumbranceObjCd": "9892",
  "finPreEncumbranceObjectCode": "9890",
  "financialCashObjectCode": "8000",
  "icrIncomeFinancialObjectCode": "1803",
  "finAccountsReceivableObjCode": "8118",
  "finInternalEncumbranceObjCd": "9891",
  "icrExpenseFinancialObjectCd": "5500",
  "incBdgtEliminationsFinObjCd": "1209",
  "expBdgtEliminationsFinObjCd": "1209",
  "fundBalanceObjectCode": "9899",
  "name": "BLOOMINGTON AUX",
  "code": "BA",
  "codeAndDescription": "BA - BLOOMINGTON AUX",
  "chartCodeForReport": "BA"
}]

React.render(
    <ResultsBox searchResults={searchResults}/>,
    document.getElementById('main')
);