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
