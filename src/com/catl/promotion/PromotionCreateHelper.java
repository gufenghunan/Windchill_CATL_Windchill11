package com.catl.promotion;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.File;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.State;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

public class PromotionCreateHelper
{
	private static final boolean verbose = false;
	private static String validateStates = "";
	private static final String RESOURCE = "wt.lifecycle.State";

	/**
	 * 如果选择的零部件或是文档不符合下面任何一个条件，在界面上弹出界面报告异常信息，不允许创建签审包：<br>
	 * 3. 所选对象的生命周期状态必须是“正在工作”或“正在修改”<br>
	 * 5. 所选对象没有在其它升级包中存在<br>
	 * 
	 * @param wtobject
	 * @return
	 * @throws Exception
	 */
	public static String validatePromotionObject(WTObject wtobject) throws Exception
	{
		String error = validateInOtherPromotion(wtobject);

		return error;
	}

	private static String validateObjectContainer(WTObject wtobject) throws Exception
	{
		if (verbose)
		{
			System.out.println("enter validateObjectContainer");
		}
		String errorContainerMess = "";
		PromotionNotice pn = PromotionHelper.promotionNotice;
		if (pn == null)
		{
			errorContainerMess = "请刷新页面";
			return errorContainerMess;
		}
		WTContainerRef container = pn.getContainerReference();
		long containerOid = container.getObjectId().getId();
		System.out.println("container" + container);
		WTContainerRef objectCon = null;
		long objectConOid = 0;
		if (wtobject instanceof WTPart)
		{
			WTPart part = (WTPart) wtobject;
			objectCon = part.getContainerReference();
			objectConOid = objectCon.getObjectId().getId();
			System.out.println("objectCon" + objectCon);
		} else if (wtobject instanceof WTDocument)
		{
			WTDocument doc = (WTDocument) wtobject;
			objectCon = doc.getContainerReference();
			objectConOid = objectCon.getObjectId().getId();
		}
		if (containerOid != objectConOid)
		{
			errorContainerMess = "所选对象必须在升级请求对象所在容器" + container.getName() + "中";
			// return errorContainerMess;
		}
		if (verbose)
		{
			System.out.println("end validateObjectContainer" + errorContainerMess);
		}
		return errorContainerMess;
	}

	/**
	 * 所选对象的生命周期状态必须是“正在工作”或“正在修改”
	 */
	private static String validateObjectState(WTObject wtobject) throws Exception
	{
		return null;
	}

	/**
	 * 所选对象必须是第一个大版本
	 */
	private static boolean validateFirstVersion(WTObject wtobject) throws Exception
	{
		if (wtobject instanceof Versioned)
		{
			QueryResult qr = VersionControlHelper.service.allVersionsOf((Versioned) wtobject);
			if (qr.size() == 1)
				return true;
		}

		return false;
	}

