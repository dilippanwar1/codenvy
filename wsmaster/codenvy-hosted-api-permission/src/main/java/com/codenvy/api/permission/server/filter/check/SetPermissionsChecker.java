package com.codenvy.api.permission.server.filter.check;


import com.codenvy.api.permission.shared.model.Permissions;

/**
 * @author Anton Korneta
 */
public interface SetPermissionsChecker {

    boolean isPermitted(Permissions permissions);

}
