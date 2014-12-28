package org.treblereel.gwt.widget.client.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Icons extends ClientBundle {
	
	@Source("folder-open-16.png")
	ImageResource getFolderOpen16();
	
	@Source("folder-16.png")
	ImageResource getFolder16();
	
	@Source("open.png")
	ImageResource getOpen16();
	
	@Source("close.png")
	ImageResource getClose16();
	
	@Source("clear.cache.gif")
	ImageResource getClear();
	
	@Source("txt.png")
	ImageResource getDocument();
	
	
	@Source("checkbox_false.png")
	ImageResource getCheckboxFalse();
	
	@Source("checkbox_true.png")
	ImageResource getCheckboxTrue();
	
	@Source("checkbox_childs_selected.png")
	ImageResource getCheckboxChildsSelected();
	
}
