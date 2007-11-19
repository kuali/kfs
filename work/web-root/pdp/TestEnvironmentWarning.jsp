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
<%@ page language="java" %>
<%@ page import="java.sql.Timestamp" %>

<%
  //path = /pur-dev
  String path = request.getContextPath();
  int index = path.indexOf("-");
  String env = path.substring(++index).toUpperCase();
  // This code is taken out of this release.
  if ( 1 == 0 ) {
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
