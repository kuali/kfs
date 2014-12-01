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

function onblur_generateCalculatedTotal(anyAmountField, callbackFunction) {
    var elPrefix = findElPrefix( anyAmountField.name );
    var salePrice = dwr.util.getValue( elPrefix + ".salePrice" );
    var handlingFee = dwr.util.getValue( elPrefix + ".handlingFeeAmount" );
    var preventiveMaintenanceAmount = dwr.util.getValue( elPrefix + ".preventiveMaintenanceAmount" );

    var editedSalePrice = editString(salePrice);
    var editedHandlingFee = editString(handlingFee);
    var editedPreventiveMaintenanceAmount = editString(preventiveMaintenanceAmount);
    
	if (editedSalePrice != "") {
		var dwrReply = {
			callback :callbackFunction,
			errorHandler : function(errorMessage) {
				setRecipientValue(
						"document.newMaintainableObject.calculatedTotal",
						wrapError("Unable to calculate total based on entered amounts above."), true);
			}
		};
		AssetRetirementService.generateCalculatedTotal(editedSalePrice, editedHandlingFee, editedPreventiveMaintenanceAmount, dwrReply);
	}
}

function generateCalculatedTotal_Callback(data) {
	var newData = formatCurrency(data);
	if(newData=="$0.00"){
		setRecipientValue("document.newMaintainableObject.calculatedTotal",data);
	} else {
		setRecipientValue("document.newMaintainableObject.calculatedTotal",newData);
	}
}

function editString(numString)
{
  var editStringReturn = "";
  editStringReturn = numString.trimString();
  editStringReturn = editStringReturn.replaceAllString('$', '');
  editStringReturn = editStringReturn.replaceAllString(',', '');
  return editStringReturn;
}

String.prototype.trimString = function ()
{
 var regExp = /^\s+|\s+$/;
 return this.replace(regExp, '');
}

String.prototype.replaceAllString = function (replaceValue, newValue)
{
 var functionReturn = this;
 while ( true )
 {
  var currentValue = functionReturn;
  functionReturn = functionReturn.replace(replaceValue, newValue);
  if ( functionReturn == currentValue )
   break;
 }
 return functionReturn;
}

function formatCurrency(num) {
	num = num.replace(/\$|\,/g,'');
	if(isNaN(num)) {
		num = "0";
	}
	sign = (num == (num = Math.abs(num)));
	num = Math.floor(num*100+0.50000000001);
	cents = num%100;
	num = Math.floor(num/100).toString();
	if(cents<10) {
		cents = "0" + cents;
	}
	for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++) {
		num = num.substring(0,num.length-(4*i+3))+','+
		num.substring(num.length-(4*i+3));
	}
	return (((sign)?'':'-') + '$' + num + '.' + cents);
}
