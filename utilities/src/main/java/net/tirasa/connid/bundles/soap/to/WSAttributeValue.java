/**
 * Copyright (C) 2011 ConnId (connid-dev@googlegroups.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tirasa.connid.bundles.soap.to;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.identityconnectors.framework.common.FrameworkUtil;

@XmlAccessorType(XmlAccessType.FIELD)
public class WSAttributeValue extends WSAttribute {

    private static final long serialVersionUID = 8850711594062996107L;

    private List<Object> values = null;

    private List<Object> evaluatedTypeValues = null;

    public WSAttributeValue() {
        super();
    }

    public WSAttributeValue(final WSAttribute wsAttribute) {
        super();

        if (wsAttribute != null) {
            setType(wsAttribute.getType());
            setName(wsAttribute.getName());
            setKey(wsAttribute.isKey());
            setNullable(wsAttribute.isNullable());
            setPassword(wsAttribute.isPassword());
        }
    }

    public List<Object> getValues() {
        if (this.values == null) {
            this.values = new ArrayList<>();
        }

        if (evaluatedTypeValues == null) {
            evaluatedTypeValues = new ArrayList<>();

            for (Object obj : values) {
                try {
                    FrameworkUtil.checkAttributeValue(obj);
                    getValues().add(obj);
                } catch (IllegalArgumentException e) {
                    getValues().add(obj.toString());
                }
            }
        }

        return this.evaluatedTypeValues;
    }

    public void setValues(final List<Object> values) {
        this.values = values;
    }

    public final boolean addValue(final Object value) {
        if (this.values == null) {
            this.values = new ArrayList<>();
        }

        return this.values.add(value);
    }

    public String getStringValue() {
        if (getType() == null || !"String".equals(getType())) {
            throw new IllegalArgumentException("Invalid type declaration");
        }

        String res;

        if (values == null || values.isEmpty() || values.iterator().next() == null) {
            res = null;
        } else {
            res = values.iterator().next().toString();
        }

        return res;
    }

    public Boolean getBooleanValue() {
        if (getType() == null || !"Boolean".equals(getType())) {
            throw new IllegalArgumentException("Invalid type declaration");
        }

        Boolean res;

        if (values == null || values.isEmpty() || values.iterator().next() == null) {
            res = null;
        } else {
            res = (Boolean) values.iterator().next();
        }

        return res;
    }

    public Long getLongValue() {
        if (getType() == null || !"Long".equals(getType())) {
            throw new IllegalArgumentException("Invalid type declaration");
        }

        Long res;

        if (values == null || values.isEmpty() || values.iterator().next() == null) {
            res = null;
        } else {
            res = (Long) values.iterator().next();
        }

        return res;
    }

    public Float getFloatValue() {
        if (getType() == null || !"Float".equals(getType())) {
            throw new IllegalArgumentException("Invalid type declaration");
        }

        Float res;

        if (values == null || values.isEmpty() || values.iterator().next() == null) {
            res = null;
        } else {
            res = (Float) values.iterator().next();
        }

        return res;
    }

    public Double getDoubleValue() {
        if (getType() == null || !"Double".equals(getType())) {
            throw new IllegalArgumentException("Invalid type declaration");
        }

        Double res;

        if (values == null || values.isEmpty() || values.iterator().next() == null) {
            res = null;
        } else {
            res = (Double) values.iterator().next();
        }

        return res;
    }

    public Integer getIntegerValue() {
        if (getType() == null || !"Integer".equals(getType())) {
            throw new IllegalArgumentException("Invalid type declaration");
        }

        Integer res;

        if (values == null || values.isEmpty() || values.iterator().next() == null) {
            res = null;
        } else {
            res = (Integer) values.iterator().next();
        }

        return res;
    }

    public Date getDateValue() {
        if (getType() == null || !"Date".equals(getType())) {
            throw new IllegalArgumentException("Invalid type declaration");
        }

        Date res;

        if (values == null || values.isEmpty() || values.iterator().next() == null) {
            res = null;
        } else {
            res = (Date) values.iterator().next();
        }

        return res;
    }

    public Character getCharacterValue() {
        if (getType() == null || !"Character".equals(getType())) {
            throw new IllegalArgumentException("Invalid type declaration");
        }

        Character res;

        if (values == null || values.isEmpty() || values.iterator().next() == null) {
            res = null;
        } else {
            res = (Character) values.iterator().next();
        }

        return res;
    }

    public URI getURIValue() {
        if (getType() == null || !"URI".equals(getType())) {
            throw new IllegalArgumentException("Invalid type declaration");
        }

        URI res;

        if (values == null || values.isEmpty() || values.iterator().next() == null) {
            res = null;
        } else {
            res = (URI) values.iterator().next();
        }

        return res;
    }

    public File getFileValue() {
        if (getType() == null || !"File".equals(getType())) {
            throw new IllegalArgumentException("Invalid type declaration");
        }

        File res;

        if (values == null || values.isEmpty() || values.iterator().next() == null) {
            res = null;
        } else {
            res = (File) values.iterator().next();
        }

        return res;
    }
}
