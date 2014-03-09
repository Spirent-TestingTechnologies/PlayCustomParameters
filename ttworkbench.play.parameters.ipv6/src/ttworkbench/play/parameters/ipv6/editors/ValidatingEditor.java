package ttworkbench.play.parameters.ipv6.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;

import ttworkbench.play.parameters.ipv6.components.IMessagePanel;
import ttworkbench.play.parameters.ipv6.components.MessagePanel;

import com.testingtech.ttworkbench.ttman.parameters.api.IConfiguration;
import com.testingtech.ttworkbench.ttman.parameters.api.IMessageHandler;
import com.testingtech.ttworkbench.ttman.parameters.api.IParameter;
import com.testingtech.ttworkbench.ttman.parameters.api.IParameterValidator;
import com.testingtech.ttworkbench.ttman.parameters.validation.ErrorKind;
import com.testingtech.ttworkbench.ttman.parameters.validation.ValidationResult;

public abstract class ValidatingEditor<T> extends AbstractEditor<T> implements IMessageHandler {


	private MessagePanel messagePanel = null;
	private static final ScheduledExecutorService validationWorker = Executors.newSingleThreadScheduledExecutor();
	private static final ScheduledExecutorService validationMessageWorker = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> validationTaskFuture;
	private ScheduledFuture<?> validationMessageTaskFuture;	
	
	public ValidatingEditor( String theTitle, String theDescription) {
		super( theTitle, theDescription);
	}
	
	
	public IMessagePanel getMessagePanel() {
		return messagePanel;
	}

	
	
	protected Layout extractLayoutFromParams( final Layout theDefaultLayout, final Object ...theParams) {
		Layout layout = theDefaultLayout; 
		for (int i = 0; i < theParams.length; i++) {
			if ( theParams[i] instanceof Layout) {
		    layout = (Layout) theParams[i];
		    theParams[i] = null; // remove handled param
		    break;
			}
		}
		return layout;
	}

	protected Object[] extractLayoutDataFromParams( final Object theDefaultLayoutData, final int theCountOfCells, final Object[] theParams) {
		if ( theCountOfCells < 1) {
			Object[] defaultResult = {theDefaultLayoutData};
			return defaultResult;
		}
		
		Object[] layoutData = new Object[theCountOfCells];
		layoutData[0] = theDefaultLayoutData;
		
		int i = 0;
		for (int j = 0; j < theParams.length; j++) {
			if ( theParams[j] instanceof GridData || 
				 	 theParams[j] instanceof RowData ||	  
					 theParams[j] instanceof FormData) {
				layoutData[i] = theParams[j];
				theParams[j] = null; // mark param as handled
				i++;
				if ( i >= theCountOfCells)
					break;
			}
		} 
		
		if ( i < theCountOfCells) {
			for ( int j = i; j < layoutData.length; j++) {
				layoutData[j] = layoutData[0];
			}
		}
		
		return layoutData;
	}
	
  
	
	/**
	 * Validates the current parameter value in the main thread.
	 * @return
	 */
	protected List<ValidationResult> validate() {
		IConfiguration configuration = getConfiguration();
		IParameter<?> parameter = getParameter();
		List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
		if ( configuration != null) {			
			Set<IParameterValidator> validators = configuration.getValidators( parameter);
			for (IParameterValidator validator : validators) {
				if ( !Thread.currentThread().isInterrupted())
				  validationResults.addAll( validator.validate( parameter));
			}
		}
		return validationResults;
	}


  /**
   * Validates the current parameter value delayed in another thread.
   * TODO remove parameter theDelayInSeconds -> put into behavior class
   * @param theDelayInSeconds 
   */
	protected void validateDelayed( final int theDelayInSeconds) {
		if ( validationTaskFuture != null) 
			validationTaskFuture.cancel( true);
		if ( validationMessageTaskFuture != null) 
			validationMessageTaskFuture.cancel( true);
		

		Runnable validationMessageTask = new Runnable() {
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						getMessagePanel().putTaggedMessage( "run_validator", "Validation process in progress.", ErrorKind.info);
					}
				});
			}
		};
		validationMessageTaskFuture = validationMessageWorker.schedule( validationMessageTask, theDelayInSeconds +1, TimeUnit.SECONDS);	
		
		Runnable validationTask = new Runnable() {
			@Override
			public void run() {
				
				for (int i = 0; i < 800000; i++) {
					System.out.println( "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
				}
				
				validate();	
				
				validationMessageTaskFuture.cancel( false);
				
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						getMessagePanel().putTaggedMessage( "run_validator", "Validation finished.", ErrorKind.success);
					}
				});
			}
		};
		validationTaskFuture = validationWorker.schedule( validationTask, theDelayInSeconds, TimeUnit.SECONDS);
	}



	
	
	protected abstract void createEditRow(Composite theContainer, Object[] theLayoutData, Object[] theParams);
	
	private void createMessageRow(Composite theParent) {
		// TODO Auto-generated method stub
		messagePanel = new MessagePanel( theParent, SWT.NONE);
		messagePanel.setFlashDurationInSeconds( 2);
		messagePanel.setLayoutData( new GridData(SWT.FILL, SWT.TOP, true, true, 0, 0));
		messagePanel.enableBeep();
		messagePanel.setChangedListener( new Listener() {
			@Override
			public void handleEvent(Event theArg0) {
				updateControl();
			}
		});
	}
	
	
	@Override
	public final Composite createControl(Composite theParent, Object... theParams) {
	
	  Layout editLayout = extractLayoutFromParams( new RowLayout( SWT.HORIZONTAL), theParams);
	  Object[] editLayoutData = extractLayoutDataFromParams( new RowData(), 3, theParams);
		
		Composite container = new Composite( theParent, SWT.None);
		container.setLayout( new GridLayout( 1, true));
	  // TODO check layout data. Is compatible? to Flowlayout or Filllayout 
		container.setLayoutData( new GridData(SWT.FILL, SWT.TOP, true, false, 0, 0));
		
		createMessageRow( container);
		
		Composite editRowContainer = new Composite( container, SWT.None);
		editRowContainer.setLayout( editLayout);
		editRowContainer.setLayoutData( new GridData(SWT.FILL, SWT.TOP, true, false, 0, 0));
		createEditRow( editRowContainer, editLayoutData, theParams);
		
		container.setSize( container.computeSize( SWT.DEFAULT, SWT.DEFAULT));
		container.layout();
		
		return container;
	}
	

	@Override
	public synchronized void report( final IParameterValidator theValidator, final List<ValidationResult> theValidationResults,
			final IParameter theParameter) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String senderId = String.format( "%s@%s", theValidator.getClass().getName(), theValidator.hashCode());
				messagePanel.beginUpdateForSender( senderId);
				for (ValidationResult validationResult : theValidationResults) {
					if ( validationResult.isTagged()) {
						messagePanel.putTaggedMessage( validationResult.getTag(), validationResult.getErrorMessage(), validationResult.getErrorKind());
					} else {
					  messagePanel.addUntaggedMessage( validationResult.getErrorMessage(), validationResult.getErrorKind());					
					}
				}
				messagePanel.endUpdate();
			}
		});
	}
	

}
