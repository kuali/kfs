<%-- 
 Copyright 2006 The Kuali Foundation.
 
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

<%@ attribute name="itemInProcess" required="true" description="An item in process to provide an inquirable link to." type="org.kuali.module.financial.bo.CashieringItemInProcess" %>

<p>
  <kul:inquiry boClassName="org.kuali.module.financial.bo.CashieringItemInProcess" keyValues="workgroupName=${itemInProcess.workgroupName}&itemIdentifier=${itemInProcess.itemIdentifier}" render="true">
    #${itemInProcess.itemIdentifier}
  </kul:inquiry>
</p>