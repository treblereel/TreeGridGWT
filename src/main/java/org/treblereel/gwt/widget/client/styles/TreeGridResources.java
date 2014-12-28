package org.treblereel.gwt.widget.client.styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Resources;

/**
 * 
 * 
 * @author dtikhomirov
 * 
 */
public interface TreeGridResources extends Resources {

	public TreeGridResources INSTANCE = GWT.create(TreeGridResources.class);

	@Source("TreeGridStyle.css")
	CellTable.Style cellTableStyle();


}
