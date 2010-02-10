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
function accountNameLookup( anyFieldOnAwardAccount ) {
    var elPrefix = findElPrefix( anyFieldOnAwardAccount.name );
    var chartOfAccountsCode = DWRUtil.getValue( elPrefix + ".organizationOwnerChartOfAccountsCode" ).toUpperCase().trim();
    var accountNumber = DWRUtil.getValue( elPrefix + ".organizationOwnerAccountNumber" ).toUpperCase().trim();
    var targetFieldName = elPrefix + ".organizationOwnerAccount.accountName";
    if (chartOfAccountsCode == "" || accountNumber == "") {
        clearRecipients( targetFieldName );
    } else {
        var dwrReply = makeDwrSingleReply( "organizationOwnerAccount", "accountName", targetFieldName);
        AccountService.getByPrimaryIdWithCaching( chartOfAccountsCode, accountNumber, dwrReply);
    }
}

function makeDwrSingleReply( boName, propertyName, targetFieldName ) {
    var friendlyBoName = boName.replace(/([A-Z])/g, ' $1').toLowerCase();
    return {
        callback:function(data) {
            if (data != null && typeof data == 'object') {
                setRecipientValue( targetFieldName, data[propertyName] );
            } else {
                setRecipientValue( targetFieldName, wrapError( friendlyBoName + " not found" ), true );
            }
        },
        errorHandler:function(errorMessage) {
            setRecipientValue( targetFieldName, wrapError( friendlyBoName + " not found" ), true );
        }
    };
}


function onblur_postingYearAndPeriodCode(field, callbackFunction) {
	var postedDate = getElementValue(field.name);

	if (postedDate != "") {
		var dwrReply = {
			callback :callbackFunction,
			errorHandler : function(errorMessage) {
				setRecipientValue(
						"document.newMaintainableObject.add.assetPaymentDetails.postingYear",
						wrapError("Fiscal Year and Period not found"), true);
			}
		};
        AssetGlobalMaintenanceDocumentService.getByStringDate( postedDate, dwrReply);
	}
}

function postingYearAndPeriodCode_Callback(data) {
	setRecipientValue(
			"document.newMaintainableObject.add.assetPaymentDetails.postingYear",
			data.universityFiscalYear);
	setRecipientValue(
			"document.newMaintainableObject.add.assetPaymentDetails.postingPeriodCode",
			data.universityFiscalPeriodCode);
}
