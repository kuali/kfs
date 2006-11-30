<%--
 Copyright 2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/WEB-INF/tags/kra/routingform/routingFormMainPageAgencyDeliveryInfo.tag,v $
 
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
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>


<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<c:set var="docHeaderAttributes" value="${DataDictionary.DocumentHeader.attributes}" />
<c:set var="routingFormAgencyAttributes" value="${DataDictionary.RoutingFormAgency.attributes}" />
<c:set var="cfdaAttributes" value="${DataDictionary.CatalogOfFederalDomesticAssistanceReference.attributes}" />

<dd:evalNameToMap mapName="DataDictionary.${KualiForm.docTypeName}.attributes" returnVar="documentAttributes"/>

<kul:tab tabTitle="Agency/Delivery Info" defaultOpen="true" >

		<div class="tab-container" align="center">
            <div class="tab-container-error"> </div>
            <div class="h2-container">
              <h2>Agency/Delivery Info</h2>
            </div>
            
            <table cellpadding="0" cellspacing="0" summary="view/edit document overview information">
              <tr>
                <th width="20%" align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.agencyNumber}" skipHelpUrl="true" /></th>
                <td width="30%">
			    	<html:hidden write="true" property="document.routingFormAgency.agencyNumber" /> 
			    	<c:if test="${empty KualiForm.document.routingFormAgency.agencyNumber}">(select)</c:if>
			    	<c:if test="${!viewOnly}">
			    		<kul:lookup boClassName="org.kuali.module.cg.bo.Agency" lookupParameters="document.routingFormAgency.agencyNumber:agencyNumber" fieldConversions="agencyNumber:document.routingFormAgency.agencyNumber" tabindexOverride="5100" anchor="${currentTabIndex}" />
                	</c:if>
                </td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.routingFormDueDate}" skipHelpUrl="true" /></th>
                <td colspan="2" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.routingFormDueDate" attributeEntry="${routingFormAgencyAttributes.routingFormDueDate}" datePicker="true" />
                </td>
              </tr>
              <tr>
                <th width="20%" align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.agencyFederalPassThroughNumber}" skipHelpUrl="true" /></th>

                <td width="30%" align=left valign=middle >
<!-- 
NOTE: NOT WORKING RIGHT STILL, BUT A STEP IN THE RIGHT DIRECTION
 -->                
			    	<html:hidden property="document.agencyFederalPassThroughNumber" /> 
			    	<html:hidden write="true" property="document.routingFormAgency.federalPassThroughAgency.fullName" /> 
	    			<c:if test="${empty KualiForm.document.agencyFederalPassThroughNumber}">(select)</c:if>
	    			<kul:lookup boClassName="org.kuali.module.cg.bo.Agency" fieldConversions="agencyNumber:document.agencyFederalPassThroughNumber" tabindexOverride="5110" anchor="${currentTabIndex}" />

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
                	<kul:htmlControlAttribute property="document.grantsGovernmentSubmissionIndicator" attributeEntry="${documentAttributes.grantsGovernmentSubmissionIndicator}"  />
                	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.grantsGovernmentSubmissionIndicator}" skipHelpUrl="true" labelFor="document.grantsGovernmentSubmissionIndicator" noColon="true" />
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
			    	<html:hidden property="document.catalogOfFederalDomesticAssistanceReference.cfdaNumber" /> 
			    	<html:hidden write="true" property="document.catalogOfFederalDomesticAssistanceReference.cfdaProgramTitleName"/>
			    	<c:if test="${empty KualiForm.document.catalogOfFederalDomesticAssistanceReference.cfdaNumber}">(select)</c:if>
			    	<c:if test="${!viewOnly}">
			    		<kul:lookup boClassName="org.kuali.module.cg.bo.CatalogOfFederalDomesticAssistanceReference" lookupParameters="document.catalogOfFederalDomesticAssistanceReference.cfdaNumber:cfdaNumber,document.catalogOfFederalDomesticAssistanceReference.cfdaProgramTitleName:cfdaProgramTitleName" fieldConversions="cfdaNumber:document.catalogOfFederalDomesticAssistanceReference.cfdaNumber,cfdaProgramTitleName:document.catalogOfFederalDomesticAssistanceReference.cfdaProgramTitleName" tabindexOverride="5100" anchor="${currentTabIndex}" />
                	</c:if>
				</td>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${routingFormAgencyAttributes.routingFormRequiredCopyNumber}" skipHelpUrl="true" useShortLabel="true" /></th>
                <td colspan="2" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAgency.routingFormRequiredCopyNumber" attributeEntry="${routingFormAgencyAttributes.routingFormRequiredCopyNumber}"  />
                	Submit 2 additional copies plus the number of required by your department and school.
                </td>
              </tr>
              <tr>
                <th align=right valign=middle><kul:htmlAttributeLabel attributeEntry="${documentAttributes.routingFormAnnouncementNumber}" skipHelpUrl="true" useShortLabel="true" /></th>

                <td colspan="4" align=left valign=middle >
                	<kul:htmlControlAttribute property="document.routingFormAnnouncementNumber" attributeEntry="${documentAttributes.routingFormAnnouncementNumber}"  />
                </td>
              </tr>
            </table>
          </div>

</kul:tab>
