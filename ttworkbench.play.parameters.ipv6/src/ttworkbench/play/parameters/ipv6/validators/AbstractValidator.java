/*******************************************************************************
 * Copyright (c)  2014 Johannes Dahlke, Thomas Büttner, Alexander Dümont, Fares Mokrani
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may not use this file except in compliance with the License.
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 * This project came to life under the cooperation of the Authors (cited below) and the Testing Technologies GmbH company in the frame of a University Project proposed by the FU-Berlin.
 * 
 * The software is basically a plug-in for the company's eclipse-based framework TTWorkbench. The plug-in offers a new user-friendly view that enables easy configuration of parameters meant to test IPv6 environments.
 *  
 * 
 * Contributors: Johannes Dahlke, Thomas Büttner, Alexander Dümont, Fares Mokrani
 ******************************************************************************/
package ttworkbench.play.parameters.ipv6.validators;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import ttworkbench.play.parameters.ipv6.validators.exceptions.ValidationException;

import com.testingtech.ttworkbench.ttman.parameters.api.IActionHandler;
import com.testingtech.ttworkbench.ttman.parameters.api.IMessageHandler;
import com.testingtech.ttworkbench.ttman.parameters.api.IParameter;
import com.testingtech.ttworkbench.ttman.parameters.api.IParameterValidator;
import com.testingtech.ttworkbench.ttman.parameters.validation.ValidationResult;
import com.testingtech.ttworkbench.ttman.parameters.validation.ValidationResultAction;
import com.testingtech.ttworkbench.ttman.parameters.validation.ValidationResultMessage;

public abstract class AbstractValidator implements IParameterValidator {

	private final String title;
	private final String description;
	
	// TODO use this? why? private final Set<IAttribute> attributes = new HashSet<IAttribute>();
	private final Map<String, String> attributes = new LinkedHashMap<String, String>();
	
	private final Set<IMessageHandler> messageHandlers = new HashSet<IMessageHandler>();
	private final Set<IActionHandler> actionHandlers = new HashSet<IActionHandler>();
	
	public AbstractValidator( final String theTitle, final String theDescription) {
		this.title = theTitle;
		this.description = theDescription;
	}
	

  /**
   * Get the attribute for current actor.
   * @param name the attribute name
   * @return value of the attribute
   */
  String getAttribute(String name) {
  	return attributes.get(name);
  }
  
  
	@Override
	public void setAttribute(String theName, String theValue) {
		attributes.put(theName, theValue);	
	}

	@Override
	public void parametersChanged(List<IParameter<?>> theParameters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public synchronized final List<ValidationResult> validate( IParameter theParameter, Object theClient) {
		List<ValidationResult> results = new LinkedList<ValidationResult>();
		try {
			results.addAll( validateParameter( theParameter, theClient));
	
			List<ValidationResultMessage> resultsMessages = new LinkedList<ValidationResultMessage>();
			List<ValidationResultAction> resultsActions = new LinkedList<ValidationResultAction>();
	
			if ( results != null) {
				for(ValidationResult result : results) {
					if(result instanceof ValidationResultMessage) {
						resultsMessages.add( (ValidationResultMessage) result);
					}
					else if(result instanceof ValidationResultAction) {
						resultsActions.add( (ValidationResultAction) result);
					}
				}
			}
	
			notifyMessageHandlers( this, resultsMessages, theParameter);
			notifyActionHandlers( this, resultsActions, theParameter);
		}
		catch(Exception e) {
			e.printStackTrace();
			MessageBox dialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			dialog.setText(e.getClass().getSimpleName());
			dialog.setMessage("A problem occured while invoking \""+this.getClass().getSimpleName()+"\":\n"+e.getMessage());
			dialog.open(); 
		}
		
		return results;
	}

	private void notifyMessageHandlers(AbstractValidator theAbstractValidator,
			List<ValidationResultMessage> theResults, IParameter<?> theParameter) {
		for ( IMessageHandler messageHandler : messageHandlers) {
			messageHandler.report( this, theResults, theParameter);
		} 
	}
	private void notifyActionHandlers(AbstractValidator theAbstractValidator,
			List<ValidationResultAction> theResults, IParameter<?> theParameter) {
		for ( IActionHandler actionHandler : actionHandlers) {
			actionHandler.trigger( this, theResults, theParameter);
		} 
	}


	protected abstract List<ValidationResult> validateParameter(IParameter<?> theParameter, Object theClient) throws ValidationException;
	
	
	@Override
	public void registerForMessages(IMessageHandler theMessageHandler) {
		messageHandlers.add( theMessageHandler);
	}
	
	@Override
	public void registerForActions(IActionHandler theActionHandler) {
		actionHandlers.add( theActionHandler);
	}
	
	
	@Override
	public String toString() {
		return "\""+getTitle()+"\"@"+getId();
	}

	@Override
	public long getId() {
		return hashCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ( ( description == null) ? 0 : description.hashCode());
		result = prime * result + ( ( title == null) ? 0 : title.hashCode());
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractValidator other = (AbstractValidator) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals( other.attributes))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals( other.description))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals( other.title))
			return false;
		return true;
	}
}
