/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.logging.deployments;

import org.jboss.as.logging.LoggingModuleDependency;
import org.jboss.as.logging.logging.LoggingLogger;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;

/**
 * Adds the default logging dependencies to the deployment.
 * <p/>
 * Default Dependencies:
 * <ul>
 * <li>org.jboss.logging</li>
 * <li>org.apache.commons.logging</li>
 * <li>org.apache.log4j</li>
 * <li>org.sfl4j</li>
 * <li>org.jboss.logging-jul-to-slf4j-stub</li>
 * </ul>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class LoggingDependencyDeploymentProcessor implements DeploymentUnitProcessor {

    @Override
    public void deploy(final DeploymentPhaseContext phaseContext) {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        final ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
        final ModuleLoader moduleLoader = Module.getBootModuleLoader();
        // Add the logging modules
        for (LoggingModuleDependency moduleDep : LoggingModuleDependency.values()) {
            final String moduleId = moduleDep.getModuleName();
            try {
                LoggingLogger.ROOT_LOGGER.tracef("Adding module '%s' to deployment '%s'", moduleId, deploymentUnit.getName());
                moduleLoader.loadModule(moduleId);
                moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, moduleId, false, false, moduleDep.isImportServices(), false));
            } catch (ModuleLoadException ex) {
                LoggingLogger.ROOT_LOGGER.debugf("Module not found: %s", moduleId);
            }
        }
    }

    @Override
    public void undeploy(final DeploymentUnit context) {
    }
}
