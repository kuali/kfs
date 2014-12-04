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
