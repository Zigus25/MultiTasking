package Logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Logic {
    Connection connectionF,connectionL;
    public Logic (Connection connF,Connection connL){
        this.connectionF = connF;
        this.connectionL = connL;
    }
    //Get size of sth
    public int sizes(int whi) throws SQLException {
        PreparedStatement query;
        switch (whi){
            case 1 -> {
                query = this.connectionL.prepareStatement("SELECT count(Link) AS count FROM Links WHERE StateOf = 0");
            }
            case 2 ->{
                query = this.connectionF.prepareStatement("SELECT COUNT(*) AS count FROM (SELECT Year FROM Races GROUP BY Year)");
            }
            case 3 -> {
                query = this.connectionF.prepareStatement("SELECT count(*) AS count FROM (SELECT Driver From Results GROUP BY Driver ORDER BY Driver)");
            }
            case 4 ->{
                query = this.connectionF.prepareStatement("SELECT count(*) AS count FROM (SELECT Constructor FROM Results GROUP BY Constructor ORDER BY Constructor)");
            }
            case 5 -> {
                query = this.connectionF.prepareStatement("SELECT COUNT(*) AS count FROM (SELECT EngineSupplier FROM Results GROUP BY EngineSupplier ORDER BY EngineSupplier)");
            }
            default -> throw new IllegalStateException("Unexpected value: " + whi);
        }
        assert query != null;
        return query.executeQuery().getInt("count");
    }

    public ResultSet resForCombo(int whi) throws SQLException {
        PreparedStatement query;
        switch (whi){
            case 1 -> {
                query = this.connectionF.prepareStatement("SELECT Driver FROM Results GROUP BY Driver ORDER BY Driver");
            }
            case 2 ->{
                query = this.connectionF.prepareStatement("SELECT Constructor FROM Results GROUP BY Constructor ORDER BY Constructor");
            }
            case 3 -> {
                query = this.connectionF.prepareStatement("SELECT EngineSupplier FROM Results GROUP BY EngineSupplier ORDER BY EngineSupplier");
            }
            default -> throw new IllegalStateException("Unexpected value: " + whi);
        }
        assert query != null;
        return query.executeQuery();
    }
}
