/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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


