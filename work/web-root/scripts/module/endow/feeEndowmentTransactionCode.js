/*
 * Copyright 2007 The Kuali Foundation.
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

 function loadFeeEndowmentCodeDesc(feeEndowmentCodeFieldName){
	var elPrefix = findElPrefix(feeEndowmentCodeFieldName.name);
	var feeEndowmentCodeDescriptionFieldName = elPrefix + ".endowmentTransactionCode.name";
	var errorMessage = "Charge with Endowment Transaction code description not found";
 	setFeeEndowmentCodeDescription(feeEndowmentCodeFieldName, feeEndowmentCodeDescriptionFieldName, errorMessage);
 }
  
 function loadFeeEndowmentTransactionName(feeEndowmentCodeFieldName){
	var elPrefix = findElPrefix(feeEndowmentCodeFieldName.name);
	var feeEndowmentCodeDescriptionFieldName = elPrefix + ".endowmentTransaction.name";
	var errorMessage = "Endowment Transaction code description not found";	
 	setFeeEndowmentCodeDescription(feeEndowmentCodeFieldName, feeEndowmentCodeDescriptionFieldName, errorMessage);
 }
 
 function setFeeEndowmentCodeDescription(feeEndowmentCodeFieldName, feeEndowmentCodeDescriptionFieldName, errorMessage){
 
	var feeEndowmentCode = dwr.util.getValue(feeEndowmentCodeFieldName);
    
	if (feeEndowmentCode =='') {
		clearRecipients(feeEndowmentCodeDescriptionFieldName);
	} else {
		feeEndowmentCode = feeEndowmentCode.toUpperCase();
		
		var dwrReply = {
			callback:function(data) {
			if (data != null && typeof data == 'object') {
				setRecipientValue(feeEndowmentCodeDescriptionFieldName, data.name);
			} else {
				setRecipientValue(feeEndowmentCodeDescriptionFieldName, wrapError(errorMessage), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(feeEndowmentCodeDescriptionFieldName, wrapError(errorMessage), true);
			}
		};
		EndowmentTransactionCodeService.getByPrimaryKey(feeEndowmentCode, dwrReply);
	}
}
