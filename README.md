TreeGridGWT
===========

TreeGrid widget for GWT framework



Example: http://opensheet.org:8080/treegrid_example-1.0-SNAPSHOT/Opensheet.html

Usage: 
To build a library execute following commands in the termonal:
> mvn clean package

Jar will appear in the target directory under project folder. To use it within GWT project add it to dependencies and put :
  <inherits name='org.treblereel.gwt.widget.TreeGrid' />
to the project.gwt.xml file of your project.


Entry point example:
<code>
public class Opensheet implements EntryPoint {

  public void onModuleLoad() {

    final TreeGrid<Place> table = new TreeGrid<Place>();

    TextColumn<Place> nameColumn = new TextColumn<Place>() {
      @Override
      public String getValue(Place object) {
        return object.getName();
      }
    };

    table.addColumn(nameColumn, "index");

    ExpandableColumn<Place> imagesColumn = new ExpandableColumn<Place>(
        table) {
      @Override
      public String getValue(Place object) {
        return object.getName();
      }
    };
    table.addExpandableColumn(imagesColumn, "City name");

    List<Place> list = TreeUtils.getTreeNodeList();
    for (Place t : list) {
      table.getDataStore().getList().add(t);
    }

    final MultiSelectionModel<Place> multiSelectionModel = new MultiSelectionModel<Place>(
        Place.KEY_PROVIDER);

    final SingleSelectionModel<Place> singleSelectionModel = new SingleSelectionModel<Place>(
        Place.KEY_PROVIDER);

    table.setColumnLines(true);
    table.setColumnWidth(0, "300px");
    table.setColumnWidth(1, "485px");
    table.showHeader(true);
    table.setWidth("785px");
    table.setHeight("300px");
    table.showScroll(false);
    table.setSelectable(true);
    table.setAlwaysShowScrollBars(false);
    table.setSelectionModel(multiSelectionModel);

    RootPanel.get().add(table);

    Button b = new Button("Show me selected");
    b.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        for (Place p : multiSelectionModel.getSelectedSet()) {
          System.out.println(p.getName());
        }
      }
    });

    RootPanel.get().add(b);
    Button collapseAll = new Button("Collapse All");
    collapseAll.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        table.collapseAll();
      }
    });
    RootPanel.get().add(collapseAll);

    Button expandAll = new Button("Expand All");
    expandAll.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        table.expandAll();
      }
    });
    RootPanel.get().add(expandAll);

  }

}
</code>


Node example:

<code>
public class Place extends TreeNode<Place> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String code;
	private String comment;

	private Place() {
		super();
	}

	public Place(Long id, String name, Place parent) {
		super();
		this.setId(id);
		this.setName(name);
		this.setParent(parent);
	}

	/**
	 * The key provider that provides the unique ID of a contact.
	 */
	public static final ProvidesKey<Place> KEY_PROVIDER = new ProvidesKey<Place>() {
		public Object getKey(Place item) {
			return item == null ? null : item.getId();
		}

	};

	public String asString() {
		return "id " + id + " " + " name" + name + " comment " + comment;
	}
	
	.... setters and getters omitted

</code>

TreeGrid hierarchy  utility...

<code>
public static List<Place> getTreeNodeList() {

		List<Place> list= new ArrayList<Place>();

		Place p = new Place(random.nextLong(), "Moscow", null);
		
		Place pp = new Place(random.nextLong(), "Dublin", p);
		p.getChildren().add(pp);
		pp = new Place(random.nextLong(), "Berlin", p);
		p.getChildren().add(pp);
		pp = new Place(random.nextLong(), "Glasgow", p);
		p.getChildren().add(pp);
		
		list.add(p);
		p = new Place(random.nextLong(), "Frankfurt", null);
		list.add(p);
		p = new Place(random.nextLong(), "London", null);
		list.add(p);
		p = new Place(random.nextLong(), "Washington", null);

		Place c = new Place(random.nextLong(), "Warsaw", p);
		p.getChildren().add(c);
		
		c = new Place(random.nextLong(), "San francisco", p);

		
		Place cb = new Place(random.nextLong(), "Beijing", c);
		c.getChildren().add(cb);
		cb = new Place(random.nextLong(), "Saigon", c);
		c.getChildren().add(cb);
		cb = new Place(random.nextLong(), "Phuket", c);
		c.getChildren().add(cb);
		cb = new Place(random.nextLong(), "Los angeles", c);
		c.getChildren().add(cb);
		
		p.getChildren().add(c);
		c = new Place(random.nextLong(), "Toronto", p);

		Place ca = new Place(random.nextLong(), "Vienna", c);
		c.getChildren().add(ca);
		ca = new Place(random.nextLong(), "Caracas", c);
		c.getChildren().add(ca);
		ca = new Place(random.nextLong(), "Sydney", c);
		c.getChildren().add(ca);
		ca = new Place(random.nextLong(), "Johannesburg", c);
		c.getChildren().add(ca);
		

		p.getChildren().add(c);
		c = new Place(random.nextLong(), "Newcastle", p);
		p.getChildren().add(c);
		list.add(p);
		p = new Place(random.nextLong(), "Dubai", null);
		list.add(p);

		return list;
	}
</code>






