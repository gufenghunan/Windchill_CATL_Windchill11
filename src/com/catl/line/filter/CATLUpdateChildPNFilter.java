package com.catl.line.filter;


import java.util.Arrays;
import java.util.Locale;

import org.drools.core.util.StringUtils;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.fc.Persistable;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.util.IBAUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLUpdateChildPNFilter extends DefaultSimpleValidationFilter{

	/**
	 * 更新衍生PN验证 
	 * 对当前部件有修改权限。
	 * 当前部件的状态为指定状态
	 */
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
			Persistable persistable = criteria.getContextObject().getObject();
			if(persistable instanceof WTPart){
				WTPart part=(WTPart) persistable;
				String isParentPN = IBAUtil.getStringIBAValue(part,
						ConstantLine.var_parentPN);
				if (StringUtils.isEmpty(isParentPN)|| isParentPN.equals(ConstantLine.judgeparentPN)) {
					status = UIValidationStatus.HIDDEN;
				}else{
					String allow_state_str=PropertiesUtil.getValueByKey("config_filter_updatechildpn_state");
					String []allow_state_array=allow_state_str.split(",");
					boolean flag=Arrays.asList(allow_state_array).contains(part.getLifeCycleState().getDisplay(Locale.CHINA));
					if(flag){
						WTPrincipal user;
						try {
							user = SessionHelper.getPrincipal();
							boolean access=AccessControlHelper.manager.hasAccess(user, part, AccessPermission.MODIFY);
							if(!access){//对当前部件没有修改权限隐藏
								status = UIValidationStatus.HIDDEN;
							}else{
								status = UIValidationStatus.ENABLED;
							}
						} catch (WTException e) {
							e.printStackTrace();
						}
					}else{
						status = UIValidationStatus.HIDDEN;
					}
				}
				
			}
			
		return status;
	}
	
}
