var Root = React.createClass({
    getInitialState: function() {
        return {entries: []}
    },
    componentWillMount: function() {
        $.ajax({
            url: this.props.url,
            dataType: 'json',
            type: 'GET',
            success: function(entries) {
                this.setState({entries: entries});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        })
    },
    rowGetter: function(rowIndex) {
        return this.state.entries[rowIndex]
    },
    render: function() {
        if (!this.state.entries || this.state.entries.length == 0) {
            return null
        }
        return (
            <Table>
                rowGetter={this.rowGetter}
                rowsCount={this.state.entries.length}
                <Column label="Namespace" dataKey={"namespace"}/>
                <Column label="Business Object" dataKey={"objectLabel"}/>
            </Table>)
    }
})

React.render(
    <Root url="/kfs-dev/core/datadictionary/businessObjectEntries" />,
    document.getElementById('main')
)