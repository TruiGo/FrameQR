package com.alipay.android.client.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.push.util.StringUtils;
import com.alipay.android.security.Des;
import com.alipay.android.security.RSA;
import com.alipay.android.util.LogUtil;

public class DBHelper
{
	public static final String TAG = "Recent_DBHelper.java";

	public static final int aliUser_maxRows = 5;
	public static final int loginUser_maxRows = 5;
    
	private static final String DataBaseName = "RecentDB";
	private static final String TableName = "RecentTable";//原来的表
	private static final String TableName2 = "RecentTable2";//为了保证老用户可以使用新建一个表
	private static final String TablePattern = "USERPATTERN";
	private static final String TableAutoLogin = "AUTOLOGIN";
//	private static final String FieldID = "ID";
//	private static final String FiledNAME = "NAME";
	
	private static final String ColumnPattern = "PATTERN";
	private static final String ColumnName = "NAME";
	private static final String ColumnType = "TYPE";
	private static final String ColumnPassword = "PASSWORD";
	private static final String ColumnRsaPassword = "RSAPASSWORD";
	private static final String ColumnRandomnumPassword = "RANDOMNUM";
	private static final String ColumnAutoLogin = "AUTOLOGIN";
	
	SQLiteDatabase db;
	Context context;

	public DBHelper(Context context) 
	{
		this.open(context);
	}

	private void createTable(String name)
	{
		String sql = "";
		try
		{
			sql = "CREATE TABLE IF NOT EXISTS "
					+ name
					+ " (ID INTEGER PRIMARY KEY autoincrement,  NAME TEXT, PASSWORD TEXT, TYPE TEXT, LOGINTIME TEXT)";
			this.db.execSQL(sql);
//			Log.v(TAG, "Create Table TableName ok");
		}
		catch (Exception e)
		{
//			Log.v(TAG, "Create Table TableName fail");
		}
		finally
		{
			// this.db.close();
//			Log.v(TAG, "Create Table TableName ");
		}
	}
	
	private void createPatternTable()
	{
		String sql = "";
		try
		{
			sql = "CREATE TABLE IF NOT EXISTS "
					+ TablePattern
					+ " (ID INTEGER PRIMARY KEY autoincrement,  NAME TEXT, TYPE TEXT, PATTERN TEXT)";
			this.db.execSQL(sql);
//			Log.v(TAG, "Create Table TableName ok");
		}
		catch (Exception e)
		{
//			Log.v(TAG, "Create Table TableName fail");
		}
		finally
		{
			// this.db.close();
//			Log.v(TAG, "Create Table TableName ");
		}
	}
	
	private void createAutoLoginTable()
	{
		String sql = "";
		try
		{
			sql = "CREATE TABLE IF NOT EXISTS "
					+ TableAutoLogin
					+ " (ID INTEGER PRIMARY KEY autoincrement, NAME TEXT, TYPE TEXT, AUTOLOGIN INTEGER)";
			this.db.execSQL(sql);
//			Log.v(TAG, "Create Table TableName ok");
		}
		catch (Exception e)
		{
//			Log.v(TAG, "Create Table TableName fail");
		}
		finally
		{
			// this.db.close();
//			Log.v(TAG, "Create Table TableName ");
		}
	}

	public boolean save(String name, String password, String type, String loginTime, String userId,String realname,String phoneNo,String mUserAvtarPath)
	{
		
		if (!delete(name, type, false))
			return false;
		
//		String sql = "insert into " + DBHelper.TableName2 + " values(null,'"
//				+ name + "','" + password + "','" + type + "', '" + loginTime + "','" + userId  + "')";
		
		String sql = "insert into " + TableName2 + " values (null,'" + name + "','','" + type + "','" + loginTime + "','" + userId + "','" + (password == null ? "" : password) + "','" + ("".equals(password) ? "": StringUtils.randomString(10)) + "','" + realname + "','" + phoneNo +"','" + mUserAvtarPath + "')";
		
		try
		{
			LogUtil.logOnlyDebuggable(TAG, sql);		
			this.db.execSQL(sql);
			LogUtil.logOnlyDebuggable(TAG, "insert  Table TableName 1 record ok");		
			return true;
		}
		catch (Exception e)
		{

			LogUtil.logOnlyDebuggable(TAG, "insert  Table TableName 1 record fail");
			return false;
		}
		finally
		{
			// this.db.close();
//			Log.v(TAG, "insert  Table TableName ");
		}
	}
	
