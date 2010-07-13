/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtree;

import org.csstudio.platform.ui.workbench.OpenViewAction;

/** Action connected to workbench menu action set for showing the view.
 *  @author Kay Kasemir
 */
public class ShowAlarmTreeAction extends OpenViewAction
{
    public ShowAlarmTreeAction()
    {
        super(AlarmTreeView.ID);
    }
}
