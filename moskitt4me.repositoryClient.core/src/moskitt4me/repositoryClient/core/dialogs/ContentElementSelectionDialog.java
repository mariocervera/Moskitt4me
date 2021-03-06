package moskitt4me.repositoryClient.core.dialogs;

import org.eclipse.epf.uma.ContentElement;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * A dialog that allows the user to select a content element from the method model. 
 * This dialog is used by the Integrate Technical Fragment action.
 *
 * @author Mario Cervera
 */
public class ContentElementSelectionDialog extends SelectionDialog {

	// Dialog size
	
	private int DEFAULT_DIALOG_WIDTH = 400;
	private int DEFAULT_DIALOG_HEIGHT = 300;
	private int MIN_DIALOG_WIDTH = 300;
	private int MIN_DIALOG_HEIGHT = 300;

	// The label and content providers
	
	private ILabelProvider labelProvider;
	private ITreeContentProvider contentProvider;
	
	// The main graphical widget (a tree viewer)
	
	private Tree contentElementTree;
	private TreeViewer contentElementTreeViewer;
	
	private Object[] objects;
	
	/*
	 * Constructor
	 */
	public ContentElementSelectionDialog(Shell parentShell, Object[] objects) {
		
		super(parentShell);
		
		setTitle("Task/Work Product Selection");
		setMessage("Select the task or work product the fragment will be associated with");
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		this.objects = objects;
	}
	
	protected void configureShell(Shell shell) {
		shell.setMinimumSize(MIN_DIALOG_WIDTH, MIN_DIALOG_HEIGHT);

		super.configureShell(shell);
	}

	/*
	 * This method arranges the graphical components of the Dialog according to a Grid Layout
	 */
	protected Control createDialogArea(Composite parent) {

		Composite dialogComposite = (Composite) super.createDialogArea(parent);

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
		contentElementTreeViewer.setLabelProvider(this.labelProvider);
		contentElementTreeViewer.setContentProvider(this.contentProvider);
		contentElementTreeViewer.setSorter(null);
		contentElementTreeViewer.setInput(this.objects);
		
		contentElementTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				
				if(event.getSelection() instanceof StructuredSelection) {
					Object selectedElement = 
						((StructuredSelection)event.getSelection()).getFirstElement();
					
					if(selectedElement instanceof ContentElement) {
						getOkButton().setEnabled(true);
					}
					else getOkButton().setEnabled(false);
				}
			}
		});
		
		return dialogComposite;
	}
	
	public void setLabelProvider(ILabelProvider provider) {
		this.labelProvider = provider;
	}
	
	public void setContentProvider(ITreeContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}
	
	public TreeViewer getViewer() {
		return this.contentElementTreeViewer;
	}
	
	@Override
	protected void okPressed() {
	
		if(contentElementTreeViewer.getSelection() instanceof StructuredSelection) {
			StructuredSelection selection = (StructuredSelection) contentElementTreeViewer.getSelection();
			setResult(selection.toList());
		}
		
		super.okPressed();
	}

}
