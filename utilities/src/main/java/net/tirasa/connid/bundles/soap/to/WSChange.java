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
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
public class WSChange extends AbstractData {

    private static final long serialVersionUID = 7246925790810993679L;

    /**
     * Attributes changed.
     */
    private Set<WSAttributeValue> attributes;

    /**
     * ID of the change.
     */
    private int id;

    /**
     * Type of the change:
     * - CREATE_OR_UPDATE
     * - DELETE
     */
    private String type;

    public Set<WSAttributeValue> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Set<WSAttributeValue> attributes) {
        this.attributes = attributes;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
