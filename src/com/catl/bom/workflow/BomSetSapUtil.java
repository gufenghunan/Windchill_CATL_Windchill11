package com.catl.bom.workflow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.httpgw.GatewayAuthenticator;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;

import com.catl.integration.BomInfo;
import com.catl.integration.ErpData;
import com.catl.integration.PartInfo;

public class BomSetSapUtil {
	private static Logger log = Logger.getLogger(BomSetSapUtil.class.getName());

	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);
		String oid=args[0];
		String oidString = "OR:wt.maturity.PromotionNotice:"+oid;
		Persistable persistable = BomWfUtil.getPersistableByOid(oidString);
		PromotionNotice pNotice = (PromotionNotice) persistable;
		try {
			BomWfUtil.refreshPromotionTargets(pNotice);
		} catch (WTException e1) {
			e1.printStackTrace();
		}
		try {
			ErpData data = getBomPublishInfo(pNotice);
			List<PartInfo> partList = data.getParts();
			List<BomInfo> bomInfos = data.getBoms();
			System.out.println("part list ==" + partList.size());
			for (int i = 0; i < partList.size(); i++) {
				PartInfo partInfo = (PartInfo) partList.get(i);
				System.out.println("PartNumber=="+partInfo.getPartNumber());
				System.out.println("PartName=="+partInfo.getPartName());
				System.out.println("Source=="+partInfo.getSource());
				System.out.println("Version=="+partInfo.getDrawingVersion());
				System.out.println("Iteration=="+partInfo.getIteration());
				System.out.println("MaterialGroup=="+partInfo.getMaterialGroup());
				System.out.println("DefaultUnit=="+partInfo.getDefaultUnit());
			}
			System.out.println("bom list ==" + bomInfos.size());
			for (int i = 0; i < bomInfos.size(); i++) {
				BomInfo bomInfo = (BomInfo) bomInfos.get(i);
				System.out.println("ParentPartNumber=="+bomInfo.getParentPartNumber());
				System.out.println("ChildPartNumber=="+bomInfo.getChildPartNumber());
				System.out.println("Quantity=="+bomInfo.getQuantity());
				System.out.println("SubstitutePartNumber=="+bomInfo.getSubstitutePartNumber());
			}
		} catch (MaturityException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static ErpData getBomPublishInfo(PromotionNotice pn)
			throws MaturityException, WTException, ParseException {
		ArrayList<WTPart> partlist = getBomforPRtargets(pn);
		return getInfoNodes(partlist);
	}

	public static ArrayList<WTPart> getBomforPRtargets(PromotionNotice pn)
			throws MaturityException, WTException {
		ArrayList<WTPart> partlist = new ArrayList();
		QueryResult targetsResult = MaturityHelper.service
				.getPromotionTargets(pn);
		while (targetsResult.hasMoreElements()) {
			Object object = targetsResult.nextElement();
			if ((object instanceof WTPart)) {
				WTPart part = (WTPart) object;
				partlist.add(part);
			}
		}
		return partlist;
	}

	public static ErpData getInfoNodes(List parents) throws WTException,
			ParseException {
		ErpData data = new ErpData();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List partInfoList = new ArrayList();
		List bomInfoList = new ArrayList();
		for (Object object : parents) {
			if ((object instanceof WTPart)) {
				PartInfo partInfo = new PartInfo();
				WTPart part = (WTPart) object;

				partInfo.setPartName(part.getName());
				partInfo.setPartNumber(part.getNumber());
				partInfo.setCreateDate(format.parse(part.getCreateTimestamp()
						.toLocaleString()));
				partInfo.setCreator(part.getCreatorName());
				partInfo.setDefaultUnit(part.getDefaultUnit().toString());
				partInfo.setSource(part.getSource().getDisplay());

				partInfo.setMaterialGroup(part.getNumber().substring(0, 6));
				partInfo.setIteration(part.getIterationIdentifier().getValue());
				partInfo.setVersionBig(part.getVersionIdentifier().getValue());

				partInfoList.add(partInfo);
				WTPartConfigSpec configSpec = (WTPartConfigSpec) ConfigHelper.service
						.getConfigSpecFor(part);
				QueryResult qr = WTPartHelper.service.getUsesWTParts(part,
						configSpec);
				WTPartUsageLink usageLink = null;
				WTPartMaster childmaster = null;
				while (qr.hasMoreElements()) {
					BomInfo bomInfo = new BomInfo();
					Persistable[] aSubNodePair = (Persistable[]) qr
							.nextElement();
					usageLink = (WTPartUsageLink) aSubNodePair[0];
				    
					ArrayList<String> substituteList = getSubstitutePart(usageLink);
					log.debug("substituteList size==" + substituteList.size());
					childmaster = usageLink.getUses();
					String childnumberString = childmaster.getNumber();
					if (substituteList.size() > 0) {
						for (int i = 0; i < substituteList.size(); i++) {

							BomInfo bomInfo1 = new BomInfo();
							bomInfo1.setChildPartNumber(childnumberString);
							bomInfo1.setParentPartNumber(part.getNumber());
							bomInfo1.setQuantity(usageLink.getQuantity()
									.getAmount());
							bomInfo1.setSubstitutePartNumber(((String) substituteList
									.get(i)).toString());
							bomInfoList.add(bomInfo1);
						}
					} else {
						bomInfo.setChildPartNumber(childnumberString);
						bomInfo.setParentPartNumber(part.getNumber());
						bomInfo.setQuantity(usageLink.getQuantity().getAmount());
						bomInfo.setSubstitutePartNumber("");
						bomInfoList.add(bomInfo);
					}
				}
			}
		}
		data.setBoms(bomInfoList);
		data.setParts(partInfoList);

		return data;
	}

	private static ArrayList<String> getSubstitutePart(WTPartUsageLink link)
			throws WTException {
		ArrayList<String> substitutepartlList = new ArrayList();
		WTCollection collection = WTPartHelper.service.getSubstituteLinks(link);
		log.debug("collection size====" + collection.size());
		if (!collection.isEmpty()) {
			Iterator itr = collection.iterator();
			while (itr.hasNext()) {
				ObjectReference objReference = (ObjectReference) itr.next();
				WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference
						.getObject();
				WTPartMaster partMaster = (WTPartMaster) subLink
						.getSubstitutes();
				substitutepartlList.add(partMaster.getNumber());
				log.debug("sub part number===" + partMaster.getNumber());
			}
		}
		return substitutepartlList;
	}
}
