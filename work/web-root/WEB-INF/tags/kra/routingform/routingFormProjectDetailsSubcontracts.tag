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
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="docHeaderAttributes" value="${DataDictionary.DocumentHeader.attributes}" />
<c:set var="subcontractorAttributes" value="${DataDictionary.RoutingFormSubcontractor.attributes}" />
<c:set var="cgSubcontractorAttributes" value="${DataDictionary.Subcontractor.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />
<c:set var="budgetLinked" value="${KualiForm.editingMode['budgetLinked']}" />


<kul:tab tabTitle="Subcontracts" defaultOpen="false" tabErrorKey="document.routingFormSubcontractor*" >

  <div class="tab-container" align="center">
    <div class="h2-container"><span class="subhead-left">
      <h2>Subcontracts</h2>
    </span></div>
     
    <table cellpadding=0 cellspacing="0"  summary="">
      <tr>
        <th width="50">&nbsp;</th>
        <th><div align="center"><kul:htmlAttributeLabel attributeEntry="${cgSubcontractorAttributes.subcontractorName}" skipHelpUrl="true" useShortLabel="false" noColon="true" /></div></th>
        <th><div align="center"><kul:htmlAttributeLabel attributeEntry="${subcontractorAttributes.routingFormSubcontractorAmount}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></div></th>
        <c:if test="${not readOnly and not budgetLinked}"><th>Action</th></c:if>
      </tr>

<c:if test="${not readOnly and not budgetLinked}">
      <tr>
        <th scope="row">add:</th>
        <td class="infoline">
          <div align="center">
            <c:if test="${ empty KualiForm.newRoutingFormSubcontractor.subcontractor.subcontractorName }">(select)</c:if>
            <html:hidden property="newRoutingFormSubcontractor.subcontractor.subcontractorName" write="true" />
            <kul:htmlControlAttribute property="newRoutingFormSubcontractor.routingFormSubcontractorNumber" attributeEntry="${subcontractorAttributes.routingFormSubcontractorNumber}" />
            <kul:lookup boClassName="org.kuali.module.cg.bo.Subcontractor" lookupParameters="newRoutingFormSubcontractor.subcontractor.subcontractorName:subcontractorName,newRoutingFormSubcontractor.routingFormSubcontractorNumber:subcontractorNumber" fieldConversions="subcontractorName:newRoutingFormSubcontractor.subcontractor.subcontractorName,subcontractorNumber:newRoutingFormSubcontractor.routingFormSubcontractorNumber" tabindexOverride="5100" anchor="${currentTabIndex}" />
          </div>
        </td>
        <td class="infoline">
          <div align="right">
            <kul:htmlControlAttribute property="newRoutingFormSubcontractor.routingFormSubcontractorAmount" attributeEntry="${subcontractorAttributes.routingFormSubcontractorAmount}" styleClass="amount" />
          </div>
        </td>
        <td class="infoline">
          <div align=center>
            <html:image property="methodToCall.insertRoutingFormSubcontractor.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add subcontractor"/>
          </div>
        </td>
      </tr>
</c:if>
      <c:forEach items = "${KualiForm.document.routingFormSubcontractors}" var="routingFormSubcontractor" varStatus="status"  >
        <tr>
          <th scope="row">
            <div align="center">${status.index+1}</div>
          </th>
          <td>
            <kul:htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].routingFormSubcontractorSequenceNumber" attributeEntry="${subcontractorAttributes.routingFormSubcontractorSequenceNumber}" />
            <kul:htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].routingFormSubcontractorNumber" attributeEntry="${subcontractorAttributes.routingFormSubcontractorNumber}" />
            <kul:htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].documentNumber" attributeEntry="${subcontractorAttributes.documentNumber}"/>
            <kul:htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].objectId" attributeEntry="${subcontractorAttributes.objectId}" />
            <kul:htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].versionNumber" attributeEntry="${subcontractorAttributes.versionNumber}"/>
            <div align="center">
              <c:if test="${ empty routingFormSubcontractor.subcontractor.subcontractorName }">(select)</c:if>
              <html:hidden property="document.routingFormSubcontractor[${status.index}].subcontractor.subcontractorName" write="true" />
              <c:if test="${not readOnly and not budgetLinked}">
                <kul:lookup boClassName="org.kuali.module.cg.bo.Subcontractor" lookupParameters="document.routingFormSubcontractor[${status.index}].subcontractor.subcontractorName:subcontractorName,document.routingFormSubcontractor[${status.index}].routingFormSubcontractorNumber:subcontractorNumber" fieldConversions="subcontractorName:document.routingFormSubcontractor[${status.index}].subcontractor.subcontractorName,subcontractorNumber:document.routingFormSubcontractor[${status.index}].routingFormSubcontractorNumber" tabindexOverride="5100" anchor="${currentTabIndex}" />
              </c:if>
            </div>
          </td>
          <td>
            <div align="right">
              <kul:htmlControlAttribute property="document.routingFormSubcontractor[${status.index}].routingFormSubcontractorAmount" attributeEntry="${subcontractorAttributes.routingFormSubcontractorAmount}" styleClass="amount" readOnly="${readOnly or budgetLinked}" />
            </div>
          </td>
          <c:if test="${not readOnly and not budgetLinked}">
          <td>
            <div align=center>
              <html:image property="methodToCall.deleteRoutingFormSubcontractor.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete subcontractor"/>
            </div>
          </td>
          </c:if>
        </tr>
      </c:forEach>

      <tr>
        <td colspan="2" class="total-line"  scope="row">&nbsp;</td>
        <td class="total-line"><strong> Total: <fmt:formatNumber value="${KualiForm.document.totalSubcontractorAmount}" type="currency" /></strong></td>
        <c:if test="${not readOnly and not budgetLinked}"><td class="total-line">&nbsp;</td></c:if>
      </tr>
    </table>
  </div>
</kul:tab>