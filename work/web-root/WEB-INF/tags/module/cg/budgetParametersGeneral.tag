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
	
	<div class="tab-container-error"><div class="left-errmsg-tab">
		<kra:auditErrors cluster="parametersAuditErrors" keyMatch="document.budget.audit.parameters.pd*" isLink="false" includesTitle="true"/>
	</div></div>

    <div class="tab-container" id="G02" style="" align="center">
              
	  <a name="General"></a><h3>General</h3>
	  <table cellpadding="0" cellspacing="0" class="datatable" summary=""> 
	  
	  <tr>
	  	<kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.budgetProjectDirectorUniversalIdentifier}"
			labelFor="document.budget.budgetProjectDirectorUniversalIdentifier" horizontal="true" useShortLabel="false"/>
	    
	    <td>
	      <c:if test="${!viewOnly}">
		      <kul:htmlControlAttribute	attributeEntry="${budgetAttributes.budgetProjectDirectorUniversalIdentifier}"
					property="document.budget.budgetProjectDirectorUniversalIdentifier"/> 	
					
	      	<c:if test="${KualiForm.document.budget.projectDirectorToBeNamedIndicator}">TO BE NAMED</c:if>
		    
		    <kul:lookup boClassName="org.kuali.rice.kim.bo.impl.PersonImpl" fieldConversions="principalId:document.budget.budgetProjectDirectorUniversalIdentifier,name:document.budget.projectDirector.name" tabindexOverride="5000" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_namelater.gif" extraButtonParams="&document.budget.projectDirectorToBeNamedIndicator=true" anchor="General" />
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
	    
	    <kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.budgetPersonnelInflationRate}"
			horizontal="true" labelFor="document.budget.budgetPersonnelInflationRate" useShortLabel="false"/>
			
	    <td  nowrap="nowrap">
	    	<kul:htmlControlAttribute property="document.budget.budgetPersonnelInflationRate" attributeEntry="${budgetAttributes.budgetPersonnelInflationRate}" readOnly="${viewOnly}"  tabindexOverride="5080"  styleClass="amount"/>% (Range: 0 - 11.00)
	    </td>
	  </tr>
	  
	  <tr>
	    <kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.budgetName}" labelFor="document.budget.budgetName" horizontal="true" useShortLabel="false"/>
	    <td><kul:htmlControlAttribute property="document.budget.budgetName" attributeEntry="${budgetAttributes.budgetName}" readOnly="${viewOnly}" tabindexOverride="5010" /></td>
	    
		<kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.budgetNonpersonnelInflationRate}" labelFor="document.budget.budgetNonpersonnelInflationRate" horizontal="true" useShortLabel="false"/>
	    <td  nowrap="nowrap"><kul:htmlControlAttribute property="document.budget.budgetNonpersonnelInflationRate" attributeEntry="${budgetAttributes.budgetNonpersonnelInflationRate}" readOnly="${viewOnly}" tabindexOverride="5090"  styleClass="amount"/> % (Range: 0 - 11.00)</td>
	  </tr>
	  
	  <tr>
	    <kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.budgetProgramAnnouncementName}" labelFor="document.budget.budgetProgramAnnouncementName" horizontal="true" useShortLabel="false"/>
	    <td><kul:htmlControlAttribute property="document.budget.budgetProgramAnnouncementName" attributeEntry="${budgetAttributes.budgetProgramAnnouncementName}" readOnly="${viewOnly}" tabindexOverride="5020" /></td>
	    
	    <kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.budgetAgencyNumber}" labelFor="document.budget.budgetAgencyNumber" horizontal="true" useShortLabel="false" forceRequired="true"/>
	    <td>
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
	    <kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.budgetProgramAnnouncementNumber}" labelFor="document.budget.budgetProgramAnnouncementNumber" horizontal="true" useShortLabel="false"/>
	    <td><kul:htmlControlAttribute property="document.budget.budgetProgramAnnouncementNumber" attributeEntry="${budgetAttributes.budgetProgramAnnouncementNumber}" readOnly="${viewOnly}" tabindexOverride="5030" /></td>
	    
	    <kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.federalPassThroughAgencyNumber}" labelFor="document.budget.federalPassThroughAgencyNumber" horizontal="true" useShortLabel="false"/>
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
	    <kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.electronicResearchAdministrationGrantNumber}" labelFor="document.budget.electronicResearchAdministrationGrantNumber" horizontal="true" useShortLabel="false"/>
	    <td><kul:htmlControlAttribute property="document.budget.electronicResearchAdministrationGrantNumber" attributeEntry="${budgetAttributes.electronicResearchAdministrationGrantNumber}" readOnly="${viewOnly}" tabindexOverride="5040" /></td>
	    
	    <kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.institutionCostShareIndicator}" labelFor="document.budget.institutionCostShareIndicator" horizontal="true" useShortLabel="false"/>
	    <td><kul:htmlControlAttribute property="document.budget.institutionCostShareIndicator" attributeEntry="${budgetAttributes.institutionCostShareIndicator}" readOnly="${viewOnly}" tabindexOverride="5120"/>
	      <label for="document.budget.institutionCostShareIndicator"> include</label></td>
	  </tr>
	  
	  <tr>
	    <kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.budgetTypeCodeText}" labelFor="document.budget.budgetTypeCodeText" horizontal="true" useShortLabel="false"/>  
	    <td>      	
	      <kul:htmlControlAttribute property="document.budget.agencyModularIndicator" attributeEntry="${budgetAttributes.agencyModularIndicator}" readOnly="${viewOnly}" onclick="modularVarianceToggle(); " disabled="${!supportsModular}" tabindexOverride="5070"/><kul:htmlAttributeLabel attributeEntry="${budgetAttributes.agencyModularIndicator}" labelFor="document.budget.agencyModularIndicator" useShortLabel="true" skipHelpUrl="true" noColon="true" />
	    </td>
	    
	    <kul:htmlAttributeHeaderCell attributeEntry="${budgetAttributes.budgetThirdPartyCostShareIndicator}" labelFor="document.budget.budgetThirdPartyCostShareIndicator" horizontal="true" useShortLabel="false" />
	    <td><kul:htmlControlAttribute property="document.budget.budgetThirdPartyCostShareIndicator" attributeEntry="${budgetAttributes.budgetThirdPartyCostShareIndicator}" readOnly="${viewOnly}" tabindexOverride="5130"/>
	      <label for="document.budget.budgetThirdPartyCostShareIndicator"> include</label></td>
	  </tr>
	  
	  </tbody>
	</table>
	<div align="right">*required&nbsp;&nbsp;&nbsp;</div>
</div>
</kul:tab>