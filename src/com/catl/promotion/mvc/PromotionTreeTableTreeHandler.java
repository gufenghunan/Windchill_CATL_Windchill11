package com.catl.promotion.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.catl.common.util.EpmUtil;
import com.catl.process.bean.ProcessStatusBean;
import com.catl.process.util.WFReviewList;
import com.catl.promotion.PromotionHelper;
import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistentReference;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.log4j.LogR;
import wt.maturity.MaturityException;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartPromotionNoticeConfigSpec;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.struct.StructHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

/**
 * 构建升级请求对象树形结构展示
 * 展示升级请求对象中 按关联关系展示树形结构（ConfigSpec为升级请求范围）
 * ConfigSpec cf = WTPartPromotionNoticeConfigSpec.newWTPartPromotionNoticeConfigSpec(pn);
 * @author plm
 *
 */
public class PromotionTreeTableTreeHandler extends TreeHandlerAdapter
{
	private static final Logger logger = LogR.getLogger(PromotionTreeTableTreeHandler.class.getName());

	static int MAXLEVEL = 20;
	PromotionNotice pn;
	HashSet<Promotable> rootSet = new HashSet<Promotable>();
	/**
	 * Object升级请求中的对象
	 * 升级请求对象中关联的EPMDocuemnt WTDocuemnt WTPart
	 */
	Map<Object, List> childrenMap = new HashMap<Object, List>();
	ArrayList<Promotable> needDel = new ArrayList<Promotable>();

	public PromotionTreeTableTreeHandler()
	{
	}

	public PromotionTreeTableTreeHandler(ComponentParams params) throws WTException, WTPropertyVetoException
	{
		NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		String oid = (String) cb.getRequest().getParameter("oid");
		logger.info("PromotionTreeTableTreeHandler oid is:" + oid);
		ReferenceFactory rf = new ReferenceFactory();
		WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
		PersistentReference obj = wi.getPrimaryBusinessObject();
		if (obj.getObject() instanceof PromotionNotice)
		{
			pn = (PromotionNotice) obj.getObject();
			System.out.println("pn is: " + pn);
			filterPN();
		}
	}

	public void filterPN() throws MaturityException, WTException, WTPropertyVetoException
	{
		QueryResult qr = PromotionHelper.getPromotable(pn);

		while (qr.hasMoreElements())
		{
			Promotable obj = (Promotable) qr.nextElement();
			System.out.println("pn list: " + obj);
			rootSet.add(obj);
		}
		for (Promotable pm : rootSet)
		{
			if (pm instanceof WTPart)
			{
				WTPart parent = (WTPart) pm;
				System.out.println("parent:" + parent.getNumber());
				ConfigSpec cf = WTPartPromotionNoticeConfigSpec.newWTPartPromotionNoticeConfigSpec(pn);

				QueryResult queryResult = WTPartHelper.service.getUsesWTParts(parent, cf);
				if (queryResult.size() == 0)
				{
					addChildrenMap(parent, null, pn);
					continue;
				}
				while (queryResult.hasMoreElements())
				{
					Persistable ps[] = (Persistable[]) queryResult.nextElement();
					if (ps[1] instanceof WTPart)
					{
						WTPart child = (WTPart) ps[1];
						System.out.println("----child:" + child.getNumber());
						addChildrenMap(parent, child, pn);
						needDel.add(child);
					}
				}
			} else if (pm instanceof EPMDocument)
			{
				EPMDocument parent = (EPMDocument) pm;
				System.out.println(parent.getNumber());
				ConfigSpec cf = WTPartPromotionNoticeConfigSpec.newWTPartPromotionNoticeConfigSpec(pn);
				QueryResult queryResult = StructHelper.service.navigateUsesToIteration(parent, cf);
				if (queryResult.size() == 0)
				{
					addChildrenMap(parent, null, pn);
					continue;
				}
				while (queryResult.hasMoreElements())
				{
					EPMDocument child = (EPMDocument) queryResult.nextElement();
					System.out.println(child.getNumber());
					addChildrenMap(parent, child, pn);
					needDel.add(child);
				}
			}
		}
		System.out.println(rootSet.size());
		rootSet.removeAll(needDel);
		System.out.println("end!" + rootSet.size());
	}
	/**
	 * 添加升级请求中对象关联EPM DOC对象到needDel中
	 * @param parent
	 * @param child
	 * @param pn
	 * @throws WTException
	 */
	private void addChildrenMap(Promotable parent, Promotable child, PromotionNotice pn) throws WTException
	{
	    LinkedList<Promotable> children = new LinkedList<Promotable>();
		// add children
		// add EPMDocument
		addEPMDocument(parent, children);
		// add refDocument
		addRefDesDocuments(parent, children);
		if (children != null && child != null)
            children.add(child);
		if (childrenMap.containsKey(parent))
		{
		    LinkedList<Promotable> childrenSet = (LinkedList<Promotable>) childrenMap.get(parent);
			childrenSet.addAll(children);
			childrenMap.remove(parent);
			childrenMap.put(parent, childrenSet);
			System.out.println("childrenSet:" + childrenSet);
		} else if (children != null)
		{
		    LinkedList<Promotable> childrenSet = new LinkedList<Promotable>(children);
			childrenMap.put(parent, childrenSet);
			System.out.println("childrenSet:" + childrenSet);
		}
	}

