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
 
 function loadFreqCodeDesc(freqCodeFieldName){
	var elPrefix = findElPrefix( freqCodeFieldName.name );
	var freqCodeDescFieldName = elPrefix + ".frequencyCode.name";
 	setFreqDesc(freqCodeFieldName, freqCodeDescFieldName);
 }
 
 function setFreqDesc( freqCodeFieldName, freqCodeDescFieldName ){
 
	var freqCode = dwr.util.getValue( freqCodeFieldName );

	if (freqCode =='') {
		clearRecipients(freqCodeDescFieldName, "");
	} else {
		freqCode = freqCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( freqCodeDescFieldName, data.name );
			} else {
				setRecipientValue( freqCodeDescFieldName, wrapError( "frequency code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( freqCodeDescFieldName, wrapError( "frequency code not found" ), true );
			}
		};
		FrequencyCodeService.getByPrimaryKey( freqCode, dwrReply );
	}
}
