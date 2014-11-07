package org.geek.geeksearch.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.geek.geeksearch.configure.Configuration;


/**
 * 数据库操作
 *
 */
public class DBOperator 
{
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement prepStmt = null;
    /* configure.properties */
    private String path_GeekDB = null;//指向要访问的数据库名
    private String user_GeekDB = null;// MySQL配置时的用户名
    private String password_GeekDB = null; // MySQL配置时的密码
    
    public DBOperator(Configuration config)
    {
        try
        {
        	path_GeekDB = config.getValue("path_GeekDB");
        	user_GeekDB = config.getValue("user_GeekDB");
        	password_GeekDB = config.getValue("password_GeekDB");
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(path_GeekDB, user_GeekDB, password_GeekDB);
            stmt = conn.createStatement();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public DBOperator(String sql)     //若提供预备好的sql,是要用prepareStatement
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(path_GeekDB, user_GeekDB, password_GeekDB);
            this.prepareStatement(sql);
        }catch(Exception e){
            e.printStackTrace();
        }       
    }
    
    public void cleanAllTables() {
    	String sql = " TRUNCATE TABLE TermsIndex ";
		executeUpdate(sql);
    	sql = " TRUNCATE TABLE PagesIndex ";
		executeUpdate(sql);
		sql = " TRUNCATE TABLE DocsIndex ";
		executeUpdate(sql);
		sql = " TRUNCATE TABLE InvertedIndex ";
		executeUpdate(sql);
	}
    
    public Connection getConnection() 
    {
        return conn;
    }
    
    public void prepareStatement(String sql) 
    {
        try
        {
        	prepStmt = conn.prepareStatement(sql);
        }catch(Exception e){
            e.printStackTrace();
        } 
    }

    public void setString(int index,String value)
    {   
        try
        {
        	prepStmt.setString(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setInt(int index,int value)
    {
        try
        {
        	prepStmt.setInt(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setBoolean(int index,boolean value)
    { 
        try
        {
        	prepStmt.setBoolean(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setDate(int index,Date value) throws SQLException 
    {
        try
        {
        	prepStmt.setDate(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setLong(int index,long value) throws SQLException 
    {
        try
        {
        	prepStmt.setLong(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setFloat(int index,float value) throws SQLException 
    {
        try
        {
        	prepStmt.setFloat(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setBinaryStream(int index,InputStream in,int length) throws SQLException
    {
        try
        {
        	prepStmt.setBinaryStream(index,in,length);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void clearParameters()throws SQLException
    {
        try
        {
        	prepStmt.clearParameters();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public PreparedStatement getPreparedStatement()
    {
        return prepStmt;
    }
    
    public Statement getStatement() 
    {
        return stmt;
    }
    
    public ResultSet executeQuery(String sql)
    {
        try
        {
            if (stmt != null) 
                return stmt.executeQuery(sql);
            else 
                return null;
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
    
    public ResultSet executeQuery()
    {
        try
        {
            if (prepStmt != null) 
                return prepStmt.executeQuery();
            else
                return null;
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void executeUpdate(String sql)
    {
        try
        {
            if (stmt != null)
                stmt.executeUpdate(sql);
        }catch(Exception e){
        	System.err.println("error occurs while updating database: "+sql);
            e.printStackTrace();
        }
    }
    
    public void executeUpdate()
    {
        try
        {
            if (prepStmt != null)
            	prepStmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void close()     
    {
        try
        {
            if (stmt != null) 
            {
                stmt.close();
                stmt = null;
            }
            if (prepStmt != null) 
            {
            	prepStmt.close();
            	prepStmt = null;
            }
            conn.close();
            conn = null;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}