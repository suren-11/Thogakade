package com.seekerscloud.pos.dao;

import com.seekerscloud.pos.entity.SuperEntity;

import java.sql.SQLException;

public interface CrudDao<T,ID> {
    public boolean save(T t);
    public boolean delete(ID id);
    public boolean update(T t);
}
