package com.xy.fy.main;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cardsui.example.MyCard;
import com.cardsui.example.MyPlayCard;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.xy.fy.adapter.ChooseHistorySchoolExpandAdapter;
import com.xy.fy.adapter.ChooseSchoolExpandAdapter;
import com.xy.fy.adapter.ListViewAdapter;
import com.xy.fy.util.BitmapUtil;
import com.xy.fy.util.DownLoadThread;
import com.xy.fy.util.HttpUtil;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;
import com.xy.fy.view.HistoryCollege;
import com.xy.fy.view.ToolClass;

@SuppressLint("HandlerLeak")
/**
 * ��һ�����������������  ��ѯ�ɼ�
 * @author Administrator
 * 2014-7-21
 */
public class MainActivity extends Activity {

	public static SlidingMenu slidingMenu;// �໬���
	private Button refresh;// ˢ�°�ť
	private Button chooseCollege;// ѡ��ѧУ��ť
	private Button chooseMsgKind;// ѡ��˵˵����
	private Button chooseMsgSort;// ѡ��˵˵����ʽ
	private ProgressBar progress;// ˢ��ʱ��ˢ�°�ť��Ϊprogress
	// private CustomListView listView;// ˵˵�б����Լ������ListView
	private CardUI mCardView;
	private TextView nickname;// �û���
	private String name;
	private LinearLayout menuBang = null;// �ɼ���ѯ
	private LinearLayout menuMyBukao = null;// ������ѯ
	private LinearLayout menuMyPaiming = null;// �ҵ�����
	private LinearLayout menuMyCollect = null;// ���ղص�
	private LinearLayout menuSetting = null;// ����
	private LinearLayout menuAbout = null;// ����
	ArrayList<HashMap<String, Object>> listItem;// json����֮����б�
	private String score_json;// json����
	private ListViewAdapter listViewAdapter = null;

	private ProgressDialog dialog = null;

	private Bitmap bitmap = null;// �޸�ͷ��

	private static final int PIC = 11;// ͼƬ
	private static final int PHO = 22;// ����
	private static final int RESULT = 33;// ���ؽ��

	private int page = 0;

	private ChooseSchoolExpandAdapter adapter = new ChooseSchoolExpandAdapter(
			MainActivity.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_main);

		// ���� ��ȡ �ɼ�
		GetScoreAsyntask getScoreAsyntask = new GetScoreAsyntask();
		getScoreAsyntask.execute();
		dialog = ViewUtil.getProgressDialog(MainActivity.this, "�����޸�");

		setMenuItemListener();

		// ��ǰActivity��ջ
		StaticVarUtil.activities.add(MainActivity.this);

