package org.treblereel.gwt.widget.client;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.treblereel.gwt.widget.client.styles.TreeGridResources;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;

/**
 * 
 * @author chani
 * 
 */

public class TreeGrid<T extends TreeNode<T>> extends VerticalPanel {
	private class TreeGridWrapper<T extends TreeNode<T>> extends CellTable<T> {

		private ListDataProvider<T> dataProvider;
		private static final int DEFAULT_PAGESIZE = 15;

		public TreeGridWrapper(Resources resources) {
			super(DEFAULT_PAGESIZE, resources);
			init();
			setStyleName("treeGrid");
		}

		@Override
		public void addColumn(Column<T, ?> column) {
			super.addColumn(column);
		}

		public void addExpandableColumn(ExpandableColumn<T> expandableColumn) {
			insertColumn(0, expandableColumn);
			setFieldUpdater(expandableColumn);
			expandableColumn.setCellStyleNames("treeGrid-ExpandableColumn");
		}

		private void addExpandableColumnAction(int index, T object, String value) {
			if (!object.isLeaf() && !object.isExpand()) {
				expandNode(index, object);
			} else if (!object.isLeaf() && object.isExpand()) {
				collapseNode(object);
			}
		}

		private void collapseNode(T object) {
			object.setExpand(false);
			for (T child : object.getChildren()) {
				if (child.getChildren().size() > 0) {
					collapseNode(child);
				}
				this.dataProvider.getList().remove(child);
			}
			this.setPageSize(this.dataProvider.getList().size());
		}

		private void expandNode(int index, T object) {
			if (!object.isExpand() && !object.isLeaf()) {
				object.setExpand(true);
				int i = index;
				for (T child : object.getChildren()) {
					i++;
					this.dataProvider.getList().add(i, child);
				}
				this.setPageSize(this.dataProvider.getList().size());
			}
		}

		public ListDataProvider<T> getDataStore() {
			return this.dataProvider;
		}

		private void init() {
			initDataStore();
		}

		private void initDataStore() {
			dataProvider = new ListDataProvider<T>();
			dataProvider.addDataDisplay(this);
		}

		public void setSelectable(boolean action) {
			treeGrid.setSelectable(action);
		}

		public void setColumnLines(boolean result) {
			if (result) {
				addStyleName("cellBorder");
			} else {
				removeStyleName("cellBorder");
			}
		}

		private void setRowSelected(T object, boolean selection) {
			getSelectionModel().setSelected(object, selection);
			if (getSelectionModel() instanceof MultiSelectionModel) {
				if (object.getChildren().size() > 0) {
					object.setChildrenSelected(selection);
					for (T o : object.getChildren()) {
						setRowSelected(o, selection);
					}
				}
			}
		}

		private void setFieldUpdater(final ExpandableColumn<T> expandableColumn) {
			expandableColumn.setFieldUpdater(new FieldUpdater<T, String>() {
				public void update(int index, T object, String value) {
					if (value.equals("ICON_CHECKBOX") && isSelectable()) {
						setRowSelected(object,
								!getSelectionModel().isSelected(object));
						checkParentChildrenSelected(object.getParent());
						treeGrid.redraw();
					} else {
						addExpandableColumnAction(index, object, value);
					}
				}
			});
		}

		private void checkParentChildrenSelected(T object) {
			int selectedCounter = 0;
			boolean preventToBecomeisChidlrenSelected = false;
			for (T neighbor : object.getChildren()) {
				if (getSelectionModel().isSelected(neighbor) == true)
					selectedCounter++;
					if(neighbor.getChildren().size() > 0 && !neighbor.isChidlrenSelected()){
						preventToBecomeisChidlrenSelected = true;
					}
			}
			if (selectedCounter == object.getChildren().size()) {
				object.setChildrenSelected(true);
				getSelectionModel().setSelected(object, true);
			} else if (selectedCounter < object.getChildren().size()
					&& selectedCounter != 0) {
				object.setChildrenSelected(false);
				getSelectionModel().setSelected(object, true);
			} else if (selectedCounter == 0) {
				object.setChildrenSelected(false);
				getSelectionModel().setSelected(object, false);
			}
			if(preventToBecomeisChidlrenSelected){
				object.setChildrenSelected(false);
			}
			if (object.getLevel() != 0)
				checkParentChildrenSelected(object.getParent());
		}

		public void expandAll(boolean action) {
			for (T o : dataProvider.getList()) {
				if (action == false) {
					collapseNode(o);
				} else {
					expandAllAction(treeGrid.getVisibleItems().indexOf(o), o);
				}
			}
		}

		private void expandAllAction(int index, T object) {
			expandNode(index, object);
			if (object.getChildren().size() > 0) {
				for (T child : object.getChildren()) {
					expandAllAction(treeGrid.getVisibleItems().indexOf(child),
							child);
				}
			}
		}
	}

	private ScrollPanel scrollPanel;
	private TreeGridWrapper<T> treeGrid;
	private CellTable<T> header;
	private TextColumn<T> offSetColumn;
	private LinkedHashMap<Integer, Column<T, ?>> columns = new LinkedHashMap<Integer, Column<T, ?>>();
	private LinkedHashMap<Column<T, ?>, String> columnNames = new LinkedHashMap<Column<T, ?>, String>();
	private boolean selectable = false;
	private TextHeader offSetHeader;
	private Resources resources;

	public TreeGrid() {
		init();
	}

	public TreeGrid(Resources resources) {
		this.resources = resources;
		init();
	}

	public void addColumn(Column<T, ?> column, String name) {
		columns.put(columns.size() + 1, column);
		columnNames.put(column, name);
		refreshColumns();
	}

