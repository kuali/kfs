/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function setKemidBenefittingOrgLastChangeDate(kemidBenePctFieldName) {

	var elPrefix = findElPrefix(kemidBenePctFieldName.name);
	var kemidBeneLastChangeDateFieldName = elPrefix + ".lastChangeDate";
	
	setLastChangeDate( kemidBeneLastChangeDateFieldName);
}

function setLastChangeDate( kemidBeneLastChangeDateFieldName) {
	
	var dwrReply = {callback:function (data) {
		if (data != null) {
			setRecipientValue(kemidBeneLastChangeDateFieldName, data);
		} else {
			setRecipientValue(kemidBeneLastChangeDateFieldName, wrapError("kemid benefitting organization last change date error"), true);
		}
	}, errorHandler:function (errorMessage) {
		setRecipientValue(kemidBeneLastChangeDateFieldName, wrapError("kemid benefitting organization last change date error"), true);
	}};
	
	KEMService.getCurrentSystemProcessDate(dwrReply);
}