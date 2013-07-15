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

<script language="javascript" src="scripts/module/tem/common.js"></script>

<c:set var="fullEntryMode" value="${KualiForm.editingMode['fullEntry']}" scope="request" />
<c:set var="travelerEntryMode" value="${KualiForm.editingMode['travelerEntry']}" scope="request" />
<c:set var="documentTitle" value="${'TravelArrangerDocument'}" />

<kul:documentPage showDocumentInfo="true"
    documentTypeName="TravelArrangerDocument"
    htmlFormAction="temTravelArranger" renderMultipart="true"
    showTabButtons="true">
       
    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
    <tem-arranger:traveler/>
    <tem-arranger:request/>
    
    
    <kul:adHocRecipients />

    <kul:routeLog />
    
    <kul:panelFooter />
    
    <sys:documentControls
    transactionalDocument="${documentEntry.transactionalDocument}"
    extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
