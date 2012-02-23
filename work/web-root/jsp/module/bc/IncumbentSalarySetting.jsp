<%--
 Copyright 2007 The Kuali Foundation
 
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
<%@ page import="org.kuali.kfs.sys.context.SpringContext"%>
<%@ page import="org.kuali.kfs.coa.service.AccountService"%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="readOnly" value="${KualiForm.viewOnlyEntry || KualiForm.salarySettingClosed}" />
<c:set var="accountsCanCrossCharts"
	value="<%=SpringContext.getBean(AccountService.class).accountsCanCrossCharts()%>" />


<kul:page showDocumentInfo="false" htmlFormAction="budgetIncumbentSalarySetting" renderMultipart="true"
	showTabButtons="true" docTitle="${KualiForm.documentTitle}" transactionalDocument="false">

    <html:hidden property="mainWindow" />

    <bc:incumbentSalarySetting readOnly="${readOnly}" accountsCanCrossCharts="${accountsCanCrossCharts}"/>	
    
	<kul:panelFooter />

    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${not readOnly}">
	        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_save.gif" 
	        	styleClass="globalbuttons" property="methodToCall.save" title="save" alt="save"/>
        </c:if>
    	    
	    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" 
       		styleClass="globalbuttons" property="methodToCall.close" title="close" alt="close"/>
    </div>

	<%-- Need these here to override and initialize vars used by objectinfo.js to BC specific --%>
	<SCRIPT type="text/javascript">
	  subObjectCodeNameSuffix = ".financialSubObject.financialSubObjectCdshortNm";
	  var kualiForm = document.forms['KualiForm'];
	  var kualiElements = kualiForm.elements;
	</SCRIPT>
</kul:page>
