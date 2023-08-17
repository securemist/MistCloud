-- 函数，递归查询出指定文件夹的所有子文件夹
delimiter $$
DROP FUNCTION IF EXISTS get_chlid_lists$$
CREATE FUNCTION get_chlid_lists(in_id bigint)
    RETURNS VARCHAR(10000)
    DETERMINISTIC
BEGIN
    DECLARE ids VARCHAR(10000) DEFAULT '';
    DECLARE tempids VARCHAR(10000);

    SET tempids = in_id;
    WHILE tempids IS NOT NULL
        DO
            SET ids = CONCAT_WS(',', ids, tempids);
SELECT GROUP_CONCAT(id) INTO tempids FROM sys_folder WHERE FIND_IN_SET(parent_id, tempids) > 0;
end WHILE;
RETURN ids;

END $$
delimiter;



-- 查找文件夹所在路径，当 parent_id = 0 是说明位于根路径
delimiter $$
drop function if exists get_parent_list$$
create function get_parent_list(in_id bigint) returns varchar(10000)
begin
    declare ids varchar(10000);
    declare tempid bigint;

    set tempid = in_id;
    while tempid != 0 do
        set ids = CONCAT_WS(',',ids,tempid);
        select parent_id into tempid from sys_folder where id=tempid;
end while;
return ids;
end
$$
delimiter ;





-- 所有的子文件夹 id
SET @folder_ids = (SELECT GROUP_CONCAT(id)
                   FROM sys_folder
                   WHERE FIND_IN_SET(id, get_chlid_lists(1)));

-- 所有的子文件夹内的文件 id
SET @file_ids = (SELECT GROUP_CONCAT(id)
                 FROM sys_file
                 WHERE FIND_IN_SET(folder_id, @folder_ids));

-- 一一删除
UPDATE sys_folder
SET deleted      = 1,
    deleted_time = NOW()
WHERE FIND_IN_SET(id, @folder_ids);

UPDATE sys_file
SET deleted      = 1,
    deleted_time = NOW()
WHERE FIND_IN_SET(id, @file_ids);

-- 最后排除掉根文件夹
UPDATE sys_folder
SET deleted = 0
WHERE id = 1