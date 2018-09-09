package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

//import org.opencv.samples.facedetect.BTClient;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Point;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Camera;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import org.opencv.samples.facedetect.ColorBlobDetector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FdActivity extends Activity implements CvCameraViewListener2 {

	private static final String TAG = "OCVSample::Activity";
	private static final Scalar FACE_RECT_COLOR = new Scalar(255, 255, 0, 255);
	public static final int JAVA_DETECTOR = 0;
	public static final int NATIVE_DETECTOR = 1;

	private MenuItem mItemFace50;
	private MenuItem mItemFace40;
	private MenuItem mItemFace30;
	private MenuItem mItemFace20;
	private MenuItem mItemType;

	private Mat mRgba;
	private Mat mGray;
	public File mCascadeFile;
	private CascadeClassifier mJavaDetector;
	private DetectionBasedTracker mNativeDetector;

	private int mDetectorType = JAVA_DETECTOR;
	private String[] mDetectorName;

	private float mRelativeFaceSize = 0.2f;
	private int mAbsoluteFaceSize = 0;

	public int faceWidth = 0;
	public int faceHeight = 0;
	public int faceWidthTemp = 0;
	public int faceHeightTemp = 0;
	public Bitmap GetBit = null;
	private boolean mIsColorSelected = false;
	private Scalar mBlobColorRgba;
	private Scalar mBlobColorHsv;
	private ColorBlobDetector mDetector;
	private Mat mSpectrum;
	private Size SPECTRUM_SIZE;
	private Scalar CONTOUR_COLOR;

	private CameraBridgeViewBase mOpenCvCameraView;
	// public Camera

	private final static int REQUEST_CONNECT_DEVICE = 1; // 宏定义查询设备句柄

	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // SPP服务UUID号

	private InputStream is; // 输入流，用来接收蓝牙数据
	private EditText edit0; // 发送数据输入句柄

	private String smsg = ""; // 显示用数据缓存
	private String fmsg = ""; // 保存用数据缓存

	BluetoothDevice _device = null; // 蓝牙设备
	public BluetoothSocket _socket = null; // 蓝牙通信socket
	boolean _discoveryFinished = false;
	boolean bRun = true;
	boolean bThread = false;

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter(); // 获取本地蓝牙适配器，即蓝牙设备

	// /自定义内容
	public char[] Thing = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
			'0', '0' };// toso
	// 王老吉为'w'，加多宝为'j'，雪碧为's'，可口可乐为'k'，百事可乐为'b'，零度可乐为'l'，美年达为'm'，醒目为'x'
	// A区蓝色方块为'1'，红色为'2'，绿色为'3'，黄色为'4'
	// b区蓝色方块为'5'，红色为'6'，绿色为'7'，黄色为'8'
	public boolean isConnected = false;
	public int PickCount = 0;
	// private String BoxInput = ""; //显示要取的东西
	public int Tempx = 0;// 通过分析得到的物体的大致准确位置
	public int Tempy = 0;
	public int Count = 0;
	public int Pixel = 0;
	public int ScreenWidth = 0;
	public int ScreenHeight = 0;
	public Bitmap bmp;
	private Bitmap bmpresult;
	public long PixelColor = 0;
	public int rr = 0;
	public int gg = 0;
	public int bb = 0;
	private char PPP;
	private char QQQ;
	public String s;
	public boolean isg = false;
	// /
	public char Shibie = '0';// 识别的物体
	public String templateFile = "storage/sdcard0/input/jiaduobao.jpg";
	public String inFile = "storage/sdcard0/picturein.jpg";
	public int ccount = 0;
	// String templateFile="storage/sdcard0/jiaduobao.jpg";
	public String outFile = "storage/sdcard0/pictureout" + ccount + ".jpg";
	private VideoCapture camera; // 相机对象

	public double wlj = 0;
	public double jdb = 0;
	public double mnd = 0;
	public double xm = 0;
	// /
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				// Load native library after(!) OpenCV initialization
				System.loadLibrary("detection_based_tracker");
				try {
					// load cascade file from application resources

					File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
					InputStream is = getResources().openRawResource(
							R.raw.lbpcascade_frontalface);
					mCascadeFile = new File(cascadeDir, "jiaduobao.xml");
					switch (Thing[PickCount]) {
					// 王老吉为'w'，加多宝为'j'，雪碧为's'，可口可乐为'k'，百事可乐为'b'，零度可乐为'l'，美年达为'm'，醒目为'x'
					// D区蓝色方块为'1'，红色为'2'，绿色为'3'，黄色为'4'
					// C区蓝色方块为'5'，红色为'6'，绿色为'7'，黄色为'8'
					case 'w': {
						mCascadeFile = new File(cascadeDir, "wanglaoji.xml");
						break;
					}
					case 'j': {
						mCascadeFile = new File(cascadeDir, "jiaduobao.xml");
						break;
					}
					case 's': {
						mCascadeFile = new File(cascadeDir, "xuebi.xml");
						break;
					}
					case 'k': {
						mCascadeFile = new File(cascadeDir, "kekoukele.xml");
						break;
					}
					case 'b': {
						mCascadeFile = new File(cascadeDir, "baishi.xml");
						break;
					}
					case 'l': {
						mCascadeFile = new File(cascadeDir, "lingdukele.xml");
						break;
					}
					case 'm': {
						mCascadeFile = new File(cascadeDir, "meinianda.xml");
						break;
					}
					case 'x': {
						mCascadeFile = new File(cascadeDir, "xingmu.xml");
						break;
					}
					case '1': {
						mCascadeFile = new File(cascadeDir, "lan.xml");
						break;
					}
					case '2': {
						mCascadeFile = new File(cascadeDir, "hong.xml");
						break;
					}
					case '3': {
						mCascadeFile = new File(cascadeDir, "lv.xml");
						break;
					}
					case '4': {
						mCascadeFile = new File(cascadeDir, "huang.xml");
						break;
					}
					case '5': {
						mCascadeFile = new File(cascadeDir, "lan.xml");
						break;
					}
					case '6': {
						mCascadeFile = new File(cascadeDir, "hong.xml");
						break;
					}
					case '7': {
						mCascadeFile = new File(cascadeDir, "lv.xml");
						break;
					}
					case '8': {
						mCascadeFile = new File(cascadeDir, "huang.xml");
						break;
					}
					default: {
						mCascadeFile = new File(cascadeDir, "wanglaoji.xml");
					}
					}
					// mCascadeFile = new File(cascadeDir,
					// "lbpcascade_frontalface.xml");
					FileOutputStream os = new FileOutputStream(mCascadeFile);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();

					mJavaDetector = new CascadeClassifier(
							mCascadeFile.getAbsolutePath());
					if (mJavaDetector.empty()) {
						Log.e(TAG, "Failed to load cascade classifier");
						mJavaDetector = null;
					} else
						Log.i(TAG, "Loaded cascade classifier from "
								+ mCascadeFile.getAbsolutePath());

					mNativeDetector = new DetectionBasedTracker(
							mCascadeFile.getAbsolutePath(), 0);

					// cascadeDir.delete();

				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
				}

				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public FdActivity() {
		mDetectorName = new String[2];
		mDetectorName[JAVA_DETECTOR] = "Java";
		mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.face_detect_surface_view);
		edit0 = (EditText) findViewById(R.id.Edit0); // 得到输入框句柄
		TextView showTex = (TextView) findViewById(R.id.textView1);
		showTex.setText("Set Face");
		handler.postDelayed(runnable, 200);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);

		// 如果打开本地蓝牙设备不成功，提示信息，结束程序
		if (_bluetooth == null) {
			Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}

		// 设置设备可以被搜索
		new Thread() {
			public void run() {
				if (_bluetooth.isEnabled() == false) {
					_bluetooth.enable();
				}
			}
		}.start();
	}

	// 接收活动结果，响应startActivityForResult()
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE: // 连接结果，由DeviceListActivity设置返回
			// 响应返回结果
			if (resultCode == Activity.RESULT_OK) { // 连接成功，由DeviceListActivity设置返回
				// MAC地址，由DeviceListActivity设置返回
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// 得到蓝牙设备句柄
				_device = _bluetooth.getRemoteDevice(address);

				// 用服务号得到socket
				try {
					_socket = _device.createRfcommSocketToServiceRecord(UUID
							.fromString(MY_UUID));
				} catch (IOException e) {
					Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
				}
				// 连接socket
				Button btn = (Button) findViewById(R.id.Button03);
				try {
					_socket.connect();
					Toast.makeText(this, "连接" + _device.getName() + "成功！",
							Toast.LENGTH_SHORT).show();
					btn.setText("断开");
				} catch (IOException e) {
					try {
						Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT)
								.show();
						_socket.close();
						_socket = null;
					} catch (IOException ee) {
						Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT)
								.show();
					}
					return;
				}
				// 打开接收线程
				try {
					is = _socket.getInputStream(); // 得到蓝牙数据输入流
				} catch (IOException e) {
					Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (bThread == false) {
					ReadThread.start();
					bThread = true;
				} else {
					bRun = true;
				}
			}
			break;
		default:
			break;
		}
	}

	// 接收数据线程
	Thread ReadThread = new Thread() {

		public void run() {
			int num = 0;
			byte[] buffer = new byte[1024];
			byte[] buffer_new = new byte[1024];
			int i = 0;
			int n = 0;
			bRun = true;
			// 接收线程
			while (true) {
				try {
					while (is.available() == 0) {
						while (bRun == false) {
						}
					}
					while (true) {
						num = is.read(buffer); // 读入数据
						n = 0;

						// String s0 = new String(buffer,0,num);
						for (i = 0; i < num; i++) {
							if ((buffer[i] == 0x0d) && (buffer[i + 1] == 0x0a)) {
								buffer_new[n] = 0x0a;
								i++;
							} else {
								buffer_new[n] = buffer[i];
							}
							n++;
						}
						String s = new String(buffer_new, 0, n);
						smsg += s; // 写入接收缓存
						char[] receive = s.toCharArray();
						PPP = receive[0];

						if (is.available() == 0)
							break; // 短时间没有数据才跳出进行显示
					}
					// 发送显示消息，进行显示刷新
					readhandler.sendMessage(readhandler.obtainMessage());
				} catch (IOException e) {
				}
			}
		}
	};

	Handler readhandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// smsg = msg.toString();

			TextView showTex = (TextView) findViewById(R.id.textView1);
			showTex.setText((char) PPP + ' ' + "tl:" + faceWidth + "br:"
					+ faceHeight + "Color:" + rr + " " + gg + " " + bb + " "
					+ smsg + " " + fmsg);
			// showTex.setText("Receive: "+smsg+"Send: "+fmsg);
			if (PPP == '-') {// 小车开始行驶，回复在A,B,C,D 实际是
				isConnected = true;
				// 王老吉为'w'，加多宝为'j'，雪碧为's'，可口可乐为'k'，百事可乐为'b'，零度可乐为'l'，美年达为'm'醒目为'x'
				// D区蓝色方块为'1'，红色为'2'，绿色为'3'，黄色为'4'
				// C区蓝色方块为'5'，红色为'6'，绿色为'7'，黄色为'8'
				switch (Thing[PickCount]) {
				// 王老吉为'w'，加多宝为'j'，雪碧为's'，可口可乐为'k'，百事可乐为'b'，零度可乐为'l'，美年达为'm'，醒目为'x'
				case 'w': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('A');
						fmsg += 'A';
						templateFile = "storage/sdcard0/input/wanglaoji.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 'j': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('A');
						fmsg += 'A';
						templateFile = "storage/sdcard0/input/jiaduobao.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 's': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('B');
						fmsg += 'B';
						templateFile = "storage/sdcard0/input/xuebi.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 'k': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('B');
						templateFile = "storage/sdcard0/input/kekoukele.jpg";
						fmsg += 'B';
						break;
					} catch (IOException e) {
					}
				}
				case 'b': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('B');
						fmsg += 'B';
						templateFile = "storage/sdcard0/input/baishikele.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 'l': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('B');
						fmsg += 'B';
						templateFile = "storage/sdcard0/input/lingdukele.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 'm': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('A');
						fmsg += 'A';
						templateFile = "storage/sdcard0/input/meinianda.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 'x': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('A');
						fmsg += 'A';
						templateFile = "storage/sdcard0/input/xingmu.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '1': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('D');
						fmsg += 'D';
						templateFile = "storage/sdcard0/input/lan.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '2': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('D');
						fmsg += 'D';
						templateFile = "storage/sdcard0/input/hong.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '3': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('D');
						fmsg += 'D';
						templateFile = "storage/sdcard0/input/lv.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '4': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('D');
						fmsg += 'D';
						templateFile = "storage/sdcard0/input/huang.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '5': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('C');
						fmsg += 'C';
						templateFile = "storage/sdcard0/input/lan.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '6': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('C');
						fmsg += 'C';
						templateFile = "storage/sdcard0/input/hong.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '7': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('C');
						fmsg += 'C';
						templateFile = "storage/sdcard0/input/lv.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '8': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('C');
						fmsg += 'C';
						templateFile = "storage/sdcard0/input/huang.jpg";
						break;
					} catch (IOException e) {
					}
				}
				}
			}

			if (PPP == 'q') {// 单片机取货的时候，发出收到确认
				// isConnected = false;
				isg = false;
			}
			if (PPP == 'j') {// 拿货架中的东西的时候，有东西发Y，没有发I
				 try {OutputStream os =
				 _socket.getOutputStream();os.write('Y');fmsg+='Y';} catch
				 (IOException e) {}
				 if (faceWidth <= 384 && faceHeight <= 200) {
				 try {
				 OutputStream os = _socket.getOutputStream();
				 os.write('J');
				 fmsg += 'J';
				 } catch (IOException e) {
				 }
				 } else if (faceWidth > 384 && faceHeight <= 200) {
				 try {
				 OutputStream os = _socket.getOutputStream();
				 os.write('O');
				 fmsg += 'O';
				 } catch (IOException e) {
				 }
				 } else if (faceWidth <= 384 && faceHeight > 200) {
				 try {
				 OutputStream os = _socket.getOutputStream();
				 os.write('P');
				 fmsg += 'P';
				 } catch (IOException e) {
				 }
				 } else if (faceWidth > 384 && faceHeight > 200) {
				 try {
				 OutputStream os = _socket.getOutputStream();
				 os.write('Q');
				 fmsg += 'Q';
				 } catch (IOException e) {
				 }
				 }
				
				
				
//				if (Thing[PickCount] == 'w' || Thing[PickCount] == 'j'
//						|| Thing[PickCount] == 'm' || Thing[PickCount] == 'x') {
//					CompareA();
//					if (Thing[PickCount] == Shibie) {
//						try {
//							OutputStream os = _socket.getOutputStream();
//							os.write('Y');
//							fmsg += 'Y';
//						} catch (IOException e) {
//						}
//					} else {
//						try {
//							OutputStream os = _socket.getOutputStream();
//							os.write('I');
//							fmsg += 'I';
//						} catch (IOException e) {
//						}
//					}
//				}
//
//				if (Thing[PickCount] == 's' || Thing[PickCount] == 'b'
//						|| Thing[PickCount] == 'l' || Thing[PickCount] == 'k') {
//					CompareB();
//					if (Thing[PickCount] == Shibie) {
//						try {
//							OutputStream os = _socket.getOutputStream();
//							os.write('Y');
//							fmsg += 'Y';
//						} catch (IOException e) {
//						}
//					} else {
//						try {
//							OutputStream os = _socket.getOutputStream();
//							os.write('I');
//							fmsg += 'I';
//						} catch (IOException e) {
//						}
//					}
//				}
//
//				if (Thing[PickCount] == '5' || Thing[PickCount] == '6'
//						|| Thing[PickCount] == '7' || Thing[PickCount] == '8') {
//					CompareCPhoto();
//					if (Thing[PickCount] == Shibie) {
//						try {
//							OutputStream os = _socket.getOutputStream();
//							os.write('Y');
//							fmsg += 'Y';
//						} catch (IOException e) {
//						}
//					} else {
//						try {
//							OutputStream os = _socket.getOutputStream();
//							os.write('I');
//							fmsg += 'I';
//						} catch (IOException e) {
//						}
//					}
//				}
//				if (Thing[PickCount] == '1' || Thing[PickCount] == '2'
//						|| Thing[PickCount] == '3' || Thing[PickCount] == '4') {
//					CompareDPhoto();
//					if (Thing[PickCount] == Shibie) {
//						try {
//							OutputStream os = _socket.getOutputStream();
//							os.write('Y');
//							fmsg += 'Y';
//						} catch (IOException e) {
//						}
//					} else {
//						try {
//							OutputStream os = _socket.getOutputStream();
//							os.write('I');
//							fmsg += 'I';
//						} catch (IOException e) {
//						}
//					}
//				}
//2014.5.14改
			}
			if (PPP == 'w') {// 单片机补货之前,发送：蓝色木块E,红色木块F,绿色G,黄色H,可口可乐T,零度可乐U,百事可乐V,雪碧W,王老吉X,加多宝Y,醒目Z,美年达I
				faceWidth = 0;
				switch (Thing[PickCount]) {
				// 王老吉为'w'，加多宝为'j'，雪碧为's'，可口可乐为'k'，百事可乐为'b'，零度可乐为'l'，美年达为'm'，醒目为'x'
				// A区蓝色方块为'1'，红色为'2'，绿色为'3'，黄色为'4'
				// b区蓝色方块为'5'，红色为'6'，绿色为'7'，黄色为'8'
				case 'w': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('W');
						fmsg += 'W';
						break;
					} catch (IOException e) {
					}
				}
				case 'j': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('X');
						fmsg += 'X';
						break;
					} catch (IOException e) {
					}
				}
				case 's': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('V');
						fmsg += 'V';
						break;
					} catch (IOException e) {
					}
				}
				case 'k': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('T');
						fmsg += 'T';
						break;
					} catch (IOException e) {
					}
				}
				case 'b': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('V');
						fmsg += 'V';
						break;
					} catch (IOException e) {
					}
				}
				case 'l': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('U');
						fmsg += 'U';
						break;
					} catch (IOException e) {
					}
				}
				case 'm': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('D');
						fmsg += 'D';
						break;
					} catch (IOException e) {
					}
				}
				case 'x': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('D');
						fmsg += 'D';
						break;
					} catch (IOException e) {
					}
				}
				case '1': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('E');
						fmsg += 'E';
						break;
					} catch (IOException e) {
					}
				}
				case '2': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('F');
						fmsg += 'F';
						break;
					} catch (IOException e) {
					}
				}
				case '3': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('G');
						fmsg += 'G';
						break;
					} catch (IOException e) {
					}
				}
				case '4': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('H');
						fmsg += 'H';
						break;
					} catch (IOException e) {
					}
				}
				case '5': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('E');
						fmsg += 'E';
						break;
					} catch (IOException e) {
					}
				}
				case '6': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('F');
						fmsg += 'F';
						break;
					} catch (IOException e) {
					}
				}
				case '7': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('G');
						fmsg += 'G';
						break;
					} catch (IOException e) {
					}
				}
				case '8': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('H');
						fmsg += 'H';
						break;
					} catch (IOException e) {
					}
				}
				}
			}

			if (PPP == 'l') {// 小车开始补货，开启摄像头，回复'K''L''M''R''S'没有发'N'
				if (faceWidth <= 250) {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('K');
					} catch (IOException e) {
					}
				} else if (faceWidth > 250 && faceWidth <= 500) {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('L');
					} catch (IOException e) {
					}
				} else if (faceWidth > 500 && faceWidth <= 600) {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('M');
					} catch (IOException e) {
					}
				} else if (faceWidth > 600 && faceWidth <= 8500) {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('R');
					} catch (IOException e) {
					}
				} else if (faceWidth > 850) {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('S');
					} catch (IOException e) {
					}
				} else {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('N');
					} catch (IOException e) {
					}
				}
			}
			if (PPP == 'g' && isg == false) {// 开始取下一个东西 实际是f

				PickCount++;
				switch (Thing[PickCount]) {
				// 王老吉为'w'，加多宝为'j'，雪碧为's'，可口可乐为'k'，百事可乐为'b'，零度可乐为'l'，美年达为'm'，醒目为'x'
				case 'w': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('A');
						fmsg += 'A';
						templateFile = "storage/sdcard0/input/wanglaoji.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 'j': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('A');
						fmsg += 'A';
						templateFile = "storage/sdcard0/input/jiaduobao.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 's': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('B');
						fmsg += 'B';
						templateFile = "storage/sdcard0/input/xuebi.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 'k': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('B');
						templateFile = "storage/sdcard0/input/kekoukele.jpg";
						fmsg += 'B';
						break;
					} catch (IOException e) {
					}
				}
				case 'b': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('B');
						fmsg += 'B';
						templateFile = "storage/sdcard0/input/baishikele.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 'l': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('B');
						fmsg += 'B';
						templateFile = "storage/sdcard0/input/lingdukele.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 'm': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('A');
						fmsg += 'A';
						templateFile = "storage/sdcard0/input/meinianda.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case 'x': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('A');
						fmsg += 'A';
						templateFile = "storage/sdcard0/input/xingmu.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '1': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('D');
						fmsg += 'D';
						templateFile = "storage/sdcard0/input/lan.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '2': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('D');
						fmsg += 'D';
						templateFile = "storage/sdcard0/input/hong.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '3': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('D');
						fmsg += 'D';
						templateFile = "storage/sdcard0/input/lv.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '4': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('D');
						fmsg += 'D';
						templateFile = "storage/sdcard0/input/huang.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '5': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('C');
						fmsg += 'C';
						templateFile = "storage/sdcard0/input/lan.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '6': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('C');
						fmsg += 'C';
						templateFile = "storage/sdcard0/input/hong.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '7': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('C');
						fmsg += 'C';
						templateFile = "storage/sdcard0/input/lv.jpg";
						break;
					} catch (IOException e) {
					}
				}
				case '8': {
					try {
						OutputStream os = _socket.getOutputStream();
						os.write('C');
						fmsg += 'C';
						templateFile = "storage/sdcard0/input/huang.jpg";
						break;
					} catch (IOException e) {
					}
				}
				}
				try {
					// load cascade file from application resources
					InputStream is = getResources().openRawResource(
							R.raw.lbpcascade_frontalface);
					File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
					switch (Thing[PickCount]) {
					// 王老吉为'w'，加多宝为'j'，雪碧为's'，可口可乐为'k'，百事可乐为'b'，零度可乐为'l'，美年达为'm'，醒目为'x'
					// A区蓝色方块为'1'，红色为'2'，绿色为'3'，黄色为'4'
					// b区蓝色方块为'5'，红色为'6'，绿色为'7'，黄色为'8'
					case 'w': {
						mCascadeFile = new File(cascadeDir, "wanglaoji.xml");
						break;
					}
					case 'j': {
						mCascadeFile = new File(cascadeDir, "jiaduobao.xml");
						break;
					}
					case 's': {
						mCascadeFile = new File(cascadeDir, "xuebi.xml");
						break;
					}
					case 'k': {
						mCascadeFile = new File(cascadeDir, "kekoukele.xml");
						break;
					}
					case 'b': {
						mCascadeFile = new File(cascadeDir, "baishi.xml");
						break;
					}
					case 'l': {
						mCascadeFile = new File(cascadeDir, "lingdukele.xml");
						break;
					}
					case 'm': {
						mCascadeFile = new File(cascadeDir, "meinianda.xml");
						break;
					}
					case 'x': {
						mCascadeFile = new File(cascadeDir, "xingmu.xml");
						break;
					}
					case '1': {
						mCascadeFile = new File(cascadeDir, "lan.xml");
						break;
					}
					case '2': {
						mCascadeFile = new File(cascadeDir, "hong.xml");
						break;
					}
					case '3': {
						mCascadeFile = new File(cascadeDir, "lv.xml");
						break;
					}
					case '4': {
						mCascadeFile = new File(cascadeDir, "huang.xml");
						break;
					}
					case '5': {
						mCascadeFile = new File(cascadeDir, "lan.xml");
						break;
					}
					case '6': {
						mCascadeFile = new File(cascadeDir, "hong.xml");
						break;
					}
					case '7': {
						mCascadeFile = new File(cascadeDir, "lv.xml");
						break;
					}
					case '8': {
						mCascadeFile = new File(cascadeDir, "huang.xml");
						break;
					}
					default: {
						mCascadeFile = new File(cascadeDir,
								"lbpcascade_frontalface.xml");
					}
					}
					FileOutputStream os = new FileOutputStream(mCascadeFile);
					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();

					mJavaDetector = new CascadeClassifier(
							mCascadeFile.getAbsolutePath());
					if (mJavaDetector.empty()) {
						Log.e(TAG, "Failed to load cascade classifier");
						mJavaDetector = null;
					} else
						Log.i(TAG, "Loaded cascade classifier from "
								+ mCascadeFile.getAbsolutePath());

					mNativeDetector = new DetectionBasedTracker(
							mCascadeFile.getAbsolutePath(), 0);

					// cascadeDir.delete();

				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
				}

				mOpenCvCameraView.enableView();
				isg = true;// 为了防止重复多次收到信号

			} else {
			}
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		mOpenCvCameraView.disableView();
		if (_socket != null) // 关闭连接socket
			try {
				_socket.close();
			} catch (IOException e) {
			}
		// _bluetooth.disable(); //关闭蓝牙服务
	}

	public void onCameraViewStarted(int width, int height) {
		mGray = new Mat();
		mRgba = new Mat(height, width, CvType.CV_8UC3);
		// mRgba = new Mat(height, width, CvType.CV_8UC4);
		// mDetector = new ColorBlobDetector();
		// mSpectrum = new Mat();
		// mBlobColorRgba = new Scalar(255);
		// mBlobColorHsv = new Scalar(255);
		// SPECTRUM_SIZE = new Size(200, 64);
		// CONTOUR_COLOR = new Scalar(255,0,0,255);
	}

	public void onCameraViewStopped() {
		mGray.release();
		mRgba.release();
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			TextView showTexc = (TextView) findViewById(R.id.textView2);
			 showTexc.setText(fmsg+"tl:" + faceWidth + "br:" + faceHeight +
			 "Color:"
			 + rr + " " + gg + " " + bb + Thing[0] + Thing[1] + Thing[2]
			 + Thing[3] + Thing[4] + Thing[5] + Thing[6] + Thing[7]
			 + Thing[8] + Thing[9] + Thing[10] + Thing[11]);
//			showTexc.setText(jdb + " " + wlj + " " + mnd + " " + xm + " ");
			handler.postDelayed(this, 200);
		}
	};

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();

		mGray = inputFrame.gray();

		// if (mIsColorSelected) {
		// mDetector.process(mRgba);
		// List<MatOfPoint> contours = mDetector.getContours();
		// Log.e(TAG, "Contours count: " + contours.size());
		// Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);
		//
		// Mat colorLabel = mRgba.submat(4, 68, 4, 68);
		// colorLabel.setTo(mBlobColorRgba);
		//
		// Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70,
		// 70 + mSpectrum.cols());
		// mSpectrum.copyTo(spectrumLabel);
		// }
		//
		// if (mAbsoluteFaceSize == 0) {
		// int height = mGray.rows();
		// if (Math.round(height * mRelativeFaceSize) > 0) {
		// mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
		// }
		// mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
		// }
		//
		// MatOfRect faces = new MatOfRect();
		//
		// if (mDetectorType == JAVA_DETECTOR) {
		// if (mJavaDetector != null)
		// mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2,
		// new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
		// new Size());
		// } else if (mDetectorType == NATIVE_DETECTOR) {
		// if (mNativeDetector != null)
		// mNativeDetector.detect(mGray, faces);
		// } else {
		// Log.e(TAG, "Detection method is not selected!");
		// }
		//
		// Rect[] facesArray = faces.toArray();
		// for (int i = 0; i < facesArray.length; i++) {
		// Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(),
		// FACE_RECT_COLOR, 3);
		// faceWidthTemp = (int) facesArray[i].x + ((int) facesArray[0].width)
		// / 2;
		// faceHeightTemp = (int) facesArray[i].y
		// + ((int) facesArray[0].height) / 2;
		// Mat mat_new = (mRgba);
		// Bitmap bmp_new = Bitmap.createBitmap(mat_new.cols(),
		// mat_new.rows(), Bitmap.Config.ARGB_8888);
		// Utils.matToBitmap(mat_new, bmp_new, false);
		// PixelColor = bmp_new.getPixel(faceWidth, faceHeight);
		// rr = (int) ((PixelColor & 0xff0000) >> 16);
		// gg = (int) ((PixelColor & 0xff00) >> 8);
		// bb = (int) (PixelColor & 0xff);
		// // A区蓝色方块为'1'，红色为'2'，绿色为'3'，黄色为'4'
		// // b区蓝色方块为'5'，红色为'6'，绿色为'7'，黄色为'8'
		// // if(Thing[PickCount]=='1'||Thing[PickCount]=='5'){
		// // if(bb>=200&&rr<80&&gg<80){faceWidth = faceWidthTemp;faceHeight =
		// // faceHeightTemp;break;}
		// // }
		// // else if(Thing[PickCount]=='2'||Thing[PickCount]=='6'){
		// // if(bb<80&&rr>=200&&gg<80){faceWidth = faceWidthTemp;faceHeight =
		// // faceHeightTemp;break;}
		// // }
		// // else if(Thing[PickCount]=='3'||Thing[PickCount]=='7'){
		// // if(bb<80&&rr<80&&gg>=200){faceWidth = faceWidthTemp;faceHeight =
		// // faceHeightTemp;break;}
		// // }
		// // else if(Thing[PickCount]=='4'||Thing[PickCount]=='8'){
		// // if(bb<80&&rr>=200&&gg>=200){faceWidth = faceWidthTemp;faceHeight
		// // = faceHeightTemp;break;}
		// // }
		//
		// if (Count == 0) {
		// Tempx = faceWidthTemp;
		// Tempy = faceHeightTemp;
		// Count++;
		// }
		//
		// }
		// if (Count >= 1 && Count <= 1000000) {
		// try {
		// Thread.sleep(100);// 括号里面的5000代表5000毫秒，也就是5秒，可以该成你需要的时间
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// if (Math.abs(faceWidthTemp - Tempx) > 3
		// || Math.abs(faceHeightTemp - Tempy) > 3) {
		// Count = 0;
		// } else {
		// Tempx = faceWidthTemp;
		// Tempy = faceHeightTemp;
		// Count++;
		// }
		// } else if (Tempx > 0 && Tempy > 0) {
		// // faceWidth = Tempx;
		// // faceHeight = Tempy;
		// // onTouch(mOpenCvCameraView);
		// Count = 0;
		// }

		return mRgba;
	}

	public boolean onTouch(View v) {
		int cols = mRgba.cols();
		int rows = mRgba.rows();

		// int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
		// int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

		// int x = (int)event.getX() - xOffset;
		// int y = (int)event.getY() - yOffset;
		int x = faceWidth + 1;
		int y = faceHeight + 1;

		Rect touchedRect = new Rect();

		touchedRect.x = (x > 4) ? x - 4 : 0;
		touchedRect.y = (y > 4) ? y - 4 : 0;

		touchedRect.width = (x + 4 < cols) ? x + 4 - touchedRect.x : cols
				- touchedRect.x;
		touchedRect.height = (y + 4 < rows) ? y + 4 - touchedRect.y : rows
				- touchedRect.y;

		Mat touchedRegionRgba = mRgba.submat(touchedRect);

		Mat touchedRegionHsv = new Mat();
		Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv,
				Imgproc.COLOR_RGB2HSV_FULL);

		// Calculate average color of touched region
		mBlobColorHsv = Core.sumElems(touchedRegionHsv);
		int pointCount = touchedRect.width * touchedRect.height;
		for (int i = 0; i < mBlobColorHsv.val.length; i++)
			mBlobColorHsv.val[i] /= pointCount;

		mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

		Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", "
				+ mBlobColorRgba.val[1] + ", " + mBlobColorRgba.val[2] + ", "
				+ mBlobColorRgba.val[3] + ")");

		mDetector.setHsvColor(mBlobColorHsv);

		Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

		mIsColorSelected = true;

		touchedRegionRgba.release();
		touchedRegionHsv.release();

		return false; // don't need subsequent touch events
	}

	// /
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		mItemFace50 = menu.add("Face size 50%");
		mItemFace40 = menu.add("Face size 40%");
		mItemFace30 = menu.add("Face size 30%");
		mItemFace20 = menu.add("Face size 20%");
		mItemType = menu.add(mDetectorName[mDetectorType]);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
		if (item == mItemFace50)
			setMinFaceSize(0.5f);
		else if (item == mItemFace40)
			setMinFaceSize(0.4f);
		else if (item == mItemFace30)
			setMinFaceSize(0.3f);
		else if (item == mItemFace20)
			setMinFaceSize(0.2f);
		else if (item == mItemType) {
			int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
			item.setTitle(mDetectorName[tmpDetectorType]);
			setDetectorType(tmpDetectorType);
		}
		return true;
	}

	private void setMinFaceSize(float faceSize) {
		mRelativeFaceSize = faceSize;
		mAbsoluteFaceSize = 0;
	}

	private void setDetectorType(int type) {
		if (mDetectorType != type) {
			mDetectorType = type;

			if (type == NATIVE_DETECTOR) {
				Log.i(TAG, "Detection Based Tracker enabled");
				mNativeDetector.start();
			} else {
				Log.i(TAG, "Cascade detector enabled");
				mNativeDetector.stop();
			}
		}
	}

	// 连接按键响应函数
	public void onConnectButtonClicked(View v) {

		if (_bluetooth.isEnabled() == false) { // 如果蓝牙服务不可用则提示
			Toast.makeText(this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
			return;
		}

		// 如未连接设备则打开DeviceListActivity进行设备搜索
		Button btn = (Button) findViewById(R.id.Button03);
		if (_socket == null) {
			Intent serverIntent = new Intent(this, DeviceListActivity.class); // 跳转程序设置
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义
		} else {
			// 关闭连接socket
			try {

				is.close();
				_socket.close();
				_socket = null;
				bRun = false;
				btn.setText("连接");
			} catch (IOException e) {
			}
		}
		return;
	}

	public void onSendButtonClicked(View v) {
		int i = 0;
		String getList = edit0.getText().toString();
		char[] bos = getList.toCharArray();
		for (i = 0; (i < 12 && i < getList.length()); i++) {
			Thing[i] = bos[i];
		}

		try {
			// load cascade file from application resources
			InputStream is = getResources().openRawResource(R.raw.hong);
			File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
			switch (Thing[PickCount]) {
			// 王老吉为'w'，加多宝为'j'，雪碧为's'，可口可乐为'k'，百事可乐为'b'，零度可乐为'l'，美年达为'm'，醒目为'x'
			// A区蓝色方块为'1'，红色为'2'，绿色为'3'，黄色为'4'
			// b区蓝色方块为'5'，红色为'6'，绿色为'7'，黄色为'8'
			case 'w': {
				mCascadeFile = new File(cascadeDir, "wanglaoji.xml");
				break;
			}
			case 'j': {
				mCascadeFile = new File(cascadeDir, "jiaduobao.xml");
				break;
			}
			case 's': {
				mCascadeFile = new File(cascadeDir, "xuebi.xml");
				break;
			}
			case 'k': {
				mCascadeFile = new File(cascadeDir, "kekoukele.xml");
				break;
			}
			case 'b': {
				mCascadeFile = new File(cascadeDir, "baishi.xml");
				break;
			}
			case 'l': {
				mCascadeFile = new File(cascadeDir, "lingdukele.xml");
				break;
			}
			case 'm': {
				mCascadeFile = new File(cascadeDir, "meinianda.xml");
				break;
			}
			case 'x': {
				mCascadeFile = new File(cascadeDir, "xingmu.xml");
				break;
			}
			case '1': {
				mCascadeFile = new File(cascadeDir, "lan.xml");
				break;
			}
			case '2': {
				mCascadeFile = new File(cascadeDir, "hong.xml");
				break;
			}
			case '3': {
				mCascadeFile = new File(cascadeDir, "lv.xml");
				break;
			}
			case '4': {
				mCascadeFile = new File(cascadeDir, "huang.xml");
				break;
			}
			case '5': {
				mCascadeFile = new File(cascadeDir, "lan.xml");
				break;
			}
			case '6': {
				mCascadeFile = new File(cascadeDir, "hong.xml");
				break;
			}
			case '7': {
				mCascadeFile = new File(cascadeDir, "lv.xml");
				break;
			}
			case '8': {
				mCascadeFile = new File(cascadeDir, "huang.xml");
				break;
			}
			default: {
				mCascadeFile = new File(cascadeDir,
						"lbpcascade_frontalface.xml");
			}
			}
			FileOutputStream os = new FileOutputStream(mCascadeFile);
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();

			mJavaDetector = new CascadeClassifier(
					mCascadeFile.getAbsolutePath());
			if (mJavaDetector.empty()) {
				Log.e(TAG, "Failed to load cascade classifier");
				mJavaDetector = null;
			} else
				Log.i(TAG,
						"Loaded cascade classifier from "
								+ mCascadeFile.getAbsolutePath());

			mNativeDetector = new DetectionBasedTracker(
					mCascadeFile.getAbsolutePath(), 0);

			// cascadeDir.delete();

		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
		}

		mOpenCvCameraView.enableView();

	}
//TODO
	public void onSingButtonClicked(View v) {
		 try {
		 OutputStream os = _socket.getOutputStream();
		 os.write('m');
		 fmsg += 'm';
		 } catch (IOException e) {
		 }
//		Thing[PickCount] = 'j';
//		CompareB();
	}

	public void ComparePhoto() {
		Highgui.imwrite(inFile, mRgba);
		Mat img = Highgui.imread(inFile);
		Mat templ = Highgui.imread(templateFile);
		// Highgui.imwrite(tempFile, templ);
		int match_method = Imgproc.TM_CCOEFF_NORMED;

		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(result);

		Point matchLoc;

		if (match_method == Imgproc.TM_SQDIFF
				|| match_method == Imgproc.TM_SQDIFF_NORMED) {
			matchLoc = mmr.minLoc;
		} else {
			matchLoc = mmr.maxLoc;
		}

		// / Show me what you got
		Core.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
				matchLoc.y + templ.rows()), new Scalar(0, 255, 0));
		faceWidth = (int) (matchLoc.x + 0.5 * templ.cols());
		faceHeight = (int) (matchLoc.y + 0.5 * templ.rows());
		// String s1=""+resultpoint[0];
		// Save the visualized detection.
		Highgui.imwrite(outFile, img);

	}

	public void CompareCPhoto() {
		double lanMatch, lvMatch, hongMatch, huangMatch, tempMatch;

		Highgui.imwrite(inFile, mRgba);
		Mat img = Highgui.imread(inFile);
		List<Mat> lRgb = new ArrayList<Mat>(3);
		Core.split(img, lRgb);
		Mat mR = lRgb.get(0);
		Mat mG = lRgb.get(1);
		Mat mB = lRgb.get(2);

		List<Mat> listMat = Arrays.asList(mB, mG, mR);
		Core.merge(listMat, img);
		int match_method = Imgproc.TM_CCOEFF_NORMED;
		Mat templ = Highgui.imread("storage/sdcard0/input/lan.jpg");

		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(result);
		MinMaxLocResult csv = mmr;
		lanMatch = mmr.maxVal;// 最大的东西~
		tempMatch = lanMatch;

		templ = Highgui.imread("storage/sdcard0/input/lv.jpg");
		result_cols = img.cols() - templ.cols() + 1;
		result_rows = img.rows() - templ.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		lvMatch = mmr.maxVal;// 最大的东西~
		if (tempMatch < lvMatch) {
			tempMatch = lvMatch;
			csv = mmr;
		}

		templ = Highgui.imread("storage/sdcard0/input/hong.jpg");
		result_cols = img.cols() - templ.cols() + 1;
		result_rows = img.rows() - templ.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		hongMatch = mmr.maxVal;// 最大的东西~
		if (tempMatch < hongMatch) {
			tempMatch = hongMatch;
			csv = mmr;
		}

		templ = Highgui.imread("storage/sdcard0/input/huang.jpg");
		result_cols = img.cols() - templ.cols() + 1;
		result_rows = img.rows() - templ.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		huangMatch = mmr.maxVal;// 最大的东西~
		if (tempMatch < huangMatch) {
			tempMatch = huangMatch;
			csv = mmr;
		}

		Point matchLoc;
		if (match_method == Imgproc.TM_SQDIFF
				|| match_method == Imgproc.TM_SQDIFF_NORMED) {
			matchLoc = csv.minLoc;

		} else {
			matchLoc = csv.maxLoc;
		}

		// / Show me what you got
		Core.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
				matchLoc.y + templ.rows()), new Scalar(0, 255, 0));
		faceWidth = (int) (matchLoc.x + 0.5 * templ.cols());
		faceHeight = (int) (matchLoc.y + 0.5 * templ.rows());
		// String s1=""+resultpoint[0];
		// Save the visualized detection.

		if (tempMatch == lanMatch) {
			Shibie = '5';
		}
		if (tempMatch == hongMatch) {
			Shibie = '6';
		}
		if (tempMatch == lvMatch) {
			Shibie = '7';
		}
		if (tempMatch == huangMatch) {
			Shibie = '8';
		}

		Highgui.imwrite(outFile, img);

	}

	public void CompareDPhoto() {
		double lanMatch, lvMatch, hongMatch, huangMatch, tempMatch;

		Highgui.imwrite(inFile, mRgba);
		Mat img = Highgui.imread(inFile);

		List<Mat> lRgb = new ArrayList<Mat>(3);
		Core.split(img, lRgb);
		Mat mR = lRgb.get(0);
		Mat mG = lRgb.get(1);
		Mat mB = lRgb.get(2);

		List<Mat> listMat = Arrays.asList(mB, mG, mR);
		Core.merge(listMat, img);

		int match_method = Imgproc.TM_CCOEFF_NORMED;
		Mat templ = Highgui.imread("storage/sdcard0/input/lan.jpg");

		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(result);
		MinMaxLocResult csv = mmr;
		lanMatch = mmr.maxVal;// 最大的东西~
		tempMatch = lanMatch;

		templ = Highgui.imread("storage/sdcard0/input/lv.jpg");
		result_cols = img.cols() - templ.cols() + 1;
		result_rows = img.rows() - templ.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		lvMatch = mmr.maxVal;// 最大的东西~
		if (tempMatch < lvMatch) {
			tempMatch = lvMatch;
			csv = mmr;
		}

		templ = Highgui.imread("storage/sdcard0/input/hong.jpg");
		result_cols = img.cols() - templ.cols() + 1;
		result_rows = img.rows() - templ.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		hongMatch = mmr.maxVal;// 最大的东西~
		if (tempMatch < hongMatch) {
			tempMatch = hongMatch;
			csv = mmr;
		}

		templ = Highgui.imread("storage/sdcard0/input/huang.jpg");
		result_cols = img.cols() - templ.cols() + 1;
		result_rows = img.rows() - templ.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		huangMatch = mmr.maxVal;// 最大的东西~
		if (tempMatch < huangMatch) {
			tempMatch = huangMatch;
			csv = mmr;
		}

		Point matchLoc;
		if (match_method == Imgproc.TM_SQDIFF
				|| match_method == Imgproc.TM_SQDIFF_NORMED) {
			matchLoc = csv.minLoc;

		} else {
			matchLoc = csv.maxLoc;
		}

		// / Show me what you got
		Core.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
				matchLoc.y + templ.rows()), new Scalar(0, 255, 0));
		faceWidth = (int) (matchLoc.x + 0.5 * templ.cols());
		faceHeight = (int) (matchLoc.y + 0.5 * templ.rows());
		// String s1=""+resultpoint[0];
		// Save the visualized detection.

		if (tempMatch == lanMatch) {
			Shibie = '1';
		}
		if (tempMatch == hongMatch) {
			Shibie = '2';
		}
		if (tempMatch == lvMatch) {
			Shibie = '3';
		}
		if (tempMatch == huangMatch) {
			Shibie = '4';
		}

		Highgui.imwrite(outFile, img);

	}

	public void CompareBPhoto() {
		double bsklMatch, kkklMatch, xbMatch, ldklMatch, tempMatch;

		Highgui.imwrite(inFile, mRgba);
		Mat img = Highgui.imread(inFile);
		List<Mat> lRgb = new ArrayList<Mat>(3);
		Core.split(img, lRgb);
		Mat mR = lRgb.get(0);
		Mat mG = lRgb.get(1);
		Mat mB = lRgb.get(2);

		List<Mat> listMat = Arrays.asList(mB, mG, mR);
		Core.merge(listMat, img);
		int match_method = Imgproc.TM_CCOEFF_NORMED;
		Mat templ = Highgui.imread("storage/sdcard0/input/baishikele.jpg");

		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(result);
		MinMaxLocResult csv = mmr;
		bsklMatch = mmr.maxVal;// 最大的东西~
		tempMatch = bsklMatch;
		jdb = bsklMatch;

		templ = Highgui.imread("storage/sdcard0/input/kekoukele.jpg");
		result_cols = img.cols() - templ.cols() + 1;
		result_rows = img.rows() - templ.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		kkklMatch = mmr.maxVal;// 最大的东西~
		if (tempMatch < kkklMatch) {
			tempMatch = kkklMatch;
			csv = mmr;
		}
		wlj = kkklMatch;

		templ = Highgui.imread("storage/sdcard0/input/xuebi.jpg");
		result_cols = img.cols() - templ.cols() + 1;
		result_rows = img.rows() - templ.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		xbMatch = mmr.maxVal;// 最大的东西~
		if (tempMatch < xbMatch) {
			tempMatch = xbMatch;
			csv = mmr;
		}
		mnd = xbMatch;

		templ = Highgui.imread("storage/sdcard0/input/lingdukele.jpg");
		result_cols = img.cols() - templ.cols() + 1;
		result_rows = img.rows() - templ.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		ldklMatch = mmr.maxVal;// 最大的东西~
		if (tempMatch < ldklMatch) {
			tempMatch = ldklMatch;
			csv = mmr;
		}
		xm = ldklMatch;

		Point matchLoc;
		if (match_method == Imgproc.TM_SQDIFF
				|| match_method == Imgproc.TM_SQDIFF_NORMED) {
			matchLoc = csv.minLoc;

		} else {
			matchLoc = csv.maxLoc;
		}

		// / Show me what you got
		Core.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
				matchLoc.y + templ.rows()), new Scalar(0, 255, 0));
		faceWidth = (int) (matchLoc.x + 0.5 * templ.cols());
		faceHeight = (int) (matchLoc.y + 0.5 * templ.rows());
		// String s1=""+resultpoint[0];
		// Save the visualized detection.

		if (tempMatch == kkklMatch) {
			Shibie = 'k';
			fmsg += 'k';
		}
		if (tempMatch == bsklMatch) {
			Shibie = 'b';
			fmsg += 'b';
		}
		if (tempMatch == ldklMatch) {
			Shibie = 'l';
			fmsg += 'l';
		}
		if (tempMatch == xbMatch) {
			Shibie = 's';
			fmsg += 's';
		}

		Highgui.imwrite(outFile, img);

	}

	public void CompareAPhoto() {
		// showTexc.setText(jdb+" "+wlj+" "+mnd+" "+xm+" ");
		double jdbMatch, wljMatch, xmMatch, mndMatch, tempMatch;

		Highgui.imwrite(inFile, mRgba);
		Mat img = Highgui.imread(inFile);
		List<Mat> lRgb = new ArrayList<Mat>(3);
		Core.split(img, lRgb);
		Mat mR = lRgb.get(0);
		Mat mG = lRgb.get(1);
		Mat mB = lRgb.get(2);

		List<Mat> listMat = Arrays.asList(mB, mG, mR);
		Core.merge(listMat, img);

		// Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2HSV, 3);// 后加
		int match_method = Imgproc.TM_CCOEFF_NORMED;
		Mat templA = Highgui.imread("storage/sdcard0/input/jiaduobao.jpg");
		// Imgproc.cvtColor(templA, templA, Imgproc.COLOR_RGB2HSV, 3);

		int result_cols = img.cols() - templA.cols() + 1;
		int result_rows = img.rows() - templA.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templA, result, match_method);
		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(result);
		MinMaxLocResult csv = mmr;
		jdbMatch = mmr.maxVal;// 最大的东西~
		tempMatch = jdbMatch;
		jdb = jdbMatch;

		Mat templB = Highgui.imread("storage/sdcard0/input/wanglaoji.jpg");
		// Imgproc.cvtColor(templB, templB, Imgproc.COLOR_RGB2HSV, 3);
		result_cols = img.cols() - templB.cols() + 1;
		result_rows = img.rows() - templB.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templB, result, match_method);
		// Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new
		// Mat());
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		wljMatch = mmr.maxVal;// 最大的东西~
		wlj = wljMatch;
		if (tempMatch < wljMatch) {
			tempMatch = wljMatch;
			csv = mmr;
		}

		Mat templC = Highgui.imread("storage/sdcard0/input/meinianda.jpg");
		// Imgproc.cvtColor(templC, templC, Imgproc.COLOR_RGB2HSV, 3);
		result_cols = img.cols() - templC.cols() + 1;
		result_rows = img.rows() - templC.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templC, result, match_method);
		// Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new
		// Mat());
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		mndMatch = mmr.maxVal;// 最大的东西~
		mnd = mndMatch;
		if (tempMatch < mndMatch) {
			tempMatch = mndMatch;
			csv = mmr;
		}

		Mat templD = Highgui.imread("storage/sdcard0/input/xingmu.jpg");
		// Imgproc.cvtColor(templD, templD, Imgproc.COLOR_RGB2HSV, 3);
		result_cols = img.cols() - templD.cols() + 1;
		result_rows = img.rows() - templD.rows() + 1;
		result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templD, result, match_method);
		// Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new
		// Mat());
		// / Localizing the best match with minMaxLoc
		mmr = Core.minMaxLoc(result);
		xmMatch = mmr.maxVal;// 最大的东西~
		xm = xmMatch;
		if (tempMatch < xmMatch) {
			tempMatch = xmMatch;
			csv = mmr;
		}

		Point matchLoc;
		if (match_method == Imgproc.TM_SQDIFF
				|| match_method == Imgproc.TM_SQDIFF_NORMED) {
			matchLoc = csv.minLoc;

		} else {
			matchLoc = csv.maxLoc;
		}

		// // / Show me what you got
		// Core.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
		// matchLoc.y + templ.rows()), new Scalar(0, 255, 0));
		// faceWidth = (int) (matchLoc.x + 0.5 * templ.cols());
		// faceHeight = (int) (matchLoc.y + 0.5 * templ.rows());
		// // String s1=""+resultpoint[0];
		// // Save the visualized detection.

		if (tempMatch == jdbMatch) {
			Shibie = 'j';
			fmsg += 'j';
		}
		if (tempMatch == wljMatch) {
			Shibie = 'w';
			fmsg += 'w';
		}
		if (tempMatch == xmMatch) {
			Shibie = 'x';
			fmsg += 'x';
		}
		if (tempMatch == mndMatch) {
			Shibie = 'm';
			fmsg += 'm';
		}
		// ccount++;
		Highgui.imwrite(outFile, img);
	}

	// /
	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
		Mat pointMatRgba = new Mat();
		Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
		Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL,
				4);

		return new Scalar(pointMatRgba.get(0, 0));
	}

	public double getpipeidu(String lujing) {// 得到匹配度信息

		double pipeidu;
		Mat img = Highgui.imread(inFile);
		List<Mat> lRgb = new ArrayList<Mat>(3);
		Core.split(img, lRgb);
		Mat mR = lRgb.get(0);
		Mat mG = lRgb.get(1);
		Mat mB = lRgb.get(2);
		List<Mat> listMat = Arrays.asList(mB, mG, mR);
		Core.merge(listMat, img);
		int match_method = Imgproc.TM_CCOEFF_NORMED;
		Mat templ = Highgui.imread(lujing);

		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(result);
		pipeidu = mmr.maxVal;// 最大的东西~
		return pipeidu;
	}

	public double CompareXuebiPhotos() {// 比较雪碧的最佳匹配度

		double[] Xuebi = new double[6];
		Xuebi[0] = getpipeidu("storage/sdcard0/input/Xuebi/1.jpg");
		Xuebi[1] = getpipeidu("storage/sdcard0/input/Xuebi/2.jpg");
		Xuebi[2] = getpipeidu("storage/sdcard0/input/Xuebi/3.jpg");
		Xuebi[3] = getpipeidu("storage/sdcard0/input/Xuebi/4.jpg");
		Xuebi[4] = getpipeidu("storage/sdcard0/input/Xuebi/5.jpg");
		Xuebi[5] = getpipeidu("storage/sdcard0/input/Xuebi/6.jpg");
		double max = Xuebi[0];
		for (int i = 0; i < Xuebi.length; i++) {
			System.out.print(Xuebi[i] + " ");
			if (Xuebi[i] > max)
				max = Xuebi[i];
		}
		return max;
	}

	public double CompareKekoukelePhotos() {// 比较可口可乐的最佳匹配度
		double[] Kekoukele = new double[6];
		Kekoukele[0] = getpipeidu("storage/sdcard0/input/Kekoukele/1.jpg");
		Kekoukele[1] = getpipeidu("storage/sdcard0/input/Kekoukele/2.jpg");
		Kekoukele[2] = getpipeidu("storage/sdcard0/input/Kekoukele/3.jpg");
		Kekoukele[3] = getpipeidu("storage/sdcard0/input/Kekoukele/4.jpg");
		Kekoukele[4] = getpipeidu("storage/sdcard0/input/Kekoukele/5.jpg");
		Kekoukele[5] = getpipeidu("storage/sdcard0/input/Kekoukele/6.jpg");
		double max = Kekoukele[0];
		for (int i = 0; i < Kekoukele.length; i++) {
			System.out.print(Kekoukele[i] + " ");
			if (Kekoukele[i] > max)
				max = Kekoukele[i];
		}
		return max;
	}

	public double CompareBaishikelePhotos() {// 比较百事可乐的最佳匹配度

		double[] Baishikele = new double[6];
		Baishikele[0] = getpipeidu("storage/sdcard0/input/Baishikele/1.jpg");
		Baishikele[1] = getpipeidu("storage/sdcard0/input/Baishikele/2.jpg");
		Baishikele[2] = getpipeidu("storage/sdcard0/input/Baishikele/3.jpg");
		Baishikele[3] = getpipeidu("storage/sdcard0/input/Baishikele/4.jpg");
		Baishikele[4] = getpipeidu("storage/sdcard0/input/Baishikele/5.jpg");
		double max = Baishikele[0];
		for (int i = 0; i < Baishikele.length; i++) {
			System.out.print(Baishikele[i] + " ");
			if (Baishikele[i] > max)
				max = Baishikele[i];
		}
		return max;
	}

	public double CompareLingdukelePhotos() {// 比较零度可乐的最佳匹配度

		double[] Lingdukele = new double[6];
		Lingdukele[0] = getpipeidu("storage/sdcard0/input/Lingdukele/1.jpg");
		Lingdukele[1] = getpipeidu("storage/sdcard0/input/Lingdukele/2.jpg");
		Lingdukele[2] = getpipeidu("storage/sdcard0/input/Lingdukele/3.jpg");
		Lingdukele[3] = getpipeidu("storage/sdcard0/input/Lingdukele/4.jpg");
		Lingdukele[4] = getpipeidu("storage/sdcard0/input/Lingdukele/5.jpg");
		Lingdukele[5] = getpipeidu("storage/sdcard0/input/Lingdukele/6.jpg");
		double max = Lingdukele[0];
		for (int i = 0; i < Lingdukele.length; i++) {
			System.out.print(Lingdukele[i] + " ");
			if (Lingdukele[i] > max)
				max = Lingdukele[i];
		}
		return max;
	}

	public void CompareB() {
		double Zuida;
		Highgui.imwrite(inFile, mRgba);
		double Kekoukele = CompareKekoukelePhotos();
		Zuida = Kekoukele;
		jdb = Kekoukele;
		double Lingdukele = CompareLingdukelePhotos();
		wlj = Lingdukele;
		if (Lingdukele > Zuida)
			Zuida = Lingdukele;
		double Baishikele = CompareBaishikelePhotos();
		mnd = Baishikele;
		if (Baishikele > Zuida)
			Zuida = Baishikele;
		double Xuebi = CompareXuebiPhotos();
		xm = Xuebi;
		if (Xuebi > Zuida)
			Zuida = Xuebi;
		if (Zuida == Kekoukele) {
			Shibie = 'k';
			fmsg += 'k';
		}
		if (Zuida == Baishikele) {
			Shibie = 'b';
			fmsg += 'b';
		}
		if (Zuida == Lingdukele) {
			Shibie = 'l';
			fmsg += 'l';
		}
		if (Zuida == Xuebi) {
			Shibie = 's';
			fmsg += 's';
		}

	}

	public double CompareJiaduobaoPhotos() {

		double[] Jiaduobao = new double[6];
		Jiaduobao[0] = getpipeidu("storage/sdcard0/input/Jiaduobao/1.jpg");
		Jiaduobao[1] = getpipeidu("storage/sdcard0/input/Jiaduobao/2.jpg");
		Jiaduobao[2] = getpipeidu("storage/sdcard0/input/Jiaduobao/3.jpg");
		Jiaduobao[3] = getpipeidu("storage/sdcard0/input/Jiaduobao/4.jpg");
		Jiaduobao[4] = getpipeidu("storage/sdcard0/input/Jiaduobao/5.jpg");
		double max = Jiaduobao[0];
		for (int i = 0; i < Jiaduobao.length; i++) {
			System.out.print(Jiaduobao[i] + " ");
			if (Jiaduobao[i] > max)
				max = Jiaduobao[i];
		}
		return max;
	}

	public double CompareWanglaojiPhotos() {

		double[] Wanglaoji =new double[6];
		Wanglaoji[0] = getpipeidu("storage/sdcard0/input/Wanglaoji/1.jpg");
		Wanglaoji[1] = getpipeidu("storage/sdcard0/input/Wanglaoji/2.jpg");
		Wanglaoji[2] = getpipeidu("storage/sdcard0/input/Wanglaoji/3.jpg");
		Wanglaoji[3] = getpipeidu("storage/sdcard0/input/Wanglaoji/4.jpg");
		Wanglaoji[4] = getpipeidu("storage/sdcard0/input/Wanglaoji/5.jpg");
		Wanglaoji[5] = getpipeidu("storage/sdcard0/input/Wanglaoji/6.jpg");
		double max = Wanglaoji[0];
		for (int i = 0; i < Wanglaoji.length; i++) {
			System.out.print(Wanglaoji[i] + " ");
			if (Wanglaoji[i] > max)
				max = Wanglaoji[i];
		}
		return max;
	}

	public double CompareMeiniandaPhotos() {

		double[] Meinianda = new double[6];
		Meinianda[0] = getpipeidu("storage/sdcard0/input/Meinianda/1.jpg");
		Meinianda[1] = getpipeidu("storage/sdcard0/input/Meinianda/2.jpg");
		Meinianda[2] = getpipeidu("storage/sdcard0/input/Meinianda/3.jpg");
		Meinianda[3] = getpipeidu("storage/sdcard0/input/Meinianda/4.jpg");
		Meinianda[4] = getpipeidu("storage/sdcard0/input/Meinianda/5.jpg");
		Meinianda[5] = getpipeidu("storage/sdcard0/input/Meinianda/6.jpg");
		double max = Meinianda[0];
		for (int i = 0; i < Meinianda.length; i++) {
			System.out.print(Meinianda[i] + " ");
			if (Meinianda[i] > max)
				max = Meinianda[i];
		}
		return max;
	}

	public double CompareXingmuPhotos() {

		double[] Xingmu = new double[6];
		Xingmu[0] = getpipeidu("storage/sdcard0/input/Xingmu/1.jpg");
		Xingmu[1] = getpipeidu("storage/sdcard0/input/Xingmu/2.jpg");
		Xingmu[2] = getpipeidu("storage/sdcard0/input/Xingmu/3.jpg");
		Xingmu[3] = getpipeidu("storage/sdcard0/input/Xingmu/4.jpg");
		Xingmu[4] = getpipeidu("storage/sdcard0/input/Xingmu/5.jpg");
		Xingmu[5] = getpipeidu("storage/sdcard0/input/Xingmu/6.jpg");
		double max = Xingmu[0];
		for (int i = 0; i < Xingmu.length; i++) {
			System.out.print(Xingmu[i] + " ");
			if (Xingmu[i] > max)
				max = Xingmu[i];
		}
		return max;
	}
	
	public void CompareA() {
		double Zuida;
		Highgui.imwrite(inFile, mRgba);
		double Jiaduobao = CompareKekoukelePhotos();
		Zuida = Jiaduobao;
		double Wanglaoji = CompareLingdukelePhotos();
		if (Wanglaoji > Zuida)
			Zuida = Wanglaoji;
		double Xingmu = CompareBaishikelePhotos();
		if (Xingmu > Zuida)
			Zuida = Xingmu;
		double Meinianda = CompareXuebiPhotos();
		if (Meinianda > Zuida)
			Zuida = Meinianda;
		if (Zuida == Jiaduobao) {
			Shibie = 'j';
			fmsg += 'j';
		}
		if (Zuida == Wanglaoji) {
			Shibie = 'w';
			fmsg += 'w';
		}
		if (Zuida == Xingmu) {
			Shibie = 'x';
			fmsg += 'x';
		}
		if (Zuida == Meinianda) {
			Shibie = 'm';
			fmsg += 'm';
		}

	}

}