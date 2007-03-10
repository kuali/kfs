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
function updateLocation( postalCodeField, callbackFunction ) {
	var postalCode = getElementValue( postalCodeField.name );
	if ( postalCode != "" ) {
		var dwrReply = {
			callback:callbackFunction,
			errorHandler:function( errorMessage ) { 
				setRecipientValue( "document.newMaintainableObject.organizationCityName", wrapError( "postal code not found" ), true );
				clearRecipients( "document.newMaintainableObject.organizationStateCode" );
			}
		};
		PostalZipCodeService.getByPrimaryId( postalCode, dwrReply );
	}	
}

function updateLocation_Callback( data ) {
	setRecipientValue( "document.newMaintainableObject.organizationCityName", data.postalCityName );
	setRecipientValue( "document.newMaintainableObject.organizationStateCode", data.postalStateCode );
}