		// �ҵ�ID
		slidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);

		// ��sliding�������
		slidingMenu.setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
				// ��ȡ��ǰ�˵���ѡ��
				int item = getCurrentMeunItem();
				if (item == 1) {
					setMenuItemState(menuBang, true, menuMyBukao, false,
							menuMyPaiming, false, menuMyCollect, false,
							menuSetting, false, menuAbout, false);
				} else if (item == 2) {
					setMenuItemState(menuBang, false, menuMyBukao, true,
							menuMyPaiming, false, menuMyCollect, false,
							menuSetting, false, menuAbout, false);
				} else if (item == 3) {
					setMenuItemState(menuBang, false, menuMyBukao, false,
							menuMyPaiming, true, menuMyCollect, false,
							menuSetting, false, menuAbout, false);
				} else if (item == 4) {
					setMenuItemState(menuBang, false, menuMyBukao, false,
							menuMyPaiming, false, menuMyCollect, true,
							menuSetting, false, menuAbout, false);
				} else if (item == 5) {
					setMenuItemState(menuBang, false, menuMyBukao, false,
							menuMyPaiming, false, menuMyCollect, false,
							menuSetting, true, menuAbout, false);
				} else if (item == 6) {
					setMenuItemState(menuBang, false, menuMyBukao, false,
							menuMyPaiming, false, menuMyCollect, false,
							menuSetting, false, menuAbout, true);
				}
			}
		});

		// slidingMenu.setOnClosedListener(new OnClosedListener() {
		// @Override
		// public void onClosed() {
		// System.out.println("�˵��ر�");
		// menu2();
		// }
		// });
	}

	/**
	 * �ڶ����˵���
	 * 
	 * @param title
	 *            ��Ŀ
	 * @param fileName
	 *            Ҫ������ļ���������
	 */
	private void menuMy(String title, final String fileName,
			final DownLoadThread downLoadThread) {
		// ����
		Button butMy = (Button) findViewById(R.id.butMy);
		butMy.setText(title);

		// �����������Ĺ��ܲ�����
		if (StaticVarUtil.student == null) {
			ViewUtil.toastShort("�Բ������ȵ�¼..", MainActivity.this);
			return;
		}

		page = 0;
		// ˢ�°�ť
		refresh = (Button) findViewById(R.id.butRefresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ˢ�µ�ʱ��
				page = 0;
				// downLoadThread.setPageAndHanlder(page, handler);
				StaticVarUtil.fileName = fileName;
				StaticVarUtil.executorService.submit(downLoadThread);
			}
		});

		/*
		 * listView = (CustomListView) findViewById(R.id.listView);
		 * 
		 * FileCache fileCache = new FileCache(); String dataResource =
		 * fileCache.readHistoryJsonData(fileName); if (dataResource == null ||
		 * dataResource.equals("")) {// ���Ϊ�գ��Զ�ˢ�� page = 0;
		 * downLoadThread.setPageAndHanlder(page, handler);
		 * StaticVarUtil.executorService.submit(downLoadThread); } else {//
		 * ���򽫻����е�������ȡ���� listViewAdapter = new ListViewAdapter(dataResource,
		 * MainActivity.this); listView.setAdapter(listViewAdapter);
		 * listViewAdapter.notifyDataSetChanged();// ���ݸ��� } // ����ˢ��
		 * listView.setOnRefreshListener(new OnRefreshListener() {
		 * 
		 * @Override public void onRefresh() { page = 0;
		 * downLoadThread.setPageAndHanlder(page, handler);
		 * StaticVarUtil.executorService.submit(downLoadThread); } });
		 * 
		 * // �鿴���ఴť listView.setOnMoreListener(new OnMoreButtonListener() {
		 * 
		 * @Override public void onClick(View v) { page++;
		 * downLoadThread.setPageAndHanlder(page, handlerMore);
		 * StaticVarUtil.executorService.submit(downLoadThread); } });
		 */
	}

	/**
	 * ��һ���˵���
	 */
	private void menu1() {
		page = 0;// �����������

		// init CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);

		CardStack stack2 = new CardStack();
		stack2.setTitle("REGULAR CARDS");
		mCardView.addStack(stack2);

		// add AndroidViews Cards
		mCardView.addCard(new MyCard("By Mc"));// ���忨Ƭ ������������ ѧ��
		mCardView.addCardToLastStack(new MyCard("for Xiyou"));
		MyCard androidViewsCard = new MyCard("www.xiyoumobile.com");
		androidViewsCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(Intent.ACTION_VIEW, Uri
						.parse("www.xiyoumobile.com"));
				it.setClassName("com.android.browser",
						"com.android.browser.BrowserActivity");
				startActivity(it);

			}
		});
		androidViewsCard.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(v.getContext(), "This is a long click",
						Toast.LENGTH_SHORT).show();
				return true;
			}

		});
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("www.xiyoumobile.com"));

		mCardView.addCardToLastStack(androidViewsCard);

		Resources resources = getResources();
		String[] xn = resources.getStringArray(R.array.xn);
		for (int i = 0; i < xn.length; i++) {
			showCard(xn[i]);
		}
		// draw cards
		mCardView.refresh();

	}

	/**
	 * ��ʾ��Ƭ
	 */
	private void showCard(String xn) {
		

		// add one card, and then add another one to the last stack.
		String first_score = getScore(xn, "1") == null ? "" : getScore(xn, "1")
				.toString();
		if (!first_score.equals("")) {
			CardStack stackPlay = new CardStack();
			stackPlay.setTitle(xn);
			mCardView.addStack(stackPlay);
			MyPlayCard _myPlayCard = new MyPlayCard("��һѧ��", first_score,
					"#33b6ea", "#33b6ea", true, false);
			String[][] first_score_array = getScoreToArray(first_score);
			_myPlayCard.setOnClickListener(new ScoreClass(
					first_score_array.length, first_score_array,
					"2013-2014 ��һѧ��"));
			mCardView.addCard(_myPlayCard);
			// mCardView.addCardToLastStack(new
			// MyCard("By Androguide & GadgetCheck"));
		}

		String second_score = getScore(xn, "2") == null ? ""
				: getScore(xn, "2").toString();
		if (!second_score.equals("")) {
			MyPlayCard myCard = new MyPlayCard("�ڶ�ѧ��", second_score, "#e00707",
					"#e00707", false, true);
			String[][] second_score_array = getScoreToArray(second_score);
			myCard.setOnClickListener(new ScoreClass(second_score_array.length,
					second_score_array, "2013-2014 �ڶ�ѧ��"));
			mCardView.addCardToLastStack(myCard);
		}

	}

	/**
	 * �����Ƭ��ת
	 * 
	 * @author Administrator 2014-7-23
	 */
	class ScoreClass implements OnClickListener {

		int col;// �ɼ������к�
		String[][] score;// �����гɼ�����Ϊ��ά����
		String xn;

		public ScoreClass(int col, String[][] score, String xn) {
			this.col = col;
			this.score = score;
			this.xn = xn;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = new Intent();
			i.setClass(getApplicationContext(), ShowScoreActivity.class);
			Bundle b = new Bundle();
			b.putString("col", String.valueOf(col));
			for (int j = 0; j < score.length; j++) {
				b.putStringArray("score" + j, score[j]);
			}
			b.putString("xn_and_xq", xn);
			i.putExtras(b);
			startActivity(i);
		}

	}

	/**
	 * �ı�������ɫ
	 * 
	 * @param str
	 * @return
	 */
	private String change_color(String str) {
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		// SpannableStringBuilderʵ��CharSequence�ӿ�
		style.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		/*
		 * style.setSpan(new ForegroundColorSpan(Color.YELLOW), 2,
		 * 4,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE ); style.setSpan(new
		 * ForegroundColorSpan(Color.GREEN), 4,
		 * 6,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
		 */
		return style.toString();
	}

	/**
	 * �� �ɼ����ϳ� n��4�е����飬Ϊ�˿�����ʾ��tableҳ����
	 * 
	 * @param score
	 * @return
	 */
	private String[][] getScoreToArray(String score) {
		String[] s = score.split("\n");
		String[][] result = new String[s.length][4];// n�� 4�е�����
		for (int i = 0; i < result.length; i++) {
			result[i] = s[i].split("--");
		}
		return result;
	}

	/**
	 * ���� ��ȡ�̶�ѧ�� �̶�ѧ�ڵĳɼ�
	 */
	private StringBuilder getScore(String xn, String xq) {
		StringBuilder result = null;
		if (listItem != null) {
			result = new StringBuilder();
			// ����json
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(score_json);
				JSONArray jsonArray = (JSONArray) jsonObject
						.get("liScoreModels");// ѧ��
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject o = (JSONObject) jsonArray.get(i);
					if (o.get("xn").equals(xn)) {// ĳ��ѧ��
						JSONArray jsonArray2 = (JSONArray) o
								.get("list_xueKeScore");
						for (int j = 0; j < jsonArray2.length(); j++) {
							JSONObject jsonObject2 = (JSONObject) jsonArray2
									.get(j);
							if (jsonObject2.get("xq").equals(xq)) {
								result.append(jsonObject2.get("kcmc") == null ? " "
										: change_color(jsonObject2.get("kcmc")
												.toString()));
								// result.append(jsonObject2.get("kcxz")==null?" ":"--"+change_color(jsonObject2.get("kcxz").toString()));
								result.append(/* jsonObject2.get("cj")==null?" ": */"--"
										+ change_color(jsonObject2.get("cj")
												.toString()));
								// result.append(jsonObject2.get("xf")==null?" ":"--"+change_color(jsonObject2.get("xf").toString()));
								result.append(jsonObject2.get("pscj")
										.equals("") ? "/" : "--"
										+ change_color(jsonObject2.get("pscj")
												.toString()));
								result.append(jsonObject2.get("qmcj")
										.equals("") ? "/" : "--"
										+ change_color(jsonObject2.get("qmcj")
												.toString()));
								// result.append(jsonObject2.get("xymc")==null?" ":"--"+change_color(jsonObject2.get("xymc").toString()));
								result.append("\n");
							}
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * for (HashMap<String, Object> score : listItem) { if
			 * (score.get("xn").equals(xn)) {//ĳ��ѧ�� String s = (String)
			 * score.get("list_xueKeScore");
			 * 
			 * 
			 * 
			 * 
			 * result.append(hashMap.getKcmc()==null?"":"�γ�:"+hashMap.getKcmc());
			 * result
			 * .append(hashMap.getKcxz()==null?"":" ����:"+hashMap.getKcxz());
			 * result.append(hashMap.getCj()==null?"":" �ɼ�:"+hashMap.getCj());
			 * result.append(hashMap.getXf()==null?"":" ѧ��:"+hashMap.getXf());
			 * result
			 * .append(hashMap.getPscj()==null?"":" ƽʱ�ɼ�:"+hashMap.getPscj());
			 * result
			 * .append(hashMap.getQmcj()==null?"":" ��ĩ�ɼ�"+hashMap.getQmcj());
			 * result
			 * .append(hashMap.getXymc()==null?"":" ѧԺ:"+hashMap.getXymc());
			 * 
			 * } } }
			 */
		}
		return result;
	}

	/**
	 * ������Ϣ��handler
	 */
	/*
	 * private Handler handlerMore = new Handler() { public void
	 * handleMessage(Message msg) { switch (msg.what) { case
	 * StaticVarUtil.START: listView.start(); break; case
	 * StaticVarUtil.END_SUCCESS: listView.finish();
	 * listViewAdapter.addData(StaticVarUtil.response);// ��������
	 * listView.setAdapter(listViewAdapter); adapter.notifyDataSetChanged();
	 * System.out.println("page:" + page); listView.setSelection(page * 20);
	 * break; case StaticVarUtil.END_FAIL: listView.finish();
	 * ViewUtil.toastShort("������Ϣʧ��", MainActivity.this); break; case
	 * StaticVarUtil.INTERNET_ERROR: listView.finish();
	 * ViewUtil.toastShort("�����쳣", MainActivity.this); break; default: break; }
	 * }; };
	 */
	/**
	 * ˢ�µ�handler
	 */
	/*
	 * Handler handler = new Handler() { public void handleMessage(Message msg)
	 * { switch (msg.what) { case StaticVarUtil.START:
	 * progress.setVisibility(View.VISIBLE); refresh.setVisibility(View.GONE);
	 * break; case StaticVarUtil.END_SUCCESS: progress.setVisibility(View.GONE);
	 * refresh.setVisibility(View.VISIBLE); listView.refreshComplete(); String
	 * dataResource = StaticVarUtil.response; // �����̸߳��±���jsonCache�ļ�,��׷������
	 * FileCache fileCache = new FileCache();
	 * fileCache.updateJsonCache(dataResource, false, StaticVarUtil.fileName);
	 * // ����adapter listViewAdapter = new ListViewAdapter(dataResource,
	 * MainActivity.this); listView.setAdapter(listViewAdapter); break; case
	 * StaticVarUtil.END_FAIL: progress.setVisibility(View.GONE);
	 * refresh.setVisibility(View.VISIBLE);
	 * ViewUtil.toastLength("�ͻ��˴���,�뷴�����񳬣�QQ��1154786190", MainActivity.this);
	 * break; case StaticVarUtil.INTERNET_ERROR:
	 * progress.setVisibility(View.GONE); refresh.setVisibility(View.VISIBLE);
	 * ViewUtil.toastLength("�������,���Ժ�����...", MainActivity.this); break; default:
	 * break; } }; };
	 */

	/**
	 * ���õ�ǰMenuItem��״̬
	 * 
	 * @param item
	 *            MenuItem�����flag�������״̬
	 */
	private void setMenuItemState(LinearLayout item1, boolean flag1,
			LinearLayout item2, boolean flag2, LinearLayout item3,
			boolean flag3, LinearLayout item4, boolean flag4,
			LinearLayout item5, boolean flag5, LinearLayout item6, boolean flag6) {
		item1.setPressed(flag1);
		item2.setPressed(flag2);
		item3.setPressed(flag3);
		item4.setPressed(flag4);
		item5.setPressed(flag5);
		item6.setPressed(flag6);
	}

	/**
	 * ����һЩmenuItem����
	 */
	private void setMenuItemListener() {

		nickname = (TextView) findViewById(R.id.nickname);// �û���
		menuBang = (LinearLayout) findViewById(R.id.menu_bang);// 1.�ɼ���ѯ
		menuMyBukao = (LinearLayout) findViewById(R.id.menu_my_bukao);// 2.������ѯ
		menuMyPaiming = (LinearLayout) findViewById(R.id.menu_my_paiming);// 3.�ҵ�����
		menuMyCollect = (LinearLayout) findViewById(R.id.menu_my_collect);// 4.���ղص�
		menuSetting = (LinearLayout) findViewById(R.id.menu_setting);// 5.����
		menuAbout = (LinearLayout) findViewById(R.id.menu_about);

		LinearLayout menuQuit = (LinearLayout) findViewById(R.id.menu_quit);
		menuBang.setPressed(true);// ��ʼ��Ĭ���Ƿ��ư񱻰���
		setCurrentMenuItem(1);// ��¼��ǰѡ��λ��

		menuBang.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setMenuItemState(menuBang, true, menuMyBukao, false,
						menuMyPaiming, false, menuMyCollect, false,
						menuSetting, false, menuAbout, false);
				setCurrentMenuItem(1);// ��¼��ǰѡ��λ��
				slidingMenu.toggle();// ҳ����ת

				slidingMenu.setContent(R.layout.card_main);
				menu1();
			}
		});

		menuMyBukao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "����Գ������Ŭ�������У��������ע...",
						2000).show();
				/*
				 * setMenuItemState(menuBang, false, menuMyBukao, true,
				 * menuMyPaiming, false, menuMyCollect, false, menuSetting,
				 * false, menuAbout, false); setCurrentMenuItem(2);//
				 * ��¼��ǰѡ��λ�ã�������ת slidingMenu.toggle();// ҳ����ת
				 * 
				 * slidingMenu.setContent(R.layout.activity_my);
				 * StaticVarUtil.fileName = "jsonCacheMyPublish.txt";
				 * 
				 * progress = (ProgressBar) findViewById(R.id.proRefresh); //
				 * �˵���ť Button menu = (Button) findViewById(R.id.butMenu);
				 * menu.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View v) { slidingMenu.toggle();
				 * } });
				 * 
				 * // �������ο� if (StaticVarUtil.student == null) {
				 * ViewUtil.toastShort("���ȵ�¼��Ȼ���ٲ鿴...", MainActivity.this);
				 * return; }
				 * 
				 * menuMy("������ѯ", StaticVarUtil.fileName, new DownLoadThread(
				 * StaticVarUtil.student.getAccount(),
				 * HttpUtil.MY_PUBLISH_MESSAGE));
				 */
			}
		});

		menuMyPaiming.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "����Գ������Ŭ�������У��������ע...",
						2000).show();
				/*
				 * setMenuItemState(menuBang, false, menuMyBukao, false,
				 * menuMyPaiming, true, menuMyCollect, false, menuSetting,
				 * false, menuAbout, false); setCurrentMenuItem(3);//
				 * ��¼��ǰѡ��λ�ã�������ת slidingMenu.toggle();// ҳ����ת
				 * 
				 * slidingMenu.setContent(R.layout.activity_my);
				 * StaticVarUtil.fileName = "jsonCacheMyComment.txt";
				 * 
				 * progress = (ProgressBar) findViewById(R.id.proRefresh); //
				 * �˵���ť Button menu = (Button) findViewById(R.id.butMenu);
				 * menu.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View v) { slidingMenu.toggle();
				 * } });
				 * 
				 * // �������ο� if (StaticVarUtil.student == null) {
				 * ViewUtil.toastShort("���ȵ�¼��Ȼ���ٲ鿴...", MainActivity.this);
				 * return; }
				 * 
				 * menuMy("�ҵ�����", StaticVarUtil.fileName, new DownLoadThread(
				 * StaticVarUtil.student.getAccount(),
				 * HttpUtil.MY_COMMENT_MESSAGE));
				 */
			}
		});

		menuMyCollect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "����Գ������Ŭ�������У��������ע...",
						2000).show();
				/*
				 * setMenuItemState(menuBang, false, menuMyBukao, false,
				 * menuMyPaiming, false, menuMyCollect, true, menuSetting,
				 * false, menuAbout, false); setCurrentMenuItem(4);//
				 * ��¼��ǰѡ��λ�ã�������ת slidingMenu.toggle();// ҳ����ת
				 * 
				 * slidingMenu.setContent(R.layout.activity_my);
				 * StaticVarUtil.fileName = "jsonCacheMyCollect.txt";
				 * 
				 * progress = (ProgressBar) findViewById(R.id.proRefresh); //
				 * �˵���ť Button menu = (Button) findViewById(R.id.butMenu);
				 * menu.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View v) { slidingMenu.toggle();
				 * } });
				 * 
				 * // �������ο� if (StaticVarUtil.student == null) {
				 * ViewUtil.toastShort("���ȵ�¼��Ȼ���ٲ鿴...", MainActivity.this);
				 * return; }
				 * 
				 * menuMy("���ղص�", StaticVarUtil.fileName, new DownLoadThread(
				 * StaticVarUtil.student.getAccount(),
				 * HttpUtil.MY_COLLECT_MESSAGE));
				 */
			}
		});

		menuSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setMenuItemState(menuBang, false, menuMyBukao, false,
						menuMyPaiming, false, menuMyCollect, false,
						menuSetting, true, menuAbout, false);
				setCurrentMenuItem(5);// ��¼��ǰѡ��λ�ã�������ת
				slidingMenu.toggle();// ҳ����ת

				slidingMenu.setContent(R.layout.activity_setting);
				menuSetting();
			}
		});

		menuAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setMenuItemState(menuBang, false, menuMyBukao, false,
						menuMyPaiming, false, menuMyCollect, false,
						menuSetting, false, menuAbout, true);
				setCurrentMenuItem(6);// ��¼��ǰѡ��λ�ã�������ת
				slidingMenu.toggle();// ҳ����ת

				slidingMenu.setContent(R.layout.activity_about);

				// �˵���ť
				Button menu = (Button) findViewById(R.id.butMenu);
				menu.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						slidingMenu.toggle();
					}
				});
			}
		});

		menuQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				quit();
			}
		});

	}

	/**
	 * �޸ĸ�����Ϣ��ֻ���޸��ǳƣ����룬ͷ��
	 */
	protected void menuSetting() {
		// �˵���ť
		Button menu = (Button) findViewById(R.id.butMenu);
		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				slidingMenu.toggle();
			}
		});

		if (StaticVarUtil.student == null) {
			ViewUtil.toastShort("�Բ��𣬲鿴ѡ�����ȵ�¼", MainActivity.this);
			return;
		}

		EditText etAccount = (EditText) findViewById(R.id.etAccount);
		etAccount.setText("0"+StaticVarUtil.student.getAccount() + "");
		etAccount.setEnabled(false);// ������

		final EditText etPassword1 = (EditText) findViewById(R.id.etPassword1);
		final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);
        final EditText cofPassword2 = (EditText)findViewById(R.id.corfimPassword2);//ȷ������
		// �޸�
		Button butAlter = (Button) findViewById(R.id.butAlter);
		butAlter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Ҫ���ݵĲ���
				String password = null;
				// �ؼ�ֵ
				String password1 = etPassword1.getText().toString();
				String password2 = etPassword2.getText().toString().trim();
                String password3 = cofPassword2.getText().toString().trim();
				if (password1.equals("") && password2.equals("")
						&& bitmap == null && password3.equals("")) {
					ViewUtil.toastShort("��û����Ϣ��Ҫ�޸�", MainActivity.this);
					return;
				}

				// ����
				if (password1.equals("") && password2.equals("")) {// ������޸�
					password = StaticVarUtil.student.getPassword();
				} else {
					if (password1.equals(StaticVarUtil.student.getPassword())
							&& password2.equals("") == false && password2.equals(password3)) {
						// �����������ȷ�������벻Ϊ��,��ô����������
						password = password2;
					} else {
						ViewUtil.toastShort("�����벻��ȷ���������벻��Ϊ��,�������",
								MainActivity.this);
						return;
					}
				}
