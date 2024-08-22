ALTER TABLE "options"
ALTER COLUMN "iscorrect" TYPE boolean
USING CASE WHEN "iscorrect" = 'true' THEN TRUE ELSE FALSE END;