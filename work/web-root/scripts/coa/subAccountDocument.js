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
	var subAccountTypeCode = getElementValue( subAccountTypeCodeField.name );

    // clear Edit CG ICR data if subAccountTypeCode is CS 
	if( subAccountTypeCode == "CS" )
	{
		// check if the current user has permissions to the ICR fields
		if ( kualiElements["document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier"].type.toLowerCase() != "hidden" ) {
			setElementValue( "document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier", "" );
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryChartOfAccountsCode", "" );
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryAccountNumber", "" );
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryTypeCode", "" );
		}
	}
}
 
function updateICRAccount(acctField) {
	var chartCode = getElementValue( findElPrefix( acctField.name ) + ".chartOfAccountsCode" );
	var accountCode = getElementValue( acctField.name );
	var subAccountTypeCode = getElementValue( findElPrefix( acctField.name ) + ".a21SubAccount.subAccountTypeCode" );
	
	if ( accountCode != "" && chartCode != "") {
		var dwrReply = {
			callback:updateICRAccount_Callback,
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
			}
		};
		
		A21SubAccountService.buildCgIcrAccount( chartCode, accountCode, null, subAccountTypeCode, dwrReply );
	}
} 

function updateICRAccount_Callback( data ) {
	// check if the current user has permissions to the ICR fields
	if ( kualiElements["document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier"].type.toLowerCase() != "hidden" ) {
		alert("test" + data.indirectCostRecoveryAccountNumber);
		if (data) {
			setElementValue( "document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier", data.financialIcrSeriesIdentifier );
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryChartOfAccountsCode", data.indirectCostRecoveryChartOfAccountsCode );
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryAccountNumber", data.indirectCostRecoveryAccountNumber );
			setElementValue( "document.newMaintainableObject.a21SubAccount.offCampusCode", data.offCampusCode );
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryTypeCode", data.indirectCostRecoveryTypeCode );
		}
	}
}

function updateICR( acctField) {
	var chartCode = getElementValue( findElPrefix( acctField.name ) + ".chartOfAccountsCode" );
	var accountCode = getElementValue( acctField.name );
	var subAccountTypeCode = getElementValue( findElPrefix( acctField.name ) + ".a21SubAccount.subAccountTypeCode" );
	
	if ( accountCode != "" && chartCode != "" && subAccountTypeCode != "CS" ) {
		var dwrReply = {
			callback:updateICR_Callback,
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
			}
		};
		AccountService.getByPrimaryIdWithCaching( chartCode, accountCode, dwrReply );
	}
}

function updateICR_Callback( data ) {
	// check if the current user has permissions to the ICR fields
	if ( kualiElements["document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier"].type.toLowerCase() != "hidden" ) {
		if (data.financialIcrSeriesIdentifier) {
			setElementValue( "document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier", data.financialIcrSeriesIdentifier );
		}
		if (data.indirectCostRcvyFinCoaCode) {
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryChartOfAccountsCode", data.indirectCostRcvyFinCoaCode );
		}
		if (data.indirectCostRecoveryAcctNbr) {
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryAccountNumber", data.indirectCostRecoveryAcctNbr );
		}
		if (data.accountOffCampusIndicator) {
			setElementValue( "document.newMaintainableObject.a21SubAccount.offCampusCode", data.accountOffCampusIndicator );
		}
		if (data.acctIndirectCostRcvyTypeCd) {
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryTypeCode", data.acctIndirectCostRcvyTypeCd );
		}
	}
}

function clearICR2( subAccountTypeCodeField ) {
	var subAccountTypeCode = getElementValue( subAccountTypeCodeField.name );

    // clear Edit CG ICR data if subAccountTypeCode is CS 
	if( subAccountTypeCode == "CS" )
	{
		// check if the current user has permissions to the ICR fields
		if ( kualiElements["document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier"].type.toLowerCase() != "hidden" ) {
			setElementValue( "document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier", "" );
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryChartOfAccountsCode", "" );
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryAccountNumber", "" );
			setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryTypeCode", "" );
		}
	}
}
