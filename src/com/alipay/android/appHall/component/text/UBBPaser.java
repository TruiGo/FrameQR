package com.alipay.android.appHall.component.text;


public class UBBPaser {
    private String mData;

    private StringBuilder sb;
    private int textLen;
    private int currentStartIndex = 0;
    private int currentEndIndex = 0;

    public UBBPaser(String data) {
        mData = data;
        mData = mData.replaceAll("，", ",");
        mData = mData.replaceAll("\\[space\\]", "&nbsp;&nbsp;");
    }

    public String parse2Html() {
        sb = new StringBuilder(mData);
        textLen = sb.length();

        String element = null;

        currentStartIndex = sb.indexOf("[");
        if (currentStartIndex == -1) {
            return mData;
        }

        while (currentStartIndex != -1) {

            currentEndIndex = sb.indexOf("]", currentStartIndex);
            element = sb.substring(currentStartIndex, currentEndIndex + 1);
            parseElement(element);

            textLen = sb.length();
            if (currentStartIndex >= textLen) {
                break;
            }
            currentStartIndex = sb.indexOf("[", currentStartIndex);
        }
        
        return sb.toString();
    }

    private void parseElement(String element) {

        if (element.indexOf("[/color") != -1) {
            parseColor(element, true);
        } else if (element.indexOf("[/link") != -1) {
            parseLink(element, true);
        } else if (element.indexOf("[color") != -1) {
            parseColor(element, false);
        } else if (element.indexOf("[link") != -1) {
            parseLink(element, false);
        } else if (element.indexOf("[br") != -1) {
            parseBr(element, false);
        }
    }

    private void parseLink(String element, boolean isEndElement) {
        if (isEndElement) {
            sb.replace(currentStartIndex, currentEndIndex + 1, "</a>");
            currentStartIndex = currentStartIndex + 4;
            return;
        }
        String tempHrefValue = getElementValue(element);
        String hrefValueElement = "<a href=" + tempHrefValue + " >";
        sb.replace(currentStartIndex, currentEndIndex + 1, hrefValueElement);
        currentStartIndex = currentStartIndex + hrefValueElement.length();
    }

    private void parseColor(String element, boolean isEndElement) {
        if (isEndElement) {
            sb.replace(currentStartIndex, currentEndIndex + 1, "</font>");
            currentStartIndex = currentStartIndex + 7;
            return;
        }
        String tempColorValue = getElementValue(element);
        String colorValueElement = "<font color=#" + tempColorValue + " >";
        sb.replace(currentStartIndex, currentEndIndex + 1, colorValueElement);
        currentStartIndex = currentStartIndex + colorValueElement.length();

    }

    private void parseBr(String element, boolean isEndElement) {
        if ("[br]".equals(element)) {
            String brElement = "<br>";
            sb.replace(currentStartIndex, currentEndIndex + 1, brElement);
            currentStartIndex = currentStartIndex + brElement.length();
        }
    }

    private String getElementValue(String element) {
        int valueStart = element.indexOf("=");
        int valueEnd = element.indexOf("]") - 1;
        return element.substring(valueStart + 1, valueEnd + 1).trim();
    }
    
}
