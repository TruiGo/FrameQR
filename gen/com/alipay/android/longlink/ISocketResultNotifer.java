/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/zhangqian/work/workspace/FrameQR/src/com/alipay/android/longlink/ISocketResultNotifer.aidl
 */
package com.alipay.android.longlink;
/**
 * 长连接建立成功与否回调
 */
public interface ISocketResultNotifer extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.alipay.android.longlink.ISocketResultNotifer
{
private static final java.lang.String DESCRIPTOR = "com.alipay.android.longlink.ISocketResultNotifer";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.alipay.android.longlink.ISocketResultNotifer interface,
 * generating a proxy if needed.
 */
public static com.alipay.android.longlink.ISocketResultNotifer asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.alipay.android.longlink.ISocketResultNotifer))) {
return ((com.alipay.android.longlink.ISocketResultNotifer)iin);
}
return new com.alipay.android.longlink.ISocketResultNotifer.Stub.Proxy(obj);
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
case TRANSACTION_onSocketCreateSuccess:
{
data.enforceInterface(DESCRIPTOR);
this.onSocketCreateSuccess();
reply.writeNoException();
return true;
}
case TRANSACTION_onSocketCreateFail:
{
data.enforceInterface(DESCRIPTOR);
this.onSocketCreateFail();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.alipay.android.longlink.ISocketResultNotifer
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
public void onSocketCreateSuccess() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onSocketCreateSuccess, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onSocketCreateFail() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onSocketCreateFail, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onSocketCreateSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onSocketCreateFail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void onSocketCreateSuccess() throws android.os.RemoteException;
public void onSocketCreateFail() throws android.os.RemoteException;
}
