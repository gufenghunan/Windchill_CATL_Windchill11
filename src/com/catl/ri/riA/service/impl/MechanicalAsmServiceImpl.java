package com.catl.ri.riA.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import wt.part.WTPart;
import wt.query.QueryException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import net.sf.json.JSONArray;

import com.catl.ri.entity.CellAttr;
import com.catl.ri.riA.helper.CommonHelper;
import com.catl.ri.riA.service.MechanicalAsmService;
import com.catl.ri.riA.util.WTPartUtil;
import com.catl.cadence.util.NodeUtil;
import com.catl.ecad.utils.CommonUtil;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;


@Scope("prototype")
@Service("riAAsmService")
public class MechanicalAsmServiceImpl implements MechanicalAsmService{
	private static final Logger logger = Logger.getLogger(MechanicalAsmServiceImpl.class.getName());
	@Override
	public JSONArray getAsmInfo(String name) throws WTException, WTPropertyVetoException, FileNotFoundException, IOException {
			List parts=new ArrayList();
			List cellattrs=new ArrayList();
			WTPart part=WTPartUtil.getLastestWTPartByName(name);
			if(part==null){
				throw new WTException("找不到名称为"+name+"的机械件组合");
			}
			CommonUtil.getAllChildParts(parts, part, part.getViewName());
			for (int i = 0; i < parts.size(); i++) {
				WTPart cpart=(WTPart) parts.get(i);
				LWCStructEnumAttTemplate lwc=NodeUtil.getLWCStructEnumAttTemplateByPart(cpart);
				String clfname=lwc.getName();
				List<Map<String, String>> infos=CommonHelper.getTypeAsm(clfname);
				List<CellAttr> attrs=CommonHelper.getAsmCellAttrs(infos,cpart);
				cellattrs.add(attrs);
			}
			List<Map<String, String>> nameinfos=CommonHelper.getTypeAsm("name");
			List<CellAttr> asmnameattrs=CommonHelper.getAsmCellAttrs(nameinfos, part);
			cellattrs.add(asmnameattrs);
			
			List<Map<String, String>> authorinfos=CommonHelper.getTypeAsm("author");
			List<CellAttr> authorattrs=CommonHelper.getAsmCellAttrs(authorinfos, part);
			cellattrs.add(authorattrs);
			return JSONArray.fromObject(cellattrs);
	}
	@Override
	public JSONArray getAsmPNJson() throws QueryException, WTPropertyVetoException, WTException {
		List list=WTPartUtil.queryRIAsmParts();
		return JSONArray.fromObject(list);
	}
}
