<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
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
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
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
