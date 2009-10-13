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

<c:set var="itemInProcessAttributes" value="${DataDictionary.CashieringItemInProcess.attributes}" />
<tr>
  <c:if test="${!creatingItemInProcess}"><td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemIdentifier" attributeEntry="${itemInProcessAttributes.itemIdentifier}" /></td></c:if>
  <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemAmount" attributeEntry="${itemInProcessAttributes.itemAmount}" /></td>
  <c:if test="${!creatingItemInProcess}">
    <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemReducedAmount" attributeEntry="${itemInProcessAttributes.itemReducedAmount}" /></td>
    <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemRemainingAmount" attributeEntry="${itemInProcessAttributes.itemRemainingAmount}" /></td>
    <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.currentPayment" attributeEntry="${itemInProcessAttributes.currentPayment}" /></td>
  </c:if>
  <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemOpenDate" attributeEntry="${itemInProcessAttributes.itemOpenDate}" /></td>
  <td><kul:htmlAttributeLabel labelFor="${itemInProcessProperty}.itemDescription" attributeEntry="${itemInProcessAttributes.itemDescription}" /></td>
</tr>
