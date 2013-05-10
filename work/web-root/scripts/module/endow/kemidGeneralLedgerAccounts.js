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
 */

function loadChartInfo(codeFieldName ) {
	var elPrefix = findElPrefix(codeFieldName.name);
	var codeDescriptionFieldName = elPrefix + ".chart.finChartOfAccountDescription";
	var code = dwr.util.getValue(codeFieldName);
	
	setChartDescription(code, codeDescriptionFieldName);
}

function setChartDescription(code, codeDescriptionFieldName){	 

 	if (code == '') {
 		clearRecipients(codeDescriptionFieldName);
 	} else {
 		var dwrReply = {
 			callback:function(data) {
 				if ( data != null && typeof data == 'object') {
 					setRecipientValue(codeDescriptionFieldName, data.finChartOfAccountDescription);
 				} else {
 					setRecipientValue(codeDescriptionFieldName, wrapError("Chart not found"), true);			
 				} },
 			errorHandler:function(errorMessage) { 
 				setRecipientValue(codeDescriptionFieldName, wrapError("Chart not found"), true);
 			}
 		};
 		ChartService.getByPrimaryId(code.toUpperCase(), dwrReply);
 	}
}

function loadAccountName(accountFieldName ) {
	var elPrefix = findElPrefix(accountFieldName.name);
	var accountDescriptionFieldName = elPrefix + ".account.accountName";
	var chartCodeFieldName = elPrefix + ".chartCode";
	var accountNumber = dwr.util.getValue(accountFieldName);
	var code = dwr.util.getValue(chartCodeFieldName);
	
	setAccountDescription(accountNumber, code, accountDescriptionFieldName);
}

function setAccountDescription(accountNumber, code, accountDescriptionFieldName){	 

 	if (code == '' || accountNumber == '') {
 		clearRecipients(accountDescriptionFieldName);
 	} else {
 		var dwrReply = {
 			callback:function(data) {
 				if ( data != null && typeof data == 'object') {
 					setRecipientValue(accountDescriptionFieldName, data.accountName);
 				} else {
 					setRecipientValue(accountDescriptionFieldName, wrapError("Account not found"), true);			
 				} },
 			errorHandler:function(errorMessage) { 
 				setRecipientValue(accountDescriptionFieldName, wrapError("Account not found"), true);
 			}
 		};
 		AccountService.getByPrimaryIdWithCaching(code.toUpperCase(), accountNumber.toUpperCase(), dwrReply);
 	}
}
 
 