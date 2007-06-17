<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<br>
<c:if test='${backdoor == "Y"}'>
<center class="bodytext">
<font color="green"><b>Backdoor Id is enabled. Id is <c:out value="${user.networkId}"/>.</b></font>
</center>
</c:if>