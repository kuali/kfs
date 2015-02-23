<%--
 Copyright 2005-2008 The Kuali Foundation
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="documentTypeName" required="true" description="The name of the document type this document page is rendering." %>

<%@ attribute name="showDocumentInfo" required="false" description="Boolean value of whether to display the Document Type name and document type help on the page." %>
<%@ attribute name="headerMenuBar" required="false" description="HTML text for menu bar to display at the top of the page." %>
<%@ attribute name="headerTitle" required="false" description="The title of this page which will be displayed in the browser's header bar.  If left blank, docTitle will be used instead." %>
<%@ attribute name="htmlFormAction" required="false" description="The URL that the HTML form rendered on this page will be posted to." %>
<%@ attribute name="renderMultipart" required="false" description="Boolean value of whether the HTML form rendred on this page will be encoded to accept multipart - ie, uploaded attachment - input." %>
<%@ attribute name="showTabButtons" required="false" description="Whether to show the show/hide all tabs buttons." %>
<%@ attribute name="extraTopButtons" required="false" type="java.util.List" %>
<%@ attribute name="headerDispatch" required="false" description="A List of org.kuali.rice.kns.web.ui.ExtraButton objects to display at the top of the page." %>
<%@ attribute name="headerTabActive" required="false" description="The name of the active header tab, if header navigation is used." %>
<%@ attribute name="feedbackKey" required="false" description="application resources key that contains feedback contact address only used when lookup attribute is false" %>
<%@ attribute name="auditCount" required="false" description="The number of audit errors displayed on this page." %>
<%@ attribute name="docTitle" required="false" %>
<%@ attribute name="alternativeHelp" required="false"%>

<%@ variable name-given="documentEntry" scope="NESTED" %>
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}" />
<c:set var="sessionDocument" value="${documentEntry.sessionDocument}" />
<c:set var="additionalScriptFiles" value="${KualiForm.additionalScriptFiles}" />

<c:choose>
	<c:when test="${docTitle != null}">
		<c:set var="documentTitle" value="${docTitle}" />
	</c:when>
	<c:otherwise>
		<c:set var="documentTitle" value="${documentEntry.label}" />
	</c:otherwise>
</c:choose>
<!--  pass documentTypeName into htmlControlAttribute -->
<!-- Do not remove session check here. Since it used by other pages (not MD or TD) -->
<c:if test="${KualiForm.document.sessionDocument || sessionDocument}">
<% request.setAttribute("sessionDoc", Boolean.TRUE); %>
</c:if>
<c:set var="renderRequiredFieldsLabel" value="${(KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]
||KualiForm.documentActions[Constants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS]) && (not KualiForm.suppressAllButtons)}" />
<kul:page docTitle="${documentTitle}" transactionalDocument="${documentEntry.transactionalDocument}"
  headerMenuBar="${headerMenuBar}" showDocumentInfo="${showDocumentInfo}" headerTitle="${headerTitle}" 
  htmlFormAction="${htmlFormAction}" alternativeHelp="${alternativeHelp}" renderMultipart="${renderMultipart}" showTabButtons="${showTabButtons}" 
  extraTopButtons="${extraTopButtons}" headerDispatch="${headerDispatch}" headerTabActive="${headerTabActive}" 
  feedbackKey="${feedbackKey}" auditCount="${auditCount}" additionalScriptFiles="${additionalScriptFiles}" renderRequiredFieldsLabel="${renderRequiredFieldsLabel}">
    <jsp:doBody/>
</kul:page>
