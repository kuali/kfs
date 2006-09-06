function updateICR( acctField, callbackFunction ) {
	var chartCode = getElementValue( findElPrefix( acctField.name ) + ".chartOfAccountsCode" );
	if ( acctField.value.trim() != "" && chartCode != "" ) {
		loadKualiObjectWithCallback( callbackFunction, "accountICR", chartCode, acctField.value.toUpperCase().trim(), "", "", "", "" );
	}
}

function updateICR_Callback( responseText ) {
	var data = responseText.parseJSON();

	// check if the current user has permissions to the ICR fields
	if ( kualiElements["document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier"].type.toLowerCase() != "hidden" ) {
		setElementValue( "document.newMaintainableObject.a21SubAccount.financialIcrSeriesIdentifier", data.financialIcrSeriesIdentifier );
		setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryChartOfAccountsCode", data.indirectCostRecoveryChartOfAccountsCode );
		setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryAccountNumber", data.indirectCostRecoveryAccountNumber );
		setElementValue( "document.newMaintainableObject.a21SubAccount.offCampusCode", data.offCampusCode );
		setElementValue( "document.newMaintainableObject.a21SubAccount.indirectCostRecoveryTypeCode", data.indirectCostRecoveryTypeCode );
	}
}