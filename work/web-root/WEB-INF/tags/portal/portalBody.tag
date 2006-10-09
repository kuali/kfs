<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<%@ attribute name="channelTitle" required="true" %>
<%@ attribute name="channelUrl" required="true" %>
<%@ attribute name="selectedTab" required="true" %>

      <portal:immutableBar />

 <table width="100%"  cellspacing="0" cellpadding="0" id="iframe_portlet_container_table">
    <tr valign="top" bgcolor="#FFFFFF">
     <td width="15" class="leftback-focus">&nbsp;</td>
        <c:choose>
          <%-- first try to check if they are focusing in --%>
          <c:when test='${!empty channelTitle && !empty channelUrl}'>
            <td class="content" valign="top" colspan="2">
              <c:if test="${!empty param.backdoorId}">
                  <portal:iframePortletContainer channelTitle="${channelTitle}" channelUrl="${channelUrl}?backdoorId=${param.backdoorId}&methodToCall.login.x=1" />
              </c:if>
              <c:if test="${empty param.backdoorId}">
                  <portal:iframePortletContainer channelTitle="${channelTitle}" channelUrl="${channelUrl}" />
              </c:if>
            </td>
          </c:when>
          <%-- then default to tab based actions if they are not focusing in --%>
          <c:when test='${selectedTab == "portalMainMenuBody"}'>
              <portal:portalMainMenuBody />
          </c:when>
          <c:when test='${selectedTab == "portalAdministrationBody"}'>
              <portal:portalAdministrationBody />
          </c:when>
          <c:when test='${selectedTab == "portalFutureModulesBody"}'>
              <portal:portalFutureModulesBody />
          </c:when>
          
          <%-- as backup go to the main menu index --%>
          <c:otherwise>
            <portal:portalMainMenuBody />
          </c:otherwise>
        </c:choose>
    </tr>
</table>

 <div class="footerbevel">&nbsp;</div>
  <div id="footer-copyright"> <bean:message key="app.copyright" /></div>


