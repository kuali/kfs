/*
 * Copyright 2006 The Kuali Foundation
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

function onblur_subAccountTypeCode( subAccountTypeCodeField ) {
	// need to call findElPrefix twice to strip off the a21SubAccount prefix
	var subAccountTypeCodeFieldName = subAccountTypeCodeField.name;
	var fieldPrefix = findElPrefix( findElPrefix(subAccountTypeCodeFieldName) );
	var chartCodeFieldName = fieldPrefix + ".chartOfAccountsCode";
	var accountNumberFieldName = fieldPrefix + ".accountNumber";
	
	//alert ("chartCodeFieldName = " + chartCodeFieldName + ", accountNumberFieldName = " + accountNumberFieldName + ", subAccountTypeCodeFieldName = " + subAccountTypeCodeFieldName);
	updateCgIcrAccount(chartCodeFieldName, accountNumberFieldName, subAccountTypeCodeFieldName);
}
 
/* This function is for the primary key account number in SubAccount BO. */
function onblur_accountNumberPK( accountNumberField ) {
	var accountNumberFieldName = accountNumberField.name;
	var fieldPrefix = findElPrefix(accountNumberFieldName);
	var chartCodeFieldName = fieldPrefix + ".chartOfAccountsCode";
	var subAccountTypeCodeFieldName = fieldPrefix + ".a21SubAccount.subAccountTypeCode";	
	
	//alert ("chartCodeFieldName = " + chartCodeFieldName + ", accountNumberFieldName = " + accountNumberFieldName + ", subAccountTypeCodeFieldName = " + subAccountTypeCodeFieldName);
	updateCgIcrAccount(chartCodeFieldName, accountNumberFieldName, subAccountTypeCodeFieldName);
	
	var dwrReply = {
		callback: function (param) {
			if ( typeof param == 'boolean' && param == false) {	
				loadChartCode(chartCodeFieldName, accountNumberFieldName);
			}
		},	
		errorHandler:function( errorMessage ) { 
			window.status = errorMessage;
		}
	};
	AccountService.accountsCanCrossCharts(dwrReply);			
}

function updateCgIcrAccount( chartCodeFieldName, accountNumberFieldName, subAccountTypeCodeFieldName ) {	
	var chartCode = getElementValue( chartCodeFieldName );
	var accountNumber = getElementValue( accountNumberFieldName );
	var subAccountTypeCode = getElementValue( subAccountTypeCodeFieldName );
	//alert ("chartCode = " + chartCode + ", accountNumber = " + accountNumber + ", subAccountTypeCode = " + subAccountTypeCode);
	
	var dwrReply = {
		callback:updateCgIcrAccount_Callback,
		errorHandler:function( errorMessage ) { 
			window.status = errorMessage;
		}
	};	
	A21SubAccountService.buildCgIcrAccount( chartCode, accountNumber, null, subAccountTypeCode, dwrReply );
}

function updateCgIcrAccount_Callback( data ) {
	// check if the current user has permissions to the ICR fields
	if ( kualiElements["document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier"].type.toLowerCase() == "hidden" ) {
		return;
	}
	
	var prefix = "document.newMaintainableObject.a21SubAccount";
	if (data != null) {
		setElementValue( prefix + ".financialIcrSeriesIdentifier", data.financialIcrSeriesIdentifier );
		setElementValue( prefix + ".indirectCostRcvyFinCoaCode", data.indirectCostRcvyFinCoaCode );
		setElementValue( prefix + ".indirectCostRecoveryAcctNbr", data.indirectCostRecoveryAcctNbr );
		setElementValue( prefix + ".offCampusCode", data.offCampusCode );
		setElementValue( prefix + ".indirectCostRecoveryTypeCode", data.indirectCostRecoveryTypeCode );
	}
	else{
		setElementValue( prefix + ".financialIcrSeriesIdentifier", "" );
		setElementValue( prefix + ".indirectCostRcvyFinCoaCode", "" );
		setElementValue( prefix + ".indirectCostRecoveryAcctNbr", "" );
		setElementValue( prefix + ".offCampusCode", "" );
		setElementValue( prefix + ".indirectCostRecoveryTypeCode", "" );
	}
}

/* This function is for the a21SubAccount account numbers in SubAccount BO. */
function onblur_accountNumberA21Sub( accountNumberField, chartCodePropertyName ) {
	// need to call findElPrefix twice to strip off the a21SubAccount prefix
	var accountNumberFieldName = accountNumberField.name;
	var fieldPrefix = findElPrefix( findElPrefix(accountNumberFieldName) );
	var chartCodeFieldName = fieldPrefix + "." + chartCodePropertyName;
	//alert ("chartCodeFieldName = " + chartCodeFieldName + ", accountNumberFieldName = " + accountNumberFieldName);

	var dwrReply = {
		callback: function (param) {
			if ( typeof param == 'boolean' && param == false) {	
				loadChartCode(chartCodeFieldName, accountNumberFieldName);
			}
		},	
		errorHandler:function( errorMessage ) { 
			window.status = errorMessage;
		}
	};
	AccountService.accountsCanCrossCharts(dwrReply);	
}

function loadChartCode( chartCodeFieldName, accountNumberFieldName ) {
    var accountNumber = getElementValue( accountNumberFieldName );	

    if (accountNumber == "") {
		clearRecipients(chartCodeFieldName);    		
	}
	else {
		var dwrReply = {
				callback: function (data) {
				//alert ("chartCode = " + data.chartOfAccountsCode + ", accountNumber = " + accountNumber);
				if ( data != null && typeof data == 'object' ) {   
					setRecipientValue( chartCodeFieldName, data.chartOfAccountsCode );
				}
				else {
					clearRecipients(chartCodeFieldName); 
				}
			},
			errorHandler:function( errorMessage ) { 
				clearRecipients(chartCodeFieldName); 
				window.status = errorMessage;
			}
		};
		AccountService.getUniqueAccountForAccountNumber( accountNumber, dwrReply );	    
	}
}