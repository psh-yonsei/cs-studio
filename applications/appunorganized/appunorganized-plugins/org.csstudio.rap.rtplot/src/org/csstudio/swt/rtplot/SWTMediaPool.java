/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

/** Thread-safe caching pool of {@link Color}, {@link Font}
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SWTMediaPool
{
    final private Device device;

    final private Map<RGB, Color> colors = new HashMap<>();
    final private Map<FontData, Font> fonts = new HashMap<>();

    /** Create pool for device
     *
     *  <p>Need to dispose when done
     *  @param device {@link Device}
     */
    public SWTMediaPool(final Device device)
    {
        this.device = device;
    }

    /** Create pool for parent widget
     *
     *  <p>Will be disposed with parent
     *  @param parent Parent widget
     */
    public SWTMediaPool(final Composite parent)
    {
        device = parent.getDisplay();
        parent.addDisposeListener((e) -> dispose());
    }

    /** @param rgb Color description
     *  @return Color
     */
    public Color get(final RGB rgb)
    {
        synchronized (colors)
        {
            Color color = colors.get(rgb);
            if (color == null)
            {
                color = new Color(device, rgb);
                colors.put(rgb, color);
            }
            return color;
        }
    }

    /** Convert FontData into portable description
     *  @param font_data {@link FontData}
     *  @return Portable description of the font
     */
    public static String getFontDescription(final FontData font_data)
    {
        return font_data.getName() + "|" + font_data.getHeight() + "|" + font_data.getStyle();
    }

    /** Convert portable font description into FontData
     *  @param description Font description generated by <code>getFontDescription()</code>
     *  @return {@link FontData}
     */
    public static FontData getFontFromDescription(final String description)
    {
        final String name;
        final int height, style;
        final String[] sections = description.split("\\|");
        if (sections.length == 3)
        {   // Font description generated by <code>getFontDescription()</code>
            name = sections[0];
            height = Integer.parseInt(sections[1]);
            style = Integer.parseInt(sections[2]);
        }
        else if (sections.length > 3  &&  description.startsWith("1|"))
        {   // Assume legacy format from FontData.toString(): "1|name|(double)height|style"
            name = sections[1];
            height = (int) Double.parseDouble(sections[2]);
            style = Integer.parseInt(sections[3]);
        }
        else
            throw new IllegalArgumentException("Invalid Font description '" + description + "'");
        // Note by Jaka Bobnar in org.csstudio.trends.databrowser2.model.FontDataUtil:
        // Windows doesn't respect the name given in constructor
        final FontData result = new FontData(name, height, style);
        result.setName(name);
        return result;
    }

    /** @param name Font name
     *  @param height Pixel height
     *  @param style SWT.NORMAL, SWT.BOLD, SWT.ITALIC
     *  @return Font
     */
    public Font getFont(final String name, final int height, final int style)
    {
        return get(new FontData(name, height, style));
    }

    /** @param font_data Font description
     *  @return Font
     */
    public Font get(final FontData font_data)
    {
        synchronized (fonts)
        {
            Font font = fonts.get(font_data);
            if (font == null)
            {
                font = new Font(device, font_data);
                fonts.put(font_data, font);
            }
            return font;
        }
    }

    /** Dispose pooled resources
     *
     *  <p>Needs to be called unless pool is
     *  associated with parent widget
     */
    public void dispose()
    {
        for (Font font : fonts.values())
            font.dispose();
        for (Color color : colors.values())
            color.dispose();
    }
}
