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
function loadTransactionLineKEMIDShortTitle(kemidFieldName, kemidShortTitleFieldName){
	var kemid = dwr.util.getValue( kemidFieldName );
	
	if (kemid =='') {
		setRecipientValue(kemidShortTitleFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue(kemidFieldName, data.kemid);
				setRecipientValue(kemidShortTitleFieldName, data.shortTitle);
			} else {
				setRecipientValue(kemidShortTitleFieldName, wrapError( "kemid not found"), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(kemidShortTitleFieldName, wrapError( "kemid not found"), true);				
			}
		};
		
		KEMIDService.getByPrimaryKey(kemid, dwrReply);
	}
 }