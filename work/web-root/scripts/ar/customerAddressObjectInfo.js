function loadCustomerAddressInfo( customerNumberFieldName ) {
	var customerNumber = DWRUtil.getValue( customerNumberFieldName );
	
	if (customerNumber == '') {
		clearAddressFields();
		return;
	}
	
	var dwrReply = {
		callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setAddressFields(data);
			}
			else {
				clearAddressFields();
			}
		}, 
		errorHandler:function( errorMessage ) {
			
		}
	};
	CustomerAddressService.getPrimaryAddress( customerNumber, dwrReply );
}

function setAddressFields( data ) {
	setRecipientValueIgnoreNull( "document.customerBillToAddressIdentifier",          data.customerAddressIdentifier );
    setRecipientValueIgnoreNull( "document.billingCityName",                          data.customerCityName );
	setRecipientValueIgnoreNull( "document.billingAddressTypeCode",                   data.customerAddressTypeCode );
	setRecipientValueIgnoreNull( "document.billingStateCode",                         data.customerStateCode );
	setRecipientValueIgnoreNull( "document.billingAddressName",                       data.customerAddressName );
	setRecipientValueIgnoreNull( "document.billingZipCode",                           data.customerZipCode );
	setRecipientValueIgnoreNull( "document.billingLine1StreetAddress",                data.customerLine1StreetAddress );
	setRecipientValueIgnoreNull( "document.billingAddressInternationalProvinceName",  data.customerAddressInternationalProvinceName );
	setRecipientValueIgnoreNull( "document.billingLine2StreetAddress",                data.customerLine2StreetAddress );
	setRecipientValueIgnoreNull( "document.billingInternationalMailCode",             data.customerInternationalMailCode );
	setRecipientValueIgnoreNull( "document.billingEmailAddress",                      data.customerEmailAddress );
	setRecipientValueIgnoreNull( "document.billingCountryCode",                       data.customerCountryCode );

    setRecipientValueIgnoreNull( "document.customerShipToAddressIdentifier",          data.customerAddressIdentifier );
    setRecipientValueIgnoreNull( "document.shippingCityName",                          data.customerCityName );
	setRecipientValueIgnoreNull( "document.shippingAddressTypeCode",                   data.customerAddressTypeCode );
	setRecipientValueIgnoreNull( "document.shippingStateCode",                         data.customerStateCode );
	setRecipientValueIgnoreNull( "document.shippingAddressName",                       data.customerAddressName );
	setRecipientValueIgnoreNull( "document.shippingZipCode",                           data.customerZipCode );
	setRecipientValueIgnoreNull( "document.shippingLine1StreetAddress",                data.customerLine1StreetAddress );
	setRecipientValueIgnoreNull( "document.shippingAddressInternationalProvinceName",  data.customerAddressInternationalProvinceName );
	setRecipientValueIgnoreNull( "document.shippingLine2StreetAddress",                data.customerLine2StreetAddress );
	setRecipientValueIgnoreNull( "document.shippingInternationalMailCode",             data.customerInternationalMailCode );
	setRecipientValueIgnoreNull( "document.shippingEmailAddress",                      data.customerEmailAddress );
	setRecipientValueIgnoreNull( "document.shippingCountryCode",                       data.customerCountryCode );
}

function clearAddressFields() {
	setRecipientValueIgnoreNull( "document.customerBillToAddressIdentifier", "" );
	setRecipientValueIgnoreNull( "document.billingCityName", "" );
	setRecipientValueIgnoreNull( "document.billingAddressTypeCode", "" );
	setRecipientValueIgnoreNull( "document.billingStateCode", "" );
	setRecipientValueIgnoreNull( "document.billingAddressName", "" );
	setRecipientValueIgnoreNull( "document.billingZipCode", "" );
	setRecipientValueIgnoreNull( "document.billingLine1StreetAddress", "" );
	setRecipientValueIgnoreNull( "document.billingAddressInternationalProvinceName", "" );
	setRecipientValueIgnoreNull( "document.billingLine2StreetAddress", "" );
	setRecipientValueIgnoreNull( "document.billingInternationalMailCode", "" );
	setRecipientValueIgnoreNull( "document.billingEmailAddress", "" );
	setRecipientValueIgnoreNull( "document.billingCountryCode", "" );

    setRecipientValueIgnoreNull( "document.customerShipToAddressIdentifier", "" );
    setRecipientValueIgnoreNull( "document.shippingCityName", "" );
	setRecipientValueIgnoreNull( "document.shippingAddressTypeCode", "" );
	setRecipientValueIgnoreNull( "document.shippingStateCode", "" );
	setRecipientValueIgnoreNull( "document.shippingAddressName", "" );
	setRecipientValueIgnoreNull( "document.shippingZipCode", "" );
	setRecipientValueIgnoreNull( "document.shippingLine1StreetAddress", "" );
	setRecipientValueIgnoreNull( "document.shippingAddressInternationalProvinceName", "" );
	setRecipientValueIgnoreNull( "document.shippingLine2StreetAddress", "" );
	setRecipientValueIgnoreNull( "document.shippingInternationalMailCode", "" );
	setRecipientValueIgnoreNull( "document.shippingEmailAddress", "" );
	setRecipientValueIgnoreNull( "document.shippingCountryCode", "" );
}

//	this is here instead of using setRecipientValue in dhtml.js because that other
// one chokes and pukes when value == null.
function setRecipientValueIgnoreNull(recipientBase, value, isError ) {

    if (value) {
    	value = value.toString().trim();
    }
    else {
    	value = '';
    }
    
    var containerHidden = document.getElementById(recipientBase);
    if ( !containerHidden ) {
	    containerHidden = kualiElements[recipientBase];
    }
    var containerDiv = document.getElementById(recipientBase + divSuffix);
    if (containerDiv) {
		if (value == '') {
			DWRUtil.setValue( containerDiv.id, "&nbsp;" );
		} else {
			DWRUtil.setValue( containerDiv.id, value, isError?null:{escapeHtml:true} );
		}
	}
    if (containerHidden) {
    	// get rid of HTML in the value
    	value = value.replace(/(<([^>]+)>)/ig,"");
		DWRUtil.setValue( recipientBase, value );
	}
}

