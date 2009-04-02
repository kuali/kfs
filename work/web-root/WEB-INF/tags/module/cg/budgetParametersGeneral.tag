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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="supportsModular" required="true" %>

<c:set var="budgetAttributes" value="${DataDictionary.Budget.attributes}" />
<c:set var="businessObjectClass" value="${DataDictionary.Budget.businessObjectClass}" />
<c:set var="viewOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>
<c:set var="routingFormAttributes" value="${DataDictionary.RoutingFormDocument.attributes}" />

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
	<div class="tab-container-error"><div class="left-errmsg-tab"><cg:auditErrors cluster="parametersAuditErrors" keyMatch="document.budget.audit.parameters.pd*" isLink="false" includesTitle="true"/></div></div>

    <div class="tab-container" id="G02" style="" align="center">
              
  <a name="General"></a><h3>General</h3>
  <table cellpadding="0" cellspacing="0" class="datatable" summary=""> 
  
  <tr>
    <th scope="row" ><div align="right"><label for="document.budget.projectDirector.principalName">* ${budgetAttributes.budgetProjectDirectorUniversalIdentifier.label}</label>:</div></th>
    <td>
      <c:if test="${!viewOnly}">
        <html:text styleId="document.budget.projectDirector.principalName" property="document.budget.projectDirector.principalName" onblur="personIDLookup('document.budget.projectDirector.principalName')"/>
      	<c:if test="${KualiForm.document.budget.projectDirectorToBeNamedIndicator}">TO BE NAMED</c:if>
	    	<kul:lookup boClassName="org.kuali.rice.kim.bo.impl.PersonImpl" fieldConversions="principalId:document.budget.budgetProjectDirectorUniversalIdentifier,name:document.budget.projectDirector.name," tabindexOverride="5000" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_namelater.gif" extraButtonParams="&document.budget.projectDirectorToBeNamedIndicator=true" anchor="General" />
    	</c:if>
          <div id="document.budget.projectDirector.name.div" >
             <c:if test="${!empty KualiForm.document.budget.projectDirector.principalName}">
                 <c:choose>
					<c:when test="${empty KualiForm.document.budget.projectDirector.name}">
						<span style='color: red;'><c:out value="person not found" /> </span>
					</c:when>
					<c:otherwise>
						<c:out value="${KualiForm.document.budget.projectDirector.name}" />
					</c:otherwise>
				 </c:choose>                        
              </c:if>
           </div>

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
    <th scope="row" ><div align="right"><label for="document.budget.budgetAgencyNumber">* ${budgetAttributes.budgetAgency.label}</;label>:</div></th>
    <td>
    	<html:hidden property="document.budget.budgetAgency.agencyTypeCode" styleId="document.budget.budgetAgency.agencyTypeCode"/>
        <kul:htmlControlAttribute property="document.budget.budgetAgencyNumber" attributeEntry="${routingFormAttributes.routingFormAgency}" readOnly="${viewOnly}" onblur="onblur_agencyNumber('document.budget.budgetAgencyNumber','budgetAgency');"/>
    	<c:if test="${KualiForm.document.budget.agencyToBeNamedIndicator}">TO BE NAMED</c:if>
    	<c:if test="${!viewOnly}">
    		<kul:lookup boClassName="org.kuali.kfs.module.cg.businessobject.Agency" lookupParameters="document.budget.budgetAgencyNumber:agencyNumber,document.budget.budgetAgency.fullName:fullName" fieldConversions="agencyNumber:document.budget.budgetAgencyNumber,fullName:document.budget.budgetAgency.fullName" tabindexOverride="5100" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_namelater.gif" extraButtonParams="&document.budget.agencyToBeNamedIndicator=true" anchor="General" />
    	</c:if>
          <div id="document.budget.budgetAgency.fullName.div" >
             <c:if test="${!empty KualiForm.document.budget.budgetAgencyNumber}">
                 <c:choose>
					<c:when test="${empty KualiForm.document.budget.budgetAgency.fullName}">
						<span style='color: red;'><c:out value="budget agency not found" /> </span>
					</c:when>
					<c:otherwise>
						<c:out value="${KualiForm.document.budget.budgetAgency.fullName}" />
					</c:otherwise>
				 </c:choose>                        
              </c:if>
           </div>
    	
    </td>
  </tr>
  
  <tr>
    <th scope="row" ><div align="right">${budgetAttributes.budgetProgramAnnouncementNumber.label}:</div></th>
    <td><kul:htmlControlAttribute property="document.budget.budgetProgramAnnouncementNumber" attributeEntry="${budgetAttributes.budgetProgramAnnouncementNumber}" readOnly="${viewOnly}" tabindexOverride="5030" /></td>
    <th scope="row" ><div align="right"><label for="document.budget.federalPassThroughAgencyNumber">${budgetAttributes.federalPassThroughAgency.label}</label>:</div></th>
    <td>
    	<c:choose>
    		<c:when test="${!viewOnly && KualiForm.document.budget.budgetAgency.agencyTypeCode != KFSConstants.AGENCY_TYPE_CODE_FEDERAL}">
    			<!-- <c:if test="${empty KualiForm.document.budget.federalPassThroughAgencyNumber}">&nbsp;</c:if> -->
	     <div id="pDiv">
	       <div id="cDiv">

                <kul:htmlControlAttribute property="document.budget.federalPassThroughAgencyNumber" attributeEntry="${routingFormAttributes.routingFormAgency}" readOnly="${viewOnly}" onblur="onblur_agencyNumber('document.budget.federalPassThroughAgencyNumber','federalPassThroughAgency');addCfp('document.budget.federalPassThroughAgencyNumber');"/>
    			<kul:lookup boClassName="org.kuali.kfs.module.cg.businessobject.Agency" fieldConversions="agencyNumber:document.budget.federalPassThroughAgencyNumber,fullName:document.budget.federalPassThroughAgency.fullName" tabindexOverride="5110" anchor="General" />
	          <div id="document.budget.federalPassThroughAgency.fullName.div" >
	             <c:if test="${!empty KualiForm.document.budget.federalPassThroughAgencyNumber}">
	                 <c:choose>
						<c:when test="${empty KualiForm.document.budget.federalPassThroughAgency.fullName}">
							<span style='color: red;'><c:out value="federal pass through agency not found" /> </span>
						</c:when>
						<c:otherwise>
							<c:out value="${KualiForm.document.budget.federalPassThroughAgency.fullName}" />
						</c:otherwise>
					 </c:choose>                        
	              </c:if>
	           </div>
	           <div id="myDiv">
	               <div id="newDiv">
	                     <c:if test="${not empty KualiForm.document.budget.federalPassThroughAgencyNumber}"><html:image src="${ConfigProperties.externalizable.images.url}tinybutton-clearfptagency.jpg" styleClass="tinybutton" property="methodToCall.clearFedPassthrough.anchor${currentTabIndex}" title="clear fed passthrough" alt="clear fed passthrough"/></c:if>
	           	   </div>
	           </div>
	       </div>
	     </div>    
	           
	           
    		</c:when>
    		<c:otherwise>
	     <div id="pDiv">
	       <div id="cDiv">
    			N/A
	       </div>
	     </div>    
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
    <th scope="row" ><div align="right"> <kul:htmlAttributeLabel attributeEntry="${budgetAttributes.budgetTypeCodeText}" skipHelpUrl="true" /></div></th>
    <td>
    	<%-- Grants.gov %>
    	<logic:iterate id="budgetType" name="KualiForm" property="budgetTypeCodes" indexId="i"> 
    		<c:choose>
    			<c:when test="${not viewOnly}">
		    		<html:multibox styleId="selectedBudgetTypesMultiboxFix(${budgetType.budgetTypeCode})" property="selectedBudgetTypesMultiboxFix(${budgetType.budgetTypeCode})" value="${budgetType.budgetTypeCode}"/>
		    		<%-- <html:multibox name="KualiForm" property="document.budget.budgetTypeCodeArray"> ${budgetType.budgetTypeCode} </html:multibox> --> ${budgetType.budgetTypeDescription} <br/>
    			</c:when>
    			<c:otherwise>
    				<c:choose>
    					<c:when test="${fn:contains(KualiForm.document.budget.budgetTypeCodeText, budgetType.budgetTypeCode)}"> Yes </c:when>
    					<c:otherwise> No </c:otherwise>
    				</c:choose>
    				<label for="selectedBudgetTypesMultiboxFix(${budgetType.budgetTypeCode})>${budgetType.budgetTypeDescription}</label> <br/>
    			</c:otherwise>
    		</c:choose>
      	</logic:iterate> 
      	--%>
      	
      <kul:htmlControlAttribute property="document.budget.agencyModularIndicator" attributeEntry="${budgetAttributes.agencyModularIndicator}" readOnly="${viewOnly}" onclick="modularVarianceToggle(); " disabled="${!supportsModular}" tabindexOverride="5070"/><kul:htmlAttributeLabel attributeEntry="${budgetAttributes.agencyModularIndicator}" labelFor="document.budget.agencyModularIndicator" useShortLabel="true" skipHelpUrl="true" noColon="true" />
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
        
