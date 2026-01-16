package com.ht.user.card.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.user.admin.vo.*;
import com.ht.user.card.entity.CardMapMerchantCards;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 商家卡券 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Mapper
public interface CardMapMerchantCardsMapper extends BaseMapper<CardMapMerchantCards> {

    /**
     * 根据商家编码查询卡片列表
     *
     * @param merchantCode
     * @return
     */
    @Select("select merchant_code,card_code,card_type,card_name,type,state,card_face_value,price,on_sale_state,create_at,update_at from card_map_merchant_cards where merchant_code = #{merchantCode}")
    List<CardMapMerchantCards> selectListByMerchantCode(String merchantCode);

    /**
     * 根据卡号和商户编码 查询商户-卡 关联信息
     *
     * @param cardCode
     * @param merchantCode
     * @return
     */
    @Select("select " +
            "id,merchant_code,card_code,card_type,card_name,type,state,card_face_value,price,on_sale_state,create_at,update_at " +
            "from " +
            "card_map_merchant_cards " +
            "where " +
            "card_code= #{cardCode} " +
            "AND " +
            "merchant_code = #{merchantCode}")
    CardMapMerchantCards selectByCardCodeAndMerchantCode(@Param("cardCode") String cardCode, @Param("merchantCode") String merchantCode);

    /**
     * 根据卡号和商户编码和批次号 查询商户-卡 关联信息
     *
     * @param cardCode
     * @param merchantCode
     * @return
     */
    @Select("SELECT mc.id,merchant_code,card_code,card_type,card_name,type,state,card_face_value,price,reference_price,on_sale_state,on_sale_date,halt_sale_date,mc.create_at,mc.update_at,batch_code,dc.`value` as cardTypeStr\n" +
            "FROM\tcard_map_merchant_cards mc \n" +
            "left join dic_constant dc on mc.card_type = dc.`key`" +
            "where " +
            "mc.card_code= #{cardCode} " +
            "AND " +
            "mc.merchant_code = #{merchantCode}" +
            "AND mc.batch_code = #{batchCode}")
    CardMapMerchantCards selectByCardCodeAndMerchantCodeAndBatchCode(@Param("cardCode") String cardCode, @Param("merchantCode") String merchantCode,@Param("batchCode") String batchCode);


    /**
     * 根据卡号和商户编码和批次号 查询商户-卡 关联信息
     *
     * @param cardCode
     * @param batchCode
     * @return
     */
    @Select("SELECT mc.id,merchant_code,card_code,card_type,card_name,type,state,card_face_value,price,reference_price,on_sale_state,on_sale_date,halt_sale_date,mc.create_at,mc.update_at,batch_code,dc.`value` as cardTypeStr\n" +
            "FROM\tcard_map_merchant_cards mc \n" +
            "left join dic_constant dc on mc.card_type = dc.`key`" +
            "where " +
            "mc.card_code= #{cardCode} " +
            "AND mc.batch_code = #{batchCode}")
    CardMapMerchantCards selectByCardCodeAndBatchCode(@Param("cardCode") String cardCode,@Param("batchCode") String batchCode);


