/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/zhangqian/work/workspace/FrameQR/src/com/alipay/android/client/IAlipayPushLinkService.aidl
 */
package com.alipay.android.client;
public interface IAlipayPushLinkService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.alipay.android.client.IAlipayPushLinkService
{
private static final java.lang.String DESCRIPTOR = "com.alipay.android.client.IAlipayPushLinkService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.alipay.android.client.IAlipayPushLinkService interface,
 * generating a proxy if needed.
 */
public static com.alipay.android.client.IAlipayPushLinkService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.alipay.android.client.IAlipayPushLinkService))) {
return ((com.alipay.android.client.IAlipayPushLinkService)iin);
}
return new com.alipay.android.client.IAlipayPushLinkService.Stub.Proxy(obj);
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
case TRANSACTION_getPushLinkStatus:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPushLinkStatus();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_initializePushLink:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.initializePushLink(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_registerAppId:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.registerAppId(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterAppId:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.unregisterAppId(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_closePushLink:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.closePushLink();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.alipay.android.client.IAlipayPushLinkService
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
//

public int getPushLinkStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPushLinkStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean initializePushLink(int netType, java.lang.String clientDealDesc) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(netType);
_data.writeString(clientDealDesc);
mRemote.transact(Stub.TRANSACTION_initializePushLink, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean registerAppId(int appId, java.lang.String actionName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(appId);
_data.writeString(actionName);
mRemote.transact(Stub.TRANSACTION_registerAppId, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean unregisterAppId(int appId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(appId);
mRemote.transact(Stub.TRANSACTION_unregisterAppId, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//boolean sendAppData(JSON request);

public boolean closePushLink() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_closePushLink, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getPushLinkStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_initializePushLink = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registerAppId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unregisterAppId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_closePushLink = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
//

public int getPushLinkStatus() throws android.os.RemoteException;
public boolean initializePushLink(int netType, java.lang.String clientDealDesc) throws android.os.RemoteException;
public boolean registerAppId(int appId, java.lang.String actionName) throws android.os.RemoteException;
public boolean unregisterAppId(int appId) throws android.os.RemoteException;
//boolean sendAppData(JSON request);

public boolean closePushLink() throws android.os.RemoteException;
}
