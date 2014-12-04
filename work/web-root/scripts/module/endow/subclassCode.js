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

 function loadSubClassCodeDesc(subclassCodeFieldName){
	var elPrefix = findElPrefix( subclassCodeFieldName.name );
	var subclassCodeDescFieldName = elPrefix + ".subclassCode.name";
 	setSubclassCodeDesc(subclassCodeFieldName, subclassCodeDescFieldName);
 }
 
 function setSubclassCodeDesc( subclassCodeFieldName, subclassCodeDescFieldName ){
 
	var subclassCode = dwr.util.getValue( subclassCodeFieldName );

	if (subclassCode =='') {
		clearRecipients(subclassCodeDescFieldName, "");
	} else {
		subclassCode = subclassCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( subclassCodeDescFieldName, data.name );
			} else {
				setRecipientValue( subclassCodeDescFieldName, wrapError( "subclass code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( subclassCodeDescFieldName, wrapError( "subclass code not found" ), true );
			}
		};
		SubClassCodeService.getByPrimaryKey( subclassCode, dwrReply );
	}
}
