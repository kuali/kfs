<%--
 Copyright 2007 The Kuali Foundation
 
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

<kul:page showDocumentInfo="false" headerTitle="Cash Drawer Correction" docTitle="Cash Drawer Correction" htmlFormAction="cashDrawerCorrection" transactionalDocument="false">
    
    <html:hidden property="campusCode" />
    
    <kul:tabTop tabTitle="Cash Drawer Corrections" defaultOpen="true" tabErrorKey="cashDrawerErrors">
      <div class="tab-container" align="center">
          <h3>Cash Drawer for ${KualiForm.cashDrawer.campusCode}</h3>
        <fp:cashDrawerCurrencyCoin cashDrawerProperty="cashDrawer" readOnly="false" showCashDrawerSummary="false" />
      </div>
    </kul:tabTop>
    
    <kul:panelFooter/>
    
    <div id="globalbuttons" class="globalbuttons">
      <html:image property="methodToCall.saveCashDrawer" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_save.gif" alt="save cash drawer corrections" title="save corrections" styleClass="tinybutton" />
      <html:image property="methodToCall.cancelCorrections" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" alt="cancel" title="cancel" styleClass="tinybutton" />
    </div>
</kul:page>