	public boolean save(String name, String password, String type, String userId,String realname,String phoneNo,String mUserAvtarPath)
	{
		String loginTime = getTimeStamp();
		boolean bRet = save(name, password, type, loginTime, userId,realname,phoneNo, mUserAvtarPath);
		
		//
		// limit total rows of the table.
		//limitRows( loginUser_maxRows );
		
		if (bRet) {
			this.saveAutoLogin(name, type, "".equals(password) ? 0 : 1);
		}
		
		return bRet;
	}
	
	public boolean saveSafepay(String name, String password, String type1, String type2)
	{
		Cursor cur;
		cur = db.query(DBHelper.TableName, 
				new String[] { "ID",
				"NAME AS _id"},
				"NAME LIKE ? and TYPE = ?", new String[] {
				name, type1 }, null,
				null, null);
		if (cur.getCount()>0)
			return false;
		
		String loginTime = getTimeStamp();
		String sql = "insert into " + DBHelper.TableName2 + " values(null,'"
				+ name + "','" + password + "','" + type2 + "', '" + loginTime + "')";
		try
		{
			this.db.execSQL(sql);
//			Log.v(TAG, "insert  Table TableName 1 record ok");		
			return true;
		}
		catch (Exception e)
		{

//			Log.v(TAG, "insert  Table TableName 1 record fail");
			return false;
		}
		finally
		{
			// this.db.close();
//			Log.v(TAG, "insert  Table TableName ");
		}
	}
	
	public boolean delete(String name, String type, boolean deleteAllRelated)
	{
		if (deleteAllRelated) {
			deletePattern(name, type);
			deleteAutoLoginRecord(name, type);
		}
		
		String sql = "delete from " + DBHelper.TableName2 + " where NAME = '"
				+ name + "'" + " and TYPE = '" + type + "'";
		try
		{
			this.db.execSQL(sql);
//			Log.v(TAG, "delete record ok");
			return true;
		}
		catch (Exception e)
		{

//			Log.v(TAG, "delete record fail");
			return false;
		}
		finally
		{
			// this.db.close();
//			Log.v(TAG, "delete  Table TableName ");
		}
	}
	
