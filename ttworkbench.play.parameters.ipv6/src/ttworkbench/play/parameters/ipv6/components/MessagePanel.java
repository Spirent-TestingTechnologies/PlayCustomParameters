package ttworkbench.play.parameters.ipv6.components;

import java.awt.Toolkit;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ttworkbench.play.parameters.ipv6.customize.DefaultMessageViewLookAndBehaviour;
import ttworkbench.play.parameters.ipv6.customize.IMessageViewLookAndBehaviour;


import com.testingtech.ttworkbench.ttman.parameters.validation.ErrorKind;

public class MessagePanel extends Composite implements IMessageView {
	
	
	private class MessageLine extends Composite {

		private String message;
		private ErrorKind errorKind;
		private Label label;
		
		
		private MessageLine( final String theMessage, final ErrorKind theErrorKind) {
			super( MessagePanel.this, SWT.NONE);
			this.setLayout( new FillLayout());
			setLayoutData( new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
			label = new Label( this, SWT.NONE);
			FontData[] fontData = label.getFont().getFontData();
			fontData[0].setHeight( fontData[0].getHeight() -1);
			label.setFont( lookAndBehaviour.getMessageFont( theErrorKind));//new Font( Display.getCurrent(), fontData[0]));
			
			setMessage( theMessage, theErrorKind);
		}

		public void setMessage( String theMessage, ErrorKind theErrorKind) {
	    // set content
			this.message = theMessage;
	    label.setText( message);

	    
	    // set look
	    this.errorKind = theErrorKind;
	    Color foregroundColor = lookAndBehaviour.getMessageForeground( errorKind);
	    Color backgroundColor = lookAndBehaviour.getMessageBackground( errorKind);
	    label.setForeground( foregroundColor);
	    label.setBackground( backgroundColor);
	    
	    // set behaviour
	    switch ( errorKind) {
				case error:
				case warning:
					beep();
					break;
			}
		}
		
		private void beep() {
			if ( MessagePanel.this.lookAndBehaviour.isBeepEnabled())
			  Toolkit.getDefaultToolkit().beep();
		}

		ErrorKind getErrorKind() {
			return errorKind;
		}
		
		String getMessage() {
			return message;
		}
		
	}
	
	
	
	private class MessageBlock {

		private Map<String, MessageLine> taggedMessageLines = new HashMap<String,MessageLine>();
		private Set<String> agedMessageTags = new HashSet<String>();
		private List<MessageLine> untaggedMessageLines = new ArrayList<MessageLine>();
		
		private MessageBlock() {
		}
		
		public synchronized void putTaggedMessage( final String theTag, final String theMessage, final ErrorKind theErrorKind) {
			MessageLine oldMessageLine = taggedMessageLines.remove( theTag);
			
			// release old message control first
			if ( oldMessageLine != null)
				oldMessageLine.dispose();
			
			// add a success message only as an answer to a message of a prior cycle. 
			if ( !agedMessageTags.contains( theTag) &&
					 theErrorKind.equals( ErrorKind.success))
				return;
			
			taggedMessageLines.put( theTag, new MessageLine( theMessage, theErrorKind));
		}

		public synchronized void clearTaggedSuccessMessage( final String theTag) {
			MessageLine messageLine = taggedMessageLines.get( theTag);
			if ( messageLine != null &&
					 messageLine.getErrorKind().equals( ErrorKind.success)) {
				taggedMessageLines.remove( theTag);
				messageLine.dispose();
			}
		}
		
		public synchronized void clearTaggedMessage( final String theTag) {
			MessageLine messageLine = taggedMessageLines.remove( theTag);
			if ( messageLine != null) {
				messageLine.dispose();
			}
		}
		
		public void addUntaggedMessage( final String theMessage, final ErrorKind theErrorKind) {
			if ( theErrorKind.equals( ErrorKind.success))
				return;
			
			untaggedMessageLines.add( new MessageLine( theMessage, theErrorKind));
		}

		
		private void clearAllTaggedMessages() {
			for (MessageLine messageLine : taggedMessageLines.values()) {
				messageLine.dispose();
			}
			taggedMessageLines.clear();
		}
		
		private void clearAllUntaggedMessages() {
			for (MessageLine messageLine : untaggedMessageLines) {
				messageLine.dispose();
			}
			untaggedMessageLines.clear();
		}
		
		private void retainTags() {
			agedMessageTags.addAll( taggedMessageLines.keySet());
		}
		
		private void clearRetainedTags() {
			agedMessageTags.clear();
		}
			
		
		public synchronized void beginUpdateCycle() {
			retainTags();
			clearAllTaggedMessages();
			clearAllUntaggedMessages();
		}
			
		public void endUpdateCycle() {
			clearRetainedTags();
		}

		public List<String> getMessages( EnumSet<ErrorKind> theMessageKinds) {
			List<String> result = new ArrayList<String>();
			Collection<MessageLine> messageLines = taggedMessageLines.values();
			for (MessageLine messageLine : messageLines) {
				if ( theMessageKinds.contains( messageLine.getErrorKind()))
				  result.add( messageLine.getMessage());
			}
			return result;
		}		
		
	}
	
	
	
	
	
	private IMessageViewLookAndBehaviour lookAndBehaviour = new DefaultMessageViewLookAndBehaviour();
	
	private final Map<Object, MessageBlock> messages = new HashMap<Object, MessageBlock>();	 
	private Object currentSenderId;
	
	private static final ScheduledExecutorService messageWorker = Executors.newSingleThreadScheduledExecutor();
	
	
	private Map<String, ScheduledFuture> flashMessageFutures = new HashMap<String, ScheduledFuture>();
	
