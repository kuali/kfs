<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<%@ attribute name="supportsModular" required="true" %>

<c:set var="budgetAttributes" value="${DataDictionary.Budget.attributes}" />
<c:set var="businessObjectClass" value="${DataDictionary.Budget.businessObjectClass}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>

<script type="text/javascript">
	function modularVarianceToggle() {
		var agencyModularIndicator = document.forms[0].elements['document.budget.agencyModularIndicator'].checked;
		var budgetModularTaskNumber = document.forms[0].elements['document.budget.modularBudget.budgetModularTaskNumber'];
		
		if (budgetModularTaskNumber.length >= 1) {
		    for (var i = 1; i < budgetModularTaskNumber.length; i++) {
		     	budgetModularTaskNumber[i].disabled = !agencyModularIndicator;
		    }
			budgetModularTaskNumber[1].checked = agencyModularIndicator;
		}
	}
</script>

<kul:tab 
	tabTitle="General" 
	defaultOpen="true" 
	tabErrorKey="document.budget.parameters*,document.budget.budgetAgency*,document.budget.budgetProject*,document.budget.institution*,document.budget.electronic*,document.budget.federal*,document.budget.modular*,document.budget.budgetPersonnelInflationRate,document.budget.budgetNonpersonnelInflationRate" 
	auditCluster="parametersAuditErrors" 
	tabAuditKey="document.budget.audit.parameters.pd*">

    <div class="tab-container" id="G02" style="" align="center">
              
