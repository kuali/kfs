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

var receivableChartCodeSuffix = ".paymentChartOfAccountsCode";
var receivableChartNameSuffix = ".paymentChartOfAccounts.finChartOfAccountDescription";
var receivableAccountNumberSuffix = ".paymentAccountNumber";
var receivableSubAccountNumberSuffix = ".paymentSubAccountNumber";
var receivableSubAccountNameSuffix = ".paymentSubAccount.subAccountName";
var receivableObjectCodeSuffix = ".paymentFinancialObjectCode";
var receivableSubObjectCodeSuffix = ".paymentFinancialSubObjectCode";
var receivableSubObjectCodeNameSuffix = ".paymentSubObjectCode.financialSubObjectCodeName";

/**
 * Loads account info and chart info when ACCOUNT_CAN_CROSS_CHART is false.
 * @param accountCodeFieldName
 * @param accountNameFieldName
 * @return
 */
function loadReceivableChartAccountInfo( accountCodeFieldName, accountNameFieldName ) {
    var elPrefix = findElPrefix( accountCodeFieldName );
	var coaCodeFieldName = elPrefix + receivableChartCodeSuffix;
	var coaNameFieldName = elPrefix + receivableChartNameSuffix;
	var accountCode = dwr.util.getValue( accountCodeFieldName );
    
    if (valueChanged( accountCodeFieldName )) {
        setRecipientValue( elPrefix + receivableSubAccountNumberSuffix, "" );
        setRecipientValue( elPrefix + receivableSubAccountNameSuffix, "" );
        setRecipientValue( elPrefix + receivableSubObjectCodeSuffix, "" );
        setRecipientValue( elPrefix + receivableSubObjectCodeNameSuffix, "" );
    }
    
    if (accountCode=='') {
		clearRecipients(accountNameFieldName);
		clearRecipients(coaCodeFieldName);    		
		clearRecipients(coaNameFieldName);    		
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( accountNameFieldName, data.accountName );
				setRecipientValue( coaCodeFieldName, data.chartOfAccountsCode );
				setRecipientValue( coaNameFieldName, data.chartOfAccounts.finChartOfAccountDescription );
			} else {
				setRecipientValue( accountNameFieldName, wrapError( "account not found" ), true );			
				clearRecipients(coaCodeFieldName);    		
				clearRecipients(coaNameFieldName);
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( accountNameFieldName, wrapError( "account not found" ), true );
				clearRecipients(coaCodeFieldName);    		
				clearRecipients(coaNameFieldName);  
			}
		};
		AccountService.getUniqueAccountForAccountNumber( accountCode, dwrReply );	  
	}	
}

/**
 * Loads account info and chart info when ACCOUNT_CAN_CROSS_CHART is true.
 * @param accountCodeFieldName
 * @param accountNameFieldName
 * @return
 */
function loadReceivableAccountInfo( accountCodeFieldName, accountNameFieldName ) {
    var elPrefix = findElPrefix( accountCodeFieldName );
    var accountCode = dwr.util.getValue( accountCodeFieldName );
    var coaCode = dwr.util.getValue( elPrefix + receivableChartCodeSuffix );

    if (valueChanged( accountCodeFieldName )) {
        setRecipientValue( elPrefix + receivableSubAccountNumberSuffix, "" );
        setRecipientValue( elPrefix + receivableSubAccountNameSuffix, "" );
        setRecipientValue( elPrefix + receivableSubObjectCodeSuffix, "" );
        setRecipientValue( elPrefix + receivableSubObjectCodeNameSuffix, "" );
    }
    
    if (accountCode=='') {
		clearRecipients(accountNameFieldName);
	} else if (coaCode=='') {
		setRecipientValue(accountNameFieldName, wrapError( 'chart code is empty' ), true );
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( accountNameFieldName, data.accountName );
			} else {
				setRecipientValue( accountNameFieldName, wrapError( "account not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( accountNameFieldName, wrapError( "account not found" ), true );
			}
		};
		AccountService.getByPrimaryIdWithCaching( coaCode, accountCode, dwrReply );
	}	
}

