<%--

    Copyright 2005-2014 The Kuali Foundation

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
<%@ taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@ taglib uri="http://www.kuali.org/struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-logic-el" prefix="logic-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html-el:html>
<head>
<title>Thread Pool</title>
<style type="text/css">
   .highlightrow {}
   tr.highlightrow:hover, tr.over td { background-color: #66FFFF; }
</style>
<link href="css/screen.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="scripts/en-common.js"></script>
<script language="JavaScript" src="scripts/messagequeue-common.js"></script>
</head>

<body>

<table width="100%" border=0 cellpadding=0 cellspacing=0 class="headercell1">
  <tr>
    <td width="15%"><img src="images/wf-logo.gif" alt="Workflow" width=150 height=21 hspace=5 vspace=5></td>
    <td width="85%"><a href="ThreadPool.do?methodToCall=start">Refresh Page</a></td>
    <td>&nbsp;&nbsp;</td>
  </tr>
</table>

  <table width="100%" border=0 cellspacing=0 cellpadding=0>
  <tr>
        <td width="20" height="20">&nbsp;</td>
  	<td>

      <br>
  	  <jsp:include page="../Messages.jsp"/>
      <br>

Core Pool Size: <c:out value="${ThreadPoolForm.corePoolSize}"/><br>
Maximum Pool Size: <c:out value="${ThreadPoolForm.maximumPoolSize}"/><br>
Pool Size: <c:out value="${ThreadPoolForm.threadPool.poolSize}"/><br>
Active Count: <c:out value="${ThreadPoolForm.threadPool.activeCount}"/><br>
Largest Pool Size: <c:out value="${ThreadPoolForm.threadPool.largestPoolSize}"/><br>
Keep Alive Time: <c:out value="${ThreadPoolForm.threadPool.keepAliveTime}"/><br>
Task Count: <c:out value="${ThreadPoolForm.threadPool.taskCount}"/><br>
Completed Task Count: <c:out value="${ThreadPoolForm.threadPool.completedTaskCount}"/><br>
RouteQueue.TimeIncrement: <c:out value="${ThreadPoolForm.timeIncrement}"/><br>
RouteQueue.maxRetryAttempts: <c:out value="${ThreadPoolForm.maxRetryAttempts}"/><br>
<br>
</td>
</tr>
  <tr>
        <td width="20" height="20">&nbsp;</td>
  </tr>

</table>
<br>
<jsp:include page="../Footer.jsp"/>

</body>
</html-el:html>
