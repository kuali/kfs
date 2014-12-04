<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
