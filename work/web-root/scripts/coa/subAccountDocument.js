/*
 * Copyright 2006-2007 The Kuali Foundation.
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
function clearICR( subAccountTypeCodeField ) {
	var subAccountTypeCodeFieldPrefix = findElPrefix( subAccountTypeCodeField.name );
	var accountFieldPrefix = findElPrefix(subAccountTypeCodeFieldPrefix);
	var acctFieldName = accountFieldPrefix + ".accountNumber";

	updateCgIcrAccount(acctFieldName);
}
 
function updateICRAccount(acctField) {
	var acctFieldName = acctField.name;	
	if(acctFieldName != ""){
		updateCgIcrAccount(acctFieldName);
	}
}

function updateCgIcrAccount(acctFieldName) {
	var fieldPrefix = findElPrefix( acctFieldName );
	
	var accountNumber = getElementValue( acctFieldName );
	var chartCode = getElementValue( fieldPrefix + ".chartOfAccountsCode" );
	var subAccountTypeCode = getElementValue( fieldPrefix + ".a21SubAccount.subAccountTypeCode" );
	
	if ( accountNumber != "" && chartCode != "") {
		var dwrReply = {
			callback:updateCgIcrAccount_Callback,
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
			}
		};
		
		A21SubAccountService.buildCgIcrAccount( chartCode, accountNumber, null, subAccountTypeCode, dwrReply );
	}
}

function updateCgIcrAccount_Callback( data ) {
	// check if the current user has permissions to the ICR fields
	if ( kualiElements["document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier"].type.toLowerCase() == "hidden" ) {
		return;
	}
	
	var prefix = "document.newMaintainableObject.a21SubAccount";
	if (data != null) {
		setElementValue( prefix + ".financialIcrSeriesIdentifier", data.financialIcrSeriesIdentifier );
		setElementValue( prefix + ".indirectCostRecoveryChartOfAccountsCode", data.indirectCostRecoveryChartOfAccountsCode );
		setElementValue( prefix + ".indirectCostRecoveryAccountNumber", data.indirectCostRecoveryAccountNumber );
		setElementValue( prefix + ".offCampusCode", data.offCampusCode );
		setElementValue( prefix + ".indirectCostRecoveryTypeCode", data.indirectCostRecoveryTypeCode );
	}
	else{
		setElementValue( prefix + ".financialIcrSeriesIdentifier", "" );
		setElementValue( prefix + ".indirectCostRecoveryChartOfAccountsCode", "" );
		setElementValue( prefix + ".indirectCostRecoveryAccountNumber", "" );
		setElementValue( prefix + ".offCampusCode", "" );
		setElementValue( prefix + ".indirectCostRecoveryTypeCode", "" );
	}
}