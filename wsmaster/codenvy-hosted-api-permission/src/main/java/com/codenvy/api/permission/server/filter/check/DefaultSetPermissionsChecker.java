package com.codenvy.api.permission.server.filter.check;

import com.codenvy.api.permission.server.SuperPrivilegesChecker;
import com.codenvy.api.permission.shared.model.Permissions;

import org.eclipse.che.commons.env.EnvironmentContext;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.codenvy.api.permission.server.AbstractPermissionsDomain.SET_PERMISSIONS;

/**
 * @author Anton Korneta
 */
@Singleton
public class DefaultSetPermissionsChecker implements SetPermissionsChecker {

    private final SuperPrivilegesChecker superPrivilegesChecker;

    @Inject
    public DefaultSetPermissionsChecker(SuperPrivilegesChecker superPrivilegesChecker) {
        this.superPrivilegesChecker = superPrivilegesChecker;
    }

    @Override
    public boolean isPermitted(Permissions permissions) {
        return superPrivilegesChecker.isPrivilegedToManagePermissions(permissions.getDomainId())
               || EnvironmentContext.getCurrent().getSubject().hasPermission(permissions.getDomainId(),
                                                                             permissions.getInstanceId(),
                                                                             SET_PERMISSIONS);
    }
}
