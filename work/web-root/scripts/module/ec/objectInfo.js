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

function EffortAmountUpdator(){ 
	totalAmountFiledName ="document.totalOriginalPayrollAmount";
	editableDetailLineTableId = "editableDetailLineTable";
	detailLinesPrefix = "document.effortCertificationDetailLines";
	summarizedDetailLinesPrefix = "document.summarizedDetailLines";
	
	fiscalYearFieldNameSuffix = ".universityFiscalYear";
	objectCodeFieldNameSuffix = ".financialObjectCode";
	chartOfAccountsCodeFieldNameSuffix = ".chartOfAccountsCode";
	chartOfAccountDescSuffix = ".chartOfAccounts.finChartOfAccountDescription";
	accountNumberSuffix = ".accountNumber";
	accountNameSuffix = ".account.accountName";
	chartCodeSuffix = ".chartOfAccountsCode";
	
	payrollAmountFieldNameSuffix = ".effortCertificationPayrollAmount";
	effortPercentFieldNameSuffix = ".effortCertificationUpdatedOverallPercent";
	fringeBenefitFieldNameSuffix = ".fringeBenefitAmount"
	
	federalIndicatorFieldNameSuffix = ".federalOrFederalPassThroughIndicator";
	
	spanSuffix = ".span";
	divSuffix = ".div";
	readonlySuffix = ".readonly";
	comma = ",";
	percentageSign = "%";
}

// recalculate the payroll amount when the effort percent is changed
EffortAmountUpdator.prototype.recalculatePayrollAmount = function(effortPercentFieldName, payrollAmountFieldName){
	var fieldNamePrefix = findElPrefix(effortPercentFieldName);
	var totalPayrollAmount = this.removeDelimator(dwr.util.getValue(totalAmountFiledName), comma);
	var effortPercent = this.removeDelimator(dwr.util.getValue(effortPercentFieldName), comma);
	var message = "Must be an integer between 0 and 100";
	
	if(isNaN(effortPercent) || effortPercent > 100 || effortPercent <0){
		this.displayMessageAfter(effortPercentFieldName, message);
		return;
	}
		
	if(totalPayrollAmount != '' && effortPercent != ''){
		this.setValueByElementId(fieldNamePrefix + payrollAmountFieldNameSuffix + divSuffix, "");
		this.setValueByElementId(effortPercentFieldName + divSuffix, "");
		
		var updatePayrollAmount = {
			callback:function(data) {
				var amount = new Number(data).toFixed(2);
				var percent = effortPercent + percentageSign;
				
				var	payrollAmountFieldName = fieldNamePrefix + payrollAmountFieldNameSuffix;
				effortAmountUpdator.setValueByElementId( payrollAmountFieldName, amount);
				
				var payrollAmountSpanReadonly = payrollAmountFieldName + spanSuffix + readonlySuffix;			
				effortAmountUpdator.setValueByElementId( payrollAmountSpanReadonly, amount);
				
				var effortPercentSpanReadonly = effortPercentFieldName + spanSuffix + readonlySuffix;				
				effortAmountUpdator.setValueByElementId( effortPercentSpanReadonly, percent);
				
				effortAmountUpdator.recalculateFringeBenefit(fieldNamePrefix, data);
			}
		};
		
		PayrollAmountUtil.recalculatePayrollAmount(totalPayrollAmount, parseInt(effortPercent), updatePayrollAmount);
	}
};

