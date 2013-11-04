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
