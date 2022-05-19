package com.zhmenko.ids.data.jdbc.clickhouse;

import com.zhmenko.ids.data.UserStatisticDao;
import com.zhmenko.ids.model.netflow.user.NetflowUserStatistic;
import com.zhmenko.ids.model.netflow.Protocol;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@AllArgsConstructor
public class ClickHouseJdbcUserStatisticDao implements UserStatisticDao {
    private final String FIND_USER_STATISTIC_BY_MAC_ADDRESS = "SELECT "+
            "       MAC_ADDRESS                                                                           ,\n" +
            "       count(if(DateDiff('second', TIMESTAMP , now()) <= ?, 1, NULL))    AS LAST_PACKET_COUNT,\n" +
            "       count(if(DateDiff('second', TIMESTAMP , now()) > ?, 1, NULL))     AS PACKET_COUNT,\n" +
            "       countDistinct(if(PROTOCOL_TYPE = 'TCP', DESTINATION_PORT, NULL))  AS TCP_UNIQUE_DESTINATION_PORT_PACKET_COUNT,\n" +
            "       countDistinct(if(PROTOCOL_TYPE = 'UDP', DESTINATION_PORT, NULL))  AS UDP_UNIQUE_DESTINATION_PORT_PACKET_COUNT,\n" +
            "       countDistinct(if(PROTOCOL_TYPE = 'ICMP', DESTINATION_PORT, NULL)) AS ICMP_UNIQUE_DESTINATION_PORT_PACKET_COUNT,\n" +
            "       countDistinct(if(PROTOCOL_TYPE = 'IGMP', DESTINATION_PORT, NULL)) AS IGMP_UNIQUE_DESTINATION_PORT_PACKET_COUNT,\n" +
            "       min(TIMESTAMP)                                                    AS OLDEST_PACKET_TIME\n" +
            "FROM USER_FLOW_DATA WHERE MAC_ADDRESS=? GROUP BY MAC_ADDRESS";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public NetflowUserStatistic findUserStatisticByMacAddress(String macAddress, long meanValueIntervalSecond) {
        NetflowUserStatistic netflowUserStatistic = null;
        try {
            netflowUserStatistic = jdbcTemplate.queryForObject(FIND_USER_STATISTIC_BY_MAC_ADDRESS,
                    (rs, rowNum) -> {
                        NetflowUserStatistic userStatistic = new NetflowUserStatistic();

                        userStatistic.setMacAddress(macAddress);
                        int idx = 1;
                        userStatistic.setLastPacketsCount(rs.getLong(++idx));
                        userStatistic.setPacketsCount(rs.getLong(++idx));

                        Map<Protocol, Long> uniqueDstPortCountMap = new HashMap<>();
                        uniqueDstPortCountMap.put(Protocol.TCP, rs.getLong(++idx));
                        uniqueDstPortCountMap.put(Protocol.UDP, rs.getLong(++idx));
                        uniqueDstPortCountMap.put(Protocol.ICMP, rs.getLong(++idx));
                        uniqueDstPortCountMap.put(Protocol.IGMP, rs.getLong(++idx));
                        userStatistic
                                .setProtocolUniqueDestinationPortCountMap(uniqueDstPortCountMap);
                        userStatistic.setOldestPacketTime(rs.getTimestamp(++idx));

                        return userStatistic;
                    },
                    meanValueIntervalSecond,
                    meanValueIntervalSecond,
                    macAddress);
        } catch (Exception e) { }
        return netflowUserStatistic;
    }
}
