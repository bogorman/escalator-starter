-- ============================================================
-- USER & AUTHENTICATION
-- ============================================================

-- User Roles
INSERT INTO attributes (attr_type, ordering, description)
VALUES (('USER_ROLE', 'ADMIN'), 1, 'Administrator');

INSERT INTO attributes (attr_type, ordering, description)
VALUES (('USER_ROLE', 'USER'), 2, 'Standard user');

INSERT INTO attributes (attr_type, ordering, description)
VALUES (('USER_ROLE', 'TRADER'), 3, 'Trading user');

INSERT INTO attributes (attr_type, ordering, description)
VALUES (('USER_ROLE', 'VIEWER'), 4, 'Read-only viewer');

-- User Status
INSERT INTO attributes (attr_type, ordering, description)
VALUES (('USER_STATUS', 'ACTIVE'), 1, 'Active');

INSERT INTO attributes (attr_type, ordering, description)
VALUES (('USER_STATUS', 'SUSPENDED'), 2, 'Suspended');

INSERT INTO attributes (attr_type, ordering, description)
VALUES (('USER_STATUS', 'DELETED'), 3, 'Deleted');

INSERT INTO attributes (attr_type, ordering, description)
VALUES (('USER_STATUS', 'PENDING'), 4, 'Pending verification');


-- Insert session type attributes into attributes table first
INSERT INTO attributes (attr_type, ordering, description) VALUES
  (('USER_SESSION_TYPE', 'UI'), 1, 'Web browser user interface session');

-------

INSERT INTO public.attributes (attr_type,ordering,description)
VALUES ( ('WORK_TYPE', 'email') , null, 'EMAIL');

-------

INSERT INTO public.attributes (attr_type,ordering,description)
VALUES ( ('WORK_STATUS', 'pending') , null, 'pending');

INSERT INTO public.attributes (attr_type,ordering,description)
VALUES ( ('WORK_STATUS', 'working') , null, 'working');

INSERT INTO public.attributes (attr_type,ordering,description)
VALUES ( ('WORK_STATUS', 'complete') , null, 'complete');

INSERT INTO public.attributes (attr_type,ordering,description)
VALUES ( ('WORK_STATUS', 'failed') , null, 'failed');







--set these yourself
INSERT INTO public.users (email, encrypted_password, reset_password_token, remember_token, remember_created_at, sign_in_count, current_sign_in_at, last_sign_in_at, current_sign_in_ip, last_sign_in_ip, confirmation_token, confirmed_at, confirmation_sent_at, password_salt, full_name, initials, two_factor_auth_active, two_factor_auth_secret, role, status, created_at, updated_at) 
VALUES ('info@escalatorstarter.com', 'password', 'random', 'random', NOW(), 0, NOW(), NOW(), NULL, NULL, NULL, NOW(), NOW(), NULL, 'Info', 'INFO', false, NULL, ('USER_ROLE', 'USER'), ('USER_STATUS', 'ACTIVE'), now(), now());
