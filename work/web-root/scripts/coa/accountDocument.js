/*
 * Copyright 2006 The Kuali Foundation
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

function loadChartCodeUsingAccountNumber(accountNumberField, coaCodePropertyName) {
    var accountNumber = dwr.util.getValue(accountNumberField);
    
	var dwrReply = {
		callback: function (param) {
			if ( typeof param == "boolean" && param == false) {	
				loadChartCodeForBenefitExpenseTransferDocument(accountNumber, coaCodePropertyName);
			}
		},	
		errorHandler:function( errorMessage ) { 
			window.status = errorMessage;
		}
	};
	AccountService.accountsCanCrossCharts(dwrReply);	
}

function loadChartCodeForBenefitExpenseTransferDocument(accountNumber, coaCodePropertyName) {
	var chartCodePropertyName = coaCodePropertyName;
	
	if (accountNumber == "") {
	}
	else {
		var dwrReply = {
				callback: function (data) {
				if ( data != null && typeof data == "object" ) {  
					dwr.util.setValue(chartCodePropertyName, data.chartOfAccountsCode, {escapeHtml:true});					
				}
				else {
					clearRecipients(coaCodePropertyName); 
				}
			},
			errorHandler:function( errorMessage ) { 
				clearRecipients(coaCodePropertyName); 
			}
		};
		AccountService.getUniqueAccountForAccountNumber(accountNumber, dwrReply);	    
	}
}

function onblur_subFundGroup( sfgField, callbackFunction ) {
    var subFundGroup = getElementValue(sfgField.name);

    if (subFundGroup != "") {
		var dwrReply = {
			callback:callbackFunction,
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
			}
		};
		SubFundGroupService.getByPrimaryId( subFundGroup, dwrReply );
    }
}

/* Same as onblur_subFundGroup */
function updateSubFundGroup( sfgFieldName, callbackFunction ) {
    var subFundGroup = getElementValue(sfgFieldName);

    if (subFundGroup != "") {
		var dwrReply = {
			callback:callbackFunction,
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
			}
		};
		SubFundGroupService.getByPrimaryId( subFundGroup, dwrReply );
    }
}

function onblur_accountRestrictedStatusCode( codeField, callbackFunction ) {
	var subFundGroupFieldName = findElPrefix( codeField.name ) + ".subFundGroupCode";
	updateSubFundGroup( subFundGroupFieldName, callbackFunction );
}

function checkRestrictedStatusCode_Callback( data ) {
	if ( data.accountRestrictedStatusCode != "" ) {
		if ( kualiElements["document.newMaintainableObject.accountRestrictedStatusCode"].type.toLowerCase() != "hidden" ) {
			setElementValue( "document.newMaintainableObject.accountRestrictedStatusCode", data.accountRestrictedStatusCode );
		}
	}
}

function update_laborBenefitRateCategoryCode ( codeField ) {
	var acctTypeCd = codeField.value;
	
	var dwrReply = {
		    callback:function(data) {
		    	if(kualiElements["document.newMaintainableObject.laborBenefitRateCategoryCode"].value == ''){
		    		setElementValue ( "document.newMaintainableObject.laborBenefitRateCategoryCode",data);
		    		alert("Setting the Labor Benefit Rate Category Code to the default described in the system parameter DEFAULT_BENEFIT_RATE_CATEGORY_CODE_BY_ACCOUNT_TYPE");
		    	}
		    },
		    errorHandler:function( errorMessage ) {
		    	window.status = errorMessage;
		    }
		};
	AccountService.getDefaultLaborBenefitRateCategoryCodeForAccountType(acctTypeCd, dwrReply);
}



function update_laborBenefitRateCategoryCode ( codeField ) {
	var acctTypeCd = codeField.value;
	
	var dwrReply = {
		    callback:function(data) {
		    	if(kualiElements["document.newMaintainableObject.laborBenefitRateCategoryCode"].value == ''){
		    		setElementValue ( "document.newMaintainableObject.laborBenefitRateCategoryCode",data);
		    		alert("Setting the Labor Benefit Rate Category Code to the default described in the system parameter DEFAULT_BENEFIT_RATE_CATEGORY_CODE_BY_ACCOUNT_TYPE");
		    	}
		    },
		    errorHandler:function( errorMessage ) {
		    	window.status = errorMessage;
		    }
		};
	AccountService.getDefaultLaborBenefitRateCategoryCodeForAccountType(acctTypeCd, dwrReply);
}

function update_laborBenefitRateCategoryCode ( codeField ) {
	var acctTypeCd = codeField.value;
	
	var dwrReply = {
		    callback:function(data) {
		    	if(kualiElements["document.newMaintainableObject.laborBenefitRateCategoryCode"].value == ''){
		    		setElementValue ( "document.newMaintainableObject.laborBenefitRateCategoryCode",data);
		    		alert("Setting the Labor Benefit Rate Category Code to the default described in the system parameter DEFAULT_BENEFIT_RATE_CATEGORY_CODE_BY_ACCOUNT_TYPE");
		    	}
		    },
		    errorHandler:function( errorMessage ) {
		    	window.status = errorMessage;
		    }
		};
	AccountService.getDefaultLaborBenefitRateCategoryCodeForAccountType(acctTypeCd, dwrReply);
}

