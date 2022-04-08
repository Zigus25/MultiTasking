package Logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticAna {
    Connection connection;

    //Get connection
    public StatisticAna(Connection conn) {
        this.connection = conn;
    }

    //Statistic of driver
    public ResultSet Driver(int which, String driver, int fromYear, int toYear) throws SQLException {
        PreparedStatement zapytanie;
        switch (which){
            case 1 -> {
                zapytanie = this.connection.prepareStatement("SELECT *, SUM(Poi) AS Total FROM Results WHERE Year = ? GROUP BY Driver ORDER BY Total DESC,Num");
                zapytanie.setString(1, String.valueOf(fromYear));
            }
            case  2 -> {
                zapytanie = this.connection.prepareStatement("SELECT Year,sum(Poi) AS SUM FROM Results WHERE Driver = ? AND Year BETWEEN ? AND ? GROUP BY Year");
                zapytanie.setString(1, driver);
                zapytanie.setString(2, String.valueOf(fromYear));
                zapytanie.setString(3, String.valueOf(toYear));
            }
            case 3 -> {
                zapytanie = this.connection.prepareStatement("SELECT count(Position) AS SUM,Position FROM Results WHERE Driver = ? AND Rok BETWEEN ? AND ? GROUP BY Position");
                zapytanie.setString(1, driver);
                zapytanie.setString(2, String.valueOf(fromYear));
                zapytanie.setString(3, String.valueOf(toYear));
            }
            case 4 -> {
                zapytanie = this.connection.prepareStatement("SELECT * FROM(SELECT ROW_NUMBER() OVER (partition by Year ORDER BY Year) AS PositionInYear,* FROM(SELECT Pkciki,Driver ,Rok FROM (SELECT *, SUM(Pkt) AS Pkciki FROM Wyniki WHERE Rok BETWEEN ? AND ? GROUP BY Rok,Driver ORDER BY Pkciki DESC,Num)))WHERE Driver = ?");
                zapytanie.setString(1, String.valueOf(fromYear));
                zapytanie.setString(2, String.valueOf(toYear));
                zapytanie.setString(3, driver);
            }
            case 5 -> {
                zapytanie = this.connection.prepareStatement("SELECT MiejsceWSezonie,count(MiejsceWSezonie) AS Ilosc,Driver,Rok FROM(SELECT * FROM(SELECT ROW_NUMBER() OVER (partition by Rok ORDER BY Rok) AS MiejsceWSezonie,* FROM(SELECT Pkciki,Driver ,Rok FROM (SELECT *, SUM(Pkt) AS Pkciki FROM Wyniki WHERE Rok BETWEEN ? AND ? GROUP BY Rok,Driver ORDER BY Pkciki DESC,Num)))WHERE Driver = ?)GROUP BY MiejsceWSezonie");
                zapytanie.setString(1, String.valueOf(fromYear));
                zapytanie.setString(2, String.valueOf(toYear));
                zapytanie.setString(3, driver);
            }
            default -> throw new IllegalStateException("Unexpected value: " + which);
        }
        return zapytanie.executeQuery();
    }

    public ResultSet Constructor(int which, String team, int fromYear, int toYear) throws SQLException {
        PreparedStatement zapytanie;
        switch (which){
            case 1 -> {
                zapytanie = this.connection.prepareStatement("SELECT Team, SUM(Poi) AS Pkt FROM Results WHERE Year = ? GROUP BY Team ORDER BY Pkt DESC,Team");
                zapytanie.setString(1, String.valueOf(fromYear));
            }
            case  2 -> {
                zapytanie = this.connection.prepareStatement("SELECT Rok,Team,sum(Pkt) AS SUMA FROM Wyniki WHERE Konstruktor = ? AND Rok BETWEEN ? AND ? GROUP BY Rok");
                zapytanie.setString(1, team);
                zapytanie.setString(2, String.valueOf(fromYear));
                zapytanie.setString(3, String.valueOf(toYear));
            }
            case  3 -> {
                zapytanie = this.connection.prepareStatement("SELECT count(Miejsce) AS SUMA,Miejsce FROM Wyniki WHERE Konstruktor like ? AND Rok BETWEEN ? AND ? GROUP BY Miejsce");
                zapytanie.setString(1, team);
                zapytanie.setString(2, String.valueOf(fromYear));
                zapytanie.setString(3, String.valueOf(toYear));
            }
            case 4 -> {
                zapytanie = this.connection.prepareStatement("SELECT * FROM(SELECT ROW_NUMBER() OVER (partition by Rok ORDER BY Rok) AS MiejsceWSezonie,* FROM(SELECT Pkciki,Konstruktor ,Rok FROM (SELECT Konstruktor, SUM(Pkt) AS Pkciki,Rok FROM Wyniki WHERE Rok BETWEEN ? AND ? GROUP BY Rok,Konstruktor ORDER BY Rok DESC,Pkciki DESC,Konstruktor)))WHERE Konstruktor = ?");
                zapytanie.setString(1, String.valueOf(fromYear));
                zapytanie.setString(2, String.valueOf(toYear));
                zapytanie.setString(3, team);
            }
            case 5 -> {
                zapytanie = this.connection.prepareStatement("SELECT MiejsceWSezonie,count(MiejsceWSezonie) AS Ilosc,Konstruktor,Rok FROM(SELECT * FROM(SELECT ROW_NUMBER() OVER (partition by Rok ORDER BY Rok) AS MiejsceWSezonie,* FROM(SELECT Pkciki,Konstruktor ,Rok FROM (SELECT Konstruktor, SUM(Pkt) AS Pkciki,Rok FROM Wyniki WHERE Rok BETWEEN ? AND ? GROUP BY Rok,Konstruktor ORDER BY Rok DESC,Pkciki DESC,Konstruktor)))WHERE Konstruktor = ?)GROUP BY MiejsceWSezonie");
                zapytanie.setString(1, String.valueOf(fromYear));
                zapytanie.setString(2, String.valueOf(toYear));
                zapytanie.setString(3, team);
            }
            default -> throw new IllegalStateException("Unexpected value: " + which);
        }
        return zapytanie.executeQuery();
    }
}
