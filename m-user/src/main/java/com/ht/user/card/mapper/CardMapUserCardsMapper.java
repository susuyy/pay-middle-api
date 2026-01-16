package com.ht.user.card.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.CardMapUserCards;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.user.card.vo.CardMapUserCardsVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 用户，卡绑定关系 Mapper 接口
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Mapper
public interface CardMapUserCardsMapper extends BaseMapper<CardMapUserCards> {

    /**
     * 查询用户与卡的关联信息
     *
     * @param cardCode
     * @param userId
     * @return
     */
    @Select("SELECT * FROM card_map_user_cards WHERE user_id = #{userId} AND card_code = #{cardCode}")
    CardMapUserCards queryByCardCodeAndUserId(@Param("cardCode") String cardCode, @Param("userId") Long userId);

    /**
     * 根据userId 查询用户下的卡信息
     *
     * @param userId
     * @return
     */
    @Select("SELECT * FROM card_map_user_cards WHERE user_id = #{userId}")
    List<CardMapUserCards> selectByUserId(@Param("userId") long userId);


    /**
     * 根据 用户id 和 商户码 获取卡券数量
     *
     * @param userId
     * @param merchantCode
     * @return
     */
    @Select("SELECT COUNT(id) FROM card_map_user_cards where user_id = #{userId} and merchant_code = #{merchantCode} AND type <> 'account' AND state = 'un_use' ")
    Integer selectCardNum(@Param("userId") Long userId, @Param("merchantCode") String merchantCode);

    /**
     * 根据用户id 卡分类码  卡分配类型  查询 卡信息
     *
     * @param userId
     * @param categoryCode
     * @param type
     * @return
     */
    @Select("SELECT * FROM card_map_user_cards WHERE category_code = #{categoryCode} AND user_id = #{userId} and type = #{type}")
    CardMapUserCards selectByUserIdAndCategoryCodeAndType(@Param("userId") Long userId, @Param("categoryCode") String categoryCode, @Param("type") String type);

    /**
     * 根据用户id 和商户编码 查询用户卡列表
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Select("SELECT * FROM card_map_user_cards WHERE user_id = #{userId} AND merchant_code=#{merchantCode} AND (state = #{state} OR state = '正常') ORDER BY create_at DESC")
    List<CardMapUserCards> selectByUserIdAndMerchantCode(@Param("userId") long userId, @Param("merchantCode") String merchantCode, @Param("state") String state);

    /**
     * 根据用户id 和商户编码 查询用户卡列表 (已绑定实体卡)
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Select("SELECT * FROM card_map_user_cards WHERE user_id = #{userId} AND merchant_code=#{merchantCode} AND state = #{state} And ic_card_id is null ORDER BY create_at DESC")
    List<CardMapUserCards> selectByUserIdAndMerchantCodeNotIc(@Param("userId") long userId, @Param("merchantCode") String merchantCode, @Param("state") String state);

    /**
     * 根据实体卡号id 和用户id  查询实体卡号绑定关系
     * @param icCardId
     * @param userId
     * @return
     */
    @Select("SELECT * FROM card_map_user_cards WHERE user_id = #{userId} AND ic_card_id = #{icCardId}")
    CardMapUserCards selectByIcCardIdAndUserId(@Param("icCardId") String icCardId,@Param("userId") Long userId);

    /**
     * 根据用户id 和 卡编号 更新实体 卡号
     * @param cardNo
     * @param userId
     * @param icCardId
     */
    @Update("UPDATE card_map_user_cards SET ic_card_id = #{icCardId} where card_no = #{cardNo} AND user_id = #{userId}")
    void updateIcCardIdByCardNoAndUserId(@Param("cardNo") String cardNo,@Param("userId") Long userId,@Param("icCardId") String icCardId);

    /**
     * 格局实体卡号 和 商户编码 查询用户卡信息相关
     * @param icCardId
     * @param merchantCode
     * @return
     */
    @Select("select * from card_map_user_cards where ic_card_id = #{icCardId} and merchant_code = #{merchantCode}")
    CardMapUserCards queryByIcCardIdAndMerchantCode(@Param("icCardId") String icCardId, @Param("merchantCode") String merchantCode);

    @Select("select * from card_map_user_cards where user_id = #{userId} and type = #{type}")
    CardMapUserCards selectByUserIdAndType(Long userId, String type);

    /**
     * pos 获取用户在商家下所有可用的 虚拟卡券
     * @param userId
     * @param merchantCode
     * @param type
     * @param state
     * @param objMerchantCode
     * @return
     */
    @Select("select cmc.* from card_map_user_cards cmc " +
            "LEFT JOIN card_cards cc on " +
            "cmc.card_code = cc.card_code " +
            "where cmc.user_id = #{userId}  and (cmc.merchant_code = #{merchantCode} or cmc.merchant_code = #{objMerchantCode})  and cmc.type = #{type} AND cmc.state = #{state} AND cc.type <> 'number' ")
    List<CardMapUserCards> selectByUserIdAndMerchantCodeAndTypeAndState(@Param("userId") Long userId, @Param("merchantCode") String merchantCode, @Param("type") String type, @Param("state") String state, @Param("objMerchantCode")String objMerchantCode);


