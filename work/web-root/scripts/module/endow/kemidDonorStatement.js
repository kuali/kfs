/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 function loadDonorStatementCodeDesc(donorStatementCodeFieldName){
	var elPrefix = findElPrefix( donorStatementCodeFieldName.name );
	var donorStatementDescriptionFieldName = elPrefix + ".donorStatement.name";

 	setDonorStatementDescription(donorStatementCodeFieldName, donorStatementDescriptionFieldName);
 }
  
 function setDonorStatementDescription(donorStatementCodeFieldName, donorStatementDescriptionFieldName){
 
	var donorStatementCode = DWRUtil.getValue(donorStatementCodeFieldName);
    
	if (donorStatementCode =='') {
		clearRecipients(donorStatementDescriptionFieldName);
	} else {
		donorStatementCode = donorStatementCode.toUpperCase();
		
		var dwrReply = {
			callback:function(data) {
			if (data != null && typeof data == 'object') {
				setRecipientValue( donorStatementDescriptionFieldName, data.name);
			} else {
				setRecipientValue(donorStatementDescriptionFieldName, wrapError("donor statement code not found" ), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(donorStatementDescriptionFieldName, wrapError("donor statement code not found" ), true);
			}
		};
		DonorStatementCodeService.getByPrimaryKey(donorStatementCode, dwrReply);
	}
}
