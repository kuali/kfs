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