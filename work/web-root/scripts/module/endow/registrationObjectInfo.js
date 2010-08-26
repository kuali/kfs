/*
 * Copyright 2008-2009 The Kuali Foundation
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
function loadRegistrationInfoFromTo( registrationCodeFieldName ) 
{
    var registrationCode = DWRUtil.getValue( registrationCodeFieldName ).toUpperCase();
    var splitWords = registrationCodeFieldName.split(".");
    var registrationCodeField = "registration.description";
    
    if (splitWords.length > 2) {
        registrationCodeField = splitWords[1] + "." + registrationCodeField;
    }

    clearRecipients(registrationCodeField, "");
 
    if (registrationCode != '') 
    {
        var dwrReply = {
            callback:function(data) {
            if ( data != null && typeof data == 'object' ) 
            {
                setRecipientValue( registrationCodeFieldName, data.code);               
                setRecipientValue( registrationCodeField, data.name);
            } 
            else 
            {
                setRecipientValue( registrationCodeField, wrapError( "Registration not found" ), true );            
            } },
            errorHandler:function( errorMessage ) 
            { 
                setRecipientValue( registrationCodeField, wrapError( "Registration not found" ), true );
            }
        };
        RegistrationCodeService.getByPrimaryKey( registrationCode, dwrReply );
    }
}

function loadRegistrationInfo( registrationCodeFieldName ) 
{
    var registrationCode = DWRUtil.getValue( registrationCodeFieldName ).toUpperCase();

    var registrationCodeField = "registration.description";

	clearRecipients(registrationCodeField, "");
 
	if (registrationCode != '') 
	{
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) 
			{
				setRecipientValue( registrationCodeFieldName, data.code);				
				setRecipientValue( registrationCodeField, data.name);
			} 
			else 
			{
				setRecipientValue( registrationCodeField, wrapError( "Registration not found" ), true );			
			} },
			errorHandler:function( errorMessage ) 
			{ 
				setRecipientValue( registrationCodeField, wrapError( "Registration not found" ), true );
			}
		};
		RegistrationCodeService.getByPrimaryKey( registrationCode, dwrReply );
	}
}
