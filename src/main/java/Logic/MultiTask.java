package Logic;

import java.io.IOException;
import java.sql.SQLException;

public class MultiTask implements Runnable{
    DBLogic DBlog;
    private Thread t;
    int whi;
    public void run() {
        try {
            DBlog  = new DBLogic();
        } catch (ErrorException e) {
            e.printStackTrace();
        }
        String link = null;
        try {
            link = DBlog.GetLink(whi);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            assert link != null;
            DBlog.GetData(link);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(String name) {
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }

    public void starting(int i){
        this.whi = i;
        start("t"+i);
    }
}
