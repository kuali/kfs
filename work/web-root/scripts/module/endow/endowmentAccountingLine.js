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

<script type='text/javascript' src='/kfs-dev/dwr/interface/ChartService.js'></script>

function loadAccountName(accountNumberFieldName, chartFieldName, accountNameFieldName){
	var accountNumber = dwr.util.getValue( accountNumberFieldName );
	var chart = dwr.util.getValue( chartFieldName );
	
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
	var subAccountNumber = dwr.util.getValue( subAccountNumberFieldName );
	var accountNumber = dwr.util.getValue( accountNumberFieldName );
	var chart = dwr.util.getValue( chartFieldName );
	
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
	var chart = dwr.util.getValue( chartFieldName );

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
	var objectCode = dwr.util.getValue( objectCodeFieldName );
	var chart = dwr.util.getValue( chartFieldName );

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
	var subObjectCode = dwr.util.getValue( subObjectCodeFieldName );
	var chart = dwr.util.getValue( chartFieldName );
	var accountNumber = dwr.util.getValue( accountNumberFieldName );
	var objectCode = dwr.util.getValue( objectCodeFieldName );

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
    var projectCode = dwr.util.getValue( projectCodeFieldName );

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