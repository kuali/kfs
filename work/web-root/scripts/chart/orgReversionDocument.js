function updateObjectName( objField ) {
	// we want the base label - and the object field is in the detail section
    var detailElPrefix = findElPrefix( objField.name );
    var elPrefix = findElPrefix( detailElPrefix );
	var fiscalYear = getElementValue( elPrefix + ".universityFiscalYear" );
	var chartCode = getElementValue( elPrefix + ".chartOfAccountsCode" );
	var objectCode = objField.value.toUpperCase().trim();
	var nameFieldName = detailElPrefix + ".organizationReversionObject.financialObjectCodeName";
	if ( fiscalYear != "" && chartCode != "" && objectCode != "" ) {
		loadKualiObjectWithCallback( function( responseText ) {
			setRecipientValue( nameFieldName, responseText );
		}, "object", fiscalYear, chartCode, objectCode, "", "", "", "" );
	} else if ( objectCode == "" ) {
		clearRecipients( nameFieldName );
	} else if ( chartCode == "" ) {
		setRecipientValue(nameFieldName, 'chart code is empty');
	} else if ( fiscalYear == "" ) {
		setRecipientValue(nameFieldName, 'fiscal year is missing');	
	}
}
