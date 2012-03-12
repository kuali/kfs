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
function loadSecurityInfoFromTo( securityIdFieldName ) 
{
    var securityId = dwr.util.getValue( securityIdFieldName ).toUpperCase();
    
    // Selecting the type of Security - Source or Target
    var securityType= null;
    if( securityIdFieldName.match("target") != null)
        securityType = 'target';
    else
        securityType = 'source';
        
    var securityCodeField = securityType + "TransactionSecurity.security.description";
    var classCodeField = "document." + securityType + "TransactionSecurity.security.securityClassCode";
    var transactionCodeField = "document." + securityType + "TransactionSecurity.security.classCode.securityEndowmentTransactionCode";
    var taxLotIndicatorField = "document." + securityType + "TransactionSecurity.security.classCode.taxLotIndicator";
    
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
                setRecipientValue( securityIdFieldName, data[4]);               
                setRecipientValue( securityCodeField, data[0]);
                setRecipientValue( classCodeField, data[1]);
                setRecipientValue( transactionCodeField, data[2] );
                setRecipientValue( taxLotIndicatorField, data[3] );
            } 
            else 
            {
                setRecipientValue( securityCodeField, wrapError( "Security description not found" ), true );            
            } },
            errorHandler:function( errorMessage ) 
            { 
                setRecipientValue( securityCodeField, wrapError( "Security description not found" ), true );
            }
        };
        EndowmentTransactionDocumentService.getSecurity( securityId, dwrReply );
    }
}

function loadSecurityInfo( securityIdFieldName ) 
{
	var securityId = dwr.util.getValue( securityIdFieldName ).toUpperCase();
	
	// Selecting the type of Security - Source or Target
	var securityType= null;
	if( securityIdFieldName.match("target") != null)
		securityType = 'target';
	else
		securityType = 'source';
		
    var securityCodeField = "security.description";
    var classCodeField = "document." + securityType + "TransactionSecurity.security.securityClassCode";
    var transactionCodeField = "document." + securityType + "TransactionSecurity.security.classCode.securityEndowmentTransactionCode";
    var taxLotIndicatorField = "document." + securityType + "TransactionSecurity.security.classCode.taxLotIndicator";
    
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
				setRecipientValue( securityIdFieldName, data[4]);				
				setRecipientValue( securityCodeField, data[0]);
				setRecipientValue( classCodeField, data[1]);
				setRecipientValue( transactionCodeField, data[2] );
				setRecipientValue( taxLotIndicatorField, data[3] );
			} 
			else 
			{
				setRecipientValue( securityCodeField, wrapError( "Security description not found" ), true );			
			} },
			errorHandler:function( errorMessage ) 
			{ 
				setRecipientValue( securityCodeField, wrapError( "Security description not found" ), true );
			}
		};
		EndowmentTransactionDocumentService.getSecurity( securityId, dwrReply );
	}
}
