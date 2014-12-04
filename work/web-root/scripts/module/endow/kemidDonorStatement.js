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

function loadDonorName(donorIDFieldName) {
	var elPrefix = findElPrefix(donorIDFieldName.name);
	var donorNameFieldName = elPrefix + ".donor.fullName";

	setDonorName(donorIDFieldName, donorNameFieldName);
}

function setDonorName(donorIDFieldName, donorNameFieldName) {

	var donorId = dwr.util.getValue(donorIDFieldName);

	if (donorId == '') {
		clearRecipients(donorNameFieldName);
	} else {

		var dwrReply = {
			callback : function(data) {
				if (data != null && typeof data == 'object') {
					setRecipientValue(donorNameFieldName, data.fullName);
				} else {
					setRecipientValue(donorNameFieldName,
							wrapError("donor not found"), true);
				}
			},
			errorHandler : function(errorMessage) {
				setRecipientValue(donorNameFieldName,
						wrapError("donor not found"), true);
			}
		};
		DonorService.getByPrimaryKey(donorId, dwrReply);
	}
}

function loadDonorStatementCodeDesc(donorStatementCodeFieldName) {
	var elPrefix = findElPrefix(donorStatementCodeFieldName.name);
	var donorStatementDescriptionFieldName = elPrefix + ".donorStatement.name";

	setDonorStatementDescription(donorStatementCodeFieldName,
			donorStatementDescriptionFieldName);
}

function setDonorStatementDescription(donorStatementCodeFieldName,
		donorStatementDescriptionFieldName) {

	var donorStatementCode = dwr.util.getValue(donorStatementCodeFieldName);

	if (donorStatementCode == '') {
		clearRecipients(donorStatementDescriptionFieldName);
	} else {
		donorStatementCode = donorStatementCode.toUpperCase();

		var dwrReply = {
			callback : function(data) {
				if (data != null && typeof data == 'object') {
					setRecipientValue(donorStatementDescriptionFieldName,
							data.name);
				} else {
					setRecipientValue(donorStatementDescriptionFieldName,
							wrapError("donor statement code not found"), true);
				}
			},
			errorHandler : function(errorMessage) {
				setRecipientValue(donorStatementDescriptionFieldName,
						wrapError("donor statement code not found"), true);
			}
		};
		DonorStatementCodeService.getByPrimaryKey(donorStatementCode, dwrReply);
	}
}

function loadCombineWithDonorName(combineWithDonorIDFieldName) {
	var elPrefix = findElPrefix(combineWithDonorIDFieldName.name);
	var combineWithDonorNameFieldName = elPrefix + ".combineWithDonor.fullName";

	setCombineWithDonorName(combineWithDonorIDFieldName, combineWithDonorNameFieldName);
}

function setCombineWithDonorName(combineWithDonorIDFieldName,
		combineWithDonorNameFieldName) {

	var combineWithDonorId = dwr.util.getValue(combineWithDonorIDFieldName);

	if (combineWithDonorId == '') {
		clearRecipients(combineWithDonorNameFieldName);
	} else {

		var dwrReply = {
			callback : function(data) {
				if (data != null && typeof data == 'object') {
					setRecipientValue(combineWithDonorNameFieldName,
							data.fullName);
				} else {
					setRecipientValue(combineWithDonorNameFieldName,
							wrapError("donor not found"), true);
				}
			},
			errorHandler : function(errorMessage) {
				setRecipientValue(combineWithDonorNameFieldName,
						wrapError("donor not found"), true);
			}
		};
		DonorService.getByPrimaryKey(combineWithDonorId, dwrReply);
	}
}

function loadDonorLabelDesc(donorLabelFieldName) {
	var elPrefix = findElPrefix(donorLabelFieldName.name);
	var donorLabelDescriptionFieldName = elPrefix + ".donorLabelObjRef.name";

	setDonorLabelDescription(donorLabelFieldName,
			donorLabelDescriptionFieldName);
}

function setDonorLabelDescription(donorLabelFieldName,
		donorLabelDescriptionFieldName) {

	var donorLabel = dwr.util.getValue(donorLabelFieldName);

	if (donorLabel == '') {
		clearRecipients(donorLabelDescriptionFieldName);
	} else {
		donorLabel = donorLabel.toUpperCase();

		var dwrReply = {
			callback : function(data) {
				if (data != null && typeof data == 'object') {
					setRecipientValue(donorLabelDescriptionFieldName, data.name);
				} else {
					setRecipientValue(donorLabelDescriptionFieldName,
							wrapError("donor label not found"), true);
				}
			},
			errorHandler : function(errorMessage) {
				setRecipientValue(donorLabelDescriptionFieldName,
						wrapError("donor label found"), true);
			}
		};
		DonorLabelService.getByPrimaryKey(donorLabel, dwrReply);
	}
}
