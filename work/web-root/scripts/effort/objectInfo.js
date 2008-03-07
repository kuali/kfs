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
var divSuffix = ".div"

function recalculatePayrollAmount(effortPercentFieldName, payrollAmountFieldName){
	var totalPayrollAmount = getElementValue( totalAmountFiledName );
	var effortPercent = parseInt(getElementValue( effortPercentFieldName ));
	
	if(totalPayrollAmount != '' && effortPercent != ''){
		var dwrReply = {
			callback:function(data) {
				var number = new Number(data).toFixed(2);
				//DWRUtil.setValue( payrollAmountFieldName + divSuffix, number);
				DWRUtil.setValue( payrollAmountFieldName, number);
				//DWRUtil.setValue( effortPercentFieldName + divSuffix, effortPercent);
			},
			
			errorHandler:function( errorMessage ) { 
			}
		};
		
		PayrollAmountUtil.recalculatePayrollAmount(totalPayrollAmount, effortPercent, dwrReply);
	}
}

function recalculateEffortPercent(payrollAmountFieldName, effortPercentFieldName){
	var totalPayrollAmount = getElementValue( totalAmountFiledName );
	var payrollAmount = getElementValue( payrollAmountFieldName );
	
	if(totalPayrollAmount != '' && payrollAmount != ''){
		var dwrReply = {
			callback:function(data) {
				var number = new Number(data).toFixed(2);
				DWRUtil.setValue( payrollAmountFieldName + divSuffix, payrollAmount);
				DWRUtil.setValue( effortPercentFieldName + divSuffix, number);
				DWRUtil.setValue( effortPercentFieldName, Math.round(data));
			},
			
			errorHandler:function( errorMessage ) { 
			}
		};
		
		PayrollAmountUtil.recalculateEffortPercent(totalPayrollAmount, payrollAmount, dwrReply);
	}
}

function addDetailLine(){
	var dwrReply = {
		callback:function(data) {
			DWRUtil.setValue("newDetailLine.effortCertificationPayrollAmount", data.totalPayrollAmount);
		},
		
		errorHandler:function( errorMessage ) { 
		}
	};
	
	CertificationRecreateForm.getEffortCertificationDocument(dwrReply);
	return false;
}