package com.xy.fy.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.xy.fy.singleton.Comment;
import com.xy.fy.singleton.Message;
import com.xy.fy.singleton.Student;
import com.xy.fy.view.ToolClass;

/**
 * 客户端静态变量类
 * 
 * @author 王玉超
 * 
 */
public class StaticVarUtil {

	/**
	 * listHerf中tittle
	 */
	public static final String QUERY_SCORE= "成绩查询";
	public static final String CHANGE_PW = "密码修改";
	/**
	 * handler中的值
	 */
	public static final int JUDGE_ACCOUNT_END = 1;
	public static final int JUDGE_NICKNAME_END = 2;
	public static final int JUDGE_EMAIL_END = 3;
	public static final int INTERNET_ERROR = 4;

	public static final int REGISTER_START = 5;
	public static final int REGISTER_END = 6;

	public static final int GET_PASSWORD_BACK1_START = 7;
	public static final int GET_PASSWORD_BACK1_END = 8;

	public static final int GET_PASSWORD_BACK2_START = 9;
	public static final int GET_PASSWORD_BACK2_END = 10;

	public static final int LOGIN_START = 11;
	public static final int LOGIN_END = 12;

	// 一组常用的handler数据
	public static final int START = 13;
	public static final int END_SUCCESS = 14;
	public static final int END_FAIL = 15;

	public static final String ACCOUNT = "account";
	public static final String PASSWORD = "password";
	public static final String USER_INFO = "userinfo";
	public static final String IS_REMEMBER = "isRemember";

	public static ArrayList<Activity> activities = new ArrayList<Activity>();

	public static Student student = new Student();

	public static String PATH = null;// 应用的目录"/sdcard/FengYun"

	public static String response = null;// 服务器返回的数据

	public static String largePicPath = null;// 大图片路径

	public static String smallPicPath = null;// 小图片路径

	public static Message message = null;// 点击的说说

	public static ExecutorService executorService = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);// 根据系统资源定义线程池大小

	public static String lastMessageTime = null;// 最后一个说说的发表时间
    public static String session = null;//请求中要使用的到的session
	public static String fileName = null;// 缓存的文件名

	// 所有的herf
	public static List<HashMap<String, String>> listHerf = new ArrayList<HashMap<String, String>>();

	public static Student parseJsonToStudent(String strJson) {
		Student student = null;
		try {
			JSONObject jsonObject = new JSONObject(strJson);
			student = new Student();
			student.setAccount(jsonObject.optString("account"));
			student.setPassword(jsonObject.optString("password"));
		} catch (JSONException e) {
			e.printStackTrace();
			student = null;
		}
		return student;
	}

	/**
	 * 按照栈的顺序依次退出
	 */
	public static void quit() {
		for (int i = StaticVarUtil.activities.size() - 1; i >= 0; i--) {
			if (StaticVarUtil.activities.get(i) != null) {
				StaticVarUtil.activities.get(i).finish();
			}
		}
	}

	/**
	 * 得到一个Message
	 * 
	 * @return Message
	 */
	public static Message getItemMessage(String jsonStr) {
		Message message = null;
		try {
			message = new Message();
			JSONObject json = new JSONObject(jsonStr);
			message.setAccount(json.optInt("account"));
			message.setCollegeId(json.optInt("collegeId"));
			message.setColNum(json.optInt("colNum"));
			message.setComNum(json.optInt("comNum"));
			message.setDate(json.optString("date"));
			message.setKind(json.optInt("kind"));
			message.setLarPic(json.optString("larPic"));
			message.setMsgId(json.optInt("msgId"));
			message.setPraNum(json.optInt("praNum"));
			message.setSmaPic(json.optString("smaPic"));
			message.setText(json.optString("text"));
			message.setTime(json.optString("time"));
			message.setNickname(json.optString("nickname"));
			message.setHeadPhoto(json.optString("headPhoto"));
		} catch (Exception e) {
			System.out.println("StaticVarUtil.getItemMessage()出错");
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * 得到所有的消息数据,并且保存最后一个说说的时间
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static ArrayList<Message> getAllMessage(String jsonStr) {
		ArrayList<Message> allMessage = null;
		try {
			allMessage = new ArrayList<Message>();
			JSONArray allJson = new JSONArray(jsonStr);
			for (int i = 0; i < allJson.length(); i++) {
				allMessage.add(getItemMessage(allJson.getString(i)));
			}
			StaticVarUtil.lastMessageTime = allMessage.get(
					allMessage.size() - 1).getDate()
					+ " " + allMessage.get(allMessage.size() - 1).getTime();
		} catch (Exception e) {
			System.out.println("StaticVarUtil.getAllMessage()出错");
			e.printStackTrace();
		}
		return allMessage;
	}

	/**
	 * 本地收藏这个消息
	 * 
	 * @param messageId
	 */
	public static void collect(int messageId, Context context) {
		SharedPreferences share = context.getSharedPreferences(
				"messageIsCollect", Activity.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean(messageId + "", true);
		editor.commit();
	}

	/**
	 * 本地是否收藏
	 * 
	 * @param messageId
	 *            消息主键
	 * @return
	 */
	public static boolean isCollect(int messageId, Context context) {
		if (StaticVarUtil.student == null) {
			return false;
		}
		SharedPreferences share = context.getSharedPreferences(
				"messageIsCollect", Activity.MODE_PRIVATE);
		return share.getBoolean(messageId + "", false);
	}

	/**
	 * 是否被赞过， 取本地数据库，进行对应，如果存在，那么返回true， 否则存入本地数据库，然后返回false
	 * 
	 * @param position
	 *            位置
	 * @return
	 */
	public static boolean isPraised(int messageId, Context context) {
		SharedPreferences share = context.getSharedPreferences(
				"messageIsPraise", Activity.MODE_PRIVATE);
		// 如果数据库中有这个键值对
		if (share.getBoolean(messageId + "", false) == true) {
			return true;
		}
		return false;
	}

	/**
	 * 对这个说说赞，进行保存
	 * 
	 * @param messageId
	 */
	public static void praise(int messageId, Context context) {
		SharedPreferences share = context.getSharedPreferences(
				"messageIsPraise", Activity.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean(messageId + "", true);
		editor.commit();
	}

	/**
	 * 得到所有的Comment
	 * 
	 * @param jsonData
	 *            json数据源
	 */
	public static ArrayList<Comment> getAllComments(String jsonData) {
		ArrayList<Comment> allComments = null;
		try {
			allComments = new ArrayList<Comment>();
			JSONArray jsonArray = new JSONArray(jsonData);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Comment comment = new Comment();
				comment.setContent(jsonObject.getString("content"));
				comment.setDate(jsonObject.getString("date"));
				comment.setName(jsonObject.getString("name"));
				comment.setTime(jsonObject.getString("time"));
				allComments.add(comment);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return allComments;
	}
}
