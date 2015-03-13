/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;

/**
 * 
 * <code>ValidationFailure</code> represents a single validation failure, which is a result of validating the OPI
 * content against the schema.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ValidationFailure {
    
    private final String widgetType;
    private final String widgetName;
    private final String property;
    private final String expected;
    private final String actual;
    private final String wuid;
    private final IPath path;
    private final Object expectedValue;
    private final Object actualValue;
    private final ValidationRule rule;
    private final boolean isCritical;
    private final boolean isFixable;
    private final String forcedMessage;
    
    private int lineNumber;
    
    private final List<SubValidationFailure> subFailures = new ArrayList<>();
    
    /**
     * Constructs a new validation failure.
     * 
     * @param path the path to the file, in which the failure originates
     * @param wuid the widget unique id
     * @param widgetType the type of the widget
     * @param widgetName the name of the widget
     * @param property the property name which is failing
     * @param expected the expected value of the property (schema value)
     * @param actual the actual value of the property (validated opi value)
     * @param rule the rule, which was violated
     * @param isCritical defines if the failure is critical or not (failure is critical when a RO rule is violated
     *          using a non-predefined value)
     * @param isFixable true if the failure can be fixed automatically or false otherwise
     * @param forcedMessage the forced message to be returned by {@link #getMessage()}. If null, it will be composed
     *          when the {@link #getMessage()} is called 
     */
    ValidationFailure(IPath path, String wuid, String widgetType, String widgetName, 
            String property, Object expected, Object actual, ValidationRule rule, boolean isCritical,
            boolean isFixable, String forcedMessage) {
        this.widgetType = widgetType;
        this.widgetName = widgetName;
        this.property = property;
        this.expectedValue = expected;
        this.actualValue = actual;
        this.expected = String.valueOf(expected);
        this.actual = String.valueOf(actual);
        this.path = path;
        this.rule = rule;
        this.isCritical = isCritical;
        this.wuid = wuid == null ? "" : wuid; //old OPIs might not have the WUIDs
        this.isFixable = isFixable;
        this.forcedMessage = forcedMessage;
    }
    
    /**
     * Constructs the message describing this validation failure. The message is used for the contents of the marker.
     * 
     * @return the message describing the failure
     */
    public String getMessage() {
        if (forcedMessage != null) {
            return forcedMessage;
        }
        boolean usingNonDefined = false;
        if (rule == ValidationRule.RO) {
            if (expectedValue == null) {
                usingNonDefined = true;
            } else {
                return new StringBuilder(property.length() + expected.length() + actual.length() + 26)
                    .append(property).append(": expected: '").append(expected).append("' but was: '")
                    .append(actual).append('\'').toString();
            }
        } else if (rule == ValidationRule.WRITE) {
            if (expectedValue == null) {
                usingNonDefined = true;
            } else {
                return new StringBuilder(property.length() + 11).append(property).append(" is not set").toString();
            }
        } else if (rule == ValidationRule.RW) {
            //can happen in the case of font and colour
            usingNonDefined = true;
        } 
        if (usingNonDefined) {
            return new StringBuilder(property.length() + actual.length() + 40).append(property)
                    .append(": '").append(actual).append("' is not one of the predefined values").toString();
        }
        return null;
    }
    
    /**
     * Constructs the location message containing the line number and widget name.
     * 
     * @return the location
     */
    public String getLocation() {
        return "line " + lineNumber + " (Widget: " + widgetName + ")";
    }
    
    /**
     * @return the line number at which the failure was recorder
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * Sets the lin number at which this failure was recorded.
     * 
     * @param lineNumber the line number
     */
    void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the widget typeId.
     */
    public String getWidgetType() {
        return widgetType;
    }

    /**
     * @return the widget name
     */
    public String getWidgetName() {
        return widgetName;
    }

    /**
     * @return the property name
     */
    public String getProperty() {
        return property;
    }

    /**
     * @return the expected property value transformed to string
     */
    public String getExpected() {
        return expected;
    }

    /**
     * @return the actual property value transformed to string
     */
    public String getActual() {
        return actual;
    }

    /**
     * @return the path to the file on which this failure occurred
     */
    public IPath getPath() {
        return path;
    }   
    
    /**
     * @return the expected property value as a real property object
     */
    public Object getExpectedValue() {
        return expectedValue;
    }
    
    /**
     * @return the actual property value as a real property object
     */
    public Object getActualValue() {
        return actualValue;
    }
    
    /**
     * @return the violated validation rule
     */
    public ValidationRule getRule() {
        return rule;
    }
    
    /**
     * @return true if this validation represents a critical failure (error) or false if not critical (warning)
     */
    public boolean isCritical() {
        return isCritical;
    }
    
    /**
     * Adds sub failures to this validation failure. Sub failure is a failure that is a failure in the sub property,
     * such as the property of action, script, or rule.
     * 
     * @param failures the list of failures to add
     */
    public void addSubFailure(List<SubValidationFailure> failures) {
        for (SubValidationFailure f : failures) {
            f.setParent(this);
        }
        subFailures.addAll(failures);
    }
    
    /**
     * @return the list of all sub failures
     */
    public SubValidationFailure[] getSubFailures() {
        return subFailures.toArray(new SubValidationFailure[subFailures.size()]);
    }
    
    /**
     * @return true if this failure has sub failures
     */
    public boolean hasSubFailures() {
        return !subFailures.isEmpty();
    }
    
    /**
     * @return the widget unique ID
     */
    public String getWUID() {
        return wuid;
    }
    
    /**
     * @return true if the failure can be fixed automatically or false otherwise
     */
    public boolean isFixable() {
        return isFixable;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getMessage();
    }
}
