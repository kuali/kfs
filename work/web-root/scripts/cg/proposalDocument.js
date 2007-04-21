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
function onblur_proposalDirectCostAmount( directAmountField ) {
    updateTotalAmount( directAmountField.name, findElPrefix( directAmountField.name ) + ".proposalIndirectCostAmount", "proposalTotalAmount" );
}

function onblur_proposalIndirectCostAmount( indirectAmountField ) {
    updateTotalAmount( findElPrefix( indirectAmountField.name ) + ".proposalDirectCostAmount", indirectAmountField.name, "proposalTotalAmount" );
}

function onblur_proposalStatusCode( proposalStatusCodeField ) {
    var fieldName = proposalStatusCodeField.name;
    if (valueChanged( fieldName )) {
        var code = getElementValue( fieldName );
        var rejectedName = findElPrefix( fieldName ) + ".proposalRejectedDate";
        // if status changed to rejected or withdrawn
        if (code == "R" || code == "W") {
            // then default rejected date to today
            if (getElementValue( rejectedName ) == "") {
                setRecipientValue( rejectedName, today() );
            }
        }
    }
}

function onblur_subcontractorNumber( subcontractorNumberField ) {
    singleKeyLookup( SubcontractorService.getByPrimaryId, subcontractorNumberField, "subcontractor", "subcontractorName" );
}

function onblur_agencyNumber( agencyNumberField ) {
    singleKeyLookup( AgencyService.getByPrimaryId, agencyNumberField, "agency", "fullName" );
}

function onblur_federalPassThroughAgencyNumber( federalPassThroughAgencyNumberField ) {
    singleKeyLookup( AgencyService.getByPrimaryId, federalPassThroughAgencyNumberField, "federalPassThroughAgency", "fullName" );
}

function singleKeyLookup( dwrFunction, primaryKeyField, boName, propertyName ) {
    var primaryKeyValue = DWRUtil.getValue( primaryKeyField.name ).trim();
    var targetFieldName = findElPrefix( primaryKeyField.name ) + "." + boName + "." + propertyName;
    if (primaryKeyValue == "") {
        clearRecipients( targetFieldName );
    } else {
        dwrFunction( primaryKeyValue, makeDwrSingleReply( boName, propertyName, targetFieldName));
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

function organizationNameLookup( anyFieldOnProposalOrganization ) {
    var elPrefix = findElPrefix( anyFieldOnProposalOrganization.name );
    var chartOfAccountsCode = DWRUtil.getValue( elPrefix + ".chartOfAccountsCode" ).toUpperCase().trim();
    var organizationCode = DWRUtil.getValue( elPrefix + ".organizationCode" ).toUpperCase().trim();
    var targetFieldName = elPrefix + ".organization.organizationName";
    if (chartOfAccountsCode == "" || organizationCode == "") {
        clearRecipients( targetFieldName );
    } else {
        var dwrReply = makeDwrSingleReply( "organization", "organizationName", targetFieldName);
        OrganizationService.getByPrimaryIdWithCaching( chartOfAccountsCode, organizationCode, dwrReply);
    }
}

function proposalDirectorIDLookup( userIdField ) {
    var elPrefix = findElPrefix( userIdField.name );
	var userNameFieldName = elPrefix + ".personName";
	var universalIdFieldName = findElPrefix( elPrefix ) + ".personUniversalIdentifier";
	
	loadDirectorInfo( userIdField.name, universalIdFieldName, userNameFieldName );
}

function loadDirectorInfo( userIdFieldName, universalIdFieldName, userNameFieldName ) {
    var userId = DWRUtil.getValue( userIdFieldName ).trim();

    if (userId == "") {
        clearRecipients( universalIdFieldName );
        clearRecipients( userNameFieldName );
    } else {
        var dwrReply = {
            callback:function(data) {
                if ( data != null && typeof data == 'object' ) {
                    setRecipientValue( universalIdFieldName, data.personUniversalIdentifier );
                    setRecipientValue( userNameFieldName, data.personName );
                } else {
                    clearRecipients( universalIdFieldName );
                    setRecipientValue( userNameFieldName, wrapError( "director not found" ), true );
                } },
            errorHandler:function( errorMessage ) {
                clearRecipients( universalIdFieldName );
                setRecipientValue( userNameFieldName, wrapError( "director not found" ), true );
            }
        };
        ProjectDirectorService.getByPersonUserIdentifier( userId, dwrReply );
    }
}

function today() {
    var now = new Date();
    // Kuali's DateFormatter requires this format, regardless of Locale.
    return (1 + now.getMonth()) + "/" + now.getDate() + "/" + now.getFullYear();
}

function updateTotalAmount( directAmountFieldName, indirectAmountFieldName, totalAmountFieldName ) {
    var directAmount = getElementValue( directAmountFieldName );
    var indirectAmount = getElementValue( indirectAmountFieldName );
    var totalFieldName = findElPrefix( directAmountFieldName ) + "."+ totalAmountFieldName;
    if ( isCurrencyNumber( directAmount ) && isCurrencyNumber( indirectAmount ) ) {
        var totalValue = formatCurrency( parseCurrency( directAmount ) + parseCurrency( indirectAmount ) );
        setRecipientValue( totalFieldName, totalValue );
    }
    else {
        setRecipientValue( totalFieldName, "" );
    }
}

function isCurrencyNumber( value ) {
    return /^[($-]*\d{1,3}(,?\d{3})*(\.\d{0,2})?\)?$/.test( value.toString().trim() );
}

function parseCurrency( value ) {
    value = value.toString().trim();
    var negative = /^\(.*\)$/.test(value);
    return (negative ? -1 : 1) * parseFloat( value.replace(/[($,]/g, "") );
}

function formatCurrency( amount ) {
    var negative = amount < 0;
    var roundedParts = (Math.abs(amount) + 0.005).toString().split(".");
    var whole = roundedParts[0];
    var fraction = roundedParts.length < 2 ? "00" : (roundedParts[1] + "00").substring(0, 2);
    var groups = [];
    while (whole.length > 3) {
        groups.unshift( whole.substring(whole.length - 3) );
        whole = whole.substring(0, whole.length - 3);
    }
    if (whole.length > 0) {
        groups.unshift(whole);
    }
    // Kuali's CurrencyFormatter is not displaying the $ symbol, so this function doesn't either.
    return (negative ? "(" : "") + groups.join(",") + "." + fraction + (negative ? ")" : "");
}

function onblur_awardDirectCostAmount( directAmountField ) {
    updateTotalAmount( directAmountField.name, findElPrefix( directAmountField.name ) + ".awardIndirectCostAmount", "awardTotalAmount" );
}

function onblur_awardIndirectCostAmount( indirectAmountField ) {
    updateTotalAmount( findElPrefix( indirectAmountField.name ) + ".awardDirectCostAmount", indirectAmountField.name, "awardTotalAmount" );
}
