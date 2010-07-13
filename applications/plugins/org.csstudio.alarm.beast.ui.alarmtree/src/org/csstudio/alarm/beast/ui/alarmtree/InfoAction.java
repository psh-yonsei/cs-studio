/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtree;

import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;

/** Action to display model info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class InfoAction extends Action
{
    final private AlarmClientModel model;

    public InfoAction(final AlarmClientModel model)
    {
        super("Debug", Activator.getImageDescriptor("icons/information.gif"));
        setToolTipText("Alarm System Info");
        this.model = model;
    }

    @Override
    public void run()
    {
        final StringBuilder info = new StringBuilder();
        info.append("JMS Server: " + model.getJMSServerName() + "\n");
        info.append("Alarm Server alive: " + model.isServerAlive() + "\n");
        info.append("'Write' allowed: " + model.isWriteAllowed() + "\n");
        info.append("PV Count: " + model.getConfigTree().getPVCount() + "\n");
        info.append("Active alarms: " + model.getActiveAlarms().length + "\n");
        info.append("Acknowledged alarms: " + model.getAcknowledgedAlarms().length + "\n");
        
        MessageDialog.openInformation(null, "Alarm System Information",
                info.toString());
    }
}
