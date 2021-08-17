/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author suresh
 */

package org.onida.audio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseHelper {
    Connection conn = null;
    Statement stmt = null;
    
    public DatabaseHelper() {
        //Constructor        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/onida_iot", "root", "Vidya@100");            
            stmt = conn.createStatement(); 
        } catch (SQLException ex) {
            System.out.println(" System Error");
        }
        catch (ClassNotFoundException e) {
            System.out.println(e);
            System.out.println("System error");
        }
    }    
    
    public ArrayList<String> getCompany() {
        var retData = new ArrayList<String>();       
        try {
            ResultSet resultset = stmt.executeQuery("select * from company");
   
            while(resultset.next()) {
                retData.add(resultset.getString("comp_name"));
            }
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println(" System Error");
        }
        return retData;   
    }
    
    public ArrayList<String> getProduct(String compStr) {
        ArrayList<String> retData = new ArrayList<>();       
        try {
            ResultSet resultset;
            if(compStr.equals("ALL")) {
                resultset = stmt.executeQuery("SELECT * FROM product");
            } else {
                resultset = stmt.executeQuery("SELECT * FROM product");
            }
            
            while(resultset.next()) {
                retData.add(resultset.getString("prod_name"));
            }
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println(" System Error");
        }
        return retData;   
    }
    
    public ArrayList<String> getModel(String compStr, String prodStr) {
        ArrayList<String> retData = new ArrayList<>();
        try {
            ResultSet resultset = stmt.executeQuery("SELECT * FROM model WHERE model_comp_name='"+compStr+"' AND model_prod_name='"+prodStr+"'");
            while(resultset.next()) {
                retData.add(resultset.getString("model_name"));
            }
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println(" System Error");
        }
        return retData;   
    }
    
    public String setData(ArrayList sqlData) {
        //String compName, String compPers, String compEmail, String compPhone, String compAddr
        String retMsg = "";
        try {        
            PreparedStatement st;
            switch(sqlData.get(10).toString()) {
                case "1":
                    //Company Creation
                    ResultSet resultset = stmt.executeQuery("SELECT * FROM company WHERE comp_name='"+sqlData.get(0).toString()+"'");
                    if(resultset.next()) {
                    //Check if company exists                        
                       retMsg = "ERROR Company name <b>" + sqlData.get(0).toString() + "</b> is already registered"; 
                    }  else {
                        resultset = stmt.executeQuery("SELECT * FROM company WHERE comp_code='"+sqlData.get(1).toString()+"'");
                        if(resultset.next()) {
                            retMsg = "ERROR Company code <b>" + sqlData.get(1).toString() + "</b> is already registered";
                        } else {
                            st = conn.prepareStatement("INSERT into company (comp_name,comp_code,comp_pers, comp_email, comp_phone, comp_addr) VALUES (?,?,?,?,?,?)");
                            st.setString(1, sqlData.get(0).toString());//company_name
                            st.setString(2, sqlData.get(1).toString());//company_code
                            st.setString(3, sqlData.get(2).toString());//company_person
                            st.setString(4, sqlData.get(3).toString());//company_email
                            st.setString(5, sqlData.get(4).toString());//company_phone
                            st.setString(6, sqlData.get(5).toString());//company_addr
                            retMsg = "Created <b>" + sqlData.get(0).toString() + "<b> Company Sucessfully"; 
                            st.executeUpdate();
                            st.close();
                        }
                    }
                    break;
                case "2":
                    //Product Creation
                    resultset = stmt.executeQuery("SELECT * FROM product WHERE prod_name='"+sqlData.get(6).toString()+"'");
                    if(resultset.next()) {
                        //Check if product exists
                        retMsg = "ERROR Product name <b>" + sqlData.get(6).toString() + "</b> is already registered"; 
                    } else {
                        resultset = stmt.executeQuery("SELECT * FROM product WHERE prod_code='"+sqlData.get(7).toString()+"'");
                        if(resultset.next()){
                            retMsg = "ERROR Product code <b>" + sqlData.get(7).toString() + "</b> is already registered";
                        } else {
                            st = conn.prepareStatement("INSERT into product (prod_name,prod_code,prod_desc) VALUES (?,?,?)");
                            st.setString(1, sqlData.get(6).toString());//product_name
                            st.setString(2, sqlData.get(7).toString());//product_code
                            st.setString(2, sqlData.get(8).toString());//product_description
                            retMsg = "Created <b>" + sqlData.get(6).toString() + "</b> Product sucessfully"; 
                            st.executeUpdate();
                            st.close();
                        }
                    }
                    break;
                case "3":
                    //Create Model
                    resultset = stmt.executeQuery("SELECT * FROM model WHERE model_name='"+ sqlData.get(9).toString()+"' AND model_comp_name='"+sqlData.get(13).toString()+"' AND model_prod_name='"+sqlData.get(14).toString()+"'");
                    if(resultset.next()) {
                        //Create model only when Company, Product, Model number combination does not exists
                        retMsg = "ERROR Model with Company <b>" + sqlData.get(13).toString() + "</b> Product <b>" + sqlData.get(14).toString() + "</b> and Model <b>" + sqlData.get(9).toString() + "</b> is already registed"; 
                    } else {
                        resultset = stmt.executeQuery("SELECT * FROM model WHERE model_comp_name='"+sqlData.get(13).toString()+"' AND model_prod_name='"+sqlData.get(14).toString()+"'");
                        int sizeResultSet = 0;
                        while(resultset.next()) {
                            sizeResultSet++;
                        }
                        String modelUniqueId = Integer.toString(sizeResultSet + 1);
                       
                        st = conn.prepareStatement("INSERT into model (model_name,model_comp_name,model_prod_name,model_uniqueid) VALUES (?,?,?,?)");
                        String modelName = sqlData.get(9).toString();//model_name
                        String compName = sqlData.get(13).toString();//dropDown1=company
                        String prodName = sqlData.get(14).toString();//dropDown2=product
                        st.setString(1, modelName);
                        st.setString(2, compName);
                        st.setString(3, prodName);
                        st.setString(4, modelUniqueId);
                        st.executeUpdate();
                        st.close();                       
                        //Form the Database name
                        String companyCode;
                        String productCode;
                        String dynamoDbName;
                        resultset = stmt.executeQuery("SELECT * FROM company WHERE comp_name='"+ compName+"'");
                        if(resultset.next()) {
                            companyCode = resultset.getString("comp_code");
                            resultset = stmt.executeQuery("SELECT * FROM product WHERE prod_name='"+ prodName+"'");
                            if(resultset.next()) {
                                productCode = resultset.getString("prod_code");
                                dynamoDbName = companyCode + productCode;
                                String retValue = CreateDbTable.createTable(dynamoDbName);
                                if(retValue.equals("OK")) {
                                    retMsg = "Created <b>" + modelName + " Model with Database name " +dynamoDbName + "</b> Sucessfully";
                                } else {
                                    retMsg = "Error Could not create table";
                                }
                            } else {
                                retMsg = "ERROR IN CREATION OF <b>" + modelName + "<b> product code";
                            }
                        } else {
                            retMsg = "ERROR IN CREATION OF <b>" + modelName + "<b> Company code";
                        }                                          
                    }
                    break;
            }           
            stmt.close();
            conn.close();            
        } catch(SQLException ex) {
           System.out.println("SQL Error"); 
        }        
        return retMsg;
    }   
}
