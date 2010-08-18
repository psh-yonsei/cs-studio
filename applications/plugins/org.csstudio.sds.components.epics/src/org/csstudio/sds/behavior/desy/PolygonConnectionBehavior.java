/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.PolygonModel;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

/**
 *
 * Default DESY-Behavior for the {@link PolygonModel} widget with Connection state
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 20.04.2010
 */
public class PolygonConnectionBehavior extends AbstractDesyConnectionBehavior<PolygonModel> {

    /**
     * Constructor.
     */
    public PolygonConnectionBehavior() {
        // add Invisible Property Id here
         addInvisiblePropertyId(PolygonModel.PROP_FILL);
         addInvisiblePropertyId(PolygonModel.PROP_COLOR_FOREGROUND);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessValueChange(final PolygonModel model, final AnyData anyData) {
        super.doProcessValueChange(model, anyData);
        int arguments = anyData.numberValue().intValue();
        if(arguments>=15) {
            model.setColor(PolygonModel.PROP_COLOR_FOREGROUND, ColorAndFontUtil.toHex(255,255,255));
        } else if(arguments>=3) {
            model.setColor(PolygonModel.PROP_COLOR_FOREGROUND, ColorAndFontUtil.toHex(249,218,60));
        } else if(arguments>=2) {
            model.setColor(PolygonModel.PROP_COLOR_FOREGROUND, ColorAndFontUtil.toHex(30,187,0));
        } else if(arguments>=1) {
            model.setColor(PolygonModel.PROP_COLOR_FOREGROUND, ColorAndFontUtil.toHex(42,99,228));
        } else if(arguments>=0) {
            model.setColor(PolygonModel.PROP_COLOR_FOREGROUND, ColorAndFontUtil.toHex(253,0,0));
        } else {
            model.setColor(PolygonModel.PROP_COLOR_FOREGROUND, ColorAndFontUtil.toHex(0,0,0));
        }
    }

    @Override
    protected void doProcessMetaDataChange(final PolygonModel widget, final MetaData metaData) {
        // do noting
    }
}
