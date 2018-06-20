package Portals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import net.risingworld.api.database.Database;
import net.risingworld.api.utils.Vector3f;

public class DatabaseStuff {
    
    public static Database data;
    public Portals P = new Portals();
    
    public void setDB(Database db){
        data = db;
    }
    
    public void initDB(){
        data.execute("CREATE TABLE IF NOT EXISTS 'Portals' ("
                + "'idNo' INTEGER, "
                + "'Name' VARCHAR(64), "
                + "'StartChunkposX' INTEGER, "
                + "'StartChunkposY' INTEGER, "
                + "'StartChunkposZ' INTEGER, "
                + "'StartBlockposX' INTEGER, "
                + "'StartBlockposY' INTEGER, "
                + "'StartBlockposZ' INTEGER, "
                + "'GlobalStartposX' FLOAT, "
                + "'GlobalStartposY' FLOAT, "
                + "'GlobalStartposZ' FLOAT, "
                + "'EndChunkposX' INTEGER, "
                + "'EndChunkposY' INTEGER, "
                + "'EndChunkposZ' INTEGER, "
                + "'EndBlockposX' INTEGER, "
                + "'EndBlockposY' INTEGER, "
                + "'EndBlockposZ' INTEGER, "
                + "'GlobalEndposX' FLOAT, "
                + "'GlobalEndposY' FLOAT, "
                + "'GlobalEndposZ' FLOAT, "
                + "'TargetposX' FLOAT, "
                + "'TargetposY' FLOAT, "
                + "'TargetposZ' FLOAT"
                + ")");
    }
    
    public Vector3f getTargetPosition(int idNo){
        Vector3f Tpos = new Vector3f(0.0f, 0.0f, 0.0f);
        Connection con = data.getConnection();
        
        try{
            PreparedStatement prep = con.prepareStatement("SELECT * FROM Portals WHERE idNo LIKE ?");
            prep.setInt(1, idNo); 
            ResultSet result = prep.executeQuery();
            if (result.next()){
                Tpos.setX(result.getFloat("TargetposX"));
                Tpos.setY(result.getFloat("TargetposY"));
                Tpos.setZ(result.getFloat("TargetposZ"));
            }
        }
        catch(SQLException e){
        }
        
        return Tpos;
    }
    
