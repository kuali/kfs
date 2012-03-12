<%--
 Copyright 2005-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ attribute name="accountsCanCrossCharts" required="false"  description="Whether or not accounts can cross charts"%>
<c:set var="documentAttributes"	value="${DataDictionary.EffortCertificationDocument.attributes}" />
<c:set var="detailAttributes" value="${DataDictionary.EffortCertificationDetail.attributes}" />

<c:set var="summarizedDetailLines" value="${KualiForm.document.summarizedDetailLines}"/>
<c:set var="newDetailLine" value="${KualiForm.newDetailLine}"/>
   
    
<kul:tab tabTitle="Effort Summary" defaultOpen="true" tabErrorKey="${EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS}">
	
	<div class="tab-container" align=center>
		<h3>Add New Detail Line</h3>
		<c:set var="newLineDetailFieldNames" value="chartOfAccountsCode,accountNumber,subAccountNumber,effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"/>
		<c:set var="newLineHiddenFieldNames" value="universityFiscalYear,sourceChartOfAccountsCode,sourceAccountNumber,effortCertificationOriginalPayrollAmount,effortCertificationCalculatedOverallPercent,costShareSourceSubAccountNumber,fringeBenefitAmount,financialObjectCode,versionNumber"/>
		
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">		
			<tr>
				<ec:detailLineHeader attributes="${detailAttributes}" detailFieldNames="${newLineDetailFieldNames}"	hasActions="true"/>
			</tr>
			
			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="${KFSConstants.ADD_PREFIX}: "/>
				<c:choose>
				<c:when test="${!accountsCanCrossCharts}">
				 <span id="document.chartOfAccountsCode.div">
				<ec:detailLine detailLine="${newDetailLine}" detailLineFormName="newDetailLine" attributes="${detailAttributes}"
					detailFieldNames="${newLineDetailFieldNames}"
					editableFieldNames="accountNumber,subAccountNumber,effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"
					detailFieldNamesWithHiddenFormWhenReadonly="chartOfAccountsCode"
					fieldInfo="${KualiForm.detailLineFieldInfo}"
					onchangeForEditableFieldNames="effortAmountUpdator.loadChartAccountInfo,loadSubAccountInfo"
					onchangeableInfoFieldNames="account.accountName,subAccount.subAccountName"
					relationshipMetadata ="${KualiForm.relationshipMetadata}"
					hasActions="true" actions="addSummarizedDetailLine" actionImageFileNames="tinybutton-add1.gif" />
				</span>
				</c:when>
				<c:otherwise>
					<ec:detailLine detailLine="${newDetailLine}" detailLineFormName="newDetailLine" attributes="${detailAttributes}"
						detailFieldNames="${newLineDetailFieldNames}"
						editableFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"
						onchangeForEditableFieldNames="loadChartInfo,effortAmountUpdator.prototype.loadAccountInfo,loadSubAccountInfo"
						onchangeableInfoFieldNames="chartOfAccounts.finChartOfAccountDescription,account.accountName,subAccount.subAccountName"
						relationshipMetadata ="${KualiForm.relationshipMetadata}"
						hasActions="true" actions="addSummarizedDetailLine" actionImageFileNames="tinybutton-add1.gif" />
				</c:otherwise>
				</c:choose>
			</tr>
		</table>
	</div>
	
	<div class="tab-container" align=center>
		<c:choose>
			<c:when test="${accountsCanCrossCharts}">
			<ec:detailLinesWithGrouping id="editableDetailLineTable" 
				detailLines="${summarizedDetailLines}"
				detailLineFormName="document.summarizedDetailLines" 
				attributes="${detailAttributes}"
				detailFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount"
				detailFieldNamesWithHiddenFormWhenReadonly="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount"				
				hiddenFieldNames="documentNumber,universityFiscalYear,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,costShareSourceSubAccountNumber,originalFringeBenefitAmount,fringeBenefitAmount,newLineIndicator,federalOrFederalPassThroughIndicator,persistedPayrollAmount,persistedEffortPercent,groupId,versionNumber"			
				inquirableUrl="${KualiForm.summarizedDetailLineFieldInquiryUrl}"
				fieldInfo="${KualiForm.summarizedDetailLineFieldInfo}"
				sortableFieldNames="chartOfAccountsCode,accountNumber,effortCertificationPayrollAmount"
				editableFieldNames="effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"
				extraEditableFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber"
				onchangeForExtraEditableFieldNames="loadChartInfo,effortAmountUpdator.loadAccountInfo,loadSubAccountInfo"
				onchangeableInfoFieldNames=""	
				onchangeableExtraInfoFieldNames="chartOfAccounts.finChartOfAccountDescription,account.accountName,subAccount.subAccountName"			
				relationshipMetadata="${KualiForm.relationshipMetadata}"
				ferderalTotalFieldNames="federalTotalOriginalEffortPercent,federalTotalEffortPercent,federalTotalOriginalPayrollAmount,federalTotalPayrollAmount" 
				nonFerderalTotalFieldNames="otherTotalOriginalEffortPercent,otherTotalEffortPercent,otherTotalOriginalPayrollAmount,otherTotalPayrollAmount"
				grandTotalFieldNames="totalOriginalEffortPercent,totalEffortPercent,totalOriginalPayrollAmount,totalPayrollAmount"
				hasActions="true" actionSuffix="SummarizedDetailLine"/>
			</c:when>	
			<c:otherwise>
				 <span id="document.summarizedDetailLines.div">
				 <ec:detailLinesWithGrouping id="editableDetailLineTable" 
					detailLines="${summarizedDetailLines}"
					detailLineFormName="document.summarizedDetailLines" 
					attributes="${detailAttributes}"
					detailFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount"
					detailFieldNamesWithHiddenFormWhenReadonly="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount"				
					hiddenFieldNames="documentNumber,universityFiscalYear,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,costShareSourceSubAccountNumber,originalFringeBenefitAmount,fringeBenefitAmount,newLineIndicator,federalOrFederalPassThroughIndicator,persistedPayrollAmount,persistedEffortPercent,groupId,versionNumber"			
					inquirableUrl="${KualiForm.summarizedDetailLineFieldInquiryUrl}"
					fieldInfo="${KualiForm.summarizedDetailLineFieldInfo}"
					sortableFieldNames="chartOfAccountsCode,accountNumber,effortCertificationPayrollAmount"
					editableFieldNames="effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"
					extraEditableFieldNames="accountNumber,subAccountNumber"
					onchangeForExtraEditableFieldNames="effortAmountUpdator.loadAccountInfo,loadSubAccountInfo"
					onchangeableInfoFieldNames=""	
					onchangeableExtraInfoFieldNames="account.accountName,subAccount.subAccountName"			
					relationshipMetadata="${KualiForm.relationshipMetadata}"
					ferderalTotalFieldNames="federalTotalOriginalEffortPercent,federalTotalEffortPercent,federalTotalOriginalPayrollAmount,federalTotalPayrollAmount" 
					nonFerderalTotalFieldNames="otherTotalOriginalEffortPercent,otherTotalEffortPercent,otherTotalOriginalPayrollAmount,otherTotalPayrollAmount"
					grandTotalFieldNames="totalOriginalEffortPercent,totalEffortPercent,totalOriginalPayrollAmount,totalPayrollAmount"
					hasActions="true" actionSuffix="SummarizedDetailLine"/>
				 </span>
			</c:otherwise>		
		</c:choose>
	</div>
</kul:tab>
