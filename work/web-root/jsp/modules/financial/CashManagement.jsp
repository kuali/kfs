<%@ include file="/jsp/core/tldHeader.jsp" %>

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialCashManagement" documentTypeName="KualiCashManagementDocument" renderMultipart="true" showTabButtons="true">

        <kul:hiddenDocumentFields isFinancialTransactionDocument="false" />
        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>

        <cm:cashManagementOverview editingMode="${KualiForm.editingMode}" />
        <cm:deposits editingMode="${KualiForm.editingMode}" />

        <kul:notes editingMode="${KualiForm.editingMode}"/>
        <kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>
        <kul:routeLog/>
        <kul:panelFooter/>
        <kul:documentControls transactionalDocument="false"/>

</kul:documentPage>
