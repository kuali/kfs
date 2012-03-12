<%--
 Copyright 2005-2008 The Kuali Foundation
 
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
<%@ page import="org.kuali.rice.krad.util.KRADConstants"%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<!-- Make sure it isn't a fabrication -->
<c:if test="${Constants.MAINTENANCE_NEW_ACTION != KualiForm.document.oldMaintainableObject.maintenanceAction}">
  <cams:assetPaymentsLookupLink capitalAssetNumber="${KualiForm.document.oldMaintainableObject.businessObject.capitalAssetNumber}" isTransactionalDocument="false"/> 
</c:if>
