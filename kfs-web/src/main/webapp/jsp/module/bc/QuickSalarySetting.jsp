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

<c:set var="readOnly" value="${KualiForm.viewOnlyEntry || KualiForm.salarySettingClosed}" />

<kul:page showDocumentInfo="false" docTitle="Quick Salary Setting" transactionalDocument="false"
	htmlFormAction="budgetQuickSalarySetting" renderMultipart="true" showTabButtons="true">
	
<%--
    <c:forEach items="${KualiForm.documentActions}" var="action">
      <html:hidden property="documentActions(${action.key})"/>
    </c:forEach>
    
	<c:forEach items="${KualiForm.editingMode}" var="mode">
  		<html:hidden property="editingMode(${mode.key})"/>
	</c:forEach>
--%>
    <html:hidden property="mainWindow" />
    
	<kul:tabTop tabTitle="Quick Salary Setting" defaultOpen="true" tabErrorKey="${BCConstants.ErrorKey.QUICK_SALARY_SETTING_TAB_ERRORS}">
		<div class="tab-container" align=center>
			<bc:expenditureSalaryLine readOnly="${readOnly}"/>	
			
			<br/>
						
			<bc:expenditureSalaryLineDetails readOnly="${readOnly}"/>
		</div>
	</kul:tabTop>

	<kul:panelFooter />

    <%-- add another copy of the errors since this screen can get long --%>
    <kul:errors keyMatch="${Constants.GLOBAL_ERRORS}" errorTitle=" " />
    <kul:errors keyMatch="${BCConstants.ErrorKey.RETURNED_DETAIL_SALARY_SETTING_TAB_ERRORS}" errorTitle=" " />
    
    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${not readOnly}">
	        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_save.gif" 
	        	styleClass="globalbuttons" property="methodToCall.save" title="save" alt="save"/>
	    </c:if>    	
	        	
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" 
       		styleClass="globalbuttons" property="methodToCall.close" title="close" alt="close"/>	

    </div>
</kul:page>
