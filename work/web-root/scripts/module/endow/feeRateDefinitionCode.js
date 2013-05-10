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

 function loadFeeRateDefinitionCodeDesc(feeFeeRateDefinitionCodeFieldName){
	var elPrefix = findElPrefix(feeFeeRateDefinitionCodeFieldName.name);
	var feeFeeRateDefinitionCodeDescriptionFieldName = elPrefix + ".classCode.name";
 	setFeeRateDefinitionCodeDescription(feeFeeRateDefinitionCodeFieldName, feeFeeRateDefinitionCodeDescriptionFieldName);
 }
  
 function setFeeRateDefinitionCodeDescription(feeFeeRateDefinitionCodeFieldName, feeFeeRateDefinitionCodeDescriptionFieldName){
 
	var feeFeeRateDefinitionCode = dwr.util.getValue(feeFeeRateDefinitionCodeFieldName);
    
	if (feeFeeRateDefinitionCode =='') {
		clearRecipients(feeFeeRateDefinitionCodeFieldName);
	} else {
		feeFeeRateDefinitionCode = feeFeeRateDefinitionCode.toUpperCase();
		
		var dwrReply = {
			callback:function(data) {
			if (data != null && typeof data == 'object') {
				setRecipientValue(feeFeeRateDefinitionCodeDescriptionFieldName, data.name);
			} else {
				setRecipientValue(feeFeeRateDefinitionCodeDescriptionFieldName, wrapError("fee rate definition code description not found"), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(feeFeeRateDefinitionCodeDescriptionFieldName, wrapError("fee rate definition code description not found"), true);
			}
		};
		FeeRateDefinitionCodeService.getByPrimaryKey(feeFeeRateDefinitionCode, dwrReply);
	}
}
