<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<jsp:doBody var="bodyContent" />
${kfunc:preserveWhitespace(bodyContent)}
