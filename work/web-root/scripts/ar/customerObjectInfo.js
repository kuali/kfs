function loadCustomerInfo( customerNumberFieldName, customerNameFieldName ) {
    var customerNumber = DWRUtil.getValue( customerNumberFieldName );

	if (customerNumber=='') {
		clearRecipients(customerNameFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( customerNameFieldName, data.customerName );
			} else {
				setRecipientValue( customerNameFieldName, wrapError( "customer not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( customerNameFieldName, wrapError( "customer not found" ), true );
			}
		};
		CustomerService.getByPrimaryKey( customerNumber, dwrReply );
	}
}