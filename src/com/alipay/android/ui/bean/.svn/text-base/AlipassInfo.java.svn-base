package com.alipay.android.ui.bean;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class AlipassInfo {
	EvoucherInfo evoucherInfo;
	Merchant merchant;
	Platform platform;
	FileInfo fileInfo;
	Style style;
	String tradeInfo;
	String passFileName;
	String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPassFileName() {
		return passFileName;
	}

	public void setPassFileName(String passFileName) {
		this.passFileName = passFileName;
	}

	public EvoucherInfo getEvoucherInfo() {
		return evoucherInfo;
	}

	public void setEvoucherInfo(EvoucherInfo evoucherInfo) {
		this.evoucherInfo = evoucherInfo;
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public String getTradeInfo() {
		return tradeInfo;
	}

	public void setTradeInfo(String tradeInfo) {
		this.tradeInfo = tradeInfo;
	}

	public static final class EvoucherInfo {
		String goodsId;
		String title;
		String type;
		String product;
		String startDate;
		String endDate;
		Einfo eInfo;
		public Einfo geteInfo() {
			return eInfo;
		}

		public void seteInfo(Einfo eInfo) {
			this.eInfo = eInfo;
		}

		List<Locations> locations;
		String description;
		String disclaimer;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getDisclaimer() {
			return disclaimer;
		}

		public void setDisclaimer(String disclaimer) {
			this.disclaimer = disclaimer;
		}


		public String getGoodsId() {
			return goodsId;
		}

		public void setGoodsId(String goodsId) {
			this.goodsId = goodsId;
		}

		public List<Locations> getLocations() {
			return locations;
		}

		public void setLocations(List<Locations> locations) {
			this.locations = locations;
		}

		public String getProduct() {
			return product;
		}

		public void setProduct(String product) {
			this.product = product;
		}

		public String getStartDate() {
			if(startDate != null && startDate.length() > 10){
				return startDate.substring(0,10);
			}
			
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			if(endDate != null && endDate.length() > 10){
				return endDate.substring(0,10);
			}
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public static final class Einfo {
		List<Barcode> barcodeList;
		String discountInfo;

		public List<Barcode> getBarcodeList() {
			return barcodeList;
		}

		public void setBarcodeList(List<Barcode> barcodeList) {
			this.barcodeList = barcodeList;
		}

		public String getDiscountInfo() {
			return discountInfo;
		}

		public void setDiscountInfo(String discountInfo) {
			this.discountInfo = discountInfo;
		}
	}

	public static final class Locations implements Parcelable{
		String addr;
		Double altitude;
		Double latitude;
		Double longitude;
		String relevantText;
		String tel;

		public Locations() {
		}
		
		public String getAddr() {
			return addr;
		}

		public void setAddr(String addr) {
			this.addr = addr;
		}

		public double getAltitude() {
			return altitude;
		}

		public void setAltitude(Double altitude) {
			this.altitude = altitude;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}

		public String getRelevantText() {
			return relevantText;
		}

		public void setRelevantText(String relevantText) {
			this.relevantText = relevantText;
		}

		public String getTel() {
			return tel;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		@Override
		public int describeContents() {
			return 0;
		}
		
		public Locations(Parcel in){
			readFormParcel(in);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(addr);
			dest.writeDouble(altitude == null ? 0d:altitude);
			dest.writeDouble(latitude == null ? 0d:latitude);
			dest.writeDouble(longitude == null ? 0d:longitude);
			dest.writeString(relevantText);
			dest.writeString(tel);
		}
		
		public static final Parcelable.Creator<Locations> CREATOR = new Parcelable.Creator<Locations>() {
	        public Locations createFromParcel(Parcel in) {
	        	return new Locations(in);
	        }

			public Locations[] newArray(int size) {
	            return new Locations[size];
	        }
	    };
		
		private void readFormParcel(Parcel in) {
			addr = in.readString();
			altitude = in.readDouble();
			latitude = in.readDouble();
			longitude = in.readDouble();
			relevantText = in.readString();
			tel = in.readString();
		}
	}

	public static final class Barcode {
		String altText;
		String format;
		String message;
		String messageEncoding;

		public String getAltText() {
			return altText;
		}

		public void setAltText(String altText) {
			this.altText = altText;
		}

		public String getFormat() {
			return format;
		}

		public void setFormat(String format) {
			this.format = format;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getMessageEncoding() {
			return messageEncoding;
		}

		public void setMessageEncoding(String messageEncoding) {
			this.messageEncoding = messageEncoding;
		}
	}

	public static final class FileInfo {
		Boolean canShare;
		String formatVersion;
		Integer serialNumber;

		public Integer getSerialNumber() {
			return serialNumber;
		}

		public void setSerialNumber(Integer serialNumber) {
			this.serialNumber = serialNumber;
		}

		public Boolean getCanShare() {
			return canShare;
		}

		public void setCanShare(Boolean canShare) {
			this.canShare = canShare;
		}

		public String getFormatVersion() {
			return formatVersion;
		}

		public void setFormatVersion(String formatVersion) {
			this.formatVersion = formatVersion;
		}

	}

	public static final class Merchant {
		String maddr;
		String minfo;
		String mname;
		String mshortName;
		String mtel;

		public String getMaddr() {
			return maddr;
		}

		public void setMaddr(String maddr) {
			this.maddr = maddr;
		}

		public String getMinfo() {
			return minfo;
		}

		public void setMinfo(String minfo) {
			this.minfo = minfo;
		}

		public String getMname() {
			return mname;
		}

		public void setMname(String mname) {
			this.mname = mname;
		}

		public String getMshortName() {
			return mshortName;
		}

		public void setMshortName(String mshortName) {
			this.mshortName = mshortName;
		}

		public String getMtel() {
			return mtel;
		}

		public void setMtel(String mtel) {
			this.mtel = mtel;
		}
	}

	public static final class Platform {
		String channelID;
		String webServiceUrl;

		public String getChannelID() {
			return channelID;
		}

		public void setChannelID(String channelID) {
			this.channelID = channelID;
		}

		public String getWebServiceUrl() {
			return webServiceUrl;
		}

		public void setWebServiceUrl(String webServiceUrl) {
			this.webServiceUrl = webServiceUrl;
		}
	}
	
	public static int resolveColor(String backgroundColor) {
		//rgb(68, 200, 190)
		if(backgroundColor != null && backgroundColor.length() > 0){
			Pattern pattern = Pattern.compile("\\(.*\\)");
			Matcher matcher = pattern.matcher(backgroundColor);
				if(matcher.find()){
					List<String> rgbList = Arrays.asList(matcher.group().split("\\(|,|\\)"));
					if(rgbList != null && rgbList.size() == 4){
						try {
							return Color.rgb(Integer.parseInt(rgbList.get(1).trim()), Integer.parseInt(rgbList.get(2).trim()), Integer.parseInt(rgbList.get(3).trim()));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				}
		}
		return 0;
	}

	public static final class Style {
		String backgroundColor;

		public int getBackgroundColor() {
			return resolveColor(backgroundColor);
		}

		public void setBackgroundColor(String backgroundColor) {
			this.backgroundColor = backgroundColor;
		}
	}

	
	private long lastModified;
	public void setLastModifiedTime(long lastModified) {
		this.lastModified = lastModified;
	}
	
	public long getLastModified(){
		return this.lastModified;
	}
}
