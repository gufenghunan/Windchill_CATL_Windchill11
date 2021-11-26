package com.catl.bom.cad;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.catl.common.constant.PartState;
import com.catl.doc.CatlDocNewNumber;
import com.catl.ecad.utils.CommonUtil;
import com.catl.ecad.utils.IBAUtility;
import com.catl.part.CatlPartNewNumber;
import com.ptc.windchill.uwgm.proesrv.c11n.DocIdentifier;
import com.ptc.windchill.uwgm.proesrv.c11n.EPMDocumentNamingDelegate;

import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewReference;

public class CatlEPMDocumentNamingDelegate implements EPMDocumentNamingDelegate {

	@Override
	public void validateDocumentIdentifier(DocIdentifier docId) {
		// TODO Auto-generated method stub
		HashMap map = docId.getParameters();
		System.out.println(CatlEPMDocumentNamingDelegate.class.getName() + "1111111111111111111");
		System.out.println(docId.getDocNumber());
		System.out.println(docId.getDocName());
		System.out.println(docId.getModelName());
		String modelName = docId.getModelName();

		String format = modelName.substring(modelName.lastIndexOf("."));
		if (format.equalsIgnoreCase(".sldprt") || format.equalsIgnoreCase(".sldasm")) {
			String surfacing = "";
			String material = "";
			String special_Explanation = "";
			String number = "";
			String name = docId.getDocName();
			String cls = "";
			String partNumber = "";
			String prdcode = "";

			number = docId.getDocNumber();
			if (number.toUpperCase().startsWith("CMP")) {
				
				int count = checkString(number);
				if(count == 1){
					prdcode = number.substring(0,number.indexOf("-"));
					try {
						number = CatlPartNewNumber.queryMaxPartNumber("CK-");
						if (number == null) {
							number = "CK-" + "00000001";
						} else {
							int temp = 0;

							temp = Integer.parseInt(
									number.substring(number.lastIndexOf("-") + 1));
							temp++;
							System.out.println(temp);

							System.out.println("temp\t" + temp);
							number = Integer.toString(temp);
							while (number.length() < 8) {
								number = "0" + number;
							}
							System.out.println("MaxNumber1\t" + number);
							number = "CK-" + number;
							System.out.println("MaxNumber2\t" + number);

						}
						cls = "CK";
						partNumber = number;
						number = number+format;
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("CK- number is \t"+number);
				}else if(count > 1){
					prdcode = number.substring(0,number.indexOf("-"));
					try {
						number = CatlPartNewNumber.queryMaxPartNumber("CP-");
						
						if (number == null) {
							number = "CP-" + "00000001";
						} else {
							int temp = 0;

							temp = Integer.parseInt(
									number.substring(number.lastIndexOf("-") + 1));
							temp++;
							System.out.println(temp);

							System.out.println("temp\t" + temp);
							number = Integer.toString(temp);
							while (number.length() < 8) {
								number = "0" + number;
							}
							System.out.println("MaxNumber1\t" + number);
							number = "CP-" + number;
							System.out.println("MaxNumber2\t" + number);

						}
						
						partNumber = number;
						number = number+format;
						
						cls = "CP";
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("CK- number is \t"+number);
				}
				Set<String> set = map.keySet();
				for (String key : set) {
					Object obj = map.get(key);
					if(obj instanceof String){
					String value = (String) map.get(key);
					value = value.trim();
					System.out.println(key + "\tValue:\t" + value);

					if ("PartNumber".equalsIgnoreCase(key)) {
						if (StringUtils.isNotBlank(value)) {
							//docId.setDocNumber(value + format);
							number = value + format;
						}
					} else if ("备注".equalsIgnoreCase(key)) {
						if (StringUtils.isNotBlank(value)) {
							special_Explanation = value;
						}
					} else if ("材质或品牌".equalsIgnoreCase(key)) {
						if (StringUtils.isNotBlank(value)) {
							material = value;
						}
					} else if ("表面或热处理".equalsIgnoreCase(key)) {
						if (StringUtils.isNotBlank(value)) {
							surfacing = value;
						}
					}
					}
				}
				docId.setDocNumber(number);
				boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if(StringUtils.isNotBlank(prdcode)){
						prdcode = prdcode.toUpperCase();
						WTPart part = createPart(partNumber, name, "com.CATLBattery.CATLPart", "buy", "ea", "/Default/"+prdcode+"/零部件", cls, "设备开发产品库",special_Explanation,material,surfacing);
						if(part.getLifeCycleState().equals(State.toState(PartState.WRITING))){
							LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part, State.toState(PartState.DESIGN));
						}
					}else{
						throw new WTException("项目代号为空！");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					SessionServerHelper.manager.setAccessEnforced(enforced);
				}
			}else{
				Set<String> set = map.keySet();
				for (String key : set) {
					Object obj = map.get(key);
					if(obj instanceof String){
						String value = (String) map.get(key);
						value = value.trim();
						System.out.println(key + "\tValue:\t" + value);

						if ("PartNumber".equalsIgnoreCase(key)) {
							if (StringUtils.isNotBlank(value)) {
								docId.setDocNumber(value + format);
							}
						}
					}
				}
			}
		}else if(format.equalsIgnoreCase(".slddrw")){
			String number = "";
			String name = docId.getModelName();
			System.out.println("Name:\t"+name);
			String d3Name = name.toUpperCase().replace(".SLDDRW", ".SLDPRT");
			System.out.println("d3NAME:\t"+d3Name);
			EPMDocument epm = getEPMByCADName(d3Name);
			if(epm == null){
				d3Name = name.toUpperCase().replace(".SLDDRW", ".SLDASM");
				epm = getEPMByCADName(d3Name);
				System.out.println("EPM is \t"+epm);
			}
			
			if(epm != null){
				String tempnum = epm.getNumber();
				tempnum = tempnum.substring(0,tempnum.lastIndexOf("."));
				number = tempnum+".SLDDRW";
				System.out.println("number \t"+number);
			}else{
				System.out.println("EPM I S NULLLLLLLLLLLLLL");
			}
			
			if(StringUtils.isNotBlank(number)){
				docId.setDocNumber(number);
			}
		}

	}

	/**
	 * 创建部件 存放指定文件夹
	 * 
	 * @param number
	 * @param name
	 * @param part
	 * @param type
	 * @param surfacing 
	 * @param material 
	 * @param special_Explanation 
	 * @throws Exception
	 */
	public static WTPart createPart(String number, String name, String type, String source, String unit,
			String folderpath, String clf, String containerName, String special_Explanation, String material, String surfacing) throws Exception {
		WTPart part = CommonUtil.getLatestWTpartByNumber(number);
		if (part == null) {
			part = WTPart.newWTPart();
			TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(type);// 设置软类型
			Folder folder = null;// (Folder) WCUtil.getWTObject(folderpath);
			WTContainer container = getContainer(containerName);
			ReferenceFactory factory = new ReferenceFactory();
			folder = FolderHelper.service.getFolder(folderpath, (WTContainerRef) factory.getReference(container));

			part.setName(name);
			part.setNumber(number);
			part.setTypeDefinitionReference(tdr);
			part.setDefaultUnit(QuantityUnit.toQuantityUnit(unit));

			part.setSource(Source.toSource(source));
			part.setContainer(container);
			
			View views = ViewHelper.service.getView("Design");
			ViewReference viewRef = ViewReference.newViewReference(views);
			part.setView(viewRef);
			FolderHelper.assignLocation(part, folder);
			part = (WTPart) PersistenceHelper.manager.save(part);

			IBAUtility iba = new IBAUtility(part);
			if (!StringUtils.isEmpty(clf)) {
				iba.setIBAValue("cls", clf);
			}
			
			if(StringUtils.isNotBlank(special_Explanation)){
				iba.setIBAValue("Special_Explanation", special_Explanation);
			}
			
			if(StringUtils.isNotBlank(material)){
				iba.setIBAValue("Material", material);
			}
			
			if(StringUtils.isNotBlank(surfacing)){
				iba.setIBAValue("Surfacing", surfacing);
			}
			
			
			

			part = (WTPart) iba.updateAttributeContainer(part);
			iba.updateIBAHolder(part);		
			
			WTPartMaster master = part.getMaster();
			
			IBAUtility ibamaster = new IBAUtility(master);
			
			ibamaster.setIBAValue("CATL_FAEStatus", "不需要");
			ibamaster.setIBAValue("CATL_Maturity", "1");
			
			master = (WTPartMaster) ibamaster.updateAttributeContainer(master);
			iba.updateIBAHolder(master);

		}
		return part;
	}

	/**
	 * 获取上下文
	 * 
	 * @param name
	 * @return
	 */
	public static WTContainer getContainer(String name) {
		WTContainer wtc = null;
		QuerySpec qs;
		try {
			qs = new QuerySpec(WTContainer.class);

			SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL,
					name);
			qs.appendWhere(sc);

			sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, SearchCondition.EQUAL, name);

			qs.appendOr();
			qs.appendWhere(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);

			if (qr.size() > 0) {
				wtc = (WTContainer) qr.nextElement();
				return wtc;
			}

		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return wtc;
	}
	
	public static int checkString(String epmnumber) {
		String str = "-";
		int count = 0;
		int start = 0;
		while (epmnumber.indexOf(str, start) >= 0 && start < epmnumber.length()) {
			count++;
			start = epmnumber.indexOf(str, start) + str.length();
		}
		return count;
	}
	
	/**
	 * 获取最新版EPMDocument
	 * 
	 * @param cadName
	 * @return
	 */
	public static EPMDocument getEPMByCADName(String cadName) {
		EPMDocument doc = null;
		try {
			QuerySpec qs = new QuerySpec(EPMDocument.class);
			SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.CADNAME, SearchCondition.EQUAL,
					cadName,false);
			qs.appendWhere(sc, new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			qr = new LatestConfigSpec().process(qr); // 过滤最新

			while (qr.hasMoreElements()) {
				doc = (EPMDocument) qr.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
}