/*
				File file = null;
				// ͷ��
				if (bitmap != null) {
					file = new File(StaticVarUtil.PATH + "/headPhoto.JPEG");
				}*/
				String account = StaticVarUtil.student.getAccount() + "";
				// �޸�
				//alertStudent(account, password, file);
				ChangePwAsyntask changePwAsyntask = new ChangePwAsyntask();
				changePwAsyntask.execute();
				
			}
		});
	}

	/**
	 * �޸���Ϣ
	 * 
	 * @param account
	 *            �˺�
	 * @param password
	 *            ����
	 * @param file
	 *            ͷ���ļ�
	 *//*
	protected void alertStudent(final String account, final String password,
			final File file) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = StaticVarUtil.START;
				handlerAlter.sendMessage(msg);

				String url = "/fengyun06_alter_judge.jsp";
				HashMap<String, String> allParams = new HashMap<String, String>();
				allParams.put(HttpUtil.ACCOUNT, account);
				allParams.put(HttpUtil.PASSWORD, password);
				// �������֣���֪��Ϊɶ�ǵô����������������fileParam����Ϊnull,���ǿ��Բ������κ�ֵ
				HashMap<String, File> fileParam = new HashMap<String, File>();
				if (file != null) {
					fileParam.put(HttpUtil.HEAD_PHOTO, file);
				}
				HttpUtil http = new HttpUtil();
				try {
					if (http.submitFormAlter(url, allParams, fileParam).equals(
							HttpUtil.SUCCESS)) {
						msg = new Message();
						msg.what = StaticVarUtil.END_SUCCESS;
						handlerAlter.sendMessage(msg);
					} else {
						msg = new Message();
						msg.what = StaticVarUtil.END_FAIL;
						handlerAlter.sendMessage(msg);
					}
				} catch (Exception e) {
					msg = new Message();
					msg.what = StaticVarUtil.INTERNET_ERROR;
					handlerAlter.sendMessage(msg);
					e.printStackTrace();
				}
			};
		}.start();
	}

	*//**
	 * �޸���Ϣ��handler
	 *//*
	private Handler handlerAlter = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StaticVarUtil.START:
				dialog.show();
				break;
			case StaticVarUtil.END_FAIL:
				dialog.cancel();
				ViewUtil.toastLength("�޸�ʧ�ܣ����Ժ�����...", MainActivity.this);
				break;
			case StaticVarUtil.END_SUCCESS:
				dialog.cancel();
				ViewUtil.toastLength("�޸ĳɹ�", MainActivity.this);
				break;
			case StaticVarUtil.INTERNET_ERROR:
				dialog.cancel();
				ViewUtil.toastLength("�����쳣�����Ժ�����...", MainActivity.this);
				break;
			default:
				break;
			}
		};
	};*/

	/**
	 * ѡ��ͷ��
	 * 
	 * @return
	 */
	protected void chooseHeadPhoto() {
		String[] items = new String[] { "ѡ�񱾵�ͼƬ", "����" };
		new AlertDialog.Builder(this)
				.setTitle("����ͷ��")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intent, PIC);
							break;
						case 1:
							Intent intent2 = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							Uri imageUri = Uri.fromFile(new File(
									StaticVarUtil.PATH, "temp.JPEG"));
							// ָ����Ƭ����·����SD������image.jpgΪһ����ʱ�ļ���ÿ�����պ����ͼƬ���ᱻ�滻
							intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
							startActivityForResult(intent2, PHO);
							break;
						}
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * ȡ�ûش�������
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// �������벻��ȡ����ʱ��
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case PHO:
				File tempFile = new File(StaticVarUtil.PATH + "/temp.JPEG");
				startPhotoZoom(Uri.fromFile(tempFile));
				break;
			case PIC:
				// ��Ƭ��ԭʼ��Դ��ַ
				Uri originalUri = data.getData();
				startPhotoZoom(originalUri);
				break;
			case RESULT:
				if (data != null) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						bitmap = extras.getParcelable("data");
					}
					bitmap = BitmapUtil.resizeBitmapWidth(bitmap, 100);// ������Ϊ100���ص�ͼƬ
					BitmapUtil.saveBitmapToFile(bitmap, StaticVarUtil.PATH,
							"headPhoto.JPEG");
				}
				break;
			default:
				break;
			}
		} else {
			bitmap = null;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * �ü�ͼƬ����ʵ��
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// ���òü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ��ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ����
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT);
	}

	/**
	 * �ж��Ƿ�����ȷѡ���ѧ��-1����������0�������У��������ִ�����ͬ��ѧ
	 */
	private int judgeCollegeId() {
		int collegeId = -1;
		try {
			// ��������д�ѧ��ֱ�ӷ���0
			if (chooseCollege.getText().toString().equals("���д�ѧ")) {
				return 0;// ֱ�ӷ������д�ѧ
			}
			// �����ĳһ��ѧ������Id
			collegeId = Integer.parseInt(ToolClass.nameIdTreeMap
					.get(chooseCollege.getText().toString()));
		} catch (Exception e) {
			// ��������-1
			System.out.println("û�������ѧ");
			collegeId = -1;
		}
		return collegeId;
	}

	/**
	 * ѡ���У����У����ѡ����ʷ
	 */
	protected void chooseCollege() {
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.choose_school, null);

		// ��ʾ��ѧ��ʷadapterHistory.notifyDataSetChanged();
		ArrayList<CharSequence> allCollege = readHistory2();
		HistoryCollege.initData(allCollege);// ��ʼ������Դ
		ChooseHistorySchoolExpandAdapter adapterHistory = new ChooseHistorySchoolExpandAdapter(
				MainActivity.this);
		ExpandableListView expandHistory = (ExpandableListView) view
				.findViewById(R.id.expandHistory);
		expandHistory.setAdapter(adapterHistory);

		// �ҵ��ؼ�expandListView
		ExpandableListView expandListView = (ExpandableListView) view
				.findViewById(R.id.expandListView);
		expandListView.setAdapter(adapter);

		final Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setView(view).create();
		dialog.setCancelable(true);
		dialog.show();

		// ѡ���ѧ����
		expandListView.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				String schoolName = ToolClass.schoolsList.get(groupPosition)
						.get(childPosition).toString();
				dialog.cancel();
				chooseCollege.setText(schoolName);
				return true;
			}
		});

		// ѡ����ʷ����
		expandHistory.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				String schoolName = HistoryCollege.allHistory
						.get(groupPosition).get(childPosition).toString();
				dialog.cancel();
				chooseCollege.setText(schoolName);
				return true;
			}
		});
	}

	/**
	 * ����ʲôʱ����Լ���ͼƬ
	 */
	OnScrollListener mScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				listViewAdapter.lock();
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				listViewAdapter.unlock();
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				listViewAdapter.unlock();
				break;
			default:
				break;
			}
			listViewAdapter.notifyDataSetChanged();
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};

	/**
	 * ˢ��
	 * 
	 * @param messageSort
	 *            0�������ڹ�ע1�������չ�ע2�������ư�
	 */
	@SuppressLint("SimpleDateFormat")
	private void refresh(int collegeId, int messageKind, int messageSort) {
		// ����������ʷ������������ѧУ��ÿ�ζ����棩�����࣬����ʽ��ֻ���浱ǰ��Σ�Ϊ���ٴν���ʱ��ֱ����ʾ��
		saveHistory();
		// // ���Զ�ȡ��ʷ�ļ�
		// FileCache fileCache = new FileCache();
		// String dataResource = fileCache.readHistoryJsonData();
		// // ����adapter
		// listViewAdapter = new ListViewAdapter(dataResource,
		// MainActivity.this);
		// listView.setAdapter(listViewAdapter);
		// �������߳�
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 6);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		int page = 0;
		StaticVarUtil.fileName = "jsonCache.txt";// ���ñ����ļ�������
		/*
		 * StaticVarUtil.executorService.submit(new DownLoadThread(handler,
		 * collegeId, messageKind, messageSort, page, sdf.format(calendar
		 * .getTime())));
		 */
	}

	/**
	 * ���������ʷ
	 */
	private void saveHistory() {
		SharedPreferences share = getSharedPreferences("history", MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString("msgKind", chooseMsgKind.getText().toString());// �����´ε�¼��ʾ
		editor.putString("msgSort", chooseMsgSort.getText().toString());// �����´ε�¼��ʾ
		editor.putString("theLastCollege", chooseCollege.getText().toString());// �����´ε�¼��ʾ

		// ����ѡ���ѧ��ʾ
		HashSet<String> set = (HashSet<String>) share.getStringSet("college",
				new HashSet<String>());
		if (judgeCollegeId() > 0) {
			if (!set.contains(chooseCollege.getText().toString())) {// ����������ͼ���
				set.add(chooseCollege.getText().toString());
				System.out.print("������ʷ��" + chooseCollege.getText().toString());
			}
		}
		editor.putStringSet("college", set);// �����ȥ
		editor.commit();// ͬ������Ӳ������
	}

	/**
	 * ��ȡ��ʷ��¼������ʾ����ѡ����Ĵ�ѧ
	 */
	private ArrayList<CharSequence> readHistory2() {
		SharedPreferences share = getSharedPreferences("history", MODE_PRIVATE);
		// ��ʾ���һ�μ�¼,û����ʾĬ��
		HashSet<String> set = (HashSet<String>) share.getStringSet("college",
				null);
		ArrayList<CharSequence> allHistory = null;// Ҫ���ص�����
		if (set != null) {// ��Ϊ�յĻ����������ݵ���ʾ
			allHistory = new ArrayList<CharSequence>();
			for (String string : set) {
				allHistory.add(string);
			}
		}
		return allHistory;
	}

	/**
	 * ���ֻ���ť�ļ���
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// ����Ƿ��ذ�ť,�˳�
			quit();
			break;
		case KeyEvent.KEYCODE_MENU:// ����ǲ˵���ť
			slidingMenu.toggle();
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * �˳�ģ��
	 */
	private void quit() {
		Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage("��ȷ��Ҫ�˳���");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				StaticVarUtil.quit();
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		Dialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * ��¼���õ�ǰMenuItem��λ�ã�1��2��3��4��5�ֱ�����ɼ���ѯ��������ѯ���ҵ����������ղصģ�ѡ��
	 * 
	 * @param menuItem
	 *            �˵���ѡ��
	 */
	private void setCurrentMenuItem(int menuItem) {
		SharedPreferences preferences = getSharedPreferences("currentMenuItem",
				MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("item", menuItem);
		editor.commit();
	}

	/**
	 * ȡ�õ�ǰMenuItem��λ��
	 * 
	 * @return ��ǰ��menu�Ĳ˵��� 1��2��3��4��5�ֱ�����ɼ���ѯ��������ѯ���ҵ����������ղصģ�ѡ��,0����û�����
	 */
	private int getCurrentMeunItem() {
		SharedPreferences preferences = getSharedPreferences("currentMenuItem",
				MODE_PRIVATE);
		int flag = preferences.getInt("item", 0);
		return flag;
	}

	// �첽���ص�¼
	class GetScoreAsyntask extends AsyncTask<Object, String, String> {

		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String url = "";
			String canshu = Util.getURL(StaticVarUtil.QUERY_SCORE);
			String[] can = canshu.split("&");
			String url_str = can[0];
			String xm = can[1];
			name = xm.split("=")[1];
			String gnmkdm = can[2];
			try {
				url = HttpUtilMc.BASE_URL
						+ "xscjcx.aspx?session="
						+ StaticVarUtil.session
						+ "&url="
						+ url_str
						+ "&xm="
						+ URLEncoder.encode(
								URLEncoder.encode(xm.split("=")[1], "utf8"),
								"utf8") + "&" + gnmkdm;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("url" + url);
			// ��ѯ���ؽ��
			String result = HttpUtilMc.queryStringForPost(url);
			System.out.println("=========================  " + result);
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// progress.cancel();
			// ��ʾ�û���
			nickname.setText(name);
			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
					if (!result.equals("error")) {
						/**
						 * ���ַ��� д��xml�ļ���
						 */
						if (!result.equals("")) {
							score_json = result;
							listItem = new ArrayList<HashMap<String, Object>>();
							System.out.println("rrrr:" + result);
							JSONObject jsonObject = new JSONObject(result);
							JSONArray jsonArray = (JSONArray) jsonObject
									.get("liScoreModels");// ������array
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject o = (JSONObject) jsonArray.get(i);
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("xn", o.get("xn"));
								map.put("list_xueKeScore",
										o.get("list_xueKeScore"));
								listItem.add(map);
							}
							/*
							 * // ��ȡsdcard��Ŀ¼ File sdCardDir = Environment
							 * .getExternalStorageDirectory(); // Ȼ��Ϳ��Խ�������Ĳ���
							 * File saveFile = new File(sdCardDir,
							 * "xuptscore/score.xml"); if (!saveFile.exists()) {
							 * new File(sdCardDir, "xuptscore").mkdir();
							 * saveFile.createNewFile(); } FileOutputStream
							 * outStream = new FileOutputStream( saveFile);
							 * outStream.write(result.getBytes());
							 * outStream.close();
							 */
						}
						menu1();
					} else {
						Toast.makeText(getApplicationContext(), "��¼ʧ��", 1)
								.show();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							HttpUtilMc.CONNECT_EXCEPTION, 1).show();
					// progress.cancel();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.i("LoginActivity", e.toString());
			}

		}

	}
	
	// �첽���ص�¼
		class ChangePwAsyntask extends AsyncTask<Object, String, String> {

			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				String url = "";
				String canshu = Util.getURL(StaticVarUtil.CHANGE_PW);
				/*String[] can = canshu.split("&");
				String url_str = can[0];
				String gnmkdm = can[1];*/
				url = HttpUtilMc.BASE_URL
						+ "xscjcx.aspx?session="
						+ StaticVarUtil.session
						+ "&url="
						+ canshu;
				System.out.println("url" + url);
				// ��ѯ���ؽ��
				String result = HttpUtilMc.queryStringForPost(url);
				System.out.println("=========================  " + result);
				return result;

			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				// progress.cancel();
				// ��ʾ�û���
				nickname.setText(name);
				try {
					if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
						if (!result.equals("error")) {
							/**
							 * ���ַ��� д��xml�ļ���
							 */
							if (!result.equals("error")) {
								Toast.makeText(getApplicationContext(), "�޸ĳɹ�", 1000).show();
							}
							menu1();
						} else {
							Toast.makeText(getApplicationContext(), "�޸Ĳ��ɹ�", 1)
									.show();
						}

					} else {
						Toast.makeText(getApplicationContext(),
								HttpUtilMc.CONNECT_EXCEPTION, 1).show();
						// progress.cancel();
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					Log.i("LoginActivity", e.toString());
				}

			}

		}
}