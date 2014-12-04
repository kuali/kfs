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

 function loadIncomeCAECodeDesc(caeCodeFieldName){
	var elPrefix = findElPrefix(caeCodeFieldName.name);
	var caeCodeDescriptionFieldName = elPrefix + ".incomeCAE.description";
	var errorMessage = "Income CAE Code description not found";
 	setCAECodeDesc(caeCodeFieldName, caeCodeDescriptionFieldName, errorMessage);
 }
  
 function loadPrincipalCAECodeDesc(caeCodeFieldName){
	var elPrefix = findElPrefix(caeCodeFieldName.name);
	var caeCodeDescriptionFieldName = elPrefix + ".principalCAE.description";
	var errorMessage = "Principal CAE Code description not found";
 	setCAECodeDesc(caeCodeFieldName, caeCodeDescriptionFieldName, errorMessage);
 }

 function setCAECodeDesc(caeCodeFieldName, caeCodeDescriptionFieldName, errorMessage){
	var caeCode = dwr.util.getValue(caeCodeFieldName);
    
	if (caeCode =='') {
		clearRecipients(caeCodeDescriptionFieldName);
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue(caeCodeDescriptionFieldName, data.description);
			} else {
				setRecipientValue(caeCodeDescriptionFieldName, wrapError(errorMessage), true);			
			} },
			errorHandler:function(errorMessage) { 
				setRecipientValue(caeCodeDescriptionFieldName, wrapError(errorMessage), true);
			}
		};
		CAECodeService.getByPrimaryKey(caeCode, dwrReply );
	}
}
