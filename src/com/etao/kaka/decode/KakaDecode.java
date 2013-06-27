package com.etao.kaka.decode;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.Pattern;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.YuvImage;

public class KakaDecode {
	static {
        System.loadLibrary("tbdecode");
    } 
	public static native DecodeResult yuvcodeDecode(byte[] data, int width, int height, int strides);

	public static DecodeResult yuvcodeDecode(YuvImage yuvImage) {
		DecodeResult result =  	yuvcodeDecode(yuvImage.getYuvData(),yuvImage.getWidth(),yuvImage.getHeight(),yuvImage.getStrides()[0]);
		if(result!=null){
			try {
				if(	isUTF8Encode(result.bytes)){
					result.strCode = new String(result.bytes,"utf-8");
				}else{
					result.strCode = new String(result.bytes,"gbk");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				result = null;
			} catch(Exception e){
				e.printStackTrace();
				result = null;
			}
		}	
		return result;
	}

	public static native DecodeResult codeDecode(byte[] data, int width, int height, int rowBytes);

	public static DecodeResult codeDecode(Bitmap bitmap) {
		Bitmap b = bitmap;
		if (b.getConfig() != Config.ARGB_8888) {
			Bitmap bb = b.copy(Config.ARGB_8888, true);
			b.recycle();
			b = bb;

		}
		ByteBuffer buf = ByteBuffer.allocate(b.getHeight() * b.getRowBytes());

		buf.order(ByteOrder.BIG_ENDIAN);
		b.copyPixelsToBuffer(buf);
		DecodeResult result = codeDecode(buf.array(), b.getWidth(), b.getHeight(), b.getRowBytes());
		if (result != null) {
			try {
				if (isUTF8Encode(result.bytes)) {
					result.strCode = new String(result.bytes, "utf-8");
				} else {
					result.strCode = new String(result.bytes, "gbk");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				result = null;
			} catch (Exception e) {
				e.printStackTrace();
				result = null;
			}
		}

		return result;
	}

	private static Pattern utf8Pattern;
	static{
	utf8Pattern = Pattern.compile("\\A(\n" + "  [\\x09\\x0A\\x0D\\x20-\\x7E]             # ASCII\\n" + "| [\\xC2-\\xDF][\\x80-\\xBF]               # non-overlong 2-byte\n"
			+ "|  \\xE0[\\xA0-\\xBF][\\x80-\\xBF]         # excluding overlongs\n" + "| [\\xE1-\\xEC\\xEE\\xEF][\\x80-\\xBF]{2}  # straight 3-byte\n"
			+ "|  \\xED[\\x80-\\x9F][\\x80-\\xBF]         # excluding surrogates\n" + "|  \\xF0[\\x90-\\xBF][\\x80-\\xBF]{2}      # planes 1-3\n"
			+ "| [\\xF1-\\xF3][\\x80-\\xBF]{3}            # planes 4-15\n" + "|  \\xF4[\\x80-\\x8F][\\x80-\\xBF]{2}      # plane 16\n" + ")*\\z", Pattern.COMMENTS);

		}

public static boolean isUTF8Encode(byte[] utf8) throws UnsupportedEncodingException {
	String phonyString = new String(utf8, "ISO-8859-1");
	return utf8Pattern.matcher(phonyString).matches();
}
	
}
