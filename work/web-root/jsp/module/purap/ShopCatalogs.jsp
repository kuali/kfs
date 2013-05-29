<%--
 Copyright 2005-2008 The Kuali Foundation
 
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
<html:html>
<head>
<script>var jsContextPath = "${pageContext.request.contextPath}";</script>

<c:forEach items="${fn:split(ConfigProperties.javascript.files, ',')}" var="javascriptFile">
	<c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
		<script language="JavaScript" type="text/javascript"
				src="${pageContext.request.contextPath}/${javascriptFile}">
		</script>
	</c:if>
</c:forEach>
</head>

<body onload="reload()">
<portal:iframePortletContainer channelTitle="Shop Catalogs" channelUrl="${KualiForm.shopUrl}"/>
</body>
</html:html>
