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
	headerTitle="Barcode Inventory Process" docTitle="" renderMultipart="true"
	transactionalDocument="false" htmlFormAction="uploadBarcodeInventoryFile" errorKey="foo">
	<html:hidden property="batchUpload.batchInputTypeName" />
    <c:set var="batchUploadAttributes" value="${DataDictionary.BatchUpload.attributes}" />

	<strong><h2>	
	  <bean:message key="${KualiForm.titleKey}"/> <a href="${ConfigProperties.externalizable.help.url}default.htm?turl=WordDocuments%2Fbarcodeinventoryprocess.htm" tabindex="${KualiForm.nextArbitrarilyHighIndex}" target="helpWindow"  title="[Help]Upload">
	                                        <img src="${ConfigProperties.kr.externalizable.images.url}my_cp_inf.gif" alt="[Help]Upload" hspace=5 border=0  align="middle"></a>
	  </h2></strong>
	</br>

	
	<table width="100%" border="0">
		<tr>
			<td>	
	  			<kul:errors keyMatch="*"/>
			</td>
		</tr>
	    <c:forEach items="${KualiForm.messages}" var="message">
			<tr>
				<td>	
				   ${message}
				</td>
			</tr>
		</c:forEach>		
	</table>
	  
	</br>
		
	<kul:tabTop tabTitle="Manage files" defaultOpen="true" tabErrorKey="">
      <div class="tab-container" align="center">
          <h3>Add Batch File</h3>
          <table width="100%" summary="" cellpadding="0" cellspacing="0">
            <tr>
              <th width="180">&nbsp;</th>
              <th> <div align="left">Browse File</div></th>
              <th> <div align="left">File Identifier</div></th>
              <th width="150"> <div align="center">Actions</div></th>
            </tr>
            
            <c:forEach items="${KualiForm.batchInputFileSetType.fileTypes}" var="fileType" varStatus="loopStatus">
              <tr>
                <th scope="row"><div align="right">add <c:out value="${KualiForm.batchInputFileSetType.fileTypeDescription[fileType]}"/>:</div></th>
                
                <td class="infoline"><html:file property="uploadedFiles(${fileType})"/>
                  <span class="fineprint"></span>
                </td>
                <td class="infoline">
                  <c:if test="${loopStatus.first}">
                    <div align="left">
                      <kul:htmlControlAttribute attributeEntry="${batchUploadAttributes.fileUserIdentifer}" property="batchUpload.fileUserIdentifer"/>
                    </div>
                  </c:if>
                  <span class="fineprint">&nbsp;</span>
                </td>
                <td class="infoline"><div align="center">
                  <c:if test="${loopStatus.first}">
                    <html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="globalbuttons" property="methodToCall.save" title="Upload Batch File" alt="Upload Batch File" />
                  </c:if>
                  &nbsp;
                </td>
              </tr>
            </c:forEach>
            
		    <tr>
			    <th>* File Upload Description:</th>
				<td align="left" valign="middle">
					<html:text property="uploadDescription"  size="45" maxlength="40"/>
				</td>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
			</tr>		    
         </table>
          
          <br>
      </div>
      
      
	</kul:tabTop>	
	<kul:panelFooter />	
</kul:page>
