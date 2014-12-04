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
function loadKEMIDShortTitle(kemidFieldName){
	var elPrefix = findElPrefix(kemidFieldName.name);
	var kemidShortTitleFieldName = elPrefix + ".kemidObj.shortTitle";
 	setKEMIDShortTitle(kemidFieldName, kemidShortTitleFieldName);
 }
 
function setKEMIDShortTitle( kemidFieldName, kemidShortTitleFieldName ){
	var kemid = dwr.util.getValue( kemidFieldName );
    
	if (kemid =='') {
		clearRecipients(kemidShortTitleFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue(kemidFieldName, data.kemid);				
				setRecipientValue( kemidShortTitleFieldName, data.shortTitle );
			} else {
				setRecipientValue( kemidShortTitleFieldName, wrapError( "kemid not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( kemidShortTitleFieldName, wrapError( "kemid not found" ), true );
			}
		};
		KEMIDService.getByPrimaryKey( kemid, dwrReply );
	}
}
