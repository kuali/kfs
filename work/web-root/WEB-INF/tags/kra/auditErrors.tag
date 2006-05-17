<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-bean.tld" prefix="bean"%>
<%@ attribute name="cluster" required="false"%>
<%@ attribute name="keyMatch" required="false"%>
<%@ attribute name="isLink" required="true"%>

<c:if test="${!empty cluster}">
	<c:set var="auditList" value="${AuditErrors[cluster]}"/>
	<c:forEach items="${auditList}" var="audit">
		<c:set var="errorText"><bean:message key="${audit.key}"/></c:set>
		<c:if test="${(empty keyMatch) || (audit.key == keyMatch) || (fn:endsWith(keyMatch, '*') && fn:startsWith(audit.key, fn:replace(keyMatch, '*', '')))}">
			<c:choose>
				<c:when test="${isLink}">
					<html:submit value="${errorText}" property="methodToCall.${audit.link}.x" styleClass="${''}" />
				</c:when>
				<c:otherwise>
					${errorText}
				</c:otherwise>
			</c:choose>
		</c:if>
	</c:forEach>
</c:if>
