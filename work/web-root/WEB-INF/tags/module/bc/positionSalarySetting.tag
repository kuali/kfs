<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="readOnly" required="false" description="determine whether the contents can be read only or not"%>
<%@ attribute name="accountsCanCrossCharts" required="false"  description="Whether or not accounts can cross charts"%>

<c:set var="tableWidth" value="100%"/>
<c:set var="isKeyFieldsLocked" value="${KualiForm.singleAccountMode}"/>

<html:hidden property="budgetByAccountMode" />

<kul:tabTop tabTitle="Position" defaultOpen="true">
	<div class="tab-container" align=center>
		<bc:positionInfo/>
	</div>
</kul:tabTop>

<c:set var="budgetConstructionPosition" value="${KualiForm.budgetConstructionPosition}" />
    
<kul:tab tabTitle="Position Funding" defaultOpen="true" tabErrorKey="${BCConstants.ErrorKey.DETAIL_SALARY_SETTING_TAB_ERRORS}">
<div class="tab-container" align="center">
	<c:if test="${not readOnly && budgetConstructionPosition.effective && budgetConstructionPosition.budgetedPosition}">   
		<kul:subtab lookedUpCollectionName="fundingLine" width="${tableWidth}" subTabTitle="Add Funding">      
			<bc:appointmentFundingLineForPosition fundingLine="${KualiForm.newBCAFLine}" fundingLineName="newBCAFLine" hasBeenAdded="false" isKeyFieldsLocked="${isKeyFieldsLocked}" countOfMajorColumns="9" accountsCanCrossCharts="${accountsCanCrossCharts}">
				<html:image property="methodToCall.addAppointmentFundingLine.anchorsalarynewLineLineAnchor" 
			       	src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" 
			       	title="Add a Salary Setting Line" alt="Add a Salary Setting Line" styleClass="tinybutton"/>
			</bc:appointmentFundingLineForPosition>
		</kul:subtab>
	</c:if>
        		
    <c:forEach items="${KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding}" var="fundingLine" varStatus="status">
    <c:if test="${!fundingLine.purged}">	 
    	<c:set var="fundingLineName" value="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"/>
    	<c:set var="editable" value="${!readOnly && !fundingLine.displayOnlyMode}"/>
		<c:set var="isVacant" value="${fundingLine.emplid eq BCConstants.VACANT_EMPLID}" />
		<c:set var="isNewLine" value="${fundingLine.newLineIndicator}" />
		<c:set var="hasBeenDeleted" value="${fundingLine.appointmentFundingDeleteIndicator}" />
		<c:set var="markedAsDelete" value="${!fundingLine.persistedDeleteIndicator && fundingLine.appointmentFundingDeleteIndicator}" />
		<c:set var="hidePercentAdjustment" value="${fundingLine.appointmentFundingDeleteIndicator || KualiForm.hideAdjustmentMeasurement || readOnly || empty fundingLine.bcnCalculatedSalaryFoundationTracker}" />
		
		<c:set var="canPurge" value="${editable && !fundingLine.purged && empty fundingLine.bcnCalculatedSalaryFoundationTracker}" />
		<c:set var="canUnpurge" value="${editable && fundingLine.purged}" />
		
		<c:set var="canDelete" value="${editable && !hasBeenDeleted && not isNewLine}" />
		<c:set var="canUndelete" value="${editable && hasBeenDeleted}" /> 
		
		<c:set var="canVacate" value="${editable && fundingLine.vacatable}"/>
		<c:set var="canRevert" value="${editable && markedAsDelete && not isVacant && not isNewLine && not fundingLine.vacatable}" />
		
		<c:set var="canRecalculate" value="${editable && !hasBeenDeleted && fundingLine.positionChangeIndicator}" />
			    
	    <kul:subtab lookedUpCollectionName="fundingLine" width="${tableWidth}" subTabTitle="${fundingLine.appointmentFundingString}" >
	    	<bc:appointmentFundingLineForPosition fundingLine="${fundingLine}" fundingLineName="${fundingLineName}"	
	    		countOfMajorColumns="9" lineIndex="${status.index}" hasBeenAdded = "true" readOnly="${readOnly}" accountsCanCrossCharts="${accountsCanCrossCharts}">    		
	    		<c:if test="${canRecalculate}">
					<html:image property="methodToCall.recalculateSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-calculate.gif" 
						title="Recalulate Salary Setting Line ${status.index}"
						alt="Recalulate Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
	    		<c:if test="${canVacate}">
					<html:image property="methodToCall.vacateSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-vacate.gif" 
						title="Vacate Salary Setting Line ${status.index}"
						alt="Vacate Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
				<c:if test="${canRevert}">	
					<html:image property="methodToCall.revertSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-revert1.gif" 
						title="revert Salary Setting Line ${status.index}"
						alt="revert Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>				
				
				<c:if test="${canPurge}">	
					<html:image property="methodToCall.purgeSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-purge.gif" 
						title="Purge Salary Setting Line ${status.index}"
						alt="Purge Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
				
				<c:if test="${canDelete}">	
					<html:image property="methodToCall.deleteSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-delete1.gif" 
						title="Delete Salary Setting Line ${status.index}"
						alt="Delete Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
				
				<c:if test="${canUndelete}">	
					<html:image property="methodToCall.undeleteSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-undelete.gif" 
						title="undelete Salary Setting Line ${status.index}"
						alt="undelete Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
			</bc:appointmentFundingLineForPosition>	
		</kul:subtab>
	</c:if>
	</c:forEach>
        
    <kul:subtab lookedUpCollectionName="fundingLine" width="${tableWidth}" subTabTitle="Totals" > 
    	<table border="0" cellpadding="0" cellspacing="0" style="width: ${tableWidth};" class="datatable">    
			<tr><td class="infoline"><center><br/>
				<bc:appointmentFundingTotal pcafAware="${KualiForm.budgetConstructionPosition}"/>
			<br/></center></td><tr>
		</table>
	</kul:subtab>
</div>
</kul:tab>

<c:if test="${!readOnly}" >
	<kul:tab tabTitle="Purged Appointment Funding" defaultOpen="false">
	<div class="tab-container" align="center">        		
	    <c:forEach items="${KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding}" var="fundingLine" varStatus="status">
		<c:if test="${fundingLine.purged}">	 
			<c:set var="fundingLineName" value="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"/>
			         	
		    <kul:subtab lookedUpCollectionName="fundingLine" width="${tableWidth}" subTabTitle="${fundingLine.appointmentFundingString}">
		    	<bc:appointmentFundingLineForPosition fundingLine="${fundingLine}" fundingLineName="${fundingLineName}"	countOfMajorColumns="9" lineIndex="${status.index}" readOnly="true" hasBeenAdded = "true">    		
					<html:image property="methodToCall.restorePurgedSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-restore.gif" 
						title="Restore the Purged Salary Setting Line ${status.index}"
						alt="Restore the Purged Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</bc:appointmentFundingLineForPosition>	
			</kul:subtab>
		</c:if>
		</c:forEach>
	</div>
	</kul:tab>
</c:if>
