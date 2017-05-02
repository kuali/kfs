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

<div id="tabs" class="tabposition">
	<ul>
		<%-- Main Menu --%>
		<c:if test='${selectedTab == "main"}'>
			<li class="red">
				<a class="red" href="portal.do?selectedTab=main"
					title="Main Menu">Main Menu</a>
			</li>
		</c:if>
		<c:if test='${selectedTab != "main"}'>
			<c:if test="${empty selectedTab}">
				<li class="red">
					<a class="red" href="portal.do?selectedTab=main"
						title="Main Menu">Main Menu</a>
				</li>
			</c:if>
			<c:if test="${!empty selectedTab}">
				<li class="green">
					<a class="green" href="portal.do?selectedTab=main"
						title="Main Menu">Main Menu</a>
				</li>
			</c:if>
		</c:if>

		<%-- Central Admin  --%>
		<c:if test='${selectedTab == "centralAdmin"}'>
			<li class="red">
				<a class="red" href="portal.do?selectedTab=centralAdmin"
					title="Central Admin">Central Admin</a>
			</li>
		</c:if>
		<c:if test='${selectedTab != "centralAdmin"}'>
			<li class="green">
				<a class="green"
					href="portal.do?selectedTab=centralAdmin"
					title="Central Admin">Central Admin</a>
			</li>
		</c:if>
		
		<%-- Administration  --%>
		<c:if test='${selectedTab == "maintenance"}'>
			<li class="red">
				<a class="red" href="portal.do?selectedTab=maintenance"
					title="Maintenance">Maintenance</a>
			</li>
		</c:if>
		<c:if test='${selectedTab != "maintenance"}'>
			<li class="green">
				<a class="green"
					href="portal.do?selectedTab=maintenance"
					title="Maintenance">Maintenance</a>
			</li>
		</c:if>

		<%-- Additional Administration  --%>
		<c:if test='${selectedTab == "administration"}'>
			<li class="red">
				<a class="red" href="portal.do?selectedTab=administration"
					title="Administration">Administration</a>
			</li>
		</c:if>
		<c:if test='${selectedTab != "administration"}'>
			<li class="green">
				<a class="green"
					href="portal.do?selectedTab=administration"
					title="Administration">Administration</a>
			</li>
		</c:if>
	</ul>
</div>