function update_laborBenefitRateCategoryCode ( codeField ) {
	var acctTypeCd = codeField.value;
	
	var dwrReply = {
		    callback:function(data) {
		    	if(kualiElements["document.newMaintainableObject.laborBenefitRateCategoryCode"].value == ''){
		    		setElementValue ( "document.newMaintainableObject.laborBenefitRateCategoryCode",data);
		    		alert("Setting the Labor Benefit Rate Category Code to the default described in the system parameter DEFAULT_BENEFIT_RATE_CATEGORY_CODE_BY_ACCOUNT_TYPE");
		    	}
		    },
		    errorHandler:function( errorMessage ) {
		    	window.status = errorMessage;
		    }
		};
	AccountService.getDefaultLaborBenefitRateCategoryCodeForAccountType(acctTypeCd, dwrReply);
}

function onblur_accountNumber( accountNumberField, coaCodePropertyName ) {
    //var coaCodeFieldName = findCoaFieldName( accountNumberField.name );
	var accountNumberFieldName = accountNumberField.name;
	var coaCodeFieldName = findElPrefix(accountNumberFieldName) + "." + coaCodePropertyName;
    var accountNumber = getElementValue( accountNumberFieldName );	    
	//alert("coaCodeFieldName = " + coaCodeFieldName + ", accountNumberFieldName = " + accountNumberFieldName);

	var dwrReply = {
		callback: function (param) {
			if ( typeof param == "boolean" && param == false) {	
				loadChartCode(accountNumber, coaCodeFieldName);
			}
		},	
		errorHandler:function( errorMessage ) { 
			window.status = errorMessage;
		}
	};
	AccountService.accountsCanCrossCharts(dwrReply);	
}

/**
 * Special case for chartCode-accountNumber fields that could contain wild cards "@"/"#". 
 */
function onblur_accountNumber_wildCard( accountNumberField, coaCodePropertyName ) {
    //var coaCodeFieldName = findCoaFieldName( accountNumberField.name );
	var accountNumberFieldName = accountNumberField.name;
	var coaCodeFieldName = findElPrefix(accountNumberFieldName) + "." + coaCodePropertyName;
    var accountNumber = getElementValue( accountNumberFieldName );	    
	//alert("coaCodeFieldName = " + coaCodeFieldName + ", accountNumberFieldName = " + accountNumberFieldName);

	var dwrReply = {
		callback: function (param) {
			if ( typeof param == "boolean" && param == false) {	
				// if accountNumber is wild card, copy it to chart code
				if (accountNumber == "@" || accountNumber == "#") {
					setRecipientValue( coaCodeFieldName, accountNumber);
				}
				// otherwise retrieve chart code from account as usual
				else {
					loadChartCode(accountNumber, coaCodeFieldName);
				}
			}
		},	
		errorHandler:function( errorMessage ) { 
			window.status = errorMessage;
		}
	};
	AccountService.accountsCanCrossCharts(dwrReply);	
}

/**
 * Special case when a new Account is created, the chartCode-accountNumber fields in the document can use the new account that's being created;
 * in which case chart code shall be populated from the PK chart code in the document instead of retrieving it from DB using the account number, 
 * as the new account doesn't exist in the DB yet.
 */
function onblur_accountNumber_newAccount( accountNumberField, coaCodePropertyName ) {
    //var coaCodeFieldName = findCoaFieldName( accountNumberField.name );
	var accountNumberFieldName = accountNumberField.name;
	var coaCodeFieldName = findElPrefix(accountNumberFieldName) + "." + coaCodePropertyName;
    var accountNumber = getElementValue( accountNumberFieldName );	 
	//alert("coaCodeFieldName = " + coaCodeFieldName + ", accountNumberFieldName = " + accountNumberFieldName);

    var chartCodePKName = "document.newMaintainableObject.chartOfAccountsCode";
    var accountNumberPKName = "document.newMaintainableObject.accountNumber";
    var chartCodePK = getElementValue(chartCodePKName);
    var accountNumberPK = getElementValue(accountNumberPKName);
    //alert("chartCodePK = " + chartCodePK + ", accountNumberPK = " + accountNumberPK);

	var dwrReply = {
		callback: function (param) {
			if ( typeof param == "boolean" && param == false) {	
				// if accountNumber is the same as accountNumberPK, copy chartCodePK to chart code
				if (accountNumber == accountNumberPK) {
					//alert("accountNumber == accountNumberPK");
					setRecipientValue(coaCodeFieldName, chartCodePK);
				}
				// otherwise retrieve chart code from account as usual
				else {
					loadChartCode(accountNumber, coaCodeFieldName);
				}
			}
		},	
		errorHandler:function( errorMessage ) { 
			window.status = errorMessage;
		}
	};
	AccountService.accountsCanCrossCharts(dwrReply);	
}

function loadChartCode( accountNumber, coaCodeFieldName ) {
	if (accountNumber == "") {
		clearRecipients(coaCodeFieldName);    		
	}
	else {
		var dwrReply = {
				callback: function (data) {
				//alert("chartOfAccountsCode = " + data.chartOfAccountsCode + ", accountNumber = " + accountNumber);
				if ( data != null && typeof data == "object" ) {   
					//var coaValue = data.chartOfAccountsCode + " - " + data.chartOfAccounts.finChartOfAccountDescription;
					setRecipientValue( coaCodeFieldName, data.chartOfAccountsCode );
				}
				else {
					clearRecipients(coaCodeFieldName); 
				}
			},
			errorHandler:function( errorMessage ) { 
				clearRecipients(coaCodeFieldName); 
				window.status = errorMessage;
			}
		};
		AccountService.getUniqueAccountForAccountNumber( accountNumber, dwrReply );	    
	}
}

/*
function findCoaFieldName( accountNumberFieldName ) {
	var index = accountNumberFieldName.indexOf("AccountNumber");    
	var coaCodeFieldName = accountNumberFieldName.substring(0, index) + "ChartOfAccountsCode";
	return coaCodeFieldName;
}    
*/
