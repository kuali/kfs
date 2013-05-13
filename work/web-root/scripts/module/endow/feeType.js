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

 function loadFeeTypeCodeDesc(feeTypeCodeFieldName){
	var elPrefix = findElPrefix( feeTypeCodeFieldName.name );
	var feeTypeCodeDescriptionFieldName = elPrefix + ".feeType.description";
 	setFeeTypeCodeDescription(feeTypeCodeFieldName, feeTypeCodeDescriptionFieldName);
 }
  
 function setFeeTypeCodeDescription(feeTypeCodeFieldName, feeTypeCodeDescriptionFieldName){
 
	var feeTypeCode = dwr.util.getValue(feeTypeCodeFieldName);
    
	if (feeTypeCode =='') {
		clearRecipients(feeTypeCodeDescriptionFieldName);
	} else {
		feeTypeCode = feeTypeCode.toUpperCase();
		
		var dwrReply = {
			callback:function(data) {
			if (data != null && typeof data == 'object') {
				setRecipientValue( feeTypeCodeDescriptionFieldName, data.name);
			} else {
				setRecipientValue(feeTypeCodeDescriptionFieldName, wrapError("fee type description not found" ), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(feeTypeCodeDescriptionFieldName, wrapError("fee type description not found" ), true);
			}
		};
		FeeTypeService.getByPrimaryKey(feeTypeCode, dwrReply);
	}
}
