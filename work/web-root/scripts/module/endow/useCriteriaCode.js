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

 function loadUseCriteriaCodeDesc(useCriteriaCodeFieldName){
	var elPrefix = findElPrefix( useCriteriaCodeFieldName.name );
	var useCriteriaCodeDescriptionFieldName = elPrefix + ".useCriteriaCode.name";
	
	alert(useCriteriaCodeFieldName.name + ":" + useCriteriaCodeDescriptionFieldName);
	
	setUseCriteriaCodeDescription(useCriteriaCodeFieldName, useCriteriaCodeDescriptionFieldName);
 }
  
 function setUseCriteriaCodeDescription(useCriteriaCodeFieldName, useCriteriaCodeDescriptionFieldName){
 
	var useCriteriaCode = DWRUtil.getValue(useCriteriaCodeFieldName);

alert(useCriteriaCod);

	if (useCriteriaCode == '') {
		clearRecipients(useCriteriaCodeDescriptionFieldName);
	} else {
		useCriteriaCode = useCriteriaCode.toUpperCase();
setRecipientValue( useCriteriaCodeDescriptionFieldName, 'Waiting...');		
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( useCriteriaCodeDescriptionFieldName, data.name );
			} else {
				setRecipientValue( useCriteriaCodeDescriptionFieldName, wrapError( "Use Criteria Code description not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( useCriteriaCodeDescriptionFieldName, wrapError( "Use Criteria Code description not found" ), true );
			}
		};
		UseCriteriaCodeService.getByPrimaryKey( useCriteriaCode, dwrReply );
	}
}
