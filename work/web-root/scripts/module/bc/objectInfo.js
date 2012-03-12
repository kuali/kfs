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
 

var chartCodeSuffix = ".chartOfAccountsCode";
var chartNameSuffix = ".chartOfAccounts.finChartOfAccountDescription";
var accountNumberSuffix = ".accountNumber";
var accountNameSuffix = ".account.accountName";
var subAccountNumberSuffix = ".subAccountNumber";
var subAccountNameSuffix = ".subAccount.subAccountName";

/**
 * import the given javascript into the hosting document
 * 
 * @param scriptSourceFileName the given script source file name 
 * @param scriptSourceHolder the script source file name holder that is a key-value pair object. 
 * One script source file name can only have one entry in the object, so it is used to avoid double load scripts.
 */  
function importJavascriptFile(scriptSourceFileName, scriptSourceHolder) {
	if (scriptSourceFileName == "" || scriptSourceHolder[scriptSourceFileName] != null) {
		return;
	}
	
	var scriptElement = document.createElement("script");
	scriptElement.type = "text/javascript";
	scriptElement.src = scriptSourceFileName;
	document.getElementsByTagName("head")[0].appendChild(scriptElement);
	
	scriptSourceHolder[scriptSourceFileName] = scriptSourceFileName;
} 

/**
 * import the script that defines the DWR interface into the hosting document
 * 
 * @param interfaceName the given interface name
 * @param interfaceScriptHolder the script name holder that is a key-value pair object
 */	
function importDWRInterface(interfaceName, interfaceScriptHolder) {
	if (interfaceName == "" || interfaceScriptHolder[interfaceName] != null) {
		return;
	}
	
	var scriptSourceFileName = "dwr/interface/" + interfaceName + ".js";
	importJavascriptFile(scriptSourceFileName, interfaceScriptHolder)
}

/**
 * the constructor of BudgetObjectInfoUpdator object
 */
function BudgetObjectInfoUpdator(){ 
	requestedCsfAmountSuffix = ".appointmentRequestedCsfAmount";
	requestedCsfTimePercentSuffix = ".appointmentRequestedCsfTimePercent";
	requestedCsfFteQuantitySuffix = ".appointmentRequestedCsfFteQuantity";
	emptyString = '';
	
	var interfaceScriptHolder = new Object();
	importDWRInterface("BudgetConstructionAppointmentFundingReasonCodeService", interfaceScriptHolder);
	importDWRInterface("BudgetConstructionIntendedIncumbentService", interfaceScriptHolder);
	importDWRInterface("BudgetConstructionDurationService", interfaceScriptHolder);
	importDWRInterface("BudgetConstructionAdministrativePostService", interfaceScriptHolder);
	importDWRInterface("BudgetConstructionPositionService", interfaceScriptHolder);
	importDWRInterface("SalarySettingService", interfaceScriptHolder);
}

/**
 * retrieve duration and open/close the amount and time percent fields 
 */
BudgetObjectInfoUpdator.prototype.loadDurationInfo = function(durationCodeFieldName, durationDescriptionFieldName ) {
    var durationCode = dwr.util.getValue( durationCodeFieldName ).trim();
    var fieldNamePrefix = findElPrefix(durationCodeFieldName).trim();
    var requestedCsfAmountField = document.getElementById(fieldNamePrefix + requestedCsfAmountSuffix);
    var requestedCsfTimePercentField = document.getElementById(fieldNamePrefix + requestedCsfTimePercentSuffix);
    var requestedCsfFteQuantityField = document.getElementById(fieldNamePrefix + requestedCsfFteQuantitySuffix);

    var durationCodeField = document.getElementById(durationCodeFieldName);

	if (durationCodeField.defaultValue != durationCode){
		durationCodeField.defaultValue = durationCode;
		
		if (durationCode==emptyString) {
			setRecipientValue(durationDescriptionFieldName, emptyString);
		}
		else {
			var isDefualtCode = (durationCode == "NONE");								
			if(isDefualtCode){
				requestedCsfAmountField.setAttribute('value', '0');
				requestedCsfAmountField.setAttribute('disabled', true);
			
				requestedCsfTimePercentField.setAttribute('value', '0.00');
				requestedCsfTimePercentField.setAttribute('disabled', true);
	
				setRecipientValue( fieldNamePrefix + requestedCsfFteQuantitySuffix , '0.00000' );			
			}
			else{
				requestedCsfAmountField.removeAttribute('disabled');
				requestedCsfTimePercentField.removeAttribute('disabled');
				requestedCsfAmountField.focus();
			} 

			var dwrReply = {
				callback:function(data) {
				if ( data != null && typeof data == 'object' ) {								
					setRecipientValue( durationDescriptionFieldName, data.appointmentDurationDescription);
				} else {
					setRecipientValue( durationDescriptionFieldName, wrapError( "duration not found" ), true );	
					requestedCsfAmountField.setAttribute('disabled', true);
					requestedCsfTimePercentField.setAttribute('disabled', true);		
				} },
				errorHandler:function( errorMessage ) { 
					setRecipientValue( durationDescriptionFieldName, wrapError( "duration not found" ), true );
				}
			};

			BudgetConstructionDurationService.getByPrimaryId( durationCode, dwrReply );
		}
	}
}

