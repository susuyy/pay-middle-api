package com.ht.user.outlets.jlpay.contants;

import com.jlpay.ext.qrcode.trans.constants.ExtConstants;

/**
 * @author zhaoyang2
 * 交易常量
 */
public class TransConstants {

//============================  以下 测试 环境参数 =====================//

//    public static final String TRADE_URL = "https://qrcode-uat.jlpay.com/api/pay/";

//    public static final String PRE_OTHER_URL = "https://openapi-uat.jlpay.com/access/merch/";

    public static final String PRE_OTHER_URL = "https://openapi.jlpay.com/access/merch/";

    //接入方系统私钥
//    public static final String sysPriKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCKyPtFB5Mqw1NaYzek99q3KZ/QH/XHx0rQ1DrG6TqqYqef/dtIVaJ6vokkWh5kCmbSiGKYJJSk5WMsv4S38ZBWXETN6o0DqZtiJ2KR79cy25GbQmw5nAC3C1h62G2lLFw0Rrwb1hQ+aoDqBMi+9nc71RhSESxwek3moySBFV44YBbBuHAKV/u1FGmB5loCDdbTJNKKIcyyTphfkgMfbMGZyhGQ70NidvksJImSGCyIlfswuRsyKKg9eJ+dJlzMyYsls5bJXMBq2R2WpueSImSMAW0YgiZdvjoYbY+ho4jNjX5zHJyT6M9m6llOJfHFL1tQoDEOXKiUUdtabTdlPnbRAgMBAAECggEAF5nwKSANpeMLpL5ksxg3SJi6hcE5odzBW1wMFtGI2XrneKzKArYVaHxIhDcTHf4q2Di7U5Y89QHRaMW1AzcATb9pL/9oNaw9MWbzO1AnL43paBbWosFl2bsDM/jkRIeTsowo5y7zyF2CSMnBfcAaLMGjXilvfj0+TC+IQK9qk3l7KYGLnx7+6OUbCMb455EC3WS0Md4ss3yyX+GfHkyLSPOd3gaL094JsgOBw7g3+OHgjYf8rcKIvnta+jZoXsUARMw2gKExUgesA7FiDyVgZ1QYXY/Qd2yRvlKPVWjmz7RSj/8EUTWk53zLJiQTa1Yxb05baqmfUQPLyO8QgjzUxQKBgQDVhUbb2ePG7J1fTzB+I6CuXo+cwfy0RTCQP4Kx6wdHM4Hb/jWbAhW55z2INaOMbHiu0fxN4qAerdIWoNgjqOV8iesaZxvWGfMtZjzp1iGujZmOE0yCrYFsBMZQy9U/7XN+PFY//EOd5H+rGa8ZNCkpk9y7iSTfYL6ItV+He1Z1vwKBgQCmZWIAl5MlX2xQgwNd46pc7rNyr1oS8baxckTzuAWcc9wMSApawGW2nqoMVejWYq57OCiYWQm7UEwRpMf/F1FY3/4o5EU8G1r99nOk66HmviW5ipGiJNrnOzk6Mimbqwm1giYdvcybcZwS/6DSeFFD6weeBBR7S7bcp4PD9aLXbwKBgHIzMk7sHvOKIjGTvS/6Bjq8wLrq1inkx7CfB1v5hI8EcXQkZq9dUhl4IGT1q1+ztGhsTzGpAFLoTPFlXbTU5MjTSzd35l+AyZuCjxnSOXmOqo5erBFIk2wesaMNIiVq7taZltfqKJAOYmo09n3YdBuUxf5Xv6zppX6g41MnGHspAoGAJ4TaosNdGjowkmqbSRhCJPI4QlutK+SmfDxkbfHdu0u1DmGpu+YIAjhqsKVSuGAVioRK9+vlqMwoVORq74XNNytzxKh6XQ0uLjTzQE8KU7ADa66iaf0Q1Gw3aj/xq9wSYT546QVj6+Muq0B1JKeYvWW7mGblqmbQFlXesJLNSxcCgYEAzVK84uFVl/pkrLKz2fJPAokHC+aNWDRWGjD4DaCn/qnRxdCoFaqvic3o8ZVeagBRmK4zF2L1X23YpAXZ2G+Zo+1WAiZGhp3enox7EK0JvbgB9YGHz9n8ZmeAa7cV7Pa4zIrVNCz/OX0OvBeArCUs/Fu4f55wzMlxTzglJjzYccw=";

