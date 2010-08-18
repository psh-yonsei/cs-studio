/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.ui.internal.actions;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Action to open a process variable in the default CSS display. The default
 * display is configured in the preferences.
 * 
 * @author Joerg Rathlev
 */
public class OpenPvInDefaultDisplayAction extends ProcessVariablePopupAction {
	
	/**
	 * Opens the received process variable names in the default display.
	 * @param pv_names the process variable names on which this action is
	 * invoked.
	 */
	@Override
	public void handlePVs(IProcessVariable[] pv_names) {
		IPreferencesService prefs = Platform.getPreferencesService();
		String defaultDisplay = prefs.getString(SdsPlugin.PLUGIN_ID,
				PreferenceConstants.PROP_DEFAULT_DISPLAY_FILE, "", null);
		boolean openAsShell = prefs.getBoolean(SdsPlugin.PLUGIN_ID,
				PreferenceConstants.PROP_DEFAULT_DISPLAY_OPEN_AS_SHELL, true, null);
		String alias = prefs.getString(SdsPlugin.PLUGIN_ID,
				PreferenceConstants.PROP_DEFAULT_DISPLAY_ALIAS, "channel", null);

		IPath displayPath = new Path(defaultDisplay);
		RunModeService runner = RunModeService.getInstance();
		for (IProcessVariable pv : pv_names) {
			Map<String, String> aliases = new HashMap<String, String>();
			String pvname = pv.getName();
			aliases.put(alias, pvname);
			CentralLogger.getInstance().debug(this,
					"Opening display " + displayPath + " with alias " + alias + "=" + pvname);
			if (openAsShell) {
				runner.openDisplayShellInRunMode(displayPath, aliases);
			} else {
				runner.openDisplayViewInRunMode(displayPath, aliases);
			}
		}
	}

}
