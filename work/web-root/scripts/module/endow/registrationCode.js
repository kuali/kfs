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
