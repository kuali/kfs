<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-bean.tld" prefix="bean"%>
<%@ attribute name="cluster" required="false"%>
<%@ attribute name="keyMatch" required="false"%>
<%@ attribute name="isLink" required="true"%>
<%@ attribute name="includesTitle" required="false"%>

<c:if test="${!empty cluster}">
	<div class="error">
		<c:forEach items="${AuditErrors[cluster].auditErrorList}" var="audit" varStatus="status">
			<c:set var="errorText">
				<bean:message key="${audit.messageKey}" arg0="${audit.params[0]}" arg1="${audit.params[1]}" arg2="${audit.params[2]}" arg3="${audit.params[3]}" arg4="${audit.params[4]}"/>
			</c:set>
			<c:if test="${(empty keyMatch) || (audit.errorKey == keyMatch) || (fn:endsWith(keyMatch, '*') && fn:startsWith(audit.errorKey, fn:replace(keyMatch, '*', '')))}">
				<c:if test="${includesTitle && status.index == 0}"><strong>Audit Errors found in this Section:</strong><br/></c:if>
				<c:choose>
					<c:when test="${isLink}">
						<tr>
							<td>&nbsp;</td>
							<td width="94%">${errorText}</td>
							<td width="5%"><div align="center"><html:image src="images/tinybutton-fix.gif" property="methodToCall.${audit.link}.x"/></div></td>
						</tr>
					</c:when>
					<c:otherwise><li>${errorText}</li></c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
	</div>
</c:if>