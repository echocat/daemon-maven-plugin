/*****************************************************************************************
 * *** BEGIN LICENSE BLOCK *****
 *
 * Version: MPL 2.0
 *
 * echocat Daemon Maven Plugin, Copyright (c) 2012-2013 echocat
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * *** END LICENSE BLOCK *****
 ****************************************************************************************/

package org.echocat.maven.plugins.daemon;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.echocat.jomon.process.local.LocalGeneratedProcess;
import org.echocat.jomon.process.local.daemon.LocalProcessDaemon;

import javax.annotation.Nonnull;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PRE_INTEGRATION_TEST;
import static org.echocat.jomon.runtime.util.ResourceUtils.closeQuietly;
import static org.echocat.maven.plugins.daemon.Daemon.Requirement.daemon;

@Mojo(
    name = "start",
    defaultPhase = PRE_INTEGRATION_TEST

)
@SuppressWarnings("InstanceVariableNamingConvention")
public class StartDaemonMojo extends DaemonMojoSupport {

    @Parameter(property = "daemon.output", defaultValue = "redirectToFile|file=${project.build.directory}/logs/daemon.log|recordProcessStarted=true|recordProcessTerminated=true", required = true)
    private String output;

    @Override
    public void call() throws Exception {
        final LocalProcessDaemon<?> daemon = getProcessDaemonRepository().generate(daemon(getExecutable())
            .withArguments(getArguments())
            .withStreamListener(getOutput()));
        registerPid(daemon.getProcess());
    }

    protected void registerPid(@Nonnull LocalGeneratedProcess process) throws MojoExecutionException {
        final String pidProperty = getPidProperty();
        validatePidProperty(process, pidProperty);
        getProperties().setProperty(pidProperty, Long.toString(process.getId()));
    }

    protected void validatePidProperty(@Nonnull LocalGeneratedProcess process, @Nonnull String pidProperty) throws MojoExecutionException {
        final String pidValue = getProperties().getProperty(pidProperty);
        if (!isEmpty(pidValue)) {
            closeQuietly(process);
            throw new MojoExecutionException("The project property '" + pidProperty + "' is already set to '" + pidValue + "' this indicates double usage of this maven plugin without configuring another pid property.");
        }
    }

    @Nonnull
    protected String getOutput() throws MojoExecutionException {
        final String result = output;
        if (result == null) {
            throw new MojoExecutionException("No output provided.");
        }
        return result;
    }

}
