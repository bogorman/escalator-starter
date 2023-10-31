
--set these yourself
INSERT INTO public.users (email, encrypted_password, reset_password_token, remember_token, remember_created_at, sign_in_count, current_sign_in_at, last_sign_in_at, current_sign_in_ip, last_sign_in_ip, confirmation_token, confirmation_at, confirmation_sent_at, password_salt, full_name, initials, two_factor_auth_active, two_factor_auth_secret, role, status, created_at, updated_at, username) 
VALUES ('info@escalatorstarter.com', 'password', 'random', 'random', NOW(), 0, NOW(), NOW(), NULL, NULL, NULL, NOW(), NOW(), NULL, 'Info', 'INFO', false, NULL, NULL, 'true', now(), now(), 'info');

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







