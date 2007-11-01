<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:if test="${KualiForm.orgSalSetClose}">
<kul:page showDocumentInfo="false"
	htmlFormAction="budgetIncumbentSalarySetting" renderMultipart="false"
	showTabButtons="false"
	docTitle="Salary Setting by Incumbent"
    transactionalDocument="false"
	>
</kul:page>
</c:if>

<c:if test="${!KualiForm.orgSalSetClose}">
<kul:page showDocumentInfo="false"
	htmlFormAction="budgetIncumbentSalarySetting" renderMultipart="true"
	showTabButtons="true"
	docTitle="Salary Setting by Incumbent"
    transactionalDocument="false"
	>

<%--
	<kul:hiddenDocumentFields
		isFinancialDocument="false"
		isTransactionalDocument="false" />
--%>
    <c:forEach items="${KualiForm.editingMode}" var="mode">
      <html:hidden property="editingMode(${mode.key})"/>
    </c:forEach>

    <bc:incumbentSalarySetting />
	<kul:panelFooter />

    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${!KualiForm.editingMode['systemViewOnly'] && KualiForm.editingMode['fullEntry']}">
	        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_save.gif" styleClass="globalbuttons" property="methodToCall.save" title="save" alt="save"/>
	    </c:if>
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.close" title="close" alt="close"/>
    </div>

<%-- Need these here to override and initialize vars used by objectinfo.js to BC specific --%>
<SCRIPT type="text/javascript">
  subObjectCodeNameSuffix = ".financialSubObject.financialSubObjectCdshortNm";
  var kualiForm = document.forms['KualiForm'];
  var kualiElements = kualiForm.elements;
</SCRIPT>
</kul:page>
</c:if>
