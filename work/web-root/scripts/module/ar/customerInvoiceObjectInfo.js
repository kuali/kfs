/*
 * Copyright 2007-2008 The Kuali Foundation
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
 
function loadCustomerNumberAndName( invoiceNumberFieldName ){
	
    var elPrefix = findElPrefix( invoiceNumberFieldName.name );
    var customerNumberFieldName = elPrefix + ".customerNumber";
    var customerNameFieldName = elPrefix + ".customer.customerName";
	var invoiceNumber = dwr.util.getValue( invoiceNumberFieldName );

	if (invoiceNumber=='') {
		clearRecipients(customerNumberFieldName);
		clearRecipients(customerNameFieldName);
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( customerNumberFieldName, data.customer.customerNumber );
				setRecipientValue( customerNameFieldName, data.customer.customerName );
			} else {
				setRecipientValue( customerNumberFieldName, wrapError( "invoice not found" ), true );			
				clearRecipients( customerNameFieldName );
			} },
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
				setRecipientValue( customerNumberFieldName, wrapError( "invoice not found" ), true );
				clearRecipients( customerNameFieldName );
			}
		};
		CustomerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber( invoiceNumber, dwrReply );
	}
}


