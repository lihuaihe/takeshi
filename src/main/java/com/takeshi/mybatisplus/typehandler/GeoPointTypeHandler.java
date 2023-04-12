package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.util.ArrayUtil;
import com.takeshi.pojo.vo.GeoPointVO;
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

/**
 * <p>GeoPointTypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = GeoPointTypeHandler.class)</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class GeoPointTypeHandler extends BaseTypeHandler<GeoPointVO> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, GeoPointVO geoPointVO, JdbcType jdbcType) throws SQLException {
        // 转Geometry
        CoordinateArraySequence coordinateArraySequence = new CoordinateArraySequence(new Coordinate[]{new CoordinateXY(geoPointVO.getLon(), geoPointVO.getLat())});
        Point point = new Point(coordinateArraySequence, new GeometryFactory());
        // Geometry转WKB
        byte[] geometryBytes = new WKBWriter(2, ByteOrderValues.LITTLE_ENDIAN, false).write(point);
        // 设置SRID为mysql默认的 0
        byte[] wkb = new byte[geometryBytes.length + 4];
        wkb[0] = wkb[1] = wkb[2] = wkb[3] = 0;
        System.arraycopy(geometryBytes, 0, wkb, 4, geometryBytes.length);
        preparedStatement.setBytes(i, wkb);
    }

    @Override
    public GeoPointVO getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return this.geoPointFromBytes(resultSet.getBytes(s));
    }

    @Override
    public GeoPointVO getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return this.geoPointFromBytes(resultSet.getBytes(i));
    }

    @Override
    public GeoPointVO getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return this.geoPointFromBytes(callableStatement.getBytes(i));
    }

    private GeoPointVO geoPointFromBytes(byte[] bytes) {
        try {
            if (ArrayUtil.isEmpty(bytes)) {
                return null;
            }
            ByteArrayInStream byteArrayInStream = new ByteArrayInStream(bytes);
            // 字节数组前4个字节表示srid去掉
            byteArrayInStream.read(new byte[4]);
            WKBReader wkbReader = new WKBReader();
            Point point = wkbReader.read(byteArrayInStream).getInteriorPoint();
            return new GeoPointVO(point.getX(), point.getY());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
