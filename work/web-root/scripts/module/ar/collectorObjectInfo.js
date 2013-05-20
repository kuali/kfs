/*
 * Copyright 2007 The Kuali Foundation
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
function proposalCollectorIDLookup( userIdField ) {
    var userIdFieldName = userIdField.name;
    var elPrefix = findElPrefix( userIdFieldName );
	var userNameFieldName = elPrefix + ".name";
	var universalIdFieldName = findElPrefix( elPrefix ) + ".principalId";
	
	loadCustomerCollectorInfo( userIdFieldName, universalIdFieldName, userNameFieldName );
}

function loadCustomerCollectorInfo( userIdFieldName, universalIdFieldName, userNameFieldName ) {
    var userId = DWRUtil.getValue( userIdFieldName ).trim();

    if (userId == "") {
        clearRecipients( universalIdFieldName );
        clearRecipients( userNameFieldName );
    } else {
        var dwrReply = {
            callback:function(data) {
                if ( data != null && typeof data == 'object' ) {
                    setRecipientValue( universalIdFieldName, data.principalId );
                    setRecipientValue( userNameFieldName, data.name );
                } else {
                    clearRecipients( universalIdFieldName );
                    setRecipientValue( userNameFieldName, wrapError( "collector not found" ), true );
                } },
            errorHandler:function( errorMessage ) {
                clearRecipients( universalIdFieldName );
                setRecipientValue( userNameFieldName, wrapError( "collector not found" ), true );
            }
        };
        PersonService.getPersonByPrincipalName( userId, dwrReply );
    }
}
