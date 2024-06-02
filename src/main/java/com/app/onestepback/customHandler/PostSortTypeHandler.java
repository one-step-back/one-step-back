package com.app.onestepback.customHandler;

import com.app.onestepback.domain.type.post.PostSortType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostSortTypeHandler extends BaseTypeHandler<PostSortType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PostSortType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public PostSortType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String name = rs.getString(columnName);
        return name == null ? null : PostSortType.valueOf(name);
    }

    @Override
    public PostSortType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String name = rs.getString(columnIndex);
        return name == null ? null : PostSortType.valueOf(name);
    }

    @Override
    public PostSortType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String name = cs.getString(columnIndex);
        return name == null ? null : PostSortType.valueOf(name);
    }
}
