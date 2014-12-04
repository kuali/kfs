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

 function loadPooledFundControlDesc(pooledSecurityIDFieldName){
	var elPrefix = findElPrefix( pooledSecurityIDFieldName.name );
	var pooledFundDescriptionFieldName = elPrefix + ".pooledFundControl.pooledFundDescription";
 	setPooledFundDescription(pooledSecurityIDFieldName, pooledFundDescriptionFieldName);
 }
 
 function loadIncomeSweepInvestmentDesc(pooledSecurityIDFieldName){
	var elPrefix = findElPrefix( pooledSecurityIDFieldName.name );
	var pooledFundDescriptionFieldName = elPrefix + ".incomePooledFundControl.pooledFundDescription";
 	setPooledFundDescription(pooledSecurityIDFieldName, pooledFundDescriptionFieldName);
 }
 
 function loadPrincipleSweepInvestmentDesc(pooledSecurityIDFieldName){
	var elPrefix = findElPrefix( pooledSecurityIDFieldName.name );
	var pooledFundDescriptionFieldName = elPrefix + ".principlePooledFundControl.pooledFundDescription";
 	setPooledFundDescription(pooledSecurityIDFieldName, pooledFundDescriptionFieldName);
 }
 
 function loadInvestment1Desc(pooledSecurityIDFieldName){
	var elPrefix = findElPrefix( pooledSecurityIDFieldName.name );
	var pooledFundDescriptionFieldName = elPrefix + ".investment1.pooledFundDescription";
 	setPooledFundDescription(pooledSecurityIDFieldName, pooledFundDescriptionFieldName);
 }
 
 function loadInvestment2Desc(pooledSecurityIDFieldName){
	var elPrefix = findElPrefix( pooledSecurityIDFieldName.name );
	var pooledFundDescriptionFieldName = elPrefix + ".investment2.pooledFundDescription";
 	setPooledFundDescription(pooledSecurityIDFieldName, pooledFundDescriptionFieldName);
 }

 function loadInvestment3Desc(pooledSecurityIDFieldName){
	var elPrefix = findElPrefix( pooledSecurityIDFieldName.name );
	var pooledFundDescriptionFieldName = elPrefix + ".investment3.pooledFundDescription";
 	setPooledFundDescription(pooledSecurityIDFieldName, pooledFundDescriptionFieldName);
 }  
 
 function loadInvestment4Desc(pooledSecurityIDFieldName){
	var elPrefix = findElPrefix( pooledSecurityIDFieldName.name );
	var pooledFundDescriptionFieldName = elPrefix + ".investment4.pooledFundDescription";
 	setPooledFundDescription(pooledSecurityIDFieldName, pooledFundDescriptionFieldName);
 } 
 
 function setPooledFundDescription( pooledSecurityIDFieldName, pooledFundDescriptionFieldName ){
 
	var pooledSecurityID = dwr.util.getValue( pooledSecurityIDFieldName );
    
	if (pooledSecurityID =='') {
		clearRecipients(pooledFundDescriptionFieldName, "");
	} else {
		pooledSecurityID = pooledSecurityID.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( pooledFundDescriptionFieldName, data.pooledFundDescription );
			} else {
				setRecipientValue( pooledFundDescriptionFieldName, wrapError( "pooled fund control not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( pooledFundDescriptionFieldName, wrapError( "pooled fund control not found" ), true );
			}
		};
		PooledFundControlService.getByPrimaryKey( pooledSecurityID, dwrReply );
	}
}