    //嘉联公钥
//    public static final String jlPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtNisjGCPs8AxDrs0EaXfEBwmpweFfkZF7bdAHPYaYTqnTtBgZ7o4zcJKkJuTW5Ua8JVsz0vnqmWX2/1IB/G5/bP5FFjAanEiFj4L/60lzx5P6WOlgpfGLnWq7o18Y1fjnPc4momAVdIIUHbIjE7xhy3NEHu4rgjW4Ofv9GubViuKfryY3agCvaYz9juUtkyR+evddcIc/6pqEMSRenb7a8mjrq3KCRCF4uBAAJB6FjxRGhOqQXsuZjxlAoU4V7W1Io/UlGCuFL4M5fb5U0LXTeuKu8XEiQNy7WRy6KFkHInaUOgn2crVbcwInTJJLvLtmgZlwhepcBt+EejGzM/TqQIDAQAB";

    //嘉联分配的商户号
//    public static final String MCH_ID = "849584358120016";

    //嘉联分配的机构号
//    public static final String ORG_CODE = "50720753";

    //接口类型值 固定
    public static final String MSG_TRAN_CODE = "MER009";

    //码付渠道入网 固定值
    public static final String SOURCE = "9";

    //商户所在地的行政区域代码 固定值
    public static final String AREA_CODE = "460200";

    //商户所在地的行政区域代码 固定值
    public static final String SIGN_METHOD = "02";

    //待加签串
    public static final String WAIT_SIGN_STR = "50720753984960205812000102";
//    public static final String WAIT_SIGN_STR = "";

//============================  以下 正式 环境参数 =====================//

    //交易请求地址
    public static final String TRADE_URL = "https://qrcode.jlpay.com/api/pay/";

