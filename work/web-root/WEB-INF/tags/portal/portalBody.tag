<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<%@ attribute name="channelTitle" required="true" %>
<%@ attribute name="channelUrl" required="true" %>
<%@ attribute name="selectedTab" required="true" %>

<table width="100%" border="0" cellpadding="0" cellspacing="9" cols="3" summary="need a summary here">
  <tbody>
    <tr>
      <td class="uportal-background-dark" valign="top" colspan="3">
      
        <portal:immutableBar />
              
      </td>
    </tr>
    <tr>
        <c:choose>
          <c:when test='${selectedTab == "portalMainMenuBody"}'>
              <portal:portalMainMenuBody />
          </c:when>
          <c:when test='${selectedTab == "portalAdministrationBody"}'>
              <portal:portalAdministrationBody />
          </c:when>
          <c:when test='${selectedTab == "portalFutureModulesBody"}'>
              <portal:portalFutureModulesBody />
          </c:when>
          
          
          <c:when test='${!empty channelTitle && !empty channelUrl}'>
            <td class="uportal-background-dark" valign="top" colspan="2">
              <c:if test="${!empty param.backdoorId}">
                  <portal:iframePortletContainer channelTitle="${channelTitle}" channelUrl="${channelUrl}?backdoorId=${param.backdoorId}&methodToCall.login.x=1" />
              </c:if>
              <c:if test="${empty param.backdoorId}">
                  <portal:iframePortletContainer channelTitle="${channelTitle}" channelUrl="${channelUrl}" />
              </c:if>
            </td>
          </c:when>
          <c:otherwise>
            <portal:portalMainMenuBody />
          </c:otherwise>
        </c:choose>
    </tr>
  </tbody>
</table>
