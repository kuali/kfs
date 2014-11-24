<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    <kul:superUserActions />
    <kul:panelFooter />
    
    <sys:documentControls
    transactionalDocument="${documentEntry.transactionalDocument}"
    extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
