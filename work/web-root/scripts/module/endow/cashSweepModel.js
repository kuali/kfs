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

 function loadCashSweepModelIDDesc(cashSweepModelIDFieldName){
	var elPrefix = findElPrefix(cashSweepModelIDFieldName.name);
	var cashSweepModelIDDescriptionFieldName = elPrefix + ".cashSweepModel.cashSweepModelName";
 	setCashSweepModelIDDescription(cashSweepModelIDFieldName, cashSweepModelIDDescriptionFieldName);
 }
  
 function setCashSweepModelIDDescription(cashSweepModelIDFieldName, cashSweepModelIDDescriptionFieldName){
	var cashSweepModelID = dwr.util.getValue(cashSweepModelIDFieldName);
    
	if (cashSweepModelID =='') {
		clearRecipients(cashSweepModelIDDescriptionFieldName);
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue(cashSweepModelIDDescriptionFieldName, data.cashSweepModelName);
			} else {
				setRecipientValue(cashSweepModelIDDescriptionFieldName, wrapError("Cash Sweep Model ID description not found"), true);			
			} },
			errorHandler:function(errorMessage) { 
				setRecipientValue(cashSweepModelIDDescriptionFieldName, wrapError("Cash Sweep Model ID description not found"), true);
			}
		};
		CashSweepModelService.getByPrimaryKey(cashSweepModelID, dwrReply );
	}
}