	public void addExpandableColumn(Column<T, ?> column, String name) {
		columns.put(0, column);
		columnNames.put(column, name);
		refreshColumns();
	}

	/**
	 * Add offSet column on TreeGrid resize if scroll appears, remove if scroll
	 * is not exist and do nothing if offSet column already exist.
	 */
	private void addOffserColumn(boolean action) {
		if (action) {
			if (header.getColumnIndex(offSetColumn) == -1) {
				header.setColumnWidth(offSetColumn,
						calculateOffsetColumnWidth() + "px");
				header.addColumn(offSetColumn, offSetHeader);
			}
		} else {
			if (header.getColumnIndex(offSetColumn) != -1)
				header.removeColumn(offSetColumn);
		}
	}

	private int calculateOffsetColumnWidth() {
		int treeGridWidth = treeGrid.getOffsetWidth();
		int scrollPanelWidth = scrollPanel.getOffsetWidth();
		return scrollPanelWidth - treeGridWidth;
	}

	public void collapseAll() {
		treeGrid.expandAll(false);
	}

	public void expandAll() {
		treeGrid.expandAll(true);
	}

	private void calculatePanelSize() {
		int hSize = getBody().getOffsetHeight() - header.getOffsetHeight();
		if (hSize <= treeGrid.getBodyHeight()) {
			addOffserColumn(true);
		} else {
			addOffserColumn(false);
		}
	}

	private void createOffsetColumn() {
		offSetColumn = new TextColumn<T>() {
			@Override
			public String getValue(T object) {
				return null;
			}
		};
		offSetHeader = new TextHeader("");
		offSetHeader.setHeaderStyleNames("treeGridOffsetHeader");
	}

	public ListDataProvider<T> getDataStore() {
		return treeGrid.dataProvider;
	}

	private void init() {
		initHeader();
		initTreeGrid();
	}

	private void initHeader() {
		header = new CellTable<T>();
		header.setVisible(false);
		header.setRowCount(0, true);
		add(header);
		createOffsetColumn();
	}

	private void initTreeGrid() {
		if (resources == null) {
			TreeGridResources.INSTANCE.cellTableStyle().ensureInjected();
			treeGrid = new TreeGridWrapper<T>(TreeGridResources.INSTANCE);
		} else {
			treeGrid = new TreeGridWrapper<T>(resources);
		}
		treeGrid.addRowCountChangeHandler(createRowCountChangeHandler());
		scrollPanel = new ScrollPanel();
		scrollPanel.add(treeGrid);
		this.add(scrollPanel);
	}

	public void setAlwaysShowScrollBars(boolean show) {
		scrollPanel.setAlwaysShowScrollBars(show);
	}

	private Handler createRowCountChangeHandler() {
		return new RowCountChangeEvent.Handler() {
			public void onRowCountChange(RowCountChangeEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						calculatePanelSize();
					}
				});
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void refreshColumns() {
		for (int i = 0; i < treeGrid.getColumnCount(); i++) {
			treeGrid.removeColumn(i);
			header.removeColumn(i);

		}
		for (Entry<Integer, Column<T, ?>> kv : columns.entrySet()) {
			if (kv.getValue() instanceof ExpandableColumn) {
				treeGrid.addExpandableColumn((ExpandableColumn<T>) kv
						.getValue());
				header.addColumn(kv.getValue(), columnNames.get(kv.getValue()));
			} else {
				treeGrid.addColumn(kv.getValue());
				header.addColumn(kv.getValue(), columnNames.get(kv.getValue()));
			}
		}
	}

	public void removeColumn(Column<T, ?> column) {
		columns.remove(column);
		columnNames.remove(column);
		refreshColumns();
	}

	public void setColumnLines(boolean result) {
		treeGrid.setColumnLines(result);
	}

	public void setColumnWidth(Column<T, ?> column, String width) {
		treeGrid.setColumnWidth(column, width);
		header.setColumnWidth(column, width);
	}

	public com.google.gwt.event.shared.HandlerRegistration addAttachHandler(
			com.google.gwt.event.logical.shared.AttachEvent.Handler handler) {
		return treeGrid.addAttachHandler(handler);
	}

	public void setColumnWidth(Integer columnIndex, String width) {
		treeGrid.setColumnWidth(columnIndex, width);
		header.setColumnWidth(columnIndex, width);
	}

	public void setHeight(String height) {
		super.setHeight(height);
		scrollPanel.setHeight(height);
	}

	public void setSelectable(boolean action) {
		this.selectable = action;
	}

	public boolean isSelectable() {
		return this.selectable;
	}

	public void redraw() {
		treeGrid.redraw();
	}

	public void setSelectionMode() {

	}

	public void setWidth(String width) {
		super.setWidth(width);
		scrollPanel.setWidth(width);
		Integer widthInteger = Integer.parseInt(width.replace("px", ""));

		header.setWidth(String.valueOf((widthInteger - scrollPanel
				.getOffsetWidth())) + "px");
	}

	public void showHeader(Boolean show) {
		header.setVisible(show);
		header.setStylePrimaryName("treeGrid treeGridHeaderBody");
	}

	public void showScroll(boolean show) {
		scrollPanel.setAlwaysShowScrollBars(show ? true : false);
	}

	public void setSelectionModel(SelectionModel<T> sm) {
		treeGrid.setSelectionModel(sm, null);
	}

	@SuppressWarnings("unchecked")
	public SelectionModel<T> getSelectionModel() {
		return (SelectionModel<T>) treeGrid.getSelectionModel();
	}
}
