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

<html>
<head>
<title>Remove/Replace User Document</title>
<link href="<c:out value="${resourcePath}"/>css/screen.css" rel="stylesheet" type="text/css">
<link href="<c:out value="${resourcePath}"/>css/kuali.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<c:out value="${resourcePath}"/>scripts/en-common.js"></script>
<script language="JavaScript" src="<c:out value="${resourcePath}"/>scripts/removereplace.js"></script>
</head>
<body>

<c:set var="ActionForm" value="${RemoveReplaceForm}" scope="request" />

<jsp:include page="RemoveReplaceDisplay.jsp"/>

<jsp:include page="../BackdoorMessage.jsp" flush="true"/>

</body>
</html>
