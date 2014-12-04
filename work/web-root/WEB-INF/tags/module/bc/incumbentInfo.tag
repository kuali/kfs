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
  
<c:set var="intcumbentAttributes" value="${DataDictionary['BudgetConstructionIntendedIncumbent'].attributes}" />
<c:set var="budgetConstructionIntendedIncumbent" value="${KualiForm.budgetConstructionIntendedIncumbent}" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
        <td colspan="6" class="subhead">Incumbent</td>
   	</tr>
   	
    <tr>
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${intcumbentAttributes.emplid}"
            horizontal="true" />
            
        <bc:pbglLineDataCell dataCellCssClass="datacell"
        	accountingLine="budgetConstructionIntendedIncumbent"
            cellProperty="budgetConstructionIntendedIncumbent.emplid"
            field="emplid"
            attributes="${intcumbentAttributes}"
            readOnly="true"
            displayHidden="false" />                  

        <kul:htmlAttributeHeaderCell
        	attributeEntry="${intcumbentAttributes.name}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
        	accountingLine="budgetConstructionIntendedIncumbent"
            cellProperty="budgetConstructionIntendedIncumbent.name"
            field="name"
            attributes="${intcumbentAttributes}"
            readOnly="true"
            displayHidden="false"/>
           
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${intcumbentAttributes.iuClassificationLevel}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionIntendedIncumbent.iuClassificationLevel"
            field="iuClassificationLevel"
            attributes="${intcumbentAttributes}"
            readOnly="true"
            displayHidden="false" />
    </tr>
    
    <tr>
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${intcumbentAttributes.setidSalary}"
            horizontal="true" />           
        <bc:pbglLineDataCell dataCellCssClass="datacell"
        	accountingLine="budgetConstructionIntendedIncumbent"
            cellProperty="budgetConstructionIntendedIncumbent.setidSalary"
            field="setidSalary"
            attributes="${intcumbentAttributes}"
            readOnly="true"
            displayHidden="false" />                  

        <kul:htmlAttributeHeaderCell
        	attributeEntry="${intcumbentAttributes.salaryAdministrationPlan}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
        	accountingLine="budgetConstructionIntendedIncumbent"
            cellProperty="budgetConstructionIntendedIncumbent.salaryAdministrationPlan"
            field="salaryAdministrationPlan"
            attributes="${intcumbentAttributes}"
            readOnly="true"
            displayHidden="false"/>
           
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${intcumbentAttributes.grade}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionIntendedIncumbent.grade"
            field="grade"
            attributes="${intcumbentAttributes}"
            readOnly="true"
            displayHidden="false" />
    </tr>
</table>

