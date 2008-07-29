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

<kul:tab tabTitle="Payment Information" defaultOpen="true"> 
    <div class="tab-container" align="center">
      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
      	<tr>
            <td colspan="4" class="tab-subhead">Payment Information</td>
		</tr>	
		<tr>
            <td colspan="4"><a class="portal_link" href="lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetPayment&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&capitalAssetNumber=${KualiForm.document.oldMaintainableObject.businessObject.capitalAssetNumber}" target="_blank" title="Payment Information">Payment Information</a></td>
		</tr>
	</table>
	</div>
</kul:tab>
