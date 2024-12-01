-- Drop database nếu đã tồn tại
DROP DATABASE IF EXISTS authentication;

-- Tạo lại cơ sở dữ liệu mới
CREATE DATABASE IF NOT EXISTS authentication;
USE authentication;

-- Drop các bảng nếu đã tồn tại
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

-- Drop trigger nếu đã tồn tại
DROP TRIGGER IF EXISTS before_user_update;

-- Drop các thủ tục (procedures) nếu đã tồn tại
DROP PROCEDURE IF EXISTS registerUser;
DROP PROCEDURE IF EXISTS updatePassword;
DROP PROCEDURE IF EXISTS assignRoleToUser;
DROP PROCEDURE IF EXISTS getUserByUsername;
DROP PROCEDURE IF EXISTS checkUserExistence;

-- Tạo bảng `users` để lưu trữ thông tin người dùng
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- ID người dùng
    username VARCHAR(255) NOT NULL UNIQUE,    -- Tên đăng nhập (unique)
    password VARCHAR(255) NOT NULL,           -- Mật khẩu (mã hóa)
    email VARCHAR(255) NOT NULL UNIQUE,       -- Email người dùng (optional, unique)
    google_id VARCHAR(255) UNIQUE,            -- ID Google (nếu đăng nhập qua Google)
    facebook_id VARCHAR(255) UNIQUE,          -- ID Facebook (nếu đăng nhập qua Facebook)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- Thời gian tạo
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- Thời gian cập nhật
);

-- Tạo bảng `roles` để lưu trữ vai trò
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- ID vai trò
    role_name VARCHAR(50) NOT NULL UNIQUE   -- Tên vai trò (ví dụ: ROLE_USER, ROLE_ADMIN)
);

-- Tạo bảng `user_roles` để lưu trữ mối quan hệ giữa người dùng và vai trò
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,                 -- ID người dùng
    role_id BIGINT NOT NULL,                 -- ID vai trò
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Trigger tự động cập nhật `updated_at` khi có thay đổi thông tin người dùng
DELIMITER $$

CREATE TRIGGER before_user_update
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END $$

DELIMITER ;

-- Thủ tục đăng ký người dùng
DELIMITER $$

CREATE PROCEDURE registerUser (
    IN p_username VARCHAR(255),
    IN p_password VARCHAR(255),
    IN p_email VARCHAR(255)
)
BEGIN
    -- Kiểm tra xem username hoặc email đã tồn tại chưa
    IF EXISTS (SELECT 1 FROM users WHERE username = p_username) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Username already exists';
    END IF;

    IF EXISTS (SELECT 1 FROM users WHERE email = p_email) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Email already exists';
    END IF;

    -- Thêm người dùng mới vào bảng users
    INSERT INTO users (username, password, email)
    VALUES (p_username, p_password, p_email);
END $$

DELIMITER ;

-- Thủ tục cập nhật mật khẩu người dùng
DELIMITER $$

CREATE PROCEDURE updatePassword (
    IN p_user_id BIGINT,
    IN p_new_password VARCHAR(255)
)
BEGIN
    -- Cập nhật mật khẩu người dùng
    UPDATE users SET password = p_new_password, updated_at = CURRENT_TIMESTAMP
    WHERE id = p_user_id;
    
    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'User not found';
    END IF;
END $$

DELIMITER ;

-- Thủ tục gán vai trò cho người dùng
DELIMITER $$

CREATE PROCEDURE assignRoleToUser (
    IN p_user_name varchar(255),
    IN p_role_name VARCHAR(50)
)
BEGIN
    DECLARE v_role_id BIGINT;
    declare user_id bigint;

    -- Lấy ID của vai trò từ bảng roles
    SELECT id INTO v_role_id FROM roles WHERE role_name = p_role_name;
    
    select id into user_id from users where username = p_user_name;
    
    IF v_role_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Role not found';
    END IF;
    
    IF user_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'User not found';
    END IF;

    -- Gán vai trò cho người dùng
    INSERT INTO user_roles (user_id, role_id) VALUES (user_id, v_role_id);
END $$


DELIMITER ;

drop procedure assignRoleToUser;

-- Thủ tục lấy thông tin người dùng theo username
DELIMITER $$

CREATE PROCEDURE getUserByUsername (
    IN p_username VARCHAR(255)
)
BEGIN
    -- Lấy thông tin người dùng từ username
    SELECT id, username, email, created_at, updated_at
    FROM users
    WHERE username = p_username;
END $$

DELIMITER ;

-- Thủ tục kiểm tra xem username và email có tồn tại không
DELIMITER $$

CREATE PROCEDURE checkUserExistence (
    IN p_username VARCHAR(255),
    IN p_email VARCHAR(255)
)
BEGIN
    -- Kiểm tra nếu username đã tồn tại
    IF EXISTS (SELECT 1 FROM users WHERE username = p_username) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Username already exists';
    END IF;

    -- Kiểm tra nếu email đã tồn tại
    IF EXISTS (SELECT 1 FROM users WHERE email = p_email) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Email already exists';
    END IF;
END $$
roles
DELIMITER ;


DELIMITER $$

CREATE PROCEDURE findRolesByUsername(IN username varchar(255))
BEGIN
    SELECT r.*
	FROM authentication.roles r
	JOIN authentication.user_roles ur ON ur.role_id = r.id
	JOIN authentication.users u ON u.id = ur.user_id
	WHERE u.username = username;
END $$

DELIMITER ;


-- TESTING

