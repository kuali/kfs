<%@ include file="/jsp/core/tldHeader.jsp" %>


   <html:hidden property="document.documentHeader.versionNumber" />
   <html:hidden property="document.documentHeader.financialDocumentNumber" />
   <html:hidden property="document.documentHeader.financialDocumentStatusCode" />

    <html:hidden property="auditActivated" />
    
    <kul:hiddenDocumentFields isFinancialDocument="false" isTransactionalDocument="false"/>
    