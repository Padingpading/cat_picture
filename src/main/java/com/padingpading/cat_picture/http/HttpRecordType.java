package com.padingpading.cat_picture.http;

/**
 * 页面名称，与产品定义字段保持一致
 *
 * @author: yu_song
 * @update: 2019/1/17 11:41
 */
public enum HttpRecordType {
    /**
     * 内部保留项:网关传来的用户信息
     */
    user,
    /**
     * 基本信息
     */
    @Deprecated
    basic,
    /**
     * 订单列表
     */
    order_list,
    /**
     * 订单详情
     */
    order_detail,
    /**
     * 余额
     */
    amount,
    /**
     * 其他
     */
    other,
    /**
     * 地址信息
     */
    address,
    /**
     * 账单信息
     */
    bill,
    /**
     * 短信详单
     */
    sms,
    /**
     * 上网详单
     */
    net,
    /**
     * 通话详单
     */
    call,
    /**
     * 亲情号
     */
    family_number,
    /**
     * 信用分
     */
    credit_score,
    /**
     * 资产信息
     */
    my_asset,
    /**
     * 花呗
     */
    hua_bei,
    /**
     * 借呗
     */
    jie_bei,
    /**
     * 余额宝
     */
    yu_e_bao,
    /**
     * 充值记录
     */
    recharge,
    /**
     * 等级
     */
    vip_lvl,
    /**
     * 套餐
     */
    packages,
    /**
     * 积分
     */
    points,
    @Deprecated
    registerDate,
    @Deprecated
    account,
    @Deprecated
    bill_info,
    @Deprecated
    package_item,
    @Deprecated
    points_value,
    basic_info,
    @Deprecated
    package_item_name,
    @Deprecated
    callrecord_info,
    @Deprecated
    sms_info,
    @Deprecated
    net_info,
    @Deprecated
    recharge_info,
    @Deprecated
    other_info,
    //#region 京东
    @Deprecated
    jd_address,
    @Deprecated
    jd_vip,
    @Deprecated
    jd_certification,
    @Deprecated
    jd_phonenum,
    @Deprecated
    jd_finance,
    @Deprecated
    jd_jt_bt_limit,
    @Deprecated
    jd_bindcard,
    @Deprecated
    jd_name_bank,
    @Deprecated
    jd_bt_bills,
    @Deprecated
    jd_notout_bt_bills,
    @Deprecated
    jd_notout_bt_product_bills,
    @Deprecated
    jd_bt_product_bills,
    @Deprecated
    jd_jt_bills,
    @Deprecated
    jd_order,
    @Deprecated
    jd_id_order,
    @Deprecated
    jd_product_order,
    //#endregion
    //#region 社保
    @Deprecated
    si_user_info,
    @Deprecated
    si_insurances_info,
    @Deprecated
    si_bill_record_info,
    @Deprecated
    si_medial_insurance_expense_info,
    //#endregion
    //#region 淘宝
    @Deprecated
    zmscore,
    @Deprecated
    base_info_one,
    @Deprecated
    base_info_two,
    @Deprecated
    base_info_three,
    @Deprecated
    base_info_four,
    @Deprecated
    base_info_five,
    @Deprecated
    base_info_six,
    @Deprecated
    base_info_seven,
    @Deprecated
    address_info,
    @Deprecated
    order_detail_list,
    @Deprecated
    alipay_set_base,
    //#endregion
    //#region 支付宝
    @Deprecated
    bank,
    @Deprecated
    huabei,
    @Deprecated
    yuebao_balance,
    @Deprecated
    balance_info,
    @Deprecated
    home,
    @Deprecated
    bill_detail,
    //#endregion
    //#region 今借到
    @Deprecated
    guarantee,
    @Deprecated
    borrow,
    @Deprecated
    loan_borrow,
    @Deprecated
    lend,
    @Deprecated
    loan_lend,
    @Deprecated
    friends,
    //#endregion
    //#region qq助手
    @Deprecated
    group,
    @Deprecated
    contacts,
    //#endregion
    //#region 无忧借条
    @Deprecated
    b_confirm,
    @Deprecated
    b_waiting,
    @Deprecated
    b_overdue,
    @Deprecated
    b_returned,
    @Deprecated
    b_failure,
    @Deprecated
    l_confirm,
    @Deprecated
    l_waiting,
    @Deprecated
    l_overdue,
    @Deprecated
    l_returned,
    @Deprecated
    l_failure,
    //#endregion
    //#region 华为云
    @Deprecated
    book,
    @Deprecated
    note,
    @Deprecated
    order_sms,
    //#endregion
    //#region 学信网
    @Deprecated
    ocr_parse_student_info,
    @Deprecated
    ocr_parse_educational,
    @Deprecated
    school_status_info,
    @Deprecated
    educational_info,
    @Deprecated
    education_result,
    @Deprecated
    name,
    @Deprecated
    photo,
    @Deprecated
    graduation_status,
    //#endregion
    //#region 美团
    @Deprecated
    order,
    //#endregion
    //#region 信用卡申请进度
    @Deprecated
    card_apply_status,
    //#endregion
    //#region 邮箱账单
    bill_installment,
    card_expenditure,
    //#endregion

    //#region 公积金
    check_bill,
    loan_basic,
    loan_bill,
    loan_pay_plan,
    //#endregion
    //#region 个税
    employment_info,
    family_member,
    individualtax_deduction_info,
    bankcard_info,
    //#endregion
    //#region 车险
    vehicle_insurance_detail_infos,
    vehicle_accident_detail_infos,
    //#endregion
    //#region QQ
    friend_info,
    nearest_contact,
    group_info,
    group_member_info,
    //#endregion QQ

    //#region 网银
    account_info_debit,
    account_info_credit,
    deposit_info,
    trade_details_debit,
    trade_details_credit,
    loan_info,
    bills_credit,

    //#endregion 网银

    //#region 物流
    logis_count,
    logis_detail,
    //#endregion
}
