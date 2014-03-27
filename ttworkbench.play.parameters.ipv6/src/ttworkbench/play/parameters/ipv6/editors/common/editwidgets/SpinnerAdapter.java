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
package ttworkbench.play.parameters.ipv6.editors.common.editwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Spinner;


@Deprecated
public class SpinnerAdapter implements IEditableWidgetAdapter<Spinner> {


	@Override
	public void setTextForWidget( String theText, Spinner theWidget) {
		theWidget.setSelection( Integer.valueOf( theText));
	}

	@Override
	public String getTextFromWidget(Spinner theWidget) {
		return theWidget.getText();
	}

	@Override
	public String getTextByEvent(Event theEvent) throws UnhandledEventException {
		switch ( theEvent.type) {
			case SWT.Verify:

				Spinner spinner = (Spinner) theEvent.widget;
				String currentText = spinner.getText();
				Character key = theEvent.character;
				String insertion = (key == '\b') ? "" : theEvent.text; 
				int beginIndex = theEvent.start;
				int endIndex = theEvent.end;
				String leftString = currentText.substring( 0, beginIndex);
				String rightString = currentText.substring( endIndex, currentText.length());
				String modifiedText = leftString + insertion + rightString;

				if ( modifiedText.isEmpty())
					modifiedText = "0";

				return modifiedText;
			default: 
				throw new UnhandledEventException();
		}
	}

	@Override
	public Class<Spinner> getSupportedWidget() {
		return Spinner.class;
	}
	


}