	/**
	 * 所选对象没有在其它升级包中存在
	 */
	private static String validateInOtherPromotion(WTObject wtobject) throws Exception
	{
		String objectNumber = "";
		String objectName = "";
		String errorMessage = "";
		// QueryResult qr =
		// PromotionHelper.getPromotionNoticeByPromotable(wtobject);
		QueryResult qr = PromotionHelper.getPromotionNotionByWTObject(wtobject);
		PromotionNotice pn = PromotionHelper.promotionNotice;
		if (pn == null)
		{
			errorMessage = "请刷新页面";
			return errorMessage;
		}

		while (qr.hasMoreElements())
		{
			if (verbose)
			{
				System.out.println("enter validateInOtherPromotion");
			}
			// the object maybe in current promotionNotice
			PromotionNotice tempPN = (PromotionNotice) qr.nextElement();
			String pnNumber = tempPN.getNumber();
			if (pnNumber.equalsIgnoreCase(pn.getNumber()))
			{
				return errorMessage;
			}
			// promotionNotice object's wfprocess finished
			Enumeration processes = WfEngineHelper.service.getAssociatedProcesses(pn, null);
			while (processes.hasMoreElements())
			{
				WfProcess aProcess = (WfProcess) processes.nextElement();
				WfState state = aProcess.getState();
				if (verbose)
				{
					System.out.println("process state == " + state);
				}
				if (state.equals(WfState.CLOSED_COMPLETED_EXECUTED) || state.equals(WfState.CLOSED) || state.equals(WfState.CLOSED_TERMINATED)
						|| state.equals(WfState.CLOSED_COMPLETED) || state.equals(WfState.CLOSED_ABORTED) || state.equals(WfState.CLOSED_COMPLETED_NOT_EXECUTED))
				{
					return errorMessage;
				}
			}
			if (wtobject instanceof WTPart)
			{
				objectNumber = ((WTPart) wtobject).getNumber();
				objectName = ((WTPart) wtobject).getName();
			} else if (wtobject instanceof WTDocument)
			{
				objectNumber = ((WTDocument) wtobject).getNumber();
				objectName = ((WTDocument) wtobject).getName();
			}
			errorMessage = "所选对象" + objectNumber + "(" + objectName + ")在升级包 " + tempPN.getName() + " 中存在";
			return errorMessage;
		}

		if (verbose)
		{
			System.out.println("end validateInOtherPromotion" + "error message of in other promotion *************************" + errorMessage);
		}
		return errorMessage;
	}

	private static ArrayList errorMessage = new ArrayList();

	public static void setErrorList(ArrayList errorList)
	{
		errorMessage = new ArrayList();
		errorMessage = errorList;
	}

	public static ArrayList getErrorList()
	{
		return errorMessage;
	}

	/**
	 * 若选择对象中含有Part则，先得到所有Part的子Part，将Part和Document放入列表中<br>
	 * 取所有Part和Document的相关联对象，DescribeDocument, DependencyDocument,
	 * DescribedByPart, ReferenceDocument.<br>
	 * 调用validatePromotionObject方法进行逐个验证<br>
	 * 将满足条件的对象返回<br>
	 * 
	 * @param wtobjects
	 * @return
	 * @throws WTException
	 * @throws Exception
	 * @throws WTPropertyVetoException
	 */
	public static Hashtable getPromotionPackageItems(WTObject wtobject) throws Exception, WTException
	{
		System.out.println("enter getPromotionPackageItems******************************************");
		Hashtable allObjectAndMessages = new Hashtable();
		ArrayList result = new ArrayList();
		ArrayList<WTObject> resultObjects = new ArrayList<WTObject>();
		// 若为Part对象
		if (wtobject instanceof WTPart)
		{
			if (verbose)
			{
				System.out.println("instanceof WTPart" + wtobject);
			}
			WTPart part = (WTPart) wtobject;

			resultObjects.add(part);
		} else if (wtobject instanceof WTDocument)
		{
			WTDocument doc = (WTDocument) wtobject;

			resultObjects.add(doc);
		}

		if (verbose)
		{
			System.out.println("begin to validate!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		// 开始验证
		for (int i = 0; i < resultObjects.size(); i++)
		{
			WTObject object = resultObjects.get(i);
			if (verbose)
			{
				System.out.println("object!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + object);
			}
			String allMess = "";
			if (verbose)
			{
				System.out.println("allMess  before!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + allMess);
			}
			String errorMess = validateInOtherPromotion(object);
			String errorInStateMess = validateObjectState(object);
			String errorIncontainer = validateObjectContainer(object);
			allMess = errorMess + errorInStateMess + errorIncontainer;
			if (verbose)
			{
				System.out.println("error message ==" + allMess);
			}
			allObjectAndMessages.put(object, allMess);
		}

		return allObjectAndMessages;
	}

	public static void main(String[] args) throws Exception
	{

		String oid = "OR:wt.maturity.PromotionNotice:63824";

	}
}
