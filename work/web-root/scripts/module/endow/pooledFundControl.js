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