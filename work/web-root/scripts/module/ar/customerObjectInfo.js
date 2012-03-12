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
function loadCustomerInfo( customerNumberFieldName, customerNameFieldName ) {
    var customerNumber = dwr.util.getValue( customerNumberFieldName ).toUpperCase();

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
