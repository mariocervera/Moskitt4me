package moskitt4me.repositoryClient.core.actions;

import java.io.IOException;

import moskitt4me.repositoryClient.core.providers.MethodFragmentItemProvider;
import moskitt4me.repositoryClient.core.util.RepositoryClientUtil;
import moskitt4me.repositoryClient.core.views.RepositoriesView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;

/**
 * This class provides a superclass for two different actions: Integrate Conceptual Fragment and
 * Integrate Technical Fragment.
 * 
 * @author Mario Cervera
 */
public abstract class IntegrateFragmentAction extends Action implements IAction {

	protected IntegrateFragmentAction() {
		
		setEnabled(false);
	}
	
	@Override
	public void run() {

		RepositoriesView repositoriesView = RepositoryClientUtil.getRepositoriesView();
		
		if(repositoriesView != null) {

			// Get the element that is selected in the Repositories view
			
			if (repositoriesView.getCommonViewer().getSelection() instanceof StructuredSelection) {
				
				Object selection = ((StructuredSelection) repositoriesView
						.getCommonViewer().getSelection()).getFirstElement();
				
				// If the selected element is a method fragment (either a conceptual or a technical
				// fragment), integrate it.
				
				if (selection instanceof MethodFragmentItemProvider) {
					
					MethodFragmentItemProvider fragment = (MethodFragmentItemProvider) selection;
					
					int result = 0;
					try {
						result = performFragmentIntegration(fragment);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if (result == 0) {
						MessageDialog.openInformation(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								"Fragment integration",
								"The fragment integration has been successfully performed");
					}
					else if (result < 0) {
						MessageDialog.openError(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								"Integration failed",
								"Something went wrong when performing the fragment integration");
					}
				}
			}
		}
	}
	
	/*
	 * This method is abstract because it must be implemented differently dependeing on whether the fragment is a
	 * conceptual fragment or a technical fragment
	 */
	protected abstract int performFragmentIntegration(MethodFragmentItemProvider fragment) throws IOException;
}
