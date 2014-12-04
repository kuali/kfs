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
