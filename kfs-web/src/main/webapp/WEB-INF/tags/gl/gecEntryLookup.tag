<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="boClassName" required="true" %>
<%@ attribute name="actionPath" required="true" %>
<%@ attribute name="fieldConversions" required="false" %>
<%@ attribute name="lookupParameters" required="false" %>
<%@ attribute name="hideReturnLink" required="false" %>
<%@ attribute name="tabindexOverride" required="false" %>
<%@ attribute name="image" required="false"%>

<c:set var="imageName" value="${empty image ? 'searchicon.gif' : image}"/>

<c:choose>
    <c:when test="${!empty tabindexOverride}">
        <c:set var="tabindex" value="${tabindexOverride}"/>
    </c:when>
    <c:otherwise>
        <c:set var="tabindex" value="${KualiForm.nextArbitrarilyHighIndex}"/>
    </c:otherwise>
</c:choose>

<c:set var="epMethodToCallAttribute" value="methodToCall.performLookup.(!!${boClassName}!!).(((${fieldConversions}))).((#${lookupParameters}#)).((<${hideReturnLink}>)).(([${actionPath}]))" />
${kfunc:registerEditableProperty(KualiForm, epMethodToCallAttribute)}
<input type="image" tabindex="${tabindex}" name="${epMethodToCallAttribute}" src="${ConfigProperties.kr.externalizable.images.url}${imageName}" alt="search" title="search" border="0" class="tinybutton" valign="middle"/>