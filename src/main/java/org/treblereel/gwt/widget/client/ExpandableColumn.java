package org.treblereel.gwt.widget.client;

import com.google.gwt.user.cellview.client.Column;

public abstract class ExpandableColumn<T> extends Column<T, String>{
	@SuppressWarnings({ "rawtypes", "unchecked", "hiding" })
	public <T extends TreeNode<T>> ExpandableColumn(TreeGrid<T> tree) {
		super(new ExpandableCell(tree));
	}
}
