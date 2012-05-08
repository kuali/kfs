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

<c:choose>
	<c:when test="${KualiForm.document.documentHeader.workflowDocument.initiated}">
		<c:set var="url" value="/effortCertificationRecreate.do" />
	</c:when>
	<c:otherwise>
		<c:set var="url" value="/effortCertificationReport.do" />
	</c:otherwise>
</c:choose>
  
<c:redirect url="${url}">
	<c:forEach var="parameter" items="${paramValues}"> 
		<c:forEach var="value" items="${parameter.value}">
			<c:param name="${parameter.key}" value="${value}"/>
		</c:forEach>
	</c:forEach>
</c:redirect>
