/*
 * Copyright 2007 The Kuali Foundation.
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

 function loadIncomeSweepRegistrationCodeDesc(registrationCodeFieldName){
	var elPrefix = findElPrefix( registrationCodeFieldName.name );
	var registrationCodeDescriptionFieldName = elPrefix + ".incomeRegistrationCode.name";
 	setRegistrationCodeDescription(registrationCodeFieldName, registrationCodeDescriptionFieldName);
 }
 
 function loadPrincipleSweepRegistrationCodeDesc(registrationCodeFieldName){
	var elPrefix = findElPrefix( registrationCodeFieldName.name );
	var registrationCodeDescriptionFieldName = elPrefix + ".principleRegistrationCode.name";
 	setRegistrationCodeDescription(registrationCodeFieldName, registrationCodeDescriptionFieldName);
 }
 
 function loadFundRegistrationCodeDesc(registrationCodeFieldName){
		var elPrefix = findElPrefix( registrationCodeFieldName.name );
		var registrationCodeDescriptionFieldName = elPrefix + ".registrationCodeObj.name";
	 	setRegistrationCodeDescription(registrationCodeFieldName, registrationCodeDescriptionFieldName);
 }
/*
 function loadInvestment1RegistrationCodeDesc(registrationCodeFieldName){
	var elPrefix = findElPrefix( registrationCodeFieldName.name );
	var registrationCodeDescriptionFieldName = elPrefix + ".investment1RegistrationCodeObj.name";
 	setRegistrationCodeDescription(registrationCodeFieldName, registrationCodeDescriptionFieldName);
 }

 function loadInvestment2RegistrationCodeDesc(registrationCodeFieldName){
	var elPrefix = findElPrefix( registrationCodeFieldName.name );
	var registrationCodeDescriptionFieldName = elPrefix + ".investment2RegistrationCodeObj.name";
 	setRegistrationCodeDescription(registrationCodeFieldName, registrationCodeDescriptionFieldName);
 }
 
 function loadInvestment3RegistrationCodeDesc(registrationCodeFieldName){
	var elPrefix = findElPrefix( registrationCodeFieldName.name );
	var registrationCodeDescriptionFieldName = elPrefix + ".investment3RegistrationCodeObj.name";
 	setRegistrationCodeDescription(registrationCodeFieldName, registrationCodeDescriptionFieldName);
 }
 
 function loadInvestment4RegistrationCodeDesc(registrationCodeFieldName){
	var elPrefix = findElPrefix( registrationCodeFieldName.name );
	var registrationCodeDescriptionFieldName = elPrefix + ".investment4RegistrationCodeObj.name";
 	setRegistrationCodeDescription(registrationCodeFieldName, registrationCodeDescriptionFieldName);
 }
 */    
 function setRegistrationCodeDescription( registrationCodeFieldName, registrationCodeDescriptionFieldName ){
 
	var registrationCode = dwr.util.getValue( registrationCodeFieldName );
    
	if (registrationCode =='') {
		clearRecipients(registrationCodeDescriptionFieldName, "");
	} else {
		registrationCode = registrationCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( registrationCodeDescriptionFieldName, data.name );
			} else {
				setRecipientValue( registrationCodeDescriptionFieldName, wrapError( "registration code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( registrationCodeDescriptionFieldName, wrapError( "registration code not found" ), true );
			}
		};
		RegistrationCodeService.getByPrimaryKey( registrationCode, dwrReply );
	}
}