<div class="h2-container"> <span class="subhead-left">
  <a name="General"></a><h2>General</h2>
  </span><span class="subhead-right"> <span class="subhead"><kul:help businessObjectClassName="${businessObjectClass}" altText="help"/></span> </span> </div>
  <table cellpadding="0" cellspacing="0" class="datatable" summary=""> 
  
  <tr>
    <th scope="row" ><div align="right">* ${budgetAttributes.budgetProjectDirectorUniversalIdentifier.label}:</div></th>
    <td>
    	<html:hidden property="document.budget.projectDirectorToBeNamedIndicator"/>
    	<html:hidden property="document.budget.budgetProjectDirectorUniversalIdentifier" />
    	<html:hidden write="true" property="document.budget.projectDirector.universalUser.personName"/>
      <html:hidden property="document.budget.projectDirector.universalUser.personUniversalIdentifier"/>
      <html:hidden property="document.budget.projectDirector.personUniversalIdentifier"/>
    	<c:if test="${empty KualiForm.document.budget.budgetProjectDirectorUniversalIdentifier && !KualiForm.document.budget.projectDirectorToBeNamedIndicator}">(select)</c:if>
    	<c:if test="${KualiForm.document.budget.projectDirectorToBeNamedIndicator}">TO BE NAMED</c:if>
    	<c:if test="${!viewOnly}">
	    	<kul:lookup boClassName="org.kuali.module.cg.bo.ProjectDirector" fieldConversions="universalUser.personUniversalIdentifier:document.budget.budgetProjectDirectorUniversalIdentifier,universalUser.personName:document.budget.projectDirector.universalUser.personName," tabindexOverride="5000" extraButtonSource="images/buttonsmall_namelater.gif" extraButtonParams="&document.budget.projectDirectorToBeNamedIndicator=true" anchor="${currentTabIndex}" />
    	</c:if>
    </td>
    <th scope="row" ><div align="right">* ${budgetAttributes.budgetPersonnelInflationRate.label}:</div></th>
    <td  nowrap="nowrap"><kul:htmlControlAttribute property="document.budget.budgetPersonnelInflationRate" attributeEntry="${budgetAttributes.budgetPersonnelInflationRate}" readOnly="${viewOnly}" tabindexOverride="5080"  styleClass="amount"/> % (Range: 0 - 11.00)</td>
  </tr>
  
  <tr>
    <th scope="row" ><div align="right">${budgetAttributes.budgetName.label}:</div></th>
    <td><kul:htmlControlAttribute property="document.budget.budgetName" attributeEntry="${budgetAttributes.budgetName}" readOnly="${viewOnly}" tabindexOverride="5010" /></td>
    <th scope="row" ><div align="right">* ${budgetAttributes.budgetNonpersonnelInflationRate.label}:</div></th>
    <td  nowrap="nowrap"><kul:htmlControlAttribute property="document.budget.budgetNonpersonnelInflationRate" attributeEntry="${budgetAttributes.budgetNonpersonnelInflationRate}" readOnly="${viewOnly}" tabindexOverride="5090"  styleClass="amount"/> % (Range: 0 - 11.00)</td>
  </tr>
  
  <tr>
    <th scope="row" ><div align="right">${budgetAttributes.budgetProgramAnnouncementName.label}:</div></th>
    <td><kul:htmlControlAttribute property="document.budget.budgetProgramAnnouncementName" attributeEntry="${budgetAttributes.budgetProgramAnnouncementName}" readOnly="${viewOnly}" tabindexOverride="5020" /></td>
    <th scope="row" ><div align="right">* ${budgetAttributes.budgetAgency.label}:</div></th>
    <td>
    	<html:hidden property="document.budget.agencyToBeNamedIndicator" />
    	<html:hidden property="document.budget.budgetAgencyNumber" /> 
    	<html:hidden write="true" property="document.budget.budgetAgency.fullName"/>
    	<c:if test="${empty KualiForm.document.budget.budgetAgencyNumber && !KualiForm.document.budget.agencyToBeNamedIndicator}">(select)</c:if>
    	<c:if test="${KualiForm.document.budget.agencyToBeNamedIndicator}">TO BE NAMED</c:if>
    	<c:if test="${!viewOnly}">
    		<kul:lookup boClassName="org.kuali.module.cg.bo.Agency" lookupParameters="document.budget.budgetAgencyNumber:agencyNumber,document.budget.budgetAgency.fullName:fullName" fieldConversions="agencyNumber:document.budget.budgetAgencyNumber,fullName:document.budget.budgetAgency.fullName" tabindexOverride="5100" extraButtonSource="images/buttonsmall_namelater.gif" extraButtonParams="&document.budget.agencyToBeNamedIndicator=true" anchor="${currentTabIndex}" />
    	</c:if>
    </td>
  </tr>
  
  <tr>
    <th scope="row" ><div align="right">${budgetAttributes.budgetProgramAnnouncementNumber.label}:</div></th>
    <td><kul:htmlControlAttribute property="document.budget.budgetProgramAnnouncementNumber" attributeEntry="${budgetAttributes.budgetProgramAnnouncementNumber}" readOnly="${viewOnly}" tabindexOverride="5030" /></td>
    <th scope="row" ><div align="right">${budgetAttributes.federalPassThroughAgency.label}:</div></th>
    <td>
    	<html:hidden property="document.budget.federalPassThroughAgencyNumber" />
    	<html:hidden write="true" property="document.budget.federalPassThroughAgency.fullName"/>
    	<c:choose>
    		<c:when test="${!viewOnly && KualiForm.document.budget.budgetAgency.agencyTypeCode != Constants.AGENCY_TYPE_CODE_FEDERAL}">
    			<c:if test="${empty KualiForm.document.budget.federalPassThroughAgencyNumber}">(select)</c:if>
    			<kul:lookup boClassName="org.kuali.module.cg.bo.Agency" fieldConversions="agencyNumber:document.budget.federalPassThroughAgencyNumber,fullName:document.budget.federalPassThroughAgency.fullName" tabindexOverride="5110" anchor="${currentTabIndex}" />
    		</c:when>
    		<c:otherwise>
    			N/A
    		</c:otherwise>
    	</c:choose>
    </td>
  </tr>
  
  <tr>
    <th scope="row" ><div align="right">${budgetAttributes.electronicResearchAdministrationGrantNumber.label}:</div></th>
    <td><kul:htmlControlAttribute property="document.budget.electronicResearchAdministrationGrantNumber" attributeEntry="${budgetAttributes.electronicResearchAdministrationGrantNumber}" readOnly="${viewOnly}" tabindexOverride="5040" /></td>
    <th scope="row" ><div align="right"><kul:htmlAttributeLabel attributeEntry="${budgetAttributes.institutionCostShareIndicator}" labelFor="document.budget.institutionCostShareIndicator" skipHelpUrl="true" /></div></th>
    <td><kul:htmlControlAttribute property="document.budget.institutionCostShareIndicator" attributeEntry="${budgetAttributes.institutionCostShareIndicator}" readOnly="${viewOnly}" tabindexOverride="5120"/>
      <label for="document.budget.institutionCostShareIndicator"> include</label></td>
  </tr>
  
  <tr>
    <th scope="row" ><div align="right">* <kul:htmlAttributeLabel attributeEntry="${budgetAttributes.budgetTypeCodeText}" skipHelpUrl="true" /></div></th>
    <td>
    	<logic:iterate id="budgetType" name="KualiForm" property="budgetTypeCodes" indexId="i"> 
    		<html:hidden property="budgetTypeCode[${i}].budgetTypeCode" /> 
    		<html:hidden property="budgetTypeCode[${i}].budgetTypeDescription" />
    		<c:choose>
    			<c:when test="${not viewOnly}">
		    		<html:multibox property="selectedBudgetTypesMultiboxFix(${budgetType.budgetTypeCode})" value="${budgetType.budgetTypeCode}"/>
		    		<%-- <html:multibox name="KualiForm" property="document.budget.budgetTypeCodeArray"> ${budgetType.budgetTypeCode} </html:multibox> --%> ${budgetType.budgetTypeDescription} <br/>
    			</c:when>
    			<c:otherwise>
    				<c:choose>
    					<c:when test="${fn:contains(KualiForm.document.budget.budgetTypeCodeText, budgetType.budgetTypeCode)}"> Yes </c:when>
    					<c:otherwise> No </c:otherwise>
    				</c:choose>
    				${budgetType.budgetTypeDescription} <br/>
    			</c:otherwise>
    		</c:choose>
      	</logic:iterate> 
      	
      <kul:htmlControlAttribute property="document.budget.agencyModularIndicator" attributeEntry="${budgetAttributes.agencyModularIndicator}" readOnly="${viewOnly}" onclick="modularVarianceToggle(); " disabled="${!supportsModular}" tabindexOverride="5070"/><kul:htmlAttributeLabel attributeEntry="${budgetAttributes.agencyModularIndicator}" labelFor="document.budget.agencyModularIndicator" useShortLabel="true" skipHelpUrl="true" noColon="true" />
      <html:hidden property="supportsModular" />
    </td>
    <th scope="row" ><div align="right"><kul:htmlAttributeLabel attributeEntry="${budgetAttributes.budgetThirdPartyCostShareIndicator}" labelFor="document.budget.budgetThirdPartyCostShareIndicator" skipHelpUrl="true" /></div></th>
    <td><kul:htmlControlAttribute property="document.budget.budgetThirdPartyCostShareIndicator" attributeEntry="${budgetAttributes.budgetThirdPartyCostShareIndicator}" readOnly="${viewOnly}" tabindexOverride="5130"/>
      <label for="document.budget.budgetThirdPartyCostShareIndicator"> include</label></td>
  </tr>
  
  </tbody>
</table>
<div align="right">*required&nbsp;&nbsp;&nbsp;
</div>


</div>
      	</kul:tab>
        