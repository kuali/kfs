<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<div class="header2">
  <div class="header2-left-focus">
    <div class="breadcrumb-focus"><a href="asdf.html"> <portal:portalLink displayTitle="false" title='Action List' url='${ConfigProperties.workflow.base.url}/ActionList.do'>
    <img src="images-portal/icon-port-actionlist.gif" alt="action list" width="91" height="19" border="0"></portal:portalLink> <portal:portalLink displayTitle="false" title='Document Search' url='${ConfigProperties.workflow.base.url}/DocumentSearch.do'>
    <img src="images-portal/icon-port-docsearch.gif" alt="doc search" width="96" height="19" border="0"></portal:portalLink> <portal:portalLink displayTitle="false" title='Workflow Services' url='${ConfigProperties.workflow.base.url}/Portal.do?methodToCall=portal'>
    <img src="images-portal/icon-port-wfservices.gif" alt="workflow services" width="141" height="19" border="0"></portal:portalLink> </div>
  </div>
</div>
<div id="login-info"> <c:choose> <c:when test="${empty UserSession.loggedInUserNetworkId}" > <strong>You are not logged in.</strong> </c:when> <c:otherwise> <strong>Logged in User:&nbsp;${UserSession.loggedInUserNetworkId}</strong> <c:if test="${UserSession.backdoorInUse}" > <strong>&nbsp;&nbsp;Impersonating User:&nbsp;${UserSession.networkId}</strong> </c:if> </c:otherwise> </c:choose></div>

<div id="search">
  <c:choose> 
      <c:when test="${empty UserSession.loggedInUserNetworkId}" > 
      </c:when> 
      <c:when test="${ConfigProperties.environment == 'prd'}" >
      </c:when>
      <c:otherwise> 
      <html:form action="/portal.do" method="post" style="margin:0;">
          <input name="backdoorId" type="text" class="searchbox" size="10" title="Enter your backdoor ID here.">
          <input name="channelUrl" type="hidden" value="${ConfigProperties.workflow.base.url}/Portal.do">
          <input name="channelTitle" type="hidden" value="Workflow Services">
          <input name="imageField" type="submit" value="login" class="go" title="Click to login.">
          </html:form> 
          </c:otherwise> 
          </c:choose> 
</div>