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

 function loadResponsibleAdministrationCodeDesc(responsibleAdministrationCodeFieldName){
	var elPrefix = findElPrefix( responsibleAdministrationCodeFieldName.name );
	var responsibleAdministrationCodeDescFieldName = elPrefix + ".responsibleAdministration.name";
 	setResponsibleAdministrationCodeDesc(responsibleAdministrationCodeFieldName, responsibleAdministrationCodeDescFieldName);
 }
 
 function setResponsibleAdministrationCodeDesc(responsibleAdministrationCodeFieldName, responsibleAdministrationCodeDescFieldName ){
 
	var responsibleAdministrationCode = dwr.util.getValue( responsibleAdministrationCodeFieldName );

	if (responsibleAdministrationCode =='') {
		clearRecipients(responsibleAdministrationCodeDescFieldName, "");
	} else {
		responsibleAdministrationCode = responsibleAdministrationCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( responsibleAdministrationCodeDescFieldName, data.name );
			} else {
				setRecipientValue( responsibleAdministrationCodeDescFieldName, wrapError( "Responsible Department not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( responsibleAdministrationCodeDescFieldName, wrapError( "Responsible Department not found" ), true );
			}
		};
		ResponsibleAdministrationCodeService.getByPrimaryKey(responsibleAdministrationCode, dwrReply );
	}
}