// recalculate the effort percent when the payroll amount is changed
EffortAmountUpdator.prototype.recalculateEffortPercent = function(payrollAmountFieldName, effortPercentFieldName){
	var fieldNamePrefix = findElPrefix(payrollAmountFieldName);
	var totalPayrollAmount = parseFloat(this.removeDelimator(dwr.util.getValue(totalAmountFiledName), comma));
	var payrollAmount = this.removeDelimator(dwr.util.getValue(payrollAmountFieldName), comma);	
	
	if(isNaN(payrollAmount) || payrollAmount > totalPayrollAmount || payrollAmount <0){
		this.displayMessageAfter(payrollAmountFieldName, "Must be between 0 and " + totalPayrollAmount);
		return;
	}
	
	if(totalPayrollAmount != '' && payrollAmount != ''){
		this.setValueByElementId(fieldNamePrefix + effortPercentFieldNameSuffix + divSuffix, "");
		this.setValueByElementId(payrollAmountFieldName + divSuffix, "");
		
		var updateEffortPercent = {
			callback:function(data) {
				var percent = Math.round(data);
				var amount = new Number(payrollAmount).toFixed(2);
				
				var effortPercentFieldName = fieldNamePrefix + effortPercentFieldNameSuffix;
				effortAmountUpdator.setValueByElementId( effortPercentFieldName, percent);	
				
				var effortPercentSpanReadonly = effortPercentFieldName + spanSuffix + readonlySuffix;											
				effortAmountUpdator.setValueByElementId( effortPercentSpanReadonly, percent + percentageSign);
								
				var payrollAmountSpanReadonly = payrollAmountFieldName + spanSuffix + readonlySuffix;										
				effortAmountUpdator.setValueByElementId( payrollAmountSpanReadonly, amount);
				
				effortAmountUpdator.recalculateFringeBenefit(fieldNamePrefix, payrollAmount);
			}
		};
		
		PayrollAmountUtil.recalculateEffortPercent(totalPayrollAmount, payrollAmount, updateEffortPercent);	
		
		var detailLinesPrefixName = effortAmountUpdator.removeIndex(fieldNamePrefix);
		effortAmountUpdator.updateTotals(detailLinesPrefixName);			
	}
};

// recalculate the fringe benefit when the payroll amount is changes
EffortAmountUpdator.prototype.recalculateFringeBenefit = function(fieldNamePrefix, payrollAmount){
	var detailLinesPrefixName = effortAmountUpdator.removeIndex(fieldNamePrefix);
	effortAmountUpdator.updateTotals(detailLinesPrefixName);
	
	try{
		var fiscalYear = dwr.util.getValue(fieldNamePrefix + fiscalYearFieldNameSuffix);
		var objectCode = dwr.util.getValue(fieldNamePrefix + objectCodeFieldNameSuffix);
		var chartOfAccountsCode = dwr.util.getValue(fieldNamePrefix + chartOfAccountsCodeFieldNameSuffix);	

		if(fiscalYear != '' && objectCode!='' && chartOfAccountsCode != '') {				
			var updateFringeBenefit = function(data) {				
				var benefit = new Number(data).toFixed(2);
												
				var benefitFieldName = fieldNamePrefix + fringeBenefitFieldNameSuffix;	
				effortAmountUpdator.setValueByElementId( benefitFieldName, benefit);
				
				var benefitFieldNameSpan = benefitFieldName + spanSuffix;	
				effortAmountUpdator.setValueByElementId( benefitFieldNameSpan, benefit);
				
				var benefitFieldNameReadonly = benefitFieldNameSpan + readonlySuffix;
				effortAmountUpdator.setValueByElementId( benefitFieldNameReadonly, benefit);
				
				var detailLinesPrefixName = effortAmountUpdator.removeIndex(fieldNamePrefix);
				effortAmountUpdator.updateTotals(detailLinesPrefixName);
			};
			
			LaborModuleService.calculateFringeBenefit(fiscalYear, chartOfAccountsCode, objectCode, payrollAmount, updateFringeBenefit);
		}
	}catch(err){
  		window.status = err.description;
	}
};

/**
	* Loads account info and chart info when ACCOUNT_CAN_CROSS_CHART is true.
	* @param accountCodeFieldName
	* @param accountNameFieldName
	* @return
	*/
EffortAmountUpdator.prototype.loadAccountInfo = function( accountCodeFieldName, accountNameFieldName ) {
    var elPrefix = findElPrefix( accountCodeFieldName );
    var accountCode = dwr.util.getValue( accountCodeFieldName );
    var coaCode = dwr.util.getValue( elPrefix + chartCodeSuffix );

    if (valueChanged( accountCodeFieldName )) {
        setRecipientValue( elPrefix + subAccountNumberSuffix, "" );
        setRecipientValue( elPrefix + subAccountNameSuffix, "" );
        setRecipientValue( elPrefix + subObjectCodeSuffix, "" );
        setRecipientValue( elPrefix + subObjectCodeNameSuffix, "" );
    }
    
    if (accountCode=='') {
		clearRecipients(accountNameFieldName);
	} else if (coaCode=='') {
		setRecipientValue(accountNameFieldName, wrapError( 'chart code is empty' ), true );
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && data.length >0 ) {
				setRecipientValue( accountNameFieldName, data );
			} else {
				setRecipientValue( accountNameFieldName, wrapError( "account not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( accountNameFieldName, wrapError( "account not found" ), true );
			}
		};
		EffortCertificationForm.loadAccountInfo( coaCode, accountCode, dwrReply );
	}	
}

