<%@ include file="/jsp/core/tldHeader.jsp" %>
<kul:documentPage showDocumentInfo="true" htmlFormAction="financialCashManagement" documentTypeName="KualiCashManagementDocument" renderMultipart="true" showTabButtons="true">
        <kul:hiddenDocumentFields isFinancialTransactionDocument="false" />
        <!--  normall the next 3 hidden fields are handled by the hiddenDocumentFields tag -->
        <!--  however, since we set that to false to avoid the transactional specific fields -->
        <!--  the following three don't show up in that tag, but they are needed -->
        <!--  so we have to manually include them in this jsp -->
        <html:hidden property="document.documentHeader.versionNumber" />
    	<html:hidden property="document.documentHeader.financialDocumentNumber" />
	    <c:forEach items="${KualiForm.editingMode}" var="mode">
    	  <html:hidden property="editingMode(${mode.key})"/>
    	</c:forEach>
        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
        <cm:cashManagementOverview editingMode="${KualiForm.editingMode}" />
        <cm:deposits editingMode="${KualiForm.editingMode}" />
        <kul:notes editingMode="${KualiForm.editingMode}"/>
        <kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>
        <kul:routeLog/>
        <kul:panelFooter/>
        <kul:documentControls transactionalDocument="false"/>
</kul:documentPage>