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

 function loadFeeTypeCodeDesc(feeTypeCodeFieldName){
	var elPrefix = findElPrefix( feeTypeCodeFieldName.name );
	var feeTypeCodeDescriptionFieldName = elPrefix + ".feeType.description";
 	setFeeTypeCodeDescription(feeTypeCodeFieldName, feeTypeCodeDescriptionFieldName);
 }
  
 function setFeeTypeCodeDescription(feeTypeCodeFieldName, feeTypeCodeDescriptionFieldName){
 
	var feeTypeCode = dwr.util.getValue(feeTypeCodeFieldName);
    
	if (feeTypeCode =='') {
		clearRecipients(feeTypeCodeDescriptionFieldName);
	} else {
		feeTypeCode = feeTypeCode.toUpperCase();
		
		var dwrReply = {
			callback:function(data) {
			if (data != null && typeof data == 'object') {
				setRecipientValue( feeTypeCodeDescriptionFieldName, data.name);
			} else {
				setRecipientValue(feeTypeCodeDescriptionFieldName, wrapError("fee type description not found" ), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(feeTypeCodeDescriptionFieldName, wrapError("fee type description not found" ), true);
			}
		};
		FeeTypeService.getByPrimaryKey(feeTypeCode, dwrReply);
	}
}
