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

 function loadValueEffectiveDate(valuationDateFieldName){
	var elPrefix = findElPrefix( valuationDateFieldName.name );
	var valueEffectiveDateFieldName = elPrefix + ".valueEffectiveDate";
	var pooledSecurityIDFieldName = elPrefix+ ".pooledSecurityID";
 	setValueEffectiveDate(valuationDateFieldName, pooledSecurityIDFieldName, valueEffectiveDateFieldName);
 }

 
 function setValueEffectiveDate( valuationDateFieldName, pooledSecurityIDFieldName, valueEffectiveDateFieldName ){
 
	var valuationDate = dwr.util.getValue( valuationDateFieldName );
    var pooledSecurityID = dwr.util.getValue(pooledSecurityIDFieldName );
    
	if (valuationDate =='') {
		clearRecipients(valuationDateFieldName, "");
	} else if (pooledSecurityID == '' ){
	    clearRecipients(pooledSecurityIDFieldName, "");	    
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null ) {
				setRecipientValue( valueEffectiveDateFieldName, data );
			} else {
				setRecipientValue( valueEffectiveDateFieldName, wrapError( "fail to display Value Effective Date" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( valueEffectiveDateFieldName, wrapError( "fail to display Value Effective Date" ), true );
			}
		};
		PooledFundValueService.calculateValueEffectiveDateForAjax( valuationDate, pooledSecurityID.toUpperCase(), dwrReply );
	}
}
