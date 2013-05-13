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

function loadKemidAgreementStatusDateAndCodeDescription(field)
{
	loadKemidAgreementStatusDate(field);
	loadAgreementStatusDesc(field);
}


 function loadAgreementStatusDesc(agreementStatusCodeFieldName){
	var elPrefix = findElPrefix( agreementStatusCodeFieldName.name );
	var agreementStatusCodeDescFieldName = elPrefix + ".agreementStatus.name";
	setAgreementStatusDesc(agreementStatusCodeFieldName, agreementStatusCodeDescFieldName);
 }
 
 function setAgreementStatusDesc( agreementStatusCodeFieldName, agreementStatusCodeDescFieldName ){
	
	var agreementStatusCode = dwr.util.getValue( agreementStatusCodeFieldName );

	if (agreementStatusCode =='') {
		clearRecipients(agreementStatusCodeDescFieldName, "");
	} else {
		agreementStatusCode = agreementStatusCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( agreementStatusCodeDescFieldName, data.name );
			} else {
				setRecipientValue( agreementStatusCodeDescFieldName, wrapError( "Agreement status code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( agreementStatusCodeDescFieldName, wrapError( "Agreement status code not found" ), true );
			}
		};
		AgreementStatusService.getByPrimaryKey(agreementStatusCode, dwrReply );
	}
}