package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.util.ArrayUtil;
import com.takeshi.pojo.bo.GeoPointBO;
import lombok.SneakyThrows;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ByteOrderValues;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>GeoPointTypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = GeoPointTypeHandler.class)</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class GeoPointTypeHandler extends BaseTypeHandler<GeoPointBO> {

    /**
     * geometry factory
     */
    private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    /**
     * wkb writer
     */
    private final static WKBWriter WKB_WRITER = new WKBWriter(2, ByteOrderValues.LITTLE_ENDIAN);

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, GeoPointBO geoPointBO, JdbcType jdbcType) throws SQLException {
        Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(geoPointBO.getLon(), geoPointBO.getLat()));
        byte[] bytes = WKB_WRITER.write(point);
        byte[] wkb = new byte[bytes.length + 4];
        ByteOrderValues.putInt(point.getSRID(), wkb, ByteOrderValues.LITTLE_ENDIAN);
        System.arraycopy(bytes, 0, wkb, 4, bytes.length);
        preparedStatement.setBytes(i, wkb);
    }

    @Override
    public GeoPointBO getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return this.geoPointFromBytes(resultSet.getBytes(s));
    }

    @Override
    public GeoPointBO getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return this.geoPointFromBytes(resultSet.getBytes(i));
    }

    @Override
    public GeoPointBO getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return this.geoPointFromBytes(callableStatement.getBytes(i));
    }

    @SneakyThrows
    private GeoPointBO geoPointFromBytes(byte[] wkb) {
        if (ArrayUtil.isEmpty(wkb)) {
            return null;
        }
        int srid = ByteBuffer.wrap(wkb).order(ByteOrder.LITTLE_ENDIAN).getInt();
        byte[] geometryBytes = new byte[wkb.length - 4];
        System.arraycopy(wkb, 4, geometryBytes, 0, geometryBytes.length);
        WKBReader wkbReader = new WKBReader(new GeometryFactory(new PrecisionModel(), srid));
        Point point = (Point) wkbReader.read(geometryBytes);
        return new GeoPointBO(point.getX(), point.getY());
    }

}