    //接入方系统私钥
    public static final String sysPriKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCPFoYGwjfZLWUA" +
            "iDWkhMXWTvu1e4IQK69tZ6mW1tpoKOE737v1rIBbUQPtZnvKBNuGr26jqeONb+Q4" +
            "5AevYh5IbG5tXM4z+kFhNfhPJlSMx9xkLFafxBoF0/f1BBE/Fwe69hoJPsfUpmHo" +
            "ZvUqx5iEFZidz4+8wdaj0RfZ5QerGF2yI3a+u5TiTqe89fw2xX5sH+PVjMO1xj5r" +
            "yq/DzTcCXtvBj8yE9lvRWdKZZDTgiAku5bOw6yznRUQOD1TP98RfBg7ciy6M5DXz" +
            "562fzYSGzlF7FI4Gw5vRMYJT49MMz5odDhE14JSplKrGCXjDvOL8D62ly5Jlx0qg" +
            "5R93BpsLAgMBAAECggEABKX1qqiKieqYmmDSZUrO5nxgXu/pNL9GjqZMcxL9u16A" +
            "mqJVB9EXxl+mYgLvSIvJ2eE/FeAB/WlCH7sAqOjmFPI40CKwrC2YDCFltKSJIJ2p" +
            "gjOSiNQlCEVvi60bJlKINMGj7OL2OOlCyvr1QfOD995/u7ivcbFWWLIFNVXkXuF6" +
            "Txh2zKdLocNx16JKu67APFQTvF4+j53n6ACzMKjgSNVAHBk9HHO4coY+FJGqmeOx" +
            "FWXtGNRZpoprcBcJFdt2ivc8SeRda010O3P+bDPfz8BpoanNkrUZb7yEgEQiYB3L" +
            "NEQhYeIPq/2kXv6GTl0oI5jvu9e8VUCo893KDiT6KQKBgQC26ZNFu4cAuvt48Z/h" +
            "zTtT1RMSkF8MzR48qi31jWjOPUUpXl/MZP9KTAqpUTaN8WA5roU0smPOlnwelKj4" +
            "ueaKMRSEriiZ9AKq4mkP3gktBl16Liz3lbF4Xl88cbuzZ/I4DSInNR1Xt62eSIYF" +
            "hhF5jevIL0B2wlHpR6dZ23htHQKBgQDIQz1bFXiaR2vCkKQY+O7SFKruUNg6tpQa" +
            "iaAw+HVe0xrJMR++BxhEmFhY69x3fbrfXOdZv3ePBxFLpo4E7Ak+N1lmdAxT8h4U" +
            "/+lodw9AlH6ff3mn75twqkJurBc2VwvhCG0ZQeG2oDgMxkYW6B7JLJkYO5E4I0ds" +
            "G3d/oio4RwKBgENHgx5BLhU8scb4yWBbBxl85HkNlYi14gtaZXFOxGAn9UqUJhSe" +
            "ibCga8N/1ds8D7Ln2KNbgo64l4sAD279oijuiKeND+4g7OJCSXbGqwVes/9/AJdI" +
            "/nBR9iHbi7kf4N5xJkDZrqNXMotoDbP2bnMRmsqVZtbH/y0ZaBsLOS6ZAoGAC3S1" +
            "PNGknYa6Zl3p1TfgSYo/IPTk+a7aGA6JaGO+Kxd+b8Xsn+09+SaR3zxW10sM9pwU" +
            "6/BlAXyRA7faZfOur61w4sh+sAqop+Df1EcDS1lzSOJ87htp0+8Zb8VOaqBeIJhf" +
            "zVMUe2L88dYhykuq/y0a8OTvcLur6UZE9rsrGR8CgYBfsejPZbR+wtZ4u1tHBerw" +
            "6RkiBmDlCHlNJVj71BwFQ4tQ7yE/yp+soj6BOjUcNDXXhHTqzPFVI1HgdDnCeDDG" +
            "zouoRGBLiwaI9H3ajBiw3uYVVWoR95alMZqLcnS3DMOpUS1RS/bleK2U6Q2dUD9k" +
            "IFXd7qoR11qGHJcTgN9zoA==";

    //嘉联公钥
    public static final String jlPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhIxO+cvjG1EpDO0BewIKKvh1cGgaMIxD6y/gSdM2cH+gZ5BQLJl6dhg+mQX7adqTYDZCArRkJF8X+yhefxGlY6CR2bjkIHN19QV86MHhrq22cpEfyvGrlQUp1splbr5mOTLJ9nrDNroZTE6mJFQyR9o80UcjDKOyZbjVdpsIrao1nYt1/knVy9NFZa3ZQM5ZMiIGlA3I9ehBZhHLjBjJOXU+xepp2tn4wcNgD5UDWszcWmKkB6QcelwutACkuJhDcJcf4EgVO+3ER29xqB3SNjYpP7X834UIKRUAP3ltbcXy5eU4N+WpCaChhpl9HfubljiZi+NWdvKWgVN3BCMp+QIDAQAB";

    //嘉联分配的商户号
    public static final String MCH_ID = "849642253110076";

    //嘉联分配的机构号
    public static final String ORG_CODE = "2312262474";


    static public void setJlpayProperty() {
        System.setProperty(ExtConstants.orgPrivateKey, TransConstants.sysPriKey);
        System.setProperty(ExtConstants.jlpayPublicKey, TransConstants.jlPubKey);
        System.setProperty(ExtConstants.tradeUrl, TransConstants.TRADE_URL);
    }
}
