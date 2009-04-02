<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:page showDocumentInfo="false"
	headerTitle="Barcode Inventory Process" docTitle="" renderMultipart="true"
	transactionalDocument="false" htmlFormAction="uploadBarcodeInventoryFile" errorKey="foo">
	<html:hidden property="batchUpload.batchInputTypeName" />
	<html:hidden property="supressDoneFileCreation"/>	
    <c:set var="batchUploadAttributes" value="${DataDictionary.BatchUpload.attributes}" />

	<strong><h2>	
	  <bean:message key="${KualiForm.titleKey}"/> <a href="${ConfigProperties.externalizable.help.url}BatchFileFormats.pdf" tabindex="${KualiForm.nextArbitrarilyHighIndex}" target="helpWindow"  title="[Help]Upload">
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
          
      
          <h3>Manage current files</h3>
          <table width="100%" summary="" cellpadding="0" cellspacing="0">
            <tr>
              <th width="120">&nbsp;</th>
              <th> <div align="left">Select File</div></th>
              <th width="150"> <div align="center">Actions</div></th>
            </tr>
            <tr>
              <th scope="row"><div align="center">Manage:</div></th>
              <td class="infoline"><span class="fineprint"></span> <label>
                <html:select property="batchUpload.existingFileName">
                   <html:optionsCollection property="fileUserIdentifiers" label="label" value="key"/>
                </html:select>
               </label></td><td class="infoline"><div align="center">
                <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-download.gif" styleClass="globalbuttons" property="methodToCall.download" title="Download Batch File" alt="Download Batch File" onclick="excludeSubmitRestriction=true;"/>
                <html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="globalbuttons" property="methodToCall.delete" title="Delete Batch File" alt="Delete Batch File" />
              </div></td>
            </tr>
          </table>
      </div>
      
      
	</kul:tabTop>	
	<kul:panelFooter />	
</kul:page>
