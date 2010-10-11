/*
 * Copyright 2007-2008 The Kuali Foundation
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

var receivableChartCodeSuffix = ".paymentChartOfAccountsCode";
var receivableAccountNumberSuffix = ".paymentAccountNumber";
var receivableSubAccountNumberSuffix = ".paymentSubAccountNumber";
var receivableSubAccountNameSuffix = ".paymentSubAccount.subAccountName";
var receivableObjectCodeSuffix = ".paymentFinancialObjectCode";
var receivableSubObjectCodeSuffix = ".paymentFinancialSubObjectCode";
var receivableSubObjectCodeNameSuffix = ".paymentSubObjectCode.financialSubObjectCodeName";

function loadReceivableAccountInfo( accountCodeFieldName, accountNameFieldName ) {
    var elPrefix = findElPrefix( accountCodeFieldName );
    var accountCode = DWRUtil.getValue( accountCodeFieldName );
    var coaCode = DWRUtil.getValue( elPrefix + receivableChartCodeSuffix );

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
