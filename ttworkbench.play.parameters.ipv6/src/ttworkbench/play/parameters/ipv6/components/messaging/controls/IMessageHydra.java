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
package ttworkbench.play.parameters.ipv6.components.messaging.controls;

import com.testingtech.ttworkbench.ttman.parameters.validation.ErrorKind;


public interface IMessageHydra {
	
	/**
	 * Creates a new client label that is bound to the master label.
	 * Changes in the master label take effect in the client labels, but not vice versa.
	 * 
	 * @param theMessageContainer
	 * @return
	 */
	public IMessageLabel newLabel( final IMessageContainer theMessageContainer);

	public void disposeLabels();

	void messageChanged(MessageLabel theSender);
	
	int getCountOfLabels();

	ErrorKind getErrorKind();
	
	boolean hasTag();

	String getMessage(); 
	
	// public void navigateToSource(); or focusSourceControl()

}
