<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ tag description="render the Docuware tag to link to the Docuware image file"%>
	
	
<c:set var="viewDocuware" value="${KualiForm.editingMode['viewImages']}" />
 <c:if test="${viewDocuware}">  

<kul:tab tabTitle="Images File" defaultOpen="false" tabErrorKey="" >
     <div class="tab-container" align="center">
     
		
          
          <h3>Download Image File</h3>
          <table width="100%" summary="" cellpadding="0" cellspacing="0">
          
          <tr>
              <th width="60%">&nbsp;</th>
              <th width="40%"> <div align="left">Actions</div></th>
          </tr>
          
          <tr>
            <th scope="row"><div align="center">Download The Image File For This Document</div></th>
            <td ><div align="left"><a target="_blank" href="${ConfigProperties.application.url}/DocuwareCaller?table=${KualiForm.docuwareTableParam}&idvalue=${KualiForm.document.documentNumber}"><img src="${ConfigProperties.externalizable.images.url}tinybutton-download.gif" alt="Download Image File" /></a></div></td>
	 
          </tr>
          </table>
         
	 </div>	
	</kul:tab>
 </c:if>      
