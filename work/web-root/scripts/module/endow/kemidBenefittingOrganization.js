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
function loadKemidBenefittingOrgLastChangeDate(kemidBenePctFieldName) {
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

function loadBenefittingChartCodeDesc(benefittingChartCodeFieldName) {
	var elPrefix = findElPrefix(benefittingChartCodeFieldName.name);
	var benefittingChartCodeDescFieldName = elPrefix + ".chart.finChartOfAccountDescription";
	var benefittingChartCode = dwr.util.getValue(benefittingChartCodeFieldName);

	setBenefittingChartCodeDescription(benefittingChartCode, benefittingChartCodeDescFieldName);
}

function setBenefittingChartCodeDescription(code, codeDescriptionFieldName) {	 
	if (code == '') {
 		clearRecipients(codeDescriptionFieldName);
 	} else {
 		var dwrReply = {
 			callback:function(data) {
 				if ( data != null && typeof data == 'object') {
 					setRecipientValue(codeDescriptionFieldName, data.finChartOfAccountDescription); 					
 				} else {
 					setRecipientValue(codeDescriptionFieldName, wrapError("Chart Code not found"), true);			
 				} },
 			errorHandler:function(errorMessage) { 
 				setRecipientValue(codeDescriptionFieldName, wrapError("Chart Code not found"), true);
 			}
 		};
 		ChartService.getByPrimaryId(code.toUpperCase(), dwrReply);
 	}
} 
 
function loadBenefittingOrganizationName(benefittingOrganizationCodeFieldName) {
	var elPrefix = findElPrefix(benefittingOrganizationCodeFieldName.name);
	var benefittingOrganizationNameFieldName = elPrefix + ".organization.organizationName";
	var benefittingChartOfAccountsCodeFieldName = elPrefix + ".benefittingChartCode";
	var organizationCode = dwr.util.getValue(benefittingOrganizationCodeFieldName);
	var chartOfAccountCode = dwr.util.getValue(benefittingChartOfAccountsCodeFieldName);
	
	setBenefittingOrganizationName(chartOfAccountCode, organizationCode, benefittingOrganizationNameFieldName);
}

function setBenefittingOrganizationName(chartOfAccountCode, organizationCode, codeDescriptionFieldName) {	 

 	if (chartOfAccountCode == '' || organizationCode == '') {
 		clearRecipients(codeDescriptionFieldName);
 	} else {
 		var dwrReply = {
 			callback:function(data) {
 				if ( data != null && typeof data == 'object') {
 					setRecipientValue(codeDescriptionFieldName, data.organizationName);
 				} else {
 					setRecipientValue(codeDescriptionFieldName, wrapError("Organization Code not found"), true);			
 				} },
 			errorHandler:function(errorMessage) { 
 				setRecipientValue(codeDescriptionFieldName, wrapError("Organization Code not found"), true);
 			}
 		};
 		OrganizationService.getByPrimaryId(chartOfAccountCode.toUpperCase(), organizationCode.toUpperCase(), dwrReply);
 	}
} 