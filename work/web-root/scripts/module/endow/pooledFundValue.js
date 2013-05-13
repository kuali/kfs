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