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
<%@ include file="/jsp/core/tldHeader.jsp"%>
<%@ taglib tagdir="/WEB-INF/tags/purap" prefix="purap"%>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="KualiPaymentRequestDocument"
    htmlFormAction="purapPaymentRequest" renderMultipart="true"
    showTabButtons="true">


    <kul:hiddenDocumentFields excludePostingYear="true" />

    
    <!-- TODO move this to where? -->
    <!-- html:hidden property="document.requisitionIdentifier" / -->

    <kul:documentOverview editingMode="${KualiForm.editingMode}"
        includePostingYear="true"
        postingYearAttributes="${DataDictionary.KualiPaymentRequestDocument.attributes}" />

         
 	<!-- purap:paymentRequestInit documentAttributes="${DataDictionary.KualiPaymentRequestDocument.attributes}"
 		 displayPaymentRequestInitFields="true" / -->
	
	<!--  purap:vendor
        documentAttributes="${DataDictionary.KualiPaymentRequestDocument.attributes}" 
        displayPurchaseOrderFields="false" displayPaymentRequestFields="true"/ -->

	<!--  purap:paymentRequestInvoiceInfo documentAttributes="${DataDictionary.KualiPaymentRequestDocument.attributes}"
 		 displayPaymentRequestInvoiceInfoFields="true" / -->        

    <!-- kul:notes / -->

    <!-- kul:adHocRecipients / -->

    <kul:routeLog />

    <kul:panelFooter />

  	
  	
    <kul:documentControls 
        transactionalDocument="true" 
        extraButtonSource="${extraButtonSource}"
        extraButtonProperty="${extraButtonProperty}"
        extraButtonAlt="${extraButtonAlt}"
        />
       

</kul:documentPage>
