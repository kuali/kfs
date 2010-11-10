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
 */

 function loadUseCriteriaCodeDesc(useCriteriaCodeFieldName){
	var elPrefix = findElPrefix( useCriteriaCodeFieldName.name );
	var useCriteriaCodeDescFieldName = elPrefix + ".useCriteria.name";
	setUseCriteriaDesc(useCriteriaCodeFieldName, useCriteriaCodeDescFieldName);
}
  
 function setUseCriteriaDesc( useCriteriaCodeFieldName, useCriteriaCodeDescFieldName ){
	 
		var useCriteriaCode = DWRUtil.getValue( useCriteriaCodeFieldName );

		if (useCriteriaCode =='') {
			clearRecipients(useCriteriaCodeDescFieldName, "");
		} else {
			useCriteriaCode = useCriteriaCode.toUpperCase();
			var dwrReply = {
				callback:function(data) {
				if ( data != null && typeof data == 'object' ) {
					setRecipientValue( useCriteriaCodeDescFieldName, data.name );
				} else {
					setRecipientValue( useCriteriaCodeDescFieldName, wrapError( "Use Criteria code not found" ), true );			
				} },
				errorHandler:function( errorMessage ) { 
					setRecipientValue( useCriteriaCodeDescFieldName, wrapError( "Use Criteria code not found" ), true );
				}
			};
			UseCriteriaCodeService.getByPrimaryKey(useCriteriaCode, dwrReply );
		}
}
 

