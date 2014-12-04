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
function loadKemidAgreementStatusDate(kemidAgreementStatusCodeFieldName) {
	var elPrefix = findElPrefix(kemidAgreementStatusCodeFieldName.name);
	var kemidAgreementStatusDateFieldName = elPrefix + ".agreementStatusDate";
	
	setKemidAgreementStatusDate(kemidAgreementStatusCodeFieldName, kemidAgreementStatusDateFieldName);
}
function setKemidAgreementStatusDate(kemidAgreementStatusCodeFieldName, kemidAgreementStatusDateFieldName) {
	var kemidAgreementStatusCode = dwr.util.getValue(kemidAgreementStatusCodeFieldName);
	
	var dwrReply = {callback:function (data) {
		if (data != null) {
			setRecipientValue(kemidAgreementStatusDateFieldName, data);
		} else {
			setRecipientValue(kemidAgreementStatusDateFieldName, wrapError("kemid agreement status date error"), true);
		}
	}, errorHandler:function (errorMessage) {
		setRecipientValue(kemidAgreementStatusDateFieldName, wrapError("kemid agreement status date error"), true);
	}};
	
	KEMService.getCurrentSystemProcessDate(dwrReply);
}

