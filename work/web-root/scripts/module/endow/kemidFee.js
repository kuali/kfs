/*
 * Copyright 2007 The Kuali Foundation.
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

function setRelatedFields(feeMethodCodeFieldName) {
	var elPrefix = findElPrefix(feeMethodCodeFieldName.name);
	var feeStartDateFieldName = elPrefix + ".feeStartDate";
	var feeMethodDescriptionFieldName = elPrefix + ".feeMethod.name";

	setFeeStartDate(feeMethodCodeFieldName, feeStartDateFieldName);

	setFeeMethodDescription(feeMethodCodeFieldName,
			feeMethodDescriptionFieldName);
}

function setFeeStartDate(feeMethodCodeFieldName, feeStartDateFieldName) {

	var feeMethodCode = dwr.util.getValue(feeMethodCodeFieldName);

	if (feeMethodCode == '') {
		clearRecipients(feeStartDateFieldName);
	} else {
		feeMethodCode = feeMethodCode.toUpperCase();

		var dwrReply = {
			callback : function(data) {
				if (data != null) {
					setRecipientValue(feeStartDateFieldName, data);
				} else {
					setRecipientValue(feeStartDateFieldName,
							wrapError("failed to display fee start date"), true);
				}
			},
			errorHandler : function(errorMessage) {
				setRecipientValue(feeStartDateFieldName,
						wrapError("failed to display fee start date"), true);
			}
		};
		FeeMethodService.getFeeMethodNextProcessDateForAjax(feeMethodCode,
				dwrReply);
	}
}

function setFeeMethodDescription(feeMethodFieldName,
		feeMethodDescriptionFieldName) {

	var feeMethod = dwr.util.getValue(feeMethodFieldName);

	if (feeMethod == '') {
		clearRecipients(feeMethodDescriptionFieldName);
	} else {
		feeMethod = feeMethod.toUpperCase();

		var dwrReply = {
			callback : function(data) {
				if (data != null && typeof data == 'object') {
					setRecipientValue(feeMethodDescriptionFieldName, data.name);
				} else {
					setRecipientValue(feeMethodDescriptionFieldName,
							wrapError("fee method not found"), true);
				}
			},
			errorHandler : function(errorMessage) {
				setRecipientValue(feeMethodDescriptionFieldName,
						wrapError("fee method not found"), true);
			}
		};
		FeeMethodService.getByPrimaryKey(feeMethod, dwrReply);
	}
}

function loadKEMIDShortTitle(kemidFieldName) {
	var elPrefix = findElPrefix(kemidFieldName.name);
	var kemidShortTitleFieldName = elPrefix
			+ ".chargeFeeToKemidObjRef.shortTitle";

	setKEMIDShortTitle(kemidFieldName, kemidShortTitleFieldName);
}

function setKEMIDShortTitle(kemidFieldName, kemidShortTitleFieldName) {

	var kemid = dwr.util.getValue(kemidFieldName);

	if (kemid == '') {
		clearRecipients(kemidShortTitleFieldName, "");
	} else {
		kemid = kemid.toUpperCase();
		var dwrReply = {
			callback : function(data) {
				if (data != null && typeof data == 'object') {
					setRecipientValue(kemidShortTitleFieldName, data.shortTitle);
				} else {
					setRecipientValue(kemidShortTitleFieldName,
							wrapError("kemid not found"), true);
				}
			},
			errorHandler : function(errorMessage) {
				setRecipientValue(kemidShortTitleFieldName,
						wrapError("kemid not found"), true);
			}
		};
		KEMIDService.getByPrimaryKey(kemid, dwrReply);
	}
}
