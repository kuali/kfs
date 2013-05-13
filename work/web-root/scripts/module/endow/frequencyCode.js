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