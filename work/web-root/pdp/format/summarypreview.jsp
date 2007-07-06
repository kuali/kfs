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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html>
<head>
  <link rel="stylesheet" type="text/css"  href="<%= request.getContextPath() %>/pdp/css/pdp_styles.css">
  <title>Format Summary</title>
</head>
<body>
  <html:form action="/pdp/formatsummary">
  <h1><strong>Format Summary Review</strong></h1><br>
  <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <br>
        <font color="#800000">
          <html:errors/>&nbsp;
        </font>&nbsp;
        <br>
      </td>
    </tr>
  </table>
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tbody>
      <tr>
        <td width="20">&nbsp;</td>
        <td>
          <table border="0" cellpadding="4" cellspacing="0" width="100%">
            <tbody>
              <tr>
                <td>Enter a Valid Process ID or Click on one below: <b><c:out value="${procId}"/><br><br></td>
                <td>&nbsp;</td>
             </tr>
            </tbody>
          </table>
        </td>
        <td width="20">&nbsp;</td>
      </tr>
      <tr>
        <td width="20">&nbsp;</td>
        <td align="left" width="325">
          <table border="0" cellpadding="0" cellspacing="0" class="bord-r-t">
            <tr>
              <th width="100">Process ID:</th>
              <td width="175"class="datacell">
                <html:text property="processId" maxlength="8" />&nbsp;&nbsp;
              </td>
            </tr>
          </table>
        </td>
        <td align="left"><input type="image" name="btnSearch" src="<%= request.getContextPath().toString() %>/pdp/images/button_search.gif" alt="Search" align="absmiddle"></a></td>
      </tr>
    </tbody>
  </table>
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tbody>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td width="20">&nbsp;</td>
        <td align="center" >
          <table border="0" cellpadding="4" cellspacing="0" width="55%"class="bord-r-t">
            <tr>
              <th>Process ID</th>
              <th>Campus</th>
              <th>Format User</th>
              <th>Format Time</th>
            </tr>
            <c:forEach var="item" items="${recentFormats}">
              <tr>
                <td class="datacell"><a href="<%= request.getContextPath().toString() %>/pdp/formatsummary.do?id=<c:out value="${item.id}"/>"><c:out value="${item.id}"/></a></td>
                <td class="datacell"><c:out value="${item.campus}"/></td>
                <td class="datacell"><c:out value="${item.processUser.networkId}"/></td>
                <td class="datacell" align="right"><fmt:formatDate value="${item.processTimestamp}" pattern="MM/dd/yyyy'  at  'hh:mm a"/></td>
              </tr>
            </c:forEach>
          </table>
        </td>
        <td width="20">&nbsp;</td>
      </tr>
    </tbody>
  </table>
  <p>&nbsp;</p>
  <c:import url="/pdp/backdoor.jsp"/>
</html:form>
</body>
</html:html>
