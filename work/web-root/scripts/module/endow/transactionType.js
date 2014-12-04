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

 function loadFeeTransactionTypeDesc(FeeTransactionTypeFieldName){
	var elPrefix = findElPrefix(FeeTransactionTypeFieldName.name);
	var feeTransactionTypeDescriptionFieldName = elPrefix + ".transactionType.description";
 	setFeeTransactionTypeDescription(FeeTransactionTypeFieldName, feeTransactionTypeDescriptionFieldName);
 }
  
 function setFeeTransactionTypeDescription(FeeTransactionTypeFieldName, feeTransactionTypeDescriptionFieldName){
 
	var feeTransactionType = dwr.util.getValue(FeeTransactionTypeFieldName);
    
	if (feeTransactionType =='') {
		clearRecipients(feeTransactionTypeDescriptionFieldName);
	} else {
		feeTransactionType = feeTransactionType.toUpperCase();
		
		var dwrReply = {
			callback:function(data) {
			if (data != null && typeof data == 'object') {
				setRecipientValue(feeTransactionTypeDescriptionFieldName, data.description);
			} else {
				setRecipientValue(feeTransactionTypeDescriptionFieldName, wrapError("Transaction Type description not found"), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(feeTransactionTypeDescriptionFieldName, wrapError("Transaction Type description not found"), true);
			}
		};
		TransactionTypeService.getByPrimaryKey(feeTransactionType, dwrReply);
	}
}
