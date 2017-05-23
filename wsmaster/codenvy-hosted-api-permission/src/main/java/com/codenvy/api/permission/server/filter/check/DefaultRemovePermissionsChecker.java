package com.codenvy.api.permission.server.filter.check;

import com.codenvy.api.permission.server.SuperPrivilegesChecker;

import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.subject.Subject;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.codenvy.api.permission.server.AbstractPermissionsDomain.SET_PERMISSIONS;

/**
 * @author Anton Korneta
 */
@Singleton
public class DefaultRemovePermissionsChecker implements RemovePermissionsChecker {

    private final SuperPrivilegesChecker superPrivilegesChecker;

    @Inject
    public DefaultRemovePermissionsChecker(SuperPrivilegesChecker superPrivilegesChecker) {
        this.superPrivilegesChecker = superPrivilegesChecker;
    }

    @Override
    public boolean isPermitted(String user, String domain, String instance) {
        final Subject subject = EnvironmentContext.getCurrent().getSubject();
        return subject.getUserId().equals(user)
               || superPrivilegesChecker.isPrivilegedToManagePermissions(domain)
               || subject.hasPermission(domain, instance, SET_PERMISSIONS);
    }
}
