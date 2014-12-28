package org.treblereel.gwt.widget.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.view.client.ProvidesKey;

public class TreeNode<T> implements SafeHtml {
	private static final long serialVersionUID = -7873482601959843088L;
	private List<T> children;
	protected T parent;
	private int level;
	private boolean isExpand,isChildrenSelected;

	public TreeNode() {
		children = new ArrayList<T>();
	}
	
	public String asString() {
		return null;
	}

	public List<T> getChildren() {
		return children;
	}

	public int getLevel() {
		return level;
	}

	public T getParent() {
		return parent;
	}

	public boolean isExpand() {
		return isExpand;
	}

	public boolean isLeaf() {
		if (this.children == null || this.children.size() == 0) {
			return true;
		}
		return false;
	}

	public void setChildren(List<T> children) {
		this.children = children;
	}

	public void setExpand(boolean expand) {
		this.isExpand = expand;
	}

	public void setLevel(int l) {
		this.level = l;
	}

	@SuppressWarnings("unchecked")
	public void setParent(T parent) {
		this.parent = parent;
		if (parent == null) {
			this.level = 0;
		} else {
			setLevel(((TreeNode<T>) parent).getLevel() + 1);
		}

	}

	boolean isChidlrenSelected() {
		return isChildrenSelected;
	}

	void setChildrenSelected(boolean isChilrenSelected) {
		this.isChildrenSelected = isChilrenSelected;
	}
}