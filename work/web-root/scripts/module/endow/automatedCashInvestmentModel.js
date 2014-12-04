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

 function loadACIIncomeModelDesc(aciModelIDFieldName){
	var elPrefix = findElPrefix(aciModelIDFieldName.name);
	var aciModelIDDescriptionFieldName = elPrefix + ".automatedCashInvestmentModelForIncomeACIModelId.aciModelName";
	var errorMessage = "Income ACI Model description not found";
 	setACIModelDesc(aciModelIDFieldName, aciModelIDDescriptionFieldName, errorMessage);
 }
  
 function loadACIPrincipalModelDesc(aciModelIDFieldName){
	var elPrefix = findElPrefix(aciModelIDFieldName.name);
	var aciModelIDDescriptionFieldName = elPrefix + ".automatedCashInvestmentModelForPrincipalACIModelId.aciModelName";
	var errorMessage = "Principal ACI Model description not found";
 	setACIModelDesc(aciModelIDFieldName, aciModelIDDescriptionFieldName, errorMessage);
 }

 function setACIModelDesc(aciModelIDFieldName, aciModelIDDescriptionFieldName, errorMessage){
	var aciModelID = dwr.util.getValue(aciModelIDFieldName);
    
	if (aciModelID =='') {
		clearRecipients(aciModelIDDescriptionFieldName);
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue(aciModelIDDescriptionFieldName, data.aciModelName);
			} else {
				setRecipientValue(aciModelIDDescriptionFieldName, wrapError(errorMessage), true);			
			} },
			errorHandler:function(errorMessage) { 
				setRecipientValue(aciModelIDDescriptionFieldName, wrapError(errorMessage), true);
			}
		};
		AutomatedCashInvestmentModelService.getByPrimaryKey(aciModelID, dwrReply );
	}
}
