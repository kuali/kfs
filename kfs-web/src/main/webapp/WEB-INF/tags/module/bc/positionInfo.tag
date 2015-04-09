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

<c:set var="positionAttributes"	value="${DataDictionary['BudgetConstructionPosition'].attributes}" />
<c:set var="budgetConstructionPosition"	value="${KualiForm.budgetConstructionPosition}" />
<c:set var="objectName"	value="budgetConstructionPosition" />

<%-- FIXME: hidden for JS FTE calc to work remove when fix is in place --%>
<html:hidden property="${objectName}.iuPayMonths" />
<html:hidden property="${objectName}.iuNormalWorkMonths" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
        <td colspan="6" class="subhead">Position</td>
   	</tr>
   	
    <tr>
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.universityFiscalYear}"
            horizontal="true" />
            
        <bc:pbglLineDataCell dataCellCssClass="datacell"
        	accountingLine="${budgetConstructionPosition}"
            cellProperty="budgetConstructionPosition.universityFiscalYear"
            field="universityFiscalYear"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />            
        
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionNumber}"
            horizontal="true" />    
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            accountingLine="${budgetConstructionPosition}"
            cellProperty="budgetConstructionPosition.positionNumber"
            field="positionNumber"
            attributes="${positionAttributes}" inquiry="true"
            boClassSimpleName="BudgetConstructionPosition"
            boPackageName="org.kuali.kfs.module.bc.businessobject"
            readOnly="true"
            displayHidden="false"
            lookupOrInquiryKeys="positionNumber,universityFiscalYear"
            accountingLineValuesMap="${KualiForm.budgetConstructionPosition.valuesMap}"/>
            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionDescription}"
            horizontal="true" />    
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            accountingLine="${budgetConstructionPosition}"
            cellProperty="budgetConstructionPosition.positionDescription"
            field="positionDescription"
            attributes="${positionAttributes}" inquiry="true"
            boClassSimpleName="BudgetConstructionPosition"
            boPackageName="org.kuali.kfs.module.bc.businessobject"
            readOnly="true"
            displayHidden="false"/>            
    </tr>
    
    <tr>
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.iuDefaultObjectCode}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
        	accountingLine="${budgetConstructionPosition}"
            cellProperty="budgetConstructionPosition.iuDefaultObjectCode"
            field="iuDefaultObjectCode"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false"/>

        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionDepartmentIdentifier}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionDepartmentIdentifier"
            field="positionDepartmentIdentifier"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false"/>
            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.setidJobCode}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.setidJobCode"
            field="setidJobCode"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
    </tr>
    
    <tr>            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.jobCode}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.jobCode"
            field="jobCode"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />

        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionSalaryPlanDefault}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionSalaryPlanDefault"
            field="positionSalaryPlanDefault"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
        
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionGradeDefault}"
            horizontal="true" />    
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionGradeDefault"
            field="positionGradeDefault"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
    </tr>
    
    <tr>            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.iuNormalWorkMonths}"
            horizontal="true" /> 
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.iuNormalWorkMonths"
            field="iuNormalWorkMonths"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
       
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.iuPayMonths}"
            horizontal="true" />             
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.iuPayMonths"
            field="iuPayMonths"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionStandardHoursDefault}"
            horizontal="true" />  
        <fmt:formatNumber value="${budgetConstructionPosition.positionStandardHoursDefault}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionStandardHoursDefault"
            field="positionStandardHoursDefault"
            attributes="${positionAttributes}"
            readOnly="true"
            formattedNumberValue="${formattedNumber}"
            displayHidden="false" dataFieldCssClass="amount" />
    </tr>
    
    <tr>            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionFullTimeEquivalency}"
            horizontal="true" />
        <fmt:formatNumber value="${KualiForm.budgetConstructionPosition.positionFullTimeEquivalency}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionFullTimeEquivalency"
            field="positionFullTimeEquivalency"
            attributes="${positionAttributes}"
            readOnly="true"
            formattedNumberValue="${formattedNumber}"
            displayHidden="false" />

<%--
		<kul:htmlAttributeHeaderCell literalLabel=" " horizontal="true" />
	    <td>&nbsp;</td>
--%>
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionEffectiveDate}"
            horizontal="true" />    
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionEffectiveDate"
            field="positionEffectiveDate"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
	    
		<kul:htmlAttributeHeaderCell literalLabel=" " horizontal="true" />
	    <td>&nbsp;</td>	     
    </tr>
</table>
