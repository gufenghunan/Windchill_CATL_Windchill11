package com.catl.bom.load;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import wt.log4j.LogR;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class BOMLoadProcessor extends DefaultObjectFormProcessor {

    private static final Logger LOGGER = LogR.getLogger(BOMLoadProcessor.class.getName());
    private final String FILEID = "bomloadFile";

    /**
     * import Reference link(s)
     * 
     * @param cmdBean
     * @param objects
     * @return
     */
    @Override
    public FormResult doOperation(NmCommandBean cmdBean, List<ObjectBean> objects) throws WTException {

        FormResult result = super.doOperation(cmdBean, objects);
        // FormResult result = new FormResult();
        LOGGER.debug("Load BOM::doOperation...");

        HashMap fileMap = (HashMap) cmdBean.getMap().get("fileUploadMap");
        String fileName = cmdBean.getTextParameter(FILEID); // get the filename which uploaded
        // if you not select file,then display errors in the UI

        if (fileName == null || "".equals(fileName)) {
            result.setNextAction(FormResultAction.NONE);
            FeedbackMessage returnMsg = new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
                    null,
                    "Please select a file to upload!");
            result.setStatus(null);
            result.addFeedbackMessage(returnMsg);
            return result;
        }
        // 1. get the tempfile of upload from fileMap
        File tempFile = (File) fileMap.get(FILEID);
        String oid = cmdBean.getRequest().getParameter("oid");
        LOGGER.debug("oid is:"+oid);
        try {
            BOMLoadHelper.doLoadBOM(oid, tempFile);
        } catch (InvalidFormatException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            throw new WTException(e, e.getLocalizedMessage());
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            throw new WTException(e, e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            throw new WTException(e, e.getLocalizedMessage());
        }

        // 2. return success promotion msg.
        LOGGER.debug("-----else---No Error-----");
        FeedbackMessage returnMsg = new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null,
                "Import the reference doc(s) successed!");
        result.addFeedbackMessage(returnMsg);
        result.setStatus(FormProcessingStatus.SUCCESS);
        return result;
    }
}
