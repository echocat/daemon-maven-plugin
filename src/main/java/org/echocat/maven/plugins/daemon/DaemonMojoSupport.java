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
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.echocat.jomon.process.ExecutableDiscovery.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.echocat.jomon.process.ExecutableDiscovery.executableDiscovery;

@SuppressWarnings({"InstanceVariableNamingConvention"})
public abstract class DaemonMojoSupport extends HanldingDaemonMojoSupport {

    @Parameter(property = "daemon.executable", required = true)
    private String executable;
    @Parameter(property = "daemon.executable.environmentVariablesPointingTo")
    private String[] environmentVariablesPointingToExecutable;
    @Parameter(property = "daemon.executable.subDirectories")
    private String[] subDirectoriesContainingExecutable;
    @Parameter(property = "daemon.executable.autoDetectExtensions", defaultValue = "true")
    private boolean autoDetectExtensionsOfExecutable;

    @Parameter(property = "daemon.arguments")
    private String[] arguments;
    @Parameter(property = "daemon.pidProperty", defaultValue = "daemon.pid")
    private String pidProperty;

    @Component(role = MavenProject.class, hint = "project")
    private MavenProject project;

    @Nonnull
    protected File getExecutable() throws MojoExecutionException {
        final String plainExecutable = executable;
        if (plainExecutable == null) {
            throw new MojoExecutionException("No executable set.");
        }
        final File result;
        final File potentialResult = new File(plainExecutable);
        if (potentialResult.canExecute()) {
            result = potentialResult;
        } else {
            result = executableDiscovery().discover(Task.executable(plainExecutable)
                .withinEnvironmentVariables(getEnvironmentVariablesPointingToExecutable())
                .searchInSubDirectories(getSubDirectoriesContainingExecutable())
                .autoDetectExtensions(isAutoDetectExtensionsOfExecutable())
            );
            if (result == null) {
                throw new MojoExecutionException("Could not find executable '" + plainExecutable + "'.");
            }
        }
        try {
            return result.getCanonicalFile();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not make path canonical of executable '" + result + "'.", e);
        }
    }

    @Nullable
    protected String[] getArguments() throws MojoExecutionException {
        return arguments;
    }

    @Nullable
    protected String[] getEnvironmentVariablesPointingToExecutable() {
        return environmentVariablesPointingToExecutable;
    }

    @Nullable
    protected String[] getSubDirectoriesContainingExecutable() {
        return subDirectoriesContainingExecutable;
    }

    protected boolean isAutoDetectExtensionsOfExecutable() {
        return autoDetectExtensionsOfExecutable;
    }

    @Nonnull
    protected String getPidProperty() throws MojoExecutionException {
        final String result = pidProperty;
        if (result == null) {
            throw new MojoExecutionException("No pidProperty set.");
        }
        return result;
    }

    @Nonnull
    protected MavenProject getProject() throws MojoExecutionException {
        final MavenProject result = project;
        if (result == null) {
            throw new MojoExecutionException("No project set.");
        }
        return result;
    }

    @Nonnull
    protected Properties getProperties() throws MojoExecutionException {
        return getProject().getProperties();
    }

}
