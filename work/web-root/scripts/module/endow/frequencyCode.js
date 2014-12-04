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

 function loadCashSweepFrequencyCodeDesc(frequencyCodeFieldName){
	var elPrefix = findElPrefix( frequencyCodeFieldName.name );
	var frequencyCodeDescriptionFieldName = elPrefix + ".cashSweepFrequencyCodeObj.name";
 	setFrequencyCodeDescription(frequencyCodeFieldName, frequencyCodeDescriptionFieldName);
 }
 
 function loadACIFrequencyCodeDesc(frequencyCodeFieldName){
	var elPrefix = findElPrefix( frequencyCodeFieldName.name );
	var frequencyCodeDescriptionFieldName = elPrefix + ".aciFrequencyCodeObj.name";
 	setFrequencyCodeDescription(frequencyCodeFieldName, frequencyCodeDescriptionFieldName);
 }
 
 function loadFrequencyCodeDesc(frequencyCodeFieldName){
	var elPrefix = findElPrefix( frequencyCodeFieldName.name );
	var frequencyCodeDescriptionFieldName = elPrefix + ".frequencyCode.name";
 	setFrequencyCodeDescription(frequencyCodeFieldName, frequencyCodeDescriptionFieldName);
 }
 
 function setFrequencyCodeDescription( frequencyCodeFieldName, frequencyCodeDescriptionFieldName ){
 
	var frequencyCode = dwr.util.getValue( frequencyCodeFieldName );
    
	if (frequencyCode =='') {
		clearRecipients(frequencyCodeDescriptionFieldName, "");
	} else {
		frequencyCode = frequencyCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( frequencyCodeDescriptionFieldName, data.name );
			} else {
				setRecipientValue( frequencyCodeDescriptionFieldName, wrapError( "frequency code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( frequencyCodeDescriptionFieldName, wrapError( "frequency code not found" ), true );
			}
		};
		FrequencyCodeService.getByPrimaryKey( frequencyCode, dwrReply );
	}
}
