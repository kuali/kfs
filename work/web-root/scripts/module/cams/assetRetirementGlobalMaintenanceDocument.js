/*
 * Copyright 2007-2008 The Kuali Foundation
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

function onblur_generateCalculatedTotal(anyAmountField, callbackFunction) {
    var elPrefix = findElPrefix( anyAmountField.name );
    var salePrice = DWRUtil.getValue( elPrefix + ".salePrice" );
    var handlingFee = DWRUtil.getValue( elPrefix + ".handlingFeeAmount" );
    var preventiveMaintenanceAmount = DWRUtil.getValue( elPrefix + ".preventiveMaintenanceAmount" );
	
	if (salePrice != "") {
		var dwrReply = {
			callback :callbackFunction,
			errorHandler : function(errorMessage) {
				setRecipientValue(
						"document.newMaintainableObject.calculatedTotal",
						wrapError("Unable to calculate total based on entered amounts above."), true);
			}
		};
		AssetRetirementService.generateCalculatedTotal(salePrice, handlingFee, preventiveMaintenanceAmount, dwrReply);
	}
}

function generateCalculatedTotal_Callback(data) {
	setRecipientValue("document.newMaintainableObject.calculatedTotal",data);
}
