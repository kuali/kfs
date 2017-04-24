<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%-- JSTLConstants magic doesn't work for nested class KRADConstants.DetailTypes, hence the following uglyness: --%>
<c:set var="backdoorEnabled" value="<%=org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(org.kuali.rice.kew.api.KewApiConstants.KEW_NAMESPACE, org.kuali.rice.krad.util.KRADConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, org.kuali.rice.kew.api.KewApiConstants.SHOW_BACK_DOOR_LOGIN_IND)%>"/>
<c:if test="${backdoorEnabled}">
	<c:choose> 
		<c:when test="${empty UserSession.loggedInUserPrincipalName}" > 
			<c:set var="backdoorIdUrl" value=""/> 			
		</c:when> 
		<c:otherwise> 			
			<c:choose>
				<c:when test="${UserSession.backdoorInUse}" >
					<c:set var="backdoorIdUrl" value="backdoorId=${UserSession.principalName}"/> 					
				</c:when>
				<c:otherwise>
					<c:set var="backdoorIdUrl" value="backdoorId=${UserSession.loggedInUserPrincipalName}"/>
				</c:otherwise>
			</c:choose>				 			
		</c:otherwise> 
	</c:choose>
</c:if>

<div class="header2">
  <div class="header2-left-focus">
    <div class="breadcrumb-focus"><a href="asdf.html"> 
    	<portal:portalLink displayTitle="false" title='Action List' url='${ConfigProperties.workflow.url}/ActionList.do${empty backdoorIdUrl ? "" : "?"}${backdoorIdUrl}'>
   		<img src="images-portal/icon-port-actionlist.gif" alt="action list" width="91" height="19" border="0"></portal:portalLink>
    	<portal:portalLink displayTitle="false" title='Document Search' url='${ConfigProperties.workflow.documentsearch.base.url}${empty backdoorIdUrl ? "" : "&"}${backdoorIdUrl}'>
    	<img src="images-portal/icon-port-docsearch.gif" alt="doc search" width="96" height="19" border="0"></portal:portalLink>
    	<a class="portal_link" href="<%=org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString("KFS-SYS", "All", "BI_REPORT_URL")%>" title="UAccess Analytics" target="_BLANK">
		<img src="images-portal/icon-analytics-reports.jpg" alt="analytics/reports" width="131" height="19" border="0"></a>
     </div>
  </div>
</div>
<div id="login-info"> <c:choose> <c:when test="${empty UserSession.loggedInUserPrincipalName}" > <strong>You are not logged in.</strong> </c:when> <c:otherwise> <strong>Logged in User:&nbsp;${UserSession.loggedInUserPrincipalName}</strong> <c:if test="${UserSession.backdoorInUse}" > <strong>&nbsp;&nbsp;Impersonating User:&nbsp;${UserSession.principalName}</strong> </c:if> </c:otherwise> </c:choose></div>

<div id="search">
  <c:choose> 
    <c:when test="${empty UserSession.loggedInUserPrincipalName}" > 
    </c:when> 
    <c:when test="${fn:trim(ConfigProperties.environment) == fn:trim(ConfigProperties.production.environment.code)}" >
	      <html:form action="/logout.do" method="post" style="margin:0; display:inline">
    	    <input name="imageField" type="submit" value="Logout" class="go" title="Click to logout.">
      	  </html:form>
    </c:when>
    <c:otherwise> 
      <c:if test="${backdoorEnabled}">
        <html:form action="/backdoorlogin.do" method="post" style="margin:0; display:inline">
          <input name="backdoorId" type="text" class="searchbox" size="10" title="Enter your backdoor ID here.">
          <input name="imageField" type="submit" value="Login" class="go" title="Click to login.">
          <input name="methodToCall" type="hidden" value="login" />
        </html:form>
      </c:if>
      
      <html:form action="/backdoorlogin.do" method="post" style="margin:0; display:inline">
        <input name="imageField" type="submit" value="Logout" class="go" title="Click to logout.">
        <input name="methodToCall" type="hidden" value="logout" />
      </html:form> 
    </c:otherwise> 
  </c:choose> 
</div>
