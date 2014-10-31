package com.geek.geeksearch.util;

/**
 * 数据库操作
 *
 */
/************************************************* 
DBConnection 类实现了mysql连接类的封装
*************************************************/  

/**
* <p>Title: DBConnection</p> 
* <p>Description: DBConnection，封装了众多的数据库操作</p> 
* <p>Copyright: Copyright (c) 2010</p> 
* <p>Company: </p> 
*  @author  <a href="dreamhunter.dy@gmail.com">dongyu</a> 
*  @version  1.0 
*  @created 2010-03-16
*/  


import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;




public class DBOperator 
{
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement prepstmt = null;
    private String url = "jdbc:mysql://localhost:3306/GeekDB"; // URL指向要访问的数据库名search_engine
    private String user = "root"; // MySQL配置时的用户名
    private String password = ""; // MySQL配置时的密码
    
    public DBOperator()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
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
            conn = DriverManager.getConnection(url, user, password);
            this.prepareStatement(sql);
        }catch(Exception e){
            e.printStackTrace();
        }       
    }
    
    public Connection getConnection() 
    {
        return conn;
    }
    
    public void prepareStatement(String sql) 
    {
        try
        {
            prepstmt = conn.prepareStatement(sql);
        }catch(Exception e){
            e.printStackTrace();
        } 
    }

    public void setString(int index,String value)
    {   
        try
        {
            prepstmt.setString(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setInt(int index,int value)
    {
        try
        {
            prepstmt.setInt(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setBoolean(int index,boolean value)
    { 
        try
        {
            prepstmt.setBoolean(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setDate(int index,Date value) throws SQLException 
    {
        try
        {
            prepstmt.setDate(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setLong(int index,long value) throws SQLException 
    {
        try
        {
            prepstmt.setLong(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setFloat(int index,float value) throws SQLException 
    {
        try
        {
            prepstmt.setFloat(index,value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void setBinaryStream(int index,InputStream in,int length) throws SQLException
    {
        try
        {
            prepstmt.setBinaryStream(index,in,length);
        }catch(Exception e){
            e.printStackTrace();
        }
    } 
    
    public void clearParameters()throws SQLException
    {
        try
        {
            prepstmt.clearParameters();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public PreparedStatement getPreparedStatement()
    {
        return prepstmt;
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
            if (prepstmt != null) 
                return prepstmt.executeQuery();
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
            e.printStackTrace();
        }
    }
    
    public void executeUpdate()
    {
        try
        {
            if (prepstmt != null)
                prepstmt.executeUpdate();
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
            if (prepstmt != null) 
            {
                prepstmt.close();
                prepstmt = null;
            }
            conn.close();
            conn = null;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}