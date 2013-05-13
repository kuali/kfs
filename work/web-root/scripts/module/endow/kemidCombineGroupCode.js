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

 function loadCombineGroupCodeDesc(combineGroupCodeFieldName){
	var elPrefix = findElPrefix( combineGroupCodeFieldName.name );
	var combineGroupCodeDescriptionFieldName = elPrefix + ".combineGroup.name";
 	setCombineGroupCodeDescription(combineGroupCodeFieldName, combineGroupCodeDescriptionFieldName);
 }
  
 function setCombineGroupCodeDescription(combineGroupCodeFieldName, combineGroupCodeDescriptionFieldName){
 
	var combineGroupCode = dwr.util.getValue(combineGroupCodeFieldName);
    
	if (combineGroupCode =='') {
		clearRecipients(combineGroupCodeDescriptionFieldName);
	} else {
		combineGroupCode = combineGroupCode.toUpperCase();
		
		var dwrReply = {
			callback:function(data) {
			if (data != null && typeof data == 'object') {
				setRecipientValue( combineGroupCodeDescriptionFieldName, data.name);
			} else {
				setRecipientValue(combineGroupCodeDescriptionFieldName, wrapError("combine group code not found" ), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(combineGroupCodeDescriptionFieldName, wrapError("combine group code not found" ), true);
			}
		};
		CombineGroupCodeService.getByPrimaryKey(combineGroupCode, dwrReply);
	}
}