/**
 * retrieve reason code and open/close the reason amount field
 */
BudgetObjectInfoUpdator.prototype.loadReasonCodeInfo = function(reasonAmountFieldName, reasonCodeFieldName, reasonDescriptionFieldName ) {
    var reasonCode = dwr.util.getValue( reasonCodeFieldName ).trim();
    var reasonAmountField = document.getElementById(reasonAmountFieldName);

    var reasonCodeField = document.getElementById(reasonCodeFieldName);

	if (reasonCodeField.defaultValue != reasonCode){
		reasonCodeField.defaultValue = reasonCode;

		if (reasonCode=='') {
			setRecipientValue(reasonDescriptionFieldName, emptyString);
			reasonAmountField.setAttribute('disabled', true);
			reasonAmountField.setAttribute('value', emptyString);
		} else {
			reasonAmountField.removeAttribute('disabled');
			reasonAmountField.focus();
	
			var dwrReply = {
				callback:function(data) {
				if ( data != null && typeof data == 'object' ) {
					setRecipientValue( reasonDescriptionFieldName, data.appointmentFundingReasonDescription);
				} else {
					setRecipientValue( reasonDescriptionFieldName, wrapError( "reason not found" ), true );			
					reasonAmountField.setAttribute('disabled', true);
				} },
				errorHandler:function( errorMessage ) { 
					setRecipientValue( reasonDescriptionFieldName, wrapError( "reason not found" ), true );
				}
			};
	
			BudgetConstructionAppointmentFundingReasonCodeService.getByPrimaryId( reasonCode, dwrReply );
		}
	} 
}

/**
 * retrieve the intended incumbent and administrative post according to the given information
 */
BudgetObjectInfoUpdator.prototype.loadIntendedIncumbentInfo = function(positionNumberFieldName, iuClassificationLevelFieldName, administrativePostFieldName, emplidFieldName, personNameFieldName) {	

	var emplid = getElementValue( emplidFieldName );

	if (emplid == emptyString) {
		setRecipientValue(personNameFieldName, emptyString);
		setRecipientValue(iuClassificationLevelFieldName, emptyString);
	}
	else if(emplid == 'VACANT'){
		setRecipientValue(personNameFieldName, emplid);
		setRecipientValue(iuClassificationLevelFieldName, emptyString);
	}
	else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( iuClassificationLevelFieldName, data.iuClassificationLevel);
				setRecipientValue( personNameFieldName, data.name);
			} else {
				setRecipientValue(iuClassificationLevelFieldName, emptyString);
				setRecipientValue( personNameFieldName, wrapError( "Intended incumbent not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( personNameFieldName, wrapError( "Error" ), true );
			}
		};
		
		BudgetConstructionIntendedIncumbentService.getByPrimaryId( emplid, dwrReply );
	}
	
	budgetObjectInfoUpdator.loadAdministrativePostInfo(emplidFieldName, positionNumberFieldName, administrativePostFieldName);
}

/**
 * retrieve the position and adminstrative post according to the given fiscal year, employee id and position number
 */
