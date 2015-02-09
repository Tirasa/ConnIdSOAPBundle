/**
 * Copyright (C) ${project.inceptionYear} ConnId (connid-dev@googlegroups.com)
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
package net.tirasa.connid.bundles.soap.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.xml.ws.WebFault;

@WebFault(name = "ProvisioningException")
public class ProvisioningException extends Exception {

    private static final long serialVersionUID = -45401395331992786L;

    private String wrappedCause;

    public ProvisioningException(String msg) {
        super(msg);
    }

    public ProvisioningException(final String msg, final Throwable cause) {
        super(msg, cause);

        final StringWriter exceptionWriter = new StringWriter();
        exceptionWriter.write(cause.getMessage() + "\n\n");
        cause.printStackTrace(new PrintWriter(exceptionWriter));
        wrappedCause = exceptionWriter.toString();
    }

    @Override
    public String getMessage() {
        return wrappedCause == null ? super.getMessage() : wrappedCause;
    }

    public String getWrappedCause() {
        return wrappedCause;
    }

    public void setWrappedCause(final String wrappedCause) {
        this.wrappedCause = wrappedCause;
    }
}
