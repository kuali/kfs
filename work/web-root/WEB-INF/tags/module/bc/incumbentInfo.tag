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

