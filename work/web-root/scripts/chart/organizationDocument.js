function updateLocation( orgField, callbackFunction ) {
	var postalCode = getElementValue( findElPrefix( orgField.name ) + ".organizationZipCode" );
	if ( orgField.value.trim() != "" && postalCode != "" ) {
		loadKualiObjectWithCallback( callbackFunction, "orgLocation", postalCode, "", "", "", "", "" );
	}
}

function updateLocation_Callback( responseText ) {
	var data = responseText.parseJSON();

	setRecipientValue( "document.newMaintainableObject.organizationCityName", data.organizationCityName );
	setRecipientValue( "document.newMaintainableObject.organizationStateCode", data.organizationStateCode );
}