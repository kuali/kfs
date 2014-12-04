/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */




function loadAgencyName( creditCardOrAgencyCodeField , creditCardOrAgencyPropertyName ) {
	var creditCardOrAgencyCodeFieldName = creditCardOrAgencyCodeField.name;
    var agencyCode = dwr.util.getValue( creditCardOrAgencyCodeFieldName );
    var agencyFieldName = findElPrefix(creditCardOrAgencyCodeFieldName) + "." + creditCardOrAgencyPropertyName;
    if (agencyCode == "") {
		clearRecipients(agencyFieldName);    		
	}
	else {
		var dwrReply = {
				callback: function (data) {
				if ( data != null && typeof data == "object" ) {   
					setRecipientValue( agencyFieldName, data.creditCardOrAgencyName );
				}
				else {
					clearRecipients(agencyFieldName); 
				}
			},
			errorHandler:function( errorMessage ) { 
				clearRecipients(agencyFieldName); 
				window.status = errorMessage;
			}
		};
		CreditCardAgencyService.getCreditCardAgencyByCode( agencyCode, dwrReply );    
	}
  
}

// function to call TravelExpenseService#getExpense and manipulate expense based on that information
function loadExpenseTypeObjectCode(expenseTypeCodeField, documentTypeName, travelerTypeCode, tripTypeCode) {
	var expenseTypeCodeFieldName = expenseTypeCodeField.name;
	var expenseTypeCodeValue = dwr.util.getValue( expenseTypeCodeFieldName );
	var lineName = expenseTypeCodeFieldName.replace(/\.expenseTypeCode$/, "");
	
	if (expenseTypeCodeValue != "") {
		var dwrReply = {
			callback: function(data) {
				if (data != null && typeof data == "object") { // we really did succeed!
					// handle taxable
					var taxableCheckbox = document.getElementById(lineName+".taxable");
					if (taxableCheckbox) {
						taxableCheckbox.checked = data.taxable;
					}
					
					var expenseAmountField = document.getElementById(lineName+".expenseAmount");
					if (expenseAmountField) {
						if (data.expenseTypeMetaCategoryCode == "M") { // handle mileage
							expenseAmountField.value = 0.0;
							expenseAmountField.disabled = true;
						} else {
							if (expenseAmountField.disabled) { // what if they were at mileage and they switched?  let's enable the amount field then
								expenseAmountField.disabled = false;
							}
						}
					} 
				}
			},
			errorHandler: function (errorMessage) {
				window.status = errorMessage;
			}
		};
		TravelExpenseService.getExpenseType(expenseTypeCodeValue, documentTypeName, tripTypeCode, travelerTypeCode, dwrReply);
	}
}
