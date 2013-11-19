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
import org.echocat.jomon.process.local.daemon.LocalProcessDaemonQuery;

import javax.annotation.Nonnull;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.maven.plugins.annotations.LifecyclePhase.POST_INTEGRATION_TEST;

@Mojo(
    name = "stop",
    defaultPhase = POST_INTEGRATION_TEST
)
@SuppressWarnings("InstanceVariableNamingConvention")
public class StopDaemonMojo extends DaemonMojoSupport {

    @Override
    public void call() throws Exception {
        getProcessDaemonRepository().removeBy(query());
    }

    @Nonnull
    protected LocalProcessDaemonQuery query() throws MojoExecutionException {
        final String pidProperty = getPidProperty();
        final String plainPid = getProperties().getProperty(pidProperty);
        final LocalProcessDaemonQuery query = new LocalProcessDaemonQuery();
        if (isEmpty(plainPid)) {
            query
                .withExecutable(getExecutable())
                .whichStartsWithArguments(getArguments());
        } else {
            query
                .withPid(Long.valueOf(plainPid));
            getProperties().remove(pidProperty);
        }
        return query;
    }

}
