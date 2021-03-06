package moskitt4me.repositoryClient.core.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.epf.uma.ContentElement;
import org.eclipse.epf.uma.ContentPackage;
import org.eclipse.epf.uma.MethodPackage;
import org.eclipse.epf.uma.MethodPlugin;
import org.eclipse.epf.uma.Task;
import org.eclipse.epf.uma.WorkProduct;

/**
 * Provides content for the Tree Viewer of the Content Element Selection Dialog.
 *
 * @author Mario Cervera
 */
public class ContentElementsContentProvider extends AdapterFactoryContentProvider {
	
	/*
	 * Constructor
	 */
	public ContentElementsContentProvider(AdapterFactory adapterFactory) {
		
		super(adapterFactory);
	}
	
	/*
	 * This method returns the root-level elements of the Tree viewer
	 */
	public Object[] getElements(Object inputElement) {
		
		if(inputElement instanceof Object[]) {
			Object[] elements = (Object[]) inputElement;
			return elements;
		}
		
		return super.getElements(inputElement);
	}
	
	/*
	 * Calculates whether a given element has any children
	 */
	public boolean hasChildren(Object element) {
		
		if (element instanceof MethodPlugin || element instanceof MethodPackage
				|| element instanceof TasksItemProvider
				|| element instanceof WorkProductsItemProvider) {
			
			return true;
		}
		
		return false;
	}
	
	/*
	 * This method returns the children of a given element
	 */
	public Object[] getChildren(Object parentElement) {
		
		List<Object> result = new ArrayList<Object>();
		
		if(parentElement instanceof  MethodPlugin) {
			
			MethodPlugin plugin = (MethodPlugin) parentElement;
			
			// The children of Method Plug-ins are Method Packages
			
			for(MethodPackage mp : plugin.getMethodPackages()) {
				if(mp.getName().equals("Content") && mp instanceof ContentPackage) {
					for(MethodPackage mp2 : mp.getChildPackages()) {
						if(mp2.getName().equals("CoreContent") && mp2 instanceof ContentPackage) {
							result.addAll(mp2.getChildPackages());
						}
					}
				}
			}
		}
		else if(parentElement instanceof ContentPackage) {
			
			// The children of a Content Package are its subpackages and also the "Tasks" and
			// "Products" folders
			
			ContentPackage cpackage = (ContentPackage) parentElement;
			
			result.addAll(cpackage.getChildPackages());
			
			result.add(new TasksItemProvider(null, cpackage));
			result.add(new WorkProductsItemProvider(null, cpackage));
		}
		else if(parentElement instanceof TasksItemProvider) {
			
			// The children of the "Tasks" folder are the method tasks
			
			TasksItemProvider tip = (TasksItemProvider) parentElement;
			
			if(tip.getAssociatedPackage() instanceof ContentPackage) {
				ContentPackage cp = (ContentPackage) tip.getAssociatedPackage();
				
				for(ContentElement element : cp.getContentElements()) {
					if(element instanceof Task) {
						result.add(element);
					}
				}
			}
		}
		else if(parentElement instanceof WorkProductsItemProvider) {
			
			// The children of the "Products" folder are the method products
			
			WorkProductsItemProvider wpip = (WorkProductsItemProvider) parentElement;
			
			if(wpip.getAssociatedPackage() instanceof ContentPackage) {
				ContentPackage cp = (ContentPackage) wpip.getAssociatedPackage();
				
				for(ContentElement element : cp.getContentElements()) {
					if(element instanceof WorkProduct) {
						result.add(element);
					}
				}
			}
		}
		
		return result.toArray();
	}

}