    public void createPortal(portal p){
        Connection con = data.getConnection();
        try{
            PreparedStatement prep = con.prepareStatement("INSERT INTO Portals ("
                    + "idNo, "
                    + "Name, "
                    + "StartChunkposX, "
                    + "StartChunkposY, "
                    + "StartChunkposZ, "
                    + "StartBlockposX, "
                    + "StartBlockposY, "
                    + "StartBlockposZ, "
                    + "GlobalStartposX, "
                    + "GlobalStartposY, "
                    + "GlobalStartposZ, "
                    + "EndChunkposX, "
                    + "EndChunkposY, "
                    + "EndChunkposZ, "
                    + "EndBlockposX, "
                    + "EndBlockposY, "
                    + "EndBlockposZ, "
                    + "GlobalEndposX, "
                    + "GlobalEndposY, "
                    + "GlobalEndposZ, "
                    + "TargetposX, "
                    + "TargetposY, "
                    + "TargetposZ"
                    + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            prep.setInt(1, p.idNo);
            prep.setString(2, p.Name);
            prep.setInt(3, p.StartChunkposX);
            prep.setInt(4, p.StartChunkposY);
            prep.setInt(5, p.StartChunkposZ);
            prep.setInt(6, p.StartBlockposX);
            prep.setInt(7, p.StartBlockposY);
            prep.setInt(8, p.StartBlockposZ);
            prep.setFloat(9, p.GlobalStartposX);
            prep.setFloat(10, p.GlobalStartposY);
            prep.setFloat(11, p.GlobalStartposZ);
            prep.setInt(12, p.EndChunkposX);
            prep.setInt(13, p.EndChunkposY);
            prep.setInt(14, p.EndChunkposZ);
            prep.setInt(15, p.EndBlockposX);
            prep.setInt(16, p.EndBlockposY);
            prep.setInt(17, p.EndBlockposZ);
            prep.setFloat(18, p.GlobalEndposX);
            prep.setFloat(19, p.GlobalEndposY);
            prep.setFloat(20, p.GlobalEndposZ);
            prep.setFloat(21, p.TargetposX);
            prep.setFloat(22, p.TargetposY);
            prep.setFloat(23, p.TargetposZ);
            prep.executeUpdate();
        }
        catch (SQLException e){
        }
    }
    
    public void setTargetPosition(int idNo, Vector3f Targetpos){
        Connection con = data.getConnection();
        try{
            PreparedStatement prep = con.prepareStatement("UPDATE Portals SET TargetposX = ?, TargetposY = ?, TargetposZ = ? WHERE idNo = ?");
            prep.setFloat(1, Targetpos.getX());
            prep.setFloat(2, Targetpos.getY());
            prep.setFloat(3, Targetpos.getZ());
            prep.setInt(4, idNo);
            prep.executeUpdate();
        }
        catch (SQLException e){
        }
    }
    
    public void removePortal(int idNo){
        Connection con = data.getConnection();
        try{
            PreparedStatement prep = con.prepareStatement("DELETE FROM Portals WHERE idNo LIKE ?");
            prep.setInt(1, idNo);
            prep.executeUpdate();
        } 
        catch (SQLException e) {
        }
    }
    
    public class portal{
        int idNo;
        String Name;
        int StartChunkposX;
        int StartChunkposY;
        int StartChunkposZ;
        int StartBlockposX;
        int StartBlockposY;
        int StartBlockposZ;
        float GlobalStartposX;
        float GlobalStartposY;
        float GlobalStartposZ;
        int EndChunkposX;
        int EndChunkposY;
        int EndChunkposZ;
        int EndBlockposX;
        int EndBlockposY;
        int EndBlockposZ;
        float GlobalEndposX;
        float GlobalEndposY;
        float GlobalEndposZ;
        float TargetposX;
        float TargetposY;
        float TargetposZ;
    }
    
    public ArrayList<portal> getPortals(){
        ArrayList<portal> portals = new ArrayList<>();
        
        try(ResultSet result = data.executeQuery("SELECT * FROM Portals")){
            while (result.next()){
                portal p =  new portal();
                p.idNo = result.getInt("idNo");
                p.Name = result.getString("Name");
                p.StartChunkposX = result.getInt("StartChunkposX");
                p.StartChunkposY = result.getInt("StartChunkposY");
                p.StartChunkposZ = result.getInt("StartChunkposZ");
                p.StartBlockposX = result.getInt("StartBlockposX");
                p.StartBlockposY = result.getInt("StartBlockposY");
                p.StartBlockposZ = result.getInt("StartBlockposZ");
                p.GlobalStartposX = result.getFloat("GlobalStartposX");
                p.GlobalStartposY = result.getFloat("GlobalStartposY");
                p.GlobalStartposZ = result.getFloat("GlobalStartposZ");
                p.EndChunkposX = result.getInt("EndChunkposX");
                p.EndChunkposY = result.getInt("EndChunkposY");
                p.EndChunkposZ = result.getInt("EndChunkposZ");
                p.EndBlockposX = result.getInt("EndBlockposX");
                p.EndBlockposY = result.getInt("EndBlockposY");
                p.EndBlockposZ = result.getInt("EndBlockposZ");
                p.GlobalEndposX = result.getFloat("GlobalEndposX");
                p.GlobalEndposY = result.getFloat("GlobalEndposY");
                p.GlobalEndposZ = result.getFloat("GlobalEndposZ");
                p.TargetposX = result.getFloat("TargetposX");
                p.TargetposY = result.getFloat("TargetposY");
                p.TargetposZ = result.getFloat("TargetposZ");
                portals.add(p);
            }
        }
        catch (SQLException e){
        }
        
        return portals;
    }
}
