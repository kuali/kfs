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
function loadAwardInfo( proposalNumberFieldName ) {
    var proposalNumber = DWRUtil.getValue( proposalNumberFieldName ).toUpperCase();

	if (proposalNumber=='') {
		clearRecipients( "document.agencyNumber" );
		clearRecipients( "document.agencyName" );
		clearRecipients( "document.customerNumber" );
		clearRecipients( "document.customerName" );
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( "document.agencyNumber", data.agency.agencyNumber );
				setRecipientValue( "document.agencyName", data.agency.fullName );
				setRecipientValue( "document.customerNumber", data.agency.customer.customerNumber );
				setRecipientValue( "document.customerName", data.agency.customer.customerName );
			} else {
				setRecipientValue( "document.agencyNumber", wrapError( "award not found" ), true );			
				clearRecipients( "document.agencyName" );
				clearRecipients( "document.customerNumber" );
				clearRecipients( "document.customerName" );
			} },
			errorHandler:function( errorMessage ) {
				setRecipientValue( "document.agencyNumber", wrapError( "award not found" ), true );			
				clearRecipients( "document.agencyName" );
				clearRecipients( "document.customerNumber" );
				clearRecipients( "document.customerName" );
			}
		};
		CollectionActivityDocumentService.retrieveAwardByProposalNumber( proposalNumber, dwrReply );
	}
}

function clearDate(booleanFieldName, dateFieldName) {
	var clearDate = DWRUtil.getValue( booleanFieldName );
	if (!clearDate) {
		document.getElementById(dateFieldName).value = "";
	}
}