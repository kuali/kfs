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

<kul:page showDocumentInfo="false"
	headerTitle="Electronic Invoice Test File Generation" docTitle="" renderMultipart="true"
	transactionalDocument="false" htmlFormAction="purapElectronicInvoiceTestFileGeneration" errorKey="foo">
	
	<strong><h2>	
	  Electronic Invoice Test File Generation
	</h2></strong>
	</br>
	
	<table width="100%" border="0"><tr><td>	
	  <kul:errors keyMatch="*" errorTitle="Errors Found In File:"/>
	</td></tr></table>  
	</br>
		
	<kul:tabTop tabTitle="Manage Batch Files" defaultOpen="true" tabErrorKey="">
      <div class="tab-container" align="center">
          <h3>Generate Test File</h3>
          <table width="100%" summary="" cellpadding="0" cellspacing="0">
            <tr>
              <th width="300">&nbsp;</th>
              <th> <div align="left"><label for="poDocNumber">Purchase Order Document Number</label></div></th>
              <th width="150"> <div align="center">Actions</div></th>
            </tr>
            
            <tr>
              <th scope="row"><div align="center">Generate eInvoice test file for PO:</div></th>
              <td class="infoline"><input type="text" name="poDocNumber" size="10" />
				<span class="fineprint"></span> 
			  </td>
              <td class="infoline"><div align="center">
				<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-generate.gif" styleClass="globalbuttons" property="methodToCall.generate" title="Generate Test File" alt="Generate Test File" />
              </td>
            </tr>
         </table>
      </div>
	</kul:tabTop>
	
	<kul:panelFooter />		
</kul:page>
