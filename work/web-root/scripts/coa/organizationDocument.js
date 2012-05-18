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
function updateLocationByPostalCode( postalCodeField, callbackFunction ) {
	var postalCode = getElementValue( postalCodeField.name );
	var postalCountryCode = getElementValue( findElPrefix( postalCodeField.name ) + ".organizationCountryCode" );
	
	if ( postalCode != "" && postalCountryCode != "" ) {
		var dwrReply = {
			callback:callbackFunction,
			errorHandler:function( errorMessage ) { 
				setRecipientValue( "document.newMaintainableObject.organizationCityName", wrapError( "postal code not found"), true );
				clearRecipients( "document.newMaintainableObject.organizationStateCode" );
			}
		};
		PostalCodeService.getPostalCode(postalCountryCode, postalCode, dwrReply );
	}	
}

function updateLocationByPostalCode_Callback( data ) {
	setRecipientValue( "document.newMaintainableObject.organizationCityName", data.cityName );
	setRecipientValue( "document.newMaintainableObject.organizationStateCode", data.stateCode );
}

function updateLocationByCountryCode( countryCodeField, callbackFunction ) {
	var postalCountryCode = getElementValue( countryCodeField.name );
	var postalCode = getElementValue( findElPrefix( countryCodeField.name ) + ".organizationZipCode" );
	
	if ( postalCode != "" && postalCountryCode != "" ) {
		var dwrReply = {
			callback:callbackFunction,
			errorHandler:function( errorMessage ) { 
				setRecipientValue( "document.newMaintainableObject.organizationCityName", wrapError( "postal code not found"), true );
				clearRecipients( "document.newMaintainableObject.organizationStateCode" );
			}
		};
		PostalCodeService.getPostalCode(postalCountryCode, postalCode, dwrReply );
	}	
}

function updateLocationByCountryCode_Callback( data ) {
	setRecipientValue( "document.newMaintainableObject.organizationCityName", data.cityName );
	setRecipientValue( "document.newMaintainableObject.organizationStateCode", data.stateCode );
}
