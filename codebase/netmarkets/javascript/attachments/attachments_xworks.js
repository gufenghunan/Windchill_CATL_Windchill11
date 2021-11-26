
   ////////////////////////////////////////////////////////////////////////////////////////////
   // Global Variables                                                                      //
   ///////////////////////////////////////////////////////////////////////////////////////////
   var primaryDragAndDropFileSelectionAppletSource = null;
   var dragAndDropFileSelectionAppletSource = null;
   var DELIM = ";;;qqq";
   var RECORD_DELIM = ";;;zzz";
   var currenFileFieldIds = "";

   var FILE_SELECTION_APPLET_NAME = "fileSelectionAndUploadAppletApplet";
   var FILE_UPLOAD_APPLET_NAME = "fileSelectionAndUploadAppletApplet";
   var SOURCE_COMBO_BOX_ID = "primary0contentSourceList";
   var PRIMARY_FILE_PREFIX = "PRIMARY_FILE";

   var DIRTY = "DIRTY";
   var scmpickerlocation="http://www.ptc.com";

   var ACTION_NAME = null;
   ////////////////////////////////////////////////////////////////////////////////////////////
   // Overrideable Functions                                                             //
   ///////////////////////////////////////////////////////////////////////////////////////////

   // This function is triggered when a file name has been changed in a multi
   // primary input senario.
   function fileNameChanged(fileNameInput) {}

   ////////////////////////////////////////////////////////////////////////////////////////////
   // Secondary Attachment Functions                                                             //
   ///////////////////////////////////////////////////////////////////////////////////////////
   function preAttachmentsStep() {
      if (dragAndDropFileSelectionAppletSource) {
         CreateControl('dragAndDropFileSelectionAppletDiv', dragAndDropFileSelectionAppletSource);
      }
   }

   function setFilePath(filePathElem, filepath) {
       if(filePathElem && filepath) {
          filePathElem.focus();
          filePathElem.value = CheckFilePathSeparators(filepath, getFileSeparator());
          filePathElem.blur();
       }
   }

   function dndSetFilePath(newValue,fileSep,pathComplete) {
      // fileInputFieldName is the textbox for the file input
      var textbox = getFirstInputByName(fileInputFieldName);
      dndPutFilePathsInTextField(newValue,fileSep,pathComplete, textbox);
      textbox.onchange();
   }

   function dndSetFilePathWithFileComponentId(newValue,fileSep,pathComplete, fileComponentId) {

      var textbox = document.getElementById(fileComponentId);
      if(textbox)
      {
          dndPutFilePathsInTextField(newValue,fileSep,pathComplete, textbox);

          textbox.onchange();
      }

   }

   function dndSetNewFilesPaths(newValue,fileSep,pathComplete) {
      dndPutFilePathsInTextField(newValue,fileSep,pathComplete, getFirstInputByName('newFiles'));
   }

   function dndPutFilePathsInTextField(newValue,fileSep,pathComplete, textField) {
       window.focus();
       newValue = CheckFilePathSeparators(newValue,fileSep);
       if(newValue ) {
          textField.value = newValue;
       }
   }

   function getFirstInputByName(name) {
      var inputsArray = document.getElementsByName(name);
      return inputsArray[0];
   }

   function setFileName(filepathElement, filepathString, fileNameString) {
      var filepathElementName = filepathElement.name;
      var filenameElementName = filepathElementName.replace(filepathString, fileNameString);
      filenameElement = document.getElementsByName(filenameElementName)[0];
      if(filenameElement)
        setFileNameElement(filepathElement, filenameElement);
   }

   function setFileNameElement(filepathElement, filenameElement) {
    var filenameValue=new Array(1);
    filenameValue[0]=filenameElement.maxLength;
       if(filepathElement && filenameElement) {
          var fileName = getFileName(filepathElement.value, getFileSeparator());
           if(filenameElement.maxLength > 0 && fileName.length > filenameElement.maxLength){
      JCAAlert("com.ptc.windchill.enterprise.attachments.attachmentsResource.TEXT_BOX_LENGTH_EXCEED_ERROR_MSG", "", filenameValue);
      filepathElement.value="";

            }
           filenameElement.value = fileName;
          if (filenameElement.onchange) {
             filenameElement.onchange();
          }
          //This condition will check the upload file radio button only when Primary Attachment file is changed.
          if((filepathElement.id=="primaryFilepathInput") || (filenameElement.id=="primaryFilenameInput")){
            //ElementID set in PrimaryAttachmentTag when RadioButton is created
            if(document.forms[0].upload_file_from_selected_filepath){
                document.forms[0].upload_file_from_selected_filepath.checked = true;
            }
          }
       }
   }

    // SPR:1492733 setUploadOption method added to set the upload selected option in Edit MultiDocument fire on
    // onChange event of file input textbox.
   function setUploadOption(obj){
        obj = getParentNode(obj,"td");
        var inputTags = obj.getElementsByTagName("input");
        for(i=0;i<inputTags.length;i++) {
            var inputTag = inputTags.item(i);
            if(inputTag.type=="radio" && inputTag.id=="upload_file_from_selected_filepath"){
                inputTag.checked = true;
            }
            if(inputTag.type=="text" && (inputTag.id.indexOf("wt.content.ApplicationData:") != -1)){
                inputTag.className = "contentRequired";
                break;
            }
        }

   }

    function getParentNode(anode, tag) {
        while (anode!=null && anode.tagName != tag && anode.tagName != "tr") {
            anode = anode.parentNode;
            if(!anode.tagName || anode.tagName.toLowerCase()==tag)
                break;
        }
        return anode;
    }

   function setBusinessObjectName(filepathElement, nameFunction, contentSource) {
       if(filepathElement && nameFunction) {
          nameFunction(getFileName(filepathElement.value, getFileSeparator()), contentSource);
       }
   }

   function setBusinessObjectNameForMulti(filepathElement, boNameElementID) {
       if (filepathElement && boNameElementID) {
           var fileName = getFileName(filepathElement.value, getFileSeparator());
           var boNameElement = document.getElementById(boNameElementID);
           if (boNameElement && boNameElement.value == '' && boNameElement.maxLength > 0) {
               var dotIndex = fileName.lastIndexOf('.');
               if ( dotIndex > -1) {
                   fileName = fileName.substring(0, dotIndex);
               }
               if (fileName.length > boNameElement.maxLength) {
                   fileName = fileName.substring(0, boNameElement.maxLength);
               }
               boNameElement.value = fileName;
               PTC.wizard.saveTableData.saveToStore(boNameElement);
           } // end if boNameELement
       } // end if boNameElementID
   }

   function getFileName(filepath, fileSep) {
       var filename = "";

       if(filepath) {
           filepath = CheckFilePathSeparators(filepath, fileSep);
           var indexOfLastSep = filepath.lastIndexOf(fileSep);
           filename = filepath.substring(indexOfLastSep + 1, filepath.length);
       }

       return filename;
   }

   function CheckFilePathSeparators(path,fileSeparator) {
      var JSPath="";
      if (fileSeparator == '\\') {
         JSPath=safeReplaceChar(path,"/","\\");
      } else {
         JSPath=path;
      }
      JSPath=safeReplaceChar(JSPath,">","\'");
      return JSPath;
   }

   function safeReplaceChar(input,search,replacestring) {
      //replace does not work in 6.2 Netscape so this looping method must be used
       var newStr = new String(input);
       var tvLast = 0;
       var result = "";
       for(var i = 0; i < newStr.length; i ++ )
       {
            if ( newStr.charAt(i) == search )
            {
                result += newStr.substr(tvLast,i - tvLast);
                result += replacestring;
                tvLast = i+1;
            }
       }
       if( tvLast < newStr.length ) {
            result+= newStr.substr(tvLast, newStr.length - tvLast );
       }
       return result;
   }

   //Used to display invalid file paths that are being uploaded
   function displayInvalidPaths( invalidFilepaths, nextURL ) {
      alert( JS_MSG_INVALID_PATHS + invalidFilepaths );
      location = nextURL;
   }

   function enableFileNameField(nameElement) {

       if(nameElement) {
          nameElement.disabled=false;
       }
   }

   function submitFileContent () {
      if ( checkContentRequiredFields() ) {
         try {
            var uploadApplet = getAppletByName(FILE_UPLOAD_APPLET_NAME);
            var retVal= true;
            if(uploadApplet) {
               goProgress();
               fileUploadApplet = uploadApplet;
               retVal= submitFileContentUsingApplet();
               if(retVal!=null && !retVal){
                   //Clear the action form data in case of any error
                  clearActionFormData();
               }
            } else {
               goProgress();
                
                // Modify the following function to ensure
                // upload if any is complete
                retVal= submitFileContentUsingHttpUpload();
            }
            if (!retVal) {
               stopProgress();
            }
                        
            return retVal;
         } catch( e) {
            stopProgress();
            alert("An exception occurred in the JS API submitFileContent().\nError name: " + e.name + ".\nError message: " + e.message);
            return false;
         }
      } else {
         return false;
      }
   }

   function submitFileContentUsingHttpUpload() {
       //<!-- viklele start -->
       if(document.getElementById('Upload_Preference')){
           return true;
       } 
       //<!-- viklele end -->
      var mform = getMainFormForAttachments();
     

      var fileElements = getFileElements(mform, false, "cachedContentDescriptor");

      var filePaths = addFilePathsAsElementsToForm(mform, fileElements);

            var fileNameMaxLength=new Array('256');

      if ( filePaths.length > 0 ) {

         var url = "servlet/GetCacheDescriptorServlet";

         var postString = "fileCount=" + filePaths.length + "&contentIdentities=";

         for ( var i = 0; i < filePaths.length; i++ ){
            if ( i > 0 ) {
               postString = postString + RECORD_DELIM;
            }

              var fileName = getFileName(filePaths[i], getFileSeparator());

              if(fileName.length>fileNameMaxLength){
            JCAAlert("com.ptc.windchill.enterprise.attachments.attachmentsResource.TEXT_BOX_LENGTH_EXCEED_ERROR_MSG", "", fileNameMaxLength);
            return false;
              }
//            postString = postString + contentIdentityConversion(filePaths[i]);
            postString = postString + filePaths[i].length;

         }
         var xmlHttp = newHttp();
         xmlHttp.open("POST", url, false);
         xmlHttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
         xmlHttp.send(postString);

         if(xmlHttp && xmlHttp.readyState == 4) {
            var responseText = xmlHttp.responseText;
           try {
               var respData = eval("(" + responseText + ")");

               var baseHrefUrl = "";
               var baseHref = $("basehref");
               if(baseHref) {
                  baseHrefUrl = baseHref.href;
               }
               if (!document.getElementById("SubmissionURL")) {
                   createHiddenAttachmentsField(mform, "SubmissionURL", mform.action, true);
               }

               var masterUrlElem = createHiddenAttachmentsField(mform, "Master_URL", respData.masterUrl, true);
               var ccdArrayElem = createHiddenAttachmentsField(mform, "CacheDescriptor_array", respData.cacheDescriptors, true);

               // Add the Master_URL and CacheDescriptor_array elements to the top of the form so that those values will
               // get received by the content service before the file input streams are received
               var knownWizardFormElem = $("requiredMessage");
               mform.insertBefore(masterUrlElem, knownWizardFormElem);
               mform.insertBefore(ccdArrayElem, knownWizardFormElem);

               mform.action = respData.uploadUrl;
            } catch (e) {
                JCAAlert("com.ptc.windchill.enterprise.attachments.attachmentsResource.DO_APPLET_UPLOAD_ERROR", "[" + responseText+"]");
               return false;
            }

         }

      }
      return true;

   }

   function contentIdentityConversion( stringToConvert ) {
      if ( stringToConvert != null ) {
         stringToConvert = safeReplaceChar(stringToConvert,"\\",">")
         stringToConvert = safeReplaceChar(stringToConvert,"/",">");
         stringToConvert = safeReplaceChar(stringToConvert,":",">");
      }
      return stringToConvert;
   }

   var fileUploadApplet = null;
   var attemptAppletUploadWaitTime = 0;
   var uploaded = false;

   // In mozilla on unix, if a div is hidden it causes the applets on the page to temporarily
   // disappear. This fix essentially creates a wait loop with a sleep to wait to see if the
   // applet is initialized yet before proceeding with the upload.
   function submitFileContentUsingApplet() {
      if (uploaded) {
         return true;
      }
      try {
         var theapplet = fileUploadApplet.getTargetApplet();
         if (theapplet == null) {
            if (attemptAppletUploadWaitTime == 0) {
               attemptAppletUploadWaitTime = 100;
            }
            if (attemptAppletUploadWaitTime < 13000) {
               setTimeout("submitFileContentUsingApplet()",attemptAppletUploadWaitTime);
               attemptAppletUploadWaitTime = attemptAppletUploadWaitTime * 2;
            }
            else {
               JCAAlert("com.ptc.windchill.enterprise.attachments.attachmentsResource.MISSING_APPLET");
            }
            return false;
         }
         else {
             var apltStarted = theapplet.isAppletStarted();
             if(!apltStarted)
             {
                JCAAlert("com.ptc.windchill.enterprise.attachments.attachmentsResource.MISSING_APPLET");
                return false;
             }
             var mform = getMainFormForAttachments();
             var fileElements = getFileElements(mform, false, "filePath");
             var filePaths = getFilePaths(fileElements);

             if(filePaths) {

                var responseText = theapplet.uploadFilesAndReturnCCDs( filePaths );

                if(responseText && responseText != "") {
                  try {
                      var respData = eval("(" + responseText + ")");

                      var ccds = respData.cachedContentDescriptors;
                      // add the cached content descriptors to the form data
                      for(var i=0; ccds && i < ccds.length; i++) {
                         if (ccds[i].cachedContentDescriptor) {
                            var ccdFieldName = ccds[i].cachedContentDescriptor.htmlFileInputFieldName.replace("filePath","cachedContentDescriptor");
                            var ccdField = createHiddenAttachmentsField(mform, ccdFieldName, ccds[i].cachedContentDescriptor.value, true);
                         }
                         else {
                            alert(ccds[i].error);
                            return false;
                         }
                      }
                   } catch (e) {
                      JCAAlert("com.ptc.windchill.enterprise.attachments.attachmentsResource.DO_APPLET_UPLOAD_ERROR", responseText);
                      return false;
                   }
                } else {
                   return false;
                }
             }
          }

          if(attemptAppletUploadWaitTime > 0) {
             uploaded = true;             
             mform.submit();
          }
      } catch (e) {
         JCAAlert("com.ptc.windchill.enterprise.attachments.attachmentsResource.DO_APPLET_UPLOAD_ERROR");
         return false;
      }
      // if everything is fine till this point, return true.
      return true;
   }

   function addFilePathsAsElementsToForm(mform, fileElements) {
      var filePaths = new Array(fileElements.length);
      if(mform && fileElements) {
         for (var i=0; i < fileElements.length; i++) {
            var fileElement = fileElements[i];

            if(fileElement) {
               addFilePathAsElementToForm(mform, fileElement.name, fileElement.value);
               filePaths[i] = fileElement.value;
            }
         }
      }

      return filePaths;
   }

   function addFilePathAsElementToForm(mform, fileInputElemName, fileInputElemValue) {
      if(mform && fileInputElemName) {
         var htmlFileInputFieldName = fileInputElemName.replace("_cachedContentDescriptor", "_filePath");

         createHiddenAttachmentsField(mform, htmlFileInputFieldName, fileInputElemValue, false);
      }
   }

   function createHiddenAttachmentsField(form, fieldName, value, returnElement) {
      var newField;
      if(fieldName && value) {
         newField = document.getElementById(fieldName);
         if (newField == null) {
            var fields = document.getElementsByName(fieldName);
            if (fields != null) {
              newField = fields[0];
            }
         }
         if(newField) {
            newField.setAttribute("value", value.toString()); //2177189 :: when the page is loaded again if preProcess fails , it will set value as [object] , hence toString(). 
         } else {
            newField = document.createElement("input");
            newField.setAttribute("type", "hidden");
            newField.setAttribute("name", fieldName);
            newField.setAttribute("id", fieldName);
            newField.setAttribute("value", value.toString());  //2041756::w/o toString(), a call to getAttribute('value') will return [object] and not 'string'. Hence forcing for string.

            form.appendChild( newField );
         }
      }

      if(returnElement) {
         return newField;
      }
   }

   function browseForFile(targetFieldName) {
    try {
      var currentFieldArray = document.getElementsByName(targetFieldName);
      var currentField = currentFieldArray[0];
      if (currentField) {
         var currentValue = currentField.value;
         if (currentValue == "") {
             var inputDefaultPathArray = document.getElementsByName("prefDefaultPath");
             var inputDefaultPath = inputDefaultPathArray[0];
             if (inputDefaultPath) {
                 currentValue = inputDefaultPath.value;
             }
         } // end if currentValue

         var newValue;
         var fileSelectionApplet = getAppletByName(FILE_SELECTION_APPLET_NAME);
         if ( fileSelectionApplet ) {
             fileSelectionApplet.getTargetApplet().fileLocatorFireAndForget(currentValue, false, "afterBrowseForFile", targetFieldName);
         } // end if fileSelectionApplet
      } // end if currentField
      return true;
    } catch( e ) {
       alert("An exception occurred in the JS API browseForFile().\nError name: " + e.name + ".\nError message: " + e.message);
      }
   }

   function afterBrowseForFile(targetFieldName,newValue) {
      var currentFieldArray = document.getElementsByName(targetFieldName);
      var currentField = currentFieldArray[0];
      if (currentField) {
         if (!newValue || newValue == "" ) {
            return false;
         }

         newValue = CheckFilePathSeparators(newValue, getFileSeparator());
         currentField.value = newValue;
         PTC.wizard.saveTableData.saveToStore(currentField);
         if (currentField.onchange) {
             currentField.onchange();
         }
//         setFileName(currentField);
      } // end if currentField
   }

   function browseForFiles(evt, targetFieldName, actionName) {
        ACTION_NAME = null;
        if (actionName != null) {
            ACTION_NAME = actionName;
        }
    
        try {
            var defaultValue = "";
            var inputDefaultPathArray = document
                    .getElementsByName("prefDefaultPath");
            var inputUpdatedPathArray = document
                    .getElementsByName("updateDefaultPath");
            var inputUpdatedPath = inputUpdatedPathArray[0];
            var inputDefaultPath = inputDefaultPathArray[0];
            if (inputDefaultPath && typeof inputDefaultPath != 'undefined') {
                defaultValue = inputDefaultPath.value;
                if ("" == inputDefaultPath.value) {
                    if (inputUpdatedPath && typeof inputUpdatedPath != 'undefined') {
                        defaultValue = inputUpdatedPath.value;
                    }
                }
    
            }
    
            var fileSelectionApplet = getAppletByName(FILE_SELECTION_APPLET_NAME);
            if (fileSelectionApplet) {
                fileSelectionApplet.getTargetApplet().fileLocatorFireAndForget(
                        defaultValue, true, "afterBrowseForFiles", targetFieldName);
                evt = (evt) ? evt : event;
                Event.stop(evt);
                return false; // don't make AJAX call yet, wait for applet to
                                // trigger it
            } // end if fileSelectionApplet
            else {
                
                // from http://tanalin.com/en/articles/ie-version-js/
                var isIE8 = false;
                if (document.all && document.querySelector && !document.addEventListener) {
                    isIE8 = true;
                    //Note:For IE9,IE10 it returns true but for IE11 it is false (checked with X26.06)
                    console.log("isIE8:"+isIE8+"  PTC_isIE11:"+PTC_isIE11);
                }

                if (isIE8 == false) {
                    // Viklele
                    var createType = document.getElementById("createType");
                    if (createType) { 
                        var val = createType.value;
                        if (val.length == 0) {
                            return false;                       
                        }
                    }
    
                    var fs = document.createElement('input');
                    fs.setAttribute('type', 'file');
                    fs.setAttribute('multiple', 'multiple');
                    
                    //IE11 needs below fixes to show file dialog. Fix#1(IE11)
                    if(PTC_isIE11 == true) 
	                document.forms[0].appendChild(fs);
        
                    fs.onchange = function(e) {
                        var files = this.files;
                        if (files === "undefined")
                            files = this.value;
        
                        createRowsAndStartUpload(targetFieldName, files, actionName);
                    };          
                    fs.click();
                    if(PTC_isIE11 == true)  //Fix#2(IE11)
                        document.forms[0].removeChild(fs);
                    return false;       
                }
            }
    
            return true;
        } catch (e) {
            alert("An exception occurred in the JS API browseForFiles().\nError name: "
                    + e.name + ".\nError message: " + e.message);
        }
    }

   
   function createRowsAndStartUpload(targetFieldName, files, actionName, rowTable) {
        var fileNames = "";
        var uploaderFileList = [];
        for (var i = 0; i < files.length; i++) {
            var key = "UPLOADITEM_" + files[i].name;
            if (window[key]) {
                console.log("Uploading same file again!");
            } 
            else {
               // attach file object to window
               window[key] = files[i];
               uploaderFileList.push(files[i]);
                
               if (i == 0)
                   fileNames = files[i].name;
               else
                   fileNames = fileNames + DELIM + files[i].name;
            }          
        }

        if (uploaderFileList.length > 0) {
            // we need this to initiate upload after the rows have been created
            // window.uploaderFileList = uploaderFileList;
            
            //Put path seperator as per OS. Inline with single file browse.
            fileNames = CheckFilePathSeparators(fileNames, getFileSeparator());

            //set the file list in targetFieldName
            var newFilesField = document.getElementsByName(targetFieldName)[0];
            newFilesField.value = fileNames;
            
            // submit row creation request
            // based on inputs provided by Ashish
            if (rowTable == null) {
                var rowTable = "table__attachments.list.editable_TABLE"; // primary doc attachment wizard step
                var currentStep = getCurrentStep(); 
                if (actionName == "wp_addFileAttachment" || currentStep.indexOf("wp.wp_exp_attachments") != -1)      
                    rowTable = "wp.attachments.list.editable";  // delivery attachment table on new package wizard step         
            }

            var domElem = document.getElementById('setTypeAndAttributesWizStepForCreateMulti'); 
            if(domElem)
                rowTable = "table__multiDocWizAttributesTableDescriptor_TABLE"; // multi-doc wizard step
                
           submitIt('addFileAttachment', 'null', '','','','','','','','','','','','','portlet', 'poppedup', 'context', '','oid', '', '', 'none', 'com.ptc.windchill.enterprise.attachments.commands.AttachmentCommands', 'addFileAttachment', 'null', '','', false, '', 'row', '',false, rowTable);
        }
   }
   
   function afterBrowseForFiles(targetFieldName,newValue) {
   var inputDefaultPathArray = document.getElementsByName("prefDefaultPath");
   var inputUpdatedPathArray = document.getElementsByName("updateDefaultPath");
      var inputDefaultPath = inputDefaultPathArray[0];
      var inputUpdatedPath = inputUpdatedPathArray[0];
      if (inputDefaultPath && typeof inputDefaultPath!= 'undefined') {
      if(""==inputDefaultPath.value ){
       if (inputUpdatedPath && typeof inputUpdatedPath!= 'undefined'){
       //Put path seperator as per OS. Inline with single file browse.
       newValue = CheckFilePathSeparators(newValue, getFileSeparator());
       inputUpdatedPath.value=newValue;
      }
      }
      }
       if ( newValue && newValue.length>0 ) {
            if(ACTION_NAME == "wp_dragAndDropFileSelectionApplet" || ACTION_NAME == "wp_addFileAttachment") {
                wp_dragAndDropFileSelectionAppletJavascriptFunction(newValue,getFileSeparator(),true);
            } else {
          dragAndDropFileSelectionAppletJavascriptFunction(newValue,getFileSeparator(),true);
       }
   }
   }

   function getFileSeparator() {
      var ua = navigator.userAgent;
      if (ua.indexOf("Win") > 0 ) {
         return "\\";
      } else {
         return "/";
      }
   }


   function getAppletByName(appletName) {
      var a_applet = null;
      if(appletName) {
         a_applet = document.getElementsByName(appletName);
      }

      if ( (a_applet) && a_applet.length ) {
         for(i=0;i<a_applet.length;i++) {
            var appletObject = a_applet[i];

            // Determine if this is the applet we want to work with.
            if(appletObject.width <= 2){
                try {
                   if( appletObject.getTargetApplet()) {
                  a_applet = appletObject;
                  break;
                   }
                 } catch ( e ) {
                  //Ignoring this exception. This is required for cases where getTargetApplet() API is not available.
               }
            }
         }
      }
      else {
         return null;
      }

      return a_applet;
   }


   function getMainFormForAttachments() {
      //var mainForm = window.document.forms.mainform;
      var mainForm = document.getElementById("mainform");
      if (!mainForm) {
          mainForm = getMainForm();
      }
      return mainForm;
   }


   function getFileElements(mform, includeEmptyFields, fileElementIndicator) {
    var fileElements = [];

      if(mform) {

        var curActionName = '';
        var url = window.location.href;
        var vars = url.split("&"); 
        for (var i=0;i<vars.length;i++) {
            var pair = vars[i].split("="); 
            if (pair[0] == "actionName") {
                curActionName =  pair[1];
                break;
            }
        }

         var arrayIndex = 0;

         var formElements = mform.elements;
         var formElementslength = formElements.length;

         // iterate through form elements looking for file elements
         for (var i=0; formElements && i < formElementslength; i++) {
           //Ext FieldSet type does not have name.
           if (typeof formElements[i].name == 'undefined') {
           continue;
       }

            var formElementName = formElements[i].name;
            if ( (formElementName.indexOf(fileElementIndicator + '_') >= 0 || formElementName.indexOf(fileElementIndicator + '!') >= 0) && formElementName.lastIndexOf('_old') < 0 ) {  // check field name to see if it's a file element (***MEC need to switch field names)

               if ( includeEmptyFields || formElements[i].value.length > 0 ) {  // check if the field has a value

                  if ( formElementName.indexOf(PRIMARY_FILE_PREFIX) >= 0 && document.getElementById( SOURCE_COMBO_BOX_ID ) ) {  // check if the field is for a primary file

                     if ( document.getElementById( SOURCE_COMBO_BOX_ID ).value=="FILE" ) {  // check if the primary content should be a file

                        if ( document.forms[0].upload_file_from_selected_filepath ) {  // check if the upload-or-not radio buttons exist

                           if ( document.forms[0].upload_file_from_selected_filepath.checked == true) {  // check if the upload radio button is selected
                              // add the upload-selected file element to the list
                              formElements[i].disabled = false;
                              fileElements[arrayIndex] = formElements[i];
                              arrayIndex = arrayIndex + 1;
                           } else {
                              // disable the file element so that it doesn't replace existing file
                              formElements[i].disabled = true;
                           } // end if (upload_file_from_selected_filepath radio button is checked)

                        } else {
                           // add the file element to the list because radio buttons do not exist so field will only have value if user gave new value
                           formElements[i].disabled = false;
                           fileElements[arrayIndex] = formElements[i];
                           arrayIndex = arrayIndex + 1;
                        } // end if (radio buttons do exist)

                     } else {
                        //disable primary file element because FILE div is hidden
                        formElements[i].disabled = true;
                     } // end if (FILE div is visible)

                  } else {
                     // add file element to the list
                     //SPR:1492733
                     var optionFound = false;
                     var optionValue = false;

                     if(curActionName == "editMultiObjects")
                     {
                        var obj = getParentNode(formElements[i],"td");
                        var inputTags = obj.getElementsByTagName("input");
                        for(inputCount = 0;inputCount<inputTags.length;inputCount++) {
                            var inputTag = inputTags.item(inputCount);
                            if(inputTag.type=="radio" && inputTag.id=="upload_file_from_selected_filepath"){
                                optionFound = true;
                                if(inputTag.checked){
                                    optionValue = inputTag.checked;
                                    break;
                                }
                            }
                        }
                     }

                     if((optionFound && optionValue) || (!optionFound)){
                         formElements[i].disabled = false;
                         fileElements[arrayIndex] = formElements[i];
                         arrayIndex = arrayIndex + 1;
                     }
                  } // end if (file element is a primary file)

               } else {
                  // disable empty file elements
                  formElements[i].disabled = true;
               } // end if (file element has a non-empty, non-whitespace value)

            } // end if (form element is a file element)

         } // end for loop through formElements

      } // end if (there is a form passed)
      return fileElements;
   } // end function getFileElements(mform, includeEmptyFields, filePathIndicator)

   function getFilePaths(fileElements) {
      var filePaths = "";

      if(fileElements) {

         // format of record
         // <fieldname> + DELIM + <filepath> [ + RECORD_DELIM + <fieldname> + DELIM + <filepath> ...]
         for (var i=0; i < fileElements.length; i++) {
            var fileElement = fileElements[i];
            if(fileElement && fileElement.value.length > 0) {

               var recordString = fileElement.name + DELIM + fileElement.value;
               if(filePaths && filePaths.length > 0) {
                  filePaths = filePaths + RECORD_DELIM + recordString;
               }
               else {
                  filePaths = recordString;
               } // end if (filePaths exist and length is greater than 0
            } // end if fileElement exists and value length is greater than 0
         } // end for loop
      } // end if (fileElements)
      return filePaths;
   }

   function getFileCount(mform) {
      var fileCount = 0;
      if(mform) {
         var htmlElements = mform.elements;
         for(var i=0; i<htmlElements.length; i++) {
            var elementType = htmlElements[i].type;
            if(elementType.toLowerCase() == "file" && htmlElements[i].value.length > 0) {
               fileCount++;
            }
         }
      }

      return fileCount;
   }

   // Given a file path string this function will
   // strip the filename off the file path.
   function stripFileOffFilePath(filepath, fileSep) {
      var path = "";
      if (filepath) {
         filepath = CheckFilePathSeparators(filepath, fileSep);
         var indexOfLastSep = filepath.lastIndexOf(fileSep);
         path = filepath.substring(0, indexOfLastSep + 1);
      }
      return path;
   }

   // Given a file input element id and a text input element id this function
   // will get the value in the file input element, strip the file off the
   // path and then write the stripped path to the text input element.
   // It will then remove the file input element from the page so that
   // it will not get submitted.  For use in selecting directory paths
   // with file input elements.
   function setStrippedFilePath(filePathElementId, strippedPathElementId) {
      var filePathElement = document.getElementById(filePathElementId);
      var strippedPathElement = document.getElementById(strippedPathElementId);
      if (filePathElement && strippedPathElement) {
         var strippedPath = stripFileOffFilePath(filePathElement.value, getFileSeparator());
         if (strippedPathElement.maxLength > 0 && strippedPath.length > strippedPathElement.maxLength) {
            strippedPath = strippedPath.substring(0, strippedPathElement.maxLength);
         }
         strippedPathElement.value = strippedPath;
         if (strippedPathElement.onchange) {
            strippedPathElement.onchange();
         }
      }

      if (filePathElement) {
          Element.remove(filePathElement);
      }
      // this is another artifact that is sometimes added to the form when there is a
      // file input component that causes issues in the WizardServlet... so it is removed too
      var fileHasDirElements = Form.getInputs(getMainFormForAttachments(), 'hidden', 'file_has_dir');
      if (fileHasDirElements.length > 0) {
         Element.remove(fileHasDirElements[0]);
      }
   }

   ////////////////////////////////////////////////////////////////////////////////////////////
   // Primary Attachment Functions                                                             //
   ///////////////////////////////////////////////////////////////////////////////////////////

   function setStatus( status ) {
      document.getElementById("primary0contentStatus").value = status;
   }

   function hideElement(id) {
     var elem = document.getElementById(id);

     if (elem) {
       inputs = elem.getElementsByTagName("input");
       for(i = 0; i < inputs.length;i++){
         if (inputs[i].className.indexOf("required") == 0) {
           inputs[i].className = "hide_must"; // no required as part of string
         }
       } // end for i

       areas = elem.getElementsByTagName("textarea");
       for(a = 0; a < areas.length; a++){
         if (areas[a].className.indexOf("required") == 0) {
           areas[a].className = "hide_must"; // no required as part of string
         }
       } // end for a

       if (elem.style) {
         elem.style.display = "none";
       } else {
         elem.display = "none";
       }
     }
   }

   function showElement(id) {
     var elem = document.getElementById(id);

     if(elem) {
       inputs = elem.getElementsByTagName("input");
       for (i = 0; i < inputs.length;i++){
         if (inputs[i].name.lastIndexOf("_old") != -1) {
           inputs[i].className = "none";
         } else if (inputs[i].type == "file" && document.forms[0].upload_file_from_selected_filepath != null && document.forms[0].upload_file_from_selected_filepath.checked == false ) {   // filepath is not required if upload radio button is present but not selected
           inputs[i].className = "none";
         } else if (inputs[i].className.indexOf("hide_must") == 0) {
           inputs[i].className = "required";
         }
       } // end for i

       areas = elem.getElementsByTagName("textarea");
       for (a = 0; a < areas.length; a++){
         if (areas[a].name.lastIndexOf("_old") != -1) {
           areas[a].className = "none";
         } else if (areas[a].className.indexOf("hide_must") == 0) {
           areas[a].className = "required";
         }
       } // end for a

       var tagName = elem.tagName.toUpperCase();
       if (elem.style) {
         if (tagName === "TBODY" || tagName === "TR" ) {
             elem.style.display = "";
         }
         else {
             elem.style.display = "block";
         }
       } 
       else {
         if (tagName === "TBODY" || tagName === "TR" ) {
             elem.display = "";
         }
         else {
             elem.display = "block";
         }
       }
     }
   }

   function setFieldRequired( elementId, isRequired ) {
         var element = document.getElementById( elementId );
         var fiWrapperElem  = document.getElementById("fiWrapper");      
         
         var rowElementUpload = document.getElementById("UploadNoContent");
         var rowElementUploadWithContent = document.getElementById("UploadWithContent");
         var radioButtonUploadWithContent = document.getElementById("upload_file_from_selected_filepath");
         var urlstr = window.location.href;
         urlstr = urlstr.substring(urlstr.indexOf("?") +1);
         var params =urlstr.parseQuery();
         
         if(isRequired == "true") {
            if(rowElementUpload)
                 rowElementUpload.style.display = 'none';
            
            if(rowElementUploadWithContent)
                 rowElementUploadWithContent.style.display = '';             
            
            if(radioButtonUploadWithContent) {
                if(params != null && params.actionName != "editMultiObjects") {
                    radioButtonUploadWithContent.checked=true;
                }
                element.checked = true;
            }
            
            if (fiWrapperElem)
                 fiWrapperElem.style.display = '';

         }
         else{
             if(rowElementUpload)
                 rowElementUpload.style.display = '';
             if(rowElementUploadWithContent)
                 rowElementUploadWithContent.style.display = 'none';
             
             if (fiWrapperElem)
                 fiWrapperElem.style.display = 'none';
         }
         
         // Show / hide uploadified file selector
         showFileSelector(elementId, isRequired);

         var buddyTextBox = null;
         if (elementId == "primaryFilepathInput") {
             buddyTextBox = document.getElementById("primaryFilenameInput");             
         }
                 
         if(buddyTextBox) {
             setFieldRequiredByElement(buddyTextBox, isRequired);
         }
         setFieldRequiredByElement( element, isRequired );
   }

   function showFileSelector(elementId, bShow) {
       var selector = "input[type='file'][id^='" + elementId + "']";
       var matchingElements = document.querySelectorAll(selector);
       if (matchingElements !== null && matchingElements.length == 1) {
           var fileInputBox = matchingElements[0];
           
           // walk up the parent chain to get the input file decorator wrapper
           var decoratorWrapper = getDecoratorWrapper(fileInputBox, "fileinput-wrapper");
           if (decoratorWrapper) {
               if(bShow == "true")
                   decoratorWrapper.style.display = '';
               else
                   decoratorWrapper.style.display = 'none';
           }
           else {
               if(bShow == "true")
                   fileInputBox.style.display = '';
               else
                   fileInputBox.style.display = 'none';
           }
       }
   }
   
   function getDecoratorWrapper(element, matchClass) {
       var parent = element.parentNode;
       if (parent) {
           if (parent.className == matchClass)
               return parent;
           else 
               return getDecoratorWrapper(parent, matchClass);
       }    
       
       return null;
   }
   
   function setFieldRequiredByElement( element, isRequired ) {

      if ( isRequired == true || isRequired == "true" ) {
         element.className = "required";
      } 
      else {
         element.className = "none";
      }

   }

   function displayBefore() {
      hideElement('after');
      showElement('before');
   }

   function displayAfter() {
      hideElement('before');
      showElement('after');
   }

   function updateVisibleContentFields()
   {
      contentTypeChanged( "primary0contentSourceList" );
   }

   function contentTypeChanged( comboBoxId )
   {

      if ( comboBoxId == null || comboBoxId.length == 0 ) {
         comboBoxId = SOURCE_COMBO_BOX_ID;
      }
      var source = document.getElementById( comboBoxId ).value;

      // first hide everything
      // ***MEC get strings from Java constants
      hideElement('primaryFileWidgets');
      hideElement('primaryURLWidgets');
      hideElement('primaryExternalWidgets');
      hideElement('primarySCMWidgets');

      // then show whatever's appropriate
      // ***MEC get strings from Java constants
      if ( source=="URL" ) {
         showElement('primaryURLWidgets');
      } else if ( source=="EXTERNAL" ) {
         showElement('primaryExternalWidgets');
      } else if ( source=="CLEARCASE" ) {
            showElement('primarySCMWidgets');
      } else if ( source=="FILE" ) {
         showElement('primaryFileWidgets');
         if ( primaryDragAndDropFileSelectionAppletSource != null ) {
             CreateControl('primaryDragAndDropFileSelectionAppletDiv', primaryDragAndDropFileSelectionAppletSource);
         }
      }

      // else don't show any primary widgets

   }

   function CreateControl(divID, controlSource)
   {
      var d = document.getElementById(divID);
      if ( d != null && controlSource != null ) {
         d.innerHTML = controlSource;
      }
   }

   /* Grabs every input that is styled as 'contentRequired' and checks
    * to make sure it has a value.  If any contentRequired fields do not
    * have values, the field in question is highlighted and
    * an alert message is displayed.  This function returns
    * a boolean that is true if the contentRequired fields are entered
    * and false if any contentRequired field is not filled in.
    */
   function checkContentRequiredFields() {
      var pass = true;
      var currentStep = $(currentStepStrName);
      var tagNames = [];
      tagNames[0] = "input";
      tagNames[1] = "textarea";
      tagNames[2] = "select";
      var tagNames_length=tagNames.length;
      var urlstr = window.location.href;
      urlstr = urlstr.substring(urlstr.indexOf("?") +1);
      var params =urlstr.parseQuery();
      for(var tagIdx=0; tagIdx<tagNames_length; tagIdx++) {
         var contentRequired = getElementsByClassNameAndTag('contentRequired', currentStep, tagNames[tagIdx]);

      // check if the primary content should be a file
      if ((document.getElementById( SOURCE_COMBO_BOX_ID ) && document.getElementById( SOURCE_COMBO_BOX_ID ).value=="FILE") || (params != null && params.actionName == "editMultiObjects")) {
         var regExp = /[^ ]/;
            var contentRequiredLength = contentRequired.length;
            for (var i = 0; i < contentRequiredLength; i++)  {
               var contentRequird = contentRequired[i];
               if (contentRequird.value.length <= 0 || !contentRequird.value.match(regExp)) {
               pass = false;
               var message = $("requiredMessage");
               wfalert(message.value);
                  var element = contentRequird;
               try {
                 new Ext.get(element).highlight({ duration: 5.0 });
                 break;
               } catch(e) {}
               }
            }
         }
      }
      return pass;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////
   //SCMI RELATED JS SCRIPTS...
   ////////////////////////////////////////////////////////////////////////////////////////////

   var scmWindow ="";
   function launchSCMPicker (location) {
      scmWindow = wfWindowOpen (location, 'scmWindow',
            'resizable=yes,scrollbars=yes,menubar=no,toolbar=no,location=no,status=yes,height=800,width=800');
   }

   function setSCMAttachment(newValue) {
      document.forms[0].___PRIMARY_CLEARCASE__ccPath___textbox.value = newValue;
      document.forms[0].___PRIMARY_CLEARCASE__ccLocation___textbox.value = parseSCMDisplayString(newValue);
      document.forms[0].___PRIMARY_CLEARCASE__ccLocation___textbox.focus();
      scmWindow.wfWindowClose();
   } // end setSCMAttachment()

   function parseSCMDisplayString (value) {
      value = value.substring (value.lastIndexOf("path=") + 5, value.length);
      value = value.substring (value.indexOf("/") + 1);
      value = value.substring (value.indexOf("/"));
      return value;
   }

   function setSCMSecondaryAttachments(record) {
      var scmControl = document.forms[0].scmResults;
      if (scmControl.value.length > 0) {
         scmControl.value = scmControl.value + ';;;zzz';
      }
      scmControl.value = scmControl.value + record;
      var idList  = record.split(";;;zzz");
      var oidList = new Array();
      var baseNumber  = generateRandomNumber();
      for (var i=0; i < idList.length; ++i) {
         var number = baseNumber + i;
         oidList[i] = "wt.facade.scm.ScmApplicationData:" + number;
         var location = parseSCMDisplayString(idList[i]);
         addScmSecondaryHiddenFields(oidList[i], idList[i], location);
      }
      if (0 < oidList.length) {
         addRows(oidList, "attachments.list.editable", false, true, true, null);
      }
      scmWindow.wfWindowClose();
   }

   function generateRandomNumber() {
      var randomnumber = Math.floor (Math.random() * 111111);
      var today = new Date();
      var to_return = today.getMilliseconds() + randomnumber;
      return to_return;
   }

   function launchScmiBrowser(evt) {
      if (scmpickerlocation == "http://www.ptc.com") {
         launchSCMPicker(document.forms[0].scmi_picker.value);
      }
      else {
         launchSCMPicker(scmpickerlocation);
      }
      evt = (evt) ? evt : event;
      Event.stop( evt );
      return false;
   }

    // add hidden fields for the path and location
   function addScmSecondaryHiddenFields(oid, ccPath, ccLocation) {
      var ccPathName = oid + "ccPath";
      var ccLocationName = oid + "ccLocation";
      addScmField(ccPathName, ccPathName, ccPath);
      addScmField(ccLocationName, ccLocationName, ccLocation);
   }

   // utility function - has Scm in name to avoid name conflicts
   function addScmField(name, id, value) {
      var field = document.createElement("input");
      field.setAttribute("type", "hidden");
      field.setAttribute("name", name);
      field.setAttribute("id", id);
      field.setAttribute("value", value);
      var mform = getMainFormForAttachments();
      mform.appendChild( field );
   }
   function refreshTable(){

      var tableID = extractParamValue(getWindow().location.href, 'tableID');
      if (tableID) 
      {
            window.opener.refreshCurrentElement(tableID, false, Ext.isIE);
      }

    }


function getContextTable() {
    var tableID = null;
    var urlstr = window.location.href;
    urlstr = urlstr.substring(urlstr.indexOf("?") +1);
    var params =urlstr.parseQuery();
    if (params.tableID && isArray(params.tableID)){
       params.tableID = params.tableID[0];
    }        
    if(params.tableID == "table__attachments.table.secondary_TABLE") {
       tableID = "table__attachments.list.editable_TABLE";      
    } else if(params.actionName && params.actionName == "createMulti") {
       tableID = "multiDocWizAttributesTableDescriptor";
    } else if(params.actionName && params.actionName == "create") {
        tableID = "table__attachments.list.editable_TABLE";
    }
    if(!tableID) {
       return null;
    } else {
       return tableID;
    }
}
function setTextAreaValueByName (textAreaName, value) { 
    var textArea = document.getElementsByTagName('textarea');   
    if(textArea.length>0){
     for(i=0;i<textArea.length;i++) {
      if(textAreaName==textArea[i].name){
       textArea[i].value = value;
       break;
      }
     }
    }
}

function setTextAreaValueById (textAreaId, value) {
   var textArea = document.getElementById(textAreaId);
   textArea.value = value;  
}

//Required for listing in browser's debug tool when the file is dynamically loaded
//# sourceURL=attachments.js 
