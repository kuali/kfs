<%--
 Copyright 2006 The Kuali Foundation.
 
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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:set var="routingFormAttributes" value="${DataDictionary.KualiRoutingFormDocument.attributes}" />
<c:set var="routingFormAgencyAttributes" value="${DataDictionary.RoutingFormAgency.attributes}" />
<c:set var="cfdaAttributes" value="${DataDictionary.CatalogOfFederalDomesticAssistanceReference.attributes}" />

<kul:tab tabTitle="Agency/Delivery Info" defaultOpen="true" auditCluster="mainPageAuditErrors" tabAuditKey="document.routingFormAgency*">

		<div class="tab-container" align="center">
            <div class="tab-container-error"> </div>
            <div class="h2-container">
              <h2>Agency/Delivery Info</h2>
            </div>
            
            <table cellpadding="0" cellspacing="0" summary="view/edit document overview information">
              <tr>
                <th width="20%" align=right valign=middle>${routingFormAttributes.routingFormAgency.label}:</th>
                <td width="30%">
                  	<html:hidden property="document.routingFormAgencyToBeNamedIndicator" />
			    	<html:hidden property="document.routingFormAgency.agencyNumber" />
			    	<html:hidden write="true" property="document.routingFormAgency.routingFormAgency.fullName"/>
			    	<c:if test="${empty KualiForm.document.routingFormAgency.agencyNumber && !KualiForm.document.routingFormAgencyToBeNamedIndicator}">(select)</c:if>
  			    	<c:if test="${KualiForm.document.routingFormAgencyToBeNamedIndicator}">TO BE NAMED</c:if>
			    	<c:if test="${!viewOnly}">
			    		<kul:lookup boClassName="org.kuali.module.cg.bo.Agency" lookupParameters="document.routingFormAgency.agencyNumber:agencyNumber,document.routingFormAgency.routingFormAgency.fullName:fullName" fieldConversions="agencyNumber:document.routingFormAgency.agencyNumber,fullName:document.routingFormAgency.routingFormAgency.fullName" extraButtonSource="images/buttonsmall_namelater.gif" extraButtonParams="&document.routingFormAgencyToBeNamedIndicator=true" tabindexOverride="5100" anchor="${currentTabIndex}" />
                	</c:if>
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.routingFormDueDate}" skipHelpUrl="true" /></th>
                <td colspan="2" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.routingFormDueDate" attributeEntry="${routingFormAgencyAttributes.routingFormDueDate}" datePicker="true" />
                </td>
              </tr>
              <tr>
                <th width="20%" align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.agencyFederalPassThroughNumber}" skipHelpUrl="true" /></th>

                <td width="30%" align=left valign=middle >
                  	<html:hidden property="document.agencyFederalPassThroughNotAvailableIndicator" />
			    	<html:hidden property="document.agencyFederalPassThroughNumber" /> 
			    	<html:hidden write="true" property="document.federalPassThroughAgency.fullName" /> 
	    			<c:if test="${empty KualiForm.document.agencyFederalPassThroughNumber && !KualiForm.document.agencyFederalPassThroughNotAvailableIndicator}">(select)</c:if>
  			    	<c:if test="${KualiForm.document.agencyFederalPassThroughNotAvailableIndicator}">Unknown</c:if>
	    	    	<c:if test="${!viewOnly}">
	    			    <kul:lookup boClassName="org.kuali.module.cg.bo.Agency" fieldConversions="agencyNumber:document.agencyFederalPassThroughNumber,fullName:document.federalPassThroughAgency.fullName" tabindexOverride="5110" extraButtonSource="images/buttonsmall_namelater.gif" extraButtonParams="&document.agencyFederalPassThroughNotAvailableIndicator=true" anchor="${currentTabIndex}" />
	    			</c:if>
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.routingFormDueDateTypeCode}" skipHelpUrl="true" /></th>

                <td colspan="2" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.routingFormDueDateTypeCode" attributeEntry="${routingFormAgencyAttributes.routingFormDueDateTypeCode}"  />
                </td>

              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.agencyAddressDescription}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.agencyAddressDescription" attributeEntry="${routingFormAgencyAttributes.agencyAddressDescription}"  />
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.agencyShippingInstructionsDescription}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.agencyShippingInstructionsDescription" attributeEntry="${routingFormAgencyAttributes.agencyShippingInstructionsDescription}"  />
                </td>
                <td nowrap >
                	<kul:htmlControlAttribute property="document.grantsGovernmentSubmissionIndicator" attributeEntry="${routingFormAttributes.grantsGovernmentSubmissionIndicator}"  />
                	<kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.grantsGovernmentSubmissionIndicator}" skipHelpUrl="true" labelFor="document.grantsGovernmentSubmissionIndicator" noColon="true" />
                  	<br>
                	<kul:htmlControlAttribute property="document.routingFormAgency.agencyDiskAccompanyIndicator" attributeEntry="${routingFormAgencyAttributes.agencyDiskAccompanyIndicator}"  />
                	<kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.agencyDiskAccompanyIndicator}" skipHelpUrl="true" labelFor="document.routingFormAgency.agencyDiskAccompanyIndicator" noColon="true" />
                  	<br>
                	<kul:htmlControlAttribute property="document.routingFormAgency.agencyElectronicSubmissionIndicator" attributeEntry="${routingFormAgencyAttributes.agencyElectronicSubmissionIndicator}"  />
                	<kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.agencyElectronicSubmissionIndicator}" skipHelpUrl="true" labelFor="document.routingFormAgency.agencyElectronicSubmissionIndicator" noColon="true" />
                  </td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${cfdaAttributes.cfdaNumber}" skipHelpUrl="true" /></th>

                <td align=left valign=middle >
			    	<html:hidden property="document.routingFormCatalogOfFederalDomesticAssistanceNumber" /> 
			    	<html:hidden write="true" property="document.catalogOfFederalDomesticAssistanceReference.cfdaProgramTitleName"/>
			    	<c:if test="${empty KualiForm.document.routingFormCatalogOfFederalDomesticAssistanceNumber}">(select)</c:if>
			    	<c:if test="${!viewOnly}">
			    		<kul:lookup boClassName="org.kuali.module.cg.bo.CatalogOfFederalDomesticAssistanceReference" lookupParameters="document.routingFormCatalogOfFederalDomesticAssistanceNumber:cfdaNumber,document.catalogOfFederalDomesticAssistanceReference.cfdaProgramTitleName:cfdaProgramTitleName" fieldConversions="cfdaNumber:document.routingFormCatalogOfFederalDomesticAssistanceNumber,cfdaProgramTitleName:document.catalogOfFederalDomesticAssistanceReference.cfdaProgramTitleName" tabindexOverride="5100" anchor="${currentTabIndex}" />
                	</c:if>
				</td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.routingFormRequiredCopyNumber}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td colspan="2" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.routingFormRequiredCopyNumber" attributeEntry="${routingFormAgencyAttributes.routingFormRequiredCopyNumber}"  />
                	Submit 2 additional copies plus the number of required by your department and school.
                </td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.routingFormAnnouncementNumber}" skipHelpUrl="true" useShortLabel="true" /></th>

                <td colspan="4" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAnnouncementNumber" attributeEntry="${routingFormAttributes.routingFormAnnouncementNumber}"  />
                </td>
              </tr>
            </table>
          </div>

</kul:tab>
