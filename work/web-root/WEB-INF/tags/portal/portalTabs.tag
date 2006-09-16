<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<%@ attribute name="selectedTab" required="true" %>


<div id="tabs" class="tabposition">
    <ul>
     <%-- Main Menu --%>
     
    <c:if test='${selectedTab == "portalMainMenuBody"}'>
        <li class="red"><a class="red" href="portal.do?selectedTab=portalMainMenuBody">Main Menu</a></li>
    </c:if>
    <c:if test='${selectedTab != "portalMainMenuBody"}'>
        <c:if test="${empty selectedTab}">
            <li class="red"><a class="red" href="portal.do?selectedTab=portalMainMenuBody">Main Menu</a></li>
        </c:if>
        <c:if test="${!empty selectedTab}">
            <li class="green"><a class="green" href="portal.do?selectedTab=portalMainMenuBody">Main Menu</a></li>
        </c:if>
    </c:if>


    <%-- Administration --%>
    <c:if test='${selectedTab == "portalAdministrationBody"}'>
        <li class="red"><a class="red" href="portal.do?selectedTab=portalAdministrationBody">Administration</a></li>
    </c:if>
    <c:if test='${selectedTab != "portalAdministrationBody"}'>
        <li class="green"><a class="green" href="portal.do?selectedTab=portalAdministrationBody">Administration</a></li>
    </c:if>

    <%-- Future Modules --%>
    <c:if test='${selectedTab == "portalFutureModulesBody"}'>
        <li class="red"><a class="red" href="portal.do?selectedTab=portalFutureModulesBody">Future Modules</a></li>
    </c:if>
    <c:if test='${selectedTab != "portalFutureModulesBody"}'>
        <li class="green"><a class="green" href="portal.do?selectedTab=portalFutureModulesBody">Future Modules</a></li>
    </c:if>
    
    
    </ul>
  </div>




