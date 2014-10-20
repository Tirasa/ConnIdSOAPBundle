/* 
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2011 ConnId. All rights reserved.
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/cddl1.php
 * See the License for the specific language governing permissions and limitations
 * under the License.
 * 
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://opensource.org/licenses/cddl1.php.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package net.tirasa.connid.bundles.soap.utilities;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

public enum AttributeType {

    String("java.lang.String"),
    Long("java.lang.Long"),
    Double("java.lang.Double"),
    Boolean("java.lang.Boolean"),
    Character("java.lang.Character"),
    Float("java.lang.Float"),
    Integer("java.lang.Integer"),
    URI("java.net.uri"),
    File("java.io.file"),
    // Date type is not supported by identityconnectors
    Date("java.lang.String");

    final private String className;

    private Format formatter;

    AttributeType(final String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public Format getBasicFormatter() {
        if (formatter == null) {
            switch (this) {
                case Date:
                    this.formatter = new SimpleDateFormat();
                    break;

                case Long:
                case Double:
                    this.formatter = new DecimalFormat();
                    break;

                default:
            }
        }

        return formatter;
    }

    public boolean isConversionPatternNeeded() {
        return this == AttributeType.Date || this == AttributeType.Double || this == AttributeType.Long;
    }
}
