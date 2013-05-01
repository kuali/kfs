<%--
 Copyright 2007-2008 The Kuali Foundation
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="${KualiForm.docTypeName}"
    htmlFormAction="${KualiForm.travelPaymentFormAction}" renderMultipart="false"
    showTabButtons="true">
	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	<tem:travelPayment/>
	<tem:specialHandling/>
	<sys:wireTransfer/>
	<sys:foreignDraft/>
	<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.paymentInformationClose" title="close" alt="close" tabindex="${tabindex}" />
</kul:documentPage>