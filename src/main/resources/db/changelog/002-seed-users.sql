-- changeset julia:002-seed-users

INSERT INTO public.users (username, "password", "role")
VALUES
  ('admin', '$2a$10$rPa6Yw.WGD7SD7Fsjry9xOvF/x2s934VyakuubnoyCMTItftypfNG', 'ADMIN'),
  ('user',  '$2a$10$5StY2tV6/6oBlZQArDVbi..B3RSRijSZ8IluOVGbvo6Hib.KyL1Yu',  'USER')
ON CONFLICT (username) DO NOTHING;
