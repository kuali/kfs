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
function onblur_subFundGroup( sfgField, callbackFunction ) {
    var subFundGroup = getElementValue(sfgField.name);

    if (subFundGroup != '') {
		var dwrReply = {
			callback:callbackFunction,
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
			}
		};
		SubFundGroupService.getByPrimaryId( subFundGroup, dwrReply );
    }
}

function onblur_accountRestrictedStatusCode( codeField, callbackFunction ) {
	var subFundGroupFieldName = getElementValue( findElPrefix( codeField.name ) + ".subFundGroupCode" );
	obblur_subFundGroup( subFundGroupFieldName, callbackFunction );
}

function checkRestrictedStatusCode_Callback( data ) {
	if ( data.accountRestrictedStatusCode != "" ) {
		if ( kualiElements["document.newMaintainableObject.accountRestrictedStatusCode"].type.toLowerCase() != "hidden" ) {
			setElementValue( "document.newMaintainableObject.accountRestrictedStatusCode", data.accountRestrictedStatusCode );
		}
	}
}