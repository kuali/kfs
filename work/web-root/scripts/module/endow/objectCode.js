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
function loadObjectName( objField ) {
  var elPrefix = findElPrefix( objField.name );
	var coaCode = getElementValue( elPrefix + ".chartCode" );
	var objectCode = getElementValue( objField.name );
	var nameFieldName = elPrefix + ".financialObjectCode.financialObjectCodeName";
	if ( coaCode != "" && objectCode != "" ) {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( nameFieldName, data.financialObjectCodeName );
			} else {
				setRecipientValue( nameFieldName, wrapError( "object not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
				setRecipientValue( nameFieldName, wrapError( "object not found" ), true );
			}
		};
		ObjectCodeService.getByPrimaryIdForCurrentYear( coaCode, objectCode, dwrReply );
	} else if ( objectCode == "" ) {
		clearRecipients( nameFieldName );
	} else if ( coaCode == "" ) {
		setRecipientValue(nameFieldName, wrapError( 'chart code is empty' ), true );
	} else if ( fiscalYear == "" ) {
		setRecipientValue(nameFieldName, wrapError( 'fiscal year is missing' ), true );	
	}
}
