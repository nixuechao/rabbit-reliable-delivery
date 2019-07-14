package com.zolvces.delivery.demo.dao;

import com.zolvces.delivery.producer.MessageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author niXueChao
 * @date 2019/7/11 11:17.
 */
@Component
public class MessageService {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public MessageService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void save(MessageInfo messageInfo) {
        String sql = "insert into send_fail_message(id, exchange, fail_reason, first_send_time, last_send_time, message_body, message_properties, resend_times, routing_key) " +
                " values (:id, :exchange, :failReason, :firstSendTime, :lastSendTime, :messageBody, :messageProperties, :resendTimes, :routingKey)";


        SendFailMessageEntity entity = new SendFailMessageEntity();
        BeanUtils.copyProperties(messageInfo, entity);

        entity.setFirstSendTime(System.currentTimeMillis());
        entity.setLastSendTime(System.currentTimeMillis());
        entity.setResendTimes(0);


        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(entity));
    }

    public void deleteById(String id) {
        String sql = "delete from send_fail_message where id= :id";

        jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("id", id));
    }

    public void updateResendTimesAndLastSendTime(String id) {
        String sql = "update send_fail_message set resend_times=resend_times+1 ,last_send_time=:currentTime where id= :id";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("currentTime", System.currentTimeMillis())
                .addValue("id", id));
    }

    public void addFailReason(String id, String cause) {
        String sql = "update send_fail_message set fail_reason= :cause where id= :id";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("cause", cause));
    }

    public void saveUnRoutableMessage(String messageProperties, String messageBody, String exchange, String routingKey) {
        String sql = "insert into unable_route_message(id, create_time, exchange, message_body, message_properties, routing_key) " +
                "values (:id, :createTime, :exchange, :messageBody, :messageProperties, :routingKey)";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("id", UUID.randomUUID().toString())
                .addValue("createTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .addValue("exchange", exchange)
                .addValue("messageBody", messageBody)
                .addValue("messageProperties", messageProperties)
                .addValue("routingKey", routingKey));
    }

    /**
     * 从发送失败的消息中选择重发的
     * 1.选择重试次数小于等于3次的
     * 2.选择最后一次发送时间在两秒钟之前的
     *
     * @return
     */
    public List<MessageInfo> selectNeedResendMessage() {
        String sql = " select * from send_fail_message where last_send_time <= :endTime and resend_times < :resendTimes order by last_send_time asc ";

        MapSqlParameterSource parameter = new MapSqlParameterSource()
                .addValue("endTime", System.currentTimeMillis() - 2000)
                .addValue("resendTimes", 3);
        return jdbcTemplate.query(sql, parameter, new BeanPropertyRowMapper<>(MessageInfo.class));
    }

    public void saveIfNotExist(MessageInfo messageInfo) {
        String sql = "select id from send_fail_message where id=:id";

        List<String> idList = jdbcTemplate.query(sql,
                new MapSqlParameterSource().addValue("id", messageInfo.getId()),
                new SingleColumnRowMapper<>(String.class));
        if (idList == null || idList.size() == 0) {
            this.save(messageInfo);
        }
    }

    public void deleteIfExist(String id) {
        this.deleteById(id);
    }
}
