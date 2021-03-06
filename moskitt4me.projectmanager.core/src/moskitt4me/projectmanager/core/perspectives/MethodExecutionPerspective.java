package moskitt4me.projectmanager.core.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewRegistry;

/**
 * The Method Execution perspective. When active, the MOSKitt4ME workbench will show the views that
 * comprise the Project Manager Component: the Resource Explorer, the Process view, the Product Explorer,
 * and the Help view
 * 
 * @author Mario Cervera
 */
public class MethodExecutionPerspective implements IPerspectiveFactory {

	public static final String ID = "moskitt4me.projectmanager.core.perspective";
	
	/*
	 * This method creates the views that comprise the Method Execution Perspective and establishes
	 * how they are arranged in the MOSKitt4ME GUI
	 */
	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.2f, editorArea);
		
		String explorerViewId = "";
		if(existsView("es.cv.gvcase.ide.navigator.resourceView")) {
			explorerViewId = "es.cv.gvcase.ide.navigator.resourceView";
		}
		else {
			explorerViewId = "org.eclipse.ui.navigator.ProjectExplorer";
		}
		
		left.addView(explorerViewId);
		left.addPlaceholder(explorerViewId);
		
		IFolderLayout leftMiddle = layout.createFolder("leftBottom", IPageLayout.BOTTOM,
				0.5f, "left");
		leftMiddle.addView("moskitt4me.projectmanager.core.views.processView");
		leftMiddle.addPlaceholder("moskitt4me.projectmanager.core.views.processView");
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM,
				0.7f, editorArea);
		bottom.addView("moskitt4me.projectmanager.core.views.productExplorerView");
		bottom.addPlaceholder("moskitt4me.projectmanager.core.views.productExplorerView");
		
		IFolderLayout bottomRight = layout.createFolder("right", IPageLayout.RIGHT,
				0.5f, "right");
		bottomRight.addView("org.eclipse.help.ui.HelpView");
		bottomRight.addPlaceholder("org.eclipse.help.ui.HelpView");
	}
	
	/*
	 * This method checks whether a given view exists in the MOSKitt4ME platform
	 */
	private boolean existsView(String viewId) {

		IViewRegistry registry = PlatformUI.getWorkbench().getViewRegistry();

		if(registry != null && registry.find(viewId) != null) {
			return true;
		}

		return false;
	}
}
