# SSM web server authentication methods

## LDAP

### Common options

| Name                                                     | Default      | Description |
|----------------------------------------------------------|--------------|-------------|
| smart.rest.server.auth.ldap.enabled                      | false        |             |
| smart.rest.server.security.ldap.url                      |              |             |
| smart.rest.server.security.ldap.search.base              |              |             |
| smart.rest.server.security.ldap.user.search.base         |              |             |
| smart.rest.server.security.ldap.group.search.base        |              |             |
| smart.rest.server.security.ldap.auth.type                | BIND         |             |
| smart.rest.server.security.ldap.bind.user                |              |             |
| smart.rest.server.security.ldap.bind.password            |              |             |
| smart.rest.server.security.ldap.user.attributes.password | userPassword |             |

### Using custom query

**Trigger option**: `smart.rest.server.security.ldap.user.search.filter`

If any entry is found during the search using the provided LDAP filter, SSM user will be authenticated.

| Name                                               | Default | Description |
|----------------------------------------------------|---------|-------------|
| smart.rest.server.security.ldap.user.search.filter |         |             |

### Using membership attribute of user

**Trigger option**: `smart.rest.server.security.ldap.user.attributes.membership`

1. Fetch DNs of groups (entry with `objectClass` equal to the value of `ldap.group.object-class` option) with the name
   attribute (specified in the `ldap.group.attributes.name` option) equal to any value of the `ldap.user.search.groups`
   option
2. Check is there any user (entry with `objectClass` equal to any value of `ldap.user.object-classes` option) with the
   membership attribute (specified in the `ldap.user.attributes.membership` option) equal to any of the groups DNs from
   previous step, and
   with name attribute (specified in the `ldap.user.attributes.name` option) equal to SSM username
   If a user entry and any group entry exist, SSM user will be authenticated.

| Name                                                       | Default      | Description |
|------------------------------------------------------------|--------------|-------------|
| smart.rest.server.security.ldap.user.attributes.name       | uid          |             |
| smart.rest.server.security.ldap.user.object-classes        | person       |             |
| smart.rest.server.security.ldap.user.attributes.membership | memberOf     |             |
| smart.rest.server.security.ldap.group.object-class         | groupOfNames |             |
| smart.rest.server.security.ldap.group.attributes.name      | cn           |             |
| smart.rest.server.security.ldap.user.search.groups         |              |             |

### Using member attribute of group

**Trigger option**: `smart.rest.server.security.ldap.group.attributes.member`

1. Fetch DN of user (entry with `objectClass` equal to any value of `ldap.user.object-classes` option) with
   name attribute (specified in the `ldap.user.attributes.name` option) equal to SSM username
2. Check are there any group (entry with `objectClass` equal to the value of `ldap.group.object-class` option) with the
   name attribute (specified in the `ldap.group.attributes.name` option) equal to any value of
   the `ldap.user.search.groups` option with
   member attribute (specified in the `ldap.group.attributes.member` option) equal to user DN from previous step.
   If a user entry and any group entry exist, SSM user will be authenticated.

| Name                                                    | Default      | Description |
|---------------------------------------------------------|--------------|-------------|
| smart.rest.server.security.ldap.user.attributes.name    | uid          |             |
| smart.rest.server.security.ldap.user.object-classes     | person       |             |
| smart.rest.server.security.ldap.group.object-class      | groupOfNames |             |
| smart.rest.server.security.ldap.group.attributes.name   | cn           |             |
| smart.rest.server.security.ldap.group.attributes.member | member       |             |
| smart.rest.server.security.ldap.user.search.groups      |              |             |

### Using name attribute of user

Check is there any user (entry with `objectClass` equal to any value of `ldap.user.object-classes` option) with
name attribute (specified in the `ldap.user.attributes.name` option) equal to SSM username.
If any user entry exists, SSM user will be authenticated.

| Name                                                 | Default | Description |
|------------------------------------------------------|---------|-------------|
| smart.rest.server.security.ldap.user.attributes.name | uid     |             |
| smart.rest.server.security.ldap.user.object-classes  | person  |             |

