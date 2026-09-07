package com.example.labs.db;
import java.sql.*;
import com.example.labs.core.Habitat;
import com.example.labs.model.BoyStudent;
import com.example.labs.model.GirlStudent;
import com.example.labs.model.IBehaviour;
import com.example.labs.model.Student;
import static com.example.labs.core.Habitat.getHabitat;
import java.sql.*;
import java.util.*;

public class Database {
    private Habitat habitat = getHabitat();
    private static final String URL = "jdbc:postgresql://localhost:5432/simulation_db";
    private static final String USER = "sim_user";
    private static final String PASS = "sim_password";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USER,PASS);
    }


    public void saveAllObject(float time) throws SQLException {
        String sql = "INSERT INTO simulation_objects "
                + "(id,type,birth_time,lifetime,pos_x,pos_y) "
                + "VALUES (?,?,?,?,?,?)";
        try(Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try(PreparedStatement ps = con.prepareStatement(sql)) {
                for(IBehaviour o : habitat.getObjects()) {
                    ps.setInt(1,o.getId());
                    ps.setString(2,o.getType());

                    long creationTimeMils = o.getCreationTimeNanos()/1000000;
                    ps.setLong(3, creationTimeMils);

                    float lifeTime = o.getLifeTime() - (time - o.getCreationTime());
                    System.out.println("Вызвано в  " + time);
                    System.out.println("Осталось жить " + lifeTime);
                    long lifetimemils = (long) (lifeTime * 1000);

                    ps.setLong(4,lifetimemils);
                    ps.setFloat(5,o.getX());
                    ps.setFloat(6,o.getY());
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
    public void saveObjectByType(String type, float time) {
        String sql = "INSERT INTO simulation_objects "
                + "(id,type,birth_time,lifetime,pos_x,pos_y) "
                + "VALUES (?,?,?,?,?,?)";
        try(Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try(PreparedStatement ps = con.prepareStatement(sql)) {
                List<IBehaviour> st = habitat.getStudentsByType(type);
                for(IBehaviour o : st) {
                    ps.setInt(1,o.getId());
                    ps.setString(2,o.getType());

                    long creationTimeMils = o.getCreationTimeNanos()/1000000;
                    ps.setLong(3, creationTimeMils);
                    float lifeTime = o.getLifeTime() - (time - o.getCreationTime());
                    long lifetimemils = (long) (lifeTime * 1000);
                    ps.setLong(4,lifetimemils);
                    ps.setFloat(5,o.getX());
                    ps.setFloat(6,o.getY());
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearAll() throws SQLException {
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM simulation_objects");
        }
    }

    public void loadAllObject() throws SQLException {
        String sql = "SELECT * FROM simulation_objects";
        ArrayList<IBehaviour> result = new ArrayList<>();

        try(Connection con = Database.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                long birthTimeMillis = rs.getLong("birth_time");
                long lifeTimeMillis = rs.getLong("lifetime");
                float posX = rs.getFloat("pos_x");
                float posY = rs.getFloat("pos_y");

                long birthTimeNanos = birthTimeMillis * 1_000_000; //время созд в нанах
                float birthTimeSecs = birthTimeMillis / 1000f;
                float lifeTimeSeconds = lifeTimeMillis / 1000f; //время жизни в сек

                IBehaviour o;
                if("boy".equals(type)) {
                    o = new BoyStudent(id, posX, posY, lifeTimeSeconds, birthTimeSecs, birthTimeNanos);
                } else if("girl".equals(type)) {
                    o = new GirlStudent(id, posX, posY, lifeTimeSeconds, birthTimeSecs, birthTimeNanos);
                } else {
                    continue;
                }
                result.add(o);
            }
        }
        if(!result.isEmpty()) {
            for(IBehaviour o : result) {
                o.setCreationTime(0);
                o.setCreationTimeNanos(0);
            }
        }
        for(IBehaviour o : result) {
            if(o instanceof Student s) {
                s.initImage();
            }
        }
        Set<Integer> ids = new HashSet<>();
        TreeMap<Integer,Long> btoid = new TreeMap<>();

        for(IBehaviour o : result) {
            ids.add(o.getId());
            btoid.put(o.getId(), o.getCreationTimeNanos());
        }
        for(IBehaviour ob : result) {
            System.out.println("Осталось жить " + ob.getLifeTime());
        }
        habitat.setObjects(result);
        habitat.setActiveIds(ids);
        habitat.setBirthToId(btoid);

    }
    public void loadObjectByType(String objType) throws SQLException {
        String sql = "SELECT * FROM simulation_objects WHERE type = ?";
        ArrayList<IBehaviour> result = new ArrayList<>();

        try(Connection con = Database.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ) {
            ps.setString(1, objType);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    int id = rs.getInt("id");
                    String type = rs.getString("type");
                    long birthTimeMillis = rs.getLong("birth_time");
                    long lifeTimeMillis = rs.getLong("lifetime");
                    float posX = rs.getFloat("pos_x");
                    float posY = rs.getFloat("pos_y");

                    long birthTimeNanos = birthTimeMillis * 1_000_000; //время созд в нанах
                    float birthTimeSecs = birthTimeMillis / 1000f;
                    float lifeTimeSeconds = lifeTimeMillis / 1000f; //время жизни в сек

                    IBehaviour o;
                    if("boy".equals(type)) {
                        o = new BoyStudent(id, posX, posY, lifeTimeSeconds, birthTimeSecs, birthTimeNanos);
                    } else if("girl".equals(type)) {
                        o = new GirlStudent(id, posX, posY, lifeTimeSeconds, birthTimeSecs, birthTimeNanos);
                    } else {
                        continue;
                    }
                    result.add(o);
            }
            }
        }
        if(!result.isEmpty()) {
            for(IBehaviour o : result) {
                o.setCreationTime(0);
                o.setCreationTimeNanos(0);
            }
        }
        for(IBehaviour o : result) {
            if(o instanceof Student s) {
                s.initImage();
            }
        }
        Set<Integer> ids = new HashSet<>();
        TreeMap<Integer,Long> btoid = new TreeMap<>();

        for(IBehaviour o : result) {
            ids.add(o.getId());
            btoid.put(o.getId(), o.getCreationTimeNanos());
        }
        habitat.setObjects(result);
        habitat.setActiveIds(ids);
        habitat.setBirthToId(btoid);
    }

}
