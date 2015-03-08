package com.zhcs.parkingSpaceDao;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.zhcs.driverBean.BookSpaceInfo;
import com.zhcs.driverBean.DriverInfo;
import com.zhcs.regAndLogin.R;

public class QRCodeView extends SlidingFragmentActivity{
	private ImageView imageview = null;
	private Button save = null;
	private Bitmap image = null;
	private CanvasTransformer mTransformer;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//ȥ������
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_2dcodeview);
		if(BookSpaceInfo.getLogObjectId() == null){
			setContentView(R.layout.nosubscribe);
			setTitle("��ά��鿴");
			
			initAnimation();
			initSlidingMenu();
			// �����Ͻ�ͼ�����߼���һ�����ص�ͼ�� 
			getActionBar().setDisplayHomeAsUpEnabled(true);
			 // �����Ͻ�ͼ�����߼���һ�����ص�ͼ�� 
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}else{
		
			setTitle("��ά��鿴");
			
			initAnimation();
			initSlidingMenu();
			// �����Ͻ�ͼ�����߼���һ�����ص�ͼ�� 
			getActionBar().setDisplayHomeAsUpEnabled(true);
			 // �����Ͻ�ͼ�����߼���һ�����ص�ͼ�� 
			getActionBar().setDisplayHomeAsUpEnabled(true);
			
			//���ʵ������
			imageview=(ImageView)findViewById(R.id.imgView);
			save=(Button)findViewById(R.id.save);
			
			//��ά�������
	//		String content = "����id��"+DriverInfo.getId()+"\n";
	//		content += "��ַ��"+ BookSpaceInfo.getSpace().getAddress()+"\n";
	//		content += "����ʱ��:"+ BookSpaceInfo.getStart()+":00--"+BookSpaceInfo.getEnd()+":00\n";
	//		content += "���ĳ�λ��:"+ BookSpaceInfo.getSpace().getNum();
			String content = BookSpaceInfo.getLogObjectId();
			
			try {
				try {
					image = createBitmap(Create2DCode(content));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			imageview.setImageBitmap(image);
			
			save.setOnClickListener(new View.OnClickListener() {
						
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
					 image.compress(Bitmap.CompressFormat.PNG, 100, baos);

			    	try {
						Write(baos.toByteArray());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public void Write(byte []b) throws IOException
	{
		File cacheFile =null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdCardDir = Environment.getExternalStorageDirectory();
			
			long time=Calendar.getInstance().getTimeInMillis();
			String fileName =time+".png";
			File dir = new File(sdCardDir.getCanonicalPath()
					+"/driver/");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			cacheFile = new File(dir, fileName);
		
		}  
		Toast toast = Toast.makeText(getApplicationContext(),
				"ͼƬ���������ڴ濨��driver�ļ����£���鿴��", Toast.LENGTH_LONG);
	      	   toast.setGravity(Gravity.CENTER, 0, 0);
	      	   LinearLayout toastView = (LinearLayout) toast.getView();
	      	   ImageView imageCodeProject = new ImageView(getApplicationContext());
	      	   imageCodeProject.setImageResource(R.drawable.fun);
	      	   toastView.addView(imageCodeProject, 0);
	      	   toast.show();
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
			
				bos.write(b,0,b.length);
				bos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Bitmap Create2DCode(String str) throws WriterException, UnsupportedEncodingException {
		//���ɶ�ά����,����ʱָ����С,��Ҫ������ͼƬ�Ժ��ٽ�������,������ģ������ʶ��ʧ��
    	
		BitMatrix matrix = new MultiFormatWriter().encode(new String(str.getBytes("GBK"),"ISO-8859-1"),BarcodeFormat.QR_CODE, 300, 300);
		
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		//��ά����תΪһά��������,Ҳ����һֱ��������
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if(matrix.get(x, y)){
					pixels[y * width + x] = 0xff000000;
				}
				
			}
		}	
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		//ͨ��������������bitmap,����ο�api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	private Bitmap createBitmap( Bitmap src){
	    if( src == null ){
	    	return null;
	    }
	    Paint paint=new Paint();
	    paint.setColor(Color.WHITE);
	    paint.setAntiAlias(true);
	   
	    int w = 300;
	    int h = 300;
	    Bitmap newb = Bitmap.createBitmap( w, h, Config.ARGB_8888 );
	    Canvas cv = new Canvas( newb );
	
	    cv.drawColor(Color.WHITE);
	 
	    cv.drawBitmap(src, 0, 0, null );
	    cv.save( Canvas.ALL_SAVE_FLAG );
	    cv.restore();//�洢
	    return newb;

    }
	
	/**
	 * ��ʼ�������˵�
	 */
	private void initSlidingMenu(){
		// ������������ͼ
		//getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SampleListFragment()).commit();
				
		// ���û����˵���ͼ
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new SampleListFragment()).commit();

		// ���û����˵�������ֵ
		SlidingMenu sm = getSlidingMenu();		
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.layout.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setBehindWidth(400);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setBehindScrollScale(0.0f);
		sm.setBehindCanvasTransformer(mTransformer);
		
		setSlidingActionBarEnabled(true);
	}

	/**
	 * ��ʼ������Ч��
	 */
	private void initAnimation(){
		mTransformer = new CanvasTransformer(){
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				canvas.scale(percentOpen, 1, 0, 0);				
			}
			
		};
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
