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
function loadAccountName(accountNumberFieldName, chartFieldName, accountNameFieldName){
	var accountNumber = DWRUtil.getValue( accountNumberFieldName );
	var chart = DWRUtil.getValue( chartFieldName );
	
	if (accountNumber =='' || chart == '') {
		setRecipientValue(accountNameFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue(accountNameFieldName, data.accountName);
			} else {
				setRecipientValue(accountNameFieldName, wrapError( "account not found"), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(accountNameFieldName, wrapError( "account not found"), true);				
			}
		};
		
		AccountService.getByPrimaryIdWithCaching(chart, accountNumber, dwrReply);
	}
 }

function loadSubAccountName(subAccountNumberFieldName, accountNumberFieldName, chartFieldName, subAccountNameFieldName){
	var subAccountNumber = DWRUtil.getValue( subAccountNumberFieldName );
	var accountNumber = DWRUtil.getValue( accountNumberFieldName );
	var chart = DWRUtil.getValue( chartFieldName );
	
	if (subAccountNumber == '' || accountNumber =='' || chart == '') {
		setRecipientValue(subAccountNameFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue(subAccountNameFieldName, data.subAccountName);
			} else {
				setRecipientValue(subAccountNameFieldName, wrapError( "sub-account not found"), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(subAccountNameFieldName, wrapError( "sub account not found"), true);				
			}
		};
		
		SubAccountService.getByPrimaryId(chart, accountNumber, subAccountNumber, dwrReply);
	}
 }

function loadFinChartOfAccountDescription(chartFieldName, chartDescFieldName) {
	var chart = DWRUtil.getValue( chartFieldName );

	if (chart == '') {
		setRecipientValue(chartDescFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue(chartDescFieldName, data.finChartOfAccountDescription);
			} else {
				setRecipientValue(chartDescFieldName, wrapError( "chart not found"), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(chartDescFieldName, wrapError( "chart not found"), true);				
			}
		};
		
		ChartService.getByPrimaryId(chart, dwrReply);
	}
}

function loadObjectCodeName(objectCodeFieldName, chartFieldName, objectCodeNameFieldName) {
	var objectCode = DWRUtil.getValue( objectCodeFieldName );
	var chart = DWRUtil.getValue( chartFieldName );

	if (objectCode == '' || chart == '') {
		setRecipientValue(objectCodeNameFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue(objectCodeNameFieldName, data.financialObjectCodeName);
			} else {
				setRecipientValue(objectCodeNameFieldName, wrapError( "object code not found"), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(objectCodeNameFieldName, wrapError( "object code not found"), true);				
			}
		};
		
		ObjectCodeService.getByPrimaryIdForCurrentYear(chart, objectCode, dwrReply);
	}
}

function loadSubObjectCodeName(subObjectCodeFieldName, chartFieldName, accountNumberFieldName, objectCodeFieldName, subObjectCodeNameFieldName) {
	var subObjectCode = DWRUtil.getValue( subObjectCodeFieldName );
	var chart = DWRUtil.getValue( chartFieldName );
	var accountNumber = DWRUtil.getValue( accountNumberFieldName );
	var objectCode = DWRUtil.getValue( objectCodeFieldName );

	if (subObjectCode == '' || chart == '' || accountNumber == '' || objectCode == '') {
		setRecipientValue(subObjectCodeNameFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue(subObjectCodeNameFieldName, data.financialSubObjectCodeName);
			} else {
				setRecipientValue(subObjectCodeNameFieldName, wrapError( "sub-object code not found"), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(subObjectCodeNameFieldName, wrapError( "sub-object code not found"), true);				
			}
		};
		
		SubObjectCodeService.getByPrimaryIdForCurrentYear(chart, accountNumber, objectCode, subObjectCode, dwrReply);
	}
}

function loadProjectCodeName(projectCodeFieldName, projectNameFieldName) {
    var projectCode = DWRUtil.getValue( projectCodeFieldName );

	if (projectCode=='') {
		setRecipientValue(projectNameFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( projectNameFieldName, data.name );
			} else {
				setRecipientValue( projectNameFieldName, wrapError( "project not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( projectNameFieldName, wrapError( "project not found" ), true );
			}
		};
		ProjectCodeService.getByPrimaryId( projectCode, dwrReply );
	}
}