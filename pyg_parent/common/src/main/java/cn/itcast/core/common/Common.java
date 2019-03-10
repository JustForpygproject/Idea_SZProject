package cn.itcast.core.common;

public class Common {

    // connect the database
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root";

    public static final String URL = "jdbc:mysql://localhost:3306/utf8?characterEncoding=utf-8";

    // common
    public static final String EXCEL_PATH = "D:\\XIAZAIZILIAO\\code\\Idea_SZProject\\shizhan\\pyg_parent\\品牌数据导出.xlsx";

    // sql
    public static final String INSERT_BRAND_SQL = "insert into tb_brand(id, name,first_char) values(?, ?, ?)";
    public static final String UPDATE_BRAND_SQL = "update tb_brand set id = ?, name = ?, first_char= ? where id = ? ";
    public static final String SELECT_BRAND_ALL_SQL = "select id, name,first_char from tb_brand";
    public static final String SELECT_BRAND_SQL = "select * from tb_brand where name like ";
}