    /**
     * 通过商户号，获取该商户下，所有的卡
     * @param merchantCode
     * @param page
     * @param codeSearch
     * @return
     */
    @Select({"<script>",
            "select cm.merchant_code,m.merchant_name,c.card_code,c.card_name,c.card_pic_url,c.category_code,c.category_name,c.face_value,c.type,l.`value` as typeName,c.validity_type,c.valid_from,c.valid_to,c.valid_gap_after_applied,c.period_of_validity,ls.`value` as state from card_map_merchant_cards cm \n" +
                    "left join card_cards c on cm.card_code = c.card_code \n" +
                    "left join dic_constant l on c.type = l.`key`" ,
                    "left join dic_constant ls on c.state = ls.key and ls.group_code = 'card_state' ",
                    "left join mrc_merchants m on cm.merchant_code = m.merchant_code ",
            " where cm.type='template' AND (cm.merchant_code = #{merchantCode}  OR m.business_subjects = #{merchantCode}) ",
            "<if test='codeSearch.cardName != null and codeSearch.cardName != &quot;&quot;'>",
            " AND c.card_name like CONCAT ('%',#{codeSearch.cardName},'%')",
            "</if>",
            "<if test='codeSearch.cardType != null and codeSearch.cardType != &quot;&quot;'>",
            " AND c.type = #{codeSearch.cardType}",
            "</if>",
            "<if test='codeSearch.cardState != null and codeSearch.cardState != &quot;&quot;'>",
            " AND ls.`value` = #{codeSearch.cardState}",
            "</if>",
            " GROUP BY c.card_code,cm.merchant_code,m.merchant_name,c.card_name,c.category_name,c.face_value,c.state,c.type,c.card_pic_url,ls.`value`,l.`value`,c.valid_from,c.valid_to,c.validity_type,c.valid_gap_after_applied,c.period_of_validity ",
            "</script>"
    })
    List<CardListVo> getCardsByMerchantCode(@Param("merchantCode") String merchantCode, IPage<CardListVo> page, CodeSearch codeSearch);

    /**
     * 通过商户号，获取该商户下所有商券商品
     * @param merchantCode
     * @param search
     * @param page
     * @return
     */
    @Select({"<script>",
            "select c.card_code,c.card_name,cm.card_name as merchantCardName,cm.merchant_code,c.card_pic_url,cm.batch_code,c.category_name,cm.price,c.state,cm.on_sale_state,cm.on_sale_date,cm.halt_sale_date,m.merchant_name,dc.`value` as cardType from card_map_merchant_cards cm left join card_cards c on cm.card_code = c.card_code\n" +
            " LEFT JOIN dic_constant dc on c.type = dc.`key` and dc.group_code = 'card_type' "+
            "left join mrc_merchants m on m.merchant_code = cm.merchant_code where cm.type = 'sell' and cm.merchant_code = #{merchantCode} and cm.state = 'normal'",
            "<if test='search.merchantName!=null and search.merchantName != &quot;&quot;'>",
            " and m.merchant_name like CONCAT('%',#{search.merchantName},'%') ",
            "</if>",
            "<if test='search.merchantCardName!=null and search.merchantCardName != &quot;&quot;'>",
            " and cm.card_name like CONCAT('%',#{search.merchantCardName},'%') ",
            "</if>",
            "<if test='search.type!=null and search.type != &quot;&quot;'>",
            " and dc.`value` = #{search.type}",
            "</if>",
            "<if test='search.published!=null and search.published != &quot;&quot;'>",
            " and cm.on_sale_state = #{search.published}",
            "</if>",
    "</script>"})
    List<MerchantCardListVo> getCardProductsByMerchantCode(@Param("merchantCode") String merchantCode, MerchantCardSearch search, IPage<MerchantCardListVo> page);

    /**
     * 获取商家 上架的 卡券
     * @param merchantCode
     * @param type
     * @return
     */
    @Select("select cmc.*,cc.category_name,cc.state card_cards_state from card_map_merchant_cards cmc " +
            "LEFT JOIN card_cards cc ON cmc.card_code = cc.card_code"+
            " where merchant_code = #{merchantCode} AND on_sale_state = 'Y' AND cc.state = 'normal' AND cmc.state = 'normal' AND cmc.type = #{type} ORDER BY cmc.create_at DESC")
    List<CardMapMerchantCards> selectListByMerchantCodeAndTypeSale(@Param("merchantCode") String merchantCode, @Param("type") String type);

    @Select("select cmc.*,cc.category_name,cc.state card_cards_state from card_map_merchant_cards cmc " +
            "LEFT JOIN card_cards cc ON cmc.card_code = cc.card_code"+
            " where merchant_code = #{merchantCode} AND on_sale_state = 'Y' AND cc.state = 'normal' AND cmc.state = 'normal' AND (cmc.type = 'free' or cmc.type ='sell' or cmc.type ='topup') " +
            "ORDER BY cmc.create_at DESC")
    List<CardMapMerchantCards> selectListByMerchantCodeAndTypeSaleNoType(@Param("merchantCode") String merchantCode);

