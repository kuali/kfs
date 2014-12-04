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

 function loadClassCodeDesc(classCodeFieldName){
	var elPrefix = findElPrefix( classCodeFieldName.name );
	var classCodeDescriptionFieldName = elPrefix + ".classCode.name";
 	setClassCodeDescription(classCodeFieldName, classCodeDescriptionFieldName);
 }
  
 function setClassCodeDescription(classCodeFieldName, classCodeDescriptionFieldName){
 
	var classCode = dwr.util.getValue(classCodeFieldName);
    
	if (classCode =='') {
		clearRecipients(classCodeDescriptionFieldName);
	} else {
		classCode = classCode.toUpperCase();
		
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( classCodeDescriptionFieldName, data.name );
			} else {
				setRecipientValue( classCodeDescriptionFieldName, wrapError( "class code description not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( classCodeDescriptionFieldName, wrapError( "class code description not found" ), true );
			}
		};
		ClassCodeService.getByPrimaryKey( classCode, dwrReply );
	}
}
