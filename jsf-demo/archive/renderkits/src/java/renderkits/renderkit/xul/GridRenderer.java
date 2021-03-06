/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package renderkits.renderkit.xul;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * <B>GridRenderer</B> is a class that renders <code>UIPanel</code> component
 * as a "Grid".
 */

public class GridRenderer extends BaseRenderer {

    //
    // Protected Constants
    //

    //
    // Class Variables
    //

    //
    // Instance Variables
    //

    // Attribute Instance Variables

    // Relationship Instance Variables

    //
    // Constructors and Initializers    
    //

    public GridRenderer() {
        super();
    }

    //
    // Class methods
    //

    //
    // General Methods
    //

    //
    // Methods From Renderer
    //

    public boolean getRendersChildren() {
        return true;
    }


    public void encodeBegin(FacesContext context, UIComponent component)
          throws IOException {

        if (context == null || component == null) {
            // PENDING - i18n
            throw new NullPointerException("'context' and/or 'component is null");
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Begin encoding component " +
                                    component.getId());
        }

        // suppress rendering if "rendered" property on the component is
        // false.
        if (!component.isRendered()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("End encoding component "
                            + component.getId() + " since " +
                            "rendered attribute is set to false ");
            }
            return;
        }

        // Render the beginning of this panel
        ResponseWriter writer = context.getResponseWriter();
        writer.writeText("\n", null);
        writer.startElement("grid", component);
        writeIdAttributeIfNecessary(context, writer, component);
        String styleClass =
              (String) component.getAttributes().get("styleClass");
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }
        writer.writeText("\n", null);

        writer.startElement("columns", component);
        writer.writeText("\n", null);
        int columns = getColumnCount(component);
        for (int i = 0; i < columns; i++) {
            writer.startElement("column", component);
            writer.endElement("column");
            writer.writeText("\n", null);
        }
        writer.endElement("columns");
        writer.writeText("\n", null);
        writer.startElement("rows", component);
        writer.writeText("\n", null);
    }


    public void encodeChildren(FacesContext context, UIComponent component)
          throws IOException {

        if (context == null || component == null) {
            // PENDING - i18n
            throw new NullPointerException("'context' and/or 'component is null");
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                       "Begin encoding children " + component.getId());
        }

        // suppress rendering if "rendered" property on the component is
        // false.
        if (!component.isRendered()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("End encoding component " +
                            component.getId() + " since " +
                            "rendered attribute is set to false ");
            }
            return;
        }

        // Set up the variables we will need
        ResponseWriter writer = context.getResponseWriter();
        int columns = getColumnCount(component);
        String columnClasses[] = getColumnClasses(component);
        int columnStyle = 0;
        int columnStyles = columnClasses.length;
        String rowClasses[] = getRowClasses(component);
        int rowStyle = 0;
        int rowStyles = rowClasses.length;
        boolean open = false;
        Iterator<UIComponent> kids = null;
        int i = 0;

        // Render our children, starting a new row as needed

        if (null != (kids = getChildren(component))) {
            while (kids.hasNext()) {
                UIComponent child = kids.next();
                if ((i % columns) == 0) {
                    if (open) {
                        writer.endElement("row");
                        writer.writeText("\n", null);
                        open = false;
                    }
                    writer.startElement("row", component);
                    if (rowStyles > 0) {
                        writer.writeAttribute("class", rowClasses[rowStyle++],
                                              "rowClasses");
                        if (rowStyle >= rowStyles) {
                            rowStyle = 0;
                        }
                    }
                    writer.writeText("\n", null);
                    open = true;
                    columnStyle = 0;
                }
                writer.startElement("box", component);
                writer.writeText("\n", null);
                if (columnStyles > 0) {
                    try {
                        writer.writeAttribute("class",
                                              columnClasses[columnStyle++],
                                              "columns");
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                    if (columnStyle >= columnStyles) {
                        columnStyle = 0;
                    }
                }
                encodeRecursive(context, child);
                writer.endElement("box");
                writer.writeText("\n", null);
                i++;
            }
        }
        if (open) {
            writer.endElement("row");
            writer.writeText("\n", null);
        }
        writer.writeText("\n", null);
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                       "End encoding children " + component.getId());
        }
    }


    public void encodeEnd(FacesContext context, UIComponent component)
          throws IOException {

        if (context == null || component == null) {
            // PENDING - i18n
            throw new NullPointerException("'context' and/or 'component is null");
        }

        // suppress rendering if "rendered" property on the component is
        // false.
        if (!component.isRendered()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("End encoding component " +
                            component.getId() + " since " +
                            "rendered attribute is set to false ");
            }
            return;
        }
        // Render the ending of this panel
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement("rows");
        writer.writeText("\n", null);
        writer.endElement("grid");
        writer.writeText("\n", null);
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                       "End encoding component " + component.getId());
        }
    }

    /**
     * Returns an array of stylesheet classes to be applied to
     * each column in the list in the order specified. Every column may or
     * may not have a stylesheet
     */
    private String[] getColumnClasses(UIComponent component) {
        String values = (String) component.getAttributes().get("columnClasses");
        if (values == null) {
            return (new String[0]);
        }
        values = values.trim();
        ArrayList<String> list = new ArrayList<String>();
        while (values.length() > 0) {
            int comma = values.indexOf(",");
            if (comma >= 0) {
                list.add(values.substring(0, comma).trim());
                values = values.substring(comma + 1);
            } else {
                list.add(values.trim());
                values = "";
            }
        }
        String results[] = new String[list.size()];
        return (list.toArray(results));
    }


    /**
     * Returns number of columns of the grid converting the value
     * specified to int if necessary.
     */
    private int getColumnCount(UIComponent component) {
        int count;
        Object value = component.getAttributes().get("columns");
        if ((value != null) && (value instanceof Integer)) {
            count = (Integer) value;
        } else {
            count = 2;
        }
        if (count < 1) {
            count = 1;
        }
        return (count);
    }


    /**
     * Returns an array of stylesheet classes to be applied to
     * each row in the list in the order specified. Every row may or
     * may not have a stylesheet
     */
    private String[] getRowClasses(UIComponent component) {
        String values = (String) component.getAttributes().get("rowClasses");
        if (values == null) {
            return (new String[0]);
        }
        values = values.trim();
        ArrayList<String> list = new ArrayList<String>();
        while (values.length() > 0) {
            int comma = values.indexOf(",");
            if (comma >= 0) {
                list.add(values.substring(0, comma).trim());
                values = values.substring(comma + 1);
            } else {
                list.add(values.trim());
                values = "";
            }
        }
        String results[] = new String[list.size()];
        return (list.toArray(results));
    }
}
