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
