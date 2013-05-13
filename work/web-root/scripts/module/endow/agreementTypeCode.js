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

 function loadAgreementTypeDesc(agreementTypeCodeFieldName)
 { 
	var elPrefix = findElPrefix( agreementTypeCodeFieldName.name );
	var agreementTypeCodeDescFieldName = elPrefix + ".agreementType.name";
	setAgreementTypeDesc(agreementTypeCodeFieldName, agreementTypeCodeDescFieldName);
 }
 
 function setAgreementTypeDesc( agreementTypeCodeFieldName, agreementTypeCodeDescFieldName ){
	 
	var agreementTypeCode = dwr.util.getValue( agreementTypeCodeFieldName );

	if (agreementTypeCode =='') {
		clearRecipients(agreementTypeCodeDescFieldName, "");
	} else {
		agreementTypeCode = agreementTypeCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( agreementTypeCodeDescFieldName, data.name );
			} else {
				setRecipientValue( agreementTypeCodeDescFieldName, wrapError( "Agreement type code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( agreementTypeCodeDescFieldName, wrapError( "Agreement type code not found" ), true );
			}
		};
		AgreementTypeService.getByPrimaryKey(agreementTypeCode, dwrReply );
	}
}