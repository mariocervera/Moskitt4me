package moskitt4me.spem2bpmn.actions;

import java.io.File;
import java.util.ArrayList;

import moskitt4me.spem2bpmn.transf.SPEM2BPMNTransformation;
import moskitt4me.spem2bpmn.util.ActivitiDiagramInitializer;
import moskitt4me.spem2bpmn.util.DeliveryProcessValidator;
import moskitt4me.spem2bpmn.util.SPEM2BPMNUtil;

import org.eclipse.epf.library.LibraryService;
import org.eclipse.epf.uma.DeliveryProcess;
import org.eclipse.epf.uma.MethodLibrary;
import org.eclipse.epf.uma.MethodPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * This action (which can be executed by means of the popup menu of the delivery processes)
 * allows the user to invoke a Model-to-Model transformation that obtains a BPMN 2.0 model
 * (in terms of the Activiti Designer) from a specific SPEM 2.0 process. The SPEM 2.0 process
 * must be defined by means of the EPF Composer.
 *
 * @author Mario Cervera
 */
public class OpenBPMN2DiagramAction implements IActionDelegate {
	
	/*
	 * The process that is the input of the transformation
	 */
	private DeliveryProcess selectedDeliveryProcess;
	
	/*
	 * The folder where the output of the transformation will be stored (relative)
	 */
	private String relativeOutputFolder;
	
	/*
	 * The folder where the output of the transformation will be stored (absolute)
	 */
	private String absoluteOutputFolder;
	
	public void run(IAction action) {
		
		try {
			
			File outputFolderFile = new File(absoluteOutputFolder);
			
			if(!outputFolderFile.isDirectory() ||
					
					(outputFolderFile.isDirectory() && outputFolderFile.listFiles().length == 0) ||
					
					(outputFolderFile.isDirectory() && outputFolderFile.listFiles().length == 1
							&& outputFolderFile.listFiles()[0].isDirectory()
							&& outputFolderFile.listFiles()[0].getName().equals(".svn"))) {
				
				boolean result = MessageDialog.openConfirm(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"New Diagram File",
						"No Activiti Designer diagram exists for this process. Do you want to create a new one?");
				
				if(result) {
					
					//Automatically generate the Activiti Designer files (only the process content)

					String errorMessage = DeliveryProcessValidator.checkPrecedenceRelationships(selectedDeliveryProcess);
					
					if(errorMessage == null || errorMessage.equals("")) {
						SPEM2BPMNTransformation t = new SPEM2BPMNTransformation(
								selectedDeliveryProcess, relativeOutputFolder, 1, 1, 1,
								new ArrayList<String>());

						t.generateBPMNModelContent();
						
						//Create graphical elements
						
						File[] files = outputFolderFile.listFiles();
						for(File f : files) {
							if(f.getName().endsWith(".activiti")) {
								ActivitiDiagramInitializer.initializeDiagram("file:" + absoluteOutputFolder + f.getName());
							}
						}
					}
					else {
						MessageDialog.openError(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								"Error", errorMessage);
					}
				}
			}
			else { //Activiti files already exist
				
				File[] files = outputFolderFile.listFiles();
				for(File f : files) {
					if(f.getName().endsWith(".activiti")) {
						ActivitiDiagramInitializer.openActivitiEditor("file:" + absoluteOutputFolder + f.getName());
					}
				}			
			}
		}
		catch(Exception e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Error", e.getMessage() != null ? e.getMessage()
					: "An error occurred during the transformation");
		}
	}

	/*
	 * Listener for the selection changed event. Updates the path of the folder where the output of
	 * the SPEM-to-BPMN transformation will be stored. This path is calculated based on the delivery
	 * process that is selected
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		
		if(selection instanceof StructuredSelection) {
			StructuredSelection sel = (StructuredSelection) selection;
			Object obj = sel.getFirstElement();
			if(obj instanceof DeliveryProcess) {
				selectedDeliveryProcess = (DeliveryProcess) obj;
				
				MethodLibrary lib = LibraryService.getInstance().getCurrentMethodLibrary();
				MethodPlugin plugin = SPEM2BPMNUtil.getMethodPlugin(selectedDeliveryProcess);
				
				if (lib != null && plugin != null) {
					
					relativeOutputFolder = "/" + lib.getName() + "/"
							+ plugin.getName() + "/deliveryprocesses/"
							+ selectedDeliveryProcess.getName() + "/activiti/";

					String libraryLocation = SPEM2BPMNUtil.replaceLastSegment(
							lib.eResource().getURI().toString(), "");
					libraryLocation = SPEM2BPMNUtil.replaceLastSegment(libraryLocation, "");
					libraryLocation = libraryLocation.replaceFirst("file:", "");
					libraryLocation = libraryLocation.substring(0, libraryLocation.length() - 1);
					
					absoluteOutputFolder = libraryLocation + relativeOutputFolder;
				}
			}
		}
	}
	
}
