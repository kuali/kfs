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
var accountNumberSuffix = ".accountNumber";
var accountNameSuffix = ".account.accountName";
var subAccountNumberSuffix = ".subAccountNumber";
var subAccountNameSuffix = ".subAccount.subAccountName";
var chartCodeSuffix = ".chartOfAccountsCode";
var objectCodeSuffix = ".financialObjectCode";
var objectCodeNameSuffix = ".objectCode.financialObjectCodeName";
var subObjectCodeSuffix = ".financialSubObjectCode";
var subObjectCodeNameSuffix = ".subObjectCode.financialSubObjectCodeName";


function loadChartInfo(coaCodeFieldName, coaNameFieldName ) {
    var coaCode = DWRUtil.getValue( coaCodeFieldName );

	if (coaCode=='') {
		clearRecipients(coaNameFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( coaNameFieldName, data.finChartOfAccountDescription );
			} else {
				setRecipientValue( coaNameFieldName, wrapError( "chart not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( coaNameFieldName, wrapError( "chart not found" ), true );
			}
		};
		ChartService.getByPrimaryId( coaCode, dwrReply );
	}
}

function loadAccountInfo( accountCodeFieldName, accountNameFieldName ) {
    var elPrefix = findElPrefix( accountCodeFieldName );
    var accountCode = DWRUtil.getValue( accountCodeFieldName );
    var coaCode = DWRUtil.getValue( elPrefix + chartCodeSuffix );

    if (valueChanged( accountCodeFieldName )) {
        setRecipientValue( elPrefix + subAccountNumberSuffix, "" );
        setRecipientValue( elPrefix + subAccountNameSuffix, "" );
        setRecipientValue( elPrefix + subObjectCodeSuffix, "" );
        setRecipientValue( elPrefix + subObjectCodeNameSuffix, "" );
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

function loadSubAccountInfo( subAccountCodeFieldName, subAccountNameFieldName ) {
    var elPrefix = findElPrefix( subAccountCodeFieldName );
    var coaCode = getElementValue( elPrefix + chartCodeSuffix );
    var accountCode = getElementValue( elPrefix + accountNumberSuffix );
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

function loadObjectInfo(fiscalYear, objectTypeNameRecipient, objectTypeCodeRecipient, objectCodeFieldName, objectNameFieldName) {
    var elPrefix = findElPrefix( objectCodeFieldName );
    var objectCode = getElementValue( objectCodeFieldName );
    var coaCode = getElementValue( elPrefix + chartCodeSuffix );

    if (valueChanged( objectCodeFieldName )) {
        clearRecipients( elPrefix + subObjectCodeSuffix );
        clearRecipients( elPrefix + subObjectCodeNameSuffix );
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

function loadSubObjectInfo(fiscalYear, subObjectCodeFieldName, subObjectNameFieldName) {
    var elPrefix = findElPrefix( subObjectCodeFieldName );
    var accountCode = getElementValue( elPrefix + accountNumberSuffix );
    var objectCode = getElementValue( elPrefix + objectCodeSuffix );
    var subObjectCode = getElementValue( subObjectCodeFieldName );
    var coaCode = getElementValue( elPrefix + chartCodeSuffix );
        
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

function loadProjectInfo(projectCodeFieldName, projectNameFieldName) {
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

function loadObjectTypeInfo(objectTypeCodeFieldName, objectTypeNameFieldName) {
    var objectTypeCode = getElementValue( objectTypeCodeFieldName );

    if (objectTypeCode=='') {
        clearRecipients(objectTypeNameFieldName);
    } else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( objectTypeNameFieldName, data.name );
			} else {
				setRecipientValue( objectTypeNameFieldName, wrapError( "object type not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( objectTypeNameFieldName, wrapError( "object type not found" ), true );
			}
		};
		ObjectTypeService.getByPrimaryKey( objectTypeCode, dwrReply );
    }
}

function loadOriginationInfo(originationCodeFieldName, originationCodeNameFieldName) {
    var originationCode = getElementValue(originationCodeFieldName);

    if (originationCode == '') {
        clearRecipients(originationCodeNameFieldName);
    } else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( originationCodeNameFieldName, data.financialSystemDatabaseName );
			} else {
				setRecipientValue( originationCodeNameFieldName, wrapError( "origin code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( originationCodeNameFieldName, wrapError( "origin code not found" ), true );
			}
		};
		OriginationCodeService.getByPrimaryKey( originationCode, dwrReply );
    }
}

function loadDocumentTypeInfo(documentTypeCodeFieldName, documentTypeNameFieldName) {
    var documentTypeCode = getElementValue(documentTypeCodeFieldName);

    if (documentTypeCode == '') {
        clearRecipients(documentTypeNameFieldName);
    } else {
		var dwrReply = {
			callback:function(data) {
				if ( data != null && typeof data == 'object' ) {
					setRecipientValue( documentTypeNameFieldName, data.financialDocumentName );
				} else {
					setRecipientValue( documentTypeNameFieldName, wrapError( "doc type not found" ), true );			
				}
			},
			errorHandler:function( errorMessage ) { 
				setRecipientValue( documentTypeNameFieldName, wrapError( "doc type not found" ), true );
			}
		};
		DocumentTypeService.getDocumentTypeByCode( documentTypeCode, dwrReply );
    }
}
