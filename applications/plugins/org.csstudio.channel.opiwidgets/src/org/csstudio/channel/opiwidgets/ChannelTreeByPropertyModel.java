package org.csstudio.channel.opiwidgets;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class ChannelTreeByPropertyModel extends AbstractWidgetModel {
	
	public final String ID = "org.csstudio.channel.opiwidgets.ChannelTreeByProperty"; //$NON-NLS-1$
	
	public static final String CHANNEL_QUERY = "channel_query"; //$NON-NLS-1$	
	public static final String TREE_PROPERTIES = "tree_properties"; //$NON-NLS-1$	
	public static final String PV_FOR_SELECTION = "pv_for_selection"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(CHANNEL_QUERY, "Channel query", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(TREE_PROPERTIES, "Tree properties", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(PV_FOR_SELECTION, "PV for selection", WidgetPropertyCategory.Basic, ""));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public String getChannelQuery() {
		return getCastedPropertyValue(CHANNEL_QUERY);
	}
	
	public List<String> getTreeProperties() {
		String list = getCastedPropertyValue(TREE_PROPERTIES);
		String[] tokens = list.split(",");
		List<String> properties = new ArrayList<String>();
		for (String token : tokens) {
			properties.add(token.trim());
		}
		return properties;
	}
	
	public String getPvForSelection() {
		return getCastedPropertyValue(PV_FOR_SELECTION);
	}

}
