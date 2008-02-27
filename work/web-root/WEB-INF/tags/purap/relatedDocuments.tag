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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>

<kul:tab tabTitle="View Related Documents" defaultOpen="false" tabErrorKey="${PurapConstants.RELATED_DOCS_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Related Documents</h2>
        </div>
		<br />

		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedRequisitionViews"
			documentTypeLabel="Requisitions" /> 
		
		<purap:relatedPurchaseOrderDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedPurchaseOrderViews"
			documentTypeLabel="Purchase Order" /> 

		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedPaymentRequestViews"
			documentTypeLabel="Payment Request" /> 

		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedCreditMemoViews"
			documentTypeLabel="Credit Memo" /> 

    </div>
</kul:tab>
