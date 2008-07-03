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

function BudgetObjectInfoUpdator(){ 
	fteQuantitySuffix = ".appointmentRequestedFteQuantity";
	requestedCsfAmountSuffix = ".appointmentRequestedCsfAmount";
	requestedCsfTimePercentSuffix = ".appointmentRequestedCsfTimePercent";
}

BudgetObjectInfoUpdator.prototype.loadDurationInfo = function(durationCodeFieldName, durationDescriptionFieldName ) {
    var durationCode = DWRUtil.getValue( durationCodeFieldName );
    var fieldNamePrefix = findElPrefix(durationCodeFieldName);
    var requestedCsfAmountField = document.getElementById(fieldNamePrefix + requestedCsfAmountSuffix);
    var requestedCsfTimePercentField = document.getElementById(fieldNamePrefix + requestedCsfTimePercentSuffix);

	if (durationCode=='') {
		clearRecipients(durationDescriptionFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				var isDefualtCode = (durationCode == "NONE");								
				setRecipientValue( durationDescriptionFieldName, data.appointmentDurationDescription);

				if(isDefualtCode){
					requestedCsfAmountField.setAttribute('disabled', 'disabled');
					requestedCsfTimePercentField.setAttribute('disabled', 'disabled');
				}
				else{
					requestedCsfAmountField.removeAttribute('disabled');
					requestedCsfTimePercentField.removeAttribute('disabled');
				}
			} else {
				setRecipientValue( durationDescriptionFieldName, wrapError( "duration not found" ), true );	
				requestedCsfAmountField.setAttribute('disabled', 'disabled');
				requestedCsfTimePercentField.setAttribute('disabled', 'disabled');		
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( durationDescriptionFieldName, wrapError( "duration not found" ), true );
			}
		};
		
		BudgetConstructionDurationService.getByPrimaryId( durationCode, dwrReply );
	}
}

BudgetObjectInfoUpdator.prototype.loadReasonCodeInfo = function(reasonCodeFieldName, reasonDescriptionFieldName ) {
    var reasonCode = DWRUtil.getValue( reasonCodeFieldName );

	if (reasonCode=='') {
		clearRecipients(reasonDescriptionFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( reasonDescriptionFieldName, data.appointmentFundingReasonDescription);
			} else {
				setRecipientValue( reasonDescriptionFieldName, wrapError( "reason not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( reasonDescriptionFieldName, wrapError( "reason not found" ), true );
			}
		};
		
		BudgetConstructionAppointmentFundingReasonCodeService.getByPrimaryId( reasonCode, dwrReply );
	}
}

BudgetObjectInfoUpdator.prototype.loadIntendedIncumbentInfo = function(reasonCodeFieldName, reasonDescriptionFieldName ) {
    var reasonCode = DWRUtil.getValue( reasonCodeFieldName );

	if (reasonCode=='') {
		clearRecipients(reasonDescriptionFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( reasonDescriptionFieldName, data.appointmentFundingReasonDescription );
			} else {
				setRecipientValue( reasonDescriptionFieldName, wrapError( "reason not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( reasonDescriptionFieldName, wrapError( "reason not found" ), true );
			}
		};
		
		BudgetConstructionAppointmentFundingReasonCodeService.getByPrimaryId( reasonCode, dwrReply );
	}
}

BudgetObjectInfoUpdator.prototype.recalculateFTE = function(payMonthsFieldName, fundingMonthsFieldName, timePercentFieldName, fteQuantityFieldName ) {
    var timePercent = DWRUtil.getValue(timePercentFieldName);
    var payMonths = DWRUtil.getValue(payMonthsFieldName);
    var fundingMonths = DWRUtil.getValue(fundingMonthsFieldName);
    
    var fieldNamePrefix = findElPrefix(timePercentFieldName);
    var fteQuantityFieldName = fieldNamePrefix + fteQuantitySuffix;

	if (timePercent=='' || payMonths=='' || fundingMonths=='') {
		clearRecipients(fteQuantityFieldName, '');
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null) {
				var formattedFTE = new Number(data).toFixed(4);
				setRecipientValue( fteQuantityFieldName, formattedFTE );
			} else {
				setRecipientValue( fteQuantityFieldName, null );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( fteQuantityFieldName, null );
			}
		};
		
		SalarySettingService.calculateFteQuantity(payMonths, fundingMonths, timePercent, dwrReply );
	}
}

var budgetObjectInfoUpdator = new BudgetObjectInfoUpdator();