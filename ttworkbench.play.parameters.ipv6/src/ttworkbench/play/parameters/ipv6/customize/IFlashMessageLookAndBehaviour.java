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
package ttworkbench.play.parameters.ipv6.customize;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;

import com.testingtech.ttworkbench.ttman.parameters.validation.ErrorKind;

public interface IFlashMessageLookAndBehaviour extends ILookAndBehaviour {

	void enableBeep();

	boolean isBeepEnabled();

	void disableBeep();

	void setFlashDurationOfSuccessMessages(int theFlashDurationInSeconds);

	int getFlashDurationOfSuccessMessages();

	void setFlashDuration(int theFlashDurationInSeconds);

	int getFlashDuration();

	void addChangedListener(Listener theChangedListener);

	void doOnChange();

	void enableFlashingOfTaggedSuccessMessages();

	void disableFlashingOfTaggedSuccessMessages();

	boolean isFlashingOfTaggedSuccessMessagesEnabled();
	
	Color getMessageForeground( ErrorKind theErrorKind);
	
	Color getMessageBackground( ErrorKind theErrorKind);

	Color getFlashMessageForeground( ErrorKind theErrorKind);
	
	Color getFlashMessageBackground( ErrorKind theErrorKind);
	
	Image getMessageImage( ErrorKind theErrorKind);
	
	Font getMessageFont( ErrorKind theErrorKind);	

}
