/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function loadCustomerAddressInfo( customerNumberFieldName ) {
	var customerNumber = dwr.util.getValue( customerNumberFieldName );
	
	if (customerNumber == '') {
		clearAddressFields();
		return;
	}
	else {
		customerNumber = customerNumber.toUpperCase();
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
			dwr.util.setValue( containerDiv.id, "&nbsp;" );
		} else {
			dwr.util.setValue( containerDiv.id, value, isError?null:{escapeHtml:true} );
		}
	}
    if (containerHidden) {
    	// get rid of HTML in the value
    	value = value.replace(/(<([^>]+)>)/ig,"");
		dwr.util.setValue( recipientBase, value );
	}
}

