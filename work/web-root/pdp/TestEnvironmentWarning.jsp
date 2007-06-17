
<%@ page language="java" %>
<%@ page import="java.sql.Timestamp" %>

<%
  //path = /pur-dev
  String path = request.getContextPath();
  int index = path.indexOf("-");
  String env = path.substring(++index).toUpperCase();
  if (!"PRD".equals(env)) {
%>
  <hr color="#800000" size="5">
    <font color="#800000"><b>**THIS IS NOT PRODUCTION!! - THIS IS A TEST ENVIRONMENT (<%=env%>)**</b></font>
<%--
    <div align="right">
      <%if (session != null) { %>
      <font color="#800000">Session ID:  <%=session.getId()%>&nbsp;&nbsp;&nbsp;<br>
      Session Create Time:  <%=(new Timestamp(session.getCreationTime())).toString()%>&nbsp;&nbsp;&nbsp;</font>
      <%} else { %>
      <font color="#800000">Session ID:  Session is NULL&nbsp;&nbsp;&nbsp;<br>
      Session Create Time:  Session is NULL&nbsp;&nbsp;&nbsp;</font>
      <%} %>
    </div>
--%>
  <hr color="#800000" size="5">
<%}%>
