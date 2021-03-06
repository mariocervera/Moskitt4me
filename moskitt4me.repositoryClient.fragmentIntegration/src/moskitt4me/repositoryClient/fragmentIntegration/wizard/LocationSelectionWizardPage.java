package moskitt4me.repositoryClient.fragmentIntegration.wizard;


import java.util.ArrayList;

import moskitt4me.repositoryClient.fragmentIntegration.providers.ElementsLabelProvider;
import moskitt4me.repositoryClient.fragmentIntegration.providers.ProcessFoldersProvider;
import moskitt4me.repositoryClient.fragmentIntegration.util.ContentItem;
import moskitt4me.repositoryClient.fragmentIntegration.util.IntegrationData;

import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.epf.uma.CapabilityPattern;
import org.eclipse.epf.uma.MethodPackage;
import org.eclipse.epf.uma.MethodPlugin;
import org.eclipse.epf.uma.ProcessComponent;
import org.eclipse.epf.uma.ProcessPackage;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;


public class LocationSelectionWizardPage extends WizardPage implements Listener{

	private int DEFAULT_DIALOG_WIDTH = 400;
	private int DEFAULT_DIALOG_HEIGHT = 300;
	private int MIN_DIALOG_WIDTH = 300;
	private int MIN_DIALOG_HEIGHT = 300;

	private Tree contentElementTree;
	private TreeViewer contentElementTreeViewer;

	private  ITreeContentProvider contentProvider;
	private ILabelProvider labelProvider;
	
	
	private Object[] plugins;
	private IntegrationData data;
	public ArrayList<ContentItem> items;
	

	public LocationSelectionWizardPage(Shell parentShell, Object[] objects, IntegrationData data, ArrayList<ContentItem> items ) {
		
		super("Process Package Selection");
		
		setTitle("Process Package Selection");
		setMessage("Select the process package where the proccess fragment will be stored");

		this.contentProvider = new ProcessFoldersProvider(new ResourceItemProviderAdapterFactory());
		this.labelProvider = new ElementsLabelProvider();
		this.plugins = objects;
		this.data = data;
		this.items = items; 
		
	}
	
	protected void configureShell(Shell shell) {
		shell.setMinimumSize(MIN_DIALOG_WIDTH, MIN_DIALOG_HEIGHT);
	}

	public void createControl(Composite parent) {
		
		Composite dialogComposite = new Composite(parent, SWT.NULL);
		
		GridLayout dialogLayout = new GridLayout();
		dialogLayout.marginWidth = 10;
		dialogLayout.marginHeight = 10;
		GridData dialogLayoutData = new GridData(GridData.FILL_BOTH);
		dialogLayoutData.widthHint = DEFAULT_DIALOG_WIDTH;
		dialogLayoutData.heightHint = DEFAULT_DIALOG_HEIGHT;
		dialogComposite.setLayout(dialogLayout);
		dialogComposite.setLayoutData(dialogLayoutData);

		contentElementTree = new Tree(dialogComposite, SWT.BORDER | SWT.SINGLE);
		contentElementTree.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		contentElementTreeViewer = new TreeViewer(contentElementTree);
		contentElementTreeViewer.setContentProvider( this.contentProvider);
		contentElementTreeViewer.setLabelProvider(this.labelProvider);
		contentElementTreeViewer.setInput(plugins);
		
		final Label error = new Label(dialogComposite, SWT.NONE);
		error.setLayoutData(new GridData(GridData.FILL_BOTH));
		error.setText("Some patterns already exists in this library");
		error.setVisible(false);
		
		contentElementTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				
				if(event.getSelection() instanceof StructuredSelection) {
					error.setVisible(false);
					Object selectedElement = 
						((StructuredSelection)event.getSelection()).getFirstElement();
					if(selectedElement instanceof ProcessPackage){
						data.setFolder(selectedElement);
						setPageComplete(true);
					}
					else {
						data.setFolder(null);
						setPageComplete(false);
					}
					if(selectedElement instanceof ProcessPackage && existingPatterns(selectedElement)>0){
						String text ="";
						if(data.getExistingPattern().size()==1) text+= "The pattern ";
						else text += "The patterns ";
						
						for(CapabilityPattern pattern : data.getExistingPattern()){
							text += pattern.getName()+ ", ";
						}
						
						text = text.substring(0, text.length() - 2);
						
						if(data.getExistingPattern().size()==1)
							text += " already exists in the selected package. It will not be duplicated";
						else text += " already exist in the selected package. They will not be duplicated";
						
						error.setText(text);
						error.setVisible(true);
						
					}else{
						error.setVisible(false);
					}
				}
			}
		});
		
		setControl(dialogComposite);
		
	}
	
	public ProcessPackage getSelectedPackage() {
		ISelection selection = contentElementTreeViewer.getSelection();
		if(selection instanceof StructuredSelection) {
			StructuredSelection sel = (StructuredSelection)selection;
			if(sel.getFirstElement() instanceof ProcessPackage) {
				return (ProcessPackage)sel.getFirstElement();
			}
		}
		
		return null;
	}
	
	public int existingPatterns(Object selectedElement){
		
		data.setExistingPattern(new ArrayList<CapabilityPattern>());
		int existe= 0;
		for(ContentItem item : items){
			ProcessPackage pack = null;
			if(selectedElement instanceof MethodPlugin)
				pack = (ProcessPackage) ((MethodPlugin)selectedElement).getMethodPackages().get(0).getChildPackages().get(2);
			else
				pack = (ProcessPackage) selectedElement;
			
			
			for(MethodPackage elem : pack.getChildPackages()){
				if(elem instanceof ProcessComponent){
					if(elem.getName().equals(item.getAttributes().get("name"))) {
						data.getExistingPattern().add( (CapabilityPattern) ((ProcessComponent) elem).getProcess());
						existe++;
					}
						
				}
				else if(elem instanceof ProcessPackage){
					existePattern(item, elem);
				}
			}
		}
		
		return existe;
	}

	private boolean existePattern(ContentItem item, MethodPackage elem ) {
		// TODO Auto-generated method stub
		for(MethodPackage elem2 : elem.getChildPackages()){
			if(elem2 instanceof ProcessPackage){
				existePattern(item, elem2);
			}
			else if(elem2 instanceof ProcessComponent){
				if(elem2.getName().equals(item.getAttributes().get("Name"))) return true;
			}
		}
		return false;
	}

	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		
	}
	

}
	
