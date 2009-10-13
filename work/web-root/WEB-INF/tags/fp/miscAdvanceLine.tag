<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<%@ attribute name="itemInProcessProperty" required="true" %>
<%@ attribute name="creatingItemInProcess" required="false" %>
<%@ attribute name="readOnly" required="false" %>

<c:if test="${empty readOnly}">
  <c:set var="readOnly" value="false" />
</c:if>

<c:set var="itemInProcessAttributes" value="${DataDictionary.CashieringItemInProcess.attributes}" />


  
  <tr>
    <c:if test="${!creatingItemInProcess}">
      <td>
        <kul:htmlControlAttribute property="${itemInProcessProperty}.itemIdentifier" attributeEntry="${itemInProcessAttributes.itemIdentifier}" readOnly="true" />
      </td>
    </c:if>
    <td>
      <kul:htmlControlAttribute property="${itemInProcessProperty}.itemAmount" attributeEntry="${itemInProcessAttributes.itemAmount}" readOnly="${readOnly || !creatingItemInProcess}" />
    </td>
    <c:if test="${!creatingItemInProcess}">
      <td><kul:htmlControlAttribute property="${itemInProcessProperty}.itemReducedAmount" attributeEntry="${itemInProcessAttributes.itemReducedAmount}" readOnly="true" /></td>
      <td><bean:write name="KualiForm" property="${itemInProcessProperty}.itemRemainingAmount"/></td>
      <td><kul:htmlControlAttribute property="${itemInProcessProperty}.currentPayment" attributeEntry="${itemInProcessAttributes.currentPayment}" readOnly="${readOnly}" />
    </c:if>
    <td>
      <c:choose>
        <c:when test="${!readOnly && creatingItemInProcess}">
          <kul:dateInput property="${itemInProcessProperty}.itemOpenDate" attributeEntry="${itemInProcessAttributes.itemOpenDate}" />
        </c:when>
        <c:otherwise>
          <kul:htmlControlAttribute property="${itemInProcessProperty}.itemOpenDate" attributeEntry="${itemInProcessAttributes.itemOpenDate}" readOnly="true" />
        </c:otherwise>
      </c:choose>
    </td>
    <td><kul:htmlControlAttribute property="${itemInProcessProperty}.itemDescription" attributeEntry="${itemInProcessAttributes.itemDescription}" readOnly="${readOnly}" /></td>
  </tr>
