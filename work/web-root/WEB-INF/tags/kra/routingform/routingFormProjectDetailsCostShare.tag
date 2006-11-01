<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/WEB-INF/tags/kra/routingform/routingFormProjectDetailsCostShare.tag,v $
 
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
<c:set var="institutionCostShareAttributes" value="${DataDictionary.RoutingFormInstitutionCostShare.attributes}" />
<c:set var="otherCostShareAttributes" value="${DataDictionary.RoutingFormOtherCostShare.attributes}" />

<dd:evalNameToMap mapName="DataDictionary.${KualiForm.docTypeName}.attributes" returnVar="documentAttributes"/>

<kul:tabTop tabTitle="Cost Share" defaultOpen="true" tabErrorKey="${Constants.DOCUMENT_ERRORS}" >

          <div class="tab-container" align="center">
            <div class="tab-container-error"> </div>
            <div class="h2-container">
              <h2>Cost Share</h2>
            </div>

            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <td colspan=7 class="tab-subhead"><span class="left">Institution Cost Share</span> </td>
              </tr>
              <tr>
                <th width="50">&nbsp;</th>
                <th><kul:htmlAttributeLabel attributeEntry="${institutionCostShareAttributes.chartOfAccountsCode}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
                <th><kul:htmlAttributeLabel attributeEntry="${institutionCostShareAttributes.organizationCode}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
                <th><kul:htmlAttributeLabel attributeEntry="${institutionCostShareAttributes.proposalCostShareDescription}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
                <th><kul:htmlAttributeLabel attributeEntry="${institutionCostShareAttributes.accountNumber}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
                <th><kul:htmlAttributeLabel attributeEntry="${institutionCostShareAttributes.proposalCostShareAmount}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
                <th>Action</th>
              </tr>

              <tr>
                <th class="infoline"> <div align="center">
					add:
                </div></td>
                <td class="infoline"> <div align="center">
			    	<c:if test="${!viewOnly}">
		                <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.chartOfAccountsCode" attributeEntry="${institutionCostShareAttributes.chartOfAccountsCode}" />
                	</c:if>
                </div></td>
                <td class="infoline"> <div align="center">
			    	<c:if test="${!viewOnly}">
		                <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.organizationCode" attributeEntry="${institutionCostShareAttributes.organizationCode}" />
			    		<kul:lookup boClassName="org.kuali.module.chart.bo.Org" lookupParameters="newRoutingFormInstitutionCostShare.organizationCode:organizationCode" fieldConversions="organizationCode:newRoutingFormInstitutionCostShare.organizationCode" tabindexOverride="5100" anchor="${currentTabIndex}" />
                	</c:if>
                </div></td>
                <td class="infoline"> <div align="center">
			    	<c:if test="${!viewOnly}">
		                <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.proposalCostShareDescription" attributeEntry="${institutionCostShareAttributes.proposalCostShareDescription}" />
                	</c:if>
                </div></td>
                <td class="infoline"> <div align="center">
			    	<c:if test="${!viewOnly}">
		                <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.accountNumber" attributeEntry="${institutionCostShareAttributes.accountNumber}" />
			    		<kul:lookup boClassName="org.kuali.module.chart.bo.Account" lookupParameters="newRoutingFormInstitutionCostShare.accountNumber:accountNumber" fieldConversions="accountNumber:newRoutingFormInstitutionCostShare.accountNumber" tabindexOverride="5100" anchor="${currentTabIndex}" />
                	</c:if>
                </div></td>
                <td class="infoline"> <div align="right">
			    	<c:if test="${!viewOnly}">
		                <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.proposalCostShareAmount" attributeEntry="${institutionCostShareAttributes.proposalCostShareAmount}" />
                	</c:if>
                </div></td>
                <td class="infoline"><div align="center"><html:image property="methodToCall.insertRoutingFormInstitutionCostShare.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add institution cost share"/></div></td>
              </tr>   
              
              <c:forEach items = "${KualiForm.document.routingFormInstitutionCostShares}" var="routingFormInstitutionCostShare" varStatus="status"  >
				  <htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].proposalCostShareSequenceNumber" attributeEntry="${institutionCostShareAttributes.proposalCostShareSequenceNumber}" />
				  <htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].researchDocumentNumber" attributeEntry="${institutionCostShareAttributes.researchDocumentNumber}"/>
				  <htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].objectId" attributeEntry="${institutionCostShareAttributes.objectId}" />
				  <htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].versionNumber" attributeEntry="${institutionCostShareAttributes.versionNumber}"/>

				  <tr>
	                <th class="neutral"> <div align="center">
		                ${status.index+1}
	                </div></th>
	                <td class="neutral"> <div align="center">
				    	<c:if test="${!viewOnly}">
			                <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].chartOfAccountsCode" attributeEntry="${institutionCostShareAttributes.chartOfAccountsCode}" />
	                	</c:if>
	                </div></td>
	                <td class="neutral"> <div align="center">
				    	<c:if test="${!viewOnly}">
			                <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].organizationCode" attributeEntry="${institutionCostShareAttributes.organizationCode}" />
				    		<kul:lookup boClassName="org.kuali.module.chart.bo.Org" lookupParameters="document.routingFormInstitutionCostShare[${status.index}].organizationCode:organizationCode" fieldConversions="organizationCode:document.routingFormInstitutionCostShare.organizationCode" tabindexOverride="5100" anchor="${currentTabIndex}" />
	                	</c:if>
	                </div></td>
	                <td class="neutral"> <div align="center">
				    	<c:if test="${!viewOnly}">
			                <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].proposalCostShareDescription" attributeEntry="${institutionCostShareAttributes.proposalCostShareDescription}" />
	                	</c:if>
	                </div></td>
	                <td class="neutral"> <div align="center">
				    	<c:if test="${!viewOnly}">
			                <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].accountNumber" attributeEntry="${institutionCostShareAttributes.accountNumber}" />
				    		<kul:lookup boClassName="org.kuali.module.chart.bo.Account" lookupParameters="document.routingFormInstitutionCostShare[${status.index}].accountNumber:accountNumber" fieldConversions="accountNumber:document.routingFormInstitutionCostShare.accountNumber" tabindexOverride="5100" anchor="${currentTabIndex}" />
	                	</c:if>
	                </div></td>
	                <td class="neutral"> <div align="right">
				    	<c:if test="${!viewOnly}">
			                <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].proposalCostShareAmount" attributeEntry="${institutionCostShareAttributes.proposalCostShareAmount}" />
	                	</c:if>
	                </div></td>
	                <td class="neutral"><div align="center"><html:image property="methodToCall.deleteRoutingFormInstitutionCostShare.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete institution cost share"/></div></td>
	              </tr>
              </c:forEach>
              <tr>
                <td colspan="5" class="total-line"  scope="row">&nbsp;</td>
                <td class="total-line">$1000.00</td>
                <td class="total-line">&nbsp;</td>
              </tr>



              <tr>
                <td colspan=7 class="tab-subhead"><span class="left">3rd Party Cost Share</span> </td>
              </tr>
              <tr>
                <th>&nbsp;</th>
                <th colspan="3"><kul:htmlAttributeLabel attributeEntry="${otherCostShareAttributes.proposalCostShareSourceName}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
                <th colspan="2"><kul:htmlAttributeLabel attributeEntry="${otherCostShareAttributes.proposalCostShareAmount}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
                <th >Action</th>
              </tr>
              <tr>
                <th scope="row">add:</th>
                <td colspan="3" class="infoline"><div align="center">
			    	<c:if test="${!viewOnly}">
		                <kul:htmlControlAttribute property="newRoutingFormOtherCostShare.proposalCostShareSourceName" attributeEntry="${otherCostShareAttributes.proposalCostShareSourceName}" />
                	</c:if>
                </div></td>
                <td colspan="2" class="infoline"><div align="right">
			    	<c:if test="${!viewOnly}">
		                <kul:htmlControlAttribute property="newRoutingFormOtherCostShare.proposalCostShareAmount" attributeEntry="${otherCostShareAttributes.proposalCostShareAmount}" />
                	</c:if>
               	</div></td>
                <td class="infoline">
                	<div align=center>
                		<html:image property="methodToCall.insertRoutingFormOtherCostShare.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add 3rd party cost share"/>
                	</div>
                </td>
              </tr>


              <c:forEach items = "${KualiForm.document.routingFormOtherCostShares}" var="routingFormOtherCostShare" varStatus="status"  >
				  <htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].proposalCostShareSequenceNumber" attributeEntry="${otherCostShareAttributes.proposalCostShareSequenceNumber}" />
				  <htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].researchDocumentNumber" attributeEntry="${otherCostShareAttributes.researchDocumentNumber}"/>
				  <htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].objectId" attributeEntry="${otherCostShareAttributes.objectId}" />
				  <htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].versionNumber" attributeEntry="${otherCostShareAttributes.versionNumber}"/>

	              <tr>
	                <th scope="row">
	                	<div align="center">
			                ${status.index+1}
	                	</div>
	                </th>
	                <td colspan="3">
	                	<div align="center">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].proposalCostShareSourceName" attributeEntry="${otherCostShareAttributes.proposalCostShareSourceName}" />
		                	</c:if>
	                	</div>
	                </td>
	                <td colspan="2">
	                	<div align="right">
					    	<c:if test="${!viewOnly}">
				                <kul:htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].proposalCostShareAmount" attributeEntry="${otherCostShareAttributes.proposalCostShareAmount}" />
		                	</c:if>
	                	</div>
	                </td>
	                <td>
	                	<div align=center> 
							<html:image property="methodToCall.deleteRoutingFormOtherCostShare.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete 3rd party cost share"/>
	                	</div>
	                </td>
	              </tr>
              </c:forEach>
              
              <tr>
                <td colspan="4" class="total-line"  scope="row">&nbsp;</td>

                <th colspan="2" class="total-line"  scope="row"><div align="right">$1000.00</div></th>
                <td class="total-line">&nbsp;</td>
              </tr>
            </table>
          </div>

</kul:tabTop>
