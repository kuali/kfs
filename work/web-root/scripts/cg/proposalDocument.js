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
    updateProposalTotalAmount( directAmountField.name, findElPrefix( directAmountField.name ) + ".proposalIndirectCostAmount" );
}

function onblur_proposalIndirectCostAmount( indirectAmountField ) {
    updateProposalTotalAmount( findElPrefix( indirectAmountField.name ) + ".proposalDirectCostAmount", indirectAmountField.name );
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

function today() {
    var now = new Date();
    // Kuali's DateFormatter requires this format, regardless of Locale.
    return (1 + now.getMonth()) + "/" + now.getDate() + "/" + now.getFullYear();
}

function updateProposalTotalAmount( directAmountFieldName, indirectAmountFieldName ) {
    var directAmount = getElementValue( directAmountFieldName );
    var indirectAmount = getElementValue( indirectAmountFieldName );
    var totalFieldName = findElPrefix( directAmountFieldName ) + ".proposalTotalAmount";
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