/**
* Loads account info and chart info when ACCOUNT_CAN_CROSS_CHART is false.
* @param accountCodeFieldName
* @param accountNameFieldName
* @return
*/
EffortAmountUpdator.prototype.loadChartAccountInfo = function( accountCodeFieldName, accountNameFieldName ) {
    var elPrefix = findElPrefix( accountCodeFieldName );
    var accountCode = dwr.util.getValue( accountCodeFieldName );
    var coaCodeFieldName =  elPrefix + chartCodeSuffix ;
    var coaNameFieldName =  elPrefix + chartOfAccountDescSuffix ;

    if (valueChanged( accountCodeFieldName )) {
        setRecipientValue( elPrefix + subAccountNumberSuffix, "" );
        setRecipientValue( elPrefix + subAccountNameSuffix, "" );
        setRecipientValue( elPrefix + subObjectCodeSuffix, "" );
        setRecipientValue( elPrefix + subObjectCodeNameSuffix, "" );
    }
    
    if (accountCode=='') {
		clearRecipients(accountNameFieldName);
		clearRecipients(coaCodeFieldName);
		clearRecipients(coaNameFieldName);    
		
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( accountNameFieldName, data.accountName  );
				setRecipientValue( coaCodeFieldName, data.chartOfAccountsCode );
			    setRecipientValue( coaNameFieldName, data.chartOfAccounts.finChartOfAccountDescription );
			} else {
				setRecipientValue( accountNameFieldName, wrapError( "account not found" ), true );	
				clearRecipients(coaCodeFieldName);  
				clearRecipients(coaNameFieldName);
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( accountNameFieldName, wrapError( "account not found" ), true );
				clearRecipients(coaCodeFieldName);  
				clearRecipients(coaNameFieldName);
			}
		};
		//EffortCertificationForm.loadAccountInfo( coaCode, accountCode, dwrReply );
		 AccountService.getUniqueAccountForAccountNumber( accountCode, dwrReply ); 
	}	
}

EffortAmountUpdator.prototype.setValueByElementId = function(elementId, value){
	if(document.getElementById(elementId) != null || document.getElementsByName(elementId).length > 0){
		dwr.util.setValue( elementId, value);
	}
};

// remove the specified delimators and leading/trailing blanks from the given string
EffortAmountUpdator.prototype.removeDelimator = function(stringObject, delimator){
	return stringObject.replace(delimator, "").trim();
};

// remove the specified delimators and leading/trailing blanks from the given string
EffortAmountUpdator.prototype.removeIndex = function(stringObject){
	var index = stringObject.lastIndexOf('[');
	
	return stringObject.substring(0, index).trim();
};

// format the given number as the currency format
EffortAmountUpdator.prototype.formatNumberAsCurrency = function(number, currencySymbol) {
	if(currencySymbol == null){
		currencySymbol = "";
	}
	
	// get the fraction part of the given number
	var fractionRegex = /\.\d{1,}/;	
	var fractionPart = (fractionRegex.test(number)) ? fractionRegex.exec(number) : "";
	
	// get the integer part of the given number and format it through putting commas
	var integerPart = parseInt(number,10).toString( );
	var integerRegex = /(-?\d+)(\d{3})/;	
	while (integerRegex.test(integerPart)) {
		integerPart = integerPart.replace(integerRegex, "$1,$2");
	}
	
	return currencySymbol + integerPart + fractionPart;
};

EffortAmountUpdator.prototype.displayMessageAfter = function(elementId, message) {
	var currentNode = document.getElementById(elementId);	
	var elementFieldName = elementId + divSuffix;
	
	if(document.getElementById(elementFieldName) == null && document.getElementsByName(elementFieldName).length <= 0){
   		var messageElement = document.createElement('div');

   		messageElement.setAttribute("id", elementFieldName);
   		messageElement.setAttribute("name", elementFieldName);   		   	
		currentNode.parentNode.insertBefore(messageElement, currentNode);
	}   	
   	this.setValueByElementId(elementFieldName, wrapError(message));	
};

