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

import org.echocat.jomon.process.local.daemon.LocalProcessDaemonRepository;

import javax.annotation.Nonnull;

import static org.echocat.jomon.process.local.daemon.LocalProcessDaemonRepository.processDaemonRepository;

public abstract class HanldingDaemonMojoSupport extends MojoSupport {

    private LocalProcessDaemonRepository _processDaemonRepository;

    @Nonnull
    public LocalProcessDaemonRepository getProcessDaemonRepository() {
        synchronized (this) {
            if (_processDaemonRepository == null) {
                _processDaemonRepository = processDaemonRepository();
            }
            return _processDaemonRepository;
        }
    }


}