    @Select("SELECT\n" +
            "\tuc.card_name,\n" +
            "\tc.card_code,\n" +
            "\tc.type as cardType,\n" +
            "\tc.face_value,\n" +
            "\tuc.state,\n" +
            "\tuc.user_id,\n" +
            "\tc.id AS cardId\n" +
            "FROM\n" +
            "\tcard_map_user_cards uc\n" +
            "\tLEFT JOIN card_cards c ON uc.card_code = c.card_code \n" +
            "WHERE\n" +
            "\tuc.merchant_code = #{merchantCode}")
    List<MerchantUserCardVo> getUserCardList(@Param("merchantCode") String merchantCode, IPage<MerchantUserCardVo> page);

    @Select({"<script>",
            "select c.card_name,c.card_code,c.card_pic_url,i.inventory,c.face_value as cardFaceValue,sdc.`value` as state,mc.on_sale_date,mc.halt_sale_date,mc.batch_code,mc.type,dc.`value` as card_type from card_map_merchant_cards mc " ,
            " left join card_cards c on mc.card_code = c.card_code " ,
            " left join card_inventory i on c.card_code = i.card_code and mc.batch_code = i.batch_code ",
            " left join dic_constant dc on c.type = dc.`key` ",
            " left join dic_constant sdc on mc.state = sdc.`key` and sdc.group_code = 'card_map_merchant_cards_state' ",
            " GROUP BY c.card_name,c.card_code,c.card_pic_url,i.inventory,c.type,c.face_value,sdc.`value`,mc.on_sale_date,mc.halt_sale_date,mc.batch_code,mc.type,mc.merchant_code,dc.`value` ",
            " HAVING mc.merchant_code = #{merchantCode} AND mc.type = 'pos' " ,
            "<if test='cardName != null and cardName != &quot;&quot;'>" ,
            " AND c.card_name like CONCAT('%',#{cardName},'%')" ,
            "</if>",
    "</script>"})
    List<CardMapMerchantCards> getPosCardList(@Param("merchantCode") String merchantCode,@Param("cardName") String cardName);


    @Select({"<script>",
            "select c.card_code,c.card_name,cm.card_name as merchantCardName,cm.merchant_code,c.card_pic_url,cm.batch_code,c.category_name,cm.price,c.state,cm.on_sale_state,cm.on_sale_date,cm.halt_sale_date,m.merchant_name,dc.`value` as cardType from card_map_merchant_cards cm left join card_cards c on cm.card_code = c.card_code\n" +
            " LEFT JOIN dic_constant dc on c.type = dc.`key` and dc.group_code = 'card_type' "+
            "left join mrc_merchants m on m.merchant_code = cm.merchant_code where cm.type = 'sell'  and cm.state = 'normal' and cm.merchant_code in "+
            "<foreach item='item' index='index' collection='merchantCodes' open='(' separator=',' close=')'>",
            " #{item} ",
            "</foreach>",
            "<if test='search.merchantName!=null and search.merchantName != &quot;&quot;'>",
            " and m.merchant_name like CONCAT('%',#{search.merchantName},'%') ",
            "</if>",
            "<if test='search.merchantCardName!=null and search.merchantCardName != &quot;&quot;'>",
            " and cm.card_name like CONCAT('%',#{search.merchantCardName},'%') ",
            "</if>",
            "<if test='search.type!=null and search.type != &quot;&quot;'>",
            " and dc.`value` = #{search.type}",
            "</if>",
            "<if test='search.published!=null and search.published != &quot;&quot;'>",
            " and cm.on_sale_state = #{search.published}",
            "</if>",
            "</script>"})
    List<MerchantCardListVo> getObjectAndSonMerchantCards(List<String> merchantCodes, MerchantCardSearch search, IPage<MerchantCardListVo> page);

    @Select("select DISTINCT(c.category_name) from card_map_merchant_cards mc left join card_cards c on mc.card_code = c.card_code" +
            " where mc.merchant_code = #{merchantCode}")
    List<String> getCardAllCategories(String merchantCode);
}
