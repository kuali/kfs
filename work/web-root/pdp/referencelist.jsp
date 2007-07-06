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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<app:getReference name="Bank"/>
<app:getReference name="DisbursementType"/>
<app:getReference name="AccountingChange"/>
<app:getReference name="PaymentChange"/>
<app:getReference name="PaymentStatus"/>
<app:getReference name="RestrictedObjectCode"/>
<app:getReference name="TransactionType"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<link rel="stylesheet" type="text/css"  href="<%= request.getContextPath() %>/pdp/css/pdp_styles.css">
<head><title>Reference List</title></head>
<body>
<h1><strong>Reference List</strong></h1>
<jsp:include page="TestEnvironmentWarning.jsp" flush="true"/>
<table>
  <tr>
    <th>Type</th>
    <th>Code</th>
    <th>Description</th>
    <th>Last Update Date</th>
    <th>Last Update User</th>
  </tr>
  <c:forEach var="item" items="${AccountingChangeList}">
  <tr>
    <td>AccountingChange</td>
    <td><c:out value="${item.code}"/></td>
    <td><c:out value="${item.description}"/></td>
    <td><fmt:formatDate value="${item.lastUpdate}" dateStyle="full" /></td>
    <td><c:out value="${item.lastUpdateUser.fullName}"/></td>
  </tr>
  </c:forEach>
  <c:forEach var="item" items="${DisbursementTypeList}">
  <tr>
    <td>DisbursementType</td>
    <td><c:out value="${item.code}"/></td>
    <td><c:out value="${item.description}"/></td>
    <td><fmt:formatDate value="${item.lastUpdate}" dateStyle="full" /></td>
    <td><c:out value="${item.lastUpdateUser.fullName}"/></td>
  </tr>
  </c:forEach>
  <c:forEach var="item" items="${PaymentChangeList}">
  <tr>
    <td>PaymentChange</td>
    <td><c:out value="${item.code}"/></td>
    <td><c:out value="${item.description}"/></td>
    <td><fmt:formatDate value="${item.lastUpdate}" dateStyle="full" /></td>
    <td><c:out value="${item.lastUpdateUser.fullName}"/></td>
  </tr>
  </c:forEach>
  <c:forEach var="item" items="${PaymentStatusList}">
  <tr>
    <td>PaymentStatus</td>
    <td><c:out value="${item.code}"/></td>
    <td><c:out value="${item.description}"/></td>
    <td><fmt:formatDate value="${item.lastUpdate}" dateStyle="full" /></td>
    <td><c:out value="${item.lastUpdateUser.fullName}"/></td>
  </tr>
  </c:forEach>
  <c:forEach var="item" items="${RestrictedObjectCodeList}">
  <tr>
    <td>RestrictedObjectCode</td>
    <td><c:out value="${item.code}"/></td>
    <td><c:out value="${item.description}"/></td>
    <td><fmt:formatDate value="${item.lastUpdate}" dateStyle="full" /></td>
    <td><c:out value="${item.lastUpdateUser.fullName}"/></td>
  </tr>
  </c:forEach>
  <c:forEach var="item" items="${TransactionTypeList}">
  <tr>
    <td>TransactionType</td>
    <td><c:out value="${item.code}"/></td>
    <td><c:out value="${item.description}"/></td>
    <td><fmt:formatDate value="${item.lastUpdate}" dateStyle="full" /></td>
    <td><c:out value="${item.lastUpdateUser.fullName}"/></td>
  </tr>
  </c:forEach>
</table>
<br>
<h1>Banks</h1>
<table>
  <tr>
    <th>Id</th>
    <th>Name</th>
    <th>Active?</th>
    <th>Last Update User</th>
  </tr>
  <c:forEach var="b" items="${BankList}">
  <tr> 
    <td><c:out value="${b.id}"/></td>
    <td><c:out value="${b.name}"/></td>
    <td><c:out value="${b.active}"/></td>
    <td><c:out value="${b.lastUpdateUser.fullName}"/></td>
  </tr>
  </c:forEach>
</table>

<br>
<c:import url="/backdoor.jsp"/>
</body>
</html>
