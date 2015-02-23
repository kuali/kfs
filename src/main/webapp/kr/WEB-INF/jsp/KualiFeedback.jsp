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
<%@ include file="tldHeader.jsp"%>

<%--

    Copyright 2005-2012 The Kuali Foundation

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

<c:set var="textAreaAttributes"
    value="${DataDictionary.AttributeReferenceElements.attributes}" />

<%
Object incident=request.getAttribute("org.kuali.rice.kns.web.struts.pojo.KualiExceptionIncident");
request.setAttribute("test", incident);
%>
<c:set var="documentId" value="${requestScope['documentId']}" />
<c:set var="userEmail" value="${requestScope['userEmail']}" />
<c:set var="headerTitle"><bean:message key="feedback.header.title" /></c:set>

<c:set var="docTitle" value="Feedback Form" />
<kul:page showDocumentInfo="false" headerTitle="${headerTitle}"
    docTitle="${docTitle}" transactionalDocument="false"
    htmlFormAction="kualiFeedbackReport"
    defaultMethodToCall="notify" errorKey="*">

    <div align="center"><font color="blue" size="3"><bean:message key="feedback.welcome" /></font></div>
    <br />
    <div class="topblurb">
    <div align="center">
    <table cellpadding="10" cellspacing="0" border="0" class="container2">
        <tr>
            <td colspan="2" class="infoline">
            <div align="left"><font color="blue"><bean:message key="feedback.instructions" /></font></div>
            </td>
        </tr>
        <tr>
            <td>
            <div align="left" valign="top"><strong><bean:message key="feedback.documentId.label" /></strong></div>
            </td>
            <td align="left" valign="top">
            <div align="left"><input type="text" name="documentId" value="${documentId}" /></div>
            </td>
        </tr>
        <tr>
            <td valign="top" align="left">
            <div align="left"><strong><bean:message key="feedback.componentName.label" /></strong></div>
            </td>
            <td align="left" valign="top"><textarea name='componentName'
                rows='5' cols='50' maxlength='1000'></textarea></td>
        </tr>
        <tr>
            <td valign="top" align="left">
            <div align="left"><strong><bean:message key="feedback.description.label" /></strong></div>
            </td>
            <td align="left" valign="top"><textarea name='description'
                rows='5' cols='100' maxlength='1000'></textarea></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td align="left">
            <div><html:image
                property="methodToCall.submitFeedback"
                src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_submit.gif"
                title="Submit" alt="Submit" styleClass="tinybutton" /> <input
                type="image" name="cancel" value="true" class="tinybutton"
                src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif"
                class="globalbuttons" title="close"
                alt="Close Without Submitting Incident"></div>
            </td>
        </tr>
    </table>
    </div>
    </div>
    <br />
</kul:page>
