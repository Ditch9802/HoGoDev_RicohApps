package net.keyring.bookend.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.keyring.bookend.Logput;
import net.keyring.bookend.R;
import net.keyring.bookend.action.MainListAction;
import net.keyring.bookend.asynctask.ImageGetTask;
import net.keyring.bookend.bean.BookBeans;
import net.keyring.bookend.constant.ConstList;
import net.keyring.bookend.db.ContentsDao;
import net.keyring.bookend.util.DateUtil;
import net.keyring.bookend.util.StringUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DocumentListAdapter extends SimpleAdapter{

	/** Inflater */
	private LayoutInflater mInflater;
	/** 書庫情報リスト(サムネイル・タイトル・著者・rowID) */
	private ArrayList<Map<String, Object>> mItems;
	/** Context */
	private Context mCon;
	/** ContentsDaoクラス */
	private ContentsDao mDao;
	/** MainListActionクラス */
	private MainListAction mAction;
	
//	private List<Integer> listSelectedPosition;
	private int positionSelected = -1;
	
	private static final String TAG = DocumentListAdapter.class.getSimpleName();
	
	private static class ViewHolder {
		LinearLayout llItemBook;
		TextView date;
		TextView expiry;
		TextView numberOfPrintsRemain;
		TextView from;
		
		TextView title;		
//		TextView status;
		CheckBox cbSelect;
		ImageView thumbnail;
		ProgressBar waitBar;
	}

	public DocumentListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// Viewを作成するLayoutInflaterを取得
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCon = context;
//		listSelectedPosition = new ArrayList<Integer>();
	}
	
//	public List<Integer> getListSelectedPosition(){
//		return listSelectedPosition;
//	}
	
	public void setPositionSelected(int position){
		positionSelected = position;
		notifyDataSetChanged();
	}
	
	public int getPositionSelected(){
		return positionSelected;
	}
	
	/** データ内容を保持 */
	public void setListData(ArrayList<Map<String, Object>> data) {
		mItems = data;
	}
	
	/**
	 * 必ず呼ばれる
	 * 
	 * @param position
	 * @param convertView
	 *            レイアウト
	 * @param parent
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final ViewHolder holder;
		
		// 書庫情報を取得
		Map<String, Object> settingData = mItems.get(position);
		if (settingData == null) return v;

		if (v == null) {
			// Web書庫レイアウト
			v = mInflater.inflate(R.layout.layout_item_document, null);
			holder = new ViewHolder();
			holder.llItemBook = (LinearLayout)v.findViewById(R.id.llItemBook);
			holder.thumbnail = (ImageView) v.findViewById(R.id.imgProfileBook);
			holder.waitBar = (ProgressBar) v.findViewById(R.id.WaitBar);
			holder.title = (TextView) v.findViewById(R.id.tvTitleBook);
			holder.from = (TextView) v.findViewById(R.id.tvFromUser);
//			holder.status = (TextView)v.findViewById(R.id.status);
			holder.date = (TextView)v.findViewById(R.id.tvDate);
			holder.expiry = (TextView)v.findViewById(R.id.tvExpiryBook);
			holder.numberOfPrintsRemain = (TextView)v.findViewById(R.id.tvNPR);
			holder.cbSelect = (CheckBox)v.findViewById(R.id.cbSelectBook);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		long rowID = (Long)settingData.get(ConstList.ID);
		if(mDao == null) mDao = new ContentsDao(mCon);
		BookBeans book = null;
		String status = null;
		try{
			book = mDao.load(rowID);
			holder.title.setText("Title: " + viewStrSet(book.getTitle()));
			holder.from.setText(book.getAuthor());	
			holder.date.setText(formatDate(book.getDownload_date()));
			holder.expiry.setText("Expiry: " + formatDate(book.getExpiry_date()));
			holder.numberOfPrintsRemain.setText("Number of prints remain: " + viewStrSet(Integer.toString(book.getPageCount())));
//			status = getStatus(book);
		}catch(NullPointerException e){
			Logput.w("Null : RowID = " + rowID);
		}
//		holder.status.setText(status);
//		holder.status.setTag(mCon.getString(R.string.status_main_tag) + rowID);

		// 画像を隠し、プログレスバーを表示
		holder.waitBar.setVisibility(View.VISIBLE);
		holder.thumbnail.setVisibility(View.GONE);
		// 仮の画像設定
		holder.thumbnail.setImageDrawable(mCon.getResources().getDrawable(android.R.drawable.ic_menu_report_image));
		// 画像読込
		try {
			// サムネイルのダウンロードURL
			String thumbURL = settingData.get(ConstList.THUMB_URL).toString();
			holder.thumbnail.setTag(thumbURL);
			// AsyncTaskは１回しか実行できない為、毎回インスタンスを生成
			ImageGetTask task = new ImageGetTask(holder.thumbnail, holder.waitBar, mCon.getResources().getDrawable(
							android.R.drawable.ic_dialog_alert));
			// 画像URLをセット
			task.execute(thumbURL);
		} catch (Exception e) {
			// 取得失敗
			// <!>アイコン
			holder.thumbnail.setImageDrawable(mCon.getResources().getDrawable(
					android.R.drawable.ic_dialog_alert));
			// プログレスバー非表示＆画像表示
			holder.waitBar.setVisibility(View.GONE);
			holder.thumbnail.setVisibility(View.VISIBLE);			
		}
		
		if(positionSelected == position){
//			holder.cbSelect.setChecked(true);
			holder.llItemBook.setBackgroundColor(R.color.item_checked);
		}else{
//			holder.cbSelect.setChecked(false);
			holder.llItemBook.setBackgroundResource(R.color.white);
		}
		holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {			
			
			@SuppressLint("ResourceAsColor") @Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				View row = (View) buttonView.getParent();
				if(isChecked && positionSelected != position){
					Log.i(TAG, "Postion: " + position);
//					listSelectedPosition.add(position);
					setPositionSelected(position);
//					holder.cbSelect.setChecked(true);
//					holder.llItemBook.setBackgroundColor(R.color.item_checked);
					
				}
				else{
//					if(listSelectedPosition.contains(position)){
//						listSelectedPosition.remove((Integer)position);
//					}
					setPositionSelected(-1);
//					holder.cbSelect.setChecked(false);
//					holder.llItemBook.setBackgroundResource(R.color.white);
				}
			}
		});
		return v;
	}

	/**
	 * 表示する項目がNULLの場合は[ ― ]を表示
	 * 
	 * @param チェック文字列
	 * @return 表示文字列
	 */
	private String viewStrSet(String value) {
		String result = " - ";
		if (!StringUtil.isEmpty(value)) {
			result = value;
		}
		return result;
	}
	
	/**
	 * ＤＬステータスをDBから取得
	 * @param rowID
	 * @return
	 */
	private String getStatus(BookBeans book){
		if(mAction == null) mAction = new MainListAction();
		return mAction.getStatus(mCon, book);
	}

	private String formatDate(String datetime){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date d = null;
		try {
			d = format.parse(datetime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat serverFormat = new SimpleDateFormat("MM/dd/yyyy");
		return serverFormat.format(d);
		
	}
}
