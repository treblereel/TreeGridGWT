package org.treblereel.gwt.widget.client;

import java.util.Set;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.SelectionModel;

public class ExpandableCell<T extends TreeNode<T>> extends
		AbstractSafeHtmlCell<String> {
	/**
	 * The HTML templates used to render the folder cell.
	 */
	interface NodeImageTemplate extends SafeHtmlTemplates {
		/**
		 * The template for this Cell, which includes styles and a value.
		 * 
		 * @param styles
		 *            the styles to include in the style attribute of the div
		 * @param value
		 *            the safe value. Since the value type is {@link SafeHtml},
		 *            it will not be escaped before including it in the
		 *            template. Alternatively, you could make the value type
		 *            String, in which case the value would be escaped.
		 * @return a {@link SafeHtml} instance
		 */
		@SafeHtmlTemplates.Template("<div name=\"{0}\" style=\"{1}\">{2}</div>")
		SafeHtml cell(String name, SafeStyles styles, SafeHtml value);

		@SafeHtmlTemplates.Template("<div name=\"{0}\" style=\"{1}\">&nbsp;{2}</div>")
		SafeHtml cellNode(String name, SafeStyles styles, SafeHtml value);
	}

	private TreeGrid treeGrid;

	private static org.treblereel.gwt.widget.client.images.Icons icons = GWT
			.create(org.treblereel.gwt.widget.client.images.Icons.class);

	/**
	 * Create a singleton instance of the templates used to render the cell.
	 */
	private static NodeImageTemplate nodeImageTemplate = GWT
			.create(NodeImageTemplate.class);

	private static final SafeHtml ICON_ARROW_CLOSE = makeImage(icons
			.getClose16());
	private static final SafeHtml ICON_ARROW_OPEN = makeImage(icons.getOpen16());
	private static final SafeHtml ICON_FOLDER_CLOSE = makeImage(icons
			.getFolder16());
	private static final SafeHtml ICON_FOLDER_OPEN = makeImage(icons
			.getFolderOpen16());
	private static final SafeHtml ICON_CLEAR = makeImage(icons.getClear());
	private static final SafeHtml ICON_DOCUMENT = makeImage(icons.getDocument());
	private static final SafeHtml ICON_CHECHBOX_TRUE = makeImage(icons
			.getCheckboxTrue());
	private static final SafeHtml ICON_CHECHBOX_FALSE = makeImage(icons
			.getCheckboxFalse());
	private static final SafeHtml ICON_CHECHBOX_CHILDS_SELECTED = makeImage(icons
			.getCheckboxChildsSelected());

	/**
	 * Make icons available as SafeHtml
	 * 
	 * @param resource
	 * @return
	 */
	private static SafeHtml makeImage(ImageResource resource) {
		AbstractImagePrototype proto = AbstractImagePrototype.create(resource);
		return proto.getSafeHtml();
	}

	T currentRow;

	private SafeHtml rendered;

	private SafeStyles imageWidthStyle;

	public ExpandableCell(SafeHtmlRenderer<String> renderer,
			Set<String> consumedEvents) {
		super(renderer, consumedEvents);
	}

	public <T extends TreeNode<T>> ExpandableCell(TreeGrid<T> treeGrid) {
		super(SimpleSafeHtmlRenderer.getInstance(), "click", "keydown");
		this.treeGrid = treeGrid;
	}

	private void createClearIcon(SafeHtmlBuilder sb, SafeStyles imgStyle,
			int times) {
		imageWidthStyle = SafeStylesUtils
				.fromTrustedString("float:left;width:16.0px;");
		for (int i = 0; i < times; i++) {
			rendered = nodeImageTemplate.cell("ICON_ARROW__CLOSE",
					imageWidthStyle, ICON_CLEAR);
			sb.append(rendered);
		}
	}

	/**
	 * @param sb
	 * @param imgStyle
	 */
	@SuppressWarnings("unchecked")
	private void createCheckBoxImg(SafeHtmlBuilder sb, SafeStyles imgStyle) {

		imageWidthStyle = SafeStylesUtils
				.fromTrustedString("float:left;width:16.0px;margin-right:2px;");
		SelectionModel<T> sm = (SelectionModel<T>) treeGrid.getSelectionModel();
		boolean selected = sm.isSelected(currentRow);
		if (selected) {
			if (currentRow.getChildren().size() > 0) {
				if (currentRow.isChidlrenSelected()) {
					rendered = nodeImageTemplate.cell("ICON_CHECKBOX",
							imageWidthStyle, ICON_CHECHBOX_CHILDS_SELECTED);
				} else {
					rendered = nodeImageTemplate.cell("ICON_CHECKBOX",
							imageWidthStyle, ICON_CHECHBOX_TRUE);
				}
			} else {
				rendered = nodeImageTemplate.cell("ICON_CHECKBOX",
						imageWidthStyle, ICON_CHECHBOX_TRUE);
			}
		} else {
			rendered = nodeImageTemplate.cell("ICON_CHECKBOX", imageWidthStyle,
					ICON_CHECHBOX_FALSE);
		}
		sb.append(rendered);
	}

	private void createClosedArrowAndFolderSafeHtml(SafeHtmlBuilder sb,
			SafeStyles imgStyle) {
		SafeHtml rendered;
		if (currentRow.getChildren().size() != 0) {
			rendered = nodeImageTemplate.cell("ICON_ARROW__CLOSE", imgStyle,
					ICON_ARROW_CLOSE);
		} else {
			SafeStyles imageWidthStyle = SafeStylesUtils
					.fromTrustedString("float:left;width:16.0px;");
			rendered = nodeImageTemplate.cell("ICON_ARROW__CLOSE",
					imageWidthStyle, ICON_CLEAR);
		}
		sb.append(rendered);
		if (treeGrid.isSelectable()) {
			createCheckBoxImg(sb, imgStyle);
		}
		rendered = nodeImageTemplate.cell("ICON_FOLDER__CLOSE", imgStyle,
				ICON_FOLDER_CLOSE);
		sb.append(rendered);
	}

	private void createLeafSafeHtml(SafeHtmlBuilder sb, SafeStyles imgStyle) {
		rendered = nodeImageTemplate.cell("ICON_DOCUMENT", imgStyle,
				ICON_DOCUMENT);
		sb.append(rendered);
	}

	private void createOpenArrowAndFolderSafeHtml(SafeHtmlBuilder sb,
			SafeStyles imgStyle) {
		SafeHtml rendered;
		if (currentRow.getChildren().size() != 0) {
			rendered = nodeImageTemplate.cell("ICON_ARROW__OPEN", imgStyle,
					ICON_ARROW_OPEN);
			sb.append(rendered);
		}
		if (treeGrid.isSelectable()) {
			createCheckBoxImg(sb, imgStyle);
		}
		rendered = nodeImageTemplate.cell("ICON_FOLDER__OPEN", imgStyle,
				ICON_FOLDER_OPEN);
		sb.append(rendered);
	}

	/**
	 * Intern action
	 * 
	 * @param value
	 *            selected value
	 * @param valueUpdater
	 *            value updater or the custom value update to be called
	 */
	private void doAction(String value, ValueUpdater<String> valueUpdater) {
		// Trigger a value updater. In this case, the value doesn't actually
		// change, but we use a ValueUpdater to let the app know that a value
		// was clicked.
		if (valueUpdater != null)
			valueUpdater.update(value);
	}

	/**
	 * Called when an event occurs in a rendered instance of this Cell. The
	 * parent element refers to the element that contains the rendered cell, NOT
	 * to the outermost element that the Cell rendered.
	 */
	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
			Element parent, String value, NativeEvent event,
			com.google.gwt.cell.client.ValueUpdater<String> valueUpdater) {

		// Let AbstractCell handle the keydown event.
		super.onBrowserEvent(context, parent, value, event, valueUpdater);

		// Handle the click event.
		if ("click".equals(event.getType())) {

			// Ignore clicks that occur outside of the outermost element.
			EventTarget eventTarget = event.getEventTarget();

			if (parent.isOrHasChild(Element.as(eventTarget))) {
				Element el = Element.as(eventTarget);
				if (el.getNodeName().equalsIgnoreCase("IMG")) {
					doAction(el.getParentElement().getAttribute("name"),
							valueUpdater);
				}
			}
		}
	}

	/**
	 * onEnterKeyDown is called when the user presses the ENTER key will the
	 * Cell is selected. You are not required to override this method, but its a
	 * common convention that allows your cell to respond to key events.
	 */
	@Override
	protected void onEnterKeyDown(Context context, Element parent,
			String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		doAction(value, valueUpdater);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void render(com.google.gwt.cell.client.Cell.Context context,
			SafeHtml data, SafeHtmlBuilder sb) {
		currentRow = (T) treeGrid.getDataStore().getList()
				.get(context.getIndex());
		/*
		 * Always do a null check on the value. Cell widgets can pass null to
		 * cells if the underlying data contains a null, or if the data arrives
		 * out of order.
		 */
		if (data == null) {
			return;
		}

		// generate the image cell
		SafeStyles imgStyle = SafeStylesUtils
				.fromTrustedString("float:left;cursor:hand;cursor:pointer;");
		SafeStyles valueStyle = SafeStylesUtils
				.fromTrustedString("float:left;");
		SafeHtml rendered;
		if (currentRow.getChildren().size() > 0) {
			createClearIcon(sb, imgStyle, currentRow.getLevel());
			if (!currentRow.isExpand()) {
				createClosedArrowAndFolderSafeHtml(sb, imgStyle);
			} else {
				createOpenArrowAndFolderSafeHtml(sb, imgStyle);
			}
		} else {
			createClearIcon(sb, imgStyle, currentRow.getLevel() + 1);
			if (treeGrid.isSelectable()) {
				createCheckBoxImg(sb, imgStyle);
			}
			createLeafSafeHtml(sb, imgStyle);
		}
		// generate the value cell
		rendered = nodeImageTemplate.cellNode("EXPANDABLE_VALUE", valueStyle,
				SafeHtmlUtils.fromString(data.asString()));
		sb.append(rendered);
	}
}
