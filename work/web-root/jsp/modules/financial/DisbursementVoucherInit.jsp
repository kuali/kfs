<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<kul:page headerTitle="Disbursement Voucher Payee Selection" transactionalDocument="false" showDocumentInfo="false" htmlFormAction="disbursementVoucherInit" docTitle="Disbursement Voucher Payee Selection">

	<html:hidden property="hasMultipleAddresses" />

	<dv:dvPayeeInit />
	
	<kul:panelFooter />

	<div align="center">	
	<b>NOTE</b>: If the payee you selected has more than one address on record, you will be redirected to a lookup page,<br/> 
       when you press the "Continue" button below, to select the address you wish to use.
	</div>
	
  <div id="globalbuttons" class="globalbuttons">
    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_continue.gif" styleClass="globalbuttons" property="methodToCall.performContinueAction" title="continue" alt="continue"/>
    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif" styleClass="globalbuttons" property="methodToCall.clearInitFields" title="clear" alt="clear"/>
    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="globalbuttons" property="methodToCall.cancel" title="cancel" alt="cancel"/>
  </div>	
</kul:page>	

