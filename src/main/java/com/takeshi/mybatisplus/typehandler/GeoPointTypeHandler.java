package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.util.ArrayUtil;
import com.takeshi.pojo.bo.GeoPointBO;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.locationtech.jts.io.ByteArrayInStream;
import org.locationtech.jts.io.ByteOrderValues;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>GeoPointTypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = GeoPointTypeHandler.class)</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class GeoPointTypeHandler extends BaseTypeHandler<GeoPointBO> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, GeoPointBO geoPointBO, JdbcType jdbcType) throws SQLException {
        preparedStatement.setBytes(i, toWkb(geoPointBO));
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

    /**
     * 查询数据库时也直接传toWkb后的byte数组进行查询即可
     *
     * @param geoPointBO geoPointBO
     * @return byte[]
     */
    public static byte[] toWkb(GeoPointBO geoPointBO) {
        // 转Geometry
        CoordinateArraySequence coordinateArraySequence = new CoordinateArraySequence(new Coordinate[]{new CoordinateXY(geoPointBO.getLon(), geoPointBO.getLat())});
        Point point = new Point(coordinateArraySequence, new GeometryFactory());
        // Geometry转WKB
        byte[] geometryBytes = new WKBWriter(2, ByteOrderValues.LITTLE_ENDIAN, false).write(point);
        // 设置SRID为mysql默认的 0
        byte[] wkb = new byte[geometryBytes.length + 4];
        wkb[0] = wkb[1] = wkb[2] = wkb[3] = 0;
        System.arraycopy(geometryBytes, 0, wkb, 4, geometryBytes.length);
        return wkb;
    }

    /**
     * 查询数据库时也直接传toWkb后的byte数组进行查询即可
     *
     * @param geoPointBOList geoPointBOList
     * @return List
     */
    public static List<byte[]> toWkb(List<GeoPointBO> geoPointBOList) {
        return geoPointBOList.stream().map(GeoPointTypeHandler::toWkb).collect(Collectors.toList());
    }

    private GeoPointBO geoPointFromBytes(byte[] bytes) {
        try {
            if (ArrayUtil.isEmpty(bytes)) {
                return null;
            }
            ByteArrayInStream byteArrayInStream = new ByteArrayInStream(bytes);
            // 字节数组前4个字节表示SRID去掉
            byteArrayInStream.read(new byte[4]);
            WKBReader wkbReader = new WKBReader();
            Point point = wkbReader.read(byteArrayInStream).getInteriorPoint();
            return new GeoPointBO(point.getX(), point.getY());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
