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
