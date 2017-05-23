package com.codenvy.api.permission.server.filter.check;

import com.google.common.collect.ImmutableMap;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Anton Korneta
 */
@Singleton
public class DomainsPermissionsCheckers {

    private final ImmutableMap<String, SetPermissionsChecker>    domain2setPermissionsChecker;
    private final DefaultSetPermissionsChecker                   defaultPermissionsChecker;
    private final ImmutableMap<String, RemovePermissionsChecker> domain2removePermissionsChecker;
    private final DefaultRemovePermissionsChecker                defaultRemovePermissionsChecker;

    @Inject
    public DomainsPermissionsCheckers(ImmutableMap<String, SetPermissionsChecker> domain2setPermissionsChecker,
                                      DefaultSetPermissionsChecker defaultPermissionsChecker,
                                      ImmutableMap<String, RemovePermissionsChecker> domain2removePermissionsChecker,
                                      DefaultRemovePermissionsChecker defaultRemovePermissionsChecker) {
        this.domain2setPermissionsChecker = domain2setPermissionsChecker;
        this.defaultPermissionsChecker = defaultPermissionsChecker;
        this.domain2removePermissionsChecker = domain2removePermissionsChecker;
        this.defaultRemovePermissionsChecker = defaultRemovePermissionsChecker;
    }

    public SetPermissionsChecker getSetChecker(String domain) {
        if (domain2setPermissionsChecker.containsKey(domain)) {
            return domain2setPermissionsChecker.get(domain);
        }
        return defaultPermissionsChecker;
    }

    public RemovePermissionsChecker getRemoveChecker(String domain) {
        if (domain2removePermissionsChecker.containsKey(domain)) {
            return domain2removePermissionsChecker.get(domain);
        }
        return defaultRemovePermissionsChecker;
    }

}
