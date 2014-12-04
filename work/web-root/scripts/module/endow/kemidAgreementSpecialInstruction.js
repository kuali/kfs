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

 function loadAgreementSpecialInstructionCodeDesc(codeFieldName) {
	var elPrefix = findElPrefix(codeFieldName.name);
	var codeDescriptionFieldName = elPrefix + ".agreementSpecialInstruction.name";
	var code = dwr.util.getValue(codeFieldName);
	
	setAgreementSpecialInstructionCodeDescription(code, codeDescriptionFieldName);
}

function setAgreementSpecialInstructionCodeDescription(code, codeDescriptionFieldName) {	 
alert(code);
 	if (code == '') {
 		clearRecipients(codeDescriptionFieldName);
 	} else {
 		var dwrReply = {
 			callback:function(data) {
 				if ( data != null && typeof data == 'object') {
 					setRecipientValue(codeDescriptionFieldName, data.name);
 				} else {
 					setRecipientValue(codeDescriptionFieldName, wrapError("Agreement Special Instruction Code not found"), true);			
 				} },
 			errorHandler:function(errorMessage) { 
 				setRecipientValue(codeDescriptionFieldName, wrapError("Agreement Special Instruction Code not found"), true);
 			}
 		};
 		AgreementSpecialInstructionService.getByPrimaryKey(code.toUpperCase(), dwrReply);
 	}
}

 
 


