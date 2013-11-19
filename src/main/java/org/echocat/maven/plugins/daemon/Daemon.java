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
import org.echocat.jomon.process.CouldNotStartException;
import org.echocat.jomon.process.listeners.stream.StreamListener;
import org.echocat.jomon.process.local.LocalGeneratedProcess;
import org.echocat.jomon.process.local.LocalGeneratedProcessRequirement;
import org.echocat.jomon.process.local.LocalProcessRepository;
import org.echocat.jomon.process.local.daemon.LocalProcessDaemon;
import org.echocat.jomon.process.local.daemon.LocalProcessDaemonRequirement.Base;
import org.echocat.jomon.runtime.generation.Generator;
import org.echocat.maven.plugins.daemon.Daemon.Requirement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

import static org.echocat.jomon.process.listeners.stream.StreamListeners.streamListenerFor;
import static org.echocat.jomon.process.local.LocalGeneratedProcessRequirement.process;

public class Daemon extends LocalProcessDaemon<Requirement> {

    public Daemon(@Nonnull LocalProcessRepository processRepository, @Nonnull Requirement requirement) throws CouldNotStartException {
        super(processRepository, requirement);
    }

    @Nonnull
    @Override
    protected LocalGeneratedProcess generateProcess(@Nonnull Generator<LocalGeneratedProcess, LocalGeneratedProcessRequirement> dependencies, @Nonnull Requirement requirement) throws Exception {
        return dependencies.generate(process(requirement.getExecutable())
            .withArguments(requirement.getArguments())
            .whichIsNoDaemon()
        );
    }

    public static class Requirement extends Base<Daemon, Requirement> {

        @Nonnull
        private final File _executable;
        @Nullable
        private String[] _arguments;

        @Nonnull
        public static Requirement daemon(@Nonnull File executable) {
            return new Requirement(executable);
        }

        public Requirement(@Nonnull File executable) {
            super(Daemon.class);
            _executable = executable;
        }

        @Nonnull
        public Requirement withArguments(@Nullable String... arguments) {
            _arguments = arguments;
            return this;
        }

        @Nonnull
        public File getExecutable() {
            return _executable;
        }

        @Nullable
        public String[] getArguments() {
            return _arguments;
        }

        @Nonnull
        public Requirement withStreamListener(@Nonnull String configuration) throws MojoExecutionException {
            final StreamListener<LocalGeneratedProcess> listener = streamListenerFor(LocalGeneratedProcess.class, configuration);
            if (listener == null) {
                throw new MojoExecutionException("Illegal streamListener configured: " + configuration);
            }
            return withStreamListener(listener);
        }

    }
}
