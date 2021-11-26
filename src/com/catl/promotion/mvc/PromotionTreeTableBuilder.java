package com.catl.promotion.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ptc.core.components.descriptor.DescriptorConstants.TableTreeProperties;
import com.ptc.jca.mvc.components.JcaTreeConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ComponentResultProcessor;
import com.ptc.mvc.components.ComponentResultProcessor;
import com.ptc.mvc.components.TreeConfig;
import com.ptc.mvc.components.TreeDataBuilderAsync;
import com.ptc.mvc.components.TreeDataBuilderAsync;
import com.ptc.mvc.components.TreeNode;
import com.ptc.mvc.components.ds.DataSourceMode;

import wt.util.WTException;

@ComponentBuilder("com.catl.promotion.mvc.PromotionTreeTableBuilder")
public class PromotionTreeTableBuilder extends AbstractComponentBuilder implements TreeDataBuilderAsync
{
	private PromotionTreeTableTreeHandler treeHandler = null;

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params) throws Exception
	{
		return new PromotionTreeTableTreeHandler(params);
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException
	{
		ComponentConfigFactory factory = getComponentConfigFactory();
		TreeConfig tree = factory.newTreeConfig();
		((JcaTreeConfig) tree).setDataSourceMode(DataSourceMode.ASYNCHRONOUS);

		//tree.addComponent(factory.newColumnConfig("type_icon", true));

		ColumnConfig number = factory.newColumnConfig("number", true);
		number.setDataUtilityId("NumberAndIconDataUtility");
		tree.addComponent(number);
		tree.setNodeColumn("number");
		
		ColumnConfig name = factory.newColumnConfig("name", true);
		tree.addComponent(name);
		
		ColumnConfig versionColumn = factory.newColumnConfig("version", false);
		tree.addComponent(versionColumn);
		
		tree.addComponent(factory.newColumnConfig("creator", true));
		
		ColumnConfig specification = factory.newColumnConfig("specification", true);
		specification.setLabel("规格");
		tree.addComponent(specification);
		
		tree.setId("com.catl.promotion.PromotionTreeTableBuilder");
		tree.setExpansionLevel(TableTreeProperties.FULL_EXPAND);

		return tree;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void buildNodeData(Object node, ComponentResultProcessor resultProcessor) throws Exception
	{
		// NmCommandBean commandBean = (NmCommandBean) ((JcaComponentParams)
		// resultProcessor
		// .getParams()).getHelperBean().getNmCommandBean();
		if (node == TreeNode.RootNode)
		{ // special case for root nodes
			treeHandler = new PromotionTreeTableTreeHandler(resultProcessor.getParams());
			resultProcessor.addElements(treeHandler.getRootNodes());
		} else
		{
			List nodeList = new ArrayList();
			nodeList.add(node);
			Map<Object, List> map = treeHandler.getNodes(nodeList);
			Set keySet = map.keySet();
			for (Object key : keySet)
			{
				List childList = map.get(key);
				if (childList != null && childList.size() > 0)
				{
					resultProcessor.addElements(map.get(key));
				}
			}
		}
	}

}
