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

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Layout;

public class RowEditorLookAndBehaviour extends DefaultEditorLookAndBehaviour {

	@Override
	public Layout getLayout() {
		RowLayout rowLayout = new RowLayout();
		// rowLayout.wrap = false;
		rowLayout.pack = false;
		rowLayout.justify = false;
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 5;
		rowLayout.marginRight = 5;
		rowLayout.marginBottom = 5;
		rowLayout.spacing = 10;
		return rowLayout;
	}

	@Override
	public Object[] getLayoutDataOfControls() {
		/*
		 * Die Methode macht keinen Sinn, da nicht feststeht welche
		 * Data-Informationen wo in dem Feld stehen und somit das unter Verwendung
		 * der Methode nicht mehr austauschbar ist.
		 */
		throw new UnsupportedOperationException();
	}

}
