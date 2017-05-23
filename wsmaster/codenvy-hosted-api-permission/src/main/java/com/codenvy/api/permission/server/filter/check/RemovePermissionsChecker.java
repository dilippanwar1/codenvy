package com.codenvy.api.permission.server.filter.check;

/**
 * @author Anton Korneta
 */
public interface RemovePermissionsChecker {

    boolean isPermitted(String user, String domain, String instance);

}
