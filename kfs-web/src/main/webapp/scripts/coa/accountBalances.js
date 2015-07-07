function getUrlPathPrefix(page) {
    var path = window.location.href
    var index = path.indexOf(page)
    return path.substring(0, index)
}

function currFormat(val) {
    return numeral(val).format('$0,0.00')
}

function interleave(ary1, ary2) {
    var zippedArray = ary1.map(function(ele, idx){
        return [ele, ary2[idx]]
    })
    return zippedArray.reduce(function(prev, curr) {
        curr.forEach(function(val) {
            prev.push(val)
        })
        return prev
    }, [])
}

function bulletChart(target, chartData) {
    var margin = {top: 5, right: 40, bottom: 20, left: 120},
        width = 800 - margin.left - margin.right,
        height = 50 - margin.top - margin.bottom

    var chart = d3.bullet()
        .width(width)
        .height(height)

    d3.json(chartData, function(error, data) {
        var svg = d3.select(target).selectAll("svg")
            .data(data)
            .enter().append("svg")
            .attr("class", "bullet")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
            .call(chart)

        var title = svg.append("g")
            .style("text-anchor", "end")
            .attr("transform", "translate(-6," + height / 2 + ")")

        title.append("text")
            .attr("class", "title")
            .text(function(d) { return d.title; })

        title.append("text")
            .attr("class", "subtitle")
            .attr("dy", "1em")
            .text(function(d) { return d.subtitle; })
    })
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
        var maxAmount = this.props.accountConsolidation.accountBalances.reduce(function(prevVal, currVal) {
            return (currVal > prevVal) ? currVal : prevVal
        }, -1)
        var consolidationLines = this.props.accountConsolidation.accountBalances.map(function (ele) {
            var key = ele.chartOfAccountsCode+"-"+ele.accountNumber+"-"+ele.objectConsolidationName.replace(/\s/,"")
            return (
                <ConsolidationRow consolidationName={ele.objectConsolidationName} budget={ele.budget} spent={ele.spent} allocated={ele.allocated} balance={ele.balance} key={key}/>
            )
        })
        var consolidationChartLines = this.props.accountConsolidation.accountBalances.map(function (ele) {
            var key = ele.chartOfAccountsCode+"-"+ele.accountNumber+"-"+ele.objectConsolidationName.replace(/\s/,"")+"-chart"
            return (
                <ConsolidationChartRow consolidationName={ele.objectConsolidationName} budget={ele.budget} spent={ele.spent} maxAmount={maxAmount} key={key}/>
            )
        })
        var tableLines = interleave(consolidationLines, consolidationChartLines)
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
            {tableLines}
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

//http://www.d3noob.org/2013/07/introduction-to-bullet-charts-in-d3js.html
//http://boothead.github.io/d3/ex/bullet.html
var ConsolidationChartRow = React.createClass({
    buildChartData: function() {
        var chartData = {}
        //chartData['title'] = ""
        //chartData['subtitle'] = ""
        chartData['ranges'] = [this.props.spent, this.props.budget, this.props.maxAmount]
        //chartData['measures'] = []
        //chartData['markers'] = []
        return chartData
    },
    render: function() {
        var html = "<div id="+this.props.chartId+"></div>\n<script>\n\tvar chartId = "+this.props.chartId+"\n\tvar data = "+this.buildChartData()+"\n\tbulletChart(chartId, data)\n</script>"
        return (
            <tr><td>&nbsp;</td><td colSpan="5"><div dangerouslySetInnerHTML={{__html: html}} /></td></tr>
        )
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