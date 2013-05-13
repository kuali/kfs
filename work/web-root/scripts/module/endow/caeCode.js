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