BudgetObjectInfoUpdator.prototype.loadPositionInfo = function(universityFiscalYearFieldName, emplidFieldName, 
	iuNormalWorkMonthsFieldName, iuPayMonthsFieldName, positionFullTimeEquivalencyFieldName, administrativePostFieldName, 
	positionNumberFieldName, positionDescriptionFieldName) {

	var emplid = getElementValue( emplidFieldName );
	var universityFiscalYear = getElementValue( universityFiscalYearFieldName );
	var positionNumber = getElementValue( positionNumberFieldName );

	if (positionNumber == emptyString || universityFiscalYear == emptyString) {
		setRecipientValue(positionDescriptionFieldName, emptyString);
		setRecipientValue(iuNormalWorkMonthsFieldName, emptyString);
		setRecipientValue(iuPayMonthsFieldName, emptyString);
		setRecipientValue(positionFullTimeEquivalencyFieldName, emptyString);
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( positionDescriptionFieldName, data.positionDescription);
				setRecipientValue( iuNormalWorkMonthsFieldName, data.iuNormalWorkMonths);
				setRecipientValue( iuPayMonthsFieldName, data.iuPayMonths);
				setRecipientValue( positionFullTimeEquivalencyFieldName, data.positionFullTimeEquivalency);
			} else {
				setRecipientValue( positionDescriptionFieldName, wrapError( "position not found" ), true );
				setRecipientValue(iuNormalWorkMonthsFieldName, emptyString);
				setRecipientValue(iuPayMonthsFieldName, emptyString);
				setRecipientValue(positionFullTimeEquivalencyFieldName, emptyString);			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( positionDescriptionFieldName, wrapError( "position not found" ), true );
			}
		};
		
		BudgetConstructionPositionService.getByPrimaryId(universityFiscalYear, positionNumber, dwrReply );
	}
	
	budgetObjectInfoUpdator.loadAdministrativePostInfo(emplidFieldName, positionNumberFieldName, administrativePostFieldName);
}

/**
 * retrieve the administrative post according to the given emplid and position number
 */
BudgetObjectInfoUpdator.prototype.loadAdministrativePostInfo = function(emplidFieldName, positionNumberFieldName, administrativePostFieldName) {
	var emplid = getElementValue( emplidFieldName );
	var positionNumber = getElementValue( positionNumberFieldName );
	
	if (positionNumber == emptyString || emplid == emptyString || emplid == 'VACANT') {
		setRecipientValue(administrativePostFieldName, emptyString);
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( administrativePostFieldName, data.administrativePost);
			} else {
				clearRecipients(administrativePostFieldName, emptyString);			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( administrativePostFieldName, wrapError( "administrative post not found" ), true );
			}
		};
		
		BudgetConstructionAdministrativePostService.getByPrimaryId(emplid, positionNumber, dwrReply );
	}
}

/**
 * calculate fte quantity based on the given information
 */
BudgetObjectInfoUpdator.prototype.recalculateFTE = function(payMonthsFieldName, fundingMonthsFieldName, fteQuantityFieldName, timePercentFieldName, fteQuantityField ) {
    var timePercent = dwr.util.getValue(timePercentFieldName).trim();
    var payMonths = dwr.util.getValue(payMonthsFieldName).trim();
    var fundingMonths = dwr.util.getValue(fundingMonthsFieldName).trim();

	if (timePercent==emptyString || payMonths==emptyString || fundingMonths==emptyString) {
		setRecipientValue(fteQuantityFieldName, emptyString);
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null) {
				var formattedFTE = new Number(data).toFixed(5);
				setRecipientValue( fteQuantityFieldName, formattedFTE );
			} else {
				setRecipientValue( fteQuantityFieldName, emptyString );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( fteQuantityFieldName, emptyString );
			}
		};
		
		SalarySettingService.calculateFteQuantity(payMonths, fundingMonths, timePercent, dwrReply );
	}
}

/**
* Loads account info and chart info when ACCOUNT_CAN_CROSS_CHART is false.
* @param accountCodeFieldName
* @param accountNameFieldName
* @return
*/
BudgetObjectInfoUpdator.prototype.loadChartAccountInfo = function( accountCodeFieldName, accountNameFieldName ) {
    var elPrefix = findElPrefix( accountCodeFieldName );
    var accountCode = dwr.util.getValue( accountCodeFieldName );
    var coaCodeFieldName =  elPrefix + chartCodeSuffix ;
    var coaNameFieldName =  elPrefix + chartNameSuffix ;

    if (valueChanged( accountCodeFieldName )) {
        setRecipientValue( elPrefix + subAccountNumberSuffix, "" );
        setRecipientValue( elPrefix + subAccountNameSuffix, "" );
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
		
		 AccountService.getUniqueAccountForAccountNumber( accountCode, dwrReply ); 
	}	
}

var budgetObjectInfoUpdator = new BudgetObjectInfoUpdator();

