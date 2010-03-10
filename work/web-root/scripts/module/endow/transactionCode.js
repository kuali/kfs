/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

function loadEndTransDesc(securityEndowmentTransactionCodeFieldName){
	var elPrefix = findElPrefix( securityEndowmentTransactionCodeFieldName.name );
	var securityEndowmentTransactionCodeDescFieldName = elPrefix + ".endowmentTransactionCode.name";
 	setEndTransDesc(securityEndowmentTransactionCodeFieldName, securityEndowmentTransactionCodeDescFieldName);
 }
 
 function loadIncEndTransPostDesc(securityEndowmentTransactionCodeFieldName){
    var elPrefix = findElPrefix( securityEndowmentTransactionCodeFieldName.name );
	var securityEndowmentTransactionCodeDescFieldName = elPrefix + ".incomeEndowmentTransactionPost.name";
 	setEndTransDesc(securityEndowmentTransactionCodeFieldName, securityEndowmentTransactionCodeDescFieldName);
 }
 
 
 function loadAssetPurchaseOffsetTranCodeDesc(securityEndowmentTransactionCodeFieldName){
    var elPrefix = findElPrefix( securityEndowmentTransactionCodeFieldName.name );
	var securityEndowmentTransactionCodeDescFieldName = elPrefix + ".assetPurchaseOffsetTranCode.name";
 	setEndTransDesc(securityEndowmentTransactionCodeFieldName, securityEndowmentTransactionCodeDescFieldName);
 }
 
 function loadAssetSaleOffsetTranCodeDesc(securityEndowmentTransactionCodeFieldName){
    var elPrefix = findElPrefix( securityEndowmentTransactionCodeFieldName.name );
	var securityEndowmentTransactionCodeDescFieldName = elPrefix + ".assetSaleOffsetTranCode.name";
 	setEndTransDesc(securityEndowmentTransactionCodeFieldName, securityEndowmentTransactionCodeDescFieldName);
 }

 function loadSaleGainLossOffsetTranCodeDesc(securityEndowmentTransactionCodeFieldName){
    var elPrefix = findElPrefix( securityEndowmentTransactionCodeFieldName.name );
	var securityEndowmentTransactionCodeDescFieldName = elPrefix + ".saleGainLossOffsetTranCode.name";
 	setEndTransDesc(securityEndowmentTransactionCodeFieldName, securityEndowmentTransactionCodeDescFieldName);
 }
 
 function loadCashDepositOffsetTranCodeDesc(securityEndowmentTransactionCodeFieldName){
    var elPrefix = findElPrefix( securityEndowmentTransactionCodeFieldName.name );
	var securityEndowmentTransactionCodeDescFieldName = elPrefix + ".cashDepositOffsetTranCode.name";
 	setEndTransDesc(securityEndowmentTransactionCodeFieldName, securityEndowmentTransactionCodeDescFieldName);
 } 
  
 function setEndTransDesc( securityEndowmentTransactionCodeFieldName, securityEndowmentTransactionCodeDescFieldName ){
	
	var securityEndowmentTransactionCode = DWRUtil.getValue( securityEndowmentTransactionCodeFieldName );

	if (securityEndowmentTransactionCode=='') {
		clearRecipients(securityEndowmentTransactionCodeDescFieldName, "");
	} else {
		securityEndowmentTransactionCode = securityEndowmentTransactionCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( securityEndowmentTransactionCodeDescFieldName, data.name );
			} else {
				setRecipientValue( securityEndowmentTransactionCodeDescFieldName, wrapError( "endowment transaction code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( securityEndowmentTransactionCodeDescFieldName, wrapError( "endowment transaction code not found" ), true );
			}
		};
		EndowmentTransactionCodeService.getByPrimaryKey( securityEndowmentTransactionCode, dwrReply );
	}
}