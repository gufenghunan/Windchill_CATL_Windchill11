package com.catl.change.filter;

import org.apache.log4j.Logger;

import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.constant.ChangeState;
import com.catl.common.constant.TypeName;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.windchill.principal.user.userActionsRB;

public class ReivseValidation extends DefaultSimpleValidationFilter{
	private static Logger logger=Logger.getLogger(ReivseValidation.class.getName());
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria validationCriteria){
		Persistable persistable = validationCriteria.getContextObject().getObject();
		 WTPrincipal userPrincipal=null;
		try {
			userPrincipal = SessionHelper.manager.getPrincipal();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//logger.debug("now user is=="+userPrincipal.getName());
		if (isSiteAdmin(userPrincipal)) {
			return UIValidationStatus.ENABLED;
		}
		String type="";
		if(persistable instanceof WTDocument)
		{
			WTDocument document =(WTDocument)persistable;
			TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(document);
			type = ti.getTypename();
			logger.debug("doctype===="+type);
		}	
		if(!type.endsWith(TypeName.doc_type_rdDoc)&&!type.endsWith(TypeName.doc_type_technicalDoc)&&!type.endsWith(TypeName.doc_type_EDatasheetDoc)&&!type.endsWith(TypeName.kopdmdoc)
				&&!type.endsWith(TypeName.mdDoc)&&!type.endsWith(TypeName.theSimulationReport)&&!type.endsWith(TypeName.sordoc)&&!type.endsWith(TypeName.doc_type_CoreProductData))
		{
		try{
				QueryResult ecaResult=new QueryResult();
				String ecaState = "";
				int count=0;
				ecaResult=ChangeHelper2.service.getAffectingChangeActivities((Changeable2) persistable);
               //  logger.debug("ecaResult size==="+ecaResult.size());
                 while (ecaResult.hasMoreElements()) {
					WTChangeActivity2 eca = (WTChangeActivity2) ecaResult.nextElement();
					ecaState =eca.getState().toString();
					if (ecaState.endsWith(ChangeState.IMPLEMENTATION)) {
						count++;
					}
				}
				if(count > 0){
	                return UIValidationStatus.ENABLED;
	            }else if (count==0){
					return UIValidationStatus.DISABLED;
				}
		} catch (Exception e){
			e.printStackTrace();
		}
		}else {
			return UIValidationStatus.ENABLED;
		}
		
		return UIValidationStatus.HIDDEN;
	}
    public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
        try {
            return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
}