	private void addRefDesDocuments(Promotable parent, LinkedList<Promotable> children) throws WTException
	{
		if (parent instanceof WTPart)
		{
			QueryResult refResult = WTPartHelper.service.getReferencesWTDocumentMasters((WTPart) parent);
			while (refResult.hasMoreElements())
			{
				Object tempObj = refResult.nextElement();
				if (tempObj instanceof WTDocumentMaster)
				{
					WTDocumentMaster docMaster = (WTDocumentMaster) tempObj;
					QueryResult qr2 = VersionControlHelper.service.allIterationsOf(docMaster);
					if (qr2.hasMoreElements())
					{
						WTDocument doc = (WTDocument) qr2.nextElement();
						if (rootSet.contains(doc))
						{
							if (!children.contains(doc))
							{
								children.add(doc);
								needDel.add(doc);
							}
						}
					}
				}
			}

			QueryResult desResult = WTPartHelper.service.getDescribedByWTDocuments((WTPart) parent);
			while (desResult.hasMoreElements())
			{
				Object tempObj = desResult.nextElement();
				if (tempObj instanceof WTDocument)
				{
					WTDocument doc = (WTDocument) tempObj;
					if (rootSet.contains(doc))
					{
						if (!children.contains(doc))
						{
							children.add(doc);
							needDel.add(doc);
						}
					}
				}
			}
		}
	}

	private void addEPMDocument(Promotable parent, LinkedList<Promotable> children) throws WTException
	{
		if (parent instanceof WTPart)
		{
			QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments((WTPart) parent);
			while (qr.hasMoreElements())
			{
				Object obj = (Object) qr.nextElement();
				if (obj instanceof EPMDocument)
				{
					EPMDocument epmdoc = (EPMDocument) obj;
					System.out.println("epmdoc:" + epmdoc.getNumber());
					if (rootSet.contains(epmdoc))
					{
						if (!children.contains(epmdoc))
						{
							children.add(epmdoc);
							needDel.add(epmdoc);
						}
					}

					Collection<EPMDocument> drawingCollection = EpmUtil.getDrawings(epmdoc);
					if (!drawingCollection.isEmpty())
					{
						Iterator it = drawingCollection.iterator();
						while (it.hasNext())
						{
							EPMDocument drawingdoc = (EPMDocument) it.next();
							if (rootSet.contains(drawingdoc))
							{
								if (!children.contains(drawingdoc))
								{
									children.add(drawingdoc);
									needDel.add(drawingdoc);
								}
							}
						}
					}
				}
			}
		} else if (parent instanceof EPMDocument)
		{
			Collection<EPMDocument> drawingCollection = EpmUtil.getDrawings((EPMDocument) parent);
			if (!drawingCollection.isEmpty())
			{
				Iterator it = drawingCollection.iterator();
				while (it.hasNext())
				{
					EPMDocument drawingdoc = (EPMDocument) it.next();
					if (rootSet.contains(drawingdoc))
					{
						if (!children.contains(drawingdoc))
						{
							children.add(drawingdoc);
							needDel.add(drawingdoc);
						}
					}
				}
			}
		}
	}

	@Override
	public Map<Object, List> getNodes(List parents) throws WTException
	{
		Map<Object, List> result = new HashMap<Object, List>();
		if (pn == null)
			return result;
		// for (Promotable pm : rootSet)
		// {
		// if (pm == null)
		// continue;
		// if (pm instanceof WTPart)
		// {
		// WTPart parent = (WTPart) pm;
		// getTreeTable(parent);
		// } else if (pm instanceof EPMDocument)
		// {
		// EPMDocument parent = (EPMDocument) pm;
		// getTreeTable(parent);
		// } else if (pm instanceof WTDocument)
		// {
		// WTDocument parent = (WTDocument) pm;
		// getTreeTable(parent);
		// }
		// }
		for (Object obj : parents)
		{
			result.put(obj, childrenMap.get(obj));
		}
		return result;
	}

	private String getTreeTable(Promotable parent)
	{
		StringBuffer result = new StringBuffer();
		ArrayList<Promotable> children = (ArrayList<Promotable>) childrenMap.get(parent);
		if (children != null)
		{
			printChildren(children, 1);
		}
		return result.toString();
	}

	private String printChildren(ArrayList<Promotable> children, int i)
	{
		StringBuffer result = new StringBuffer();
		for (Promotable pm : children)
		{
			ArrayList<Promotable> childrenSet = (ArrayList<Promotable>) childrenMap.get(pm);
			if (childrenSet != null)
			{
				for (Promotable child : childrenSet)
				{
					printChild(child, i + 1, result);
				}
			}
		}
		return result.toString();
	}

	private void printChild(Promotable pm, int i, StringBuffer row)
	{
		if (i >= MAXLEVEL)
			return;
		ArrayList<Promotable> childrenSet = (ArrayList<Promotable>) childrenMap.get(pm);
		if (childrenSet != null)
		{
			printChild(pm, i + 1, row);
		} else
			return;
	}

	@Override
	public List<Object> getRootNodes() throws WTException
	{
		if (pn == null)
		{
			return new ArrayList();
		}
		return new ArrayList(rootSet);
	}

}
