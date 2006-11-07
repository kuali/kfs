<%--
 Copyright 2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/WEB-INF/tags/kra/routingform/routingFormProjectDetailsSubcontracts.tag,v $
 
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
<c:set var="subcontractorAttributes" value="${DataDictionary.RoutingFormSubcontractor.attributes}" />

<dd:evalNameToMap mapName="DataDictionary.${KualiForm.docTypeName}.attributes" returnVar="documentAttributes"/>

<kul:tab tabTitle="Subcontracts" defaultOpen="true" tabErrorKey="${Constants.DOCUMENT_ERRORS}" >

          <div class="tab-container" align="center">
            <div class="h2-container"> <span class="subhead-left">
              <h2>Subcontracts</h2>

              </span> </div>
            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <th width="50">&nbsp;</th>
                <th> <div align="center"><kul:htmlAttributeLabel attributeEntry="${subcontractorAttributes.routingFormSubcontractorName}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></div></th>
                <th><div align="center"><kul:htmlAttributeLabel attributeEntry="${subcontractorAttributes.routingFormSubcontractorAmount}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></div></th>
                <th>Action</th>

              </tr>
              <tr>
                <th scope="row">add:</th>
                <td class="infoline">
                	<div align="center">
			    	<c:if test="${!viewOnly}">
		                <kul:htmlControlAttribute property="newRoutingFormSubcontractor.routingFormSubcontractorName" attributeEntry="${subcontractorAttributes.routingFormSubcontractorName}" />
			    		<kul:lookup boClassName="org.kuali.module.cg.bo.Subcontractor" lookupParameters="newRoutingFormSubcontractor.routingFormSubcontractorName:subcontractorName" fieldConversions="subcontractorName:newRoutingFormSubcontractor.routingFormSubcontractorName" tabindexOverride="5100" anchor="${currentTabIndex}" />
                	</c:if>
                    </div>
                </td>
                <td class="infoline">
                	<div align="right">
				    	<c:if test="${!viewOnly}">
			                <kul:htmlControlAttribute property="newRoutingFormSubcontractor.routingFormSubcontractorAmount" attributeEntry="${subcontractorAttributes.routingFormSubcontractorAmount}" />
	                	</c:if>
                  	</div>
                </td>
                <td class="infoline">
                	<div align=center>
						<html:image property="methodToCall.insertRoutingFormSubcontractor.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add subcontractor"/>
                	</div>
                </td>
              </tr>



              <c:forEach items = "${KualiForm.document.routingFormSubcontractors}" var="routingFormSubcontractor" varStatus="status"  >
				  <htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].routingFormSubcontractorSequenceNumber" attributeEntry="${subcontractorAttributes.routingFormSubcontractorSequenceNumber}" />
				  <htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].routingFormSubcontractorNumber" attributeEntry="${subcontractorAttributes.routingFormSubcontractorNumber}" />
				  <htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].researchDocumentNumber" attributeEntry="${subcontractorAttributes.researchDocumentNumber}"/>
				  <htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].objectId" attributeEntry="${subcontractorAttributes.objectId}" />
				  <htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].versionNumber" attributeEntry="${subcontractorAttributes.versionNumber}"/>


	              <tr>
	                <th scope="row">
	                	<div align="center">
	                		${status.index+1}
	                	</div>
	                </th>
	                <td>
	                	<div align="center">
			                <kul:htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].routingFormSubcontractorName" attributeEntry="${subcontractorAttributes.routingFormSubcontractorName}" />
	                    </div>
	                </td>
	                <td>
	                	<div align="right">
				            <kul:htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].routingFormSubcontractorAmount" attributeEntry="${subcontractorAttributes.routingFormSubcontractorAmount}" />
	                    </div>
	                </td>
	                <td><div align=center>
						<html:image property="methodToCall.deleteRoutingFormSubcontractor.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete subcontractor"/>
	                </div></td>
	              </tr>
			  </c:forEach>



              <tr>
                <td colspan="2" class="total-line"  scope="row">&nbsp;</td>
                <td class="total-line"><strong> Total: $${KualiForm.document.currencyFormattedTotalSubcontractorAmount}</strong><html:hidden write="false" property="document.totalSubcontractorAmount" /></td>
                <td class="total-line">&nbsp;</td>
              </tr>
            </table>
          </div>

</kul:tab>
