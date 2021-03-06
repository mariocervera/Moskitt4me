package moskitt4me.projectmanager.core.pages;

import java.io.File;
import java.util.Enumeration;

import moskitt4me.projectmanager.core.providers.DeliveryProcessesContentProvider;
import moskitt4me.projectmanager.core.providers.ProcessLabelProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.epf.uma.DeliveryProcess;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.osgi.framework.Bundle;

/**
 * The only page of the New MOSKitt4ME Project Wizard. It allows the user to specify the project
 * name and also to select the process that will be associated to the project
 *
 * @author Mario Cervera
 */
public class NewMOSKitt4MEProjectPage extends WizardPage {
	
	// Project name and associated process
	
	private String projectName;
	private DeliveryProcess selectedProcess;
	
	// Widgets
	
	private Label projectNameLabel;
	private Text projectNameText;
	
	private Label processesLabel;
	private TreeViewer processesViewer;
	
	//Listeners
	
	private Listener projectNameModifyListener = new Listener() {
		public void handleEvent(Event e) {
			
			projectName = projectNameText.getText();
			
			// Check whether the input data is correct
			// In this case, set the page as completed
			
			setPageComplete(validatePage());
		}
	};
	
	/*
	 * Constructor
	 */
	public NewMOSKitt4MEProjectPage(String pageName) {
		
		super(pageName);
	}

	// Getters

	public String getProjectName() {
		return projectName;
	}
	
	public DeliveryProcess getSelectedProcess() {
		return selectedProcess;
	}
	
	/*
	 * Create the graphical elements (GridLayout)
	 */
	public void createControl(Composite parent) {

		Composite newMethodProjectComposite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout(1, true);
		newMethodProjectComposite.setLayout(layout);
		newMethodProjectComposite.setLayoutData(new GridData());
		
		createProjectNameGroup(newMethodProjectComposite);
		
		createProcessesGroup(newMethodProjectComposite);
		
		setPageComplete(false);
		setControl(newMethodProjectComposite);
	}

	/*
	 * This method creates the part of the GUI that allows the user to set the project name
	 */
	private void createProjectNameGroup(Composite parent) {
		
		Composite projectNameGroup = new Composite(parent, SWT.NONE);
	        GridLayout layout = new GridLayout();
	        layout.numColumns = 2;
	        projectNameGroup.setLayout(layout);
	        projectNameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	
	        projectNameLabel = new Label(projectNameGroup, SWT.NONE);
	        projectNameLabel.setText("Project Name:");
	
	        projectNameText = new Text(projectNameGroup, SWT.BORDER);
	        GridData data = new GridData(GridData.FILL_HORIZONTAL);
	        projectNameText.setLayoutData(data);
	        projectNameText.addListener(SWT.Modify, projectNameModifyListener);
	}
	
	/*
	 * This method creates the part of the GUI that allows the user to select the process
	 * that will be associated to the project
	 */
	private void createProcessesGroup(Composite parent) {
		
		Composite processesGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		processesGroup.setLayout(layout);
		processesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		processesLabel = new Label(processesGroup, SWT.NONE);
		processesLabel.setText("Delivery Processes:");
        
	        Tree tree = new Tree(parent, SWT.BORDER | SWT.SINGLE);
	        GridData data = new GridData(GridData.FILL_BOTH);
	        tree.setLayoutData(data);
	        
	        processesViewer = new TreeViewer(tree);
	        processesViewer.setContentProvider(new DeliveryProcessesContentProvider());
	        processesViewer.setLabelProvider(new ProcessLabelProvider());
	        processesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				
				if(!event.getSelection().isEmpty()) {
					
					DeliveryProcess dp = null;
					
					if(event.getSelection() instanceof StructuredSelection) {
						Object elem = ((StructuredSelection) event.getSelection()).getFirstElement();
						if(elem instanceof DeliveryProcess) {
							dp = (DeliveryProcess) elem;
						}
					}
					
					selectedProcess = dp;
				}
				
				setPageComplete(validatePage());
			}
        });
        processesViewer.setInput("random string"); //Just to invoke content provider
	}
	
	/*
	 * The page is valid when the user has set both the project name and process
	 */
	protected boolean validatePage() {

		return (validateName() && validateProcess());
	}
	
	/*
	 * This method ensures that the user specifies a project name and also that this
	 * name is valid
	 */
	private boolean validateName() {
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
		String pName = projectNameText.getText();
		
		if (pName == null || pName.equals("")) {
			
			setErrorMessage("Project name must be specified");
		    	return false;
		 }
		
		IStatus nameStatus = workspace.validateName(pName, IResource.PROJECT);
		
		if (!nameStatus.isOK()) {
			
		    setErrorMessage(nameStatus.getMessage());
		    return false;
		}
		
		IProject handle = ResourcesPlugin.getWorkspace().getRoot().getProject(pName);
		
		if (handle.exists()) {
			
		    setErrorMessage("A project with that name already exists in the workspace");
		    return false;
		}
		
		setErrorMessage(null);
		setMessage(null);
		
		return true;
	}
	
	/*
	 * This method ensures that the user selects a valid process for the project to be created
	 */
	private boolean validateProcess() {
		
		if(selectedProcess == null) {
			setErrorMessage("A process must be selected");
			return false;
		}
		else if(!hasBPMNmodel(selectedProcess)) {
			setErrorMessage("The selected process does not have an associated BPMN 2.0 model");
			return false;
		}
		else {
			setErrorMessage(null);
		        setMessage(null);
		        return true;
		}
	}
	
	/*
	 * For a SPEM 2.0 process to be executable, it must have an equivalent (and executable)
	 * BPMN 2.0 representation associated to it
	 */
	private boolean hasBPMNmodel(DeliveryProcess dp) {
		
		try {
			URI resourceUri = dp.eResource().getURI();
			URI segmentedURI = resourceUri.trimSegments(1);
			URI activitiFolderURI = segmentedURI.appendSegment("activiti");

			if(!activitiFolderURI.isPlatformPlugin()) {
				File f = new File(activitiFolderURI.toString().replaceFirst("file:", ""));
				
				return (f.isDirectory() && f.listFiles().length > 0);
			}
			else {
				String pluginId = activitiFolderURI.segment(1);
				Bundle b = Platform.getBundle(pluginId);
				
				String relativePath = activitiFolderURI.toString().replace("platform:/plugin/" + pluginId, "");
				
				Enumeration _enum = b.getEntryPaths(relativePath);
				
				return (_enum.hasMoreElements());
			}
		}
		catch(Exception e) {
			return false;
		}
	}
}
