<%@ include file="/jsp/core/tldHeader.jsp" %>

<%--
  HACK: CashManagementDocument isn't a transactionalDocument, but its XML file claims that it is,
  which is why this JSP abuses some the standard transactionalDocument tags
--%>
<kul:documentPage showDocumentInfo="true" htmlFormAction="financialCashManagement" documentTypeName="KualiCashManagementDocument" renderMultipart="true" showTabButtons="true">
    <kul:hiddenDocumentFields isTransactionalDocument="false" />

    <kul:documentOverview editingMode="${KualiForm.editingMode}"/>

    <cm:cashManagementOverview editingMode="${KualiForm.editingMode}" />
    <cm:deposits editingMode="${KualiForm.editingMode}" />

    <kul:notes/>
    <kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>
    <kul:routeLog/>
    <kul:panelFooter/>
    <kul:documentControls transactionalDocument="false"/>
</kul:documentPage>