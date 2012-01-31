/*
 * Copyright 2006-2007 The Kuali Foundation
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

/**
 * Java script to reload the page and call getSections() - this will be used to manipulate the Award Schedule tabs
 */
function onblur_preferredBillingFrequency(awardScheduleField) {

	var fieldValue = getElementValue(awardScheduleField.name);
	//to clear the values of Billing Schedule when the user toggles AwardSchedule

	var elPrefix = findElPrefix(awardScheduleField.name);

	var billNumber = elPrefix + ".add.bills.billNumber";
	var billDate = elPrefix + ".add.bills.billDate";
	var estimatedAmount = elPrefix + ".add.bills.estimatedAmount";
	var bisItBilled = elPrefix + ".add.bills.isItBilled";
	clearRecipients(billNumber);
	clearRecipients(billDate);
	clearRecipients(estimatedAmount);
	clearRecipients(bisItBilled);

	//submitting the form to bring dynamic sections
	document.forms[0].submit();
}

/**
 * Java script to fetch the Letter of Credit Fund Group for the appropriate Letter of Credit Fund
 */
function onblur_letterOfCreditFundCode(letterOfCreditFundCodeField) {
	singleKeyLookup(LetterOfCreditFundService.getByPrimaryId,
			letterOfCreditFundCodeField,
			"letterOfCreditFund.letterOfCreditFundGroup",
			"letterOfCreditFundGroupDescription");

}

/**
 * Defaults the Draw Number to Award Document Number, if not already 
 */
function onblur_awardSetDrawNumber(awardDocNbrField) {
	var awardDocNumber = getElementValue(awardDocNbrField.name);
	var drawNumber = getElementValue("document.newMaintainableObject.drawNumber");

	// If the draw number was not empty or same as before,
	// notify the user that the draw number will be changed
	if (drawNumber != "" && awardDocNumber != drawNumber) {
		// The previous value of the draw number will be replaced
		alert("Please note that the draw number will be updated with the new Award Document Number.");
	}

	// Set the draw number to the award ID
	setRecipientValue("document.newMaintainableObject.drawNumber",
			awardDocNumber);
}
