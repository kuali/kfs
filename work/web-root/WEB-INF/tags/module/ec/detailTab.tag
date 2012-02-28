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

<%@ attribute name="isOpen" required="false" description="determine whehter the tab is open"%>
<%@ attribute name="isEditable" required="false" description="determine whehter the tab is editable"%>
<%@ attribute name="accountsCanCrossCharts" required="false"  description="Whether or not accounts can cross charts"%>

<c:set var="documentAttributes"	value="${DataDictionary.EffortCertificationDocument.attributes}" />
<c:set var="detailAttributes" value="${DataDictionary.EffortCertificationDetail.attributes}" />

<c:set var="detailLines" value="${KualiForm.detailLines}"/>
<c:set var="newDetailLine" value="${KualiForm.newDetailLine}"/>
<c:set var="tabErrorKey" value="${isOpen ? EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS : '' }" />


<kul:tab tabTitle="Effort Detail" defaultOpen="${isOpen}" tabErrorKey="${tabErrorKey}">	
<c:choose>
	<c:when test="${isEditable}">
		<div class="tab-container" align=center>
			<h3>Add New Detail Line</h3>
			<c:set var="newLineDetailFieldNames" value="chartOfAccountsCode,accountNumber,subAccountNumber,effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"/>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
				<tr>
					<ec:detailLineHeader attributes="${detailAttributes}" detailFieldNames="${newLineDetailFieldNames}"	hasActions="true"/>
				</tr>
				
				<tr>
					<kul:htmlAttributeHeaderCell literalLabel="${KFSConstants.ADD_PREFIX}: "/>
					<c:choose>
						<c:when test="${!accountsCanCrossCharts}">
						<ec:detailLine detailLine="${newDetailLine}" detailLineFormName="newDetailLine" attributes="${detailAttributes}"
							detailFieldNames="${newLineDetailFieldNames}"
							editableFieldNames="accountNumber,subAccountNumber,effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"
							detailFieldNamesWithHiddenFormWhenReadonly="chartOfAccountsCode"
							fieldInfo="${KualiForm.detailLineFieldInfo}"
							onchangeForEditableFieldNames="effortAmountUpdator.loadChartAccountInfo,loadSubAccountInfo"
							onchangeableInfoFieldNames="account.accountName,subAccount.subAccountName"
							relationshipMetadata ="${KualiForm.relationshipMetadata}"
							hasActions="true" actions="add" actionImageFileNames="tinybutton-add1.gif" />
						</c:when>
						<c:otherwise>
							<ec:detailLine detailLine="${newDetailLine}" detailLineFormName="newDetailLine" attributes="${detailAttributes}"
							detailFieldNames="${newLineDetailFieldNames}"
							editableFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"
							onchangeForEditableFieldNames="loadChartInfo,effortAmountUpdator.loadAccountInfo,loadSubAccountInfo"
							onchangeableInfoFieldNames="chartOfAccounts.finChartOfAccountDescription,account.accountName,subAccount.subAccountName"
							relationshipMetadata ="${KualiForm.relationshipMetadata}"
							hasActions="true" actions="add" actionImageFileNames="tinybutton-add1.gif" />
						</c:otherwise>
					</c:choose>
				</tr>
			</table>
		</div>
	
		<div class="tab-container" style="text-align: center;">
			<c:choose>
				<c:when test="${!accountsCanCrossCharts}">
			<ec:detailLinesWithGrouping id="editableDetailLineTable" detailLines="${detailLines}" 
				detailLineFormName="document.effortCertificationDetailLines"
				attributes="${detailAttributes}"
				detailFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,positionNumber,sourceChartOfAccountsCode,sourceAccountNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount,originalFringeBenefitAmount,fringeBenefitAmount"
				detailFieldNamesWithHiddenFormWhenReadonly="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount,originalFringeBenefitAmount,fringeBenefitAmount"				
				hiddenFieldNames="documentNumber,universityFiscalYear,newLineIndicator,federalOrFederalPassThroughIndicator,persistedPayrollAmount,persistedEffortPercent,versionNumber"
				inquirableUrl="${KualiForm.detailLineFieldInquiryUrl}"
				fieldInfo="${KualiForm.fieldInfo}"
				sortableFieldNames="chartOfAccountsCode,accountNumber,effortCertificationPayrollAmount"
				editableFieldNames="effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"
				extraEditableFieldNames="accountNumber,subAccountNumber,financialObjectCode,positionNumber"
				onchangeForExtraEditableFieldNames="effortAmountUpdator.loadAccountInfo,loadSubAccountInfo,loadObjectCodeInfo"
				onchangeableInfoFieldNames=""	
				onchangeableExtraInfoFieldNames="account.accountName,subAccount.subAccountName,financialObject.financialObjectCodeName,"			
				relationshipMetadata="${KualiForm.relationshipMetadata}"
				ferderalTotalFieldNames="federalTotalOriginalEffortPercent,federalTotalEffortPercent,federalTotalOriginalPayrollAmount,federalTotalPayrollAmount,federalTotalOriginalFringeBenefit,federalTotalFringeBenefit" 
				nonFerderalTotalFieldNames="otherTotalOriginalEffortPercent,otherTotalEffortPercent,otherTotalOriginalPayrollAmount,otherTotalPayrollAmount,otherTotalOriginalFringeBenefit,otherTotalFringeBenefit"
				grandTotalFieldNames="totalOriginalEffortPercent,totalEffortPercent,totalOriginalPayrollAmount,totalPayrollAmount,totalOriginalFringeBenefit,totalFringeBenefit"
				hasActions="true"/>
				</c:when>
				<c:otherwise>
					<ec:detailLinesWithGrouping id="editableDetailLineTable" detailLines="${detailLines}" 
				detailLineFormName="document.effortCertificationDetailLines"
				attributes="${detailAttributes}"
				detailFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,positionNumber,sourceChartOfAccountsCode,sourceAccountNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount,originalFringeBenefitAmount,fringeBenefitAmount"
				detailFieldNamesWithHiddenFormWhenReadonly="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount,originalFringeBenefitAmount,fringeBenefitAmount"				
				hiddenFieldNames="documentNumber,universityFiscalYear,newLineIndicator,federalOrFederalPassThroughIndicator,persistedPayrollAmount,persistedEffortPercent,versionNumber"
				inquirableUrl="${KualiForm.detailLineFieldInquiryUrl}"
				fieldInfo="${KualiForm.fieldInfo}"
				sortableFieldNames="chartOfAccountsCode,accountNumber,effortCertificationPayrollAmount"
				editableFieldNames="effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount"
				extraEditableFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,positionNumber"
				onchangeForExtraEditableFieldNames="loadChartInfo,effortAmountUpdator.loadAccountInfo,loadSubAccountInfo,loadObjectCodeInfo"
				onchangeableInfoFieldNames=""	
				onchangeableExtraInfoFieldNames="chartOfAccounts.finChartOfAccountDescription,account.accountName,subAccount.subAccountName,financialObject.financialObjectCodeName,"			
				relationshipMetadata="${KualiForm.relationshipMetadata}"
				ferderalTotalFieldNames="federalTotalOriginalEffortPercent,federalTotalEffortPercent,federalTotalOriginalPayrollAmount,federalTotalPayrollAmount,federalTotalOriginalFringeBenefit,federalTotalFringeBenefit" 
				nonFerderalTotalFieldNames="otherTotalOriginalEffortPercent,otherTotalEffortPercent,otherTotalOriginalPayrollAmount,otherTotalPayrollAmount,otherTotalOriginalFringeBenefit,otherTotalFringeBenefit"
				grandTotalFieldNames="totalOriginalEffortPercent,totalEffortPercent,totalOriginalPayrollAmount,totalPayrollAmount,totalOriginalFringeBenefit,totalFringeBenefit"
				hasActions="true"/>
				</c:otherwise>
			</c:choose>			
		</div>	
	</c:when>
	
	<c:otherwise>
		<div class="tab-container" style="text-align: center;">
			<ec:detailLinesWithGrouping id="readonlyDetailLineTable" detailLines="${detailLines}" 
				detailLineFormName="document.effortCertificationDetailLines"
				attributes="${detailAttributes}"
				detailFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,positionNumber,sourceChartOfAccountsCode,sourceAccountNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount,originalFringeBenefitAmount,fringeBenefitAmount"
				detailFieldNamesWithHiddenFormWhenReadonly="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,effortCertificationCalculatedOverallPercent,effortCertificationUpdatedOverallPercent,effortCertificationOriginalPayrollAmount,effortCertificationPayrollAmount,originalFringeBenefitAmount,fringeBenefitAmount"				
				hiddenFieldNames="documentNumber,universityFiscalYear,newLineIndicator,federalOrFederalPassThroughIndicator,persistedPayrollAmount,persistedEffortPercent,groupId,versionNumber"
				inquirableUrl="${KualiForm.detailLineFieldInquiryUrl}"
				fieldInfo="${KualiForm.fieldInfo}"
				ferderalTotalFieldNames="federalTotalOriginalEffortPercent,federalTotalEffortPercent,federalTotalOriginalPayrollAmount,federalTotalPayrollAmount,federalTotalOriginalFringeBenefit,federalTotalFringeBenefit" 
				nonFerderalTotalFieldNames="otherTotalOriginalEffortPercent,otherTotalEffortPercent,otherTotalOriginalPayrollAmount,otherTotalPayrollAmount,otherTotalOriginalFringeBenefit,otherTotalFringeBenefit"
				grandTotalFieldNames="totalOriginalEffortPercent,totalEffortPercent,totalOriginalPayrollAmount,totalPayrollAmount,totalOriginalFringeBenefit,totalFringeBenefit"
				hasActions="false" readOnlySection="true"/>			
		</div>
	</c:otherwise>
</c:choose>							
</kul:tab>