	private Lock updateLock = new ReentrantLock();
	
	private Composite container;
	private Composite wrappedComposite;
	
	public MessagePanel( final Composite theParent, final int theStyle) {
		super( theParent, theStyle);
		createPanel( theParent);
		// precreate default  block
		messages.put( getThisId(), new MessageBlock());
	}
	
	private void createPanel(Composite theParent) {
	  container = new Composite( theParent, SWT.None);
		container.setLayout( new GridLayout( 1, true));
		// TODO check layout data. Is compatible? to Flowlayout or Filllayout 
		container.setLayoutData( new GridData(SWT.FILL, SWT.TOP, true, false, 0, 0));
		
		// create message panel
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		setLayout( layout);
		
		container.setSize( container.computeSize( SWT.DEFAULT, SWT.DEFAULT));
		container.layout();
	}

	@Override
	public void setLookAndBehaviour(IMessageViewLookAndBehaviour theLookAndBehaviour) {
		this.lookAndBehaviour = theLookAndBehaviour;
	}
	
	@Override
	public IMessageViewLookAndBehaviour getLookAndBehaviour() {
		return lookAndBehaviour;
	}
	
	@Override
	public void putTaggedMessage( final String theTag, final String theMessage, final ErrorKind theErrorKind) {
		final Object id = ( currentSenderId != null) ? currentSenderId : getThisId();

		if ( !lookAndBehaviour.isFlashingOfTaggedSuccessMessagesEnabled() && 
				 theErrorKind.equals( ErrorKind.success))
			return; 
		
		final MessageBlock messageBlock = messages.get( id);
		messageBlock.putTaggedMessage( theTag, theMessage, theErrorKind);
		
		if ( lookAndBehaviour.isFlashingOfTaggedSuccessMessagesEnabled() && 
				 theErrorKind.equals( ErrorKind.success)) {
			Runnable removeMessageTask = new Runnable() {
				public void run() {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							messageBlock.clearTaggedSuccessMessage( theTag);
							doOnChange();
						}
					});		
				}
			};
			messageWorker.schedule( removeMessageTask, lookAndBehaviour.getFlashDurationOfSuccessMessages(), TimeUnit.SECONDS);
		}
		
    tryOnChange();
	}

	@Override
	public void addUntaggedMessage( final String theMessage, final ErrorKind theErrorKind) {
		final Object id = ( currentSenderId != null) ? currentSenderId : getThisId();

		MessageBlock messageBlock = messages.get( id);
		messageBlock.addUntaggedMessage( theMessage, theErrorKind);
	
    tryOnChange();
	} 
	
	@Override
	public void flashMessage( final String theTag, final String theWarning, final ErrorKind theErrorKind) {
		final String id = getThisId();
		final MessageBlock messageBlock = messages.get( id);
		
		final String tag = theTag != null && !theTag.isEmpty() ? theTag : String.valueOf( System.currentTimeMillis());

		if ( flashMessageFutures.containsKey( tag))
			flashMessageFutures.get( tag).cancel( true);
		messageBlock.clearTaggedMessage( tag);
		messageBlock.putTaggedMessage( tag, theWarning, theErrorKind); 	
		
		Runnable flashWarningTask = new Runnable() {
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						messageBlock.clearTaggedMessage( tag);
						doOnChange();
					}
				});		
			}
		};

		ScheduledFuture flashMessageFuture = messageWorker.schedule( flashWarningTask, lookAndBehaviour.getFlashDuration(), TimeUnit.SECONDS);
		flashMessageFutures.put( tag, flashMessageFuture);
		
		doOnChange();
	}
	
	@Override
	public void beginUpdateForSender( final Object theSenderId) {
		updateLock.lock();
		
		this.currentSenderId = theSenderId;
		
		// create a message block if no one exist yet
		if ( !messages.containsKey( currentSenderId))
			messages.put( currentSenderId, new MessageBlock());
		
		MessageBlock messageBlock = messages.get( currentSenderId);
		messageBlock.beginUpdateCycle();
	}

	@Override
  public void endUpdate() {
		try {
			MessageBlock messageBlock = messages.get( currentSenderId);
			messageBlock.endUpdateCycle();
			// TODO remove empty message blocks ? 
			this.currentSenderId = null;

			doOnChange();
		} finally {
			updateLock.unlock();
		}
	}
	
	
	@Override
	public List<String> getMessages( EnumSet<ErrorKind> theMessageKinds) {
		List<String> result = new ArrayList<String>();
		Collection<MessageBlock> messageBlocks = messages.values();
		for (MessageBlock messageBlock : messageBlocks) {
			result.addAll( messageBlock.getMessages( theMessageKinds));
		}
		return result;
	}
	
	
	private void doOnChange() {
		lookAndBehaviour.doOnChange();	
	}
	
	private void tryOnChange() {
		if ( this.currentSenderId == null)
			doOnChange();
		else
			try {
				updateLock.tryLock();
				doOnChange();
			} finally {
				updateLock.unlock();
			}
	}
	
	private String getThisId() {
		return this.getClass().getName() + "@" + this.hashCode(); 
	}

	@Override
	public void wrapControl(Composite theWrappedComposite) {
		if ( wrappedComposite != null)
			wrappedComposite.dispose();
		this.wrappedComposite = theWrappedComposite;
		wrappedComposite.setParent( this);
		
    container.setSize( container.computeSize( SWT.DEFAULT, SWT.DEFAULT));
		container.layout();
	}

	
	

}