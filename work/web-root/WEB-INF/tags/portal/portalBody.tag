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
      <td class="uportal-background-dark" valign="top" colspan="2">
      
        <portal:immutableBar />
              
      </td>
    </tr>
    <tr>
        <c:choose>
          <c:when test='${selectedTab == "edocs"}'>
            <td class="uportal-background-dark" valign="top" width="33%">
              <portal:portalEdocs />
          </c:when>
          <c:when test='${!empty channelTitle && !empty channelUrl}'>
            <td class="uportal-background-dark" valign="top" width="33%" colspan="2">
              <portal:iframePortletContainer channelTitle="${channelTitle}" channelUrl="${channelUrl}" />
            </td>
          </c:when>
          <c:otherwise>
            <td class="uportal-background-dark" valign="top" width="33%">
              <portal:portalEdocs />
          </c:otherwise>
        </c:choose>
    </tr>
  </tbody>
</table>
