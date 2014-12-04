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
