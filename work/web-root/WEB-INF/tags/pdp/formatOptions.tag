<%--
 Copyright 2006-2008 The Kuali Foundation
 
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

<%@ attribute name="paymentGroupAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for disbursement number range."%>

<kul:tab tabTitle="Format Options" defaultOpen="true" tabErrorKey="ranges*">
	<div id="formatOptions" class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="Format Options">
			<tr>
				<td colspan="3" class="subhead">
					Enter a Pay Date and any other selection criteria required
				</td>
			</tr>
			<tr>
			
			 <tr>
            <th align="right" nowrap="nowrap" width="50%">
            <kul:htmlAttributeLabel attributeEntry="${paymentGroupAttributes.paymentDate}" labelFor="paymentDate" />
            <br><font size="1"> Ex. 11/26/2004</font></th>
            <td class="datacell" align="left">
            <kul:dateInput attributeEntry="${paymentGroupAttributes.paymentDate}" property="paymentDate"/></td>
          </tr>
         <tr>
            <th align="right" nowrap="nowrap">Only Disbursements Flagged as Immediate:</th>
            <td class="datacell" align="left"><html:checkbox property="paymentTypes" value="immediate"/></td>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">All Payment Types:</th>
            <td class="datacell" align="left"><html:radio property="paymentTypes" value="all"/></td>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">Only Disbursements with Attachments:</th>
            <td class="datacell" align="left"><html:radio property="paymentTypes" value="pymtAttachment"/></td>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">Only Disbursements with No Attachments:</th>
            <td class="datacell" align="left"><html:radio property="paymentTypes" value="pymtAttachmentFalse"/></td>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">Only Disbursements with Special Handling:</th>
            <td class="datacell" align="left"><html:radio property="paymentTypes" value="pymtSpecialHandling"/></td>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">Only Disbursements with No Special Handling:</th>
            <td class="datacell" align="left"><html:radio property="paymentTypes" value="pymtSpecialHandlingFalse"/></td>
          </tr>

		</table>

		
	</div>
</kul:tab>
    
