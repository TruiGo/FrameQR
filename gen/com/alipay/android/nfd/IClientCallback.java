/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/zhangqian/work/workspace/FrameQR/src/com/alipay/android/nfd/IClientCallback.aidl
 */
package com.alipay.android.nfd;
/**
 * 客户端远程回调通知函数
 * @author daping.gp
 * @version $Id: RemoteServiceCallback.java, v 0.1 2012-6-18 下午4:04:12 daping.gp Exp $
 */
public interface IClientCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.alipay.android.nfd.IClientCallback
{
private static final java.lang.String DESCRIPTOR = "com.alipay.android.nfd.IClientCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.alipay.android.nfd.IClientCallback interface,
 * generating a proxy if needed.
 */
public static com.alipay.android.nfd.IClientCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.alipay.android.nfd.IClientCallback))) {
return ((com.alipay.android.nfd.IClientCallback)iin);
}
return new com.alipay.android.nfd.IClientCallback.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_refresh:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _arg0;
_arg0 = new java.util.ArrayList<java.lang.String>();
this.refresh(_arg0);
reply.writeNoException();
reply.writeStringList(_arg0);
return true;
}
case TRANSACTION_startActivity:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
android.os.Bundle _arg3;
if ((0!=data.readInt())) {
_arg3 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
this.startActivity(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_registReslut:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.registReslut(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.alipay.android.nfd.IClientCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * 通知远程调用者已经近场发现完成并返回结果
     */
public void refresh(java.util.List<java.lang.String> clientInfos) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_refresh, _data, _reply, 0);
_reply.readException();
_reply.readStringList(clientInfos);
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void startActivity(java.lang.String packageName, java.lang.String className, int iCallingPid, android.os.Bundle bundle) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
_data.writeString(className);
_data.writeInt(iCallingPid);
if ((bundle!=null)) {
_data.writeInt(1);
bundle.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_startActivity, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void registReslut(int resultCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(resultCode);
mRemote.transact(Stub.TRANSACTION_registReslut, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_refresh = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registReslut = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
/**
     * 通知远程调用者已经近场发现完成并返回结果
     */
public void refresh(java.util.List<java.lang.String> clientInfos) throws android.os.RemoteException;
public void startActivity(java.lang.String packageName, java.lang.String className, int iCallingPid, android.os.Bundle bundle) throws android.os.RemoteException;
public void registReslut(int resultCode) throws android.os.RemoteException;
}
