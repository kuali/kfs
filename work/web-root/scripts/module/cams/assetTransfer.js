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

function onblur_accountNumber( accountNumberField, coaCodePropertyName ) {
	var accountNumberFieldName = accountNumberField.name;
	var coaCodeFieldName = findElPrefix(accountNumberFieldName) + "." + coaCodePropertyName;
    var accountNumber = dwr.util.getValue( accountNumberFieldName );	    
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

function loadChartCode( accountNumber, coaCodeFieldName ) {
	if (accountNumber == "") {
		clearRecipients(coaCodeFieldName);    		
	}
	else {
		var dwrReply = {
				callback: function (data) {
				//alert("chartOfAccountsCode = " + data.chartOfAccountsCode + ", accountNumber = " + accountNumber);
				if ( data != null && typeof data == "object" ) {   
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

