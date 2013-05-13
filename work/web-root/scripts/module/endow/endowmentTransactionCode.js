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

 function loadEndowmentTransactionName(endowmentCodeFieldName, endowmentCodeDescriptionFieldName){
	 var endowmentCode = dwr.util.getValue(endowmentCodeFieldName);

		if (endowmentCode =='') {
			setRecipientValue(endowmentCodeDescriptionFieldName, "");
		} else {
			var dwrReply = {
				callback:function(data) {
				if ( data != null && typeof data == 'object' ) {
					setRecipientValue(endowmentCodeFieldName, data.code );					
					setRecipientValue(endowmentCodeDescriptionFieldName, data.name );				
				} else {
					setRecipientValue(endowmentCodeDescriptionFieldName, wrapError("ETran code description not found"), true);			
				} },
				errorHandler:function(errorMessage ) { 
					setRecipientValue(endowmentCodeDescriptionFieldName, wrapError("ETran code description not found"), true);				
				}
			};
			
			EndowmentTransactionCodeService.getByPrimaryKey(endowmentCode, dwrReply);
		}
}