// update all values in total fields
EffortAmountUpdator.prototype.updateTotals = function(detailLinesPrefixName){
	// update the payroll amount totals
	totalFieldId = "document.totalPayrollAmount";
	federalTotalFieldId = "document.federalTotalPayrollAmount";
	otherTotalFieldId = "document.otherTotalPayrollAmount";
	this.updateTotalField(editableDetailLineTableId, detailLinesPrefixName, payrollAmountFieldNameSuffix, false, totalFieldId, federalTotalFieldId, otherTotalFieldId);

	// update the effort percent totals
	totalFieldId = "document.totalEffortPercent";
	federalTotalFieldId = "document.federalTotalEffortPercent";
	otherTotalFieldId = "document.otherTotalEffortPercent";
	this.updateTotalField(editableDetailLineTableId, detailLinesPrefixName, effortPercentFieldNameSuffix, true, totalFieldId, federalTotalFieldId, otherTotalFieldId);
	
	// update the fringe benefit totals
	totalFieldId = "document.totalFringeBenefit";
	federalTotalFieldId = "document.federalTotalFringeBenefit";
	otherTotalFieldId = "document.otherTotalFringeBenefit";
	this.updateTotalField(editableDetailLineTableId, detailLinesPrefixName, fringeBenefitFieldNameSuffix, false, totalFieldId, federalTotalFieldId, otherTotalFieldId);
};

// update the specific total fields
EffortAmountUpdator.prototype.updateTotalField = function(detailLineTableId, detailLinesPrefixName, amountFieldSuffix, isPercent, totalFieldId, federalTotalFieldId, otherTotalFieldId){
	var federalTotal = 0.0;
	var otherTotal = 0.0;
	
	var numOfTableRows = document.getElementById(detailLineTableId).rows.length;  	
  	for (var index = 0; index < numOfTableRows; index++) {
  		var indexHolder = "[" + index + "]";
  		var amountFieldId = detailLinesPrefixName + indexHolder + amountFieldSuffix;
  		var fereralIndicatorFieldId = detailLinesPrefixName + indexHolder + federalIndicatorFieldNameSuffix;
  		
  		var nodes = document.getElementsByName(amountFieldId);
  		if(document.getElementById(amountFieldId) == null && nodes.length <= 0){
  			continue;
  		}
  		
  		var lineAmount = parseFloat(this.removeDelimator(dwr.util.getValue(amountFieldId), comma));  		
  		var federalIndicator = dwr.util.getValue(fereralIndicatorFieldId); 		
  		if(federalIndicator.toUpperCase() == "YES"){
  			federalTotal += lineAmount;	
  		}
  		else{
  			otherTotal += lineAmount;
  		} 
 	}
 	
 	var formattedFederalTotal, formattedOtherTotal, formattedGrandTotal;
 	if(!isPercent){
	 	formattedFederalTotal = this.formatNumberAsCurrency(new Number(federalTotal).toFixed(2));
	 	formattedOtherTotal = this.formatNumberAsCurrency(new Number(otherTotal).toFixed(2));
	 	formattedGrandTotal = this.formatNumberAsCurrency(new Number(federalTotal + otherTotal).toFixed(2));
 	}
 	else{
 		formattedFederalTotal = this.formatNumberAsCurrency(Math.round(federalTotal)) + percentageSign;
	 	formattedOtherTotal = this.formatNumberAsCurrency(Math.round(otherTotal)) + percentageSign;
	 	formattedGrandTotal = this.formatNumberAsCurrency(Math.round(federalTotal + otherTotal)) + percentageSign;
 	}
 	
 	this.setValueByElementId( federalTotalFieldId, formattedFederalTotal);
 	this.setValueByElementId( otherTotalFieldId, formattedOtherTotal);
 	this.setValueByElementId( totalFieldId, formattedGrandTotal);
 	
 	//this.setValueByElementId( federalTotalFieldId + readonlySuffix, formattedFederalTotal);
 	//this.setValueByElementId( otherTotalFieldId + readonlySuffix, formattedOtherTotal);
 	//this.setValueByElementId( totalFieldId + readonlySuffix, formattedGrandTotal);
};

var effortAmountUpdator = new EffortAmountUpdator();
