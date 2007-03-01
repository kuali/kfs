<%--
 Copyright 2007 The Kuali Foundation.
 
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

<%@ attribute name="begin" required="true" %>
<%@ attribute name="end" required="true" %>

<c:set var="routingFormAttributes" value="${DataDictionary.KualiRoutingFormDocument.attributes}" />
<c:set var="routingFormProjectTypeAttributes" value="${DataDictionary.RoutingFormProjectType.attributes}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>

<table width="100%" cellpadding="0" cellspacing="0" class="nobord">
  <c:forEach items="${KualiForm.document.routingFormProjectTypes}" var="routingFormProjectType" varStatus="status" begin="${begin}" end="${end}">
    <tr>
      <td class="nobord">
        <label>
          <kul:htmlControlAttribute property="document.routingFormProjectTypes[${status.index}].projectTypeSelectedIndicator" attributeEntry="${routingFormProjectTypeAttributes.projectTypeSelectedIndicator}" readOnly="${viewOnly}"/>
          <%-- Need to expicitly reference description because kul:htmlControlAttribute doesn't fine it withing a referenceObject. --%>
          ${routingFormProjectType.projectType.projectTypeDescription}
        </label>
        <c:if test="${routingFormProjectType.projectTypeCode eq KualiForm.systemParametersMap[KraConstants.PROJECT_TYPE_OTHER]}">
          &nbsp;<kul:htmlControlAttribute property="document.projectTypeOtherDescription" attributeEntry="${routingFormAttributes.projectTypeOtherDescription}" readOnly="${viewOnly}"/>
        </c:if>
      </td>
    </tr>
  </c:forEach>
</table>
