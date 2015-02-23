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
<title>Configuration Viewer</title>
<style type="text/css">
.highlightrow {
	
}

tr.highlightrow:hover,tr.over td {
	background-color: #66FFFF;
}
</style>
<link href="css/screen.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="scripts/en-common.js"></script>
<script language="JavaScript" src="scripts/messagequeue-common.js"></script>
</head>




<body>
<table width="100%" border=0 cellpadding=0 cellspacing=0
	class="headercell1">
	<tr>
		<td width="15%"><img src="images/wf-logo.gif" alt="Workflow"
			width=150 height=21 hspace=5 vspace=5></td>
		<td width="85%"><a href="ConfigViewer.do?methodToCall=start">Refresh Page</a></td>
		<td>&nbsp;&nbsp;</td>
	</tr>
</table>

<br />
<br />

<table width="100%" border=0 cellspacing=0 cellpadding=0>
	<tr>
		<td width="20" height="20">&nbsp;</td>
		<td>
		<b>Configured Properties:</b> <%-- Table layout of the search results --%>
		<display:table excludedParams="*" class="bord-r-t"
			style="width:100%" cellspacing="0" cellpadding="0"
			name="${ConfigViewerForm.properties}" id="result"
			requestURI="ConfigViewer.do?methodToCall=start" defaultsort="1"
			defaultorder="ascending">
			<display:setProperty name="paging.banner.placement" value="both" />
			<display:setProperty name="paging.banner.all_items_found" value="" />
			<display:setProperty name="export.banner" value="" />
			<display:setProperty name="basic.msg.empty_list">No Configuration Found</display:setProperty>
			<display:column class="datacell" sortable="true"
				title="<div>Config Key</div>">
				<c:out value="${result.key}" />&nbsp;
		    </display:column>
			<display:column class="datacell" sortable="true"
				title="<div>Config Value</div>">
				<c:out value="${result.value}" />&nbsp;
		    </display:column>
		</display:table>
		</td>
		<td width="20" height="20">&nbsp;</td>
	</tr>
</table>
<jsp:include page="../Footer.jsp"/>
</body>
</html-el:html>
