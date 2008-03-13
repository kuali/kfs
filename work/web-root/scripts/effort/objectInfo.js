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

var totalAmountFiledName ="document.totalOriginalPayrollAmount";

var fringeBenefitFieldNameSuffix = ".fringeBenefitAmount"
var fiscalYearFieldNameSuffix = ".universityFiscalYear";
var objectCodeFieldNameSuffix = ".financialObjectCode";
var chartOfAccountsCodeFieldNameSuffix = ".chartOfAccountsCode";
var payrollAmountFieldNameSuffix = ".effortCertificationPayrollAmount";
var effortPercentFieldNameSuffix = ".effortCertificationUpdatedOverallPercent";

var divSuffix = ".div";
var comma = ",";
var percentageSign = "%";

// recalculate the payroll amount when the effort percent is changed
function recalculatePayrollAmount(effortPercentFieldName, payrollAmountFieldName){
	var fieldNamePrefix = findElPrefix(payrollAmountFieldName);
	var totalPayrollAmount = removeDelimator(DWRUtil.getValue(totalAmountFiledName), comma);
	var effortPercent = parseInt(removeDelimator(DWRUtil.getValue(effortPercentFieldName), comma));
	
	if(payrollAmountFieldName == effortPercentFieldName){
		payrollAmountFieldName = fieldNamePrefix + payrollAmountFieldNameSuffix
	}
	
	if(totalPayrollAmount != '' && effortPercent != ''){
		var updatePayrollAmount = {
			callback:function(data) {
				var amount = new Number(data).toFixed(2);
				var percent = new Number(effortPercent).toFixed(4) + percentageSign;
				
				DWRUtil.setValue( payrollAmountFieldName + divSuffix, amount);
				DWRUtil.setValue( payrollAmountFieldName, amount);
				DWRUtil.setValue( effortPercentFieldName + divSuffix, percent);
				
				recalculateFringeBenefit(fieldNamePrefix, data);
			}
		};
		
		PayrollAmountUtil.recalculatePayrollAmount(totalPayrollAmount, effortPercent, updatePayrollAmount);
	}
}

// recalculate the effort percent when the payroll amount is changed
function recalculateEffortPercent(payrollAmountFieldName, effortPercentFieldName){
	var fieldNamePrefix = findElPrefix(payrollAmountFieldName);
	var totalPayrollAmount = removeDelimator(DWRUtil.getValue(totalAmountFiledName), comma);
	var payrollAmount = removeDelimator(DWRUtil.getValue(payrollAmountFieldName), comma);
	
	if(payrollAmountFieldName == effortPercentFieldName){
		effortPercentFieldName = fieldNamePrefix + effortPercentFieldNameSuffix;
	}
	
	if(totalPayrollAmount != '' && payrollAmount != ''){
		var updateEffortPercent = {
			callback:function(data) {
				var amount = new Number(payrollAmount).toFixed(2);
				var percent = new Number(data).toFixed(4) + percentageSign;
				
				DWRUtil.setValue( payrollAmountFieldName + divSuffix, amount);
				DWRUtil.setValue( effortPercentFieldName + divSuffix, percent);
				DWRUtil.setValue( effortPercentFieldName, Math.round(data));
				
				recalculateFringeBenefit(fieldNamePrefix, payrollAmount);
			}
		};
		
		PayrollAmountUtil.recalculateEffortPercent(totalPayrollAmount, payrollAmount, updateEffortPercent);				
	}
}

// recalculate the fringe benefit when the payroll amount is changes
function recalculateFringeBenefit(fieldNamePrefix, payrollAmount){
	var fiscalYear, objectCode, chartOfAccountsCode, benefitFieldName;
	
	try{
		fiscalYear = DWRUtil.getValue(fieldNamePrefix + fiscalYearFieldNameSuffix);
		objectCode = DWRUtil.getValue(fieldNamePrefix + objectCodeFieldNameSuffix);
		chartOfAccountsCode = DWRUtil.getValue(fieldNamePrefix + chartOfAccountsCodeFieldNameSuffix);		
		benefitFieldName = fieldNamePrefix + fringeBenefitFieldNameSuffix;
	
		if(fiscalYear != '' && objectCode!='' && chartOfAccountsCode != '') {				
			var updateFringeBenefit = function(data) {				
				var benefit = new Number(data).toFixed(2);			
				DWRUtil.setValue( benefitFieldName, benefit);
			};
			
			LaborModuleService.calculateFringeBenefit(fiscalYear, chartOfAccountsCode, objectCode, payrollAmount, updateFringeBenefit);
		}
			}
	catch(err){
  		window.status = err.description;
	}
}

// remove the specified delimators and leading/trailing blanks from the given string
function removeDelimator(stringObject, delimator){
	return stringObject.replace(delimator, "").trim();
}