    @Update("UPDATE card_map_user_cards SET state = #{state} WHERE card_no = #{cardNo}")
    void updateUserCardsState(String cardNo,String state);

    @Select({"<script>",
            " select count(id) from card_map_user_cards_trace where user_id =#{userId} ",
            " and card_code=#{cardCode} and batch_code=#{batchCode} ",
            "<if test='date != null and date != &quot;&quot;'>",
            " AND DATE_FORMAT(action_date,\"%Y-%m-%d\")=#{date}",
            "</if>",
            "</script>"})
    Integer getUserCardAmount(Long userId, String batchCode, String cardCode, String date);

    /**
     * 获取用户虚拟卡券列表 不包含计次券
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Select("SELECT cuc.* FROM card_map_user_cards cuc " +
            "LEFT JOIN card_cards cc on " +
            "cuc.card_code = cc.card_code "+
            "WHERE cuc.user_id = #{userId} AND cuc.merchant_code=#{merchantCode} AND cc.type <> 'number' AND (cuc.state = #{state} OR cuc.state = #{mallState}) ORDER BY create_at DESC")
    List<CardMapUserCards> selectByUserIdAndMerchantCodeNoNumber(@Param("userId") long userId,
                                                                 @Param("merchantCode") String merchantCode,
                                                                 @Param("state") String state,
                                                                 @Param("mallState") String mallState);

    /**
     * 获取用户的计次卡券列表
     * @param userId
     * @param merchantCode
     * @param cardType
     * @param userCardState
     * @param
     * @return
     */
    @Select("select cmu.*,cc.card_name as cardName,cc.type as cardCardsType,cmu.face_value as cardFaceValue,cc.price as price from card_map_user_cards cmu LEFT JOIN card_cards cc ON cmu.card_code = cc.card_code " +
            "where cmu.user_id = #{userId} AND cmu.merchant_code = #{merchantCode} AND cc.type = #{cardType} AND cmu.state=#{userCardState} AND cc.state= #{cardState} AND cmu.face_value > 0")
    List<CardMapUserCardsVO> selectUserNumberList(@Param("userId") Long userId,
                                                  @Param("merchantCode") String merchantCode,
                                                  @Param("cardType") String cardType,
                                                  @Param("userCardState") String userCardState,
                                                  @Param("cardState") String cardState);

    /**
     * 商城查询 我的卡券
     * @param page
     * @param queryWrapper
     * @return
     */
    @Select("select * from card_map_user_cards ${ew.customSqlSegment}")
    Page<CardMapUserCards> mallSelectUserCard(Page<CardMapUserCards> page,@Param(Constants.WRAPPER) QueryWrapper<CardMapUserCards> queryWrapper);

    /**
     * 搜索用户所有的type类型卡券
     * @param page
     * @param userId
     * @param merchantCodes
     * @param state
     * @param type
     * @return
     */
    @Select({"<script>select uc.*,c.validity_type,c.valid_from,c.valid_to,c.period_of_validity,c.valid_gap_after_applied,c.card_pic_url from card_map_user_cards uc left join card_map_merchant_cards mc on uc.card_code = mc.card_code "+
            " left join card_cards c on c.card_code=mc.card_code " +
            "WHERE uc.user_id = #{userId}\n" +
            "<if test='state!=null and state != &quot;&quot;'>"+
            "\t and uc.state = #{state} "+
            "</if>"+
            " and mc.type = #{type}"+
            " and uc.merchant_code in "+
            "<foreach item='item' index='index' collection='merchantCodes' open='(' separator=',' close=')'>" +
            " #{item} " +
            "</foreach>"+
            "</script>"})
    IPage<CardMapUserCards> getUserCardPage(Page<CardMapUserCards> page, Long userId, List<String> merchantCodes, String state, String type);

    /**
     * 查询用户商城购买卡券
     * @param page
     * @param queryWrapper
     * @return
     */
    @Select("select * from card_map_user_cards ${ew.customSqlSegment}")
    Page<CardMapUserCards> selectUserBuyCardList(Page<CardMapUserCards> page, @Param(Constants.WRAPPER) QueryWrapper<CardMapUserCards> queryWrapper);

    /**
     * 查询 指定条目的订单明细的用户卡券
     * @param refKey
     * @param state
     * @param quantity
     * @return
     */
    @Select("select * from card_map_user_cards where ref_source_key = #{refKey} and state = #{state} limit #{quantity}")
    List<CardMapUserCards> selectByRefKeyAndStateLimit(@Param("refKey") Long refKey, @Param("state")String state, @Param("quantity")int quantity);
}
