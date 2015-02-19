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


function loadAchBankInfo(bankRoutingField) {
    var bankRoutingNumber = dwr.util.getValue( bankRoutingField.name ).trim();
    var targetFieldName = findElPrefix( bankRoutingField.name ) + ".bankRouting.bankName";

	if (bankRoutingNumber=="") {
		clearRecipients(targetFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == "object" ) {
				setRecipientValue( targetFieldName, data.bankName );
			} else {
				setRecipientValue( targetFieldName, wrapError( "bank not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( targetFieldName, wrapError( "bank not found" ), true );
			}
		};
		
		AchBankService.getByPrimaryId( bankRoutingNumber, dwrReply );
	}
}

function submit(payeeIdTypeCodeField) {
	// always force a form submit in case of change on payee type, so the readOnly fields will be re-evaluated 
	document.forms[0].submit();
	/*
	alert("payeeIdTypeCodeFieldName = " + payeeIdTypeCodeField.name);
	var payeeIdTypeCode = dwr.util.getValue(payeeIdTypeCodeField.name).trim();
	alert("payeeIdTypeCode = " + payeeIdTypeCode);
	if (payeeIdTypeCode == "E" || payeeIdTypeCode == "T") {
		var form = document.forms[0];
		form.methodToCall="post";
		alert("submit form...");
		form.submit();
	}
	*/
}
