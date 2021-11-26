package com.catl.doc.maturityUpReport.mvc.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.doc.maturityUpReport.NFAEMaturityReportLinkBean;
import com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink;
import com.catl.doc.maturityUpReport.resource.MaturityUpReportRB;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmHelperBean;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;

@ComponentBuilder("com.catl.doc.maturityUpReport.mvc.builders.MaturityUpReportLinkBuilder")
public class MaturityUpReportLinkBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		List<NFAEMaturityReportLinkBean> list = new ArrayList<NFAEMaturityReportLinkBean>();
		Object obj = params.getContextObject();
		if(obj instanceof WTDocument){
			WTDocument doc = (WTDocument)obj;
			if(MaturityUpReportHelper.isNFAEMaturityUp3Report(doc)){
				Set<NFAEMaturityUp3DocPartLink>  links = MaturityUpReportHelper.getNFAEMaturityUp3DocPartLink((WTDocumentMaster)doc.getMaster());
				for (NFAEMaturityUp3DocPartLink link : links) {
					list.add(new NFAEMaturityReportLinkBean(link));
				}
			}
		}
		else if(obj instanceof WTPart){
			WTPart part = (WTPart)obj;
			Set<NFAEMaturityUp3DocPartLink>  links = MaturityUpReportHelper.getNFAEMaturityUp3DocPartLinks((WTPartMaster)part.getMaster());
			for (NFAEMaturityUp3DocPartLink link : links) {
				list.add(new NFAEMaturityReportLinkBean(link));
			}
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		JcaComponentParams jcaParams = (JcaComponentParams)params;
		NmHelperBean helperBean = jcaParams.getHelperBean();
		Object obj = helperBean.getNmCommandBean().getActionOid().getRefObject();
		boolean isDoc = (obj instanceof WTDocument);
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		if(isDoc){
			table.setId("TABLE_RELATED_PARTS");
			table.setLabel(MaturityUpReportHelper.getLocalizedMessage(MaturityUpReportRB.TABLE_RELATED_PARTS));
			table.setSelectable(true);
			table.setActionModel("maturity_report_link_actions");
		}
		else {
			table.setId("TABLE_MATURITY_REPORTS");
			table.setLabel(MaturityUpReportHelper.getLocalizedMessage(MaturityUpReportRB.TABLE_MATURITY_REPORTS));
		}
		
		ColumnConfig number = factory.newColumnConfig("number", true);
		if(isDoc){
			number.setTargetObject("part");
		}
		else {
			number.setTargetObject("doc");
		}
		number.setInfoPageLink(true);
		table.addComponent(number);
		
		ColumnConfig version = factory.newColumnConfig("groupedVersions", false);
		if(isDoc){
			version.setTargetObject("part");
		}
		else {
			version.setTargetObject("doc");
		}
		table.addComponent(version);
		
		ColumnConfig name = factory.newColumnConfig("name", false);
		if(isDoc){
			name.setTargetObject("part");
		}
		else {
			name.setTargetObject("doc");
		}
		table.addComponent(name);
		
		ColumnConfig containerName = factory.newColumnConfig("containerName", false);
		if(isDoc){
			containerName.setTargetObject("part");
		}
		else {
			containerName.setTargetObject("doc");
		}
		table.addComponent(containerName);
		
		if(isDoc){
			ColumnConfig initialVersion = factory.newColumnConfig("version", false);
			initialVersion.setTargetObject("initialPart");
			initialVersion.setLabel(MaturityUpReportHelper.getLocalizedMessage(MaturityUpReportRB.LABEL_INITIAL_VERSION));
			table.addComponent(initialVersion);
		}
		
		return table;
	}

}
