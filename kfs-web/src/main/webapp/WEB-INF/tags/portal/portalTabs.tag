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

<%@ attribute name="selectedTab" required="true"%>


<div class="navbar main-navbar navbar-static-top">
	<div class="navbar-inner">
		<div class="container-fluid">
			<ul class="nav">
				<a class="brand" href="${ConfigProperties.application.url}/portal.do">Kuali Financial System</a>
				<%-- Main Menu --%>
				<c:if test='${selectedTab == "main"}'>
					<li class="active">
						<a href="portal.do?selectedTab=main"
							title="Main Menu">Main Menu</a>
					</li>
				</c:if>
				<c:if test='${selectedTab != "main"}'>
					<c:if test="${empty selectedTab}">
						<li class="active">
							<a href="portal.do?selectedTab=main"
								title="Main Menu">Main Menu</a>
						</li>
					</c:if>
					<c:if test="${!empty selectedTab}">
						<li>
							<a href="portal.do?selectedTab=main"
								title="Main Menu">Main Menu</a>
						</li>
					</c:if>
				</c:if>


				<%-- Administration  --%>
				<c:if test='${selectedTab == "maintenance"}'>
					<li class="active">
						<a href="portal.do?selectedTab=maintenance"
							title="Maintenance">Maintenance</a>
					</li>
				</c:if>
				<c:if test='${selectedTab != "maintenance"}'>
					<li>
						<a href="portal.do?selectedTab=maintenance"
							title="Maintenance">Maintenance</a>
					</li>
				</c:if>

				<%-- Additional Administration  --%>
				<c:if test='${selectedTab == "administration"}'>
					<li class="active">
						<a href="portal.do?selectedTab=administration"
							title="Administration">Administration</a>
					</li>
				</c:if>
				<c:if test='${selectedTab != "administration"}'>
					<li>
						<a href="portal.do?selectedTab=administration"
							title="Administration">Administration</a>
					</li>
				</c:if>
			</ul>
		</div>
	</div>
</div>

<div class="navbar subnavbar">
	<div class="navbar-inner">
		<div class="container-fluid">
			<ul class="nav">
				<li class="first user right-nav">
					<div class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<c:set var="invalidUserMsg" value="Invalid username"/>
							<c:choose>
								<c:when test="${empty UserSession.loggedInUserPrincipalName}" >You are not logged in.</c:when>
								<c:otherwise>User: ${UserSession.loggedInUserPrincipalName}
									<c:if test="${UserSession.backdoorInUse}" >
										Impersonating User:${UserSession.principalName}
									</c:if>
									<c:if test="${param.invalidUser}">
										Impersonating User:&nbsp;${invalidUserMsg}
									</c:if>
								</c:otherwise>
							</c:choose>
							&nbsp;
							<span aria-hidden="true" class=" icon-caret-down"></span>
						</a>
						<div class="login-form dropdown-menu">
							<c:choose>
								<c:when test="${empty UserSession.loggedInUserPrincipalName}" >
								</c:when>
								<c:when test="${fn:trim(ConfigProperties.environment) == fn:trim(ConfigProperties.production.environment.code)}" >
									<html:form action="/logout.do" method="post" style="margin:0; display:inline">
										<input name="imageField" type="submit" value="Logout" class="go" title="Click to logout.">
									</html:form>
								</c:when>
								<c:otherwise>
									<c:set var="backboorEnabled" value="<%=org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(org.kuali.rice.kew.api.KewApiConstants.KEW_NAMESPACE, org.kuali.rice.krad.util.KRADConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, org.kuali.rice.kew.api.KewApiConstants.SHOW_BACK_DOOR_LOGIN_IND)%>"/>
									<c:if test="${backboorEnabled}">
										<html:form action="/backdoorlogin.do" method="post" style="margin:0; display:inline">
											<input name="backdoorId" type="text" class="searchbox" size="10" title="Enter your backdoor ID here.">
											<button type="submit" value="Login" class="btn btn-mini" title="Click to login.">Login</button>
											<input name="methodToCall" type="hidden" value="login" />
										</html:form>
									</c:if>
									<html:form action="/backdoorlogin.do" method="post" style="margin:0; display:inline">
										<button name="imageField" type="submit" value="Logout" class="btn btn-mini">Logout</button>
										<input name="methodToCall" type="hidden" value="logout" />
									</html:form>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</li>

				<li class="right-nav">
					<portal:portalLink displayTitle="false" title='Document Search' url='${ConfigProperties.workflow.documentsearch.base.url}'>
						Doc Search
					</portal:portalLink>
				</li>

				<li class="last right-nav">
					<portal:portalLink displayTitle="false" title='Action List' url='${ConfigProperties.kew.url}/ActionList.do'>
						Action List
					</portal:portalLink>
				</li>
			</ul>
		</div>
	</div>
</div>