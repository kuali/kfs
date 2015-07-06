function getUrlPathPrefix(page) {
    var path = window.location.href
    var index = path.indexOf(page)
    return path.substring(0, index)
}

function currFormat(val) {
    return numeral(val).format('$0,0.00')
}

var path = getUrlPathPrefix('/account_balances.html') + "/generalLedger/accountBalanceByConsolidation/chart/BL/account/4631588"

var AccountBalancesComponentRoot = React.createClass({
    getInitialState: function() {
        return {accountConsolidation: null}
    },
    componentWillMount: function() {
        $.ajax({
            url: path,
            dataType: 'json',
            type: 'GET',
            success: function(result) {
                this.setState({accountConsolidation: result});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(path, status, err.toString());
            }.bind(this)
        });
    },
    render: function() {
        if (this.state.accountConsolidation) {
            return (<AccountTable accountConsolidation={this.state.accountConsolidation}/>)
        } else {
            return (<span></span>)
        }
    }
})

var AccountTable = React.createClass({
    render: function() {
        var consolidationLines = this.props.accountConsolidation.accountBalances.map(function (ele) {
            var key = ele.chartOfAccountsCode+"-"+ele.accountNumber+"-"+ele.objectConsolidationName.replace(/\s/,"")
            return <ConsolidationRow consolidationName={ele.objectConsolidationName} budget={ele.budget} spent={ele.spent} allocated={ele.allocated} balance={ele.balance} key={key}/>
        })
        var balanceClass = this.props.accountConsolidation.total.balance >= 0 ? "amount good_dark" : "amount bad_dark"
        return (<table className="account_balances">
            <thead>
            <tr>
                <th>Account #</th>
                <th>Account Name</th>
                <th>Budgeted</th>
                <th>Spent</th>
                <th>Allocated</th>
                <th>Balance</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>{this.props.accountConsolidation.chartOfAccountsCode}-{this.props.accountConsolidation.accountNumber}</td>
                <td span="5">
                    {this.props.accountConsolidation.accountName}
                </td>
            </tr>
            {consolidationLines}
            <tr className="total">
                <td>&nbsp;</td>
                <td>TOTAL</td>
                <td className="amount">{currFormat(this.props.accountConsolidation.total.budget)}</td>
                <td className="amount">{currFormat(this.props.accountConsolidation.total.spent)}</td>
                <td className="amount">{currFormat(this.props.accountConsolidation.total.allocated)}</td>
                <td className={balanceClass}>{currFormat(this.props.accountConsolidation.total.balance)}</td>
            </tr>
            </tbody>
        </table>)
    }
})

var ConsolidationRow = React.createClass({
    render: function() {
        var balanceClass = this.props.balance >= 0 ? "amount good_dark" : "amount bad_dark"
        return (<tr>
            <td>&nbsp;</td>
            <td>{this.props.consolidationName}</td>
            <td className="amount">{currFormat(this.props.budget)}</td>
            <td className="amount">{currFormat(this.props.spent)}</td>
            <td className="amount">{currFormat(this.props.allocated)}</td>
            <td className={balanceClass}>{currFormat(this.props.balance)}</td>
        </tr>)
    }
})

React.render(
    <AccountBalancesComponentRoot />,
    document.getElementById('main')
);