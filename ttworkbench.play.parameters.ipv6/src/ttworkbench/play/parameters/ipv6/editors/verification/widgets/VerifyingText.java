/*******************************************************************************
 * Copyright (c)  .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
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
 * Contributors:
 *     
 ******************************************************************************/
package ttworkbench.play.parameters.ipv6.editors.verification.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import ttworkbench.play.parameters.ipv6.editors.verification.IVerifier;
import ttworkbench.play.parameters.ipv6.editors.verification.VerifyingAdapter;

import com.testingtech.ttworkbench.ttman.parameters.api.IParameter;


public class VerifyingText<P> extends VerifyingAdapter<Text,P> {
	
	private final String defaultText;
	
  public VerifyingText( final IParameter<P> theParameter, final Composite theParent, final int theStyle, final IVerifier<String> ... theVerifiers) {
	  this( theParameter, theParent, theStyle, "", theVerifiers);
  }

  /**
   * 
   * @param theParameter
   * @param theParent parent of the encapsulated control
   * @param theStyle style of the encapsulated control
   * @param theDefaultText text that is show instead of an empty field.
   * @param theVerifiers verifiers which listen on SWT.Verify event.
   */
  public VerifyingText( final IParameter<P> theParameter, final Composite theParent, final int theStyle, final String theDefaultText, final IVerifier<String> ... theVerifiers) {
	  super( theParameter, theParent, theStyle, theVerifiers);
	  this.defaultText = theDefaultText;
  }
  
	@Override
	public void setText( final String theText) {
		getControl().setText( theText);
	}

	@Override
	public String getText() {
		return getControl().getText();
	}
	
	@Override
	protected final Text createControl(Composite theParent, int theStyle) {
		return new Text( theParent, theStyle);
	}
	
	/**
	 * Specified for SWT.Verify event.
	 */
	@Override
	protected String getModifiedTextByEvent(Event theEvent) {
		String currentText = getControl().getText();
		Character key = theEvent.character;
		String insertion = (key == '\b') ? "" : theEvent.text; 
		int beginIndex = theEvent.start;
		int endIndex = theEvent.end;
		String leftString = currentText.substring( 0, beginIndex);
		String rightString = currentText.substring( endIndex, currentText.length());
		String modifiedText = leftString + insertion + rightString;

		if ( modifiedText.isEmpty())
			modifiedText = defaultText;

		return modifiedText;		
	}

	
	
}
