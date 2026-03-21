-- Add role column to app_users with default ADMIN for existing users
ALTER TABLE app_users ADD COLUMN role VARCHAR(30) DEFAULT 'ADMIN';

-- Create user_groups table
CREATE TABLE user_groups (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_user_groups_org_id ON user_groups(organization_id);

-- Create user_group_memberships table
CREATE TABLE user_group_memberships (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL REFERENCES user_groups(id),
    user_id UUID NOT NULL REFERENCES app_users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (group_id, user_id)
);

CREATE INDEX idx_ugm_group_id ON user_group_memberships(group_id);
CREATE INDEX idx_ugm_user_id ON user_group_memberships(user_id);