	public boolean deleteAll()
	{
		String sql = "delete from " + DBHelper.TableName2;
		try
		{
			this.db.execSQL(sql);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
		finally
		{
			// this.db.close();
		}
	}
	
	public boolean deleteAllSafepay(String type)
	{
		String sql = "delete from " + DBHelper.TableName2+ "where TYPE = '" + type + "'" ;
		try
		{
			this.db.execSQL(sql);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
		finally
		{
			// this.db.close();
		}
	}
	public boolean limitRows(int iRows)
	{
		boolean bRet = true;

		UserInfo userInfo = null;
		Cursor cur;
		cur = db.query(DBHelper.TableName2, 
				null,
				null, 
				null, null,
				null, "ID desc" + " limit " + String.valueOf(iRows).toString());
		
		//
		// reserve the last iRows.
		if( cur.getCount() == iRows )
		{
			deleteAll();
			
			if(cur.moveToLast())
			{
				do{
					userInfo = new UserInfo();
					userInfo.userAccount = cur.getString( cur.getColumnIndex("NAME") );
					userInfo.userPassword = cur.getString( cur.getColumnIndex("PASSWORD") );
					userInfo.type = cur.getString( cur.getColumnIndex("TYPE") );
					userInfo.loginTime = cur.getString( cur.getColumnIndex("LOGINTIME") );
					userInfo.userId = cur.getString( cur.getColumnIndex("USERID") );
					userInfo.rsaPassword = cur.getString( cur.getColumnIndex("RSAPASSWORD"));
					userInfo.randomNum = cur.getString( cur.getColumnIndex("RANDOMNUM") );
					
					userInfo.userName = cur.getString( cur.getColumnIndex("REALNAME"));
					userInfo.phoneNo = cur.getString( cur.getColumnIndex("PHONENO") );
					userInfo.userAvtarPath = cur.getString(cur.getColumnIndex("USERAVTARPATH"));
					
					// 
					save(userInfo.userAccount, userInfo.rsaPassword, userInfo.type, userInfo.loginTime, userInfo.userId,userInfo.userName,userInfo.phoneNo,userInfo.userAvtarPath);
				}while(cur.moveToPrevious());
			}
		}
		// else ok.
		
		cur.close();
		
		return bRet;
	}
	
	public Cursor loadData(String id, String type, String limit)
	{
		Cursor cur;
		if (null != id)
		{
			cur = db.query(DBHelper.TableName2, new String[] { "ID",
					"NAME AS _id", "LOGINTIME" }, "NAME LIKE ? and TYPE = ?", new String[] {
					id, type }, null, null, "ID desc" + " limit " + limit);
			// null, null, null, null);
		}
		else
		{
			cur = db.query(DBHelper.TableName2, new String[] { "ID",
					"NAME AS _id", "LOGINTIME" }, "TYPE = ?", new String[] { type }, null,
					null, "ID desc" + " limit " + limit);
		}
		return cur;
	}

	
	public ArrayList<UserInfo> getAllLoginUsers(String type){
		ArrayList<UserInfo>  allLoginUsers = new ArrayList<UserInfo>();
        UserInfo userInfo = null;
		
		Cursor cur;
		if (type == null) {
			cur = db.query(DBHelper.TableName2, 
					new String[] { "ID", "NAME", "PASSWORD", "TYPE", "LOGINTIME", "USERID","RSAPASSWORD","RANDOMNUM","REALNAME","PHONENO","USERAVTARPATH"},
					null, null, null,
					null, "ID desc");
		} else {
			cur = db.query(DBHelper.TableName2, 
					new String[] { "ID", "NAME", "PASSWORD", "LOGINTIME", "USERID","RSAPASSWORD","RANDOMNUM","REALNAME","PHONENO","USERAVTARPATH"},
					"TYPE = ?", 
					new String[] { type }, null,
					null, "ID desc");
		}
		if(cur.getCount()<0){
			return null;
		}else{
		while(cur.moveToNext())
		{
			userInfo = new UserInfo();
			userInfo.userAccount = cur.getString( cur.getColumnIndex("NAME") );
			userInfo.userPassword = cur.getString( cur.getColumnIndex("PASSWORD") );
			if (type==null) {
				userInfo.type = cur.getString( cur.getColumnIndex("TYPE") );
			} else
				userInfo.type = type;
			userInfo.userId = cur.getString( cur.getColumnIndex("USERID") );
			//add two column
			userInfo.rsaPassword = cur.getString(cur.getColumnIndex("RSAPASSWORD"));
			userInfo.randomNum = cur.getString(cur.getColumnIndex("RANDOMNUM"));
			userInfo.userName = cur.getString(cur.getColumnIndex("REALNAME"));
			userInfo.phoneNo = cur.getString(cur.getColumnIndex("PHONENO"));
			userInfo.userAvtarPath = cur.getString(cur.getColumnIndex("USERAVTARPATH"));
			allLoginUsers.add(userInfo);
		}
		}
		cur.close();
		return allLoginUsers;
		
	}
	
	
	public UserInfo getLastLoginUser(String type)
	{ 
		UserInfo userInfo = null;
		
		Cursor cur;
		if (type == null) {
			cur = db.query(DBHelper.TableName2, 
					new String[] { "ID", "NAME", "PASSWORD", "TYPE", "LOGINTIME", "USERID","RSAPASSWORD","RANDOMNUM","REALNAME","PHONENO","USERAVTARPATH"},
					null, null, null,
					null, "ID desc");
		} else {
			cur = db.query(DBHelper.TableName2, 
					new String[] { "ID", "NAME", "PASSWORD", "LOGINTIME", "USERID","RSAPASSWORD","RANDOMNUM","REALNAME","PHONENO","USERAVTARPATH"},
					"TYPE = ?", 
					new String[] { type }, null,
					null, "ID desc");
		}
		if( cur.moveToFirst() )
		{
			userInfo = new UserInfo();
			userInfo.userAccount = cur.getString( cur.getColumnIndex("NAME") );
			userInfo.userPassword = cur.getString( cur.getColumnIndex("PASSWORD") );
			if (type==null) {
				userInfo.type = cur.getString( cur.getColumnIndex("TYPE") );
			} else
				userInfo.type = type;
			userInfo.userId = cur.getString( cur.getColumnIndex("USERID") );
			//add two column
			userInfo.rsaPassword = cur.getString(cur.getColumnIndex("RSAPASSWORD"));
			userInfo.randomNum = cur.getString(cur.getColumnIndex("RANDOMNUM"));
			userInfo.userName = cur.getString(cur.getColumnIndex("REALNAME"));
			userInfo.phoneNo = cur.getString(cur.getColumnIndex("PHONENO"));
			userInfo.userAvtarPath = cur.getString(cur.getColumnIndex("USERAVTARPATH"));
		}
		
		cur.close();
		return userInfo;
	}
	
	/**
	 * 获取所有账户列表
	 */
	public JSONArray getAllUser(String type){ 
	    JSONArray jsonArray = null;
        
        Cursor cur;
        if (type == null) {
            cur = db.query(DBHelper.TableName2, 
                    new String[] { "ID", "NAME"/*, "PASSWORD", "TYPE", "LOGINTIME", "USERID","RSAPASSWORD","RANDOMNUM","REALNAME","PHONENO"*/},
                    null, null, null,
                    null, "ID desc");
        } else {
            cur = db.query(DBHelper.TableName2, 
                    new String[] { "ID", "NAME"/*, "PASSWORD", "LOGINTIME", "USERID","RSAPASSWORD","RANDOMNUM","REALNAME","PHONENO"*/},
                    "TYPE = ?", 
                    new String[] { type }, null,
                    null, "ID desc");
        }
        if( cur.moveToFirst() )
        {
            jsonArray = new JSONArray();
            do {
                String account = cur.getString( cur.getColumnIndex("NAME") );
                jsonArray.put(account);
            } while (cur.moveToNext());
        }
        
        cur.close();
        return jsonArray;
    }

	public UserInfo getUser(String userName, String Type)
	{ 
		UserInfo userInfo = null;
		
		if(!db.isOpen())
		    return userInfo;
		Cursor cur;
		cur = db.query(DBHelper.TableName2, 
				new String[] { "ID",	"NAME", "PASSWORD", "USERID","RSAPASSWORD","RANDOMNUM","REALNAME","PHONENO","USERAVTARPATH"},
				"NAME = ? AND TYPE = ?", 
				new String[] { userName, Type }, null,
				null, "");
	
		if( cur.moveToFirst() )
		{
			userInfo = new UserInfo();
			userInfo.userAccount = cur.getString( cur.getColumnIndex("NAME") );
			userInfo.userPassword = cur.getString( cur.getColumnIndex("PASSWORD") );
			userInfo.userId = cur.getString( cur.getColumnIndex("USERID") );
			
			if(cur.getString( cur.getColumnIndex("RSAPASSWORD") ) != null){
				userInfo.rsaPassword = cur.getString( cur.getColumnIndex("RSAPASSWORD"));
			}
			
			if(cur.getString( cur.getColumnIndex("RANDOMNUM") ) != null){
				userInfo.randomNum = cur.getString( cur.getColumnIndex("RANDOMNUM") );
			}
			
			if(cur.getString( cur.getColumnIndex("REALNAME") ) != null){
				userInfo.userName = cur.getString( cur.getColumnIndex("REALNAME") );
			}
			
			if(cur.getString( cur.getColumnIndex("PHONENO") ) != null){
				userInfo.phoneNo = cur.getString( cur.getColumnIndex("PHONENO") );
			}
			if(cur.getString( cur.getColumnIndex("USERAVTARPATH") ) != null){
				userInfo.userAvtarPath = cur.getString( cur.getColumnIndex("USERAVTARPATH") );
			}
			
		}
		
		cur.close();
		
		return userInfo;
	}
	
	public void open(Context context)
	{
		if (null == db || !this.db.isOpen())
		{
			this.context = context;
			this.db = context.openOrCreateDatabase(DataBaseName, Context.MODE_PRIVATE, null);
			copyToNewTable();
			createTable(DBHelper.TableName2);
			createPatternTable();
			createAutoLoginTable();
//			Log.v(this.TAG, "create  or Open DataBase");
			
			Cursor cur = this.db.query(TableName2, 
					null, null, null, null,
					null, "ID desc");
			
//			Log.v(this.TAG, "get column:"+cur.getColumnIndex("USERID"));
			if (cur.getColumnIndex("USERID")== -1) {
				this.db.execSQL("alter table  " + TableName2 + " add column" + " USERID" +  " TEXT" + " ;");
			}
			
			//add two column to the new table. rasPassword,randomNum
			if(cur.getColumnIndex("RSAPASSWORD") == -1){
				this.db.execSQL("ALTER TABLE " + TableName2 + " ADD COLUMN RSAPASSWORD TEXT;");
			}
			if(cur.getColumnIndex("RANDOMNUM") == -1){
				this.db.execSQL("ALTER TABLE " + TableName2 + " ADD COLUMN RANDOMNUM TEXT;");
				//init randomNum for all column 
				initRandomNum();
			}
			
			if(cur.getColumnIndex("REALNAME") == -1){
				this.db.execSQL("ALTER TABLE " + TableName2 + " ADD COLUMN REALNAME TEXT;");
			}
			if(cur.getColumnIndex("PHONENO") == -1){
				this.db.execSQL("ALTER TABLE " + TableName2 + " ADD COLUMN PHONENO TEXT;");
			}
			if(cur.getColumnIndex("USERAVTARPATH") == -1){
				this.db.execSQL("ALTER TABLE " + TableName2 + " ADD COLUMN USERAVTARPATH TEXT;");
			}
		}
	}
	
    private void copyToNewTable()
    {
    	try
		{
			Cursor cur = db.query(DBHelper.TableName, 
					new String[] { "ID",	"NAME", "PASSWORD", "TYPE","LOGINTIME" },
					null, 
					null, null,
					null, null);
			if(cur.getCount()>0)
			{
				createTable(DBHelper.TableName2);
				while( cur.moveToNext() )
				{
					UserInfo userInfo = new UserInfo();
					userInfo.userAccount = cur.getString( cur.getColumnIndex("NAME") );
					userInfo.userPassword = cur.getString( cur.getColumnIndex("PASSWORD") );
					userInfo.type = cur.getString( cur.getColumnIndex("TYPE") );
					userInfo.loginTime = cur.getString( cur.getColumnIndex("LOGINTIME") );
					userInfo.userName = cur.getString( cur.getColumnIndex("REALNAME") );
					userInfo.phoneNo = cur.getString( cur.getColumnIndex("PHONENO") );
					userInfo.userAvtarPath = cur.getString(cur.getColumnIndex("USERAVTARPATH"));
					// 
					save(userInfo.userAccount, Des.encrypt(userInfo.userPassword,Constant.ALIPAY_INFO), userInfo.type, userInfo.loginTime,userInfo.userName,userInfo.phoneNo,userInfo.userAvtarPath);
				}
				
			}
			cur.close();
			
			this.db.execSQL("DROP TABLE IF EXISTS " + DBHelper.TableName);
		}
		catch(Exception e)
		{
			
		}
    }
    
    public void processPassword(String rsaPucKey) {
    	try
		{
			Cursor cur = db.query(TableName2, 
					new String[] { "ID","PASSWORD"},
					null, 
					null, null,
					null, null);
			if(cur.getCount()>0)
			{
				String rsaPassword = "";
				while(cur.moveToNext())
				{
					if(!"".equals(cur.getString(cur.getColumnIndex("PASSWORD")))){
						rsaPassword = RSA.encrypt(Des.decrypt(cur.getString(cur.getColumnIndex("PASSWORD")), Constant.ALIPAY_INFO), rsaPucKey);
						//alert userInfo in db
						transform2RSAPassword(rsaPassword,cur.getString(cur.getColumnIndex("ID")));
					}
				}
			}
			cur.close();
		}
		catch(Exception e){
		}
	}
    
    ContentValues values = new ContentValues();
	private void transform2RSAPassword(String rsaPassword,String id) {
		try {
			values.clear();
			values.put("PASSWORD", "");
			values.put("RSAPASSWORD", rsaPassword);
			
			db.update(TableName2, values, "ID = ?", new String[]{id});
		} catch (Exception e) {
		}
	}
	
	public void updateRandomNum(String id){
		try{
			ContentValues values = new ContentValues();
			values.put("RANDOMNUM", StringUtils.randomString(10));
			db.update(TableName2, values, "ID = ?", new String[]{id});
		}catch (Exception e) {
		}
	}

	public void initRandomNum(){
		Cursor cur = null;
		try{
			cur = db.query(TableName2, 
					new String[]{"ID","PASSWORD"},
					null, 
					null, null,
					null, null);
			ContentValues values = new ContentValues();
			String id;
			if(cur.getCount() >= 1){
				while(cur.moveToNext()){
					if(!"".equals(cur.getString(cur.getColumnIndex("PASSWORD")))){
						id = cur.getString(cur.getColumnIndex("ID"));
						values.clear();
						values.put("RANDOMNUM", StringUtils.randomString(10));
						db.update(TableName2, values, "ID = ?", new String[]{id});
					}
				}
			}
		}catch (Exception e) {
		}finally{
			cur.close();
		}
	}

	public void close()
	{
		try {
			if(db.isOpen())
				db.close();
		} catch (Exception e) {
		}
	}
	
	String getTimeStamp()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String strKey = format.format(date);
		return strKey;
	}
	
	public int resetRsaPassword(String userName, String type) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ColumnRsaPassword, "");
		contentValues.put(ColumnRandomnumPassword, "");
		
