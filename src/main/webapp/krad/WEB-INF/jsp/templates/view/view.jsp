<%--

    Copyright 2005-2014 The Kuali Foundation

    Licensed under the Educational Community License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.opensource.org/licenses/ecl2.php

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="view"	classname="org.kuali.rice.krad.uif.view.View" />

<krad:html view="${view}">
<!-- begin of view render -->
<!----------------------------------- #APPLICATION HEADER --------------------------------------->
<krad:template component="${view.applicationHeader}"/>

<c:if test="${!view.breadcrumbsInApplicationHeader}">
  <krad:template component="${view.breadcrumbs}"/>
</c:if>

  <!----------------------------------- #VIEW HEADER --------------------------------------->
<div id="viewheader_div">
	<krad:template component="${view.header}" />
</div>

<!-- changing any ids here will break navigation slide out functionality -->
<div id="viewlayout_div">
	<!----------------------------------- #VIEW NAVIGATION --------------------------------------->
	<div id="viewnavigation_div">
		<krad:template component="${view.navigation}"
			currentPageId="${view.currentPageId}" />
	</div>

	<krad:template component="${view.errorsField}" />

	<%-- write out view, page id as hidden so the view can be reconstructed if necessary --%>
	<c:if test="${view.renderForm}">
		<form:hidden path="viewId" />

		<%-- all forms will be stored in session, this is the conversation key --%>
		<form:hidden path="formKey" />
		<%-- Based on its value, form elements will be checked for dirtyness --%>
		<form:hidden path="validateDirty" />
	</c:if>

	<!----------------------------------- #VIEW PAGE --------------------------------------->
	<div id="viewpage_div">
		<krad:template component="${view.currentPage}" />

		<c:if test="${view.renderForm}">
			<form:hidden path="pageId" />
			<c:if test="${!empty view.currentPage}">
				<form:hidden id="currentPageTitle" path="view.currentPage.title"/>
			</c:if>
			<form:hidden path="jumpToId" />
			<form:hidden path="jumpToName" />
			<form:hidden path="focusId" />
			<form:hidden path="formHistory.historyParameterString"/>
		</c:if>

		<krad:script value="performJumpTo();" />
		<c:if test="${view.currentPage.autoFocus}">
			<krad:script value="performFocus();" />
		</c:if>
	</div>
</div>

<!----------------------------------- #VIEW FOOTER --------------------------------------->
<div id="viewfooter_div">
	<krad:template component="${view.footer}" />
</div>

<!----------------------------------- #APPLICATION FOOTER --------------------------------------->
<krad:template component="${view.applicationFooter}"/>

</krad:html>
<!-- end of view render -->