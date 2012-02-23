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

<kul:page showDocumentInfo="false" htmlFormAction="budgetPositionSalarySetting" renderMultipart="true"
	showTabButtons="false" docTitle="${KualiForm.documentTitle}" transactionalDocument="false">

    <kul:errors keyMatch="${BCConstants.ErrorKey.RETURNED_DETAIL_SALARY_SETTING_TAB_ERRORS}" errorTitle=" " />
    <div id="globalbuttons" class="globalbuttons">
	    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" 
	        onclick="window.close();return true;" property="methodToCall.performLost" title="close the window" alt="close the window"/>		
    </div>
</kul:page>