		int num = db.update(TableName2, contentValues, "NAME = ? AND TYPE = ?", new String[] {userName, type});
	    return num;
	}
	
	public int getAutoLogin(String userName, String type) {
		Cursor cur = db.query(TableAutoLogin, 
				new String[] {ColumnAutoLogin},
				"NAME = ? AND TYPE = ?", 
				new String[] {userName, type}, null,
				null, "");
		
		int ret = -1;
		if (null != cur) {
			if (cur.moveToFirst() && cur.getCount() > 0) {
				ret = cur.getInt(cur.getColumnIndex(ColumnAutoLogin));
			}
			
			cur.close();
		}

		return ret;
	}
	
	public void saveAutoLogin(String userName, String type, int autoLoginStatus) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ColumnAutoLogin, autoLoginStatus);
		int num = db.update(TableAutoLogin, contentValues, "NAME = ? AND TYPE = ?", new String[] {userName, type});
		if (0 == num) {
			contentValues.put(ColumnName, userName);
			contentValues.put(ColumnType, type);
			
			long ret = db.insert(TableAutoLogin, null, contentValues);
			if (-1 == ret) {
				ret = 0;
			}
			ret = 0;
		}
	}
	
	public int deleteAutoLoginRecord(String userName, String type) {
		return db.delete(TableAutoLogin, 
				"NAME = ? AND TYPE = ?", 
				new String[] {userName, type});
	}
	
	public String getPattern(String userName, String type) {
		Cursor cur = db.query(TablePattern, 
				new String[] {ColumnPattern},
				"NAME = ? AND TYPE = ?", 
				new String[] {userName, type}, null,
				null, "");
		
		String pattern = null;
		if (null != cur) {
			if( cur.moveToFirst() )
			{
				pattern = cur.getString( cur.getColumnIndex(ColumnPattern) );
			}
			
			cur.close();
		}

		return pattern;
	}
	
	public int deletePattern(String userName, String type) {
		return db.delete(TablePattern, 
				"NAME = ? AND TYPE = ?", 
				new String[] {userName, type});
	}
	
	public void updatePattern(String userName, String type, String pattern) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ColumnPattern, pattern);
		int num = db.update(TablePattern, contentValues, "NAME = ? AND TYPE = ?", new String[] {userName, type});
		if (0 == num ) {
			contentValues.put(ColumnName, userName);
			contentValues.put(ColumnType, type);
			long ret = db.insert(TablePattern, null, contentValues);
			if (-1 == ret) {
				ret = 0;
			}
			ret = 0;
		}
	}
}
