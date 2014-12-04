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
function setTypeCodeRelatedInfo(typeCodeFieldName) {
	var elPrefix = findElPrefix(typeCodeFieldName.name);
	var cashSweepModelIdFieldName = elPrefix + ".cashSweepModelId";
	var incomeACIModelIdFieldName = elPrefix + ".incomeACIModelId";
	var principalACIModelIdFieldName = elPrefix + ".principalACIModelId";
	var typeCode = dwr.util.getValue(typeCodeFieldName);
	
	if (typeCode == "") {
		clearRecipients(cashSweepModelIdFieldName);
		clearRecipients(incomeACIModelIdFieldName);
		clearRecipients(principalACIModelIdFieldName);
	} else {
		var dwrReply = {callback:function (data) {
		
			if (data != null && typeof data == "object") {
			
				setRecipientValue(cashSweepModelIdFieldName, data.cashSweepModelId);
				setRecipientValue(incomeACIModelIdFieldName, data.incomeACIModelId);
				setRecipientValue(principalACIModelIdFieldName, data.principalACIModelId);
			} else {
			
				setRecipientValue(cashSweepModelIdFieldName, wrapError("Type Code not found1"), true);
				setRecipientValue(incomeACIModelIdFieldName, wrapError("Type Code not found1"), true);
				setRecipientValue(principalACIModelIdFieldName, wrapError("Type Code not found1"), true);
			}
		}, errorHandler:function (errorMessage) {
		
			window.status = errorMessage;
			alert(errorMessage);
			setRecipientValue(cashSweepModelIdFieldName, wrapError("Type Code not found"), true);
			setRecipientValue(incomeACIModelIdFieldName, wrapError("Type Code not found"), true);
			setRecipientValue(principalACIModelIdFieldName, wrapError("Type Code not found"), true);
		}};
		
		TypeCodeService.getByPrimaryKey(typeCode, dwrReply);
	}
}

