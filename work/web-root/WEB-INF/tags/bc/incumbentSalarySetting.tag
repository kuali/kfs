<%--
 Copyright 2007 The Kuali Foundation.
 
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

<%@ attribute name="readOnly" required="false" description="determine whether the contents can be read only or not"%>

<html:hidden property="returnAnchor" />
<html:hidden property="returnFormKey" />
<html:hidden property="backLocation" />
<html:hidden property="universityFiscalYear" />
<html:hidden property="chartOfAccountsCode" />
<html:hidden property="accountNumber" />
<html:hidden property="subAccountNumber" />
<html:hidden property="financialObjectCode" />
<html:hidden property="financialSubObjectCode" />
<html:hidden property="emplid" />
<html:hidden property="budgetByAccountMode" />
<html:hidden property="addLine" />

<kul:tabTop tabTitle="Incumbent" defaultOpen="true">
	<div class="tab-container" align=center>
		<bc:incumbentInfo/>
	</div>
</kul:tabTop>

<kul:tab tabTitle="Salary Setting by Incumbent" defaultOpen="true" tabErrorKey="${KFSConstants.BUDGET_CONSTRUCTION_INCUMBENT_SALARY_SETTING_TAB_ERRORS}">
<div class="tab-container" align=center>

	<kul:subtab lookedUpCollectionName="fundingLine" width="${tableWidth}" subTabTitle="Add Funding">      
		<bc:appointmentFundingLineForIncumbent fundingLine="${KualiForm.newBCAFLine}" fundingLineName="newBCAFLine" countOfMajorColumns="11" hasBeenAdded="false">
			<html:image property="methodToCall.insertSalarySettingLine.anchorsalarynewLineLineAnchor" 
		       	src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" 
		       	title="Add a Salary Setting Line" alt="Add a Salary Setting Line" styleClass="tinybutton"/>
		</bc:appointmentFundingLineForIncumbent>
	</kul:subtab>
        		
    <c:forEach items="${KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding}" var="fundingLine" varStatus="status">
		<c:set var="fundingLineName" value="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"/>
		<c:set var="isVacant" value="${fundingLine.emplid eq KFSConstants.BudgetConstructionConstants.VACANT_EMPLID}" />
		<c:set var="isNewLine" value="${fundingLine.newLineIndicator}" />
		<c:set var="hidePercentAdjustment" value="${fundingLine.appointmentFundingDeleteIndicator || KualiForm.hideAdjustmentMeasurement || readOnly || empty fundingLine.bcnCalculatedSalaryFoundationTracker}" />
		<c:set var="notEditable" value="${readOnly || fundingLine.persistedDeleteIndicator || fundingLine.displayOnlyMode}"/>
		<c:set var="canPurge" value="${not notEditable && empty fundingLine.bcnCalculatedSalaryFoundationTracker}" />
		<c:set var="canDelete" value="${not notEditable && not isVacant && not isNewLine && not fundingLine.appointmentFundingDeleteIndicator }" />
		<c:set var="canUndelete" value="${not notEditable && not isVacant && not isNewLine && not fundingLine.vacatable && fundingLine.appointmentFundingDeleteIndicator}" />		
	
	    <c:set var="subTabTitle" value="${fundingLine.chartOfAccountsCode}"/>
	    <c:set var="subTabTitle" value="${subTabTitle}, ${fundingLine.accountNumber}"/>
	    <c:set var="subTabTitle" value="${subTabTitle}, ${fundingLine.subAccountNumber}"/>
	    <c:set var="subTabTitle" value="${subTabTitle}, ${fundingLine.financialObjectCode}"/>
	    <c:set var="subTabTitle" value="${subTabTitle}, ${fundingLine.financialSubObjectCode}"/>
	    <c:set var="subTabTitle" value="${subTabTitle}, ${fundingLine.positionNumber}"/>    
	          	
	    <kul:subtab lookedUpCollectionName="fundingLine" width="${tableWidth}" subTabTitle="${subTabTitle}" >
	    	<bc:appointmentFundingLineForIncumbent fundingLine="${fundingLine}" fundingLineName="${fundingLineName}" countOfMajorColumns="11" 
	    		lineIndex="${status.index}" hasBeenAdded="true" readOnly="${readOnly}">    		
				
				<c:if test="${fundingLine.vacatable}">
					<html:image property="methodToCall.vacateSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-vacate.gif" 
						title="Vacate Salary Setting Line ${status.index}"
						alt="Vacate Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
				
				<c:if test="${canPurge}">	
					<html:image property="methodToCall.purgeSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-purge.gif" 
						title="Purge Salary Setting Line ${status.index}"
						alt="Purge Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
				
				<c:if test="${canDelete && not canPurge}">	
					<html:image property="methodToCall.deleteSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-delete1.gif" 
						title="Delete Salary Setting Line ${status.index}"
						alt="Delete Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
				
				<c:if test="${canUndelete}">	
					<html:image property="methodToCall.revertSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-revert1.gif" 
						title="revert Salary Setting Line ${status.index}"
						alt="revert Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
			</bc:appointmentFundingLineForIncumbent>	
		</kul:subtab>
	</c:forEach>
        
    <kul:subtab lookedUpCollectionName="fundingLine" width="${tableWidth}" subTabTitle="Totals" > 
    	<table border="0" cellpadding="0" cellspacing="0" style="width: ${tableWidth};" class="datatable">    
			<tr><td class="infoline"><center><br/>
				<bc:appointmentFundingTotal pcafAware="${KualiForm.budgetConstructionIntendedIncumbent}"/>
			<br/></center></td><tr>
		</table>
	</kul:subtab>
</div>
</kul:tab>
