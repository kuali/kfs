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
