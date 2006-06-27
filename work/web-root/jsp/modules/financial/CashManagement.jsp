<%@ include file="/jsp/core/tldHeader.jsp"%>

<%--
  HACK: CashManagementDocument isn't a transactionalDocument, but its XML file claims that it is,
  which is why this JSP abuses some of the standard transactionalDocument tags
--%>
<c:set var="allowAdditionalDeposits" value="${KualiForm.editingMode['allowAdditionalDeposits']}" />
<c:set var="showDeposits" value="${allowAdditionalDeposits || (!empty KualiForm.document.deposits)}" />

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialCashManagement" documentTypeName="KualiCashManagementDocument" renderMultipart="true" showTabButtons="true">
    <kul:hiddenDocumentFields isTransactionalDocument="false"/>
    
    <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
    
    <cm:cashDrawerActivity/>
    
    <c:if test="${!showDeposits}">
        <kul:hiddenTab forceOpen="true" />
    </c:if>
    <c:if test="${showDeposits}">
        <cm:deposits editingMode="${KualiForm.editingMode}"/>
    </c:if>

    <c:if test="${KualiForm.document.bankCashOffsetEnabled}" >
        <kul:generalLedgerPendingEntries />
    </c:if>
    <kul:notes/>
    <kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>
    <kul:routeLog/>
    <kul:panelFooter/>
    
    <kul:documentControls transactionalDocument="false"/>
</kul:documentPage>
