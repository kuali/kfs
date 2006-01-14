<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<%@ attribute name="url" required="true" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="displayTitle" required="false" %>

<c:if test="${displayTitle}" >
  <a href="portal.do?channelTitle=${title}&channelUrl=${url}">${title}</a>
</c:if>
<c:if test="${! displayTitle}" >
  <a href="portal.do?channelTitle=${title}&channelUrl=${url}"><jsp:doBody/></a>
</c:if>
