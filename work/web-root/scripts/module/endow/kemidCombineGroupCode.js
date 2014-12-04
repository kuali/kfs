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

 function loadCombineGroupCodeDesc(combineGroupCodeFieldName){
	var elPrefix = findElPrefix( combineGroupCodeFieldName.name );
	var combineGroupCodeDescriptionFieldName = elPrefix + ".combineGroup.name";
 	setCombineGroupCodeDescription(combineGroupCodeFieldName, combineGroupCodeDescriptionFieldName);
 }
  
 function setCombineGroupCodeDescription(combineGroupCodeFieldName, combineGroupCodeDescriptionFieldName){
 
	var combineGroupCode = dwr.util.getValue(combineGroupCodeFieldName);
    
	if (combineGroupCode =='') {
		clearRecipients(combineGroupCodeDescriptionFieldName);
	} else {
		combineGroupCode = combineGroupCode.toUpperCase();
		
		var dwrReply = {
			callback:function(data) {
			if (data != null && typeof data == 'object') {
				setRecipientValue( combineGroupCodeDescriptionFieldName, data.name);
			} else {
				setRecipientValue(combineGroupCodeDescriptionFieldName, wrapError("combine group code not found" ), true);			
			} },
			errorHandler:function(errorMessage ) { 
				setRecipientValue(combineGroupCodeDescriptionFieldName, wrapError("combine group code not found" ), true);
			}
		};
		CombineGroupCodeService.getByPrimaryKey(combineGroupCode, dwrReply);
	}
}
