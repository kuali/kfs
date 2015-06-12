var Parent = React.createClass({
    loadInquiryData: function() {
        var url = decodeURIComponent(getUrlParameter("url"));
        console.log('url: ' + url);
        $.ajax({
            url: url,
            dataType: 'json',
            type: 'GET',
            success: function(searchResults) {
                console.log(searchResults);
                this.setState({inquiryData: searchResults});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    getInitialState: function () {
        console.log('getInitialState');
        return {inquiryData: {}};
    },
    componentDidMount: function() {
        this.loadInquiryData();
        console.log('mounted');
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
        console.log(this.props.inquiryData);
        var rows = [];
        if (this.props.inquiryData.fields) {
            this.props.inquiryData.fields.forEach(function(field) {
                rows.push(<ResultsRow field={field} data={this.props.inquiryData.data} />);
            }.bind(this));
        }
        return (
            <table>
                <tbody>{rows}</tbody>
            </table>
        );
    }
});

var ResultsRow = React.createClass({
    render: function() {
        var value = this.props.data[this.props.field.field];
        if (typeof this.props.data[this.props.field.field] === "object") {
            value = "<a href=" + value.link + "target='_blank'>" + value.value + "/a>";
        }
        return (
            <tr>
                <td>{this.props.field.label}</td>
                <td>{typeof this.props.data[this.props.field.field] === "object" ? <InquiryField field={this.props.data[this.props.field.field]} /> : this.props.data[this.props.field.field]}</td>
            </tr>
        );
    }
});

var InquiryField = React.createClass({
    render: function() {
        var url = "/kfs-dev/inquiry.html?url=" + encodeURIComponent(this.props.field.link);
        return (
            <a href={url} target="_blank">{this.props.field.value}</a>
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
