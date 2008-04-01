<%--
 Copyright 2005-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:set var="documentAttributes"	value="${DataDictionary.EffortCertificationDocument.attributes}" />
<c:set var="detailAttributes" value="${DataDictionary.EffortCertificationDetail.attributes}" />

<c:set var="detailLines" value="${KualiForm.detailLines}"/>
<c:set var="newDetailLine" value="${KualiForm.newDetailLine}"/>

<kul:tab tabTitle="Effort Summary" defaultOpen="true" tabErrorKey="${EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS}">
	
	<div class="tab-container" align=center>
		<div class="h2-container"><h2>Add New Detail Line</h2></div>
		<c:set var="newLineHiddenFieldNames" value="universityFiscalYear,financialDocumentPostingYear,sourceChartOfAccountsCode,sourceAccountNumber,effortCertificationOriginalPayrollAmount,effortCertificationCalculatedOverallPercent,costShareSourceSubAccountNumber,fringeBenefitAmount,financialObjectCode,versionNumber"/>
		<c:set var="newLineDetailFieldNames" value="chartOfAccountsCode,accountNumber,subAccountNumber,effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"/>
		
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<er:detailLineHeader attributes="${detailAttributes}" detailFieldNames="${newLineDetailFieldNames}"	hasActions="true"/>
			</tr>
			
			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="${KFSConstants.ADD_PREFIX}: "/>
				
				<er:detailLine detailLine="${newDetailLine}" detailLineFormName="newDetailLine" attributes="${detailAttributes}"
					detailFieldNames="${newLineDetailFieldNames}"
					hiddenFieldNames="${newLineHiddenFieldNames}"
					editableFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"
					onblurForEditableFieldNames="loadChartInfo,effortAmountUpdator.loadAccountInfo,loadSubAccountInfo"
					onblurableInfoFieldNames="chartOfAccounts.finChartOfAccountDescription,account.accountName,subAccount.subAccountName"
					relationshipMetadata ="${KualiForm.relationshipMetadata}"
					hasActions="true" actions="add" actionImageFileNames="tinybutton-add1.gif" />
			</tr>
		</table>
	</div>
	
	<div class="tab-container" align=center>
		<er:detailLinesWithGrouping id="editableDetailLineTable" detailLines="${detailLines}" 
			attributes="${detailAttributes}"
			detailFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount"
			detailFieldNamesWithHiddenFormWhenReadonly="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount"				
			hiddenFieldNames="documentNumber,universityFiscalYear,financialDocumentPostingYear,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,costShareSourceSubAccountNumber,originalFringeBenefitAmount,fringeBenefitAmount,newLineIndicator,federalOrFederalPassThroughIndicator,persistedPayrollAmount,versionNumber"
			inquirableUrl="${KualiForm.detailLineFieldInquiryUrl}"
			fieldInfo="${KualiForm.fieldInfo}"
			sortableFieldNames="chartOfAccountsCode,accountNumber,effortCertificationPayrollAmount"
			editableFieldNames="effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"
			extraEditableFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber"
			onblurForEditableFieldNames="effortAmountUpdator.recalculatePayrollAmount,effortAmountUpdator.recalculateEffortPercent"
			onblurForExtraEditableFieldNames="loadChartInfo,effortAmountUpdator.loadAccountInfo,loadSubAccountInfo"
			onblurableInfoFieldNames=""	
			onblurableExtraInfoFieldNames="chartOfAccounts.finChartOfAccountDescription,account.accountName,subAccount.subAccountName"			
			relationshipMetadata="${KualiForm.relationshipMetadata}"
			ferderalTotalFieldNames="federalTotalOriginalEffortPercent,federalTotalEffortPercent,federalTotalOriginalPayrollAmount,federalTotalPayrollAmount" 
			nonFerderalTotalFieldNames="otherTotalOriginalEffortPercent,otherTotalEffortPercent,otherTotalOriginalPayrollAmount,otherTotalPayrollAmount"
			grandTotalFieldNames="totalOriginalEffortPercent,totalEffortPercent,totalOriginalPayrollAmount,totalPayrollAmount"
			hasActions="true"/>			
	</div>						
</kul:tab>