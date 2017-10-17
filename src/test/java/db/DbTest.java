package db;

import com.yogi.albatross.db.DaoManager;
import com.yogi.albatross.db.user.dao.UserDao;

public class DbTest {
    public static void main(String args[]){
        DaoManager.init("com.yogi.albatross.db");
        UserDao dao=DaoManager.getDao(UserDao.class);
        dao.selectByUsername("admin");
        dao.insert("jacky","123456");
    }
}
