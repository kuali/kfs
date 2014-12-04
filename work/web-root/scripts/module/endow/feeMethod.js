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

 function loadFeeMethodDesc(feeMethodCodeFieldName){
	var elPrefix = findElPrefix( feeMethodCodeFieldName.name );
	var feeMethodCodeDescriptionFieldName = elPrefix + ".feeMethod.name";
 	setFeeMethodCodeDescription(feeMethodCodeFieldName, feeMethodCodeDescriptionFieldName);
 }
  
 function setFeeMethodCodeDescription( feeMethodCodeFieldName, feeMethodCodeDescriptionFieldName ){
 
	var feeMethodCode = dwr.util.getValue( feeMethodCodeFieldName );
    
	if (feeMethodCode =='') {
		clearRecipients(feeMethodCodeDescriptionFieldName);
	} else {
		feeMethodCode = feeMethodCode.toUpperCase();
		
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( feeMethodCodeDescriptionFieldName, data.name );
			} else {
				setRecipientValue( feeMethodCodeDescriptionFieldName, wrapError( "fee method description not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( feeMethodCodeDescriptionFieldName, wrapError( "fee method description not found" ), true );
			}
		};
		FeeMethodService.getByPrimaryKey( feeMethodCode, dwrReply );
	}
}