function loadReceivableSubAccountInfo( subAccountCodeFieldName, subAccountNameFieldName ) {
    var elPrefix = findElPrefix( subAccountCodeFieldName );
    var coaCode = getElementValue( elPrefix + receivableChartCodeSuffix );
    var accountCode = getElementValue( elPrefix + receivableAccountNumberSuffix );
    var subAccountCode = getElementValue( subAccountCodeFieldName );

	if (subAccountCode=='') {
		clearRecipients(subAccountNameFieldName);
	} else if (coaCode=='') {
		setRecipientValue(subAccountNameFieldName, wrapError( 'chart code is empty' ), true );
	} else if (accountCode=='') {
		setRecipientValue(subAccountNameFieldName, wrapError( 'account number is empty' ), true );
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( subAccountNameFieldName, data.subAccountName );
			} else {
				setRecipientValue( subAccountNameFieldName, wrapError( "sub-account not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( subAccountNameFieldName, wrapError( "sub-account not found" ), true );
			}
		};
		SubAccountService.getByPrimaryId( coaCode, accountCode, subAccountCode, dwrReply );
	}
}

function loadReceivableObjectInfo(fiscalYear, objectTypeNameRecipient, objectTypeCodeRecipient, objectCodeFieldName, objectNameFieldName) {
    var elPrefix = findElPrefix( objectCodeFieldName );
    var objectCode = getElementValue( objectCodeFieldName );
    var coaCode = getElementValue( elPrefix + receivableChartCodeSuffix );

    if (valueChanged( objectCodeFieldName )) {
        clearRecipients( elPrefix + receivableSubObjectCodeSuffix );
        clearRecipients( elPrefix + receivableSubObjectCodeNameSuffix );
    }
	if (objectCode=='') {
		clearRecipients(objectNameFieldName);
	} else if (coaCode=='') {
		setRecipientValue(objectNameFieldName, wrapError( 'chart code is empty' ), true );
	} else if (fiscalYear=='') {
		setRecipientValue(objectNameFieldName, wrapError( 'fiscal year is missing' ), true );
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( objectNameFieldName, data.financialObjectCodeName );
				setRecipientValue( objectTypeCodeRecipient, data.financialObjectTypeCode );
				setRecipientValue( objectTypeNameRecipient, data.financialObjectType.name );
			} else {
				setRecipientValue( objectNameFieldName, wrapError( "object not found" ), true );			
				clearRecipients( objectTypeCodeRecipient );
				clearRecipients( objectTypeNameRecipient );
			} },
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
				setRecipientValue( objectNameFieldName, wrapError( "object not found" ), true );
				clearRecipients( objectTypeCodeRecipient );
				clearRecipients( objectTypeNameRecipient );
			}
		};
		ObjectCodeService.getByPrimaryId( fiscalYear, coaCode, objectCode, dwrReply );
	}
}

function loadReceivableSubObjectInfo(fiscalYear, subObjectCodeFieldName, subObjectNameFieldName) {
    var elPrefix = findElPrefix( subObjectCodeFieldName );
    var accountCode = getElementValue( elPrefix + receivableAccountNumberSuffix );
    var objectCode = getElementValue( elPrefix + receivableObjectCodeSuffix );
    var subObjectCode = getElementValue( subObjectCodeFieldName );
    var coaCode = getElementValue( elPrefix + receivableChartCodeSuffix );
        
	if (subObjectCode=='') {
		clearRecipients(subObjectNameFieldName);
	} else if (coaCode=='') {
		setRecipientValue(subObjectNameFieldName, wrapError( 'chart is empty' ), true);
	} else if (fiscalYear=='') {
		setRecipientValue(subObjectNameFieldName, wrapError( 'fiscal year is missing' ), true);
	} else if (accountCode=='') {
		setRecipientValue(subObjectNameFieldName, wrapError( 'account is empty' ), true );
	} else if (objectCode=='') {
		setRecipientValue(subObjectNameFieldName, wrapError( 'object code is empty' ), true );
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( subObjectNameFieldName, data.financialSubObjectCodeName );
			} else {
				setRecipientValue( subObjectNameFieldName, wrapError( "sub-object not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( subObjectNameFieldName, wrapError( "sub-object not found" ), true );
			}
		};
		SubObjectCodeService.getByPrimaryId( fiscalYear, coaCode, accountCode, objectCode, subObjectCode, dwrReply );
	}
}

function loadReceivableProjectInfo(projectCodeFieldName, projectNameFieldName) {
    var projectCode = getElementValue( projectCodeFieldName );

	if (projectCode=='') {
		clearRecipients(projectNameFieldName);
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
