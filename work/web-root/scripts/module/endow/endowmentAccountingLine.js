/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
