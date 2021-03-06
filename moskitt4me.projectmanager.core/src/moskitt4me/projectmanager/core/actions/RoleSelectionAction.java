package moskitt4me.projectmanager.core.actions;

import java.util.ArrayList;
import java.util.List;

import moskitt4me.projectmanager.core.context.Context;
import moskitt4me.projectmanager.core.dialogs.MOSKitt4MESelectionDialog;
import moskitt4me.projectmanager.core.filters.RolesFilter;
import moskitt4me.projectmanager.core.providers.RolesContentProvider;
import moskitt4me.projectmanager.core.providers.RolesLabelProvider;
import moskitt4me.projectmanager.core.views.ProcessView;
import moskitt4me.projectmanager.core.views.ProductExplorerView;
import moskitt4me.projectmanager.methodspecification.MethodElements;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.epf.uma.RoleDescriptor;
import org.eclipse.epf.uma.UmaPackage;
import org.eclipse.epf.uma.impl.RoleDescriptorImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * An action that is shown as a toggle button in the Action Bar of the Process view. When the button
 * is activated, the Process view only displays the tasks that are assigned to the selected role. When the 
 * button is deactivated, no filter is applied. To allow the user to select his/her role, the action opens a
 * Dialog when the button changes from its deactivated to its activated state.
 *
 * @author Mario Cervera
 */
public class RoleSelectionAction extends Action implements IAction {

	/*
	 * Constructor
	 */
	public RoleSelectionAction() {
		
		super("", Action.AS_CHECK_BOX);
	}
	
	@Override
	public void run() {
		
		// Create the Role Selection Dialog
		
		MOSKitt4MESelectionDialog roleSelectionDialog = new MOSKitt4MESelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Role selection", "Select the current role", MethodElements.roles.toArray(),
				SWT.BORDER | SWT.MULTI);

		// Set a Label Provider, Content Provider, and Filter so that the roles are
		// properly displayed within the dialog

		roleSelectionDialog.setLabelProvider(new RolesLabelProvider());
		roleSelectionDialog.setContentProvider(new RolesContentProvider());
		roleSelectionDialog.setFilter(new RolesFilter());
		
		List<EClass> types = new ArrayList<EClass>();
		types.add(UmaPackage.eINSTANCE.getRoleDescriptor());
		roleSelectionDialog.setEnablementTypes(types);
		
		// Show the Dialog
		
		if (roleSelectionDialog.open() == Window.OK) {
			
			Object[] selection = roleSelectionDialog.getResult();

			if (selection != null) { // Update the selected roles
				
				if (selection.length == 1 && selection[0] instanceof NullRole) {
					Context.currentRoles.clear();
				}
				else {
					Context.currentRoles.clear();
					for(Object obj : selection) {
						if(obj instanceof RoleDescriptor) {
							Context.currentRoles.add((RoleDescriptor) obj);
						}
					}
				}

				refreshViews();
			}
		}
		
		if(Context.currentRoles != null && Context.currentRoles.size() > 0) {
			this.setChecked(true);
		}
		else {
			this.setChecked(false);
		}
	}
	
	private void refreshViews() {

		//Product Explorer view

		IViewPart viewPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						ProductExplorerView.ProductExplorerViewId);

		if (viewPart instanceof ProductExplorerView) {

			ProductExplorerView productExplorerView = (ProductExplorerView) viewPart;
			
			productExplorerView.refreshViewer();
			productExplorerView.updateContentDescription();
		}

		//Process view

		IViewPart viewPart2 = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						ProcessView.ProcessViewId);

		if (viewPart2 instanceof ProcessView) {
			
			ProcessView processView = (ProcessView) viewPart2;
			
			processView.refreshViewer();
			processView.updateContentDescription();
		}
	}
	
	public static class NullRole extends RoleDescriptorImpl {
		
	}
}
