/*
 * Copyright 2008-2009 The Kuali Foundation
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
function loadSecurityInfo( securityIdFieldName) 
{
    var securityId = DWRUtil.getValue( securityIdFieldName ).toUpperCase();

    var securityCodeField = "security.description";
    var classCodeField = "document.sourceTransactionSecurity.security.securityClassCode";
    var transactionCodeField = "document.sourceTransactionSecurity.security.classCode.securityEndowmentTransactionCode";
    var taxLotIndicatorField = "document.sourceTransactionSecurity.security.classCode.taxLotIndicator";
    

	clearRecipients(securityCodeField, "");
	clearRecipients(classCodeField, "-");
	clearRecipients(transactionCodeField, "-");
	clearRecipients(taxLotIndicatorField, "-");
 
	if (securityId != '') 
	{
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) 
			{
				setRecipientValue( securityCodeField, data[0]);
				setRecipientValue( classCodeField, data[1]);
				setRecipientValue( transactionCodeField, data[2] );
				setRecipientValue( taxLotIndicatorField, data[3] );
			} 
			else 
			{
				setRecipientValue( classCodeField, wrapError( "Security not found" ), true );			
			} },
			errorHandler:function( errorMessage ) 
			{ 
				setRecipientValue( classCodeField, wrapError( "Security not found" ), true );
			}
		};
		EndowmentTransactionDocumentService.getSecurity( securityId, dwrReply );
	}
}
